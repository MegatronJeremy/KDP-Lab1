package lab;

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
		while (q.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		return q.poll();
	}

	private Queue<T> q = new LinkedList<>();

}
