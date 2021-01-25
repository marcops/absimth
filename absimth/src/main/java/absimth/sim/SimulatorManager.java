package absimth.sim;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import absimth.sim.configuration.ConfigurationService;
import absimth.sim.configuration.model.AbsimthConfigurationModel;
import absimth.sim.configuration.model.ProgramModel;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.memory.IMemoryController;
import absimth.sim.memory.Memory;
import absimth.sim.os.OperationalSystem;
import absimth.sim.utils.AbsimLog;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

public class SimulatorManager {
	private static SimulatorManager simManager = new SimulatorManager();
	private Memory memory;
	
	private IFaultInjection faultMode;
	private IMemoryController memoryController;
	@Getter
	private Report report = new Report();
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
				AbsimLog.logView("ignorating program name="+program.getName()+", at cpu=" + program.getCpu());
		}
		
		validateModules();
	}

	private void validateModules() throws Exception {
		getMemoryController();
		getFaultMode();
		getMemory();
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
	
	//look this bootstrap.asm
//	public static final int STACK_POINTER_RISCV = 0x100000;
//	public static final int STACK_POINTER_PROGRAM_SIZE = STACK_POINTER_RISCV/4;
	
	
	public static <T> T instantiate(final String className, final Class<T> type) throws Exception {
		return type.cast(Class.forName(className).getConstructor().newInstance());
	}
	
	public IMemoryController getMemoryController() throws Exception {
		if(memoryController == null)
			memoryController = instantiate("absimth.module.memoryController." +absimthConfiguration.getModules().getMemoryController(), IMemoryController.class);
		return memoryController;
	}
	
	public IFaultInjection getFaultMode() throws Exception {
		if(faultMode == null) 
			faultMode = instantiate("absimth.module.memoryFaultInjection." +absimthConfiguration.getModules().getMemoryFaultInjection(), IFaultInjection.class);
		return faultMode;
	}

	public Memory getMemory() {
		if(memory == null) memory = new Memory(absimthConfiguration.getHardware().getMemory().getTotalOfAddress(), absimthConfiguration.getHardware().getMemory().getWorldSize());
		return memory;
	}

}
