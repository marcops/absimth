package absimth.sim.memory;

public interface IMemoryController {
	
	void write(long address, long data) throws Exception;

	long read(long address) throws Exception;
}
