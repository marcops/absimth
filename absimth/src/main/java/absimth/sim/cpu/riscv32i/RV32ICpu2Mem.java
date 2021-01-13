package absimth.sim.cpu.riscv32i;

import absimth.sim.SimulatorManager;

public class RV32ICpu2Mem {
	private int programId;
	
	public void setProgramId(int programId) {
		this.programId = programId;
	}

	// Stores a single byte in the memory array
	public void store(int addr,  int data) {
		int ref = programId * SimulatorManager.STACK_POINTER_PROGRAM_SIZE;
		SimulatorManager.getSim().getMemoryController().write(ref+ (addr/4), data);
	}

	// Returns the byte in the memory given by the address.
	public byte getByte(int addr) {
		int ref = programId * SimulatorManager.STACK_POINTER_PROGRAM_SIZE;
		int data = (int) SimulatorManager.getSim().getMemoryController().read(ref+(addr / 4));
		return getByte(addr, data);
	}

	public byte getByte(int addr, int data) {
		int mod = addr % 4;
		
		byte b0  = (byte) ((data & 0x000000FF));
		byte b1  = (byte) ((data & 0x0000FF00) >>> 8);
		byte b2  = (byte) ((data & 0x00FF0000) >>> 16);
		byte b3  = (byte) ((data & 0xFF000000) >>> 24);
		
//		System.out.println(b + ",m=" + mod + ",b0="+b0);
		if(mod == 0) return b0;
		if(mod == 1) return b1;
		if(mod == 2) return b2;
		return b3;
	}

	// Returns half word from memory given by address
	public int getHalfWord(int addr) {
		return (getByte(addr + 1) << 8) | (getByte(addr) & 0xFF);
	}

	// Returns word from memory given by address
	public int getWord(int addr) {
		return (getHalfWord(addr + 2) << 16) | (getHalfWord(addr) & 0xFFFF);
	}

	// Returns string starting at the address given and ends when next memory
	// address is zero.
	public String getString(int addr) {
		String returnValue = "";
		int i = 0;
		while (getByte(addr + i) != 0) {
			returnValue += (char) getByte(addr + i);
			i++;
		}
		return returnValue;
	}

}
