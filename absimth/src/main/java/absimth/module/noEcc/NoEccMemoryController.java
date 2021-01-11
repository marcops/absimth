package absimth.module.noEcc;

import absimth.sim.Bits;
import absimth.sim.IMemoryController;
import absimth.sim.MemoryController;

public class NoEccMemoryController extends MemoryController implements IMemoryController {

	@Override
	public void write(long address, long data) {
		writeBits(address, Bits.from(data));
	}

	@Override
	public long read(long address) {
		return readBits(address).toLong();
	}
}
