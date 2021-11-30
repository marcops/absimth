package absimth.sim.memoryController;

import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.utils.Bits;

public interface IMemoryController {
	ECCMemoryStatus getMemoryStatus();
	
	void write(long address, long data) throws Exception;

	long read(long address) throws Exception;

	Bits justDecode(long address) throws Exception;

	EccType getCurrentEccType(long address);

}
