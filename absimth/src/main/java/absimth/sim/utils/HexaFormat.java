package absimth.sim.utils;

public class HexaFormat {
	public static String f(int value) {
		return f(value, Bits.BYTE_SIZE);
	}

	public static String f(int value, int i) {
		String format = "0x%0" + i + "x";
		return String.format(format, value);
	}
}
