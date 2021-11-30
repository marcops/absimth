package absimth.sim.report;

import java.util.HashMap;

import absimth.sim.gui.model.TimelineDataSet;

public class ReportCPU {
	
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
	
	
//	private static String data(String msg, Integer data) {
//		return data(msg , data.toString());
//	}
//	
//	private static String data(String msg, Long data) {
//		return data(msg , data.toString());
//	}
//
//	private static String data(String msg, String data) {
//		return String.format("%-40s%20s\r\n", msg, data);
//	}
	
	private static String title(String msg) {
		return "[" + msg + "]\r\n";
	}
	public String printReport() {
		String ret = title("CPU");
				
//		ret += data("Number of data in bytes read " , numberOfReadDataInBytes );
//		ret += data("Number of data in bytes written: ",numberOfWriteDataInBytes);
//		ret += data("Number of data in bytes total: " ,(numberOfWriteDataInBytes+numberOfReadDataInBytes) );
		
		return ret;
	}

}
