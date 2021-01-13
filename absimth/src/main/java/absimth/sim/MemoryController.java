package absimth.sim;

public class MemoryController {
	public static void writeBits(long address, Bits data) {
		SimulatorManager.getSim().getReport().incWrite();
		SimulatorManager.getSim().getMemory().write(address, data);
	}

	public static Bits readBits(long address) {
		SimulatorManager.getSim().getReport().incRead();
		return SimulatorManager.getSim().getMemory().read(address);
	}

	public static void write(long address, long data) {
		SimulatorManager.getSim().getMemoryController().write(address, data);
	}

	public static long read(long address) {
		return SimulatorManager.getSim().getMemoryController().read(address);
	}

}
