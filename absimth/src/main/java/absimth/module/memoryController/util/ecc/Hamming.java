package absimth.module.memoryController.util.ecc;

import java.util.Set;

import absimth.exception.FixableErrorException;
import absimth.exception.UnfixableErrorException;
import absimth.sim.utils.Bits;

public class Hamming implements IEccType {

	
	boolean[][] table = {
			{true,true,false,false,false,false,false},//0
			{true,false,true,false,false,false,false},
			{false,true,true,false,false,false,false},
			{true,true,true,false,false,false,false},
			{true,false,false,true,false,false,false},
			{false,true,false,true,false,false,false},
			{true,true,false,true,false,false,false},
			{false,false,true,true,false,false,false},
			{true,false,true,true,false,false,false},
			{false,true,true,true,false,false,false},
			{true,true,true,true,false,false,false},//10
			{true,false,false,false,true,false,false},
			{false,true,false,false,true,false,false},
			{true,true,false,false,true,false,false},
			{false,false,true,false,true,false,false},
			{true,false,true,false,true,false,false},
			{false,true,true,false,true,false,false},
			{true,true,true,false,true,false,false},
			{false,false,false,true,true,false,false},
			{true,false,false,true,true,false,false},
			{false,true,false,true,true,false,false},//20
			{true,true,false,true,true,false,false},
			{false,false,true,true,true,false,false},
			{true,false,true,true,true,false,false},
			{false,true,true,true,true,false,false},
			{true,true,true,true,true,false,false},
			{true,false,false,false,false,true,false},
			{false,true,false,false,false,true,false},
			{true,true,false,false,false,true,false},
			{false,false,true,false,false,true,false},
			{true,false,true,false,false,true,false},//30
			{false,true,true,false,false,true,false},
			{true,true,true,false,false,true,false},
			{false,false,false,true,false,true,false},
			{true,false,false,true,false,true,false},
			{false,true,false,true,false,true,false},
			{true,true,false,true,false,true,false},
			{false,false,true,true,false,true,false},
			{true,false,true,true,false,true,false},
			{false,true,true,true,false,true,false},
			{true,true,true,true,false,true,false},//40
			{false,false,false,false,true,true,false},
			{true,false,false,false,true,true,false},
			{false,true,false,false,true,true,false},
			{true,true,false,false,true,true,false},
			{false,false,true,false,true,true,false},
			{true,false,true,false,true,true,false},
			{false,true,true,false,true,true,false},
			{true,true,true,false,true,true,false},
			{false,false,false,true,true,true,false},
			{true,false,false,true,true,true,false},//50
			{false,true,false,true,true,true,false},
			{true,true,false,true,true,true,false},
			{false,false,true,true,true,true,false},
			{true,false,true,true,true,true,false},
			{false,true,true,true,true,true,false},
			{true,true,true,true,true,true,false},
			{true,false,false,false,false,false,true},
			{false,true,false,false,false,false,true},
			{true,true,false,false,false,false,true},
			{false,false,true,false,false,false,true},//60
			{true,false,true,false,false,false,true},
			{false,true,true,false,false,false,true},
			{true,true,true,false,false,false,true}
	}; 
	
	public void mountTable() {
		for(int i=0;i<64;i++) {
			Bits b = Bits.from(0);
			Bits enc = encode(b);
			try {
				enc.flip(i);
				decode(enc);
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	}
	
	private int booleansToInt(boolean[] arr){
	    int n = 0;
	    for (boolean b : arr)
	        n = (n << 1) | (b ? 1 : 0);
	    return n;
	}
	
	@Override
	public Bits decode(Bits data) throws Exception {
//		if(data.length() <= 64) return data;
		boolean []pp = data.subbit(64, 7).toBoolArray();
		boolean []d = data.subbit(0,64).toBoolArray();
		boolean []pg = calculateParity(d);
		   
        if((booleansToInt(pp)^booleansToInt(pg)) == 0) return data.subbit(0,64);
	    
	    int np = discoverBF(pp, pg);
	    if(np == -1) throw new UnfixableErrorException(data, Set.of(np));
	    
	    Bits nBits = data.subbit(0,64);
	    nBits.set(np, !nBits.get(np));	    
	    throw new FixableErrorException(data , nBits, Set.of(np));
		       
	}

	private int discoverBF(boolean[] pp, boolean[] pg) {
		boolean r[] = new boolean[7];
		r[0] = pp[0]^pg[0];
		r[1] = pp[1]^pg[1];
		r[2] = pp[2]^pg[2];
		r[3] = pp[3]^pg[3];
		r[4] = pp[4]^pg[4];
		r[5] = pp[5]^pg[5];
		r[6] = pp[6]^pg[6];
//		System.out.println("{" +r[0]+","+r[1]+","+r[2]+","+r[3]+","+r[4]+","+r[5]+","+r[6]+"},");
		for(int i=0;i<table.length;i++) {
			boolean []c = table[i];
			if(r[0]==c[0]&&r[1]==c[1]&&r[2]==c[2]&&r[3]==c[3]&&r[4]==c[4]&&r[5]==c[5]&&r[6]==c[6]) return i;
		}
		return -1;
	}

	@Override
	public Bits encode(Bits data) {
		boolean []d = data.toBoolArray();
		boolean []p = calculateParity(d);
     
        Bits base = Bits.from(d);
        Bits parity = Bits.from(p);
        Bits withEcc = base.append(parity);
		return withEcc;
	}

	private boolean[] calculateParity(boolean[] d) {
		boolean []p = new boolean[7];
		p[0] = d[0]^d[1]^d[3]^d[4]^d[6]^d[8]^d[10]^d[11]^d[13]^d[15]^d[17]^d[19]^d[21]^d[23]^d[25]^d[26]^d[28]^d[30]^d[32]^d[34]^d[36]^d[38]^d[40]^d[42]^d[44]^d[46]^d[48]^d[50]^d[52]^d[54]^d[56]^d[57]^d[59]^d[61]^d[63];
        p[1] = d[0]^d[2]^d[3]^d[5]^d[6]^d[9]^d[10]^d[12]^d[13]^d[16]^d[17]^d[20]^d[21]^d[24]^d[25]^d[27]^d[28]^d[31]^d[32]^d[35]^d[36]^d[39]^d[40]^d[43]^d[44]^d[47]^d[48]^d[51]^d[52]^d[55]^d[56]^d[58]^d[59]^d[62]^d[63];
        p[2] = d[1]^d[2]^d[3]^d[7]^d[8]^d[9]^d[10]^d[14]^d[15]^d[16]^d[17]^d[22]^d[23]^d[24]^d[25]^d[29]^d[30]^d[31]^d[32]^d[37]^d[38]^d[39]^d[40]^d[45]^d[46]^d[47]^d[48]^d[53]^d[54]^d[55]^d[56]^d[60]^d[61]^d[62]^d[63];
        p[3] = d[4]^d[5]^d[6]^d[7]^d[8]^d[9]^d[10]^d[18]^d[19]^d[20]^d[21]^d[22]^d[23]^d[24]^d[25]^d[33]^d[34]^d[35]^d[36]^d[37]^d[38]^d[39]^d[40]^d[49]^d[50]^d[51]^d[52]^d[53]^d[54]^d[55]^d[56];
        p[4] = d[11]^d[12]^d[13]^d[14]^d[15]^d[16]^d[17]^d[18]^d[19]^d[20]^d[21]^d[22]^d[23]^d[24]^d[25]^d[41]^d[42]^d[43]^d[44]^d[45]^d[46]^d[47]^d[48]^d[49]^d[50]^d[51]^d[52]^d[53]^d[54]^d[55]^d[56];
        p[5] = d[26]^d[27]^d[28]^d[29]^d[30]^d[31]^d[32]^d[33]^d[34]^d[35]^d[36]^d[37]^d[38]^d[39]^d[40]^d[41]^d[42]^d[43]^d[44]^d[45]^d[46]^d[47]^d[48]^d[49]^d[50]^d[51]^d[52]^d[53]^d[54]^d[55]^d[56];
        p[6] = d[57]^d[58]^d[59]^d[60]^d[61]^d[62]^d[63];
        return p;
	}
	
	
	
//	private static int calcParity(int[] created) {
//		int v = 0;
//		for (int i = 0; i < created.length - 1; i++) {
//			v ^= created[i];
//		}
//		return v;
//	}
//
//	public static int[] encode(int[] signal) {
//
//		int[] generatedCode;
//		int parityCount = calculateParity(signal);
////		System.out.println("parity=" + parityCount);
//		generatedCode = new int[signal.length + parityCount + 1];
//
//		allocateBits(generatedCode, signal);
//		allocateParityBits(generatedCode, parityCount);
//		generatedCode[signal.length + parityCount] = calcParity(generatedCode);
//		return generatedCode;
//	}
//
//	private static int calculateParity(int[] signal) {
//		int parityCount = 0;
//		int i = 0;
//		while (i < signal.length) {
//			int poweredPos = (int) Math.pow(2, parityCount);
//			if (poweredPos == parityCount + i + 1)
//				parityCount++;
//			else
//				i++;
//		}
//		return parityCount;
//	}
//
//	private static void allocateParityBits(int[] code, int parityCount) {
//		for (int i = 0; i < parityCount; i++) {
//			code[((int) Math.pow(2, i)) - 1] = getParity(code, i);
//		}
//	}
//
//	private static void allocateBits(int[] code, int[] signal) {
//		int j = 0;
//		int k = 0;
//		for (int i = 1; i <= code.length - 1; i++) {
//			if (Math.pow(2, j) == i) {
//				code[i - 1] = 2;
//				j++;
//			} else {
//				code[k + j] = signal[k++];
//			}
//		}
//	}
//
//	private static int getParity(int[] bits, int power) {
//
//		int parity = 0;
//
//		for (int i = 0; i < bits.length; i++) {
//			if (bits[i] != 2) {
//				int k = i + 1;
//
//				String s = Integer.toBinaryString(k);
//
//				int x = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;
//
//				if (x == 1 && bits[i] == 1) {
//					parity = (parity + 1) % 2;
//				}
//			}
//		}
//		return parity;
//	}
//
//	@Override
//	public Bits decode(Bits data) throws UnfixableErrorException, FixableErrorException {
//		int []generatedCode = data.toIntArray();
//		boolean soft = false;
//		int parityCount = 7;
//		int power;
//		int parity[] = new int[parityCount];
//
//		String errorLocation = new String();
//
//		for (power = 0; power < parityCount; power++) {
//			for (int i = 0; i < generatedCode.length - 1; i++) {
//
//				int k = i + 1;
//
//				String s = Integer.toBinaryString(k);
//
//				int bit = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;
//
//				if (bit == 1) {
//					if (generatedCode[i] == 1) {
//						parity[power] = (parity[power] + 1) % 2;
//					}
//				}
//			}
//			errorLocation = parity[power] + errorLocation;
//		}
//
//		int error_location = Integer.parseInt(errorLocation, 2);
//		if (error_location != 0) {
//			generatedCode[error_location - 1] = (generatedCode[error_location - 1] + 1) % 2;
////			System.out.println("fix at =" + (error_location - 1));
//			if (generatedCode[generatedCode.length - 1] != calcParity(generatedCode)) {
////				System.out.println("Double Error");
////				System.out.println(generatedCode[generatedCode.length - 1] + " - " + calcParity(generatedCode));
//				throw new UnfixableErrorException(data, Set.of(error_location));
//			}
//			soft = true;
////			throw new SoftError();
//		} 
//
//		power = parityCount - 1;
//		String original = "";
//		for (int i = generatedCode.length - 1; i > 0; i--) {
//			if (Math.pow(2, power) != i) {
//				original += generatedCode[i - 1];
//			} else {
//				power--;
//			}
//		}
//		int re[] = new int[original.length()];
//		for (int i = 0; i < original.length(); i++)
//			re[original.length() - i - 1] = original.charAt(i) == '1' ? 1 : 0;
//		Bits b = Bits.from(re);
//		if(soft) {
//			System.out.println(b.toLong());
//			throw new FixableErrorException(data , b, Set.of(error_location));
//		}
//		return b;
//	}
//
//	@Override
//	public Bits encode(Bits signal) {
//		int[] a = signal.toIntArray();
//		if (a.length < Bits.WORD_LENGTH)
//			return Bits.from(encode(Arrays.copyOf(a, Bits.WORD_LENGTH)));
//		return Bits.from(encode(a));
//	}
}
