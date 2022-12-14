package lab;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BufferSem<T> implements Buffer<T> {

	@Override
	public void put(T data) {
		mutexTail.acquireUninterruptibly();
		q.add(data);
		items.release();
		mutexTail.release();
	}

	@Override
	public T get() {
		items.acquireUninterruptibly();
		mutexHead.acquireUninterruptibly();
		T data = q.poll();
		mutexHead.release();
		return data;
	}

	private Semaphore mutexHead = new Semaphore(1);
	private Semaphore mutexTail = new Semaphore(1);
	private Semaphore items = new Semaphore(0);
	private Queue<T> q = new LinkedList<>();

}
