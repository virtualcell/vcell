package org.vcell.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * check VCellThreadChecker methods
 * @author gweatherby
 *
 */
public class VCellThreadCheckerTest {
	PrintStream oldOut = null;
	PrintStream oldErr = null;
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	@Before
	public void setup( ) {
		oldOut = System.out;
		oldErr = System.err;
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		System.setErr(ps);
	}
	
	@After
	public void tearDown( ) {
		System.setOut(oldOut);
		System.setErr(oldErr);
		baos.reset();
	}
	
	/**
	 * verify not output was made during execution
	 */
	private void checkQuiet( ) {
		if (baos.size() == 0) {
			return;
		}
		throw new IllegalStateException("output " + baos.toString());
	}
	
	/**
	 * verify not swing
	 */
	@Test
	public void notSwingCheck( ) {
		VCellThreadChecker.checkCpuIntensiveInvocation();
		checkQuiet( );
	}
	
	/**
	 * verify is swing
	 */
	@Test
	public void swingCheck( ) throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				VCellThreadChecker.checkSwingInvocation();
			}
		});
		checkQuiet( );
	}
	
	
	public void subCall( ) {
		try (VCellThreadChecker.SuppressIntensive si = new VCellThreadChecker.SuppressIntensive()) {
			//this is a test
		}
	}
	
	/**
	 *  verify error suppressed on swing
	 */
	@Test
	public void swingCheckSupp( ) throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				try (VCellThreadChecker.SuppressIntensive si = new VCellThreadChecker.SuppressIntensive()) {
					subCall( );
					VCellThreadChecker.checkCpuIntensiveInvocation();
				}
			}
		});
		checkQuiet( );
	}
	
	/**
	 * verify suppression turns off correctly
	 */
	@Test(expected=IllegalStateException.class)
	public void swingCheckNotSupp( ) throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				try (VCellThreadChecker.SuppressIntensive si = new VCellThreadChecker.SuppressIntensive()) {
					
					//make sure turns off correctly
				}
				VCellThreadChecker.checkCpuIntensiveInvocation();
			}
		});
		checkQuiet( );
	}

}
