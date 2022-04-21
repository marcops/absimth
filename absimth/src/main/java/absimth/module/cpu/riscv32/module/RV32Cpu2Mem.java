package absimth.module.cpu.riscv32.module;

import absimth.sim.SimulatorManager;
import absimth.sim.cpu.ICPU2Mem;

public class RV32Cpu2Mem implements ICPU2Mem {
	// Stores a single byte in the memory array
	@Override
	public void storeByte(int addr,int p, byte data) throws Exception  {
//		int p = addr%4; 
		int w = (int) SimulatorManager.getSim().getMemoryController().read(addr);
		byte b[] = splitBytes(w);
		b[p] = data;
		SimulatorManager.getSim().getMemoryController().write(addr, byte2int(b));
	}
	
	public static int java2int(int val) {
		byte[] b = RV32Cpu2Mem.splitBytes(val);
		byte[] c = new byte[4];
		c[0] = b[0];
		c[3] = b[1];
		c[2] = b[2];
		c[1] = b[3];
		return byte2int(c);
	}

	// Stores a half word in the memory array
	@Override
	public void storeHalfWord(int addr, int p, short data) throws Exception {
//		int p = addr%4; 
		int w = (int) SimulatorManager.getSim().getMemoryController().read(addr);
		byte b[] = splitBytes(w);
		b[p] =  (byte) ((data & 0x000000FF));
		b[p+1] = (byte) ((data & 0x0000FF00) >>> 8);
		SimulatorManager.getSim().getMemoryController().write(addr, byte2int(b));
//		throw new Exception("VALIDAR");
	}

	// Stores a word in the memory array
	@Override
	public void storeWord(int addr, int data) throws Exception {
		SimulatorManager.getSim().getMemoryController().write(addr, data);
	}

	// Returns the byte in the memory given by the address.
	@Override
	public byte getByte(int addr) throws Exception {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(addr);
//		int aInitial = (addr / 4)*4;
		int p = addr % 4;
		p = p > 0 ? p : (p * -1);
		return getBytePos(p, data);
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
	
	private static byte getBytePos(int p, int data) {
		return splitBytes(data)[p];
	}

	// Returns half word from memory given by address
	private static int getHalfWordPos(int pos, int data) {
		return (getBytePos(pos + 1, data) << 8) | (getBytePos(pos , data) & 0xFF);
	}
	
	public int getHalfWord(int addr) throws Exception {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(addr);
		return getHalfWordPos(0, data);
	}
	// Returns word from memory given by address
	public int getWord(int addr) throws Exception {
		int data = (int) SimulatorManager.getSim().getMemoryController().read(addr);
		return getWordPos(data);
	}

	private static int getWordPos(int data) {
		return (getHalfWordPos(2, data) << 16) | (getHalfWordPos(0, data) & 0xFFFF);
	}
	
	// Returns string starting at the address given and ends when next memory
	// address is zero.
	@Override
	public String getString(int addr) throws Exception {
		String returnValue = "";
		for (int i = 0;; i++) {			
			int w = getWord(addr + i);
			byte[] b = splitBytes(w);

			for (int j = 0; j < 4; j++) {
				if (b[j] == 0) return returnValue;
				returnValue += (char) b[j];
			}
		}
	}

}
