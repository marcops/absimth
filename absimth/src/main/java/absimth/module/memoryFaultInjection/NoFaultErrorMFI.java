package absimth.module.memoryFaultInjection;

import absimth.sim.memory.IFaultInjection;

public class NoFaultErrorMFI implements IFaultInjection {

	@Override
	public void preInstruction() {
	}

	@Override
	public void posInstruction() {
	}

	@Override
	public void onRead() throws Exception {
	}

	@Override
	public void onWrite() throws Exception {
	}
	
}
