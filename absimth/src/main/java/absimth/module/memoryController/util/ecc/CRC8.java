package absimth.module.memoryController.util.ecc;

import absimth.exception.HardErrorException;
import absimth.sim.utils.Bits;
import absimth.sim.utils.JUtil;

public class CRC8 implements IEccType {
	private static final int POLY = 0x0D5;

	public static byte encode(final byte[] input, final int offset, final int len) {
		byte crc = 0;
		for (int i = 0; i < len; i++) {
			crc = encode(input[offset + i], crc);
		}
		return crc;
	}

	public static byte encode(final byte[] input) {
		return encode(input, 0, input.length);
	}

	private static final byte encode(final byte b, int bcrc) {
		int crc = bcrc;
		crc ^= b;
		for (int j = 0; j < 8; j++) {
			if ((crc & 0x80) != 0)
				crc = ((crc << 1) ^ POLY);
			else
				crc <<= 1;

		}
		return (byte) (crc &= 0xFF);
	}

	public static byte encode(final int b) {
		return encode((byte) b, 0);
	}
	
	@Override
	public Bits encode(Bits input) {
		byte br = CRC8.encode(input.toByteArray());
		input.append(Bits.from(br));
		return input;
	}

	@Override
	public Bits decode(Bits input) throws HardErrorException {
		try {
			if(input.isEmpty()) return input;
			byte[] loaded = input.toByteArray();
			byte crc = CRC8.encode(loaded, 0, 8);
			//TODO Remover last bit?
			if (loaded[8] == crc) return input;
				throw new HardErrorException(input, JUtil.createSet(input.length()));
		}catch (Exception e) {
			System.out.println(e.getMessage());
			throw new HardErrorException(input, JUtil.createSet(input.length()));
		}
	}

}