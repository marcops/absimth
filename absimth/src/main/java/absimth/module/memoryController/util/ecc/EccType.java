package absimth.module.memoryController.util.ecc;

public enum EccType {
	NONE(new None()),
	PARITY(new Parity()),
	CRC8(new CRC8()),
	HAMMING_SECDEC(new Hamming()),
	REED_SOLOMON(new ReedSolomon()),
	LPC(new LPC_64());

	private final IEccType encode;
	private static final EccType[] values = values();
	
	EccType(final IEccType newValue) {
		encode = newValue;
	}

	public IEccType getEncode() {
		return encode;
	}
	
	public EccType getNext() {
		int position = ordinal() + 1;
		return position < values.length ? values[position % values.length] : values[values.length - 1];
	}

}
