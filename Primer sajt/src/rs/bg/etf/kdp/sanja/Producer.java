package rs.bg.etf.kdp.sanja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class Producer extends Thread {

	public Producer(String fileName, Buffer<String> buf) {
		super("Producer");
		this.fileName = fileName;
		this.buf = buf;
	}

	@Override
	public void run() {
		File file = new File(fileName);
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			in.readLine();
			while (true) {
				String line = in.readLine();
				if (line == null) {
					buf.put("-1");
					break;
				} else
					buf.put(line);
			}
		} catch (Exception e) {
			buf.put("-1");
			System.out.println("Invalid file!");
		}
	}

	private final String fileName;

	private final Buffer<String> buf;

}
