package absimth.sim.os;

import absimth.sim.SimulatorManager;
import absimth.sim.cpu.riscv32i.RV32ICPUState;
import absimth.sim.cpu.riscv32i.RV32ICpu;
import lombok.Getter;

public class OSProgramExecutor {
	private RV32ICPUState state;
	private int initialAddress;
	private int programLength = 0;
	@Getter
	private String name;
	private int programId;

	// TODO retirar CPU ?
	private RV32ICpu cpu;

	private boolean instructionMode;

	public OSProgramExecutor(String name, int programId, RV32ICpu cpu, int initialAddress) {
		this.programId = programId;
		this.name = name;
		this.cpu = cpu;
		this.instructionMode = true;
		this.initialAddress = initialAddress;
		
		state = RV32ICPUState.builder()
				.pc(0)
				.prevPc(0)
				.reg(new int[32])
				.build();
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
		cpu.setInitialAddress(initialAddress);
		if (instructionMode) {
			int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
			cpu.getMemory().store(programLength * 4, data[programLength]);
			programLength++;
			if (programLength >= data.length) {
				instructionMode = false;
				cpu.setReg2();
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

	public void saveState() {
		state = RV32ICPUState.builder()
				.pc(cpu.getPc())
				.prevPc(cpu.getPrevPc())
				.reg(cpu.getReg().clone())
				.build();
	}

//	private int[] regClone(int reg[]) {
//		int[] nreg = new int[reg.length]; 
//		for(int i=0;i<reg.length;i++) {
//			nreg
//		}
//		return nreg;
//	}
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
