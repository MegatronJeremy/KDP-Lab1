package lab;

public class AtomicBroadcastBufferRegion<T> implements AtomicBroadcastBuffer<T> {

	@SuppressWarnings("unchecked")
	public AtomicBroadcastBufferRegion(int n, int size) {
		this.n = n;
		this.size = size;
		itemsA = new int[n];
		slotsA = new int[size];
		buf = (T[]) new Object[size];
		for (int i = 0; i < size; i++) {
			slotsA[i] = n;
		}
	}

	@Override
	public void put(T data) {
		synchronized (lock) {
			while (slotsA[wi] < n) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
			slotsA[wi] = 0;
		}

		buf[wi] = data;

		synchronized (lock) {
			for (int i = 0; i < n; i++) {
				itemsA[i]++;
			}
			lock.notifyAll();
		}

		wi = (wi + 1) % size;
	}

	@Override
	public T get(int id) {
		int ri = riL.get();

		synchronized (lock) {
			while (itemsA[id] == 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
			itemsA[id]--;
		}

		T data = buf[ri];

		synchronized (lock) {
			slotsA[ri]++;
			if (slotsA[ri] == n)
				lock.notifyAll();
		}

		riL.set((ri + 1) % size);
		return data;
	}

	private Object lock = new Object();
	private int[] itemsA;
	private int[] slotsA;
	private T[] buf;
	private int n;
	private int size;
	private int wi = 0;
	ThreadLocal<Integer> riL = ThreadLocal.withInitial(() -> 0);

}
