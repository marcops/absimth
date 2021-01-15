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

	public static byte[] splitBytes(int data) {
		byte []b = new byte[4];
		b[0]  = (byte) ((data & 0x000000FF));
		b[1]  = (byte) ((data & 0x0000FF00) >>> 8);
		b[2]  = (byte) ((data & 0x00FF0000) >>> 16);
		b[3]  = (byte) ((data & 0xFF000000) >>> 24);
		return b;
	}
	
	private static byte getByte(int addr, int data) {
		byte[] b = splitBytes(data);
		return b[addr % 4];
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
	public String getString(int addr) {
		String returnValue = "";
		for (int i = 0;; i++) {
			int w = getWord(addr + (i*4));
			byte[] b = splitBytes(w);

			for (int j = 0; j < 4; j++) {
				if (b[j] == 0) return returnValue;
				returnValue += (char) b[j];
			}

		}
	}

}
