package rs.bg.etf.kdp.sanja;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferLock<T> implements Buffer<T> {

	@Override
	public void put(T data) {
		lock.lock();

		try {
			q.add(data);

			itemA.signal();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T get() {
		T data = null;

		lock.lock();

		try {
			while (q.size() == 0) {
				itemA.awaitUninterruptibly();
			}

			data = q.poll();
		} finally {
			lock.unlock();
		}

		return data;
	}

	@Override
	public T poll() {
		T data = null;

		lock.lock();

		try {
			data = q.poll();
		} finally {
			lock.unlock();
		}

		return data;
	}

	private final Lock lock = new ReentrantLock();
	private final Condition itemA = lock.newCondition();
	private final Queue<T> q = new LinkedList<>();

}
