package absimth.module.memoryController.util.ecc;

import absimth.exception.UnfixableErrorException;
import absimth.sim.utils.Bits;
import absimth.sim.utils.JUtil;

public class Parity implements IEccType {
	private static boolean encode(final boolean[] input) {
		int count = 0;
		for (int i = 0; i < input.length; i++) if(input[i] == true) count++;
		return count % 2 == 0;
	}
	
	@Override
	public Bits encode(Bits input) {
		boolean br = Parity.encode(input.toBoolArray());
		Bits b = Bits.from(input.toLong());
		if(br) return b.append(Bits.from((byte)0x1));
		return b.append(Bits.from((byte)0x0));
	}

	@Override
	public Bits decode(Bits input) throws UnfixableErrorException {
		try {
			if(input.isEmpty()) return input;
			Bits base = input.subbit(0, 64);
			boolean parity = input.subbit(64, 1).toInt() == 1;
			
			boolean calc_party = Parity.encode(base.toBoolArray());
			if (calc_party == parity) return base;
				throw new UnfixableErrorException(input, JUtil.createSet(input.length()));
		}catch (Exception e) {
			System.out.println(e.getMessage());
			throw new UnfixableErrorException(input, JUtil.createSet(input.length()));
		}
	}

}