package absimth.sim.memoryController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.memoryController.model.ECCMemoryFaultType;

public class ECCMemoryStatus {
	private HashMap<Long, ECCMemoryFaultModel> memoryStatus = new HashMap<>();

	public void setStatus(long address, Set<Integer> position, ECCMemoryFaultType memStatus) {
		ECCMemoryFaultModel nModel = memoryStatus.computeIfAbsent(address, k-> ECCMemoryFaultModel.builder()
				.position(new HashSet<>())
				.faultType(memStatus)
				.build());
		nModel.getPosition().addAll(position);
	}
	
	public ECCMemoryFaultModel getFromAddress(long address) {
		return memoryStatus.get(address);
	}
	
	public boolean containErrorInsideChip(int module, int rank, int chip) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.hasFaulInThisChip(chip));
	}
	
	public boolean containErrorInsideRank(int module, int rank) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank);
	}
	
	public String print() {
		String fails = "\r\n[MEMORY ECC STATUS]\r\n";
		for(Map.Entry<Long, ECCMemoryFaultModel> entry : memoryStatus.entrySet()) {
			Long key = entry.getKey();
			ECCMemoryFaultModel value = entry.getValue();
			fails += String.format("address=0x%08x, type=%s, position=%s%n", key, value.getFaultType(), value.getPosition().toString());
			
		}
		return fails;
	}
	
	public boolean containError(IComparePhysicalAddress compare) {
		for(Map.Entry<Long, ECCMemoryFaultModel> entry : memoryStatus.entrySet()) {
			Long key = entry.getKey();
			PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress(key);
			ECCMemoryFaultModel value = entry.getValue();
			if(compare.compare(pa, value)) return true;	
		}
		return false;
	}
	
	protected interface IComparePhysicalAddress {
		boolean compare(PhysicalAddress pa, ECCMemoryFaultModel rmf);
	}

	public boolean containErrorInsideBankGroup(Integer module, Integer rank, Integer chipPos, int bankGroupPos) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.hasFaulInThisChip(chipPos) && pa.getBankGroup() == bankGroupPos);
	}
	
	public boolean containErrorInsideBank(Integer module, Integer rank, Integer chipPos, int bankGroupPos, int bank) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.hasFaulInThisChip(chipPos)
				&& pa.getBankGroup() == bankGroupPos && pa.getBank() == bank);
	}
	
	
}
