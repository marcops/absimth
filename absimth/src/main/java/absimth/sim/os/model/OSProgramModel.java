package absimth.sim.os.model;

import absimth.sim.SimulatorManager;
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
	private Integer totalOfMemory;
	private Integer initialDynamicAddress;
	private Integer lastDynamicAddress;
//	private Long totalOfTicks;
	private int[] data;
	private int cpuId;
	private int task;
	private boolean sucesuful;
	private boolean finished;
	
//	public void increaseTicks() {
//		if(totalOfTicks == null) totalOfTicks = 0L; 
////		totalOfTicks++;
//	}
	
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
				+ "\r\n - MEMORY"
				+ "\r\n  initialAddress:" + HexaFormat.f(initialAddress)
				+ "\r\n  instructionLength:" + HexaFormat.f(instructionLength)
				+ "\r\n  initialDynamicDataAddress:" + HexaFormat.f((initialAddress)+instructionLength)				
				+ "\r\n  stackSize:" + HexaFormat.f(stackSize)
				+ "\r\n  totalOfMemory:" + HexaFormat.f(totalOfMemory)
				+ "\r\n  lastAddress:" + HexaFormat.f((initialAddress+totalOfMemory))
				+ "\r\n - DYNAMIC MEMORY USED"
				+ "\r\n  initialDynamicMemoryAddressUsed:" + HexaFormat.f(initialDynamicAddress)
				+ "\r\n  totalOfDynamicMemoryAddressUsed:" + HexaFormat.f((lastDynamicAddress-initialDynamicAddress))
				+ "\r\n  lastDynamicMemoryAddressUsed:" + HexaFormat.f(lastDynamicAddress)
				+ "\r\n - CPU"
				+ "\r\n  cpuId:" + cpuId
				+ "\r\n  CPU:" + SimulatorManager.getSim().getLstCpu().get(cpuId).getCpu()
				+ "\r\n  Core:" + SimulatorManager.getSim().getLstCpu().get(cpuId).getCore()
				+ "\r\n  cpuType:" + SimulatorManager.getSim().getLstCpu().get(cpuId).getName()
				//TODO
//				+ "\r\n  totalOfTicks=" + totalOfTicks
				+ "\r\n - OTHERS"
				+ "\r\n  task:" + task
				+ "\r\n  sucesuful:" + sucesuful
				+ "\r\n - MEMORY STATUS"
				+ "\r\n " + SimulatorManager.getSim().getMemoryController().getMemoryStatus().print(getId());
	}

	public void setDynamicAddress(int vAdd) {
		if(vAdd < initialDynamicAddress && vAdd > (initialAddress+(instructionLength*4)))
			initialDynamicAddress = vAdd;
		if (vAdd > lastDynamicAddress)
			lastDynamicAddress = vAdd;
	}


//	@Override
//	public String toString() {
//		return "OSProgramModel [name=" + name + ", programId=" + programId + ", initialAddress=" + initialAddress
//				+ ", stackSize=" + stackSize + ", lastAddress=" + (initialAddress+totalOfMemoryUsed) + ", instructionLength=" + instructionLength + ", totalOfMemoryUsed="
//				+ ", totalOfTicks=" + totalOfTicks 
//				+ totalOfMemoryUsed + ", data=" + Arrays.toString(data) + ", cpu=" + cpu + ", task=" + task + ", sucesuful=" + sucesuful + "]";
//	}

}
