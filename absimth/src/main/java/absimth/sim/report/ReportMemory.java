package absimth.sim.report;

import absimth.sim.utils.Bits;

public class ReportMemory extends AReport {
	
	private long numberOfReadDataInBytes;
	private long numberOfWriteDataInBytes;
	private long numberOfReadData;
	private long numberOfWriteData;	
	private long numberOfReadInstructionInBytes;
	private long numberOfWriteInstructionInBytes;
	private long numberOfReadInstruction;
	private long numberOfWriteInstruction;
	
	private boolean inInstruction;
	
	public void incReadInstructionAndReduce(int sizeInBits) {
		numberOfReadInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
		numberOfReadInstruction++;		
		
		numberOfReadDataInBytes -= (sizeInBits/Bits.BYTE_SIZE);
		numberOfReadData--;
	}
	
	public void incInsRead(long sizeInBits) {
		numberOfReadInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
		numberOfReadInstruction++;
	}
	public void incReadData(long sizeInBits) {
		if(inInstruction) {
			numberOfReadInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
			numberOfReadInstruction++;
		} else {
			numberOfReadDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
			numberOfReadData++;
		}
	}
	
	
	public void incWriteData(long sizeInBits) {
		if(inInstruction) {
			numberOfWriteInstructionInBytes += (sizeInBits/Bits.BYTE_SIZE);
			numberOfWriteInstruction++;
		} else {
			numberOfWriteDataInBytes += (sizeInBits/Bits.BYTE_SIZE);
			numberOfWriteData++;
		}
	}
	
	public void setInstruction(boolean inInstruction) {
		this.inInstruction = inInstruction;
	}
	public String printReportSmall() {
		String ret = data("Number of instruction read: ", numberOfReadInstruction);
		ret += data("Number of instruction written: " , numberOfWriteInstruction);	
		ret += data("Number of data read: " , numberOfReadData );
		ret += data("Number of data written: " , numberOfWriteData);
		return ret;
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
		ret += data("Number of data read: " , numberOfReadData );
		ret += data("Number of data written: " , numberOfWriteData);
		ret += data("Number of data r+w: ", (numberOfReadData + numberOfWriteData));
		
		ret += data("Number of data read (bytes): " , numberOfReadDataInBytes);
		ret += data("Number of data written (bytes): ",numberOfWriteDataInBytes);
		ret += data("Number of data r+w (bytes): " , (numberOfReadDataInBytes + numberOfWriteDataInBytes));
		
		ret +="Total\r\n";
		ret += data("Number of total read: " , numberOfReadData + numberOfReadInstruction);
		ret += data("Number of total written: " , numberOfWriteData + numberOfWriteInstruction);
		ret += data("Number of total r+w: " , (numberOfReadData + numberOfWriteData)+(numberOfReadDataInBytes + numberOfWriteDataInBytes));
		
		ret += data("Number of total read (bytes): " , numberOfReadDataInBytes+numberOfReadInstructionInBytes);
		ret += data("Number of total written (bytes): ", numberOfWriteDataInBytes+numberOfWriteInstructionInBytes);
		ret += data("Number of total r+w (bytes): " , (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes)+ (numberOfReadDataInBytes + numberOfWriteDataInBytes));
		
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
