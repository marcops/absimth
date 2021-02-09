package absimth.sim;

import java.util.stream.Collectors;

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
	
	private static String data(String msg, Integer data) {
		return data(msg , data.toString());
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
		String ret = "\r\n------ REPORT ------\r\n";
		ret += title("SIMULATION");
		ret += data("Number of Bytes usage: ", calcTotalOfBytesUsed());
		ret += title("PROGRAMS");
		ret += SimulatorManager.getSim().getOsPrograms()
				.entrySet().stream()
				.map(x->x.getValue().toReport())
				.collect(Collectors.joining("\r\n"));
//		ret += "\r\n[ CPU ]\r\n";
//		ret += "Running at same time of memory: " + numberOfReadInstruction + "\r\n";
		ret+="\r\n";
		ret += title("MEMORY");
		ret += data("Number of instruction read: ", numberOfReadInstruction);
		ret += data("Number of instruction written: " , numberOfWriteInstruction );
		ret += data("Number of instruction total: " , (numberOfReadInstruction+numberOfWriteInstruction) );
		
		ret += data("Number of data read: " , numberOfReadData );
		ret += data("Number of data written: " , numberOfWriteData );
		ret += data("Number of data total: " , (numberOfWriteData+numberOfReadData) );
		
		ret += data("Number of instruction in bytes read " , numberOfReadInstructionInBytes );
		ret += data("Number of instruction in bytes written: " , numberOfWriteInstructionInBytes );
		ret += data("Number of instruction in bytes total: " , (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) );
		
		ret += data("Number of data in bytes read " , numberOfReadDataInBytes );
		ret += data("Number of data in bytes written: ",numberOfWriteDataInBytes);
		ret += data("Number of data in bytes total: " ,(numberOfWriteDataInBytes+numberOfReadDataInBytes) );
		
//		ret += "\r\n[ MEMORY SPEED]\r\n";

//		ret += "Instruction in bytes read " + numberOfReadInstructionInBytes + "\r\n";
//		ret += "Instruction in bytes written: " + numberOfWriteInstructionInBytes + "\r\n";
//		ret += "Instruction in bytes total: " + (numberOfWriteInstructionInBytes+numberOfReadInstructionInBytes) + "\r\n";
//		
//		ret += "Data in bytes read " + numberOfReadDataInBytes + "\r\n";
//		ret += "Data in bytes written: " + numberOfWriteDataInBytes + "\r\n";
//		ret += "Data in bytes total: " + (numberOfWriteDataInBytes+numberOfReadDataInBytes) + "\r\n";
		
		ret += SimulatorManager.getSim().getMemory().getMemoryStatus().print();
		return ret;
	}

	private static int calcTotalOfBytesUsed() {
		return SimulatorManager.getSim()
				.getOsPrograms().entrySet().stream()
				.map(x -> x.getValue().getTotalOfMemoryUsed()).reduce(0, Integer::sum)
				+ SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getPeripheralAddressSize();
	}

}
