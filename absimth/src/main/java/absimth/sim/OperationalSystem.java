package absimth.sim;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import absimth.sim.cpu.riscv32i.RV32ICpu;
import absimth.sim.gui.guiController;

public class OperationalSystem {
	private static OperationalSystem os = new OperationalSystem();

	private int programLength = 0;
	private RV32ICpu currentCpu = null;

	/*
	 * TODO TMP REMOVE IT
	 */
	public String getMemoryStr(int address) {
		return currentCpu.getMemory().getString(address);
	}

	public int getRegister(int pos) {
		// improve?
		return currentCpu.getReg()[pos];
	}
	/*
	 * END TMP REMOVE IT
	 */

	public static OperationalSystem getOS() {
		return os;
	}

	public int getProgramLength() {
		return programLength;
	}

	public boolean isRunningApp() {
		return programLength > 0 && currentCpu.getPc() >= 0;
	}

	public void startApp(File f) throws IOException {
		SimulatorManager.getSim().resetCPU();
		currentCpu = SimulatorManager.getSim().getCpu();
		currentCpu.setReg2(guiController.MEMORY_SIZE);
		programLength = loadInstructions(f);
	}

	public void stopApp() {
		programLength = 0;
		currentCpu = null;
	}

	public void executeNextInstruction() {
		currentCpu.executeInstruction();
	}

	public int getPreviousPC() {
		return currentCpu.getPrevPc();
	}

	/**
	 * Adds instructions from binary file to program array
	 * 
	 * @param f: A RISC-V binary file
	 * @return Array of parsed instructions
	 * @throws IOException Throws exception if file is busy
	 */
	private int loadInstructions(File f) throws IOException {
		try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
			int len = (int) f.length() / 4; // Number of instructions
			for (int i = 0; i < len; i++) {
				int data = Integer.reverseBytes(dis.readInt());
				currentCpu.getMemory().storeWord(i * 4, data);
			}
			return len;
		}
	}
}
