package absimth.sim.report;

import java.util.HashMap;

import absimth.sim.SimulatorManager;
import absimth.sim.gui.model.TimelineDataSet;

public class ReportCPU extends AReport {
	
	private HashMap<Integer, TimelineDataSet> timelineCpu = new HashMap<>();
	
	public TimelineDataSet getTimeline(int cpu) {
		timelineCpu.putIfAbsent(cpu, TimelineDataSet
				.builder()
				.start(0)
				.end(0)
				.entries(new HashMap<>())
				.build());
		return timelineCpu.get(cpu);
	}

	public String printReport() {
		int totalCpu = SimulatorManager.getSim().getLstCpu().size();
		String ret = "";
		for (int i = 0; i < totalCpu; i++) {
			ret += "CPU"+i;
			if(timelineCpu.get(i) == null)
				ret += "\r\n  Last Tick at: 0\r\n";
			else
				ret += "\r\n  Last Tick at: " + timelineCpu.get(i).getEnd()+"\r\n";
		}
		return ret;
	}
}
