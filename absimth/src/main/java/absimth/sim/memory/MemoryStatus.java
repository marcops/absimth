package absimth.sim.memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.memory.model.MemoryFaultModel;
import absimth.sim.memory.model.MemoryFaultType;

public class MemoryStatus {
	private HashMap<Long, MemoryFaultModel> memoryStatus = new HashMap<>();

	public void setStatus(long address, Set<Integer> position, MemoryFaultType memStatus) {
		MemoryFaultModel nModel = memoryStatus.computeIfAbsent(address, k-> MemoryFaultModel.builder()
				.position(new HashSet<>())
				.faultType(memStatus)
				.build());
		nModel.getPosition().addAll(position);
	}
	
	public MemoryFaultModel getFromAddress(long address) {
		return memoryStatus.get(address);
	}
	
	public boolean containErrorInsideChip(int module, int rank, int chip) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.hasFaulInThisChip(chip));
	}
	
	public boolean containErrorInsideRank(int module, int rank) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank);
	}
	
	public String print() {
		String fails = "------ MEMORY FAILS ------\r\n";
		for(Map.Entry<Long, MemoryFaultModel> entry : memoryStatus.entrySet()) {
			Long key = entry.getKey();
			MemoryFaultModel value = entry.getValue();
			fails += String.format("address=0x%08x, position=%s, type=%s%n", key, value.getPosition().toString(), value.getFaultType());
			
		}
		return fails;
	}
	
	public boolean containError(IComparePhysicalAddress compare) {
		for(Map.Entry<Long, MemoryFaultModel> entry : memoryStatus.entrySet()) {
			Long key = entry.getKey();
			PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress(key);
			MemoryFaultModel value = entry.getValue();
			if(compare.compare(pa, value)) return true;	
		}
		return false;
	}
	
	protected interface IComparePhysicalAddress {
		boolean compare(PhysicalAddress pa, MemoryFaultModel rmf);
	}

	public boolean containErrorInsideBankGroup(Integer module, Integer rank, Integer chipPos, int bankGroupPos) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.hasFaulInThisChip(chipPos) && pa.getBankGroup() == bankGroupPos);
	}
	
	public boolean containErrorInsideBank(Integer module, Integer rank, Integer chipPos, int bankGroupPos, int bank) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.hasFaulInThisChip(chipPos)
				&& pa.getBankGroup() == bankGroupPos && pa.getBank() == bank);
	}
	
	
}
