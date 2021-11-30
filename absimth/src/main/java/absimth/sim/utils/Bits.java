package absimth.sim.utils;

import java.util.BitSet;

//@Slf4j
public class Bits extends BitSet {
	private static final long serialVersionUID = 1L;
	//is that right? maybie get the number of chip * byte_size 
	public static final int WORD_LENGTH = 64;
	public static final int BYTE_SIZE = 8; 
	private int length;

	public Bits() {
		length = 0;
	}

	public Bits(int size) {
		super(size);
		length = size;
	}
	
	public void invert(long pos) {
		if(pos < 0 || pos > length) System.err.println("bit invert fail " + pos);
		this.set((int)pos,!this.get((int)pos));
	}
	
	@Override
	public int length() {
		return length;
	}
	
	public static Bits from(final byte msg) {
		return bitSet2Bits(BitSet.valueOf(new byte[] { msg }), BYTE_SIZE);
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
		if (this.length == 0) return 0;
		if (this.toLongArray().length == 0) return 0;
		return this.toLongArray()[0];
	}

	public Bits subbit(int position, int size) {
		Bits bit = new Bits(size);
		for (int i = 0; i < size; i++)
			bit.set(i, this.get(i + position));
		return bit;
	}

	public Bits appendFromArray(final byte[] msgs) {
		Bits b = fromArray(msgs);
		return this.append(b);
	}
	
	public Bits append(Bits bits) {
		int baseLength = length; 
		for (int i = 0; i < bits.length(); i++) {
			this.set(baseLength + i, bits.get(i));
			length++;
		}
		return this;
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
		byte [] bytes = new byte[length/BYTE_SIZE]; 
		for(int i=0;i<length/BYTE_SIZE;i++) {
			bytes[i] = (byte) subbit(i*BYTE_SIZE, BYTE_SIZE).toInt();
		}
		return bytes;
	}
}
