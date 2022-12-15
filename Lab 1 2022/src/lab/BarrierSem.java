package lab;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BarrierSem implements Barrier {

	public BarrierSem(int n) {
		this.n = n;
	}

	@Override
	public void arrive() {
		mutex.acquireUninterruptibly();
		cnt++;
		if (cnt == n) {
			awaiting.release(waiting);
		}
		mutex.release();
	}

	@Override
	public boolean await(long ms) {
		boolean acquired = false;

		mutex.acquireUninterruptibly();

		if (cnt == n) {
			acquired = true;
			mutex.release();
		} else {
			mutex.release();
			try {
				acquired = awaiting.tryAcquire(ms, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
			if (!acquired) {
				mutex.acquireUninterruptibly();
				waiting--;
				mutex.release();
			}
		}

		return acquired;
	}

	@Override
	public void await() {
		mutex.acquireUninterruptibly();

		if (cnt != n) {
			mutex.release();
			waiting++;
			awaiting.acquireUninterruptibly();
		} else {
			mutex.release();
		}
	}

	private Semaphore mutex = new Semaphore(1);
	private Semaphore awaiting = new Semaphore(0);
	private int n;
	private int cnt = 0;
	private int waiting = 0;
}
