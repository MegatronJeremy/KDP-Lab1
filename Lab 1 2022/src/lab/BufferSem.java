package lab;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BufferSem<T> implements Buffer<T> {

	@Override
	public void put(T data) {
		mutex.acquireUninterruptibly();
		q.add(data);
		items.release();
		mutex.release();
	}

	@Override
	public T get() {
		items.acquireUninterruptibly();
		mutex.acquireUninterruptibly();
		T data = q.poll();
		mutex.release();
		return data;
	}

	private Semaphore mutex = new Semaphore(1);
	private Semaphore items = new Semaphore(0);
	private Queue<T> q = new LinkedList<>();

}
