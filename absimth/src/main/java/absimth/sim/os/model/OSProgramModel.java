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
	private Long totalOfTicks;
	private int[] data;
	private int cpu;
	private int task;
	private boolean sucesuful;
	
	public void increaseTicks() {
		if(totalOfTicks == null) totalOfTicks = 0L; 
		totalOfTicks++;
	}
	
	public String getId() {
		return name+"_"+programId;
	}
	
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
				+ "\r\n  instructionLength=" + HexaFormat.f(instructionLength)
				+ "\r\n  totalOfMemoryUsed=" + HexaFormat.f(totalOfMemoryUsed)
				+ "\r\n  totalOfTicks=" + totalOfTicks
				+ "\r\n  cpu=" + cpu
				+ "\r\n  task=" + task
				+ "\r\n  sucesuful=" + sucesuful;
	}


	@Override
	public String toString() {
		return "OSProgramModel [name=" + name + ", programId=" + programId + ", initialAddress=" + initialAddress
				+ ", stackSize=" + stackSize + ", instructionLength=" + instructionLength + ", totalOfMemoryUsed="
				+ ", totalOfTicks=" + totalOfTicks 
				+ totalOfMemoryUsed + ", data=" + Arrays.toString(data) + ", cpu=" + cpu + ", task=" + task + ", sucesuful=" + sucesuful + "]";
	}

}
