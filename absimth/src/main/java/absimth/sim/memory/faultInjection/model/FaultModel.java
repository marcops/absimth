package absimth.sim.memory.faultInjection.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaultModel {

	private long address;
	private int position;
}
