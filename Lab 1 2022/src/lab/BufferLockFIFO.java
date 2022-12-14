package lab;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferLockFIFO<T> implements Buffer<T> {
	
	/**
	 *	Efikasnije mnogo od cistog ticket algoritma - ali slozenije. 
	 *	Ticket algoritam bi izgledao isto kao za regione - samo uz lock i jedan condition.
	 *	@param - broj niti consumera
	 */

	public BufferLockFIFO(int size) {
		this.size = size;
		c = new Condition[size];
		for (int i = 0; i < size; i++) {
			c[i] = lock.newCondition();
		}
	}

	@Override
	public void put(T data) {
		lock.lock();
		try {
			if (q.isEmpty() && cnt > 0)
				signalNext();
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
			long myT = ticket++;
			if (myT != next || q.isEmpty()) {
				cnt++;
				int ind = wi++;
				wi %= size;
				c[ind].awaitUninterruptibly();
			}
			next++;
			data = q.poll();
			if (!q.isEmpty() && cnt > 0)
				signalNext();
		} finally {
			lock.unlock();
		}

		return data;
	}

	private void signalNext() {
		cnt--;
		c[ri++].signal();
		ri %= size;
	}

	private Queue<T> q = new LinkedList<>();
	private Condition[] c;

	private Lock lock = new ReentrantLock();

	private int ri = 0, wi = 0;
	private int cnt = 0;
	private int size;
	long ticket = 0, next = 0;
}
