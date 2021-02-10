package absimth.sim.configuration.model.hardware.memory;

public enum ChannelMode {
	SINGLE_CHANNEL(1), DUAL_CHANNEL(2), QUAD_CHANNEL(4);

	private final int value;

	ChannelMode(final int newValue) {
		value = newValue;
	}

	public int getValue() {
		return value;
	}

}
