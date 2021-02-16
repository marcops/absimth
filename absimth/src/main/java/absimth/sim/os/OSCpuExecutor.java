package absimth.sim.os;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import absimth.sim.SimulatorManager;
import absimth.sim.cpu.ICPU;
import absimth.sim.os.model.OSProgramModel;
import absimth.sim.utils.AbsimLog;
import lombok.Getter;

public class OSCpuExecutor {
	private List<OSProgramExecutor> lstExecutor = new ArrayList<>();
	private int currentProgramId = -1;

	@Getter
	private ICPU ICPU;
	private OSProgramExecutor currentProgram;
	private int numberOfCyclesExecuted = 0;
	private int totalOfCyclesExecuted = 0;
	private int cpuId;

	public OSCpuExecutor(Integer cpu) {
		this.cpuId = cpu;
		ICPU = SimulatorManager.getSim().getLstCpu().get(cpu);
	}

	public int getRegister(int pos) {
		return ICPU.getReg()[pos];
	}

	public List<String> getListOfProgramsName() {
		return lstExecutor.stream().map(x->x.getProgram().getName()).collect(Collectors.toList());
	}
	
	public void add(OSProgramModel program) {
		program.setTask(lstExecutor.size());
		lstExecutor.add(new OSProgramExecutor(program, ICPU));
	}

	public void executeNextInstruction() throws Exception {
		// create a startup func?
		if (currentProgram == null) {
			currentProgram = getNextProgram();
		}
		if (currentProgram == null) return;
		

		if (numberOfCyclesExecuted >= SimulatorManager.getSim().getAbsimthConfiguration().getRun().getCyclesByProgram()) {
			currentProgram.saveState();
			currentProgram = getNextProgram();
			if (currentProgram == null) return;
			currentProgram.loadState();
			numberOfCyclesExecuted=0;
		}
		
		currentProgram.executeNextInstruction();
		
		if(!currentProgram.inInstructionMode())
			AbsimLog.cpu(cpuId, ICPU.toString());
		
		numberOfCyclesExecuted++;
		totalOfCyclesExecuted++;
	}

	public String getProgramName() {
		if(currentProgram == null) return "";
		return currentProgram.getProgram().getName();
	}
	
	private OSProgramExecutor getNextProgram() {
		currentProgramId++;
		if (currentProgramId >= lstExecutor.size())
			currentProgramId = 0;
		for(int i = currentProgramId; i< lstExecutor.size();i++) {
			if(lstExecutor.get(i).isRunningApp()) {
				currentProgramId=i;
				SimulatorManager.getSim().getReport().getCpu().getTimeline(cpuId).getEntries().put(totalOfCyclesExecuted, lstExecutor.get(i).getProgram().toCpuTimeline());
				SimulatorManager.getSim().getReport().getCpu().getTimeline(cpuId).setEnd(totalOfCyclesExecuted);
				return lstExecutor.get(i);
			}
		}
		if(currentProgramId>0) {
			for (int i = 0; i < currentProgramId && i< lstExecutor.size(); i++) {
				if(lstExecutor.get(i).isRunningApp()) {
					currentProgramId=i;
					
					SimulatorManager.getSim().getReport().getCpu().getTimeline(cpuId).getEntries().put(totalOfCyclesExecuted, lstExecutor.get(i).getProgram().toCpuTimeline());
					SimulatorManager.getSim().getReport().getCpu().getTimeline(cpuId).setEnd(totalOfCyclesExecuted);
					return lstExecutor.get(i);
				}
			}
		}
		SimulatorManager.getSim().getReport().getCpu().getTimeline(cpuId).setEnd(totalOfCyclesExecuted);
		return null;
	}

	public boolean isEmpty() {
		return lstExecutor.isEmpty();
	}
	
	public boolean isRunningApp() {
		while(true) {
			if(currentProgram == null) {
				currentProgram = getNextProgram();
				if(currentProgram == null) return false;
				if(!currentProgram.isRunningApp()) return false;
			} 
			if(currentProgram.isRunningApp()) return true;
			//if(currentProgramId>0 && currentProgramId<lstExecutor.size()) 
//			lstExecutor.remove(currentProgramId);
			currentProgram = getNextProgram();
			if(currentProgram == null) return false;
			currentProgram.loadState();
		}
	}

	public int getPreviousPC() {
		if(currentProgram == null) return 0;
		return currentProgram.getPreviousPC();
	}

	public int getProgramLength() {
		if(currentProgram == null) return 0;
		return currentProgram.getProgram().getInstructionLength();
	}

	public boolean inInstructionMode() {
		if(currentProgram == null) return true;
		return currentProgram.inInstructionMode();
	}

	// TODO REMOVER DAQUI
	public String getMemoryStr(int register) throws Exception {
		return ICPU.getMemory().getString(register);
	}

	//TODO REMOVER E ENCAMINHAR O PROGRAM...
	public int getInitialAddress() {
		if(currentProgram == null) return 0;
		return currentProgram.getProgram().getInitialAddress();
	}

	public boolean changeProgramRunning(String selectedItem) {
		if(selectedItem == null) return false;
		for(int i=0;i<lstExecutor.size();i++) {
			OSProgramExecutor osExec = lstExecutor.get(i);
			if(selectedItem.equals(osExec.getProgram().getName())) {
				currentProgram = osExec;
				numberOfCyclesExecuted = 0;
				return true;
			}
		}
		currentProgram = null;
		return false;
	}

}
