package absimth.sim.os;

import absimth.sim.SimulatorManager;
import absimth.sim.cpu.riscv32i.RV32ICpu;
import absimth.sim.gui.CPUController;

public class OSProgramExecutor {
	private int programLength = 0;
	private String name;
	private int initialAddress;
	private int memorySize;

	// TODO retirar CPU ?
	private RV32ICpu cpu;

	private boolean instructionMode;

	public OSProgramExecutor(String name, int initialAddress, int memorySize, RV32ICpu cpu) {
		this.memorySize = memorySize;
		this.initialAddress = initialAddress;
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
		if (instructionMode) {
			int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
			cpu.getMemory().storeWord(initialAddress + (programLength * 4), data[programLength]);
			programLength++;
			if (programLength >= data.length) {
				instructionMode = false;
				memorySize = CPUController.MEMORY_SIZE;
				cpu.init(memorySize, initialAddress/4);
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
		return initialAddress;
	}

}
