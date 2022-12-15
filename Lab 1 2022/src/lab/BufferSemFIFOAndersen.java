package lab;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BufferSemFIFOAndersen<T> implements Buffer<T> {

	public BufferSemFIFOAndersen(int n) {
		this.n = n;
		sems = new Semaphore[n];
		
		for (int i = 0; i < n; i++) {
			sems[i] = new Semaphore(0);
		}
		
	}

	@Override
	public void put(T data) {
		mutex.acquireUninterruptibly();

		q.add(data);

		if (cnt > 0) {
			cnt--;
			int ind = ri++;
			ri %= n;
			sems[ind].release();
		} else {
			mutex.release();
		}
	}

	@Override
	public T get() {
		mutex.acquireUninterruptibly();

		if (q.isEmpty() || cnt > 0) {
			cnt++;
			int ind = wi++;
			wi %= n;
			mutex.release();
			sems[ind].acquireUninterruptibly();
		}

		T data = q.remove();
		
		if (!q.isEmpty() && cnt > 0) {
			cnt--;
			int ind = ri++;
			ri %= n;
			sems[ind].release();
		} else {
			mutex.release();
		}

		return data;
	}

	private int n;
	private int cnt = 0;
	private Semaphore sems[];
	private int wi = 0, ri = 0;
	private Semaphore mutex = new Semaphore(1);

	private Queue<T> q = new LinkedList<>();

}
