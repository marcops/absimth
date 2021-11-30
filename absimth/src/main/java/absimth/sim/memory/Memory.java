package absimth.sim.memory;

import java.util.HashMap;

import absimth.sim.memory.model.MemoryFaultModel;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.memoryController.ECCMemoryStatus;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;
import lombok.Getter;

public class Memory {
	private HashMap<Long, Bits> memory = new HashMap<>();
	private HashMap<Long, HashMap<Integer,MemoryFaultModel>> memoryFaultAddressMap = new HashMap<>();
	@Getter
	private ECCMemoryStatus memoryStatus = new ECCMemoryStatus();
	private long addressSize;
	private int wordSize;

	public Memory(long addressSize, int wordSize) {
		this.addressSize = addressSize;
		this.wordSize = wordSize;
	}

	public void addFault(long address, Integer position, MemoryFaultType type, Boolean value) throws Exception {
		validateAddressAndWordSize(address, position);
		
		HashMap<Integer,MemoryFaultModel> memoryFaultPosition = memoryFaultAddressMap.computeIfAbsent(address, k-> new HashMap<>());
		MemoryFaultModel faultTypeValue = memoryFaultPosition.computeIfAbsent(position, k-> new MemoryFaultModel(type, value));
		memoryFaultPosition.put(position, faultTypeValue);
		memoryFaultAddressMap.put(address, memoryFaultPosition);
		
		//add temporary or stuck
		Bits bits = memory.computeIfAbsent(address, k -> Bits.from(0));
		bits.set(position, value);
		memory.put(address, bits);
		
		AbsimLog.memory("Add Fault at Address=" + address + ", position=" + position + ", type=" + type.name() + " with bit setted=" + value);
	}
	
	public void write(long address, Bits data) throws Exception {
		validateAddressAndWordSize(address, data.length());
		memory.put(address, checkAndSetStuckBit(address, data));
	}

	private Bits checkAndSetStuckBit(long address, Bits data) {
		if(!memoryFaultAddressMap.containsKey(address)) return data;
		memoryFaultAddressMap.get(address).forEach((x,y) -> { 
			if(y.getFaultType().compareTo(MemoryFaultType.STUCK) == 0)
				data.set(x,y.getValue()); 
		});
		return data;
	}

	private void validateAddressAndWordSize(long address, long dataLength) throws Exception {
		if (address < 0 || address > addressSize) {
			String msg = "Address required=" + address + ", should be more than zero and less then " + addressSize;
			AbsimLog.fatal(msg);
			throw new Exception(msg);
			
		}
		if (dataLength > wordSize) {
			String msg = "data has " + dataLength + ", the word size is " + wordSize;
			AbsimLog.fatal(msg);
			throw new Exception(msg);
		}
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
