package lab;

import java.util.Map;
import java.util.Map.Entry;

public class Printer extends Thread {
	
	public Printer(int m, Map<Integer, Integer> finalDecadeNum, Map<Integer, Integer> moviesRead,
			Barrier combinerDone) {
		M = m;
		this.finalDecadeNum = finalDecadeNum;
		this.moviesRead = moviesRead;
		this.combinerDone = combinerDone;
	}

	@Override
	public void run() {
		while (combinerDone.await(M*1000) == false) {
			printMovieInfo();
		}
		printFinalData();

	}

	private void printFinalData() {
		for (Entry<Integer, Integer> s : finalDecadeNum.entrySet()) {
			int decade = s.getKey() * 10, seriesN = s.getValue();
			System.out.println("Decade " + decade + "-" + (decade + 9) + " has " + seriesN + " unfinished tv series.");
		}
	}

	private void printMovieInfo() {
		for (Entry<Integer, Integer> s : moviesRead.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Consumer ").append(s.getKey()).append(" read: ").append(s.getValue()).append(" movies.");
			System.out.println(sb.toString());
		}
	}

	private int M;
	private Map<Integer, Integer> finalDecadeNum;
	private Map<Integer, Integer> moviesRead;
	private Barrier combinerDone;
}
