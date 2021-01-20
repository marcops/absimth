package absimth.sim.memory.model;

import java.util.BitSet;

//@Slf4j
public class Bits extends BitSet {
	public static final int WORD_LENGTH = 64;
	public static final int ONE_BYTE = 8; 
	private int length;

	public Bits() {
		length = 0;
	}

	public Bits(int size) {
		super(size);
		length = size;
	}
	
	public void invert(long pos) {
		if(pos < 0 || pos > length) {
			//log.error("bit invert fail " + pos);
			System.err.println("bit invert fail " + pos);
		}
		this.set((int)pos,!this.get((int)pos));
	}
	
	@Override
	public int length() {
		return length;
	}
	
	public static Bits from(final byte msg) {
		return bitSet2Bits(BitSet.valueOf(new byte[] { msg }), 8);
	}
	
	public static Bits fromArray(final byte[] msgs) {
		Bits b = new Bits();
		for(int i = 0; i< msgs.length;i++) 
			b.append(from(msgs[i]));
		return b;
	}
	
	public static Bits[] from(final byte[] msgs) {
		Bits[] b = new Bits[msgs.length];
		for(int i = 0; i< msgs.length;i++)
			b[i] = from(msgs[i]);
		return b;
	}
	
	public static Bits[] from(final String s) {
		return from(s.getBytes());
	}
	
	private static Bits bitSet2Bits(BitSet bs, int length) {
		Bits b = new Bits(length);
		for (int i = 0; i < length; i++) b.set(i, bs.get(i));
		return b;
	}


	public int toInt() {
		if (this.toLongArray().length == 0) return 0;
		return (int) this.toLongArray()[0];
	}
	
	public long toLong() {
		if(this.length == 0) return 0;
		if (this.toLongArray().length == 0) return 0;
		return this.toLongArray()[0];
	}

	public Bits subbit(int position, int size) {
		Bits bit = new Bits(size);
		for (int i = 0; i < size; i++)
			bit.set(i, this.get(i + position));
		return bit;
	}

	public void append(Bits bits) {
		int baseLenght = length; 
		for (int i = 0; i < bits.length(); i++) {
			this.set(baseLenght + i, bits.get(i));
			length++;
		}
	}
	
	public String toBitString() {
		String value = "";
		for (int i = 0; i < length; i++) 
			value += this.get(i) ? "1" : "0";
		return value;
	}

	public static Bits from(long r) {
		return bitSet2Bits(BitSet.valueOf(new long[] { r }), WORD_LENGTH);
	}

	public int[] toIntArray() {
		int []value = new int[length] ;
		for (int i = 0; i < length; i++) 
			value[i] = this.get(i) ? 1 : 0;
		return value;
		
	}

	public static Bits from(int[] msgs) {
		Bits b = new Bits(msgs.length);
		for(int i = 0; i< msgs.length;i++)
			b.set(i, msgs[i] == 1);
		return b;
	}
	
	@Override
	public byte[] toByteArray() {
		byte [] bytes = new byte[length/ONE_BYTE]; 
		for(int i=0;i<length/ONE_BYTE;i++) {
			bytes[i] = (byte) subbit(i*ONE_BYTE, ONE_BYTE).toInt();
		}
		return bytes;
	}
}
