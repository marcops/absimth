package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import absimth.exception.UnfixableErrorException;
import absimth.sim.utils.Bits;

//@SuppressWarnings("static-method")
class ParityTest {
	private Parity parity = new Parity();
	@Test
	void validateParityOK() throws UnfixableErrorException {
		Bits original = Bits.from(181);
		Bits be = parity.encode(original);
		Bits bd = parity.decode(be);
		assertEquals(bd.toInt(), original.toInt());
	}
	
	@Test
	void validateParityFalse() throws UnfixableErrorException {
		Bits original = Bits.from(181);
		Bits be = parity.encode(original);
		be.flip(0);
		
		Assertions.assertThrows(UnfixableErrorException.class, () -> {
			parity.decode(be);
		});
		
	}

}
