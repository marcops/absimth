package absimth.module.memoryFaultInjection;

import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.Bits;

public class BitStuckAt_OneMFI implements IFaultInjection {
	private int addressWithProblem = 262134;
	
	public BitStuckAt_OneMFI() {}
	public BitStuckAt_OneMFI(int addressWithProblem) {
		this.addressWithProblem = addressWithProblem; 
	}
	
	@Override
	public void preInstruction()  {}
	@Override
	public void posInstruction() {}
	@Override
	public boolean onRead(long address) throws Exception {
		return false;
	}
	
	@Override
	public boolean onWrite(long address, Bits data) throws Exception {
		final int POSITION_FLIP = 5;

		Bits number = SimulatorManager.getSim().getMemory().read(addressWithProblem);
		if(number.toLong() != 0) 
			number.flip(POSITION_FLIP);
		
		SimulatorManager.getSim().getMemory().write(addressWithProblem, number);
		SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(addressWithProblem, Set.of(POSITION_FLIP), ECCMemoryFaultType.INVERTED);
		return number.toLong() != 0;
	}


}
