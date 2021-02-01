package absimth.module.memoryController.util.ecc;

import java.util.Arrays;

import absimth.module.memoryController.util.ecc.reed.ReedSolomonBase;
import absimth.sim.utils.Bits;

public class ReedSolomon implements IEccType {
	/*
	 * CONSIDERANDO 32bits
	 * */
	@Override
	public Bits encode(Bits input) {
		byte[][] dataShards = new byte[2][4];
		dataShards[0] = Arrays.copyOf(input.toByteArray(), 4);
		dataShards[1] = new byte[] { 0, 0, 0, 0 };
		ReedSolomonBase codec = ReedSolomonBase.create(1, 1);
		codec.encodeParity(dataShards, 0, 4);

		return Bits.fromArray(dataShards[0]).appendFromArray(dataShards[1]);
	}

	@Override
	public Bits decode(Bits input) throws Exception {
		boolean[] shardPresent = new boolean[2];
		for (int i = 0; i < 2; i++) shardPresent[i] = true;
		
		byte b[] = input.toByteArray();
		byte[][] dataShards = new byte[2][4];
		for(int i=0;i<4;i++) 
			dataShards[0][i] = b[i];
		
		for(int i=0;i<4;i++) 
			dataShards[1][i] = b[i+4];
		
		//TODO MAKE try catch
		ReedSolomonBase codec = ReedSolomonBase.create(1, 1);
		codec.decodeMissing(dataShards, shardPresent, 0, 4);
		
		Bits nBits = Bits.fromArray(dataShards[0]);
		return nBits;
		
	}

}