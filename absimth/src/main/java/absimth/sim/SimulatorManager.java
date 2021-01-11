package absimth.sim;

import absimth.module.C2H.C2HMemoryController;
import absimth.sim.cpu.riscv32i.RV32ICpu;
import absimth.sim.gui.guiController;
import lombok.Getter;

public class SimulatorManager {
	private static SimulatorManager simManager = new SimulatorManager();
	//TODO IMPROVE IT
	//I can run 2 program.. in this memory
	private Memory memory = new Memory((guiController.MEMORY_SIZE*2) / 4 + 1, Bits.WORD_LENGTH);
	@Getter
	private Report report = new Report();
	@Getter
	private RV32ICpu cpu;
	
	public void resetCPU() {
		cpu = new RV32ICpu();
	}
	
	public static SimulatorManager getSim() {
		return simManager;
	}
	
	//TODO CREATE CONFIG	
	public IMemoryController getMemoryController() {
		return new C2HMemoryController();
	}
	
	public Memory getMemory() {
		return memory;
	}
	
}
