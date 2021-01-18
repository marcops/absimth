package absimth.sim.memory.faultInjection;

import java.util.Random;

import absimth.sim.SimulatorManager;
import absimth.sim.memory.faultInjection.model.FaultModel;

public class AroundFaultMode implements IFaultMode {

	private FaultModel faultModel;
	
	@Override
	public FaultModel getFault() {
		Integer maxAddress = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress().intValue();
		Integer wordSize = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getWorldSize();
		if (faultModel == null) {
			faultModel = FaultModel.builder()
				.address(new Random().nextInt(maxAddress))
				.position(new Random().nextInt(wordSize))
				.build();
			return faultModel;
		}
		//TODO DINAMICO a distribuição?
		faultModel.setAddress(calcDistribution(maxAddress, 100, (int) faultModel.getAddress()));
		faultModel.setPosition(calcDistribution(wordSize, 3, faultModel.getPosition()));
		
		return faultModel;
	}

	private static int calcDistribution(int maxAddress, int dist, int lastValue) {
		Integer size = maxAddress/(dist*100);
		Integer nPos = new Random().nextInt(size*2);
		return (lastValue-size) + nPos;
	}

}
