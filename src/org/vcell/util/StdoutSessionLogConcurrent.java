package org.vcell.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

/**
 * use Java concurrent capability to make {@link SessionLog} methods as
 * fast as possible.
 * 
 * Uses "local" hostname
 */
public class StdoutSessionLogConcurrent extends StdoutSessionLogA {

	private final LinkedBlockingQueue<String> messageQueue;
	private static final Logger LG = Logger.getLogger(StdoutSessionLogConcurrent.class);
	private final AtomicBoolean shutdown; 
	private final ThreadThatWrites writerThread;
	protected final PrintWriter out; 

	public StdoutSessionLogConcurrent(String userid, OutputStream outStream) {
		super(userid);
		messageQueue = new LinkedBlockingQueue<>();
		shutdown = new AtomicBoolean(false);
		writerThread = new ThreadThatWrites();
		out = new PrintWriter(outStream);
		init( );
	}
	
	
	/**
	 * use {@link #localHostInfo()}
	 */
	@Override
	protected String hostInfo() {
		return localHostInfo();
	}


	private void init( ) {
		writerThread.start();
		Runtime.getRuntime().addShutdownHook(new EndOfLife());
	}
	
	
	@Override
	protected void output(String message) {
		messageQueue.add(message);
	}
	
	/**
	 * thread safe write to {@link StdoutSessionLogA#out} 
	 */
	private class ThreadThatWrites extends Thread {
		ThreadThatWrites( ) {
			super("StdoutSessionLogS Writer");
			setDaemon(true);
		}

		@Override
		public void run() {
			LG.debug("Writer begin");
			for (;;) {
				String m = null;
				try {
					m = messageQueue.take();
					out.print(m);
					if (interrupted() && shutdown.get()) {
						if (LG.isDebugEnabled()) {
							LG.debug("Writer shutdown, flag, last msg " + m);
						}
						
						out.flush();
						 //we been told to exit
						return;
					}
				} catch (InterruptedException e) {
					if (shutdown.get()) { //have we been told to exit?
						if (LG.isDebugEnabled()) {
							LG.debug("Writer shutdown, exception, last msg " + m,e);
						}
						out.flush();
						return;
					}
					out.append(getName( ));
					out.append(" interrupt");
					e.printStackTrace(out);
					LG.debug("Writer exception",e);
				}
			}
		}
	}
	
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
			String firstMsg = messageQueue.poll( );
			out.print(firstMsg);
			LG.debug("first " + firstMsg);
			String lastMsg = null;
			while (!messageQueue.isEmpty()) {
				String m = messageQueue.poll( );
				LG.trace(m);
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
}
