package rs.ac.bg.etf.kdp.lab;

import java.util.LinkedList;
import java.util.List;

public class MonitorMessageBox<T> implements MessageBox<T> {

	public MonitorMessageBox(int size) {
		super();
		this.capacity = size;
		buffer = new LinkedList<>();
	}

	@Override
	public synchronized void put(T msg, int priority, int timeToLiveMs) {
		long start = System.currentTimeMillis();
		while (capacity == buffer.size()) {
			try {
				wait();
			} catch (InterruptedException e) {
				long end = System.currentTimeMillis();
				if (timeToLiveMs > 0 && end - start >= timeToLiveMs)
					return;
			}
		}
		long end = System.currentTimeMillis();
		if (timeToLiveMs > 0 && end - start >= timeToLiveMs)
			return;
		// trebalo bi voditi racuna i na kraju ubacivanja u bafer...

		buffer.add(msg); // bez vodjenja racuna o prioritetu
		notify();
	}

	@Override
	public synchronized T get(int timeToWait, Status status) {
		long start = System.currentTimeMillis();
		while (buffer.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				long end = System.currentTimeMillis();
				if (end - start >= timeToWait) {
					status.setStatus(false);
					return null;
				}
			}
		}
		long end = System.currentTimeMillis();
		if (end - start >= timeToWait) {
			status.setStatus(false);
			return null;
		}

		notify();

		T item = buffer.poll();
		status.setStatus(true);
		// TODO videti da li more notifyAll

		return item;
	}

	private LinkedList<T> buffer;
	private int capacity = 0;
	// size --> buffer.size()

}
