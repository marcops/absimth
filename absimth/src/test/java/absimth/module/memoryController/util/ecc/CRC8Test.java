package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

//@SuppressWarnings("static-method")
class CRC8Test {

	@Test
	void validateHamming() {
		assertEquals((byte) 181, CRC8.encode("test".getBytes()));
	}

	@Test
	void validateHammingWithError() {
		assertEquals((byte) 59, CRC8.encode("hello world".getBytes()));
	}

}
