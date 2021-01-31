package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import absimth.exception.HardErrorException;
import absimth.exception.SoftErrorException;
import absimth.sim.utils.Bits;

@SuppressWarnings("static-method")
class HammingTest {
	@Test
	void validateHamming() throws HardErrorException, SoftErrorException {
		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
		Bits originalBits = Bits.from(bits);
		Bits created = Hamming.encode(originalBits);

		Bits received = Hamming.decode(created);

		System.out.println(received);
		assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
	}

	@Test
	void validateHammingWithSoftError() {
		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
		
		Bits originalBits = Bits.from(bits); 
		Bits created = Hamming.encode(originalBits);
		created.set(3, !created.get(3));
		Assertions.assertThrows(SoftErrorException.class, () -> {
			Hamming.decode(created);
		});

	}

	@Test
	void validateHammingWith2Error() {
		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
		Bits originalBits = Bits.from(bits); 
		Bits created = Hamming.encode(originalBits);
		created.set(3, !created.get(3));
		created.set(6, !created.get(6));
		Assertions.assertThrows(HardErrorException.class, () -> {
			Hamming.decode(created);
		});
	}

	@Test
	void validateHammingBits() throws HardErrorException, SoftErrorException {
		Bits originalBits = Bits.from(Long.MAX_VALUE);
		Bits created = Hamming.encode(originalBits);

		Bits received = Hamming.decode(created);

		assertEquals(originalBits.toLong(), received.toLong());
	}
	
	
	@Test
	void validateHammingBitsP() throws HardErrorException, SoftErrorException {
		String p = "P";
		Bits originalBits = Bits.from(p)[0];
		Bits created = Hamming.encode(originalBits);

		Bits received = Hamming.decode(created);

		assertEquals(originalBits.toLong(), received.toLong());
	}

	@Test
	void validateHammingBitsWithError() {
		Bits originalBits = Bits.from(Long.MAX_VALUE);
		Bits created = Hamming.encode(originalBits);
		created.set(3, !created.get(3));
		Assertions.assertThrows(SoftErrorException.class, () -> {
			Hamming.decode(created);
		});

	}

	@Test
	void validateHammingBitsWith2Error() {
		Bits originalBits = Bits.from(Long.MAX_VALUE);
		Bits created = Hamming.encode(originalBits);
		created.set(3, !created.get(3));
		created.set(6, !created.get(6));
		Assertions.assertThrows(HardErrorException.class, () -> {
			Hamming.decode(created);
		});

	}

}
