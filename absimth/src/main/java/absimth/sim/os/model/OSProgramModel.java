package absimth.sim.os.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OSProgramModel {
	private String name;
	private Integer programId;
	private Integer initialAddress;
	private Integer stackSize;
	private Integer instructionLenght;
	
	public void incInstructionLenght() {
		this.instructionLenght++;
	}
}
