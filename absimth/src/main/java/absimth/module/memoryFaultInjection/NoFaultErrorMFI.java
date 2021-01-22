package absimth.module.memoryFaultInjection;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memory.model.Bits;

public class NoFaultErrorMFI implements IFaultInjection {

	@Override
	public void haveToCreateError() throws Exception {
		SimulatorManager.getSim().getMemory().write(1,Bits.from(5));
	}


}
