package cbit.vcell.solvers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ExecutableCommandTest {
	
	private ExecutableCommand.Container ctn;
	@Before
	public void setup( ) {
		ctn = new ExecutableCommand.Container();
		ctn.add(new ExecutableCommand("hickory","dickory","dock"));
		
	}
	@Test
	public void basic( ) {
		ExecutableCommand ec = ctn.getExecCommands().get(0);
		assertTrue(ec.isMessaging());
		assertFalse(ec.isParallel());
		String expected = "hickory dickory dock";
		String j = ec.getJoinedCommands().trim( );
		assertTrue(expected.equals(j));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void ecFunc( ) {
		final String sToken = "jabberwockey";
		ExecutableCommand ec = new ExecutableCommand("eenie","meenie", sToken);
		ec.setExitCodeToken(sToken);
		String j = ec.getJoinedCommands("minie").trim( );
		String expected = "eenie meenie minie";
		assertTrue(expected.equals(j));
		ctn.add(ec);
		
		ExecutableCommand nap = new ExecutableCommand("nap");
		nap.setExitCodeToken("oops");
		ctn.add(nap);
		assertTrue(false);//should not get here
		
		
	}

}
