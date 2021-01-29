package absimth.sim.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import absimth.sim.configuration.ConfigurationService;
import absimth.sim.configuration.model.AbsimthConfigurationModel;

class PhysicalAddressTest {
	@Test
	void testBank() throws Exception {
		AbsimthConfigurationModel cont  = ConfigurationService.load(getClass().getClassLoader().getResource("memoryPhysicalTest.yml").getPath());
		PhysicalAddressService pAddress = PhysicalAddressService.create(cont.getHardware().getMemory().getModule(), cont.getHardware().getMemory().getChannelMode());

		testAddress(pAddress, 4, 0, 0, 0, 0, 0, 2);
		testAddress(pAddress, 5, 1, 0, 0, 0, 0, 2);
		
		testAddress(pAddress, 2400, 0, 0, 0, 0, 1, 200);
		testAddress(pAddress, 1121400, 0, 0, 0, 1, 230, 700);
		testAddress(pAddress, 5121400, 0, 0, 1, 3, 250, 700);
		testAddress(pAddress, 0xa121ff, 1, 0, 3, 3, 329, 999);
	}

	private static void testAddress(PhysicalAddressService pAddress, long address, int module, int rank, int bg, int bank, int row, int column) {
		assertEquals(module, pAddress.getModule(address));
		assertEquals(rank, pAddress.getRank(address));
		assertEquals(bg, pAddress.getBankGroup(address));
		assertEquals(bank, pAddress.getBank(address));
		assertEquals(row, pAddress.getRow(address));
		assertEquals(column, pAddress.getColumn(address));
		
		long addressResult = pAddress.getPhysicalAddressReverse(module, rank, bg, bank, row, column).getPAddress();
		assertEquals(address, addressResult);
	}

}
