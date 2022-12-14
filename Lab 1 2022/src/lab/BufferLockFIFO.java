package lab;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferLockFIFO<T> implements Buffer<T> {

	public BufferLockFIFO() {
		for (int i = 0; i < size; i++) {
			full[i] = lock.newCondition();
			empty[i] = lock.newCondition();
			slots[i] = 1;
		}
	}

	@Override
	public void put(T data) {
		int ind;

		lock.lock();
		try {
			ind = tail++;
			tail %= size;
			if (slots[ind] == 0) {
				empty[ind].awaitUninterruptibly();
			}
			slots[ind]--;
		} finally {
			lock.unlock();
		}

		list[ind] = data;

		lock.lock();
		try {
			items[ind]++;
			full[ind].signal();
		} finally {
			lock.unlock();
		}

	}

	@Override
	public T get() {
		int ind;

		lock.lock();
		try {
			ind = head++;
			head %= size;

			if (items[ind] == 0) {
				full[ind].awaitUninterruptibly();
			}

			items[ind]--;
		} finally {
			lock.unlock();
		}

		T data = list[ind];

		lock.lock();
		try {
			slots[ind]++;
			empty[ind].signal();
		} finally {
			lock.unlock();
		}

		return data;
	}

	private int size = 50000;
	private Lock lock = new ReentrantLock();
	private Condition full[] = new Condition[size];
	private Condition empty[] = new Condition[size];

	private T[] list = (T[]) new Object[size];
	private int head = 0, tail = 0;
	private int[] slots = new int[size];
	private int[] items = new int[size];

}
