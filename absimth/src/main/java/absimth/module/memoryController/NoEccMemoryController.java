package absimth.module.memoryController;

import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memoryController.IMemoryController;
import absimth.sim.memoryController.MemoryController;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.utils.Bits;

public class NoEccMemoryController  extends MemoryController implements IMemoryController {

	@Override
	public void write(long address, long data) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.NONE);
		MemoryController.writeBits(address, Bits.from(data));
	}

	@Override
	public long read(long address) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.NONE);
		ECCMemoryFaultModel model = getMemoryStatus().getFromAddress(address);
		if(model!= null) model.setDirtAccess(true);
		return MemoryController.readBits(address).toLong();
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		return SimulatorManager.getSim().getMemory().read(address);
	}

	@Override
	public EccType getCurrentEccType(long address) {
		return EccType.NONE;
	}
}
