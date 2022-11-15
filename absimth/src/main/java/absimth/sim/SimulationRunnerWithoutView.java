package absimth.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import absimth.sim.configuration.model.ProgramModel;
import absimth.sim.utils.FileLog;

import java.time.Duration;
import java.time.Instant;

public class SimulationRunnerWithoutView {
	
	private String folder;
	private String filename;
	private String outputFilename;
	private String programsToLoad;
	private String memoryController;
	private String seed;
//	private String faulInjection;
	
	public void init(Map<String, String> config) {
		folder = config.get("folder");
		filename = config.get("filename");
		outputFilename = config.get("outputFilename");
		memoryController = config.get("memoryController");
		programsToLoad = config.get("programsToLoad");
		seed = config.get("seed");
	}

	public void run() throws Exception {
		SimulatorManager.getSim().preLoad(folder, filename);
		if(programsToLoad != null) SimulatorManager.getSim().getAbsimthConfiguration().getRun().setPrograms(getListOfPrograms(programsToLoad));
		if(memoryController != null) SimulatorManager.getSim().getAbsimthConfiguration().getModules().setMemoryController(memoryController);
		if(seed != null) {
			String v = SimulatorManager.getSim().getAbsimthConfiguration().getModules().getMemoryFaultInjection().getConfig();
			v = v.replace("GEN", seed);
			SimulatorManager.getSim().getAbsimthConfiguration().getModules().getMemoryFaultInjection().setConfig(v);
			System.out.println("new seed=" + v);
		}
		//if(faulInjection != null) SimulatorManager.getSim().getAbsimthConfiguration().getModules().setMemoryFaultInjection(faulInjection);
		
		SimulatorManager.getSim().posLoad(folder);
		Instant start = Instant.now();
		boolean deadLock = false;
		while(SimulatorManager.getSim().getOs().executeNextInstruction()) {
			//nothing
			Duration timeElapsed = Duration.between(start, Instant.now());
			if(timeElapsed.toMinutes() >= 4L) {
				deadLock = true;
				break;
			}
			//System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
		}
		
		String msg = "";
		if(deadLock)
			msg = SimulatorManager.getSim().getReport().printReportDead();
		else
			msg = SimulatorManager.getSim().getReport().printReportSmall();
		System.out.println(msg);
		FileLog.report(msg, outputFilename);
//		FileLog.reportCSV(msg, outputFilename);
	}

	private List<ProgramModel> getListOfPrograms(String programs) {
		List<ProgramModel> lst = new ArrayList<>();
		String[] programAndCpu = programs.split(",");
		for (String string : programAndCpu) {
			String[] prog = string.split("-");
			lst.add(ProgramModel.builder()
					.cpu(prog.length == 1 ? 0 : Integer.valueOf(prog[1]))
					.after(false)
					.name(prog[0]).build());
		}
		return lst;
	}

	public void failed() {
		String msg = SimulatorManager.getSim().getReport().printReportDead();
		FileLog.report(msg, outputFilename);
		
	}
}
