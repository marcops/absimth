package absimth.sim.utils;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.LogModel;

public class AbsimLog {

//	public static void cpu(int cpu, String program, String msg) {
//		String message = String.format("[CPU%02d - %s] %s\r\n", cpu, program, msg);
//		
//		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
//		boolean log = l != null ? l.getCpu() : true;
//		if(log) log(message);
//	}
	public static void cpu(int cpu, String msg) {
		String message = String.format("[CPU%02d] %s\r\n", cpu, msg);

		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isCpu())
			log(message);
	}

	public static void memory(String msg) {
		String message = "[MEM] " + msg + "\r\n";
		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isMemory())
			log(message);
	}

	public static void instruction(String msg) {
		String message = "[INS] " + msg + "\r\n";
		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isCpuInstruction())
			log(message);
	}

//	public static void other(String msg) {
//		String message = "OTHERS=" + msg;
//		Boolean log = SimulatorManager.getSim().getAbsimthConfiguration().getLog().getMemory();
//		if(log != null && log) log(message);
//	}

	private static void log(String msg) {
		SimulatorManager.getSim().getTextAreaToLog().appendText(msg);
	}

	public static void other(String msg) {
		LogModel l = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		if (l.isOther())
			log(msg);
	}

}
