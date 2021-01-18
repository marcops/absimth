package absimth.sim;

public class Report {
	private long numberOfRead;
	private long numberOfWrite;

	public void incRead() {
		numberOfRead++;
	}
	
	public void incWrite() {
		numberOfWrite++;
	}
	public String printReport() {
		String ret = "\r\n------ REPORT ------\r\n";
		ret += "Total Read Instruction: " + numberOfRead + "\r\n";
		ret += "Total Write Instruction: " + numberOfWrite + "\r\n\r\n";
		
		ret += SimulatorManager.getSim().getMemory().printFails();
		return ret;
	}

}
