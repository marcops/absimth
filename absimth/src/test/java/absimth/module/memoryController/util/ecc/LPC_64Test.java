package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import absimth.exception.FixableErrorException;
import absimth.sim.utils.Bits;

//@SuppressWarnings("static-method")
class LPC_64Test {
	@Test
	void validateHammingAllOK() throws Exception {
		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
		Bits originalBits = Bits.from(bits);
		Bits created = EccType.LPC.getEncode().encode(originalBits);
		Bits received = EccType.LPC.getEncode().decode(created);
		System.out.println(created);
		System.out.println(received);
		assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
	}
	
	@Test
	void validateHammingBF() throws Exception {
		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
		Bits originalBits = Bits.from(bits);
		Bits created = EccType.LPC.getEncode().encode(originalBits);
		created.set(3, !created.get(3));
//		Bits received = EccType.LPC.getEncode().decode(created);
//		System.out.println(created);
//		System.out.println(received);
		
		FixableErrorException fx = Assertions.assertThrows(FixableErrorException.class, () -> {
			EccType.LPC.getEncode().decode(created);
		});
		
		assertArrayEquals(originalBits.toIntArray(), fx.getRecovered().toIntArray());
		
//		assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
	}
	
	
	@Test
	void validateHamming2BF() throws Exception {
		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
		Bits originalBits = Bits.from(bits);
		Bits created = EccType.LPC.getEncode().encode(originalBits);
		created.set(3, !created.get(3));
		created.set(0, !created.get(0));
//		Bits received = EccType.LPC.getEncode().decode(created);
//		System.out.println(created);
//		System.out.println(received);
		
		FixableErrorException fx = Assertions.assertThrows(FixableErrorException.class, () -> {
			EccType.LPC.getEncode().decode(created);
		});
		
		assertArrayEquals(originalBits.toIntArray(), fx.getRecovered().toIntArray());
		
//		assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
	}
	
	@Test
	void validateHamming2BF_d() throws Exception {
		int[] bits = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 };
		Bits originalBits = Bits.from(bits);
		Bits created = EccType.LPC.getEncode().encode(originalBits);
		created.set(36, !created.get(36));
		created.set(0, !created.get(0));
//		Bits received = EccType.LPC.getEncode().decode(created);
//		System.out.println(created);
//		System.out.println(received);
		
		FixableErrorException fx = Assertions.assertThrows(FixableErrorException.class, () -> {
			EccType.LPC.getEncode().decode(created);
		});
		
		assertArrayEquals(originalBits.toIntArray(), fx.getRecovered().toIntArray());
		
//		assertArrayEquals(originalBits.toIntArray(), received.toIntArray());
	}

}
