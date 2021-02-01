package absimth.module.memoryController.util.ecc;

import absimth.sim.utils.Bits;

public interface IEccType {
	Bits decode(Bits data) throws Exception;
	Bits encode(Bits data);
}
