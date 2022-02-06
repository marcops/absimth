package absimth.sim.cpu;

import absimth.sim.os.model.IOSMemoryAccess;

public interface ICPU {
//	void executeInstruction() throws Exception;
	String getName();
	int getPc();
//	ICPU2Mem getMemory();
	void initializeRegisters(int stackSize, int initialAddress);
	int getPrevPc();
	int[] getReg();
	void setPc(int pc);
	void setPrevPc(int prevPc);
	void setReg(int[] clone);
	ICPUInstruction getInstruction(int data);
	String executeInstruction(Integer data, IOSMemoryAccess memAccess) throws Exception;
//	void storeInstruction(int address, int data) throws Exception;
}
