package lab;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BufferSemFifoCLH<T> implements Buffer<T> {

	/**
	 * Veoma brzo ako se ponasa kao signal and continue monitor NAJBRZE RESENJE ZA
	 * SEMAFOR
	 */

	@Override
	public void put(T data) {
		mutex.acquireUninterruptibly();

		q.add(data);

		if (q.size() == 1 && !waitingQ.isEmpty()) {
			waitingQ.poll().release();
		}

		mutex.release();
	}

	@Override
	public T get() {
		mutex.acquireUninterruptibly();

		if (cntW > 0 || q.isEmpty()) {
			cntW++;
			Semaphore s = new Semaphore(0);
			waitingQ.add(s);

			mutex.release();
			s.acquireUninterruptibly();

			mutex.acquireUninterruptibly();

			cntW--;
		}

		T data = q.remove();

		if (!q.isEmpty() && !waitingQ.isEmpty()) {
			waitingQ.poll().release();
		}

		mutex.release();

		return data;
	}

	private Semaphore mutex = new Semaphore(1);

	private int cntW = 0;

	private Queue<T> q = new LinkedList<>();

	private Queue<Semaphore> waitingQ = new LinkedList<>();
}
