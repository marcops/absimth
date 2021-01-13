package absimth.sim.os;

import java.util.ArrayList;
import java.util.List;

import absimth.sim.cpu.riscv32i.RV32ICpu;

public class OSCpuExecutor {
	private List<OSProgramExecutor> lstExecutor = new ArrayList<>();
	private RV32ICpu cpu = new RV32ICpu();
	private OSProgramExecutor currentProgram;
	
	public int getRegister(int pos) {
		return cpu.getReg()[pos];
	}

	public void add(String name, int pogramId) {
		lstExecutor.add(new OSProgramExecutor(name, pogramId, cpu));
		currentProgram = lstExecutor.get(0);
	}

	public void executeNextInstruction() {
		currentProgram.executeNextInstruction();
	}

	public boolean isRunningApp() {
		return currentProgram.isRunningApp();
	}

	public int getPreviousPC() {
		return currentProgram.getPreviousPC();
	}

	public int getProgramLength() {
		return currentProgram.getProgramLength();
	}

	public boolean inInstructionMode() {
		return currentProgram.inInstructionMode();
	}
	
	//TODO REMOVER DAQUI
	public String getMemoryStr(int register) {
		return cpu.getMemory().getString(register);
	}

	public int getInitialAddress() {
		return currentProgram.getInitialAddress();
	}

}
