package rs.ac.bg.etf.kdp.lab;

public interface MessageBox<T> {

	/**
	 * Insert message to buffer, blocking if the buffer is full.
	 * 
	 * @param msg          message to be put in buffer
	 * @param priority     prioritu of the message; lower number is higher priority
	 * @param timeToLiveMs number of milliseconds for which the message can be read;
	 *                     if more time passed, message is no longer valid; 0 means
	 *                     lives forever
	 */
	void put(T msg, int priority, int timeToLiveMs);
	
	T get(int timeToWait, Status status);

}
