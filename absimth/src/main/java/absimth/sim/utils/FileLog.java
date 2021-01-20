package absimth.sim.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLog {

	private static BufferedWriter bufferWritter;

	public static void close() {
		try {
			if(bufferWritter!= null)
				bufferWritter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bufferWritter = null;
	}
//	
//	public static void append(String content, boolean close) {
//		try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output.txt"), true))) {
//			bw.write(content);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
//	}
	
	public static void append(String content, boolean close) {
		try {
			if (bufferWritter == null) bufferWritter = openFile();
			bufferWritter.write(content);
			if(close) close();
		} catch (IOException ioe) {
			System.out.println("Exception occurred:");
			ioe.printStackTrace();
		}
	}

	private static BufferedWriter openFile() throws IOException {
		File file = new File("output.txt");
//		if (!file.exists()) file.createNewFile();
		return new BufferedWriter(new FileWriter(file, true));
	}
}