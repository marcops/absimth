package absimth.sim.memory;

import java.util.HashMap;
import java.util.Map;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.memory.model.FaultAddressModel;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.memory.model.ReportMemoryFail;

public class MemoryStatus {
	private HashMap<Long, ReportMemoryFail> memoryStatus = new HashMap<>();

	public void setStatus(long address, FaultAddressModel model, MemoryFaultType memStatus) {
		memoryStatus.put(address, ReportMemoryFail.builder()
				.faultAddress(model)
				.faultType(memStatus)
				.build());
	}
	
	public ReportMemoryFail getFromAddress(long address) {
		return memoryStatus.get(address);
	}

	
	
	public boolean containErrorInsideChip(int module, int rank, int chip) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.getFaultAddress().getChip() == chip);
	}
	
	public boolean containErrorInsideRank(int module, int rank) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank);
	}
	
	public String print() {
		String fails = "------ MEMORY FAILS ------\r\n";
		for(Map.Entry<Long, ReportMemoryFail> entry : memoryStatus.entrySet()) {
			Long key = entry.getKey();
			ReportMemoryFail value = entry.getValue();
			fails += String.format("address=0x%06x, position=%d, type=%s%n", key, value.getFaultAddress().getPosition(), value.getFaultType());
			
		}
		return fails;
	}
	
	public boolean containError(IComparePhysicalAddress compare) {
		for(Map.Entry<Long, ReportMemoryFail> entry : memoryStatus.entrySet()) {
			Long key = entry.getKey();
			PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress(key);
			ReportMemoryFail value = entry.getValue();
			if(compare.compare(pa, value)) return true;	
		}
		return false;
	}
	
	protected interface IComparePhysicalAddress {
		boolean compare(PhysicalAddress pa, ReportMemoryFail rmf);
	}
	
	
}
