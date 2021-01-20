package absimth.module.memoryController.C2H;

import java.util.HashMap;

import absimth.exception.HardErrorException;
import absimth.exception.SoftErrorException;
import absimth.module.memoryController.util.ecc.CRC8;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.module.memoryController.util.ecc.Hamming;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IMemoryController;
import absimth.sim.memory.MemoryController;
import absimth.sim.memory.model.Bits;
import absimth.sim.memory.model.FaultAddressModel;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.utils.AbsimLog;

public class C2HMemoryController extends MemoryController implements IMemoryController {

	private HashMap<Long, EccType> map = new HashMap<>();
	
	//TODO DIN?
	private int pageSize = 32000; //4kb 
	
	@Override
	public void write(long address, long data) {
		EccType type = getEncode(address);
		MemoryController.writeBits(address, encode(Bits.from(data), type));
	}

	private EccType getEncode(long address) {
		return map.getOrDefault(address/pageSize,  EccType.CRC8);
	}

	@Override
	public long read(long address) {
		EccType type = getEncode(address);
		try {
			return decode(MemoryController.readBits(address), type).toLong();
		} catch (HardErrorException he) {
			SimulatorManager.getSim().getMemory().setStatus(address,
					FaultAddressModel.builder()
						.address(address)
					.build(), MemoryFaultType.HARD_ERROR);
			AbsimLog.memory(String.format("HARD_ERROR - at 0x%08x - 0x%08x", address, he.getInput()));
			migrate(address);
		} catch (SoftErrorException se) {
			SimulatorManager.getSim().getMemory().setStatus(address,
					FaultAddressModel.builder()
						.address(address)
						.position(se.getPosition())
						.build(),
					MemoryFaultType.SOFT_ERROR);
			AbsimLog.memory(String.format("SOFT_ERROR - at 0x%08x - 0x%08x", address, se.getInput()));
			
			migrate(address);
		}
		return 0;
	}
	
	private void migrate(long address) {
		long pos = address/pageSize;
		long initialAddress = pos * pageSize;
		
		EccType type = getEncode(address);
		if (type == EccType.CRC8) {
			AbsimLog.memory(String.format("STARTING MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
			AbsimLog.memory(String.format("Changing encode from %s to %s", initialAddress, initialAddress+pageSize));
			for (long i = 0; i < pageSize; i++) {
				long nAddress = i + initialAddress;
				try {
					Bits data = decode(MemoryController.readBits(nAddress), EccType.CRC8);
					MemoryController.writeBits(nAddress, encode(data, EccType.HAMMING_SECDEC));
				} catch (HardErrorException | SoftErrorException e) {
					e.printStackTrace();
					AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
				}
			}
			map.put(pos, EccType.HAMMING_SECDEC);
			AbsimLog.memory(String.format("END MIGRATION  - 0x%08x - 0x%08x", initialAddress, initialAddress+pageSize));
		} else {
			AbsimLog.memory(String.format("WAS NOT POSSIBLE MIGRATE - 0x%08x - 0x%08x - ALREADY USING %s", initialAddress, initialAddress+pageSize, EccType.HAMMING_SECDEC));
		}
	}
	
	
	private static Bits encode(Bits data, EccType type) {
		if (type == EccType.CRC8)
			return CRC8.encode(data);
		if (type == EccType.HAMMING_SECDEC)
			return Hamming.encode(data);
		return data;
	}

	private static Bits decode(Bits data, EccType type) throws HardErrorException, SoftErrorException {
		if (type == EccType.CRC8)
			return CRC8.decode(data);
		if (type == EccType.HAMMING_SECDEC)
			return Hamming.decode(data);
		return data;

	}
	
}
