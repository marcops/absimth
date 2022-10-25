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
	public Bits onWrite(long address, Bits data) throws Exception {
		if(address != addressWithProblem) return data;
		try {
			Bits newData = Bits.from(data);

			Set<Integer> set = new HashSet<>();
			for (int i = 0; i < 2; i++) {
				int jump = 19;
				newData.flip(i*jump);
				set.add(i*jump);
			}

			SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(addressWithProblem, set, ECCMemoryFaultType.INVERTED, data, newData);
			return newData;
		} catch (Exception e) {
			System.err.println(e);
			return data;
		}
	}
}
