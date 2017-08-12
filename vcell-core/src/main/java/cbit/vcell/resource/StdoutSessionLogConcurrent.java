package cbit.vcell.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.vcell.util.LifeSignThread;
import org.vcell.util.SessionLog;

/**
 * use Java concurrent capability to make {@link SessionLog} methods as
 * fast as possible.
 * 
 * Uses "local" hostname
 */
public class StdoutSessionLogConcurrent extends StdoutSessionLogA {
	/**
	 * flush after this many messages or {@link #FLUSH_TIME}, whichever occurs first 
	 */
	private static final int FLUSH_COUNT = 1000;
	/**
	 * flush after this much time, or {@link #FLUSH_COUNT}, whichever occurs first
	 */
	private static final long FLUSH_TIME = 2;
	/**
	 *  Units of {@link #FLUSH_TIME} 
	 */
	private static final TimeUnit FLUSH_TIME_UNITS = TimeUnit.MINUTES; 
	/**
	 * token to sign flush event
	 */
	private static final String FLUSH_NOW = "";
	/**
	 * log these first messages immediately
	 */
	private static final int FIRST_MESSAGES = 200; 

	private final LinkedBlockingQueue<String> messageQueue;
	private static final Logger LG = Logger.getLogger(StdoutSessionLogConcurrent.class);
	private final AtomicBoolean shutdown; 
	private final ThreadThatWrites writerThread;
	protected final PrintWriter out; 
	private PrintStream psFacade; 
	
	public static class LifeSignInfo {
		/**
		 * message to post
		 */
		private final String message;
		/**
		 * how often
		 */
		private final long timeInterval; 
		/**
		 * units of {@link #timeInterval}
		 */
		private final TimeUnit timeUnit;
		public LifeSignInfo( ) {
			this.message = LifeSignThread.ALIVE_MESSAGE; 
			this.timeInterval = FLUSH_TIME;
			this.timeUnit = FLUSH_TIME_UNITS;
		}
		public LifeSignInfo(String message, int timeInterval, TimeUnit timeUnit) {
			this.message = message;
			this.timeInterval = timeInterval;
			this.timeUnit = timeUnit;
		} 
		
	}

	public StdoutSessionLogConcurrent(String userid, OutputStream outStream, LifeSignInfo lifeSignInfo) {
		super(userid);
		messageQueue = new LinkedBlockingQueue<>();
		shutdown = new AtomicBoolean(false);
		writerThread = new ThreadThatWrites();
		out = new PrintWriter(outStream);
		psFacade = null;
		writerThread.start();
		Runtime.getRuntime().addShutdownHook(new EndOfLife());
		VCellExecutorService.get( ).scheduleAtFixedRate(new TimeFlushLifeSign(lifeSignInfo),lifeSignInfo.timeInterval,lifeSignInfo.timeInterval,
				lifeSignInfo.timeUnit);
	}
	
	/**
	 * @return PrintStream that utilizes concurrent facility
	 */
	public PrintStream printStreamFacade( )  {
		if (psFacade == null) {
			psFacade = new PrintStream(new ConcurrentOutputStream());
		}
		return psFacade;
	}
	
	
	/**
	 * use {@link #localHostInfo()}
	 */
	@Override
	protected String hostInfo() {
		return localHostInfo();
	}
	
	@Override
	protected void output(String message) {
		messageQueue.add(message);
	}
	
	/**
	 * thread safe write to {@link StdoutSessionLogA#out} 
	 */
	private class ThreadThatWrites extends Thread {
		int messageCount;
		ThreadThatWrites( ) {
			super("StdoutSessionLogS Writer");
			setDaemon(true);
			messageCount = 0;
		}
		
		private void flush( ) {
			out.flush();
			messageCount = 0;
		}

		@Override
		public void run() {
			LG.debug("Writer begin");
			// log immediately on startup
			for (int initialCount = 0; initialCount < FIRST_MESSAGES; initialCount++) {
				String m = null; 
				try {
					m = messageQueue.take( );
					out.print(m);
					if (initialCount%5 == 0) {
						out.flush();
					}
				} catch (InterruptedException e) {
					if (testForShutdown(e, m) ) {
						return;
					}
				}
			}
			LG.debug("shifting to buffered mode");
			out.flush();

			for (;;) {
				String m = null;
				try {
					m = messageQueue.take();
					if (m != FLUSH_NOW) {
						out.print(m);
						if (++messageCount >= FLUSH_COUNT) {
							if (LG.isDebugEnabled()) {
								LG.debug("message count flush: " + messageCount + " limit:  " + FLUSH_COUNT);
							}
							flush();
						}
					} else {
						if (LG.isDebugEnabled()) {
							LG.debug("token flush: " + messageCount + " limit:  " + FLUSH_COUNT);
						}
						flush();
					}

					if (interrupted() && shutdown.get()) {
						if (LG.isDebugEnabled()) {
							LG.debug("Writer shutdown, flag, last msg " + m);
						}

						out.flush();
						// we been told to exit
						return;
					}
				} catch (InterruptedException e) {
					if (testForShutdown(e, m)) {
						return;
					}
				}
			}
		}

		private boolean testForShutdown(InterruptedException e, String m) {
			if (shutdown.get()) { // have we been told to exit?
				if (LG.isDebugEnabled()) {
					LG.debug("Writer shutdown, exception, last msg " + m, e);
				}
				out.flush();
				return true;
			}
			out.append(getName());
			out.append(" interrupt");
			e.printStackTrace(out);
			LG.debug("Writer exception", e);
			return false;
		}
	}
	
	/**
	 * combine functionality of {@link org.vcell.util.LifeSignThread} with
	 * flushing queue based on time
	 */
	private class TimeFlushLifeSign implements Runnable {
		final LifeSignInfo lsi;

		TimeFlushLifeSign(LifeSignInfo lsi) {
			Objects.requireNonNull(lsi);
			this.lsi = lsi;
		}
		
		@Override
		public void run() {
			for (;;) {
				try {
					lsi.timeUnit.sleep(lsi.timeInterval);
					StdoutSessionLogConcurrent.this.alert(lsi.message);
					StdoutSessionLogConcurrent.this.output(FLUSH_NOW);
				} catch (InterruptedException e) {
					LG.warn(getClass( ).getName() + " interrupted",e);
				}
			}
		}
	}
	
	/**
	 * shutdown hook thread that sends interrupt to {@link ThreadThatWrites}
	 */
	private class EndOfLife extends Thread {
		EndOfLife( ) {
			super("StdoutSessionLogS EndOfLife");
		}
		
		@Override
		public void run() {
			LG.debug("EndOfLife starting");
			shutdown.set(true);
			writerThread.interrupt();
			while (writerThread.isAlive()) {}
			String endOfLife = "eol"; // marker String to indicate shutdown has been triggered 
			messageQueue.add(endOfLife);
			int c = 0;
			String lastMsg = null;
			while (!messageQueue.isEmpty()) {
				String m = messageQueue.poll( );
				LG.debug(m);
				if (m == endOfLife) { //don't process Strings after shutdown invoked
					if (LG.isDebugEnabled()) {
						LG.debug("Processed " + c + " post shutdown messages, last was " + lastMsg);
					}
					return;
				}
				out.print(m);
				lastMsg = m;
				c++;
			}
		}
	}
	
	/**
	 * OutputStream which writes to the concurrent queue when
	 * newline (10) received
	 */
	private class ConcurrentOutputStream extends OutputStream {
		ThreadLocal<ByteArrayOutputStream> accumulator;
		public ConcurrentOutputStream() {
			accumulator = new TLByteArrayOutputStream();
		}

		@Override
		public void write(int byteAsInt) throws IOException {
			byte b = (byte) byteAsInt;
			ByteArrayOutputStream baos = accumulator.get();
			baos.write(b);
			if (b == 10) {
				String msg = baos.toString();
				baos.reset( );
				output(msg);
			}
		}
		
	}
	
	/**
	 * {@link ThreadLocal} {@link ByteArrayOutputStream}
	 */
	private static class TLByteArrayOutputStream extends ThreadLocal<ByteArrayOutputStream> {
		/**
		 * see {@link ThreadLocal#initialValue}
		 * @return new {@link ByteArrayOutputStream} 
		 */
		@Override
		protected ByteArrayOutputStream initialValue() {
			return new ByteArrayOutputStream(); 
		}
	}
	
	
}
