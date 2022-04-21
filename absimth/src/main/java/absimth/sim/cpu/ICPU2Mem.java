package absimth.sim.cpu;

public interface ICPU2Mem {
	void storeByte(int addr, int p, byte data) throws Exception;
	void storeHalfWord(int addr, int p, short data) throws Exception;
	void storeWord(int addr, int data) throws Exception;
	byte getByte(int addr) throws Exception;
	String getString(int register) throws Exception;
}
