package lab;

import java.util.LinkedList;
import java.util.Queue;

public class BufferMonitorFIFO<T> implements Buffer<T> {

	/**
	 * Sporo ali ne moze drugacije (barem da ja znam)
	 */

	@Override
	public synchronized void put(T data) {
		q.add(data);
		notifyAll();
	}

	@Override
	public synchronized T get() {
		long myT = ticket++;
		while (myT != next || q.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		next++;

		T data = q.poll();

		if (ticket != next && !q.isEmpty())
			notifyAll();
		else if (ticket == next)
			ticket = next = 0;

		return data;
	}

	private long ticket = 0, next = 0;

	private Queue<T> q = new LinkedList<>();

}
