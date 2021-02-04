package absimth.sim.os;

import absimth.module.cpu.riscv32.module.RV32CPUState;
import absimth.sim.SimulatorManager;
import absimth.sim.cpu.ICPU;
import lombok.Getter;

public class OSProgramExecutor {
	private RV32CPUState state;
	private int initialAddress;
	private int programLength = 0;
	@Getter
	private String name;
	private int programId;
	private int stackSize;
	// TODO retirar CPU ?
	private ICPU cpu;

	private boolean instructionMode;

	public OSProgramExecutor(String name, int programId, ICPU cpu, int initialAddress, int stackSize) {
		this.programId = programId;
		this.name = name;
		this.cpu = cpu;
		this.instructionMode = true;
		this.initialAddress = initialAddress;
		this.stackSize = stackSize;
		
		state = RV32CPUState.builder()
				.pc(0)
				.prevPc(0)
				.reg(new int[32])
				.build();
	}

	public boolean isRunningApp() {
		if (instructionMode)
			return true;
		return  programLength > 0 && cpu.getPc() >= 0;
	}

	public boolean inInstructionMode() {
		return instructionMode;
	}
	
	public void executeNextInstruction() throws Exception {
		if (instructionMode) {
			SimulatorManager.getSim().setInInstructionMode(true);
			cpu.initializeRegisters(stackSize, initialAddress);
			int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
			cpu.getMemory().storeWord(programLength * 4, data[programLength]);
			programLength++;
			if (programLength >= data.length) {
				instructionMode = false;
			}
		} else {
			SimulatorManager.getSim().setInInstructionMode(false);
			SimulatorManager.getSim().getReport().incReadInstruction(SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getWorldSize());
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

	public void saveState() {
		state = RV32CPUState.builder()
				.pc(cpu.getPc())
				.prevPc(cpu.getPrevPc())
				.reg(cpu.getReg().clone())
				.build();
	}

	public void loadState() {
		cpu.setPc(state.getPc());
		cpu.setPrevPc(state.getPrevPc());
		cpu.setReg(state.getReg().clone());
	}

	@Override
	public String toString() {
		return "OSProgramExecutor [initialAddress=" + initialAddress + ", name=" + name
				+ ", programId=" + programId + ", cpu=" + cpu + ", instructionMode=" + instructionMode + "]";
	}

}
