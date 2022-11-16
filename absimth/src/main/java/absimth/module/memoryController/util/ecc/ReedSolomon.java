package absimth.module.memoryController.util.ecc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import absimth.exception.FixableErrorException;
import absimth.exception.UnfixableErrorException;
import absimth.module.memoryController.util.ecc.reed.ReedSolomonBase;
import absimth.sim.utils.Bits;

public class ReedSolomon implements IEccType {
	@Override
	public Bits encode(Bits input) {
		Bits ba = input.subbit(0, 32);
		Bits bb = input.subbit(32, 64);
		return encode32(ba).append(bb);
	}
	
	@Override
	public Bits decode(Bits input) throws Exception {
		Bits ba = input.subbit(0, 64);
		Bits bb = input.subbit(64, 128);
		return decode32(ba).append(decode32(bb));
		
	}
	
	/*
	 * CONSIDERANDO 32bits
	 * */
	private Bits encode32(Bits input) {
		byte[][] dataShards = new byte[2][4];
		dataShards[0] = Arrays.copyOf(input.toByteArray(), 4);
		dataShards[1] = new byte[] { 0, 0, 0, 0 };
		ReedSolomonBase codec = ReedSolomonBase.create(1, 1);
		codec.encodeParity(dataShards, 0, 4);

		return Bits.fromArray(dataShards[0]).appendFromArray(dataShards[1]);
	}
	

	
	private Bits decode32(Bits input) throws Exception {
		boolean[] shardPresent = new boolean[2];
		Set<Integer> errors = new HashSet<>();
		for (int i = 0; i < 2; i++) shardPresent[i] = true;
		
		byte b[] = input.toByteArray();
		byte[][] dataShards = new byte[2][4];
		for(int i=0;i<4;i++) 
			dataShards[0][i] = b[i];
		
		for(int i=0;i<4;i++) 
			dataShards[1][i] = b[i+4];
		
		try {
			ReedSolomonBase codec = ReedSolomonBase.create(1, 1);
			codec.decodeMissing(dataShards, shardPresent, 0, 4);
		} catch (Exception e) {
			UnfixableErrorException uf = new UnfixableErrorException(input, errors);
			uf.addSuppressed(e);
			throw uf;
		}

		for(int i=0;i<dataShards[0].length;i++) {
			if(dataShards[0][i] != dataShards[1][i]) errors.add(i);
		}
		if(errors.isEmpty())
			return Bits.fromArray(dataShards[0]);
//		if(errors.size()<=4)
			throw new FixableErrorException(Bits.fromArray(dataShards[0]) , Bits.fromArray(dataShards[1]), errors);
//		throw new UnfixableErrorException(input, errors);
		
	}

}