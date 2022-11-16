package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import absimth.exception.FixableErrorException;
import absimth.sim.utils.Bits;

//@SuppressWarnings("static-method")
class HammingTest {
//	@Test
//	void print() throws Exception {
//		new Hamming().mountTable();
//	}
	
	@Test
	void validateHamming() throws Exception {
		Bits originalBits = Bits.from(200);
		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);

		Bits received = EccType.HAMMING_SECDEC.getEncode().decode(created);
		assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
	}
	
	@Test
	void validateHamming_oneErr() throws Exception {
		for(int i=0;i<64;i++ ) {
//			System.out.println(i);
			Bits originalBits = Bits.from(0);
			Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
			created.flip(i);			
			FixableErrorException f = Assertions.assertThrows(FixableErrorException.class, () -> {
				EccType.HAMMING_SECDEC.getEncode().decode(created);
			});
			Bits received = f.getRecovered();
			assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
		}
			
	}
	
	@Test
	void validateHamming_twoErr() throws Exception {
//		for(int i=0;i<64;i++ ) {
//			System.out.println(i);
			Bits originalBits = Bits.from(0);
			Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
			created.flip(0);
			created.flip(1);
//			created.flip(2);
			Assertions.assertThrows(FixableErrorException.class, () -> {
				EccType.HAMMING_SECDEC.getEncode().decode(created);
			});
//		}
			
	}
	
//	@Test
//	void validateHamming() throws Exception {
//		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
//				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
//		Bits originalBits = Bits.from(bits);
//		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
//
//		Bits received = EccType.HAMMING_SECDEC.getEncode().decode(created);
//
//		System.out.println(received);
//		assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
//	}
//
//	@Test
//	void validateHammingWithSoftError() {
//		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
//				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
//		
//		Bits originalBits = Bits.from(bits); 
//		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
//		created.set(3, !created.get(3));
//		Assertions.assertThrows(FixableErrorException.class, () -> {
//			EccType.HAMMING_SECDEC.getEncode().decode(created);
//		});
//
//	}
//
//	@Test
//	void validateHammingWith2Error() {
//		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
//				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
//		Bits originalBits = Bits.from(bits); 
//		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
//		created.set(3, !created.get(3));
//		created.set(6, !created.get(6));
//		Assertions.assertThrows(UnfixableErrorException.class, () -> {
//			EccType.HAMMING_SECDEC.getEncode().decode(created);
//		});
//	}
//
//	@Test
//	void validateHammingBits() throws Exception {
//		Bits originalBits = Bits.from(Long.MAX_VALUE);
//		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
//
//		Bits received = EccType.HAMMING_SECDEC.getEncode().decode(created);
//
//		assertEquals(originalBits.toLong(), received.toLong());
//	}
//	
//	
//	@Test
//	void validateHammingBitsP() throws Exception {
//		String p = "P";
//		Bits originalBits = Bits.from(p)[0];
//		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
//
//		Bits received = EccType.HAMMING_SECDEC.getEncode().decode(created);
//
//		assertEquals(originalBits.toLong(), received.toLong());
//	}
//
//	@Test
//	void validateHammingBitsWithError() {
//		Bits originalBits = Bits.from(Long.MAX_VALUE);
//		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
//		created.set(3, !created.get(3));
//		Assertions.assertThrows(FixableErrorException.class, () -> {
//			EccType.HAMMING_SECDEC.getEncode().decode(created);
//		});
//
//	}
//
//	@Test
//	void validateHammingBitsWith2Error() {
//		Bits originalBits = Bits.from(Long.MAX_VALUE);
//		Bits created = EccType.HAMMING_SECDEC.getEncode().encode(originalBits);
//		created.set(3, !created.get(3));
//		created.set(6, !created.get(6));
//		Assertions.assertThrows(UnfixableErrorException.class, () -> {
//			EccType.HAMMING_SECDEC.getEncode().decode(created);
//		});
//
//	}

}
