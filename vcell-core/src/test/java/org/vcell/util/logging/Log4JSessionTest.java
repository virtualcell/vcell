package org.vcell.util.logging;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.SessionLog;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;



/**
 * compare {@link Log4JSessionTest} and {@link StdoutSessionLog}
 * @author gweatherby
 *
 */
public class Log4JSessionTest {
	
	/**
	 * quick and dirty way to get stack trace with depth
	 * @param in
	 */
	public void dontBeNegative(int in) {
		if (in > 0) {
			dontBeNegative(--in);
		}
		else {
			throw new IllegalArgumentException("Don't be negative");
		}
	}
	
	public void walkThrough(SessionLog sl) {
		sl.print("Have a good day");
		sl.alert("A storm is coming!");
		try {
			dontBeNegative(4);
		} catch (Exception e) {
			sl.exception(e);
		}
	}
	@Before
	public void init( ) {
	}
	
	//@Test
	public void compare( ) {
		System.setProperty(PropertyLoader.mongodbHostInternal,"mongo.cam.uchc.edu");
		System.setProperty(PropertyLoader.mongodbPortInternal,"27017");
		
		StdoutSessionLog ssl = new StdoutSessionLog("JUnitTest");
		walkThrough(ssl);
		
		Log4jSessionLog l4jl = new Log4jSessionLog("JUnitTest");
		walkThrough(l4jl);
	}
	
	@Test
	public void makeSure( ) {
//		assertTrue(Level.WARN.isGreaterOrEqual(Level.INFO));
	}
	
	
	@Test
	public void config( ) {
//		Log4jSessionLog l4jl = new Log4jSessionLog("ConfigTest");
//		System.err.println("config set level " + l4jl.getLogger().getEffectiveLevel());
	}
	
}
