package absimth.sim.os.model;

import absimth.sim.utils.HexaFormat;
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
	private Integer totalOfMemoryUsed;
	private int[] data;
	
	public void incInstructionLenght() {
		this.instructionLenght++;
	}

	@Override
	public String toString() {
		return "OSProgramModel [name=" + name + ", programId=" + programId + ", initialAddress=" + initialAddress
				+ ", stackSize=" + stackSize + ", instructionLenght=" + instructionLenght + ", totalOfMemoryUsed="
				+ totalOfMemoryUsed + "]";
	}
	
	public String toReport() {
		return name + "\r\n  programId=" + programId 
				+ "\r\n  initialAddress=" + HexaFormat.f(initialAddress)
				+ "\r\n  stackSize=" + HexaFormat.f(stackSize) 
				+ "\r\n  instructionLenght=" + HexaFormat.f(instructionLenght)
				+ "\r\n  totalOfMemoryUsed=" + HexaFormat.f(totalOfMemoryUsed);
	}

}
