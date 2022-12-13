package lab;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
	public static void main(String[] args) {

		int consumersNum = 5;
		int bufferSize = 50000;
		int N = 1000; // time in ms

		long startTime = System.currentTimeMillis();
		
		Buffer<String> bufMovie = new BufferRegion<>();
		AtomicBroadcastBuffer<String> bufRatings = new AtomicBroadcastBufferSem<>(consumersNum, bufferSize);
		Barrier allDataRead = new BarrierRegion(consumersNum);
		String fileData = "basics.tsv", fileRatings = "ratings.tsv";
		Barrier allRatingsRead = new BarrierRegion(consumersNum), allMoviesProcessed = new BarrierRegion(1);
		Map<Integer, Map<String, Movie>> globalMax = new TreeMap<>();
		Buffer<Movie> bestMovies = new BufferRegion<>();
		AtomicInteger moviesProccessed = new AtomicInteger();

		new Producer(bufMovie, bufRatings, allDataRead, fileData, fileRatings).start();
		
		for (int i = 0; i < consumersNum; i++) {
			new Consumer(i, moviesProccessed, bufMovie, bufRatings, allDataRead, allRatingsRead, bestMovies).start();
		}
		
		new Combiner(allRatingsRead, allMoviesProcessed, globalMax, bestMovies).start();
		
		Printer p = new Printer(moviesProccessed, N, globalMax, allMoviesProcessed);
		p.start();
		
		try {
			p.join();
		} catch (InterruptedException e) {
		}

		long endTime = System.currentTimeMillis();
		
		System.out.println("Execution time: " + (endTime - startTime) + "ms");
		
	}

}
