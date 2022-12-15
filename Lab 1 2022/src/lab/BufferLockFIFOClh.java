package lab;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferLockFIFOClh<T> implements Buffer<T> {

	/**
	 * Dobre performanse
	 */
	
	@Override
	public void put(T data) {
		lock.lock();
		try {
			if (q.isEmpty() && !waitingQ.isEmpty()) {
				waitingQ.poll().signal();
			}
			q.add(data);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T get() {
		T data;

		lock.lock();
		try {
			if (cntW > 0 || q.isEmpty()) {
				cntW++;
				Condition c = lock.newCondition();
				waitingQ.add(c);
				c.awaitUninterruptibly();
				cntW--;
			}
			data = q.poll();
			if (!q.isEmpty() && !waitingQ.isEmpty())
				waitingQ.poll().signal();
		} finally {
			lock.unlock();
		}

		return data;
	}


	private Queue<T> q = new LinkedList<>();
	
	private Queue<Condition> waitingQ = new LinkedList<>();

	private Lock lock = new ReentrantLock();

	private int cntW = 0;
}
