package absimth.module.memoryController;

import java.util.HashMap;

import absimth.exception.FixableErrorException;
import absimth.exception.UnfixableErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memoryController.IMemoryController;
import absimth.sim.memoryController.MemoryController;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;

public class DFTMemoryController extends MemoryController implements IMemoryController {

	private long numberOfExecution =0;
	public class ECCConfig {
		public EccType type;
		public Boolean dynamic; 
		public int count;

		public ECCConfig(EccType type, int count, Boolean dynamic) {
			this.type = type;
			this.count = count;
			this.dynamic = dynamic; 
		}

		public EccType getType() {
			return type;
		}

		public void setType(EccType type) {
			this.type = type;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

	}
	
	private HashMap<Long, ECCConfig> map = new HashMap<>();
	
	private final int parityNext = 1;
	private final int HammingNext = 1;
	private final int LPCBack = 999999;
	private final int HammingBack = 999999;
	private final int cycleSize = 1000;
	
	//TODO DIN?
	private int pageSize = 32000; //4kb 
	
	
	private EccType getNextEcc(ECCConfig config) throws Exception {
		if(!config.dynamic) return config.getType();
		if(config.getType() == EccType.PARITY && config.count >= parityNext) return EccType.HAMMING_SECDEC;
		if(config.getType() == EccType.HAMMING_SECDEC && config.count >= HammingBack) return EccType.LPC;
		if(config.getType() == EccType.LPC) return EccType.LPC;
		throw new Exception(String.format("DFTM CONTROLLER - WRONG next ECC - %i ", config.getType()));
	}
	
	private EccType getPreviousEcc(ECCConfig config) throws Exception {
		if(!config.dynamic) return config.getType();
		if(config.getType() == EccType.PARITY) return EccType.HAMMING_SECDEC;
		if(config.getType() == EccType.HAMMING_SECDEC && config.count <= HammingNext) return EccType.LPC;
		if(config.getType() == EccType.LPC && config.count <= LPCBack) return EccType.LPC;
		throw new Exception(String.format("DFTM CONTROLLER - WRONG Next ECC - %i ", config.getType()));
	}
	
	private void evaluateAndProcessCyle() {
		numberOfExecution++;	
		if(numberOfExecution<cycleSize) return;
		map.forEach((x,y)->{
			y.setCount(y.getCount()-1);	
			try {
				EccType previousType = getPreviousEcc(y);
				if( previousType != y.getType()) {
					migrateFromTo(pageSize*x, y.getType(), previousType);
					y.setType(previousType);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
	}
	
	@Override
	public void write(long address, long data) throws Exception {
		evaluateAndProcessCyle();
		
		Long maxAddress = getMaxAddress();
		if(address>maxAddress) {
			String msg = String.format("DFTM WRITE - ADDRESS MAX THAN ALLOWED " + " - at 0x%08x - 0x%08x", address, maxAddress);
			AbsimLog.memory(msg);
			throw new Exception(msg);
		}
		ECCConfig config = getEncode(address);
		Bits baseData = config.getType().getEncode().encode(Bits.from(data));
		if(useDoubleMemory(config.getType())) {
			Bits data1 = baseData.subbit(0, 64);
			SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+config.getType());
			MemoryController.writeBits(address, data1);
			Bits data2 = baseData.subbit(64, 64);
			SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+config.getType());
			MemoryController.writeBits(getMaxAddress()+address, data2);
		} else {
			SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+config.getType());
			MemoryController.writeBits(address, baseData);
		}
	}

	private boolean useDoubleMemory(EccType type) {
		return type.equals(EccType.LPC);
	}

	private long getMaxAddress() {
		return SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress()/2;
	}

	private ECCConfig getEncode(long address) {
		return map.getOrDefault(address/pageSize,  new ECCConfig(EccType.PARITY, 0, true));
	}

	@Override
	public long read(long address) throws Exception {
		evaluateAndProcessCyle();
		
		try {
			Long maxAddress = getMaxAddress();
			if(address>maxAddress) {
				String msg = String.format("DFTM READ - ADDRESS MAX THAN ALLOWED " + " - at 0x%08x - 0x%08x", address, maxAddress);
				AbsimLog.memory(msg);
				throw new Exception(msg);
			}
			
			ECCConfig config = getEncode(address);
			if(useDoubleMemory(config.getType())) {
				SimulatorManager.getSim().getReport().memoryControllerInc("READ "+config.getType());
				Bits data1 = MemoryController.readBits(address);
				SimulatorManager.getSim().getReport().memoryControllerInc("READ "+config.getType());
				Bits data2 = MemoryController.readBits(getMaxAddress()+address);
				Bits fData = data1.append(data2);
				return config.getType().getEncode().decode(fData).toLong();				
			} else {
				SimulatorManager.getSim().getReport().memoryControllerInc("READ "+config.getType());
				return config.getType().getEncode().decode(MemoryController.readBits(address)).toLong();
			}
		} catch (UnfixableErrorException he) {
			ECCConfig config = getEncode(address);
			config.setCount(config.getCount()+1);
			EccType nextType = getNextEcc(config);
			
//			if(getEncode(address) == EccType.LPC)
//				throw he;
			this.getMemoryStatus().setStatus(address,
					he.getPosition(), ECCMemoryFaultType.UNFIXABLE_ERROR, he.getInput(), Bits.from(0));
			
			AbsimLog.memory(String.format(ECCMemoryFaultType.UNFIXABLE_ERROR.toString() + " - at 0x%08x - 0x%08x", address, he.getInput().toInt()));
			if(nextType != config.getType()) {
				migrateFromTo(address, config.getType(), nextType);
				config.setType(nextType);
			}
			throw he;
		} catch (FixableErrorException se) {
			ECCConfig config = getEncode(address);
			config.setCount(config.getCount()+1);
			EccType nextType = getNextEcc(config);
			
//			if(getEncode(address) == EccType.LPC)
//				return se.getRecovered().toLong();
			
			this.getMemoryStatus().setStatus(address,
					se.getPosition(),
					ECCMemoryFaultType.FIXABLE_ERROR, se.getInput(), se.getRecovered());
			AbsimLog.memory(String.format(ECCMemoryFaultType.FIXABLE_ERROR + " - at 0x%08x - 0x%08x", address, se.getInput().toInt()));
			if(nextType != config.getType()) {
				migrateFromTo(address, config.getType(), nextType);
				config.setType(nextType);
			}
			
			return se.getRecovered().toLong();
		}
	
	}
	
	private void migrateFromTo(long address, EccType typeOriginal, EccType typeTo) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("N. PAGE MIGRATE");
		long pos = address/pageSize;
		long initialAddress = pos * pageSize;
		AbsimLog.memoryController(String.format("STARTING MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing address %s to %s", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing encode from %s to %s", typeOriginal, typeTo));
		for (long i = 0; i < pageSize; i++) {
			long nAddress = i + initialAddress;
			try {
//				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION READ "+ typeOriginal);
				
				long data = 0;
				if(useDoubleMemory(typeOriginal)) {
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION READ "+ typeOriginal);
					Bits data1 = MemoryController.readBits(address);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION READ "+ typeOriginal);
					Bits data2 = MemoryController.readBits(getMaxAddress()+address);
					Bits fData = data1.append(data2);
					data = typeOriginal.getEncode().decode(fData).toLong();				
				} else {
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION READ "+ typeOriginal);
					data = typeOriginal.getEncode().decode(MemoryController.readBits(address)).toLong();
				}

//				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
				
				Bits baseData = typeTo.getEncode().encode(Bits.from(data));
				if(useDoubleMemory(typeTo)) {
					Bits data1 = baseData.subbit(0, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(address, data1);
					Bits data2 = baseData.subbit(64, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(getMaxAddress()+address, data2);
				} else {
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(address, baseData);
				}
				
				//MemoryController.writeBits(nAddress, typeTo.getEncode().encode(Bits.from(data)));
			} catch (FixableErrorException e) {
				System.out.println(e.toString() + nAddress);
				//SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+typeTo);
				Bits baseData = typeTo.getEncode().encode(e.getRecovered());
				if(useDoubleMemory(typeTo)) {
					Bits data1 = baseData.subbit(0, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(address, data1);
					Bits data2 = baseData.subbit(64, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(getMaxAddress()+address, data2);
				} else {
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(address, baseData);
				}
				
//				MemoryController.writeBits(nAddress, typeTo.getEncode().encode(e.getRecovered()));
			} catch (Exception e) {
				System.err.println(e.toString() + nAddress);
				Bits baseData = typeTo.getEncode().encode(Bits.from(0));
				if(useDoubleMemory(typeTo)) {
					Bits data1 = baseData.subbit(0, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(address, data1);
					Bits data2 = baseData.subbit(64, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(getMaxAddress()+address, data2);
				} else {
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(address, baseData);
				}
				
//				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN " + typeTo);
//				MemoryController.writeBits(nAddress, typeTo.getEncode().encode(Bits.from(0)));
				AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE - 0x%08x", nAddress));
			}
		}
//		map.put(pos, typeTo);
		AbsimLog.memory(String.format("END MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		try {
			ECCConfig type = getEncode(address);
			Bits b = SimulatorManager.getSim().getMemory().read(address);
			return type.getType().getEncode().decode(b);
		} catch (FixableErrorException e) {
			return e.getRecovered();			
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public EccType getCurrentEccType(long address) {
		return getEncode(address).getType();
	}
}
