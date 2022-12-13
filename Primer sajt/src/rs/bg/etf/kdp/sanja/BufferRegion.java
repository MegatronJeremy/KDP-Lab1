package rs.bg.etf.kdp.sanja;

import java.util.LinkedList;
import java.util.Queue;

public class BufferRegion<T> implements Buffer<T> {

	@Override
	public void put(T data) {
		synchronized(q) {
			q.add(data);
			q.notify();
		}
	}

	@Override
	public T get() {
		T data;
		synchronized(q) {
			while (q.size() == 0) {
				try {
					q.wait();
				} catch (InterruptedException e) {
				}
			}
			data = q.poll();
		}
		return data;
	}

	@Override
	public T poll() {
		T data;
		synchronized(q) {
			data = q.poll();
		}
		return data;
	}

	private final Queue<T> q = new LinkedList<>();
}
