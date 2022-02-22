package absimth.sim.cpu;

import absimth.sim.os.model.IOSMemoryAccess;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ICPU {
	
	private Integer core;
	private Integer cpu;
	
//	void executeInstruction() throws Exception;
	public abstract String getName();
	public abstract int getPc();
//	ICPU2Mem getMemory();
	public abstract void initializeRegisters(int stackSize, int initialAddress);
	public abstract int getPrevPc();
	public abstract int[] getReg();
	public abstract void setPc(int pc);
	public abstract void setPrevPc(int prevPc);
	public abstract void setReg(int[] clone);
	public abstract ICPUInstruction getInstruction(int data);
	public abstract String executeInstruction(Integer data, IOSMemoryAccess memAccess) throws Exception;
//	void storeInstruction(int address, int data) throws Exception;
}
