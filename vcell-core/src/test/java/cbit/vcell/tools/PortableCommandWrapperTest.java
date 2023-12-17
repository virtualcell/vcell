package cbit.vcell.tools;

import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.PortableCommandWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

/**
 * test {@link PortableCommandWrapper}
 * @author gweatherby
 */
@Tag("Fast")
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

	@Test
	public void badPackage( ) {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
		{
			@SuppressWarnings("unused")
			PortableCommandWrapper shell = new PortableCommandWrapper(null);
			System.err.println("should not print!");
		});
	}
	
	@Test
	public void unset( ) {
		Assertions.assertThrows(IllegalStateException.class, () ->
		{
			PortableCommandWrapper empty = new PortableCommandWrapper();
			System.err.println(empty.asJson() + ", should not print!");
		});
	}

	@Test
	public void readBack( ) {
		Collection<PortableCommand> pcs = PortableCommandWrapper.getCommands(FILE);
		for (PortableCommand pc: pcs) {
			System.out.println(pc);
		}
	}
}
