package rs.bg.etf.kdp.sanja;

import java.util.Map;
import java.util.Queue;

public class Combiner extends Thread {

	public Combiner(Buffer<Integer> bufIn, Barrier finalData, Barrier barC, Map<Integer, Integer> decadeM) {
		super("Combiner");
		this.bufIn = bufIn;
		this.finalData = finalData;
		this.decadeM = decadeM;
		this.barC = barC;
	}

	@Override
	public void run() {
		finalData.await();
		Integer decade;
		while ((decade = bufIn.poll()) != null) {
			processLine(decade);
		}
		barC.arrive();
	}

	private void processLine(int decade) {
		int dec = decade / 10;
		int cnt;
		if (decadeM.containsKey(dec)) {
			cnt = decadeM.get(dec) + 1;
		} else {
			cnt = 1;
		}
		decadeM.put(dec, cnt);
	}

	private final Buffer<Integer> bufIn;
	private final Map<Integer, Integer> decadeM;
	private Barrier finalData;
	private Barrier barC;

}
