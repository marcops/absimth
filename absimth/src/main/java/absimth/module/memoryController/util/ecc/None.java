package absimth.module.memoryController.util.ecc;

import absimth.exception.FixableErrorException;
import absimth.exception.UnfixableErrorException;
import absimth.sim.utils.Bits;

public class None implements IEccType {
	@Override
	public Bits decode(Bits data) throws UnfixableErrorException, FixableErrorException {
		return data;
	}

	@Override
	public Bits encode(Bits signal) {
		return signal;
	}
}
