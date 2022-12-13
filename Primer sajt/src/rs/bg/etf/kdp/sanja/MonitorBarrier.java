package rs.bg.etf.kdp.sanja;

public class MonitorBarrier implements Barrier {

	public MonitorBarrier(int n) {
		this.n = n;
	}

	@Override
	public synchronized void arrive() {
		cnt++;
		if (cnt == n) {
			notify();
		}
	}

	@Override
	public synchronized void await() {
		await(0);
	}

	@Override
	public synchronized boolean await(int ms) {
		if (ms >= 0 && cnt != n) {
			try {
				wait(ms);
			} catch (InterruptedException e) {
			}
		}
//		System.out.println(cnt + " " + n);
		return cnt == n;
	}

	private int cnt = 0;
	private int n;

}
