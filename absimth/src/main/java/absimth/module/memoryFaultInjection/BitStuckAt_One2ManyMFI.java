package absimth.module.memoryFaultInjection;

import absimth.sim.memory.IFaultInjection;
import absimth.sim.utils.Bits;

public class BitStuckAt_One2ManyMFI implements IFaultInjection {
	private int addressToBF = 262134;
	private Boolean inverted = false;
	
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
	public Bits onWrite(long address, Bits data) throws Exception {
		if (address != addressToBF) return data;
		Long lData = data.toLong();
		
		if(!inverted) {
			inverted = true;
			return new BitStuckAt_OneMFI(addressToBF).onWrite(addressToBF, data);
		}
		return new BitStuckAt_ManyMFI(addressToBF).onWrite(addressToBF, Bits.from(lData));
	}
}
