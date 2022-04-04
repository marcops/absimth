package absimth.sim.report;

import absimth.sim.utils.Bits;

public class ReportMemory extends AReport {
	
	private long totalOfReadDataInBytes;
	private long totalOfWriteDataInBytes;
	private long totalOfReadData;
	private long totalOfWriteData;
	
	private long numberOfReadInstructionInBytes;
	private long numberOfWriteInstructionInBytes;
	
	private long numberOfReadInstruction;
	private long numberOfWriteInstruction;
	
	public void incReadData(long sizeInBits) {
		totalOfReadDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
		totalOfReadData++;
	}
	
	public void incReadInstruction(long sizeInBits) {
		numberOfReadInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
		numberOfReadInstruction++;
	}
	
	public void incWriteData(long sizeInBits) {
		totalOfWriteDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
		totalOfWriteData++;
	}
	
	public void incWriteInstruction(long sizeInBits) {
		numberOfWriteInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
		numberOfWriteInstruction++;
	}

	public String printReport() {
		String ret = title("MEMORY");
		ret +="Instructions\r\n";
		ret += data("Number of instruction read: ", numberOfReadInstruction);
		ret += data("Number of instruction written: " , numberOfWriteInstruction);
		ret += data("Number of instruction r+w: " , numberOfWriteInstruction+numberOfReadInstruction);

		ret += data("Number of instruction read (bytes): " , numberOfReadInstructionInBytes );
		ret += data("Number of instruction written (bytes): " , numberOfWriteInstructionInBytes );
		ret += data("Number of instruction r+w (bytes): " , (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) );
		
		ret +="Data\r\n";
		ret += data("Number of data read: " , totalOfReadData - numberOfReadInstruction);
		ret += data("Number of data written: " , totalOfWriteData - numberOfWriteInstruction);
		ret += data("Number of data r+w: ", (totalOfReadData + totalOfWriteData) - (numberOfReadInstruction + numberOfWriteInstruction));
		
		ret += data("Number of data read (bytes): " , totalOfReadDataInBytes-numberOfReadInstructionInBytes );
		ret += data("Number of data written (bytes): ",totalOfWriteDataInBytes-numberOfWriteInstructionInBytes);
		ret += data("Number of data r+w (bytes): " , (totalOfReadDataInBytes + totalOfWriteDataInBytes)-(numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes));
		
		ret +="Total\r\n";
		ret += data("Number of total read: " , totalOfReadData);
		ret += data("Number of total written: " , totalOfWriteData);
		ret += data("Number of total r+w: " , totalOfReadData + totalOfWriteData);
		
		ret += data("Number of total read (bytes): " , totalOfReadDataInBytes);
		ret += data("Number of total written (bytes): ", totalOfWriteDataInBytes);
		ret += data("Number of total r+w (bytes): " , totalOfReadDataInBytes + totalOfWriteDataInBytes);
		
//		ret += "\r\n[ MEMORY SPEED]\r\n";

//		ret += "Instruction in bytes read " + numberOfReadInstructionInBytes + "\r\n";
//		ret += "Instruction in bytes written: " + numberOfWriteInstructionInBytes + "\r\n";
//		ret += "Instruction in bytes total: " + (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) + "\r\n";
//		
//		ret += "Data in bytes read " + numberOfReadDataInBytes + "\r\n";
//		ret += "Data in bytes written: " + numberOfWriteDataInBytes + "\r\n";
//		ret += "Data in bytes total: " + (numberOfWriteDataInBytes+numberOfReadDataInBytes) + "\r\n";
		
		return ret;
	}


}
