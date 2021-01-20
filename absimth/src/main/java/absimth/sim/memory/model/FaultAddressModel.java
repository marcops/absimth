package absimth.sim.memory.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaultAddressModel {
	private long address;
	private int position;
}
