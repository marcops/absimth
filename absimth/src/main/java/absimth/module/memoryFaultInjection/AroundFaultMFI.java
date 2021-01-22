package absimth.module.memoryFaultInjection;

import java.util.Random;

import absimth.module.memoryController.util.ecc.CRC8;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memory.model.Bits;
import absimth.sim.memory.model.FaultAddressModel;
import absimth.sim.memory.model.MemoryFaultType;
import absimth.sim.utils.AbsimLog;

public class AroundFaultMFI implements IFaultInjection {

	private FaultAddressModel faultModel;
	private int count = 0;
	
	public FaultAddressModel getFault() {
		Integer maxAddress = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress().intValue();
		Integer wordSize = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getWorldSize();
		if (faultModel == null) {
			faultModel = FaultAddressModel.builder()
					.address(new Random().nextInt(maxAddress))
					.position(new Random().nextInt(wordSize))
					.build();
			return faultModel;
		}

		faultModel = FaultAddressModel.builder()
				.address(calcDistribution(maxAddress, 100, (int) faultModel.getAddress()))
				.position(calcDistribution(wordSize, 4, faultModel.getPosition()))
				.build();
		return faultModel;
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
		SimulatorManager.getSim().getMemory().write(1, CRC8.encode(Bits.from(6)));
		count++;
//		TODO COUNT here
		if (count == 30) 
			createError();
	}

	private void createError() throws Exception {
		count = 0;
		FaultAddressModel model = getFault();
		Bits b = SimulatorManager.getSim().getMemory().read(model.getAddress());
		b.flip(model.getPosition());
		SimulatorManager.getSim().getMemory().write(model.getAddress(), b);
		
		SimulatorManager.getSim().getMemory().setStatus(model.getAddress(), model, MemoryFaultType.INVERTED);
		AbsimLog.memory(String.format("I - inverted bit at %d - 0x%08x - 0x%08x", model.getPosition(), model.getAddress(), b.toInt()));
		
		//
		SimulatorManager.getSim().getMemory().write(1, CRC8.encode(Bits.from(model.getAddress())));
		System.err.println(model.getAddress());
		AbsimLog.memory(String.format("WARNING FAIL AT 0x%08x", model.getAddress()));
	}

}
