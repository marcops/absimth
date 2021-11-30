package absimth.sim.gui.model;

import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cell3DInfoModel {
	private String text;
	private ECCMemoryFaultType status;
	private PhysicalAddress physicalAddress;
}
