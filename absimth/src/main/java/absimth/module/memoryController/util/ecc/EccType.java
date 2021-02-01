package absimth.module.memoryController.util.ecc;

public enum EccType {
	NONE(null), 
	CRC8(new CRC8()),
	HAMMING_SECDEC(new Hamming());

	private final IEccType encode;

	EccType(final IEccType newValue) {
		encode = newValue;
	}

	public IEccType getEncode() {
		return encode;
	}

}
