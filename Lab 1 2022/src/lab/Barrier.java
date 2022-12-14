package lab;

public interface Barrier {
	public void arrive();

	public boolean await(long ms);

	public void await();
}
