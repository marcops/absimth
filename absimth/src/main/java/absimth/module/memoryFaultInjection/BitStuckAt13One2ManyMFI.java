package absimth.module.memoryFaultInjection;

import absimth.sim.memory.IFaultInjection;

public class BitStuckAt13One2ManyMFI implements IFaultInjection {
	private Long initialTimestampInMilli = System.currentTimeMillis();
	@Override
	public void preInstruction()  {}
	@Override
	public void posInstruction() {}
	@Override
	public void onRead() throws Exception {}
	
	@Override
	public void onWrite() throws Exception {
//		EccType type = SimulatorManager.getSim().getMemoryController().getCurrentEccType(262134);
		if(System.currentTimeMillis() - initialTimestampInMilli < (5*1000)) {
			new BitStuckAt13OneMFI().onWrite();
		} else {
			new BitStuckAt13ManyMFI().onWrite();
		}
	}
	
}
