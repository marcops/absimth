package absimth.sim;

import java.util.Map;

import absimth.sim.utils.FileLog;

public class SimulationRunnerWithoutView {
	
	private String folder;
	private String filename;
	private String outputFilename;
	
	public void init(Map<String, String> config) {
		folder = config.get("folder");
		filename = config.get("filename");
		outputFilename = config.get("outputFilename");
	}

	public void run() throws Exception {
		SimulatorManager.getSim().load(folder, filename);
		
		while(SimulatorManager.getSim().getOs().executeNextInstruction()) {
			//nothing
		}
		String msg = SimulatorManager.getSim().getReport().printReport();
		FileLog.report(msg, outputFilename);
	}
}
