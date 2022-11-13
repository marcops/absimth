package absimth.module.memoryFaultInjection;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.MemoryFaultProbabilityModel;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;

public class BitFlipProbabilityMFINew implements IFaultInjection {
	public class BF{
		public Long address;
		public Integer bitLocation;
		
		public BF(Long address, Integer bitLocation) {
			this.address = address;
			this.bitLocation = bitLocation;
		}
		
	}
	
	private Random random;
	private Random randomTime;
	private Queue<BF> queue;
	// junit only
//	@Getter
//	@Setter
//	private Long currentAddress;
//	@Getter
//	@Setter
//	private Integer currentChip;
//	private Long maxAddress;
	private boolean firstTime = false;
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
	public BitFlipProbabilityMFINew() {
		loadConfig();
		queue = new LinkedList<BF>();
		firstTime = true;
		random = new Random();
		random.setSeed(memoryFaultProbabilityModel.getSeed());
		
		randomTime = new Random();
		randomTime.setSeed(memoryFaultProbabilityModel.getSeed()*-1);
	}

	@Override
	public void preInstruction() {
		if(firstTime) {
			Long currentAddress = -1L;
			Long maxAddress = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress();
			
			for(int i=0;i<2500;i++) {
				currentAddress = discoverErrorAddress(currentAddress, maxAddress);
				if(currentAddress != -1L) {
					int bitPosition = discoverBitPosition();
					queue.add(new BF(currentAddress, bitPosition));
				}
				
			}
			firstTime = false;	
		}
		if (!probabilityHappen(memoryFaultProbabilityModel.getProbabilityRate())) return;
		generateBitflip();
	}


	private void generateBitflip()  {
		try {
			BF bf = queue.remove();
			Bits b = SimulatorManager.getSim().getMemory().read(bf.address);
			Bits original = Bits.from(b);
			
			b.set(bf.bitLocation, !b.get(bf.bitLocation));
			
			SimulatorManager.getSim().getMemory().write(bf.address, b);
			
			SimulatorManager.getSim().getMemoryController().getMemoryStatus().setStatus(bf.address, Set.of(bf.bitLocation), ECCMemoryFaultType.INVERTED, original , b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int discoverBitPosition() {
		return (int)randomWithRange(0L, 64L);
	}

	private Long discoverErrorAddress(Long currentAddress, Long maxAddress) {
		if(currentAddress == -1L || probabilityHappen(memoryFaultProbabilityModel.getProbabilityRangeOut())) 
			return randomWithRange(0L, maxAddress);
		//near
		return randomWithRangeMoreThanZero(currentAddress, maxAddress);
	}


	private Long randomWithRangeMoreThanZero(Long local, Long maxAddress) {
		Long dist = randomWithRange(0L, memoryFaultProbabilityModel.getNearErrorRange().longValue());
		if (random.nextBoolean()) {
			if(dist + local > maxAddress) return -1L;
			return dist + local;
		}
		if(local - dist <0) return -1L;
		return local - dist;
	}



	private long randomWithRange(Long min, Long max) {
		return random.nextLong(max - min) + min;
	}

	private Boolean probabilityHappen(Double probabilityRate) {
		Double value = random.nextDouble() * 100;
		return value.compareTo(probabilityRate) <= 0 ? true : false;
	}

	@Override
	public void posInstruction() {
	}

	@Override
	public boolean onRead(long address) throws Exception {
		return false;
	}

	@Override
	public Bits onWrite(long address, Bits data) throws Exception {
		return data;
	}

}
