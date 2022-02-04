package absimth.sim.report;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import absimth.sim.SimulatorManager;
import lombok.Getter;

public class Report extends AReport {
	@Getter
	private ReportMemory memory = new ReportMemory();
	@Getter
	private ReportCPU cpu = new ReportCPU();
	@Getter
	private GeneralInformation generalInformation = new GeneralInformation();
	private Map<String, Long> memoryController = new HashMap<>();
	
//	public Long getControllerInfo(String data) {
//		memoryController.putIfAbsent(data, 0L);
//		return memoryController.get(data);
//	}
	
	public String printReport() {
		String ret = "\r\n------ REPORT ------\r\n";
		ret += title("SIMULATION");
		ret += data("Number of Application Bytes: ", calcTotalOfBytesUsed());
		ret+="\r\n";
		ret += title("PROGRAMS");
		ret += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReport())
				.collect(Collectors.joining("\r\n"));
		ret+="\r\n\r\n";
		ret += memory.printReport();
		ret += SimulatorManager.getSim().getMemoryController().getMemoryStatus().print();
		ret+="\r\n";
		ret += printMemoryController();
		ret += title("CPU");
		ret += cpu.printReport();
		ret+="\r\n";
		ret+="\r\n";
		ret += generalInformation.printReport();
		return ret;
	}

	private String printMemoryController(){
		String ret = title("MEMORY CONTROLLER");
		ret += memoryController
				.entrySet().stream()
				.map(x->x.getKey() + ": " + x.getValue().toString())
				.collect(Collectors.joining("\r\n"));
		ret+="\r\n\r\n";
		return ret;
	}
	
	private static int calcTotalOfBytesUsed() {
		return SimulatorManager.getSim()
				.getOsPrograms().entrySet().stream()
				.map(x -> x.getValue().getTotalOfMemoryUsed()).reduce(0, Integer::sum)
				+ SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getPeripheralAddressSize();
	}

	public void memoryControllerInc(String data) {
		Long l = memoryController.getOrDefault(data, 0L);
		memoryController.put(data, l+1);
	}

}
