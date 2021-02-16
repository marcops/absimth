package absimth.sim.report;

import java.util.stream.Collectors;

import absimth.sim.SimulatorManager;
import lombok.Getter;

public class Report {
	@Getter
	private ReportMemory memory = new ReportMemory();
	@Getter
	private ReportCpu cpu = new ReportCpu();
	
	private static String data(String msg, Integer data) {
		return data(msg , data.toString());
	}
	
	private static String data(String msg, String data) {
		return String.format("%-40s%20s\r\n", msg, data);
	}
	
	private static String title(String msg) {
		return "[" + msg + "]\r\n";
	}
	public String printReport() {
		String ret = "\r\n------ REPORT ------\r\n";
		ret += title("SIMULATION");
		ret += data("Number of Bytes usage: ", calcTotalOfBytesUsed());
		ret+="\r\n";
		ret += title("PROGRAMS");
		ret += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReport())
				.collect(Collectors.joining("\r\n"));
		ret+="\r\n\r\n";
		ret += memory.printReport();
		ret += SimulatorManager.getSim().getMemory().getMemoryStatus().print();
		return ret;
	}
	
	private static int calcTotalOfBytesUsed() {
		return SimulatorManager.getSim()
				.getOsPrograms().entrySet().stream()
				.map(x -> x.getValue().getTotalOfMemoryUsed()).reduce(0, Integer::sum)
				+ SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getPeripheralAddressSize();
	}

}
