package absimth.module.memoryFaultInjection;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.utils.Bits;

public class BitStuckAt_One2ManyMFI implements IFaultInjection {
	private int addressToBF = 262134;
	private Boolean processed = false;
	
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
		try {
			Long lData = data.toLong();
			if(!processed) {
				data = new BitStuckAt_OneMFI(addressToBF).onWrite(addressToBF, data);
				lData = SimulatorManager.getSim().getMemoryController().read(addressToBF);
			}
			return new BitStuckAt_ManyMFI(addressToBF).onWrite(addressToBF, Bits.from(lData));
		} catch (Exception e) {
		}
		return data;
	}
}
