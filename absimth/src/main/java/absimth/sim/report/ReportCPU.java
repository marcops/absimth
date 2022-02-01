package absimth.sim.report;

import java.util.HashMap;

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
		String ret = title("CPU");
		return ret;
	}
}
