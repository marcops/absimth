package absimth.sim.os.model;

import java.util.Arrays;

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
	private Integer instructionLength;
	private Integer totalOfMemoryUsed;
	private int[] data;
	private int cpu;
	private int task;
	
	public void incInstructionLength() {
		this.instructionLength++;
	}
	
	public String toCpuTimeline() {
		return String.format("[%02d] %s", task, name);
	}
	
	public String toReport() {
		return name + "\r\n  programId=" + programId 
				+ "\r\n  initialAddress=" + HexaFormat.f(initialAddress)
				+ "\r\n  stackSize=" + HexaFormat.f(stackSize) 
				+ "\r\n  instructionLentgh=" + HexaFormat.f(instructionLength)
				+ "\r\n  totalOfMemoryUsed=" + HexaFormat.f(totalOfMemoryUsed)
				+ "\r\n  cpu=" + cpu
				+ "\r\n  task=" + task;
	}


	@Override
	public String toString() {
		return "OSProgramModel [name=" + name + ", programId=" + programId + ", initialAddress=" + initialAddress
				+ ", stackSize=" + stackSize + ", instructionLength=" + instructionLength + ", totalOfMemoryUsed="
				+ totalOfMemoryUsed + ", data=" + Arrays.toString(data) + ", cpu=" + cpu + ", task=" + task + "]";
	}

}
