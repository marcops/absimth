package absimth.sim.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import absimth.sim.configuration.ConfigurationService;
import absimth.sim.configuration.model.AbsimthConfigurationModel;

class PhysicalAddressTest {
	@Test
	void testDual() throws Exception {
		AbsimthConfigurationModel cont  = ConfigurationService.load(getClass().getClassLoader().getResource("memoryPhysicalTestDual.yml").getPath());
		PhysicalAddressService pAddress = PhysicalAddressService.create(cont.getHardware().getMemory().getModule(), cont.getHardware().getMemory().getChannelMode());

		
		
		testAddress(pAddress, 0, 0, 0, 0, 0, 0, 0, 0);
		testAddress(pAddress, 1, 0, 0, 0, 0, 0, 0, 1);
		testAddress(pAddress, 2, 1, 0, 0, 0, 0, 0, 0);
		testAddress(pAddress, 3, 1, 0, 0, 0, 0, 0, 1);
		testAddress(pAddress, 4, 0, 0, 0, 0, 0, 1, 0);
		testAddress(pAddress, 5, 0, 0, 0, 0, 0, 1, 1);
		testAddress(pAddress, 6, 1, 0, 0, 0, 0, 1, 0);
		testAddress(pAddress, 7, 1, 0, 0, 0, 0, 1, 1);
		testAddress(pAddress, 8, 0, 0, 0, 0, 1, 0, 0);
		testAddress(pAddress, 9, 0, 0, 0, 0, 1, 0, 1);

		testAddress(pAddress, 15, 1, 0, 0, 0, 1, 1, 1);
		testAddress(pAddress, 16, 0, 0, 0, 1, 0, 0, 0);
		testAddress(pAddress, 17, 0, 0, 0, 1, 0, 0, 1);

		testAddress(pAddress, 31, 1, 0, 0, 1, 1, 1, 1);
		testAddress(pAddress, 32, 0, 0, 1, 0, 0, 0, 0);
		testAddress(pAddress, 33, 0, 0, 1, 0, 0, 0, 1);
		
		testAddress(pAddress, 63, 1, 0, 1, 1, 1, 1, 1);
		testAddress(pAddress, 64, 0, 1, 0, 0, 0, 0, 0);
		testAddress(pAddress, 65, 0, 1, 0, 0, 0, 0, 1);
		
		testAddress(pAddress, 127, 1, 1, 1, 1, 1, 1, 1);
	}
	
	@Test
	void testSingle() throws Exception {
		AbsimthConfigurationModel cont  = ConfigurationService.load(getClass().getClassLoader().getResource("memoryPhysicalTestSingle.yml").getPath());
		PhysicalAddressService pAddress = PhysicalAddressService.create(cont.getHardware().getMemory().getModule(), cont.getHardware().getMemory().getChannelMode());
		
		
		testAddress(pAddress, 0, 0, 0, 0, 0, 0, 0, 0);
		testAddress(pAddress, 1, 0, 0, 0, 0, 0, 0, 1);
		testAddress(pAddress, 2, 0, 0, 0, 0, 0, 1, 0);
		testAddress(pAddress, 3, 0, 0, 0, 0, 0, 1, 1);
		testAddress(pAddress, 4, 0, 0, 0, 0, 1, 0, 0);
		testAddress(pAddress, 5, 0, 0, 0, 0, 1, 0, 1);
		testAddress(pAddress, 6, 0, 0, 0, 0, 1, 1, 0);
		testAddress(pAddress, 7, 0, 0, 0, 0, 1, 1, 1);
		testAddress(pAddress, 8, 0, 0, 0, 1, 0, 0, 0);
		testAddress(pAddress, 9, 0, 0, 0, 1, 0, 0, 1);
		
		testAddress(pAddress, 15, 0, 0, 0, 1, 1, 1, 1);
		testAddress(pAddress, 16, 0, 0, 1, 0, 0, 0, 0);
		testAddress(pAddress, 17, 0, 0, 1, 0, 0, 0, 1);
		
		testAddress(pAddress, 31, 0, 0, 1, 1, 1, 1, 1);
		testAddress(pAddress, 32, 0, 1, 0, 0, 0, 0, 0);
		testAddress(pAddress, 33, 0, 1, 0, 0, 0, 0, 1);
		
		testAddress(pAddress, 63, 0, 1, 1, 1, 1, 1, 1);
		testAddress(pAddress, 64, 1, 0, 0, 0, 0, 0, 0);
		testAddress(pAddress, 65, 1, 0, 0, 0, 0, 0, 1);
		
		testAddress(pAddress, 127, 1, 1, 1, 1, 1, 1, 1);
		
	}
	

	private static void testAddress(PhysicalAddressService pAddress, long address, int module, int rank, int bg, int bank, int row, int column, int pagePosition) {
		assertEquals(module, pAddress.getModule(address));
		assertEquals(rank, pAddress.getRank(address));
		assertEquals(bg, pAddress.getBankGroup(address));
		assertEquals(bank, pAddress.getBank(address));
		assertEquals(row, pAddress.getRow(address));
		assertEquals(column, pAddress.getColumn(address));
		assertEquals(pagePosition, pAddress.getPagePosition(address));
		
		long addressResult = pAddress.getPhysicalAddressReverse(module, rank, bg, bank, row, column, pagePosition).getPAddress();
		assertEquals(address, addressResult);
	}

}
