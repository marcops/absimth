/**
 * Unit tests for ReedSolomon
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */

package absimth.module.memoryController.util.ecc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import absimth.module.memoryController.util.ecc.reed.ReedSolomonBase;

/**
 * Tests the ReedSolomon class.
 *
 * Most of the test cases were copied from the Python code to make sure that the
 * Java code does the same thing.
 */
//@SuppressWarnings("static-method")
class ReedSolomonBaseTest {

	@Test
	void testSimpleEncodeDecode() {
		 byte [][] dataShards = new byte [][] {
			 new byte [] { (char)'a', (char)'b',  (char)'c', (char)'d'},
             new byte [] { 0, 0, 0, 0 }
         };

        int sizeShards = dataShards.length;
 	    int sizeData = dataShards[0].length;
 	    
         
         byte [][] original = new byte [sizeShards/2] [];
         for (int i = 0; i < sizeShards/2; i++) {
        	 original[i] = Arrays.copyOf(dataShards[i], sizeData);
         }
         
	    
		boolean[] shardPresent = new boolean[sizeShards];
		for(int i=0;i<sizeShards;i++) shardPresent[i] = true;

		ReedSolomonBase codec = ReedSolomonBase.create(sizeShards/2, sizeShards/2);
		codec.encodeParity(dataShards, 0, sizeData);

		codec.decodeMissing(dataShards, shardPresent, 0, sizeData);
		for (int i = 0; i < original.length; i++) {
			assertArrayEquals(original[i], dataShards[i]);
		}

	}
	
}
