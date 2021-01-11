package absimth.sim;

import java.util.HashMap;

public class Memory {
	private HashMap<Long, Bits> memory = new HashMap<>();
	private long addressSize;
	private int wordSize;

	public Memory(long addressSize, int wordSize) {
		this.addressSize = addressSize;
		this.wordSize = wordSize;
	}

	public void write(long address, Bits data) {
		if (address < 0 || address > addressSize)
			throw new IllegalAccessError("Address required=" + address + ", should be more than zero and less then " + addressSize);
		if (data.length() > wordSize)
			throw new IllegalAccessError("data has " + data.length() + ", the word size is " + wordSize);
		memory.put(address, data);
	}

	public Bits read(long address) {
		if (address < 0 || address > addressSize)
			throw new IllegalAccessError("Address required=" + address + ", should be more than zero and less then " + addressSize);
		if(!memory.containsKey(address)) return Bits.from(0);
		return memory.get(address);
	}
}
