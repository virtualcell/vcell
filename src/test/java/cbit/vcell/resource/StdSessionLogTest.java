package cbit.vcell.resource;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.vcell.util.SessionLog;
import org.vcell.util.logging.Logging;

import cbit.vcell.resource.StdoutSessionLogA;
import cbit.vcell.resource.StdoutSessionLogConcurrent;
import cbit.vcell.resource.StdoutSessionLogConcurrent.LifeSignInfo;

/**
 * 
 * load testing {@link StdoutSessionLogA} implementations 
 */
public class StdSessionLogTest {
	
	private static final boolean TEST_STD_OUT = true;

	public static void main(String[] args) {
		try {
			if (args.length >= 4) {
				setup(args);
			}
			else {
				showUsage( );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void showUsage( ) {
		System.out.println("usage: " + StdSessionLogTest.class.getCanonicalName() 
				+ " [reg|con] [number threads] [run for (seconds)] [file name] (millisescond wait on threads");
	}
	
	@SuppressWarnings("resource")
	private static void setup(String[] args) throws Exception{
		Logging.init();
		PrintStream output = new PrintStream(args[3]);
		SessionLog log = null;
		String type = args[0];
		if (type.equals("reg")) {
			log = new StdoutSessionLog("stdlog", output);
			if (TEST_STD_OUT) {
				PrintStream ps = new PrintStream(new FileOutputStream("console.txt"),true);
				System.setOut(ps);
			}
		}
		else if (type.equals("con")) {
			StdoutSessionLogConcurrent a = new StdoutSessionLogConcurrent("conlog",output, new LifeSignInfo( ));
			if (TEST_STD_OUT) {
				PrintStream ad = a.printStreamFacade(); 
				System.setOut(ad);
				System.out.print("h");
			}
			log = a; 
		}
		else {
			showUsage();
			return;
		}
		final int nThreads = Integer.parseInt(args[1]);
		final int nSeconds = Integer.parseInt(args[2]);
		Thread babblers[] = new Thread[nThreads];
		if (args.length > 4) {
			final long milliWait = Long.parseLong(args[4]);
			for (int t = 0; t < nThreads; t++) {
				babblers[t] = new BabblerWait(log, t,milliWait);
			}
		}
		else {
			for (int t = 0; t < nThreads; t++) {
				babblers[t] = new Babbler(log, t);
			}

		}
		for (int t = 0; t < nThreads; t++) {
			babblers[t].start();
		}
		System.err.println("Sleeping " + nSeconds);
		Thread.sleep(1000 * nSeconds);
		log.print("exiting main");
		System.err.println("End of sleep");
	}

	private static class Babbler extends Thread {
		final SessionLog log;
		final int id;

		Babbler(SessionLog log, int i) {
			super("Babbler " + i);
			this.log = log;
			id = i;
			setDaemon(true);
		}

		@Override
		public void run() {
			int i = 1;
			for (;;) {
				if (TEST_STD_OUT) {
					System.out.println(Integer.toString(id) + " tells console " +  i + " time");
				}
				log.print(Integer.toString(id) + " says hello " + ( i++ ) + " time");
			}
		}
	}
	
	private static class BabblerWait extends Thread {
		final SessionLog log;
		final int id;
		final long milliWait;

		BabblerWait(SessionLog log, int i, long milliWait) {
			super("Babbler " + i);
			this.log = log;
			id = i;
			this.milliWait = milliWait;
			setDaemon(true);
		}

		@Override
		public void run() {
			int i = 1;
			for (;;) {
				log.print(Integer.toString(id) + " says hello " + ( i++ ) + " time");
				try {
					Thread.sleep(milliWait);
				} catch (InterruptedException e) {}
			}
		}
	}
}
