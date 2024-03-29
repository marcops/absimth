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

public class C2HMemoryController extends MemoryController implements IMemoryController {

	private HashMap<Long, EccType> map = new HashMap<>();
	
	//TODO DIN?
	private int pageSize = 32000; //4kb 
	
	@Override
	public void write(long address, long data) throws Exception {
		EccType type = getEncode(address);
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+type);
		MemoryController.writeBits(address, type.getEncode().encode(Bits.from(data)));
	}

	private EccType getEncode(long address) {
		return map.getOrDefault(address/pageSize,  EccType.HAMMING_SECDEC);
	}

	@Override
	public long read(long address) throws Exception {
		EccType type = getEncode(address);
		try {
			SimulatorManager.getSim().getReport().memoryControllerInc("READ "+type);
			return type.getEncode().decode(MemoryController.readBits(address)).toLong();
		} catch (UnfixableErrorException he) {
			if(getEncode(address) == EccType.REED_SOLOMON)
				throw he;
			this.getMemoryStatus().setStatus(address,
					he.getPosition(), ECCMemoryFaultType.UNFIXABLE_ERROR, he.getInput(), Bits.from(0));
			AbsimLog.memory(String.format(ECCMemoryFaultType.UNFIXABLE_ERROR.toString() + " - at 0x%08x - 0x%08x", address, he.getInput().toInt()));
			migrate(address);
			throw he;
		} catch (FixableErrorException se) {
			if(getEncode(address) == EccType.REED_SOLOMON)
				return se.getRecovered().toLong();
			
			this.getMemoryStatus().setStatus(address,
					se.getPosition(),
					ECCMemoryFaultType.FIXABLE_ERROR, se.getInput(), se.getRecovered() );
			AbsimLog.memory(String.format(ECCMemoryFaultType.FIXABLE_ERROR + " - at 0x%08x - 0x%08x", address, se.getInput().toInt()));
			migrate(address);
			return se.getRecovered().toLong();
		}
	
	}
	
	private boolean migrate(long address) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("N. PAGE MIGRATE");
		long pos = address/pageSize;
		long initialAddress = pos * pageSize;
		
		EccType typeOriginal = getEncode(address);
		EccType typeTo = typeOriginal.getNext();
		if(typeOriginal.compareTo(typeTo) ==0) {
			AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE ADDRESS from 0x%08x to 0x%08x - ALREADY USING %s", initialAddress, initialAddress+pageSize, typeTo));
			return false;
		}
		AbsimLog.memoryController(String.format("STARTING MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing address %s to %s", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing encode from %s to %s", typeOriginal, typeTo));
		for (long i = 0; i < pageSize; i++) {
			long nAddress = i + initialAddress;
			try {
				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION READ "+ typeOriginal);
				int data = typeOriginal.getEncode().decode(MemoryController.readBits(nAddress)).toInt();
				
				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+ typeTo);
				MemoryController.writeBits(nAddress, typeTo.getEncode().encode(Bits.from(data)));
			} catch (FixableErrorException e) {
				System.out.println(e.toString() + nAddress);
				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN "+typeTo);
				MemoryController.writeBits(nAddress, typeTo.getEncode().encode(e.getRecovered()));
			} catch (Exception e) {
				System.err.println(e.toString() + nAddress);
				SimulatorManager.getSim().getReport().memoryControllerInc("MIGRATION WRITTEN " + typeTo);
				MemoryController.writeBits(nAddress, typeTo.getEncode().encode(Bits.from(0)));
				AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE - 0x%08x", nAddress));
			}
		}
		map.put(pos, typeTo);
		AbsimLog.memory(String.format("END MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
		return true;
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
