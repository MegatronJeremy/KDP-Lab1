package lab;

public class BarrierMonitor implements Barrier {

	public BarrierMonitor(int n) {
		this.n = n;
	}

	@Override
	public synchronized void arrive() {
		cnt++;
		if (cnt == n)
			notifyAll();
	}

	@Override
	public synchronized boolean await(long ms) {
		if (cnt != n) {
			try {
				wait(ms);
			} catch (InterruptedException e) {
			}
		}

		return cnt == n;
	}

	@Override
	public synchronized void await() {
		while (cnt != n) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	int n;
	int cnt = 0;

}
