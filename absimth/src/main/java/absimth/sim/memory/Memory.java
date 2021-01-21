package absimth.sim.memory;

import java.util.HashMap;
import java.util.Map;

import absimth.sim.memory.model.Bits;
import absimth.sim.memory.model.FaultAddressModel;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.memory.model.ReportMemoryFail;
import absimth.sim.utils.AbsimLog;

public class Memory {
	private HashMap<Long, Bits> memory = new HashMap<>();
	private HashMap<Long, ReportMemoryFail> memoryStatus = new HashMap<>();
	private long addressSize;
	private int wordSize;

	public Memory(long addressSize, int wordSize) {
		this.addressSize = addressSize;
		this.wordSize = wordSize;
	}

	public void setStatus(long address, FaultAddressModel model, MemoryFaultType memStatus) {
		memoryStatus.put(address, ReportMemoryFail.builder()
				.faultAddress(model)
				.faultType(memStatus)
				.build());
	}
	
	public ReportMemoryFail getStatus(long address) {
		return memoryStatus.get(address);
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
		if(address == 1) {
			System.out.println("a");
		}
		if(!memory.containsKey(address)) return Bits.from(0);
		return memory.get(address);
	}

	public String printFails() {
		String fails = "------ MEMORY FAILS ------\r\n";
		for(Map.Entry<Long, ReportMemoryFail> entry : memoryStatus.entrySet()) {
			Long key = entry.getKey();
			ReportMemoryFail value = entry.getValue();
			fails += String.format("address=0x%06x, position=%d, type=%s\r\n", key, value.getFaultAddress().getPosition(), value.getFaultType());
			
		}
		return fails;
	}

}
