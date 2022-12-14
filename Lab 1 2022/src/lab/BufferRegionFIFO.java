package lab;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Ne bi bilo lose izdvojiti citanje i upis van regiona - procita indeks i iz
 * niza uzme svoj podatak - veca konkurentnost nego Queue<T> (ali potrebne
 * dodatne promenljive).
 */

public class BufferRegionFIFO<T> implements Buffer<T> {

	@Override
	public void put(T data) {
		synchronized (q) {
			q.add(data);
			q.notifyAll();
		}
	}

	@Override
	public T get() {
		T data;
		synchronized (q) {
			long myT = ticket++;
			while (q.isEmpty() || myT != next) {
				try {
					q.wait();
				} catch (InterruptedException e) {
				}
			}
			next++;
			if (ticket != next && !q.isEmpty())
				q.notifyAll();
			else if (ticket == next)
				ticket = next = 0;

			data = q.poll();
		}

		return data;
	}

	private Queue<T> q = new LinkedList<>();
	private long ticket = 0, next = 0;

}
