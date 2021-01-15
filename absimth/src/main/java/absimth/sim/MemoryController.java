package absimth.sim;

import absimth.sim.utils.AbsimLog;

public class MemoryController {
	protected static void writeBits(long address, Bits data) {
		SimulatorManager.getSim().getReport().incWrite();
		SimulatorManager.getSim().getMemory().write(address, data);
		AbsimLog.memory(String.format("W - 0x%08x - 0x%08x",  address, data.toInt()));
	}

	protected static Bits readBits(long address) {
		SimulatorManager.getSim().getReport().incRead();
		Bits b = SimulatorManager.getSim().getMemory().read(address);
		AbsimLog.memory(String.format("R - 0x%08x - 0x%08x",  address, b.toInt()));
		return b;
	}

//	public void writeOS(long address, int memoryReference , long data) {
//		SimulatorManager.getSim().getMemoryController().write(address, data);
//	}
//
//	public long readOS(long address, int memoryReference) {
//		return SimulatorManager.getSim().getMemoryController().read(address);
//		
//	}

}
