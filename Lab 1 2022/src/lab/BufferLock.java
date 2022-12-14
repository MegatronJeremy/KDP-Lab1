package lab;

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
			full.signal();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T get() {
		T data;
		lock.lock();
		try {
			while (q.isEmpty()) {
				full.awaitUninterruptibly();
			}
			data = q.poll();
		} finally {
			lock.unlock();
		}

		return data;
	}

	private Lock lock = new ReentrantLock();
	private Condition full = lock.newCondition();

	private Queue<T> q = new LinkedList<>();
}
