package absimth.module.memoryController;

import java.util.HashMap;

import absimth.exception.FixableErrorException;
import absimth.exception.UnfixableErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memoryController.IMemoryController;
import absimth.sim.memoryController.MemoryController;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
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
	private final int LPCBack = -1;
	private final int HammingBack = -1;
	private final int cycleSize = 10;
	
	//TODO DIN?
	private int pageSize = 32000; //4kb 
	
	
	private EccType getNextEcc(ECCConfig config) throws Exception {
		if(!config.dynamic) return config.getType();
		if(config.getType() == EccType.PARITY && config.count >= parityNext) return EccType.HAMMING_SECDEC;
		if(config.getType() == EccType.HAMMING_SECDEC && config.count >= HammingNext) return EccType.REED_SOLOMON;
		if(config.getType() == EccType.REED_SOLOMON) return EccType.REED_SOLOMON;
		return config.getType();
		//throw new Exception(String.format("DFTM CONTROLLER - WRONG next ECC - %i ", config.getType()));
	}
	
	private EccType getPreviousEcc(ECCConfig config) throws Exception {
		if(!config.dynamic) return config.getType();
		if(config.getType() == EccType.PARITY) return EccType.HAMMING_SECDEC;
		if(config.getType() == EccType.HAMMING_SECDEC && config.count <= HammingBack) return EccType.REED_SOLOMON;
		if(config.getType() == EccType.REED_SOLOMON && config.count <= LPCBack) return EccType.REED_SOLOMON;
		return config.getType();
		//throw new Exception(String.format("DFTM CONTROLLER - WRONG Next ECC - %i ", config.getType()));
	}
	
	private void evaluateAndProcessCyle() {
		numberOfExecution++;	
		if(numberOfExecution<cycleSize) return;
		numberOfExecution = 0;
		map.forEach((x,y)->{
			try {
				EccType previousType = getPreviousEcc(y);
				EccType nextType = getNextEcc(y);
				if( previousType != y.getType()) {
					migrateFromTo(x, y.getType(), previousType);
					y.setType(previousType);
				} else if( nextType != y.getType()) {
					migrateFromTo(x, y.getType(), nextType);
					y.setType(nextType);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int n = y.getCount()-1;
			y.setCount(n<0?0:n);	
			
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
		return type.equals(EccType.REED_SOLOMON);
	}

	private long getMaxAddress() {
		return SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress()/2;
	}

	private ECCConfig getEncode(long address) {
		return map.getOrDefault(address/pageSize,  new ECCConfig(EccType.HAMMING_SECDEC, 0, true));
	}

	private Bits readInternal(long address) throws Exception {
		try {
			SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.REED_SOLOMON);
			return  MemoryController.readBits(address);
		} catch (FixableErrorException e) {
			try {
				getMemoryStatus().getFromAddress(address).setFixedData(e.getRecovered());
				getMemoryStatus().getFromAddress(address).setOriginalData(e.getRecovered());
			}catch (Exception e1) {}
			return e.getRecovered();	
		}catch (UnfixableErrorException e) {
			SimulatorManager.getSim().getOs().getCurrentCPU().getCurrentProgram().setSucesuful(false);
	           try {
					getMemoryStatus().getFromAddress(address).setFixedData(e.getInput());
				}catch (Exception e1) {}
	           return e.getInput();
		}catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public long read(long address) throws Exception {
		evaluateAndProcessCyle();
		
		
			ECCMemoryFaultModel model = getMemoryStatus().getFromAddress(address);
			if(model!= null) model.setDirtAccess(true);
			
			Long maxAddress = getMaxAddress();
			if(address>maxAddress) {
				String msg = String.format("DFTM READ - ADDRESS MAX THAN ALLOWED " + " - at 0x%08x - 0x%08x", address, maxAddress);
				AbsimLog.memory(msg);
				throw new Exception(msg);
			}
			
			ECCConfig config = getEncode(address);
			if(useDoubleMemory(config.getType())) {
				Bits data1 = readInternal(address);
				Bits data2 = readInternal(getMaxAddress()+address);
				return data1.append(data2).toLong();			
			} else {
				try {
					SimulatorManager.getSim().getReport().memoryControllerInc("READ "+config.getType());
					return config.getType().getEncode().decode(MemoryController.readBits(address)).toLong();
				} catch (UnfixableErrorException he) {
					SimulatorManager.getSim().getOs().getCurrentCPU().getCurrentProgram().setSucesuful(false);
					try {
						getMemoryStatus().getFromAddress(address).setFixedData(he.getInput());
					}catch (Exception e1) {}
					config.setCount(config.getCount()+1);
					map.put(address/pageSize, config);
					
					this.getMemoryStatus().setStatus(address,
							he.getPosition(), ECCMemoryFaultType.UNFIXABLE_ERROR, he.getInput(), Bits.from(0));
					
					AbsimLog.memory(String.format(ECCMemoryFaultType.UNFIXABLE_ERROR.toString() + " - at 0x%08x - 0x%08x", address, he.getInput().toInt()));
					
					
					return he.getInput().toLong();
					//throw he;
				} catch (FixableErrorException se) {
					config.setCount(config.getCount()+1);
					map.put(address/pageSize, config);
					
					try {
						getMemoryStatus().getFromAddress(address).setFixedData(se.getRecovered());
						getMemoryStatus().getFromAddress(address).setOriginalData(se.getRecovered());
					}catch (Exception e1) {}
					AbsimLog.memory(String.format(ECCMemoryFaultType.FIXABLE_ERROR + " - at 0x%08x - 0x%08x", address, se.getInput().toInt()));
					
					return se.getRecovered().toLong();
				}
				
			}
		
	
	}
	
	private void migrateFromTo(long pos, EccType typeOriginal, EccType typeTo) throws Exception  {
		SimulatorManager.getSim().getReport().memoryControllerInc("N. PAGE MIGRATE");
		long initialAddress = pos * pageSize;
		AbsimLog.memoryController(String.format("STARTING MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing address %s to %s", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing encode from %s to %s", typeOriginal, typeTo));
		
		for (long i = 0; i < pageSize; i++) {
			long nAddress = i + initialAddress;
//				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION READ "+ typeOriginal);
				
				long data = 0;
//				if(useDoubleMemory(typeOriginal)) {
//					Bits data1 = readInternal(nAddress);
//					Bits data2 = readInternal(getMaxAddress()+nAddress);
//					data = data1.append(data2).toLong();
//				} else {
					try {
						SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION READ "+ typeOriginal);
						data = typeOriginal.getEncode().decode(MemoryController.readBits(nAddress)).toLong();
					} catch (UnfixableErrorException e) {
						getMemoryStatus().getFromAddress(nAddress).setFixedData(e.getInput());
						data = e.getInput().toLong();
					} catch (FixableErrorException e) {
						getMemoryStatus().getFromAddress(nAddress).setFixedData(e.getRecovered());
						getMemoryStatus().getFromAddress(nAddress).setOriginalData(e.getRecovered());
						data = e.getRecovered().toLong();
					}
//				}

//				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
				
				Bits baseData = typeTo.getEncode().encode(Bits.from(data));
				if(useDoubleMemory(typeTo)) {
					Bits data1 = baseData.subbit(0, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(nAddress, data1);
					Bits data2 = baseData.subbit(64, 64);
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(getMaxAddress()+nAddress, data2);
				} else {
					SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
					MemoryController.writeBits(nAddress, baseData);
				}
				
				//MemoryController.writeBits(nAddress, typeTo.getEncode().encode(Bits.from(data)));
			
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
