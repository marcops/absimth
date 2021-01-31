package absimth.sim.cpu;

public interface ICPU {
	void executeInstruction() throws Exception;
	long getWordSize();
}
