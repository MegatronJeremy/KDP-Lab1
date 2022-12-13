package lab;

public interface Barrier {
	void await();

	boolean await(long ms);

	void arrive();
}
