package absimth.sim.cpu.riscv32i;

import absimth.sim.SimulatorManager;

public class RV32ICpu2Mem {
	private int initialAddress;
	
	public void setInitialAddress(int initialAddress) {
		this.initialAddress = initialAddress;
	}
	
	// Stores a single byte in the memory array
//	public void storeByte(int addr, int data) {
//		SimulatorManager.getSim().getMemoryController().write(initialAddress + (addr/4), data);
//	}
//
//	// Stores a half word in the memory array
//	public void storeHalfWord(int addr, short data) {
//		SimulatorManager.getSim().getMemoryController().write(initialAddress + (addr/4), data);
//	}
//
//	// Stores a word in the memory array
//	public void storeWord(int addr, int data) {
//		SimulatorManager.getSim().getMemoryController().write(initialAddress + (addr/4), data);
//	}

	// Stores a single byte in the memory array
	public void store(int addr,  int data) {
		SimulatorManager.getSim().getMemoryController().write(initialAddress + (addr/4), data);
	}

	// Returns the byte in the memory given by the address.
	public byte getByte(int addr) {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(initialAddress + (addr / 4));
		return getByte(addr, data);
	}

	private static byte getByte(int addr, int data) {
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
	private static int getHalfWord(int addr, int data) {
		return (getByte(addr + 1, data) << 8) | (getByte(addr , data) & 0xFF);
	}
	
	public int getHalfWord(int addr) {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(initialAddress + (addr / 4));
		return getHalfWord(addr, data);
	}
	// Returns word from memory given by address
	public int getWord(int addr) {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(initialAddress + (addr / 4));
		return getWord(addr, data);
	}

	private static int getWord(int addr, int data) {
		return (getHalfWord(addr + 2, data) << 16) | (getHalfWord(addr, data) & 0xFFFF);
	}
	
	// Returns string starting at the address given and ends when next memory
	// address is zero.
	//TODO MELHORAR ACESSO AQUI
	//TODO REVER BUG DO PRINT?
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
