package lab;

import java.util.Map;
import java.util.TreeMap;

public class Combiner extends Thread {
	
	public Combiner(Barrier allRatingsRead, Barrier allMoviesProcessed, Map<Integer, Map<String, Movie>> globalMax,
			Buffer<Movie> bestMovies) {
		this.allRatingsRead = allRatingsRead;
		this.allMoviesProcessed = allMoviesProcessed;
		this.globalMax = globalMax;
		this.bestMovies = bestMovies;
	}

	@Override
	public void run() {
		allRatingsRead.await();

		Movie m;
		while ((m = bestMovies.poll()) != null) {
			updateGlobalMax(m);
		}

		allMoviesProcessed.arrive();
	}

	private void updateGlobalMax(Movie m) {
		Map<String, Movie> t;
		if (!globalMax.containsKey(m.getDecade())) {
			t = new TreeMap<>();
			globalMax.put(m.getDecade(), t);
		} else {
			t = globalMax.get(m.getDecade());
		}

		for (String s : m.getGenres()) {
			if (!t.containsKey(s)) {
				t.put(s, m);
				continue;
			}
			Movie m1 = t.get(s);
			if (m1.getRating() < m.getRating()
					|| m1.getRating() == m.getRating() && m1.getNumVotes() < m.getNumVotes()) {
				t.put(s, m);
			}
		}

	}

	private Barrier allRatingsRead, allMoviesProcessed;
	private Map<Integer, Map<String, Movie>> globalMax; 
	private Buffer<Movie> bestMovies;
}
