package absimth.sim.cpu;

public interface ICPU {
//	void executeInstruction() throws Exception;
	String getName();
	int getPc();
	ICPU2Mem getMemory();
	void initializeRegisters(int stackSize, int initialAddress);
	int getPrevPc();
	int[] getReg();
	void setPc(int pc);
	void setPrevPc(int prevPc);
	void setReg(int[] clone);
	ICPUInstruction getInstruction(int data);
	void executeInstruction(Integer data) throws Exception;
}
