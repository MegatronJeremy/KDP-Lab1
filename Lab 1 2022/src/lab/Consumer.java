package lab;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumer extends Thread {

	public Consumer(int id, int k, AtomicBoolean firstDone, Buffer<String> fileInfo,
			Buffer<Entry<Integer, Integer>> seriesPerDecade, DoubleDoorBarrier consumersDone, Map<Integer, Integer> moviesRead) {
		this.id = id;
		K = k;
		this.firstDone = firstDone;
		this.fileInfo = fileInfo;
		this.seriesPerDecade = seriesPerDecade;
		this.consumersDone = consumersDone;
		this.moviesRead = moviesRead;
	}

	@Override
	public void run() {
		while (true) {
			String line = fileInfo.get();
			if (line == null) {
				fileInfo.put(line);
				break;
			}

			movieCnt++;
			parseLine(line);

			if (movieCnt % K == 0) {
				moviesRead.put(id, movieCnt);
			}
		}

		moviesRead.put(id, movieCnt);
		sendData();

		consumersDone.pass();

		if (firstDone.getAndSet(true) == false) {
			seriesPerDecade.put(null);
		}
	}

	private void parseLine(String line) {
		String[] s = line.split("\t");

		if (!s[1].equals("tvSeries") || s[5].equals("\\N") || !s[6].equals("\\N"))
			return;

		int decade = Integer.parseInt(s[5]) / 10;

		int cnt = decadeNum.getOrDefault(decade, 0);
		decadeNum.put(decade, cnt + 1);
	}

	private void sendData() {
		for (Entry<Integer, Integer> s : decadeNum.entrySet()) {
			seriesPerDecade.put(s);
		}
	}

	private int id, K;
	private AtomicBoolean firstDone;
	private Buffer<String> fileInfo;
	private Buffer<Entry<Integer, Integer>> seriesPerDecade;
	private DoubleDoorBarrier consumersDone;
	private Map<Integer, Integer> moviesRead;

	private int movieCnt = 0;
	private Map<Integer, Integer> decadeNum = new HashMap<>();

}
