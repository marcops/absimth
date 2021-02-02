package absimth.module.memoryController;

import java.util.HashMap;

import absimth.exception.HardErrorException;
import absimth.exception.SoftErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IMemoryController;
import absimth.sim.memory.MemoryController;
import absimth.sim.memory.model.FaultAddressModel;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;

public class C2HMemoryController extends MemoryController implements IMemoryController {

	private HashMap<Long, EccType> map = new HashMap<>();
	
	//TODO DIN?
	private int pageSize = 32000; //4kb 
	
	@Override
	public void write(long address, long data) throws Exception {
		EccType type = getEncode(address);
		MemoryController.writeBits(address, type.getEncode().encode(Bits.from(data)));
	}

	private EccType getEncode(long address) {
		return map.getOrDefault(address/pageSize,  EccType.CRC8);
	}

	@Override
	public long read(long address) throws Exception {
		EccType type = getEncode(address);
		try {
			return type.getEncode().decode(MemoryController.readBits(address)).toLong();
		} catch (HardErrorException he) {
			SimulatorManager.getSim().getMemory().setStatus(address,
					FaultAddressModel.builder()
						.address(address)
					.build(), MemoryFaultType.HARD_ERROR);
			AbsimLog.memory(String.format("HARD_ERROR - at 0x%08x - 0x%08x", address, he.getInput().toInt()));
			migrate(address);
			return 0;
		} catch (SoftErrorException se) {
			SimulatorManager.getSim().getMemory().setStatus(address,
					FaultAddressModel.builder()
						.address(address)
						.position(se.getPosition())
						.build(),
					MemoryFaultType.SOFT_ERROR);
			AbsimLog.memory(String.format("SOFT_ERROR - at 0x%08x - 0x%08x", address, se.getInput().toInt()));
			
			migrate(address);
			type = getEncode(address);
			return type.getEncode().decode(MemoryController.readBits(address)).toLong();
		}
	
	}
	
	private void migrate(long address) throws Exception {
		long pos = address/pageSize;
		long initialAddress = pos * pageSize;
		
		EccType typeOriginal = getEncode(address);
		EccType typeTo = typeOriginal.getNext();
		if(typeOriginal.compareTo(typeTo) ==0) {
			AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE ADDRESS from 0x%08x to 0x%08x - ALREADY USING %s", initialAddress, initialAddress+pageSize, typeTo));
			return;
		}
		AbsimLog.memory(String.format("STARTING MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing address %s to %s", initialAddress, initialAddress+pageSize));
		AbsimLog.memory(String.format("Changing encode from %s to %s", typeOriginal, typeTo));
		for (long i = 0; i < pageSize; i++) {
			long nAddress = i + initialAddress;
			try {
				int data = typeOriginal.getEncode().decode(MemoryController.readBits(nAddress)).toInt();
				MemoryController.writeBits(nAddress, typeTo.getEncode().encode(Bits.from(data)));
			} catch (Exception e) {
				System.err.println(e.toString());
				MemoryController.writeBits(nAddress, typeTo.getEncode().encode(Bits.from(0)));
				AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE - 0x%08x", nAddress));
			}
		}
		map.put(pos, typeTo);
		AbsimLog.memory(String.format("END MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		EccType type = getEncode(address);
		Bits b = SimulatorManager.getSim().getMemory().read(address);
		return type.getEncode().decode(b);
	}
	
}
