package lab;

import java.util.Map;
import java.util.Map.Entry;

public class Combiner extends Thread {

	public Combiner(Buffer<Entry<Integer, Integer>> seriesPerDecade, Map<Integer, Integer> finalDecadeNum,
			Barrier combinerDone) {
		this.seriesPerDecade = seriesPerDecade;
		this.finalDecadeNum = finalDecadeNum;
		this.combinerDone = combinerDone;
	}

	@Override
	public void run() {
		Entry<Integer, Integer> e;

		while ((e = seriesPerDecade.get()) != null) {
			int cnt = finalDecadeNum.getOrDefault(e.getKey(), 0);
			finalDecadeNum.put(e.getKey(), e.getValue() + cnt);
		}

		combinerDone.arrive();
	}

	private Buffer<Entry<Integer, Integer>> seriesPerDecade;
	private Map<Integer, Integer> finalDecadeNum;
	private Barrier combinerDone;
}
