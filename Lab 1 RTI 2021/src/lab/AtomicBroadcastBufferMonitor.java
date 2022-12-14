package lab;

public class AtomicBroadcastBufferMonitor<T> implements AtomicBroadcastBuffer<T> {

	@SuppressWarnings("unchecked")
	public AtomicBroadcastBufferMonitor(int n, int size) {
		this.n = n;
		this.size = size;
		slotsA = new int[size];
		itemsA = new int[n];
		buf = (T[]) new Object[size];
		for (int i = 0; i < size; i++) {
			slotsA[i] = n;
		}
	}

	@Override
	public synchronized void put(T data) {
		while (slotsA[wi] < n) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		slotsA[wi] = 0;

		buf[wi] = data;
		wi = (wi + 1) % size;

		for (int i = 0; i < n; i++) {
			itemsA[i]++;
		}
		notifyAll();
	}

	@Override
	public synchronized T get(int id) {
		T data;
		int ri = riL.get();
		while (itemsA[id] == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		itemsA[id]--;

		data = buf[ri];

		slotsA[ri]++;
		if (slotsA[ri] == n)
			notifyAll();

		riL.set((ri + 1) % size);
		return data;
	}

	private int[] itemsA;
	private T[] buf;
	private int n;
	private int[] slotsA;
	private int size;
	private int wi = 0;
	ThreadLocal<Integer> riL = ThreadLocal.withInitial(() -> 0);

}
