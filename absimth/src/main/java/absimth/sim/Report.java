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
	public void printReport() {
		String ret = "\r\n---- SIMULATION FINISHED --- \r\n";
		ret += "\r\n\r\nTotal Read Instruction: " + numberOfRead;
		ret += "\r\nTotal Write Instruction: " + numberOfWrite;
		System.out.println(ret);
	}

}
