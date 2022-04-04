package absimth.module.memoryFaultInjection;

import java.util.Set;

import absimth.module.cpu.riscv32.module.RV32Cpu2Mem;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.Bits;

public class ErrorAt1000MFI implements IFaultInjection {
	@Override
	public void preInstruction()  {}
	@Override
	public void posInstruction() {}
	@Override
	public void onRead() throws Exception {}
	@Override
	public void onWrite() throws Exception {
		final int ADDRESS_WITH_ERROR = 1000;
		
//		setControllerAddress(ADDRESS_WITH_ERROR);
		setErrorOnMemory(ADDRESS_WITH_ERROR);
	}

	private static void setErrorOnMemory(final int addressWithProblem) throws Exception {
		final int POSITION_FLIP = 0;
		
		EccType type = SimulatorManager.getSim().getMemoryController().getCurrentEccType(addressWithProblem);
		
		Bits number = SimulatorManager.getSim().getMemory().read(addressWithProblem);
//		if (number.length() > 5)
//			number.flip(POSITION_FLIP);
//		else
		number = type.getEncode().encode(Bits.from(5));
		number.flip(POSITION_FLIP);
		
		SimulatorManager.getSim().getMemory().write(addressWithProblem, number);
		SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(addressWithProblem, Set.of(POSITION_FLIP), ECCMemoryFaultType.INVERTED);
	}

//	private static void setControllerAddress(final int addressWithProblem) throws Exception  {
//		EccType type = SimulatorManager.getSim().getMemoryController().getCurrentEccType(1);
//		SimulatorManager.getSim().getMemory().write(1, type.getEncode().encode(Bits.from(RV32Cpu2Mem.java2int(addressWithProblem))));
//	}
	
}
