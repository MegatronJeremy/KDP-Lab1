package lab;

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
	public T poll() {
		T data;
		synchronized(q) {
			data = q.poll();
		}
		return data;
	}

	@Override
	public T get() {
		T data;
		synchronized(q) {
			while (q.isEmpty()) {
				try {
					q.wait();
				} catch (InterruptedException e) {
				}
			}
			data = q.poll();
		}
		return data;
	}
	
	private Queue<T> q = new LinkedList<>();

}
