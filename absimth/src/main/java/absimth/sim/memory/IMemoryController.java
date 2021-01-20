package absimth.sim.memory;

public interface IMemoryController {
	
	void write(long address, long data);

	long read(long address);
}
