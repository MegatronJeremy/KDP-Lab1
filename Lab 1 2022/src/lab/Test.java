package lab;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {
	public static void main(String[] args) {
		int consumersNum = 5;
		int M = 1;
		int K = 10000;

		long startTime = System.currentTimeMillis();

		String fileName = "300000_data.tsv";
		Buffer<String> fileInfo = new BufferSemFIFO<>();
		AtomicBoolean firstDone = new AtomicBoolean(false);
		Buffer<Entry<Integer, Integer>> seriesPerDecade = new BufferMonitor<>();
		Barrier consumersDone = new BarrierMonitor(consumersNum);
		Map<Integer, Integer> moviesRead = new ConcurrentHashMap<>();
		Map<Integer, Integer> finalDecadeNum = new TreeMap<>();
		Barrier combinerDone = new BarrierMonitor(1);

		new Producer(fileName, fileInfo).start();

		for (int i = 0; i < consumersNum; i++) {
			new Consumer(i, K, firstDone, fileInfo, seriesPerDecade, consumersDone, moviesRead).start();
		}

		new Combiner(seriesPerDecade, finalDecadeNum, combinerDone).start();

		Printer p = new Printer(M, finalDecadeNum, moviesRead, combinerDone);
		p.start();

		try {
			p.join();
		} catch (InterruptedException e) {
		}

		long endTime = System.currentTimeMillis();

		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}
}
