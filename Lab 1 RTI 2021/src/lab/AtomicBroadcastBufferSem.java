package lab;

import java.util.concurrent.Semaphore;

public class AtomicBroadcastBufferSem<T> implements AtomicBroadcastBuffer<T> {

	@SuppressWarnings("unchecked")
	public AtomicBroadcastBufferSem(int n, int size) {
		this.n = n;
		this.size = size;
		itemsA = new Semaphore[n];
		slotsA = new Semaphore(n * size);
		buf = (T[]) new Object[size];
		for (int i = 0; i < n; i++) {
			itemsA[i] = new Semaphore(0);
		}
	}

	@Override
	public void put(T data) {
		slotsA.acquireUninterruptibly(n);
		buf[wi] = data;
		for (int i = 0; i < n; i++) {
			itemsA[i].release();
		}
		wi = (wi + 1) % size;
	}

	@Override
	public T get(int id) {
		T data;
		int ri = riL.get();
		itemsA[id].acquireUninterruptibly();
		data = buf[ri];
		slotsA.release();
		riL.set((ri + 1) % size);
		return data;
	}

	private Semaphore[] itemsA;
	private Semaphore slotsA;
	private T[] buf;
	private int n;
	private int size;
	private int wi = 0;
	ThreadLocal<Integer> riL = ThreadLocal.withInitial(() -> 0);

}
