package absimth.module.memoryFaultInjection;

import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;

public class ErrorAt1000ToManyMFI implements IFaultInjection {
	@Override
	public void preInstruction()  {}
	@Override
	public void posInstruction() {}
	@Override
	public void onRead() throws Exception {}
	
	@Override
	public void onWrite() throws Exception {
		EccType type = SimulatorManager.getSim().getMemoryController().getCurrentEccType(0);
		if(type == EccType.HAMMING_SECDEC) new ErrorAt1000MFI().onWrite();
		else new ErrorAt13ManyMFI().onWrite();
	}

}
