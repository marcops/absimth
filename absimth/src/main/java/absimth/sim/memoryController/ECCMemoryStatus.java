package absimth.sim.memoryController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.os.model.OSProgramModel;
import absimth.sim.utils.Bits;

public class ECCMemoryStatus {
	private ConcurrentHashMap<String, HashMap<Long,ECCMemoryFaultModel>> memoryStatus = new ConcurrentHashMap<>();

	public List<Long> getAddressWithError(){
		return memoryStatus.entrySet().stream()
			.flatMap(x->x.getValue().entrySet().stream().map(y->y.getKey()))
			.toList();
	}
	
	public void setStatus(long address, Set<Integer> position, ECCMemoryFaultType memStatus, Bits original, Bits flipped) {
		OSProgramModel osProgram = SimulatorManager.getSim().getOs().getCurrentCPU().getCurrentProgram();
		HashMap<Long,ECCMemoryFaultModel> programStatus = memoryStatus.computeIfAbsent(osProgram.getId(), k->new HashMap<Long,ECCMemoryFaultModel>());
		
		ECCMemoryFaultModel nModel = programStatus.computeIfAbsent(address, k-> ECCMemoryFaultModel.builder()
				.position(new HashSet<>())
				.faultType(memStatus)
				.originalData(original)
				.flippedData(flipped)
				.dirtAccess(false)
				.build());
		nModel.getPosition().addAll(position);
//		nModel.setDirtAccess(memoryStatus.containsKey(address));
		
		System.out.println("address=" + address+ " o="+original.toLong() + " fp="+ flipped.toLong());
	}
	
	public ECCMemoryFaultModel getFromAddress(long address) {
		OSProgramModel osProgram = SimulatorManager.getSim().getOs().getCurrentCPU().getCurrentProgram();
		HashMap<Long,ECCMemoryFaultModel> programStatus = memoryStatus.get(osProgram.getId());
		if(programStatus == null) return null;
		return programStatus.get(address);
	}
	
	public boolean containErrorInsideChip(int module, int rank, int chip) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank && rmf.hasFaulInThisChip(chip));
	}
	
	public boolean containErrorInsideRank(int module, int rank) {
		return containError((pa, rmf)->pa.getModule() == module && pa.getRank() == rank);
	}
	
	public String print(String id) {
		String fails = "\r\n[MEMORY ECC STATUS]\r\n";
		int tot = 0;
		HashMap<Long,ECCMemoryFaultModel> programStatus = memoryStatus.getOrDefault(id, new HashMap<Long,ECCMemoryFaultModel>());
		for(Map.Entry<Long, ECCMemoryFaultModel> entry : programStatus.entrySet()) {
			Long key = entry.getKey();
			ECCMemoryFaultModel value = entry.getValue();
			fails += String.format("address=0x%08x, type=%s, position=%s, dirtAccess=%b, changeValue=%b %n", key, value.getFaultType(), value.getPosition().toString(), value.getDirtAccess(), 
					value.getFixedData() == null || value.getOriginalData().toLong() != value.getFixedData().toLong());
			tot += value.getPosition().size();
			String x = value.getFixedData() == null ? "NULO" : Long.valueOf(value.getFixedData().toLong()).toString();
			System.out.println("Address" +key +" o=" + value.getOriginalData().toLong() + " f=" + x);
		}
		if(memoryStatus.entrySet().size() == 0)
			fails += "Without Errors\r\n";
		else
			fails += "Total of erros: "+ tot  + "\r\n";
		return fails;
	}
	
	public boolean containError(IComparePhysicalAddress compare) {
		for(Map.Entry<String, HashMap<Long,ECCMemoryFaultModel>> entryProgram : memoryStatus.entrySet()) {
			for(Map.Entry<Long, ECCMemoryFaultModel> entry : entryProgram.getValue().entrySet()) {
				Long key = entry.getKey();
				PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress(key);
				ECCMemoryFaultModel value = entry.getValue();
				if(compare.compare(pa, value)) return true;	
			}
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
	
	public String printAll() {
		String fails = "";
		for(Map.Entry<String, HashMap<Long,ECCMemoryFaultModel>> entryProgram : memoryStatus.entrySet()) {
			for(Map.Entry<Long, ECCMemoryFaultModel> entry : entryProgram.getValue().entrySet()) {
				
					Long key = entry.getKey();
					ECCMemoryFaultModel value = entry.getValue();
					fails += String.format("address=0x%08x, type=%s, position=%s, dirtAccess=%b, changeValue=%b %n", key, value.getFaultType(), value.getPosition().toString(), value.getDirtAccess(), 
							value.getFixedData() == null || value.getOriginalData().toLong() != value.getFixedData().toLong());
					
				}
		}
			
		return fails;
	}

	public String printSmall(String id) {
		HashMap<Long,ECCMemoryFaultModel> programStatus = memoryStatus.getOrDefault(id, new HashMap<Long,ECCMemoryFaultModel>());
		
		boolean dirtAccess = false;
		boolean changeValue = false;
		int tot = 0;
		for(Map.Entry<Long, ECCMemoryFaultModel> entry : programStatus.entrySet()) {
			ECCMemoryFaultModel value = entry.getValue();
			
			boolean cv = value.getFixedData() == null || value.getOriginalData().toLong() != value.getFixedData().toLong();
			if(cv && value.getDirtAccess()) changeValue = true;
			if(value.getDirtAccess()) dirtAccess = true;
			tot += value.getPosition().size();
			
		}
		
		if(memoryStatus.entrySet().size() == 0)
			return ";"+ tot;
		else {
			if(changeValue) return "-I;"+ tot;
			if(dirtAccess) return "-F;" + tot;
		}
			
		return ";"+tot;
	}

	public String printSmallStatus(String id) {
		HashMap<Long,ECCMemoryFaultModel> programStatus = memoryStatus.getOrDefault(id, new HashMap<Long,ECCMemoryFaultModel>());
		
		boolean dirtAccess = false;
		boolean changeValue = false;
		for(Map.Entry<Long, ECCMemoryFaultModel> entry : programStatus.entrySet()) {
			ECCMemoryFaultModel value = entry.getValue();
			
			boolean cv = value.getFixedData() == null || value.getOriginalData().toLong() != value.getFixedData().toLong();
			if(cv && value.getDirtAccess()) changeValue = true;
			if(value.getDirtAccess()) dirtAccess = true;
			
		}
		
		if(changeValue) return "-I";
		if(dirtAccess) return "-F";
		return "";
			
	}

	public Integer printSmallTotal(String id) {
		HashMap<Long,ECCMemoryFaultModel> programStatus = memoryStatus.getOrDefault(id, new HashMap<Long,ECCMemoryFaultModel>());
		
		int tot = 0;
		for(Map.Entry<Long, ECCMemoryFaultModel> entry : programStatus.entrySet()) {
			ECCMemoryFaultModel value = entry.getValue();
			
			tot += value.getPosition().size();
			
		}
			
		return tot;
	}
	
	
}
