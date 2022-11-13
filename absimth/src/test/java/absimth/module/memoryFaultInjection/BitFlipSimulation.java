package absimth.module.memoryFaultInjection;

import org.junit.jupiter.api.Test;

import absimth.sim.SimulatorManager;

//@SuppressWarnings("static-method")
class BitFlipSimulation {

	@Test
	void validateSimulationFistTime() throws Exception {
		BitFlipProbabilityMFIOld bitFlipProbabilityMFI = initializeBitFlip();
		
		bitFlipProbabilityMFI.preInstruction();

	}
	
//	@Test
//	void validateSimulationSecondTime() throws Exception {
//		BitFlipProbabilityMFI bitFlipProbabilityMFI = initializeBitFlip();
//		bitFlipProbabilityMFI.setCurrentAddress(5000L);
//		bitFlipProbabilityMFI.setCurrentChip(3);
//		bitFlipProbabilityMFI.preInstruction();
//		
////		PhysicalAddress previousPA = SimulatorManager.getSim().getPhysicalAddressService()
////				.getPhysicalAddress(5000L);
//		
//		PhysicalAddress PA = SimulatorManager.getSim().getPhysicalAddressService()
//		.getPhysicalAddress(bitFlipProbabilityMFI.getCurrentAddress());
//		
//		assertEquals(PA.getModule(), PA.getModule());
//		assertEquals(PA.getRank(), PA.getRank());
//		assertEquals(PA.getBankGroup(), PA.getBankGroup());
//		assertEquals(PA.getBank(), PA.getBank());
//		
//
//	}

	private BitFlipProbabilityMFIOld initializeBitFlip() throws Exception {
		String path = getClass().getClassLoader().getResource("bitFlipTest.yml").getFile();
		if((System.getProperty("os.name").compareTo("Windows 10")==0)) path = path.substring(1);
		path = path.substring(0, path.length()-"bitFlipTest.yml".length());
		SimulatorManager.getSim().load(path, "bitFlipTest.yml");
		BitFlipProbabilityMFIOld bitFlipProbabilityMFI = new BitFlipProbabilityMFIOld();
		return bitFlipProbabilityMFI;
	}

}
