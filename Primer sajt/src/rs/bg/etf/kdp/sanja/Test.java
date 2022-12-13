package rs.bg.etf.kdp.sanja;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

public class Test {

	/*
	 * Treba nam vise bafera? Neka bude jedna sablonska klasa Ne samo singleton - da
	 * ima vise instanci Verovatno treba - jer imamo deljeni objekat Mozda bafer
	 * nije dovoljan - mozda nam treba jos nesto Bafer koji ce funkcionisati po
	 * atomic broadcast? Barijera? Nesto deseto? Deljeni objekat nekako prosledjen -
	 * objekat vidljiv svima
	 */

	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		int consumersNumber = 5;
		int N = 100; // posle koliko linija saljemo informaciju (printer)
		int printerMs = 100;
		String fileName = "test_fajlovi/data_3000000.tsv";

		Buffer<String> bufConsumer = new BufferMonitor<>();
		Buffer<Integer> bufCombiner = new BufferSemaphore<>();
		Map<Integer, Integer> consumerData = new ConcurrentHashMap<>();
		Map<Integer, Integer> decades = new TreeMap<>();
		Barrier barrierConsumer = new MonitorBarrier(consumersNumber);
		Barrier barrierCombiner = new MonitorBarrier(1);

		Producer producer = new Producer(fileName, bufConsumer);
		producer.start();

		for (int i = 0; i < consumersNumber; i++) {
			Consumer consumer = new Consumer(i, barrierConsumer, bufConsumer, bufCombiner, consumerData, N);
			consumer.start();
		}

		Combiner combiner = new Combiner(bufCombiner, barrierConsumer, barrierCombiner, decades);
		combiner.start();

		Printer printer = new Printer(printerMs, barrierCombiner, consumerData, decades);
		printer.start();

		// na kraju ispisati ukupno vreme (Time::now)

		try {
			printer.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();

		System.out.println("Vreme izvrsavanja: " + (endTime - startTime) + "ms");
	}
}
