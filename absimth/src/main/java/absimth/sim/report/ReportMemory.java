package absimth.sim.report;

import absimth.sim.utils.Bits;

public class ReportMemory extends AReport {
	
	private long totalOfReadDataInBytes;
	private long totalOfWriteDataInBytes;
	
	private long numberOfReadInstructionInBytes;
	private long numberOfWriteInstructionInBytes;
	
	public void incReadData(long sizeInBits) {
		totalOfReadDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}
	
	public void incReadInstruction(long sizeInBits) {
		numberOfReadInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}
	
	public void incWriteData(long sizeInBits) {
		totalOfWriteDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}
	
	public void incWriteInstruction(long sizeInBits) {
		numberOfWriteInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
	}

	private int calcBytesToNumberOfRiscV(long number) {
		Long rec = number * Bits.BYTE_SIZE;
		return (int) (rec/18);
	}
	
	public String printReport() {
		String ret = title("MEMORY");
		ret +="Instructions\r\n";
		ret += data("Number of instruction read: ", calcBytesToNumberOfRiscV(numberOfReadInstructionInBytes));
		ret += data("Number of instruction written: " , calcBytesToNumberOfRiscV(numberOfWriteInstructionInBytes) );
		ret += data("Number of instruction r+w: " , calcBytesToNumberOfRiscV(numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) );

		ret += data("Number of instruction read (bytes): " , numberOfReadInstructionInBytes );
		ret += data("Number of instruction written (bytes): " , numberOfWriteInstructionInBytes );
		ret += data("Number of instruction r+w (bytes): " , (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) );
		
		ret +="Data\r\n";
		Long realDataBytes = (totalOfReadDataInBytes + totalOfWriteDataInBytes)-(numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes);
		ret += data("Number of data read: " , calcBytesToNumberOfRiscV(totalOfReadDataInBytes-numberOfReadInstructionInBytes));
		ret += data("Number of data written: " , calcBytesToNumberOfRiscV(totalOfWriteDataInBytes-numberOfWriteInstructionInBytes));
		ret += data("Number of data r+w: " , calcBytesToNumberOfRiscV(realDataBytes));
		
		ret += data("Number of data read (bytes): " , totalOfReadDataInBytes-numberOfReadInstructionInBytes );
		ret += data("Number of data written (bytes): ",totalOfWriteDataInBytes-numberOfWriteInstructionInBytes);
		ret += data("Number of data r+w (bytes): " , realDataBytes);
		
		ret +="Total\r\n";
		ret += data("Number of total read: " , calcBytesToNumberOfRiscV(totalOfReadDataInBytes));
		ret += data("Number of total written: " , calcBytesToNumberOfRiscV(totalOfWriteDataInBytes));
		ret += data("Number of total r+w: " , calcBytesToNumberOfRiscV(totalOfReadDataInBytes + totalOfWriteDataInBytes));
		
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
