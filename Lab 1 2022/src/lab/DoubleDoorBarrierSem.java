package lab;

import java.util.concurrent.Semaphore;

public class DoubleDoorBarrierSem implements DoubleDoorBarrier {

	public DoubleDoorBarrierSem(int n) {
		this.n = n;

		sem1 = new Semaphore(n);
		sem2 = new Semaphore(0);
	}

	@Override
	public void pass() {
		sem1.acquireUninterruptibly();

		cnt++;
		if (cnt == n) {
			sem2.release();
		} else {
			sem1.release();
		}

		sem2.acquireUninterruptibly();

		cnt--;

		if (cnt == 0) {
			sem1.release();
		} else {
			sem2.release();
		}
	}

	private int n;
	private int cnt = 0;

	private Semaphore sem1, sem2;

}
