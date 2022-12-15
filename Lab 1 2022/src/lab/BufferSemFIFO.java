package lab;

import java.util.concurrent.Semaphore;

public class BufferSemFIFO<T> implements Buffer<T> {
	/**
	 * Najbolja varijanta - dosta konkurentno.
	 * Dobijanje mutexa nije fifo pa ako se zeli to postignuti moze se staviti 
	 * fer vrednost semafora na true (verovatno nije bitno - samo da prvi koji dobije pravo pristupa
	 * i prvi dobije podatak).
	 */

	public BufferSemFIFO() {
		for (int i = 0; i < size; i++) {
			full[i] = new Semaphore(0, true);
			empty[i] = new Semaphore(1, true);
		}
	}

	@Override
	public void put(T data) {
		mutexTail.acquireUninterruptibly();
		
		int ind = tail++;
		tail %= size;
		
		mutexTail.release();

		empty[ind].acquireUninterruptibly();

		list[ind] = data;

		full[ind].release();
	}

	@Override
	public T get() {
		mutexHead.acquireUninterruptibly();

		int ind = head++;
		head %= size;

		mutexHead.release();

		full[ind].acquireUninterruptibly();

		T data = list[ind];

		empty[ind].release();

		return data;
	}

	private int size = 5000;
	private Semaphore mutexTail = new Semaphore(1), mutexHead = new Semaphore(1);
	private Semaphore full[] = new Semaphore[size];
	private Semaphore empty[] = new Semaphore[size];

	@SuppressWarnings("unchecked")
	private T[] list = (T[]) new Object[size];
	private int head = 0, tail = 0;
}
