package absimth.sim.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		return new BufferedWriter(new FileWriter(file, true));
	}

	public static void report(String msg, String filename) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
		File dir = new File("reports");
		dir.mkdirs();
		String f = filename != null ? filename : "";
		File file = new File(dir, f + dateFormat.format(new Date()) + ".txt") ;
		try(BufferedWriter buff = new BufferedWriter(new FileWriter(file, true))) {
			buff.write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void reportCSV(String msg, String filename) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
		File dir = new File("reports");
		dir.mkdirs();
		String f = filename != null ? filename : "";
		File file = new File(dir, f + dateFormat.format(new Date()) + ".csv") ;
		
		String[] rows = msg.split("\n");
		try(BufferedWriter buff = new BufferedWriter(new FileWriter(file, true))) {
			for (String row : rows) {
				String[] data = row.split(":");
				if(data.length==2) 
					buff.write(data[0] + ";" + data[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}