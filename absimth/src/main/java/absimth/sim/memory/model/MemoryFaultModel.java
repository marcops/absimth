package absimth.sim.memory.model;

import java.util.Set;

import absimth.sim.utils.Bits;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemoryFaultModel {
//	private FaultAddressModel faultAddress;
	private long address;
	private Set<Integer> position;
	private MemoryFaultType faultType;
	
	public boolean hasFaulInThisChip(int pos) {
		int realPos = pos/Bits.BYTE_SIZE;
		for(int i=0;i<Bits.BYTE_SIZE;i++) {
			if(position.contains(i+realPos)) return true;
		}
		return false;
	}
}
