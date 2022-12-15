package lab;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BufferSemFifoCLH<T> implements Buffer<T> {

	/**
	 * Komparativno - i cak malo brze od Andersenovog algoritma.
	 */
	
	@Override
	public void put(T data) {
		mutex.acquireUninterruptibly();

		q.add(data);

		if (!waitingQ.isEmpty()) {
			waitingQ.poll().release();
		} else {
			mutex.release();
		}
	}

	@Override
	public T get() {
		mutex.acquireUninterruptibly();

		if (q.isEmpty() || !waitingQ.isEmpty()) {
			Semaphore s = new Semaphore(0);
			waitingQ.add(s);

			mutex.release();
			s.acquireUninterruptibly();
		}

		T data = q.remove();

		if (!q.isEmpty() && !waitingQ.isEmpty()) {
			waitingQ.poll().release();
		} else {
			mutex.release();
		}

		return data;
	}

	private Semaphore mutex = new Semaphore(1);

	private Queue<T> q = new LinkedList<>();
	private Queue<Semaphore> waitingQ = new LinkedList<>();
}
