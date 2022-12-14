package lab;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AtomicBroadcastBufferLock<T> implements AtomicBroadcastBuffer<T> {

	@SuppressWarnings("unchecked")
	public AtomicBroadcastBufferLock(int n, int size) {
		this.size = size;
		this.n = n;

		items = new int[n];
		itemsA = new Condition[n];

		slots = new int[size];

		buf = (T[]) new Object[size];

		for (int i = 0; i < n; i++) {
			itemsA[i] = lock.newCondition();
		}
		for (int i = 0; i < size; i++) {
			slots[i] = n;
		}
	}

	@Override
	public void put(T data) {
		lock.lock();
		try {
			if (slots[wi] < n) {
				slotsA.awaitUninterruptibly();
			}
			slots[wi] = 0;
		} finally {
			lock.unlock();
		}

		buf[wi] = data;

		lock.lock();
		try {
			for (int i = 0; i < n; i++) {
				items[i]++;
				itemsA[i].signal();
			}
		} finally {
			lock.unlock();
		}

		wi = (wi + 1) % size;

	}

	@Override
	public T get(int id) {
		int ri = riL.get();

		lock.lock();
		try {
			if (items[id] == 0) {
				itemsA[id].awaitUninterruptibly();
			}
			items[id]--;
		} finally {
			lock.unlock();
		}

		T data = buf[ri];

		lock.lock();
		try {
			slots[ri]++;
			if (slots[ri] == n)
				slotsA.signal();
		} finally {
			lock.unlock();
		}

		riL.set((ri + 1) % size);

		return data;
	}

	private T[] buf;
	private int size;
	private int n;
	private Lock lock = new ReentrantLock();
	private Condition slotsA = lock.newCondition();
	private Condition itemsA[];
	private int[] items;
	private int[] slots;
	private int wi;
	private ThreadLocal<Integer> riL = ThreadLocal.withInitial(() -> 0);

}
