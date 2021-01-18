package absimth.sim.memory.faultInjection;

import java.util.HashMap;

import absimth.sim.memory.faultInjection.model.Bits;
import absimth.sim.memory.faultInjection.model.MemoryStatus;
import absimth.sim.utils.AbsimLog;

public class Memory {
	private HashMap<Long, Bits> memory = new HashMap<>();
	private HashMap<Long, MemoryStatus> memoryStatus = new HashMap<>();
	private long addressSize;
	private int wordSize;

	public Memory(long addressSize, int wordSize) {
		this.addressSize = addressSize;
		this.wordSize = wordSize;
	}

	public void setStatus(long address, MemoryStatus memStatus) {
		memoryStatus.put(address, memStatus);
	}
	
	public MemoryStatus getStatus(long address) {
		return memoryStatus.get(address);
	}
	
	public void write(long address, Bits data) {
		if (address < 0 || address > addressSize) {
			String msg = "Address required=" + address + ", should be more than zero and less then " + addressSize;
			AbsimLog.fatal(msg);
			throw new IllegalAccessError(msg);
			
		}
		if (data.length() > wordSize) {
			String msg = "data has " + data.length() + ", the word size is " + wordSize;
			AbsimLog.fatal(msg);
			throw new IllegalAccessError(msg);
		}
		memory.put(address, data);
	}

	public Bits read(long address) {
		if (address < 0 || address > addressSize) {
			String msg = "Address required=" + address + ", should be more than zero and less then " + addressSize;
			AbsimLog.fatal(msg);
			throw new IllegalAccessError(msg);
		}
		if(!memory.containsKey(address)) return Bits.from(0);
		return memory.get(address);
	}
}
