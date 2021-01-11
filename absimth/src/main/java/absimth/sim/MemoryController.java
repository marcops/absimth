package absimth.sim;

public class MemoryController {
	protected void writeBits(long address, Bits data) {
		SimulatorManager.getSim().getReport().incWrite();
		SimulatorManager.getSim().getMemory().write(address, data);
	}

	protected Bits readBits(long address) {
		SimulatorManager.getSim().getReport().incRead();
		return SimulatorManager.getSim().getMemory().read(address);
	}

	public void write(long address, long data) {
		SimulatorManager.getSim().getMemoryController().write(address, data);
	}

	public long read(long address) {
		return SimulatorManager.getSim().getMemoryController().read(address);
	}

}
