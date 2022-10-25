package absimth.module.memoryFaultInjection;

import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.Bits;

public class BitStuckAt_OneMFI implements IFaultInjection {
	private int addressWithProblem = 262134;

	public BitStuckAt_OneMFI() {
	}

	public BitStuckAt_OneMFI(int addressWithProblem) {
		this.addressWithProblem = addressWithProblem;
	}

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
		if(address != addressWithProblem) return data;
		final int POSITION_FLIP = 19;
		
		Bits newBits = Bits.from(data);
		newBits.flip(POSITION_FLIP);

		SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(addressWithProblem, Set.of(POSITION_FLIP), ECCMemoryFaultType.INVERTED, data, newBits);
		return newBits;
	}

}
