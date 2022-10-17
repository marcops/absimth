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

	private HashMap<Long, EccType> map = new HashMap<>();
	
	//TODO DIN?
	private int pageSize = 32000; //4kb 
	
	@Override
	public void write(long address, long data) throws Exception {
		Long maxAddress = getMaxAddress();
		if(address>maxAddress) {
			String msg = String.format("DFTM WRITE - ADDRESS MAX THAN ALLOWED " + " - at 0x%08x - 0x%08x", address, maxAddress);
			AbsimLog.memory(msg);
			throw new Exception(msg);
		}
		EccType type = getEncode(address);
		Bits baseData = type.getEncode().encode(Bits.from(data));
		if(useDoubleMemory(type)) {
			Bits data1 = baseData.subbit(0, 64);
			SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+type);
			MemoryController.writeBits(address, data1);
			Bits data2 = baseData.subbit(64, 64);
			SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+type);
			MemoryController.writeBits(getMaxAddress()+address, data2);
		} else {
			SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+type);
			MemoryController.writeBits(address, baseData);
		}
	}

	private boolean useDoubleMemory(EccType type) {
		return type.equals(EccType.LPC) || type.equals(EccType.REED_SOLOMON);
	}

	private long getMaxAddress() {
		return SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress()/2;
	}

	private EccType getEncode(long address) {
		return map.getOrDefault(address/pageSize,  EccType.HAMMING_SECDEC);
	}

	@Override
	public long read(long address) throws Exception {
		try {
			Long maxAddress = getMaxAddress();
			if(address>maxAddress) {
				String msg = String.format("DFTM READ - ADDRESS MAX THAN ALLOWED " + " - at 0x%08x - 0x%08x", address, maxAddress);
				AbsimLog.memory(msg);
				throw new Exception(msg);
			}
			
			EccType type = getEncode(address);
			if(useDoubleMemory(type) ) {
				SimulatorManager.getSim().getReport().memoryControllerInc("READ "+type);
				Bits data1 = MemoryController.readBits(address);
				SimulatorManager.getSim().getReport().memoryControllerInc("READ "+type);
				Bits data2 = MemoryController.readBits(getMaxAddress()+address);
				Bits fData = data1.append(data2);
				return type.getEncode().decode(fData).toLong();				
			} else {
				SimulatorManager.getSim().getReport().memoryControllerInc("READ "+type);
				return type.getEncode().decode(MemoryController.readBits(address)).toLong();
			}
		} catch (UnfixableErrorException he) {
			if(getEncode(address) == EccType.REED_SOLOMON)
				throw he;
			this.getMemoryStatus().setStatus(address,
					he.getPosition(), ECCMemoryFaultType.UNFIXABLE_ERROR);
			AbsimLog.memory(String.format(ECCMemoryFaultType.UNFIXABLE_ERROR.toString() + " - at 0x%08x - 0x%08x", address, he.getInput().toInt()));
			migrate(address);
			throw he;
		} catch (FixableErrorException se) {
			if(getEncode(address) == EccType.REED_SOLOMON)
				return se.getRecovered().toLong();
			
			this.getMemoryStatus().setStatus(address,
					se.getPosition(),
					ECCMemoryFaultType.FIXABLE_ERROR);
			AbsimLog.memory(String.format(ECCMemoryFaultType.FIXABLE_ERROR + " - at 0x%08x - 0x%08x", address, se.getInput().toInt()));
			migrate(address);
			return se.getRecovered().toLong();
		}
	
	}
	
	private boolean migrate(long address) throws Exception {
		long pos = address/pageSize;
		long initialAddress = pos * pageSize;
		EccType typeOriginal = getEncode(address);
		EccType typeTo = typeOriginal.getNext();
		if(typeOriginal.compareTo(typeTo) ==0) {
			AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE ADDRESS from 0x%08x to 0x%08x - ALREADY USING %s", initialAddress, initialAddress+pageSize, typeTo));
			return false;
		}
		migrateFromTo(address, typeOriginal, typeTo);
		return true;
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
		map.put(pos, typeTo);
		AbsimLog.memory(String.format("END MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		try {
			EccType type = getEncode(address);
			Bits b = SimulatorManager.getSim().getMemory().read(address);
			return type.getEncode().decode(b);
		} catch (FixableErrorException e) {
			return e.getRecovered();			
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public EccType getCurrentEccType(long address) {
		return getEncode(address);
	}
}
