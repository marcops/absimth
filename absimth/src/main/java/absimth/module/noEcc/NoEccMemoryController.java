package absimth.module.noEcc;

import absimth.sim.memory.faultInjection.IMemoryController;
import absimth.sim.memory.faultInjection.MemoryController;
import absimth.sim.memory.faultInjection.model.Bits;

public class NoEccMemoryController  extends MemoryController implements IMemoryController {

	@Override
	public void write(long address, long data) {
		MemoryController.writeBits(address, Bits.from(data));
	}

	@Override
	public long read(long address) {
		return MemoryController.readBits(address).toLong();
	}
}
