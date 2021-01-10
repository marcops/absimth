package absimth.sim.cpu.riscv32i;

import absimth.sim.MemoryController;

public class RV32ICpu2Mem {
	private MemoryController memoryController = new MemoryController();
	//TODO the current program size, remove from here
//	private int programSize;
	/**
	 * Constructor for Memory Initializes as a byte array of size given by argument
	 */
//	public RV32ICpu2Mem(int MEMORY_SIZE_IN_BYTES) {
//		programSize = MEMORY_SIZE_IN_BYTES;
//	}

	// Stores a single byte in the memory array
	public void storeByte(int addr, int data) {
		memoryController.write(addr/4, data);
	}

	// Stores a half word in the memory array
	public void storeHalfWord(int addr, short data) {
		memoryController.write(addr/4, data);
	}

	// Stores a word in the memory array
	public void storeWord(int addr, int data) {
		memoryController.write(addr/4, data);
	}

	// Returns the byte in the memory given by the address.
	public byte getByte(int addr) {
		int data = (int)memoryController.read(addr/4);
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

//	public int getSize() {
//		return programSize - 1;
//	}
}
