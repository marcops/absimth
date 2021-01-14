package absimth.sim.os;

import java.util.ArrayList;
import java.util.List;

import absimth.sim.cpu.riscv32i.RV32ICpu;

public class OSCpuExecutor {
	private List<OSProgramExecutor> lstExecutor = new ArrayList<>();
	private int currentProgramId = -1;

	private RV32ICpu cpu = new RV32ICpu();
	private OSProgramExecutor currentProgram;
	private static final int NUMBER_OF_CYCLES = 10;
	private int numberOfCyclesExecuted = 0;

	public int getRegister(int pos) {
		return cpu.getReg()[pos];
	}

	public void add(String name, int pogramId, int initialAddress) {
		lstExecutor.add(new OSProgramExecutor(name, pogramId, cpu, initialAddress));
	}

	public void executeNextInstruction() {
		// create a startup func?
		if (currentProgram == null) currentProgram = getNextProgram();
		if (currentProgram == null) return;

		if (numberOfCyclesExecuted > NUMBER_OF_CYCLES) {
			currentProgram.saveState();
			currentProgram = getNextProgram();
			if (currentProgram == null)
				return;
			currentProgram.loadState();
			numberOfCyclesExecuted=0;
			System.out.println("---- " + currentProgram.getName() + "---- ");
		}
		
		currentProgram.executeNextInstruction();
		numberOfCyclesExecuted++;
	}

	public String getProgramName() {
		if(currentProgram == null) return "";
		return currentProgram.getName();
	}
	
	private OSProgramExecutor getNextProgram() {
		currentProgramId++;
		if (lstExecutor.isEmpty())
			return null;
		if (currentProgramId >= lstExecutor.size())
			currentProgramId = 0;
		
		//if(!lstExecutor.get(currentProgramId).isRunningApp())
		return lstExecutor.get(currentProgramId);
	}

	public boolean isRunningApp() {
		while(true) {
			if(currentProgram == null) return false;
			if(currentProgram.isRunningApp()) return true;
			lstExecutor.remove(currentProgramId);
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
		return cpu.getMemory().getString(register);
	}

	public int getInitialAddress() {
		return currentProgram.getInitialAddress();
	}

}
