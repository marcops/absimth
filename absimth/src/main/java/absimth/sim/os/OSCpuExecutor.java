package absimth.sim.os;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import absimth.sim.SimulatorManager;
import absimth.sim.cpu.riscv32i.RV32ICpu;
import absimth.sim.utils.AbsimLog;

public class OSCpuExecutor {
	private List<OSProgramExecutor> lstExecutor = new ArrayList<>();
	private int currentProgramId = -1;

	private RV32ICpu RV32ICpu = new RV32ICpu();
	private OSProgramExecutor currentProgram;
	private int numberOfCyclesExecuted = 0;
	private int cpuId;

	public OSCpuExecutor(Integer cpu) {
		this.cpuId = cpu;
	}

	public int getRegister(int pos) {
		return RV32ICpu.getReg()[pos];
	}

	public List<String> getListOfProgramsName() {
		return lstExecutor.stream().map(OSProgramExecutor::getName).collect(Collectors.toList());
	}
	
	public void add(String name, int pogramId, int initialAddress) {
		lstExecutor.add(new OSProgramExecutor(name, pogramId, RV32ICpu, initialAddress));
	}

	public void executeNextInstruction() {
		// create a startup func?
		if (currentProgram == null) currentProgram = getNextProgram();
		if (currentProgram == null) return;

		if (numberOfCyclesExecuted >= SimulatorManager.getSim().getAbsimthConfiguration().getRun().getCyclesByProgram()) {
			currentProgram.saveState();
			currentProgram = getNextProgram();
			if (currentProgram == null)
				return;
			currentProgram.loadState();
			numberOfCyclesExecuted=0;
//			System.out.println("---- " + currentProgram.getName() + "---- ");
		}
		
		if(!currentProgram.inInstructionMode())
			AbsimLog.cpu(cpuId, RV32ICpu.toString());
		
		currentProgram.executeNextInstruction();
		numberOfCyclesExecuted++;
	}

	public String getProgramName() {
		if(currentProgram == null) return "";
		return currentProgram.getName();
	}
	
	private OSProgramExecutor getNextProgram() {
		currentProgramId++;
		if (currentProgramId >= lstExecutor.size())
			currentProgramId = 0;
		for(int i = currentProgramId; i< lstExecutor.size();i++) {
			if(lstExecutor.get(i).isRunningApp()) {
				currentProgramId=i;
				return lstExecutor.get(i);
			}
		}
		if(currentProgramId>0) {
			for (int i = 0; i < currentProgramId && i< lstExecutor.size(); i++) {
				if(lstExecutor.get(i).isRunningApp()) {
					currentProgramId=i;
					return lstExecutor.get(i);
				}
			}
		}
		return null;
//		if (lstExecutor.isEmpty())
//			return null;
//		if (currentProgramId >= lstExecutor.size())
//			currentProgramId = 0;
		
		//if(!lstExecutor.get(currentProgramId).isRunningApp())
//		return lstExecutor.get(currentProgramId);
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
		return currentProgram.getProgramLength();
	}

	public boolean inInstructionMode() {
		if(currentProgram == null) return true;
		return currentProgram.inInstructionMode();
	}

	// TODO REMOVER DAQUI
	public String getMemoryStr(int register) {
		return RV32ICpu.getMemory().getString(register);
	}

	public int getInitialAddress() {
		if(currentProgram == null) return 0;
		return currentProgram.getInitialAddress();
	}

	public boolean changeProgramRunning(String selectedItem) {
		if(selectedItem == null) return false;
		for(int i=0;i<lstExecutor.size();i++) {
			OSProgramExecutor osExec = lstExecutor.get(i);
			if(selectedItem.equals(osExec.getName())) {
				currentProgram = osExec;
				numberOfCyclesExecuted = 0;
				return true;
			}
		}
		currentProgram = null;
		return false;
	}

}
