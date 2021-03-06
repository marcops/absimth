package absimth.sim.memory;

import absimth.sim.SimulatorManager;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;

public class MemoryController {
	protected static void writeBits(long address, Bits data) throws Exception {
		SimulatorManager.getSim().getFaultMode().haveToCreateError();

		if(SimulatorManager.getSim().isInInstructionMode()) SimulatorManager.getSim().getReport().getMemory().incWriteInstruction(data.length());
		else SimulatorManager.getSim().getReport().getMemory().incWriteData(data.length());
		
		SimulatorManager.getSim().getMemory().write(address, data);
		AbsimLog.memory(String.format("W - 0x%08x - 0x%08x",  address, data.toInt()));
	}

	protected static Bits readBits(long address) throws Exception {
		SimulatorManager.getSim().getFaultMode().haveToCreateError();
		
		Bits b = SimulatorManager.getSim().getMemory().read(address);
		
		if(SimulatorManager.getSim().isInInstructionMode()) SimulatorManager.getSim().getReport().getMemory().incReadInstruction(b.length());
		else SimulatorManager.getSim().getReport().getMemory().incReadData(b.length());
		
		AbsimLog.memory(String.format("R - 0x%08x - 0x%08x",  address, b.toInt()));
		return b;
	}

}
