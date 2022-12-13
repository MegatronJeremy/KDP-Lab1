package rs.bg.etf.kdp.sanja;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Consumer extends Thread {

	public Consumer(int id, Barrier bar, Buffer<String> buf, Buffer<Integer> bufCombiner, Map<Integer, Integer> map,
			int N) {
		super("Consumer" + id);
		this.bar = bar;
		this.id = id;
		this.buf = buf;
		this.bufCombiner = bufCombiner;
		this.N = N;
		this.map = map;
	}

	@Override
	public void run() {
		while (true) {
			String line = null;
			while (line == null) {
				line = buf.get();
			}
			cnt++;
			if (line.equals("-1")) {
				sendData();
				bar.arrive();
				buf.put("-1");
				break;
			}
			parseLine(line);
			if (cnt % N == 0) {
				sendData();
			}
		}
	}

	private void sendData() {
		while (!decades.isEmpty()) {
			bufCombiner.put(Integer.parseInt(decades.poll()));
		}
		map.put(id, cnt);
	}

	private void parseLine(String line) {
		if (line == null)
			return;

		String[] args = line.split("\t");
		String birthYear = args[2];
		String deathYear = args[3];
		String primaryProfession = args[4];
		if (deathYear.equals("\\N") || birthYear.equals("\\N")
				|| !primaryProfession.contains("actor") && !primaryProfession.contains("actress")) {
			return;
		}

		decades.add(birthYear);
	}

	private final int N;
	private final int id;
	private final Buffer<String> buf;
	private final Buffer<Integer> bufCombiner;
	private final Map<Integer, Integer> map;
	private final Queue<String> decades = new LinkedList<>();
	private final Barrier bar;
	private int cnt = 0;

}
