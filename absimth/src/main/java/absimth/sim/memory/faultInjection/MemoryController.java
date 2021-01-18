package absimth.sim.memory.faultInjection;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.faultInjection.model.Bits;
import absimth.sim.memory.faultInjection.model.FaultModel;
import absimth.sim.memory.faultInjection.model.MemoryStatus;
import absimth.sim.utils.AbsimLog;

public class MemoryController {
	private static int count=0;
	protected static void writeBits(long address, Bits data) {
		haveToCreateError();
		SimulatorManager.getSim().getReport().incWrite();
		SimulatorManager.getSim().getMemory().write(address, data);
		AbsimLog.memory(String.format("W - 0x%08x - 0x%08x",  address, data.toInt()));
	}

	private static void haveToCreateError() {
		count++;
		//TODO COUNT here
		if(count>100) {
			count = 0;
			IFaultMode mode = SimulatorManager.getSim().getFaultMode();
			FaultModel model = mode.getFault();
			Bits b = SimulatorManager.getSim().getMemory().read(model.getAddress());
			b.flip(model.getPosition());
			SimulatorManager.getSim().getMemory().write(model.getAddress(), b);
			SimulatorManager.getSim().getMemory().setStatus(model.getAddress(), MemoryStatus.INVERTED);
			AbsimLog.memory(String.format("I - inverted bit at %d - 0x%08x - 0x%08x", model.getPosition(), model.getAddress(), b.toInt()));
			
		}
		
	}

	protected static Bits readBits(long address) {
		haveToCreateError();
		SimulatorManager.getSim().getReport().incRead();
		Bits b = SimulatorManager.getSim().getMemory().read(address);
		AbsimLog.memory(String.format("R - 0x%08x - 0x%08x",  address, b.toInt()));
		return b;
	}

}
