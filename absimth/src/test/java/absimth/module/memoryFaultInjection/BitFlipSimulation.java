package absimth.module.memoryFaultInjection;

import org.junit.jupiter.api.Test;

import absimth.sim.SimulatorManager;
import javafx.scene.control.TextArea;

//@SuppressWarnings("static-method")
class BitFlipSimulation {

	@Test
	void validateSimulationStep() throws Exception {
		String path = getClass().getClassLoader().getResource("bitFlipTest.yml").getPath();
		path = path.substring(0, path.length()-"bitFlipTest.yml".length());
		SimulatorManager.getSim().load(path, "bitFlipTest.yml");
		BitFlipProbabilityMFI bitFlipProbabilityMFI = new BitFlipProbabilityMFI();
		
		bitFlipProbabilityMFI.preInstruction();

	}

}
