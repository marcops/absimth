package absimth.sim.os;

import absimth.sim.SimulatorManager;
import absimth.sim.cpu.riscv32i.RV32ICpu;

public class OSProgramExecutor {
	private int programLength = 0;
	private String name;
	private int programId;

	// TODO retirar CPU ?
	private RV32ICpu cpu;

	private boolean instructionMode;

	public OSProgramExecutor(String name, int programId, RV32ICpu cpu) {
		this.programId = programId;
		this.name = name;
		this.cpu = cpu;
		this.instructionMode = true;
	}

	public boolean isRunningApp() {
		if (instructionMode)
			return true;
		return programLength > 0 && cpu.getPc() >= 0;
	}

	public boolean inInstructionMode() {
		return instructionMode;
	}
	
	public void executeNextInstruction() {
		cpu.setContext(programId);
		if (instructionMode) {
			int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
			cpu.getMemory().store(programLength * 4, data[programLength]);
			programLength++;
			if (programLength >= data.length) {
				instructionMode = false;
			}
		} else {
			cpu.executeInstruction();
		}
	}

	public int getPreviousPC() {
		return cpu.getPrevPc();
	}

	public int getProgramLength() {
		return programLength;
	}

	public int getInitialAddress() {
		return programId*SimulatorManager.STACK_POINTER_PROGRAM_SIZE;
	}

}
