package absimth.sim.report;

import absimth.sim.utils.Bits;

public class ReportMemory {
	
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
	
	private static String data(String msg, Long data) {
		return data(msg , data.toString());
	}

	private static String data(String msg, String data) {
		return String.format("%-40s%20s\r\n", msg, data);
	}
	
	private static String title(String msg) {
		return "[" + msg + "]\r\n";
	}
	public String printReport() {
		String ret = title("MEMORY");
		ret +="Instructions\r\n";
		ret += data("Number of instruction read: ", numberOfReadInstruction);
		ret += data("Number of instruction written: " , numberOfWriteInstruction );
		ret += data("Number of instruction r+w: " , (numberOfReadInstruction+numberOfWriteInstruction) );

		ret += data("Number of instruction read (bytes): " , numberOfReadInstructionInBytes );
		ret += data("Number of instruction written (bytes): " , numberOfWriteInstructionInBytes );
		ret += data("Number of instruction r+w (bytes): " , (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) );
		
		ret +="Data\r\n";
		Long realData = (numberOfReadData-numberOfReadInstruction);
		ret += data("Number of data read: " , realData);
		ret += data("Number of data written: " , numberOfWriteData );
		ret += data("Number of data r+w: " , (numberOfWriteData+realData) );
		
		Long realDataBytes = (numberOfReadDataInBytes-numberOfReadInstructionInBytes);
		ret += data("Number of data read (bytes): " , realDataBytes );
		ret += data("Number of data written (bytes): ",numberOfWriteDataInBytes);
		ret += data("Number of data r+w (bytes): " ,(numberOfWriteDataInBytes+realDataBytes) );
		
		ret +="Total\r\n";
		ret += data("Number of total read: " , realData+numberOfReadInstruction);
		ret += data("Number of total written: " , numberOfWriteData +numberOfWriteInstruction);
		ret += data("Number of total r+w: " , (numberOfWriteData+realData)+ (numberOfReadInstruction+numberOfWriteInstruction) );
		
		ret += data("Number of total read (bytes): " , realDataBytes + numberOfReadInstructionInBytes );
		ret += data("Number of total written (bytes): ",numberOfWriteDataInBytes + numberOfWriteInstructionInBytes);
		ret += data("Number of total r+w (bytes): " ,(numberOfWriteDataInBytes+realDataBytes) + (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes));
		
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
