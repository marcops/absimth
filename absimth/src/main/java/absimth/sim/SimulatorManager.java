package absimth.sim;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import absimth.module.C2H.C2HMemoryController;
import absimth.sim.configuration.ConfigurationService;
import absimth.sim.configuration.model.AbsimthConfigurationModel;
import absimth.sim.configuration.model.ProgramModel;
import absimth.sim.os.OperationalSystem;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

public class SimulatorManager {
	private static SimulatorManager simManager = new SimulatorManager();
	
	@Getter
	@Setter
	private TextArea textAreaToLog;
	@Getter
	private AbsimthConfigurationModel absimthConfiguration;
	@Getter
	private OperationalSystem os = new OperationalSystem();
	@Getter
	private HashMap<String, int[]> binaryPrograms = new HashMap<>();
	
	
	public static SimulatorManager getSim() {
		return simManager;
	}

	public void load(String path, String name) throws Exception {
		absimthConfiguration = ConfigurationService.load(path + name);
		final String EXTENSION = ".bin";
		
		List<ProgramModel> programs = absimthConfiguration.getRun().getPrograms();
		for (int i = 0; i < programs.size(); i++) {
			ProgramModel program = programs.get(i);
			binaryPrograms.put(program.getName(), loadInstructions(path + program.getName() + EXTENSION));
			if (program.getCpu() < absimthConfiguration.getHardware().getCpu().getAmount())
				os.add(program.getCpu(), program.getName(), i);
			else
				System.err.println("ignorating program name="+program.getName()+", at cpu=" + program.getCpu());
			
			
		}
	}

	/**
	 * Adds instructions from binary file to program array
	 * 
	 * @param f: A RISC-V binary file
	 * @return Array of parsed instructions
	 * @throws IOException Throws exception if file is busy
	 */
	private static int[] loadInstructions(String filename) throws IOException {
		File f = new File(filename);
		try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
			int len = (int) f.length() / 4; // Number of instructions
			int[] data = new int[len];
			for (int i = 0; i < len; i++) {
				data[i] = Integer.reverseBytes(dis.readInt());
			}
			return data;
		}
	}
	
	// REVIEW IT
	// TODO IMPROVE IT
	// I can run 2 program.. in this memory
	//TODO IMPROVE IT
	//10485760 stack size padrao?
	public static final int STACK_POINTER_RISCV = 0x100000;
	public static final int STACK_POINTER_PROGRAM_SIZE = STACK_POINTER_RISCV/4;
	private final static int NUMBER_OF_PROGRAMS_TO_RUN = 10;
	private final static int GLOBAL_MEMORY_NUMBER_OF_ADDRESS = STACK_POINTER_PROGRAM_SIZE * NUMBER_OF_PROGRAMS_TO_RUN;
	
	private Memory memory = new Memory(GLOBAL_MEMORY_NUMBER_OF_ADDRESS, Bits.WORD_LENGTH);
	@Getter
	private Report report = new Report();
	
	// TODO CREATE CONFIG
	public IMemoryController getMemoryController() {
		return new C2HMemoryController();
	}

	public Memory getMemory() {
		return memory;
	}

}
