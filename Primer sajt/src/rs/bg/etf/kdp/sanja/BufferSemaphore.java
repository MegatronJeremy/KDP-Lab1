package rs.bg.etf.kdp.sanja;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BufferSemaphore<T> implements Buffer<T> {

	@Override
	public T poll() {
		T data;
		if (itemsAvailable.tryAcquire() == false) {
			return null;
		}

		mutex.acquireUninterruptibly();

		data = q.poll();

		mutex.release();

		return data;
	}

	@Override
	public void put(T data) {
		mutex.acquireUninterruptibly();

		q.add(data);

		mutex.release();

		itemsAvailable.release();
	}

	@Override
	public T get() {
		T data;

		itemsAvailable.acquireUninterruptibly();

		mutex.acquireUninterruptibly();

		data = q.poll();

		mutex.release();

		return data;
	}

	private final Semaphore mutex = new Semaphore(1);
	private final Semaphore itemsAvailable = new Semaphore(0);
	private final Queue<T> q = new LinkedList<>();
}
