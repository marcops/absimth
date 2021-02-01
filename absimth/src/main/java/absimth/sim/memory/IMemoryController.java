package absimth.sim.memory;

import absimth.sim.utils.Bits;

public interface IMemoryController {
	
	void write(long address, long data) throws Exception;

	long read(long address) throws Exception;
	
	Bits justDecode(long address) throws Exception;

}
