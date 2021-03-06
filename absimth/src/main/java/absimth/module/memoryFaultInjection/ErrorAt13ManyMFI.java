package absimth.module.memoryFaultInjection;

import java.util.HashSet;
import java.util.Set;

import absimth.module.cpu.riscv32.module.RV32Cpu2Mem;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.utils.Bits;

public class ErrorAt13ManyMFI implements IFaultInjection {
	@Override
	public void haveToCreateError() throws Exception {
		final int ADDRESS_WITH_ERROR = 0x13;
		
		setControllerAddress(ADDRESS_WITH_ERROR);
		setErrorOnMemory(ADDRESS_WITH_ERROR);
	}

	private static void setErrorOnMemory(final int addressWithProblem) throws Exception  {
		EccType type = SimulatorManager.getSim().getMemoryController().getCurrentEccType(addressWithProblem);
		try {
			Bits f = SimulatorManager.getSim().getMemoryController().justDecode(addressWithProblem);
			Bits l = type.getEncode().encode(f);
			Set<Integer> set = new HashSet<>();
			for(int i=0;i<20;i++) {
				l.flip(i);
				set.add(i);
			}
			
			SimulatorManager.getSim().getMemory().write(addressWithProblem, l);
			SimulatorManager.getSim().getMemory().getMemoryStatus().setStatus(addressWithProblem, set, MemoryFaultType.INVERTED);
		} catch (Exception e) {
			return;
		}
	}
	private static void setControllerAddress(final int addressWithProblem) throws Exception  {
		EccType type = SimulatorManager.getSim().getMemoryController().getCurrentEccType(1);
		SimulatorManager.getSim().getMemory().write(1, type.getEncode().encode(Bits.from(RV32Cpu2Mem.java2int(addressWithProblem))));
	}
}
