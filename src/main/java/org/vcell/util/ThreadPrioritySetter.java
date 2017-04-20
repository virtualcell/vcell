package org.vcell.util;

/**
 * set Thread priority temporarily using try with resources idiom
 * @author gweatherby
 *
 */
public class ThreadPrioritySetter implements AutoCloseable {
	private final Thread invoker;
	private final int basePriority;

	/**
	 * temporarily set thread priority
	 * @param priority
	 */
	public ThreadPrioritySetter(int priority) {
		invoker = Thread.currentThread();
		basePriority = invoker.getPriority();
		invoker.setPriority(priority);
	}

	/**
	 * restore prior thread priority
	 */
	@Override
	public void close() throws Exception {
		if (Thread.currentThread() != invoker) {
			throw new ProgrammingException(ThreadPrioritySetter.class.getName() + " closed on different thread");
		}
		invoker.setPriority(basePriority);
	}
}
