package rs.bg.etf.kdp.sanja;

import java.util.LinkedList;
import java.util.Queue;

public class BufferMonitor<T> implements Buffer<T> {

	@Override
	public synchronized void put(T data) {
		q.add(data);
		notify();
	}

	@Override
	public synchronized T get() {
		while (q.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		return q.poll();
	}

	@Override
	public synchronized T poll() {
		return q.poll();
	}

	private final Queue<T> q = new LinkedList<>();

}
