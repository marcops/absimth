/**
 * Unit tests for ReedSolomon
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */

package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import absimth.exception.FixableErrorException;
import absimth.sim.utils.Bits;

@SuppressWarnings("static-method")
class ReedSolomonTest {

	
	@Test
	void testEncodeDecodeInt() throws Exception {
//		byte b[] = {'1','2', '3', '4', '5', '6', '7', '8', '9' };
		int d = 1048887;
		Bits original = Bits.from(d);
		ReedSolomon reed = new ReedSolomon();
		Bits coded = reed.encode(original);
		Bits returned = reed.decode(coded);
		assertArrayEquals(original.toByteArray(), returned.toByteArray());
	}
	
	@Test
	void testEncodeDecode() throws Exception {
		byte b[] = {'A','B', 'C', 'D' }; 
		Bits original = Bits.fromArray(b);
		ReedSolomon reed = new ReedSolomon();
		Bits coded = reed.encode(original);
		Bits returned = reed.decode(coded);
		assertArrayEquals(b, returned.toByteArray());
	}
	
	@Test()
	void testFixableEncodeDecode1() throws Exception {
		byte b[] = {'A','B', 'C', 'D' }; 
		Bits original = Bits.fromArray(b);
		ReedSolomon reed = new ReedSolomon();
		Bits coded = reed.encode(original);
		coded.flip(2);
		
		FixableErrorException fx = Assertions.assertThrows(FixableErrorException.class, () -> {
			reed.decode(coded);
		});
		
		assertArrayEquals(b, fx.getRecovered().toByteArray());
	}	
	@Test()
	void testfixableEncodeDecode32() throws Exception {
		byte b[] = {'A','B', 'C', 'D' }; 
		Bits original = Bits.fromArray(b);
		ReedSolomon reed = new ReedSolomon();
		Bits coded = reed.encode(original);
		for(int i=0;i<32;i++)
			coded.flip(i);
		
		FixableErrorException fx = Assertions.assertThrows(FixableErrorException.class, () -> {
			reed.decode(coded);
		});
		
		assertArrayEquals(b, fx.getRecovered().toByteArray());
	}
	
	
//	@Test()
//	void testUnfixableEncodeDecode35() throws Exception {
//		byte b[] = {'A','B', 'C', 'D' }; 
//		Bits original = Bits.fromArray(b);
//		ReedSolomon reed = new ReedSolomon();
//		Bits coded = reed.encode(original);
//		for(int i=0;i<35;i++)
//			coded.flip(i);
//		
//		Assertions.assertThrows(UnfixableErrorException.class, () -> {
//			reed.decode(coded);
//		});
//		
//	}
	
}
