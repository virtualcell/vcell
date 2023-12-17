package cbit.vcell.solvers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class ExecutableCommandTest {
	
	private ExecutableCommand.Container ctn;
	@BeforeEach
	public void setup( ) {
		ctn = new ExecutableCommand.Container();
		ctn.add(new ExecutableCommand(null,"hickory","dickory","dock"));
		
	}
	@Test
	public void basic( ) {
		ExecutableCommand ec = ctn.getExecCommands().get(0);
		assertTrue(ec.isMessaging());
		assertFalse(ec.isParallel());
		String expected = "hickory dickory dock";
		String j = ec.getJoinedCommands().trim();
        assertEquals(expected, j);
	}
	
	@Test
	public void ecFunc( ) {
		assertThrows(UnsupportedOperationException.class, () ->
		{
			final String sToken = "jabberwockey";
			ExecutableCommand ec = new ExecutableCommand(null, "eenie", "meenie", sToken);
			ec.setExitCodeToken(sToken);
			String j = ec.getJoinedCommands("minie").trim();
			String expected = "eenie meenie minie";
            assertEquals(expected, j);
			ctn.add(ec);

			ExecutableCommand nap = new ExecutableCommand(null, "nap");
			nap.setExitCodeToken("oops");
			ctn.add(nap);
			fail();//should not get here
		});
	}

}
