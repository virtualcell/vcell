package org.vcell.util;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.logging.Logging;

@SuppressWarnings("unused")
public class TerminatorTest {

	public static void main(String[] args) {
		Logging.init();
		Logger.getLogger(ApplicationTerminator.class).setLevel(Level.INFO);
		try {
			int testN = 1; 
			boolean shouldNotReturn = false;
			switch (testN) {
			case 0:
				shouldNotReturn = testExit( );
				break;
			case 1:
				shouldNotReturn = testAbortExit();
				break;
			case 2:
				shouldNotReturn = testMainFaster();
				break;
			case 3:
				shouldNotReturn = testExceptionThrown();
				break;
			}
			if (shouldNotReturn) {
				System.err.println("test failed");
			}
		}
		catch (InterruptedException ie) {
			System.err.println("interupted");
		}
		System.out.println("exiting main");
	}
	
	private static boolean testExit( ) throws InterruptedException {
		ApplicationTerminator arnold = ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 5, 3);
		Thread.sleep(10 * 1000);
		return true;
	}
	
	private static boolean testMainFaster( ) throws InterruptedException {
		ApplicationTerminator arnold = ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 15, 4);
		Thread.sleep(5 * 1000);
		return false;
	}
	
	private static boolean testAbortExit( ) throws InterruptedException {
		ApplicationTerminator arnold = ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 4, 0);
		Thread.sleep(3 * 1000);
		arnold.abortExit();
		return false;
	}
	
	private static boolean testExceptionThrown( ) throws InterruptedException {
		boolean caught = false;
		ApplicationTerminator arnold = ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 4, 0);
		try {
			ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 4, 0);
		}
		catch (IllegalStateException ise) {
			caught = true;
			//ise.printStackTrace();
		}
		if (!caught) {
			throw new RuntimeException("Exception not thrown?");
		}
		Thread.sleep(2 * 1000);
		return false;
	}

}
