package rs.ac.bg.etf.kdp.lab;

import java.util.Random;

public class Consumer extends Thread {

	public Consumer(MessageBox<Integer> buffer) {
		super("Consumer");
		buff = buffer;
	}

	@Override
	public void run() {
		while (true) {
			Status myStatus = new BooleanStatus(false);
			Integer item = buff.get(Integer.MAX_VALUE, myStatus);
			if (myStatus.getStatus() == true) {
				// OK, uzet element
				consume(item);
			}
		}
	}

	private void consume(Integer item) {
		try {
			sleep(1000 + new Random().nextInt(9001));
		} catch (InterruptedException e) {
		}
		System.out.println(String.format("Consumer consumed item %d", item.intValue()));
	}

	private MessageBox<Integer> buff;

}
