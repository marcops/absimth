package absimth.module.cpu.riscv32i;

import absimth.sim.SimulatorManager;

public class RV32ICpu2Mem {
	private int initialAddress;
	
	public void setInitialAddress(int initialAddress) {
		this.initialAddress = initialAddress;
	}
	
	// Stores a single byte in the memory array
	public void storeByte(int addr, byte data) throws Exception  {
		int p = addr%4; 
		int w = (int) SimulatorManager.getSim().getMemoryController().read(initialAddress + (addr / 4));
		byte b[] = splitBytes(w);
		b[p] = data;
		SimulatorManager.getSim().getMemoryController().write(initialAddress + (addr/4), byte2int(b));
	}
	
	public static int java2int(int val) {
		byte[] b = RV32ICpu2Mem.splitBytes(val);
		byte[] c = new byte[4];
		c[0] = b[0];
		c[3] = b[1];
		c[2] = b[2];
		c[1] = b[3];
		return byte2int(c);
	}

	// Stores a half word in the memory array
	public void storeHalfWord(int addr, short data) throws Exception {
		int p = addr%4; 
		int w = (int) SimulatorManager.getSim().getMemoryController().read(initialAddress + (addr / 4));
		byte b[] = splitBytes(w);
		b[p] =  (byte) ((data & 0x000000FF));
		b[p+1] = (byte) ((data & 0x0000FF00) >>> 8);
		SimulatorManager.getSim().getMemoryController().write(initialAddress + (addr/4), byte2int(b));
		throw new Exception("VALIDAR");
	}

	// Stores a word in the memory array
	public void storeWord(int addr, int data) throws Exception {
		SimulatorManager.getSim().getMemoryController().write(initialAddress + (addr/4), data);
	}

	// Returns the byte in the memory given by the address.
	public byte getByte(int addr) throws Exception {
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
	
	private static int byte2int(byte[] data) {
		return (data[3]<<24)&0xff000000|
			       (data[2]<<16)&0x00ff0000|
			       (data[1]<< 8)&0x0000ff00|
			       (data[0]<< 0)&0x000000ff;
    }
	
	private static byte getByte(int addr, int data) {
		byte[] b = splitBytes(data);
		int p = addr % 4;
		p = p > 0 ? p : (p * -1);
		return b[p];
	}

	// Returns half word from memory given by address
	private static int getHalfWord(int addr, int data) {
		return (getByte(addr + 1, data) << 8) | (getByte(addr , data) & 0xFF);
	}
	
	public int getHalfWord(int addr) throws Exception {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(initialAddress + (addr / 4));
		return getHalfWord(addr, data);
	}
	// Returns word from memory given by address
	public int getWord(int addr) throws Exception {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(initialAddress + (addr / 4));
		return getWord(addr, data);
	}

	private static int getWord(int addr, int data) {
		return (getHalfWord(addr + 2, data) << 16) | (getHalfWord(addr, data) & 0xFFFF);
	}
	
	// Returns string starting at the address given and ends when next memory
	// address is zero.
	public String getString(int addr) throws Exception {
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
