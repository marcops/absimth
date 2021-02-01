package absimth.module.memoryController;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.IMemoryController;
import absimth.sim.memory.MemoryController;
import absimth.sim.utils.Bits;

public class NoEccMemoryController  extends MemoryController implements IMemoryController {

	@Override
	public void write(long address, long data) throws Exception {
		MemoryController.writeBits(address, Bits.from(data));
	}

	@Override
	public long read(long address) throws Exception {
		return MemoryController.readBits(address).toLong();
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		return SimulatorManager.getSim().getMemory().read(address);
	}
}
