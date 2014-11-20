package org.vcell.util;

import java.util.concurrent.TimeUnit;

public class LiveProcessTest {
	
	static LiveProcess lp;

	/**
	 * use a main as JUnit doesn't do threads all that well
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String home = System.getProperty("java.home");
			String exe = home + "/bin/java";
			lp = new LiveProcess(exe,"-jar","doubler.jar");
			stat( );
			lp.begin("double or nothing");
			String sI = lp.getStdoutString(1500,TimeUnit.MILLISECONDS);
			System.out.println(sI);
			stat( );
			//lp.send("dir\n");
			lp.send("3\n");
			lp.send("4\n");
			lp.send("5\n");
			lp.send("Fred Flinstone\n");
			//System.out.println(lp.getStdoutString(0500,TimeUnit.MILLISECONDS));
			lp.send("8\n");
			lp.send("9\n");
			stat( );
			System.out.println(lp.getStdoutString(2500,TimeUnit.MILLISECONDS));
			String err = lp.getStderrString(0,TimeUnit.MILLISECONDS);
			if (!err.isEmpty()) {
				System.err.println(err);
			}
			//lp.send("quit\n");
			String s = lp.getStdoutString(1500,TimeUnit.MILLISECONDS);
			System.out.println(s);
			long a = System.nanoTime();
			lp.stop(0500,TimeUnit.MILLISECONDS);
			long b = System.nanoTime();
			System.out.println("stopping time " + (b-a) + " nanos ");
			stat( );
		} catch (ExecutableException e) {
			e.printStackTrace();
		}

	}
	
	private static void stat( ) {
		System.out.println(lp.getStatus());
	}

}
