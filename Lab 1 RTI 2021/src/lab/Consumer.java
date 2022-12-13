package lab;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Queue;

public class Consumer extends Thread {

	public Consumer(int id, AtomicInteger moviesProccessed, Buffer<String> bufMovie, AtomicBroadcastBuffer<String> bufRatings,
			Barrier allDataRead, Barrier allRatingsRead, Buffer<Movie> bestMovies) {
		this.id = id;
		this.moviesProccessed = moviesProccessed;
		this.bufMovie = bufMovie;
		this.bufRatings = bufRatings;
		this.allDataRead = allDataRead;
		this.allRatingsRead = allRatingsRead;
		this.bestMovies = bestMovies;
	}

	@Override
	public void run() {
		while (true) {
			String line = bufMovie.get();
			if (line == null) {
				bufMovie.put(null);
				break;
			}
			String[] s = line.split("\t");
			if (s[1].equals("movie") && !s[5].equals("\\N") && !s[8].contains("\\N")) {
				Movie m = new Movie(s);
				movies.put(m.getId(), m);
			}
		}

		allDataRead.arrive();

		while (true) {
			String line = bufRatings.get(id);
			if (line == null) {
				break;
			}
			String[] ratings = line.split("\t");
			if (!movies.containsKey(ratings[0]))
				continue;
			moviesProccessed.getAndIncrement();
			Movie m = movies.get(ratings[0]);
			m.setRating(Double.parseDouble(ratings[1]));
			m.setNumVotes(Integer.parseInt(ratings[2]));
			
			// At least 1000 votes
			if (m.getNumVotes() >= 1000)
				updateLocalMax(m);
		}

		for (Entry<Integer, Map<String, Movie>> s1 : localMax.entrySet()) {
			Map<String, Movie> t = s1.getValue();
			for (Entry<String, Movie> s2 : t.entrySet()) {
				bestMovies.put(s2.getValue());
			}
		}

		allRatingsRead.arrive();
	}

	private void updateLocalMax(Movie m) {
		Map<String, Movie> t;
		if (!localMax.containsKey(m.getDecade())) {
			t = new HashMap<>();
			localMax.put(m.getDecade(), t);
		} else {
			t = localMax.get(m.getDecade());
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

	private int id;
	private AtomicInteger moviesProccessed;
	private Buffer<String> bufMovie;
	private AtomicBroadcastBuffer<String> bufRatings;
	private Barrier allDataRead, allRatingsRead;
	private Buffer<Movie> bestMovies;
	private Map<Integer, Map<String, Movie>> localMax = new HashMap<>();
	private Map<String, Movie> movies = new HashMap<>();
}
