package absimth.sim.os.model;

public interface IOSMemoryAccess {
	public int getWord(int address) throws Exception;

	public void storeWord(int addr, int data) throws Exception;

	public int getByte(int addr) throws Exception;

	public int getHalfWord(int addr) throws Exception;

	public String getString(int addr) throws Exception;

	public void storeHalfWord(int addr, short s) throws Exception;

	public void storeByte(int addr, byte b) throws Exception;
	
	public void startManageDynamicMemory();
}
