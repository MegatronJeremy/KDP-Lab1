package lab;

import java.util.concurrent.Semaphore;

public class AtomicBroadcastBufferSem<T> implements AtomicBroadcastBuffer<T> {

	@SuppressWarnings("unchecked")
	public AtomicBroadcastBufferSem(int n, int size) {
		this.n = n;
		this.size = size;
		itemsA = new Semaphore[n];
		slotsA = new Semaphore[size];
		buf = (T[]) new Object[size];
		for (int i = 0; i < n; i++) {
			itemsA[i] = new Semaphore(0);
		}
		for (int i = 0; i < size; i++) {
			slotsA[i] = new Semaphore(n);
		}
	}

	@Override
	public void put(T data) {
		slotsA[wi].acquireUninterruptibly(n);

		buf[wi] = data;
		wi = (wi + 1) % size;

		for (int i = 0; i < n; i++) {
			itemsA[i].release();
		}
	}

	@Override
	public T get(int id) {
		T data;
		int ri = riL.get();
		itemsA[id].acquireUninterruptibly();
		data = buf[ri];
		slotsA[ri].release();
		riL.set((ri + 1) % size);
		return data;
	}

	private Semaphore[] itemsA;
	private Semaphore[] slotsA;
	private T[] buf;
	private int n;
	private int size;
	private int wi = 0;
	ThreadLocal<Integer> riL = ThreadLocal.withInitial(() -> 0);

}
