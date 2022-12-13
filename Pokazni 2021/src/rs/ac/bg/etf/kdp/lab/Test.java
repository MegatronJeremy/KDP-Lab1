package rs.ac.bg.etf.kdp.lab;

public class Test {

	public static void main(String[] args) {

		MessageBox<Integer> buffer = new MonitorMessageBox<>(7);

		for (int i = 0; i < 10; i++) {
			Producer p = new Producer(buffer);
			p.start();
		}

		for (int i = 0; i < 10; i++) {
			Consumer c = new Consumer(buffer);
			c.start();
		}

	}

}
