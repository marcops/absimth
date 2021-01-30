package absimth.sim;

public class Report {
	
	private long numberOfReadDataInBytes;
	private long numberOfWriteDataInBytes;
	private long numberOfReadInstructionInBytes;
	private long numberOfWriteInstructionInBytes;
	
	private long numberOfReadData;
	private long numberOfWriteData;
	private long numberOfReadInstruction;
	private long numberOfWriteInstruction;
	private final Integer BYTE_SIZE = 8;
	
	public void incReadData(long sizeInBits) {
		numberOfReadData++;
		numberOfReadDataInBytes += (sizeInBits/BYTE_SIZE);
	}
	
	public void incReadInstruction(long sizeInBits) {
		numberOfReadInstruction++;
		numberOfReadInstructionInBytes += (sizeInBits/BYTE_SIZE);
	}
	
	public void incWriteData(long sizeInBits) {
		numberOfWriteData++;
		numberOfWriteDataInBytes += (sizeInBits/BYTE_SIZE);
	}
	
	public void incWriteInstruction(long sizeInBits) {
		numberOfWriteInstruction++;
		numberOfWriteInstructionInBytes += (sizeInBits/BYTE_SIZE);
	}
	
	
	public String printReport() {
		String ret = "\r\n------ REPORT ------\r\n";
		ret += "Number of instruction read: " + numberOfReadInstruction + "\r\n";
		ret += "Number of instruction written: " + numberOfWriteInstruction + "\r\n\r\n";
		
		ret += "Number of data read: " + numberOfReadData + "\r\n";
		ret += "Number of data written: " + numberOfWriteData + "\r\n\r\n";
		
		ret += "Number of instruction in bytes read " + numberOfReadInstructionInBytes + "\r\n";
		ret += "Number of instruction in bytes written: " + numberOfWriteInstructionInBytes + "\r\n\r\n";
		
		ret += "Number of data in bytes read " + numberOfReadDataInBytes + "\r\n";
		ret += "Number of data in bytes written: " + numberOfWriteDataInBytes + "\r\n\r\n";
		
		
		ret += SimulatorManager.getSim().getMemory().printFails();
		return ret;
	}

}
