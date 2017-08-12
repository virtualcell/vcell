package cbit.vcell.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * periodically flush Writers./
 * useful if running code that might crash JVM 
 * @author gweatherby
 *
 */
public class WriterFlusher extends Thread {
	private FlushTask task;
	long delay; //in milliseconds
	Timer timer;

	/**
	 * @param delay seconds between flushes
	 */
	public WriterFlusher(long delay) {
		this.delay = delay * 1000;
		task = new FlushTask();
		timer = null;
	}

	/**
	 * add a writer
	 * @param w
	 */
	public void add(Writer w) {
		synchronized(task) {
			task.writers.add(w);
			if (timer == null) {
				timer = new Timer(true); //daemon thread created
				timer.schedule(task, delay, delay);
			}
		}
	}

	private static class FlushTask extends TimerTask {
		ArrayList<Writer> writers = new ArrayList<Writer>( );

		@Override
		public void run() {
			synchronized(this) {
				for (Writer w : writers) {
					try {
						w.flush();
					} catch (IOException e) { } //let's not worry about it
				}
			}
		}
	}









}
