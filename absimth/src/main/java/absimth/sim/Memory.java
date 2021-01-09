package absimth.sim;

import java.util.HashMap;

public class Memory {
	private HashMap<Long, Bits> memory = new HashMap<>();
	private long size;

	public Memory(long size) {
		this.size = size;
	}

	public void write(long address, Bits data) {
		if (address < 0 || address > size)
			throw new IllegalAccessError("Address required=" + address + ", should be more than zero and less then " + size);
		memory.put(address, data);
	}

	public Bits read(long address) {
		if (address < 0 || address > size)
			throw new IllegalAccessError("Address required=" + address + ", should be more than zero and less then " + size);
		if(!memory.containsKey(address)) return Bits.from(0);
		return memory.get(address);
	}
}
