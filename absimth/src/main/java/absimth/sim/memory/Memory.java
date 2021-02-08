package absimth.sim.memory;

import java.util.HashMap;

import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;
import lombok.Getter;

public class Memory {
	private HashMap<Long, Bits> memory = new HashMap<>();
	@Getter
	private MemoryStatus memoryStatus = new MemoryStatus();
	private long addressSize;
	private int wordSize;

	public Memory(long addressSize, int wordSize) {
		this.addressSize = addressSize;
		this.wordSize = wordSize;
	}

	public void write(long address, Bits data) throws Exception {
		if (address < 0 || address > addressSize) {
			String msg = "Address required=" + address + ", should be more than zero and less then " + addressSize;
			AbsimLog.fatal(msg);
			throw new Exception(msg);
			
		}
		if (data.length() > wordSize) {
			String msg = "data has " + data.length() + ", the word size is " + wordSize;
			AbsimLog.fatal(msg);
			throw new Exception(msg);
		}
		memory.put(address, data);
	}

	public Bits read(long address) throws Exception {
		if (address < 0 || address > addressSize) {
			String msg = "Address required=" + address + ", should be more than zero and less then " + addressSize;
			AbsimLog.fatal(msg);
			throw new Exception(msg);
		}
		if(!memory.containsKey(address)) return Bits.from(0);
		return memory.get(address);
	}


}
