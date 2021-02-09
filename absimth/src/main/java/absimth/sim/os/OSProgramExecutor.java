package absimth.sim.os;

import absimth.module.cpu.riscv32.module.RV32CPUState;
import absimth.sim.SimulatorManager;
import absimth.sim.cpu.ICPU;
import absimth.sim.os.model.OSProgramModel;
import lombok.Getter;

public class OSProgramExecutor {
	private RV32CPUState state;
	// TODO retirar CPU ?
	private ICPU cpu;
	@Getter
	private OSProgramModel program;
	private boolean instructionMode;

	public OSProgramExecutor(OSProgramModel program, ICPU cpu) {
		this.program = program;
		this.cpu = cpu;
		this.instructionMode = true;
		
		state = RV32CPUState.builder()
				.pc(0)
				.prevPc(0)
				.reg(new int[32])
				.build();
	}

	public boolean isRunningApp() {
		if (instructionMode)
			return true;
		return program.getInstructionLenght() > 0 && cpu.getPc() >= 0;
	}

	public boolean inInstructionMode() {
		return instructionMode;
	}
	
	public void executeNextInstruction() throws Exception {
		if (instructionMode) {
			SimulatorManager.getSim().setInInstructionMode(true);
			cpu.initializeRegisters(program.getStackSize(), program.getInitialAddress());
			int[] data = program.getData();
			cpu.getMemory().storeWord(program.getInstructionLenght() * 4, data[program.getInstructionLenght()]);
			program.incInstructionLenght();
			if (program.getInstructionLenght() >= data.length) {
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
		return "OSProgramExecutor [state=" + state + ", cpu=" + cpu + ", program=" + program + ", instructionMode="
				+ instructionMode + "]";
	}

}
