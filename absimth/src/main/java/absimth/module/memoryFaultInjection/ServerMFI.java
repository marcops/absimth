package absimth.module.memoryFaultInjection;

import java.util.Random;
import java.util.Set;

import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memory.model.MemoryFaultModel;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.Bits;

public class ServerMFI implements IFaultInjection {

	private MemoryFaultModel memoryFaultModel;
	private int count = 0;
	
	public MemoryFaultModel getFault() {
		Integer maxAddress = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress().intValue();
		Integer wordSize = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getWorldSize();
		if (memoryFaultModel == null) {
			memoryFaultModel = MemoryFaultModel.builder()
					.address(new Random().nextInt(maxAddress))
					.position(Set.of(new Random().nextInt(wordSize)))
					.build();
			return memoryFaultModel;
		}

		memoryFaultModel = MemoryFaultModel.builder()
				.address(calcDistribution(maxAddress, 100, (int) memoryFaultModel.getAddress()))
				.position(Set.of(calcDistribution(wordSize, 4, memoryFaultModel.getPosition().stream().findAny().get())))
				.build();
		return memoryFaultModel;
	}

	private static int calcDistribution(int maxAddress, int distance, int lastValue) {
		Integer nPos = new Random().nextInt(distance);
		Integer nValue = new Random().nextBoolean() ? (lastValue + nPos) : (lastValue - nPos);
		if (nValue > maxAddress) return maxAddress;
		if (nValue < 0) return 0;
		return nValue;
	}
	
	@Override
	public void haveToCreateError() throws Exception {
		count++;
//		TODO COUNT here
		if (count == 30) 
			createError();
	}

	private void createError() throws Exception {
		count = 0;
		MemoryFaultModel model = getFault();
		Bits b = SimulatorManager.getSim().getMemory().read(model.getAddress());
		model.getPosition().stream().forEach(x->b.flip(x));
		SimulatorManager.getSim().getMemory().write(model.getAddress(), b);
		
		SimulatorManager.getSim().getMemory().getMemoryStatus().setStatus(model.getAddress(), model.getPosition(), MemoryFaultType.INVERTED);
		AbsimLog.memory(String.format("I - inverted bit at %s - 0x%08x - 0x%08x", model.getPosition().toString(), model.getAddress(), b.toInt()));
		
		//
		SimulatorManager.getSim().getMemory().write(1, EccType.CRC8.getEncode().encode(Bits.from(model.getAddress())));
		System.err.println(model.getAddress());
		AbsimLog.memory(String.format("WARNING FAIL AT 0x%08x", model.getAddress()));
	}

}
