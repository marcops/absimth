/**
 * Unit tests for ReedSolomon
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */

package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import absimth.sim.utils.Bits;

@SuppressWarnings("static-method")
class ReedSolomonTest {

	@Test
	void testEncodeDecode() throws Exception {
		byte b[] = {'A','B', 'C', 'D' }; 
		Bits original = Bits.fromArray(b);
		ReedSolomon reed = new ReedSolomon();
		Bits coded = reed.encode(original);
		Bits returned = reed.decode(coded);
		assertArrayEquals(original.toByteArray(), returned.toByteArray());
	}
	
}
