package lab;

public class BarrierRegion implements Barrier {

	public BarrierRegion(int n) {
		this.n = n;
	}

	@Override
	public void arrive() {
		synchronized (lock) {
			arrived++;
			if (arrived == n)
				lock.notifyAll();
		}
	}

	@Override
	public boolean await(long ms) {
		synchronized (lock) {
			if (arrived != n) {
				try {
					lock.wait(ms);
				} catch (InterruptedException e) {
				}
			}
		}
		return arrived == n;
	}

	@Override
	public void await() {
		synchronized (lock) {
			while (arrived != n) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private final Object lock = new Object();
	int n;
	int arrived = 0;
}
