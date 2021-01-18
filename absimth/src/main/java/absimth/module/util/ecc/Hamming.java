package absimth.module.util.ecc;

import absimth.exception.HardErrorException;
import absimth.exception.SoftErrorException;
import absimth.sim.memory.faultInjection.model.Bits;

public class Hamming {
	private static int calcParity(int[] created) {
		int v = 0;
		for (int i = 0; i < created.length - 1; i++) {
			v ^= created[i];
		}
		return v;
	}

	public static int[] encode(int[] signal) {

		int[] generatedCode;
		int parityCount = calculateParity(signal);
//		System.out.println("parity=" + parityCount);
		generatedCode = new int[signal.length + parityCount + 1];

		allocateBits(generatedCode, signal);
		allocateParityBits(generatedCode, parityCount);
		generatedCode[signal.length + parityCount] = calcParity(generatedCode);
		return generatedCode;
	}

	private static int calculateParity(int[] signal) {
		int parityCount = 0;
		int i = 0;
		while (i < signal.length) {
			int poweredPos = (int) Math.pow(2, parityCount);
			if (poweredPos == parityCount + i + 1)
				parityCount++;
			else
				i++;
		}
		return parityCount;
	}

	private static void allocateParityBits(int[] code, int parityCount) {
		for (int i = 0; i < parityCount; i++) {
			code[((int) Math.pow(2, i)) - 1] = getParity(code, i);
		}
	}

	private static void allocateBits(int[] code, int[] signal) {
		int j = 0;
		int k = 0;
		for (int i = 1; i <= code.length - 1; i++) {
			if (Math.pow(2, j) == i) {
				code[i - 1] = 2;
				j++;
			} else {
				code[k + j] = signal[k++];
			}
		}
	}

	private static int getParity(int[] bits, int power) {

		int parity = 0;

		for (int i = 0; i < bits.length; i++) {
			if (bits[i] != 2) {
				int k = i + 1;

				String s = Integer.toBinaryString(k);

				int x = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;

				if (x == 1 && bits[i] == 1) {
					parity = (parity + 1) % 2;
				}
			}
		}
		return parity;
	}

	public static Bits decode(Bits data) throws HardErrorException, SoftErrorException {
		int []generatedCode = data.toIntArray();
		boolean soft = false;
		int parityCount = 7;
		int power;
		int parity[] = new int[parityCount];

		String errorLocation = new String();

		for (power = 0; power < parityCount; power++) {
			for (int i = 0; i < generatedCode.length - 1; i++) {

				int k = i + 1;

				String s = Integer.toBinaryString(k);

				int bit = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;

				if (bit == 1) {
					if (generatedCode[i] == 1) {
						parity[power] = (parity[power] + 1) % 2;
					}
				}
			}
			errorLocation = parity[power] + errorLocation;
		}

		int error_location = Integer.parseInt(errorLocation, 2);
		if (error_location != 0) {
			generatedCode[error_location - 1] = (generatedCode[error_location - 1] + 1) % 2;
//			System.out.println("fix at =" + (error_location - 1));
			if (generatedCode[generatedCode.length - 1] != calcParity(generatedCode)) {
//				System.out.println("Double Error");
//				System.out.println(generatedCode[generatedCode.length - 1] + " - " + calcParity(generatedCode));
				throw new HardErrorException(data);
			}
			soft = true;
//			throw new SoftError();
		} 

		power = parityCount - 1;
		String original = "";
		for (int i = generatedCode.length - 1; i > 0; i--) {
			if (Math.pow(2, power) != i) {
				original += generatedCode[i - 1];
			} else {
				power--;
			}
		}
		int re[] = new int[original.length()];
		for (int i = 0; i < original.length(); i++)
			re[original.length() - i - 1] = original.charAt(i) == '1' ? 1 : 0;
		Bits b = Bits.from(re);
		if(soft) throw new SoftErrorException(data , b);
		return b;
	}

	public static Bits encode(Bits signal) {
		int[] a = signal.toIntArray();
		return Bits.from(encode(a));

	}


}
