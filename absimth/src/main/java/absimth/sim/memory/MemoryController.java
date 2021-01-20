package absimth.sim.memory;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.model.Bits;
import absimth.sim.utils.AbsimLog;

public class MemoryController {
	protected static void writeBits(long address, Bits data) {
		SimulatorManager.getSim().getFaultMode().haveToCreateError();

		SimulatorManager.getSim().getReport().incWrite();
		SimulatorManager.getSim().getMemory().write(address, data);
		AbsimLog.memory(String.format("W - 0x%08x - 0x%08x",  address, data.toInt()));
	}

	

	protected static Bits readBits(long address) {
		SimulatorManager.getSim().getFaultMode().haveToCreateError();
		
		SimulatorManager.getSim().getReport().incRead();
		Bits b = SimulatorManager.getSim().getMemory().read(address);
		AbsimLog.memory(String.format("R - 0x%08x - 0x%08x",  address, b.toInt()));
		return b;
	}

}
