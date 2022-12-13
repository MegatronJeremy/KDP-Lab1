package rs.bg.etf.kdp.sanja;

public interface Barrier {
	void arrive();
	void await();
	boolean await(int ms);
}
