package absimth.sim.memory.model;

import absimth.sim.utils.Bits;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaultAddressModel {
	private long address;
	private int position;
	
	public int getChip() {
		return position/Bits.BYTE_SIZE;
	}
}
