package absimth.sim.utils;

public class HexaFormat {
	public static String f(long value) {
		return f(value, Bits.BYTE_SIZE);
	}
	public static String f(int value) {
		return f((long)value);
	}

	public static String f(long value, int size) {
		String format = "0x%0" + size + "x";
		return String.format(format, value);
	}
}
