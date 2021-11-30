package absimth.sim.memory.model;

import lombok.Data;

@Data
public class MemoryFaultModel {

	private MemoryFaultType faultType;
	private Boolean value;

	public MemoryFaultModel(MemoryFaultType type, Boolean value) {
		this.faultType = type;
		this.value = value;
	}

}
