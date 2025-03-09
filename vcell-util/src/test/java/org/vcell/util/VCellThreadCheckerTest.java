package org.vcell.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * check VCellThreadChecker methods
 * @author gweatherby
 *
 */
@Tag("Fast")
public class VCellThreadCheckerTest {
	PrintStream oldOut = null;
	PrintStream oldErr = null;
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	@BeforeEach
	public void setup( ) {
		VCellThreadChecker.setGUIThreadChecker(SwingUtilities::isEventDispatchThread);
		oldOut = System.out;
		oldErr = System.err;
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		System.setErr(ps);
	}
	
	@AfterEach
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
	@Test
	public void swingCheckNotSupp( ) throws InvocationTargetException, InterruptedException {
		assertThrows(IllegalStateException.class, () -> {
			SwingUtilities.invokeAndWait(() -> {
                try (VCellThreadChecker.SuppressIntensive si = new VCellThreadChecker.SuppressIntensive()) {

                    //make sure turns off correctly
                }
                VCellThreadChecker.checkCpuIntensiveInvocation();
            });
			checkQuiet();
		});
	}

}
