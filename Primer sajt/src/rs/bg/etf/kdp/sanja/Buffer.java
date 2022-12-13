package rs.bg.etf.kdp.sanja;

public interface Buffer<T> {
	public void put(T data);
	public T get();
	public T poll();
}
