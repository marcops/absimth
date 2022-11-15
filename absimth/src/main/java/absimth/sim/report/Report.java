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
		//ret += SimulatorManager.getSim().getMemoryController().getMemoryStatus().print();
		//ret+="\r\n";
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
				.map(x -> x.getValue().getTotalOfMemory()).reduce(0, Integer::sum)
				+ SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getPeripheralAddressSize();
	}

	public void memoryControllerInc(String data) {
		Long l = memoryController.getOrDefault(data, 0L);
		memoryController.put(data, l+1);
	}

	public String printReportSmall() {
		// TODO Auto-generated method stub
		
		String msg =  SimulatorManager.getSim().getAbsimthConfiguration().getModules().toSmall() + ";";
		
		msg += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReportName())
				.collect(Collectors.joining("-")) + ";";
		
		msg += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReportStatus())
				.collect(Collectors.joining("-")) + ";";
		
		msg += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReportTotal())
				.collect(Collectors.summingInt(Integer::intValue));
				
		
		msg += ";"  + SimulatorManager.getSim().getAbsimthConfiguration().getModules().getMemoryFaultInjection().getConfig().split(";")[7];
		
//		msg += "\r\n" +SimulatorManager.getSim().getMemoryController().getMemoryStatus().printAll();
		//msg += "\r\nTotal of cycle = " + generalInformation.totalCycles();
		//msg += "\r\n"+ memory.printReportSmall();
		return msg+"\r\n";
	}
	
//	public String printReportSmall() {
//		String msg = SimulatorManager.getSim().getOsPrograms()
//				.entrySet().stream()
//				.map(x->{
//					return SimulatorManager.getSim().getAbsimthConfiguration().getModules().toSmall() + ";" +SimulatorManager.getSim().getOsPrograms()
//							.entrySet().stream()
//							.map(y->y.getValue().toReportName())
//							.collect(Collectors.joining("-")) + ";"
//				+ x.getValue().toReportName() + ";" 
//							+ x.getValue().toReportStatus() + ";" + x.getValue().toReportTotal() + ";"
//							+";"  + SimulatorManager.getSim().getAbsimthConfiguration().getModules().getMemoryFaultInjection().getConfig().split(";")[7];
//				})
//				.collect(Collectors.joining("\r\n")) + "\r\n";
//		return msg;
//	}
	
	public String printReportDead() {
		// TODO Auto-generated method stub
		
		String msg =  SimulatorManager.getSim().getAbsimthConfiguration().getModules().toSmall() + ";";
		try {
		msg += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReportName())
				.collect(Collectors.joining("-")) + ";";
		}catch (Exception e) {
			msg+="NAME FAIL;";
		}
		msg += "DEAD;";
		try {
		msg += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReportTotal())
				.collect(Collectors.summingInt(Integer::intValue));
		}catch (Exception e) {
			msg+="0";
		}
		try {
		msg += ";"  + SimulatorManager.getSim().getAbsimthConfiguration().getModules().getMemoryFaultInjection().getConfig().split(";")[7];
		}catch (Exception e) {
			msg += ";0";
		}
		//msg += "\r\nTotal of cycle = " + generalInformation.totalCycles();
		//msg += "\r\n"+ memory.printReportSmall();
		return msg+"\r\n";
	}

}
