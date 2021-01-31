package absimth.sim;

import absimth.sim.utils.Bits;

public class Report {
	
	private long numberOfReadDataInBytes;
	private long numberOfWriteDataInBytes;
	private long numberOfReadInstructionInBytes;
	private long numberOfWriteInstructionInBytes;
	
	private long numberOfReadData;
	private long numberOfWriteData;
	private long numberOfReadInstruction;
	private long numberOfWriteInstruction;
	
	public void incReadData(long sizeInBits) {
		numberOfReadData++;
		numberOfReadDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}
	
	public void incReadInstruction(long sizeInBits) {
		numberOfReadInstruction++;
		numberOfReadInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}
	
	public void incWriteData(long sizeInBits) {
		numberOfWriteData++;
		numberOfWriteDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}
	
	public void incWriteInstruction(long sizeInBits) {
		numberOfWriteInstruction++;
		numberOfWriteInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}
	
	
	public String printReport() {
		String ret = "\r\n------ REPORT ------\r\n";
		ret += "\r\n[ MEMORY ]\r\n";
		ret += "Number of instruction read: " + numberOfReadInstruction + "\r\n";
		ret += "Number of instruction written: " + numberOfWriteInstruction + "\r\n";
		ret += "Number of instruction total: " + (numberOfReadInstruction+numberOfWriteInstruction) + "\r\n";
		
		ret += "Number of data read: " + numberOfReadData + "\r\n";
		ret += "Number of data written: " + numberOfWriteData + "\r\n\r";
		ret += "Number of data total: " + (numberOfWriteData+numberOfReadData) + "\r\n";
		
		ret += "Number of instruction in bytes read " + numberOfReadInstructionInBytes + "\r\n";
		ret += "Number of instruction in bytes written: " + numberOfWriteInstructionInBytes + "\r\n";
		ret += "Number of instruction in bytes total: " + (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) + "\r\n";
		
		ret += "Number of data in bytes read " + numberOfReadDataInBytes + "\r\n";
		ret += "Number of data in bytes written: " + numberOfWriteDataInBytes + "\r\n";
		ret += "Number of data in bytes total: " + (numberOfWriteDataInBytes+numberOfReadDataInBytes) + "\r\n";
		
		ret += "\r\n[ MEMORY SPEED]\r\n";

//		ret += "Instruction in bytes read " + numberOfReadInstructionInBytes + "\r\n";
//		ret += "Instruction in bytes written: " + numberOfWriteInstructionInBytes + "\r\n";
//		ret += "Instruction in bytes total: " + (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) + "\r\n";
//		
//		ret += "Data in bytes read " + numberOfReadDataInBytes + "\r\n";
//		ret += "Data in bytes written: " + numberOfWriteDataInBytes + "\r\n";
//		ret += "Data in bytes total: " + (numberOfWriteDataInBytes+numberOfReadDataInBytes) + "\r\n";
		
		ret += SimulatorManager.getSim().getMemory().printFails();
		return ret;
	}

}
