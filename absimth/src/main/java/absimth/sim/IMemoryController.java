package absimth.sim;

public interface IMemoryController {
	
	void write(long address, long data);

	long read(long address);
}
