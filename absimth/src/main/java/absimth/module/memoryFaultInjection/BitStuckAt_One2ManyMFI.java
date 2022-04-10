package absimth.module.memoryFaultInjection;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.utils.Bits;

public class BitStuckAt_One2ManyMFI implements IFaultInjection {
	private int addressToBF = 2;
	@Override
	public void preInstruction()  {}
	@Override
	public void posInstruction() {
		try {
			new BitStuckAt_OneMFI(addressToBF).onWrite(addressToBF, null);
			SimulatorManager.getSim().getMemoryController().read(addressToBF);
			new BitStuckAt_ManyMFI(addressToBF+1).onWrite(addressToBF+1, null);
		}catch (Exception e) {
		}
		
	}
	@Override
	public boolean onRead(long address) throws Exception {
//		if(address == addressToBF) readed = true;
		return false;
	}
	
	@Override
	public boolean onWrite(long address, Bits data) throws Exception {
//		if (!readed || !oneStatus) 
//			oneStatus = new BitStuckAt_OneMFI(addressToBF).onWrite(addressToBF, data);
//		else if(SimulatorManager.getSim().getMemoryController().getCurrentEccType(addressToBF+1).equals(EccType.REED_SOLOMON))
//			return new BitStuckAt_ManyMFI(addressToBF+1).onWrite(addressToBF+1, null);
//		
		return false;
		
	}
}
