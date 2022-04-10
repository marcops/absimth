package absimth.module.memoryFaultInjection;

import java.util.HashSet;
import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.Bits;

public class BitStuckAt_ManyMFI implements IFaultInjection {
	private int addressWithProblem = 262134;
	
	public BitStuckAt_ManyMFI() {}
	public BitStuckAt_ManyMFI(int addressWithProblem) {
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
		try {
			Bits number = SimulatorManager.getSim().getMemory().read(addressWithProblem);

			Set<Integer> set = new HashSet<>();
			for (int i = 0; i < 3; i++) {
				number.flip(i*5);
				set.add(i*5);
			}

			SimulatorManager.getSim().getMemory().write(addressWithProblem, number);
			SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(addressWithProblem, set, ECCMemoryFaultType.INVERTED);
			return true;
		} catch (Exception e) {
			System.err.println(e);
			return false;
		}
	}
}
