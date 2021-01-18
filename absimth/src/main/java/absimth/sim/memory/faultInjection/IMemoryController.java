package absimth.sim.memory.faultInjection;

public interface IMemoryController {
	
	void write(long address, long data);

	long read(long address);
}
