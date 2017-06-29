package org.vcell.util.executable;

import java.util.concurrent.TimeUnit;

import org.vcell.util.exe.ExecutableException;

public class LiveProcessTest {
	
	static LiveProcess<?,?,?> glp;
	public static void main(String[] args) {
		mainLP(args);
	}

	/**
	 * use a main as JUnit doesn't do threads all that well
	 * @param args
	 */
	public static void mainLP(String[] args) {
		try {
			String home = System.getProperty("java.home");
			String exe = home + "/bin/java";
			PipedProcessInput ppi = new PipedProcessInput();
			PipedProcessOutput out = new PipedProcessOutput();
			PipedProcessOutput errp = new PipedProcessOutput();
			
			 LiveProcess<PipedProcessInput,PipedProcessOutput,PipedProcessOutput> lp 
				 = new LiveProcess<PipedProcessInput, PipedProcessOutput, PipedProcessOutput>(ppi, out, errp, exe,"-jar","doubler.jar");
			glp = lp;
			stat( );
			lp.begin("double or nothing");
			String sI =  lp.getOutputHandler().read(100,TimeUnit.MILLISECONDS);
			System.out.println(sI);
			stat( );
			//lp.send("dir\n");
			lp.getInputHandler().send("3\n");
			lp.getInputHandler().send("4\n");
			lp.getInputHandler().send("5\n");
			lp.getInputHandler().send("Fred Flinstone\n");
			lp.getInputHandler().send("8\n");
			lp.getInputHandler().send("9\n");
			stat( );
			System.out.println(lp.getOutputHandler( ).read(2500,TimeUnit.MILLISECONDS));
			String err = lp.getErrorHandler( ).read(0,TimeUnit.MILLISECONDS);
			if (!err.isEmpty()) {
				System.err.println(err);
			}
			//lp.send("quit\n");
			String s = lp.getOutputHandler( ).read(1500,TimeUnit.MILLISECONDS);
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
		System.out.println(glp.getStatus());
	}
	
}