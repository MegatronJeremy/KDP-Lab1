package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Producer extends Thread {

	public Producer(Buffer<String> bufMovie, AtomicBroadcastBuffer<String> bufRatings, Barrier allDataRead,
			String fileData, String fileRatings) {
		this.bufMovie = bufMovie;
		this.bufRatings = bufRatings;
		this.allDataRead = allDataRead;
		this.fileData = fileData;
		this.fileRatings = fileRatings;
	}

	@Override
	public void run() {
		File data = new File(fileData);
		File ratings = new File(fileRatings);

		try (BufferedReader in = new BufferedReader(new FileReader(data))) {
			in.readLine();
			while (true) {
				String line = in.readLine();
				bufMovie.put(line);
				if (line == null)
					break;
			}
		} catch (IOException e) {
			bufMovie.put(null);
		}

		allDataRead.await();
		
		try (BufferedReader in = new BufferedReader(new FileReader(ratings))) {
			in.readLine();
			while (true) {
				String line = in.readLine();
				bufRatings.put(line);
				if (line == null)
					break;
			}
		} catch (IOException e) {
			bufMovie.put(null);
		}

	}

	private Buffer<String> bufMovie;
	private AtomicBroadcastBuffer<String> bufRatings;
	private Barrier allDataRead;
	private final String fileData, fileRatings;

}
