package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map.Entry;

public class Producer extends Thread {

	public Producer(String fileName, Buffer<String> fileInfo) {
		this.fileName = fileName;
		this.fileInfo = fileInfo;
	}

	@Override
	public void run() {
		File file = new File(fileName);

		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			String line = in.readLine();
			do {
				line = in.readLine();
				fileInfo.put(line);
			} while (line != null);
		} catch (Exception e) {
			fileInfo.put(null);
			System.out.println("File error!");
		}
	}

	private String fileName;
	private Buffer<String> fileInfo;
}
