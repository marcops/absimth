package absimth.module.memoryFaultInjection;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.MemoryFaultProbabilityModel;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.os.model.OSProgramModel;

public class BitFlipProbabilityMFI implements IFaultInjection {
	private Random random;
	private Long previousAddress;
	private MemoryFaultProbabilityModel memoryFaultProbabilityModel;
	
	public BitFlipProbabilityMFI() {
		previousAddress = -1L;
		random = new Random();
		
		//TODO get loaded data
		memoryFaultProbabilityModel = MemoryFaultProbabilityModel.builder()
				.probabilityRate(0.002D)
				.initialAddress(-1L)
				.errorOnlyInUsedMemory(true)
				.nearErrorRange(100)
				.radiusIntensity(4)
				.angle(0)
				.declineIntensity(100D)
				.declineRadius(100D)
				.build();		
	}
	
	@Override
	public void preInstruction() {
		if(!haveToBitflip(memoryFaultProbabilityModel.getProbabilityRate())) return;
		Long address = discoverErrorAddress();
		generateError(address);
	}

	private void generateError(Long address) {
		PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService()
				.getPhysicalAddress(address);
	}

	private Long discoverErrorAddress() {
		if(memoryFaultProbabilityModel.getInitialAddress() !=-1L) return memoryFaultProbabilityModel.getInitialAddress();
		if(previousAddress == -1L) {
			Long randomAddress = randomWithRange(0L, 
					memoryFaultProbabilityModel.getErrorOnlyInUsedMemory() ? getMaxAddressUsed() :
					SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress());
			return randomAddress;
		}
		return discoverErrorAddressCloseTo(previousAddress);
	}

	private Long discoverErrorAddressCloseTo(Long previous) {
		PhysicalAddress previousPA = SimulatorManager.getSim().getPhysicalAddressService()
				.getPhysicalAddress(previous);
		for (int i=0;i<5;i++) {
			int cell = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup().getBank().getCell().getHeight();
			
			PhysicalAddress closePA = SimulatorManager.getSim().getPhysicalAddressService()
					.getPhysicalAddressReverse(previousPA.getModule(), previousPA.getRank(), previousPA.getBankGroup(), previousPA.getBank(), 
							randomWithRangeMoreThanZero((int)previousPA.getRow()),
							randomWithRangeMoreThanZero((int)previousPA.getColumn()),
							randomWithRangeMoreThanZero(cell));
			if(validAddress(closePA.getPAddress())) return closePA.getPAddress();
		}
		System.err.println("Discarding the attempt to find the new place to put some error");
		return 0L;
	}

	private int randomWithRangeMoreThanZero(int local) {
		int newLocal = (int)randomWithRange(0L, memoryFaultProbabilityModel.getNearErrorRange().longValue());
		if(random.nextBoolean()) return newLocal+local; 
		local -= newLocal;
		if(local <0) return 0;
		return local;
	}

	private Boolean validAddress(Long randomAddress) {
		Long maxMemory = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress();
		
		Long maxMemoryUsed = getMaxAddressUsed();
		
		Long min = randomAddress - memoryFaultProbabilityModel.getNearErrorRange();
		if (min < 0) return false;
		
		Long max = randomAddress + memoryFaultProbabilityModel.getNearErrorRange();
		if(max > maxMemory) return false;		
		if(memoryFaultProbabilityModel.getErrorOnlyInUsedMemory() && max > maxMemoryUsed) return false;
		return true;
	}

	private Long getMaxAddressUsed() {
		HashMap<String, OSProgramModel> programs = SimulatorManager.getSim().getOsPrograms();
		//
		List<OSProgramModel> values = programs.values().stream().collect(Collectors.toList());
		OSProgramModel lastEntry = values.get(programs.size()-1);
		Long maxMemoryUsed = Long.valueOf(lastEntry.getInitialAddress()+lastEntry.getTotalOfMemoryUsed());
		return maxMemoryUsed;
	}

	private long randomWithRange(Long min, Long max) {
		return random.nextLong(max - min) + min;
	}

	private Boolean haveToBitflip(Double probabilityRate) {
		Double value = random.nextDouble();
		value *= 100;
		if (probabilityRate.compareTo(value) > 0)
			System.out.println(value);
		// probabilityRate
		return true;
	}
	
	
	@Override
	public void posInstruction() { }

	@Override
	public void onRead() throws Exception { }

	@Override
	public void onWrite() throws Exception { }
}
