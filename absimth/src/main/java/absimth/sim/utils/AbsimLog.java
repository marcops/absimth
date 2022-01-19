package absimth.sim.utils;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.LogModel;
import javafx.scene.control.TextArea;

public class AbsimLog {

	public static void cpu(int cpu, String msg) {
		String message = String.format("[CPU%02d] %s\r\n", cpu, msg);
		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isCpu())
			FileLog.append(message, false);
	}

	public static void fatal(String msg) {
		String message = "[FATAL] " + msg + "\r\n";
		logView(message);
	}

	public static void memory(String msg) {
		String message = "[MEM] " + msg + "\r\n";
		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isMemory())
			FileLog.append(message, false);
	}

	public static void instruction(String msg) {
		String message = "[INS] " + msg + "\r\n";
		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isCpuInstruction())
			FileLog.append(message, false);
	}

	public static void logView(String msg) {
		FileLog.append(msg, true);
		TextArea textAreaToLog = SimulatorManager.getSim().getTextAreaToLog();
		if (textAreaToLog != null)
			textAreaToLog.appendText(msg);
	}

	public static void memoryController(String msg) {
		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isMemoryController())
			FileLog.append(msg, false);
	}

}
