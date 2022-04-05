package absimth.module.memoryFaultInjection;

import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.Bits;

public class BitStuckAt13OneMFI implements IFaultInjection {
	@Override
	public void preInstruction()  {}
	@Override
	public void posInstruction() {}
	@Override
	public void onRead() throws Exception {}
	
	@Override
	public void onWrite() throws Exception {
		final int ADDRESS_WITH_ERROR = 262134;
		setErrorOnMemory(ADDRESS_WITH_ERROR);
	}

	private static void setErrorOnMemory(final int addressWithProblem) throws Exception {
		final int POSITION_FLIP = 5;

		Bits number = SimulatorManager.getSim().getMemory().read(addressWithProblem);
		if(number.toLong() != 0)  number.set(POSITION_FLIP, true);
		
		SimulatorManager.getSim().getMemory().write(addressWithProblem, number);
		SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(addressWithProblem, Set.of(POSITION_FLIP), ECCMemoryFaultType.INVERTED);
	}

}
