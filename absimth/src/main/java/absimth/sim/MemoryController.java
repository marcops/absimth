package absimth.sim;

public class MemoryController {
	protected static void writeBits(long address, Bits data) {
		SimulatorManager.getSim().getReport().incWrite();
		System.out.println("w="+address + ", b=" + data.toInt());
		SimulatorManager.getSim().getMemory().write(address, data);
	}

	protected static Bits readBits(long address) {
		SimulatorManager.getSim().getReport().incRead();
		Bits b = SimulatorManager.getSim().getMemory().read(address);
		System.out.println("r="+address + ", b=" + b.toInt());
		return b;
	}

	public void writeOS(long address, int memoryReference , long data) {
		int ref = memoryReference * SimulatorManager.STACK_POINTER_PROGRAM_SIZE; 
		SimulatorManager.getSim().getMemoryController().write(ref + address, data);
	}

	public long readOS(long address, int memoryReference) {
		int ref = memoryReference * SimulatorManager.STACK_POINTER_PROGRAM_SIZE;
		return SimulatorManager.getSim().getMemoryController().read(ref + address);
	}

}
