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
		int totalCpu = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getCpu().size();
		String ret = "";
		for (int i = 0; i < totalCpu; i++) {
			int amount = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getCpu().get(i).getAmount();
			ret += "CPU "+i;
			ret += "\r\nCpuType=" + SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getCpu().get(i).getName();
			ret += "\r\n";
			for(int j=0;j<amount;j++) {
				int curCpu = i*j+j;
				ret += "\r\nCore "+j;
				ret += "\r\nCpuId "+curCpu;
				if(timelineCpu.get(curCpu) == null) {
					ret += "\r\n  Last Tick at: 0\r\n";
				} else {
					ret += "\r\n  Last Tick at: " + timelineCpu.get(curCpu).getEnd()+"\r\n";
				}
			}
		}
		return ret;
	}
}
