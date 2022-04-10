package absimth.module.memoryFaultInjection;

import absimth.sim.memory.IFaultInjection;
import absimth.sim.utils.Bits;

public class NoFaultErrorMFI implements IFaultInjection {

	@Override
	public void preInstruction() {
	}

	@Override
	public void posInstruction() {
	}

	@Override
	public boolean onRead(long address) throws Exception {
		return false;
	}

	@Override
	public boolean onWrite(long address, Bits data) throws Exception {
		return false;
	}
	
}
