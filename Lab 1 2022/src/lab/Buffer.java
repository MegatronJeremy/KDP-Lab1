package lab;

public interface Buffer<T> {
	void put(T data);
	T get();
}
