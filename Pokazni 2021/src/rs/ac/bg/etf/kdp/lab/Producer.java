package rs.ac.bg.etf.kdp.lab;

import java.util.Random;

public class Producer extends Thread {

	public Producer(MessageBox<Integer> buffer) {
		super("Producer");
		this.buffer = buffer;
	}
	
	@Override
	public void run() {
		while (true) {
			int item = produce();
			buffer.put(item, 0, 0);
			
			System.out.println("Producer produced item: " + item);
		}
		
	}
	
	
	/**
	 * Vraca broj izmedju 1 i 100 (inkluzivno)
	 * @return
	 */
	private int produce() {
		try {
			sleep(1000 + new Random().nextInt(9001));
		} catch (InterruptedException e) {
		}
		
		return new Random().nextInt(1, 101);
	}

	private MessageBox<Integer> buffer;

}
