package cbit.vcell.tools;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.vcell.util.logging.Logging;

import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.simdata.PortableCommand;

/**
 * test {@link PortableCommandWrapper}
 * @author gweatherby
 */
public class PortableCommandWrapperTest {
	
	private static final String FILE = "z:/t.sub";

	@Test
	public void store( ) throws IOException {
		String json = null; 
		{
			SumPortableCommandTestClass c = new SumPortableCommandTestClass(2,3);
			PortableCommandWrapper shell = new PortableCommandWrapper(c);
			json = shell.asJson();
			//System.out.println(json);
			c.execute();
		}
		
		PortableCommand c2 = PortableCommandWrapper.fromJson(json);
		c2.execute();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void badPackage( ) {
		@SuppressWarnings("unused")
		PortableCommandWrapper shell = new PortableCommandWrapper(null);
		System.err.println("should not print!");
	}
	
	@Test(expected = IllegalStateException.class)
	public void unset( ) {
		PortableCommandWrapper empty = new PortableCommandWrapper();
		System.err.println(empty.asJson() + ", should not print!");
	}

	@Before
	public void init( ) {
		Logging.init();
		Logger lg = Logger.getLogger(HtcProxy.class);
		lg.setLevel(Level.ALL);
	}

	@Test
	public void readBack( ) {
		Collection<PortableCommand> pcs = PortableCommandWrapper.getCommands(FILE);
		for (PortableCommand pc: pcs) {
			System.out.println(pc);
		}
	}
}
