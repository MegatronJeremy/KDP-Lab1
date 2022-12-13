package rs.bg.etf.kdp.sanja;

import java.util.Map;
import java.util.Map.Entry;

public class Printer extends Thread {

	public Printer(int N, Barrier bar, Map<Integer, Integer> mapC, Map<Integer, Integer> finalData) {
		super("Printer");
		this.N = N;
		this.barC = bar;
		this.mapC = mapC;
		this.finalData = finalData;
	}

	@Override
	public void run() {
		while (true) {
			boolean done = barC.await(N);
			if (done) break;
			
			for (Entry<Integer, Integer> s : mapC.entrySet()) {
				System.out.println("Proizvodjac " + s.getKey() + " proizveo " + s.getValue());
			}
		}
		
		for (Entry<Integer, Integer> s : finalData.entrySet()) {
			System.out.println("Dekada: " + s.getKey()*10 + "-" + (s.getKey()*10+9) + " ima: " + s.getValue() + " glumaca");
		}
	}
	
	private final int N;
	private Barrier barC;
	private Map<Integer, Integer> mapC;
	private Map<Integer, Integer> finalData;

}
