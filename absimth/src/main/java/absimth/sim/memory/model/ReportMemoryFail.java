package absimth.sim.memory.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportMemoryFail {
	private FaultAddressModel faultAddress;
	private MemoryFaultType faultType;
	
}
