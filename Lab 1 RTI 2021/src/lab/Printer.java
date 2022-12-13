package lab;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class Printer extends Thread {

	public Printer(AtomicInteger moviesProccessed, int n, Map<Integer, Map<String, Movie>> globalMax,
			Barrier allMoviesProcessed) {
		this.moviesProccessed = moviesProccessed;
		N = n;
		this.globalMax = globalMax;
		this.allMoviesProcessed = allMoviesProcessed;
	}

	@Override
	public void run() {
		while (allMoviesProcessed.await(N) == false) {
			System.out.println("Processed movies: " + moviesProccessed.get());
		}
		for (Entry<Integer, Map<String, Movie>> s1 : globalMax.entrySet()) {
			int decade = s1.getKey()*10;
			System.out.println("Decade: " + decade + "-" + (decade+9));
			Map<String, Movie> t = s1.getValue();
			for (Entry<String, Movie> s2 : t.entrySet()) {
				System.out.println("Genre: " + s2.getKey() + " - " + s2.getValue());
			}
			System.out.println();
		}

	}

	private AtomicInteger moviesProccessed;
	private int N;
	private Map<Integer, Map<String, Movie>> globalMax;
	private Barrier allMoviesProcessed;
}
