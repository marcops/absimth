package absimth.module.memoryFaultInjection;

import java.util.Random;
import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.MemoryFaultProbabilityModel;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;
import lombok.Getter;
import lombok.Setter;

public class BitFlipProbabilityMFI implements IFaultInjection {
	private Random random;
	// junit only
	@Getter
	@Setter
	private Long currentAddress;
	@Getter
	@Setter
	private Integer currentChip;

	private MemoryFaultProbabilityModel memoryFaultProbabilityModel;
	
	//trocar o RANDOM para poder repetir a "randomizacao"
	
	private void loadConfig() {
		try {
			String config = SimulatorManager.getSim().getAbsimthConfiguration().getModules().getMemoryFaultInjection().getConfig();
			String configs[] = config.split(";");
			// TODO get loaded data
			memoryFaultProbabilityModel = MemoryFaultProbabilityModel.builder()
					.probabilityRate(Double.valueOf(configs[0]))
					.initialAddress(Long.valueOf(configs[1]))
					.errorOnlyInUsedMemory(Boolean.valueOf(configs[2]))
					.nearErrorRange(Integer.valueOf(configs[3]))
					.maxNumberOfBitFlip(Integer.valueOf(configs[4]))
					.bitFlipRange(Integer.valueOf(configs[5]))
					.probabilityRangeOut(Double.valueOf(configs[6]))
					.seed(Integer.valueOf(configs[7]))
					.build();
		}catch (Exception e) {
			AbsimLog.fatal(e.toString());
		}
		
		
	}
	public BitFlipProbabilityMFI() {
		loadConfig();
		currentAddress = -1L;
		if(memoryFaultProbabilityModel.getSeed()!=null && memoryFaultProbabilityModel.getSeed()!=-1)
			random = new Random(memoryFaultProbabilityModel.getSeed());
		else
			random = new Random();
	}

	@Override
	public void preInstruction() {
		if (!probabilityHappen(memoryFaultProbabilityModel.getProbabilityRate()))
			return;
		currentAddress = discoverErrorAddress();
		generateError();
	}

	private void generateError() {
		for (int i = 0; i < memoryFaultProbabilityModel.getMaxNumberOfBitFlip(); i++) {
			if(random.nextBoolean()) continue;
			generateBitflip();
		}
	}

	private void generateBitflip()  {
		try {
			Long addressToAddBitflip = discoverErrorAddressCloseTo(currentAddress, memoryFaultProbabilityModel.getBitFlipRange().longValue());
			int bitPosition = discoverBitPosition();
			Bits b = SimulatorManager.getSim().getMemory().read(addressToAddBitflip);
			b.set(bitPosition, random.nextBoolean());
			setErrorOnMemory(addressToAddBitflip.intValue(), b, bitPosition);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int discoverBitPosition() {
		long initValue = currentChip * Bits.BYTE_SIZE;
		return (int)randomWithRange(initValue, initValue + Bits.BYTE_SIZE);
	}

	private static void setErrorOnMemory(final int addressWithProblem, Bits word, Integer positionFlipped) throws Exception {
//		EccType type = SimulatorManager.getSim().getMemoryController().getCurrentEccType(addressWithProblem);
//		Bits number = type.getEncode().encode(Bits.from(5));
//		number.flip(POSITION_FLIP);
		//System.out.println("add="+addressWithProblem + ", word="+word.toInt() +", pos="+ positionFlipped);
		SimulatorManager.getSim().getMemory().write(addressWithProblem, word);
		SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(addressWithProblem, Set.of(positionFlipped), ECCMemoryFaultType.INVERTED);
	}

	private Long discoverErrorAddress() {
		if (memoryFaultProbabilityModel.getInitialAddress() != -1L)
			return memoryFaultProbabilityModel.getInitialAddress();

		Long previousAddress = currentAddress;
		if (probabilityHappen(memoryFaultProbabilityModel.getProbabilityRangeOut())) currentAddress = -1L;
		else previousAddress = -1L;
		
		if (currentAddress == -1L) {
			currentChip = (int)randomWithRange(0L, SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getAmount().longValue());
			
			Long randomAddress = randomWithRange(0L, memoryFaultProbabilityModel.getErrorOnlyInUsedMemory() ? getMaxAddressUsed()
							: SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress() / 4);
			
			if(previousAddress != -1L) currentAddress = previousAddress;
			return randomAddress;
		}
		return discoverErrorAddressCloseTo(currentAddress,
				memoryFaultProbabilityModel.getNearErrorRange().longValue());
	}

	private Long discoverErrorAddressCloseTo(Long previous, Long range) {
		PhysicalAddress previousPA = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress(previous);
		for (int i = 0; i < 5; i++) {
			Integer cell = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup().getBank().getCell().getHeight()/Bits.BYTE_SIZE;
			PhysicalAddress closePA = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddressReverse(
					previousPA.getModule(), previousPA.getRank(), previousPA.getBankGroup(), previousPA.getBank(),
					randomWithRangeMoreThanZero((int) previousPA.getRow()),
					randomWithRangeMoreThanZero((int) previousPA.getColumn()), 
					(int)randomWithRange(0L, cell.longValue()));
			if (validAddress(closePA.getPAddress(), range))
				return closePA.getPAddress();
		}
		System.err.println("Discarding the attempt to find the new place to put some error");
		return -1L;
	}

	private int randomWithRangeMoreThanZero(int local) {
		int newLocal = (int) randomWithRange(0L, memoryFaultProbabilityModel.getNearErrorRange().longValue());
		if (random.nextBoolean())
			return newLocal + local;
		local -= newLocal;
		if (local < 0)
			return 0;
		return local;
	}

	private Boolean validAddress(Long randomAddress, Long range) {
		Long maxMemory = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory()
				.getTotalOfAddress();

		Long maxMemoryUsed = getMaxAddressUsed();

		Long min = randomAddress - range;
		if (min < 0)
			return false;

		Long max = randomAddress + range;
		if (max > maxMemory)
			return false;
		if (memoryFaultProbabilityModel.getErrorOnlyInUsedMemory() && max > maxMemoryUsed)
			return false;
		return true;
	}

	private Long getMaxAddressUsed() {
		//(initialAddress+totalOfMemory)/4
		Integer maxMemoryUsed = SimulatorManager.getSim()
				.getOsPrograms().values().stream()
				.mapToInt(v->v.getInitialAddress() + v.getTotalOfMemory())
				.max().orElse(4);
		if (maxMemoryUsed < 4) maxMemoryUsed = 4;
		return (long) (maxMemoryUsed/4);
	}

	private long randomWithRange(Long min, Long max) {
		return random.nextLong(max - min) + min;
	}

	private Boolean probabilityHappen(Double probabilityRate) {
		Double value = random.nextDouble();
		value *= 100;
		if (value.compareTo(probabilityRate) <= 0)
			return true;
		return false;
	}

	@Override
	public void posInstruction() {
	}

	@Override
	public boolean onRead(long address) throws Exception {
		return false;
	}

	@Override
	public boolean onWrite(long address, Bits data) throws Exception {
		return false;
	}

}
