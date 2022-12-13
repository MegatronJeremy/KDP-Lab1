package lab;

public interface AtomicBroadcastBuffer<T> {
	void put(T data);
	T get(int id);
}
