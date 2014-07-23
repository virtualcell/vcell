package cbit.vcell.message.server.cmd;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.vcell.util.ExecutableException;
import org.vcell.util.logging.Logging;

import cbit.vcell.message.server.cmd.CommandService.CommandOutput;

/**
 * verrify {@link CommandServiceSsh} works for simple commands and times out for ones that hang
 */
public class CommandServiceSshTest {
	private String password = "xxxxx"; //subst before running test
	private CommandServiceSsh sshServ;
	
	@Before
	public void setup( ) throws IOException {
		Logging.init();
		sshServ = new CommandServiceSsh("signode01", "vcell", password);
	}
	
	@Test
	public void testSimple( ) throws IOException, ExecutableException {
		String simple[] = {"ls"};
		CommandOutput c = sshServ.command(simple);
		showOutput(c);
	}
	
	@Test(expected = ExecutableException.class)
	public void testTimeout( ) throws IOException, ExecutableException {
		String hang[] = {"sleep 60000"};
		CommandOutput c = sshServ.command(hang);
		showOutput(c);
	}
	
	private void showOutput(CommandOutput c) {
		System.out.println(c.getStandardOutput());
		System.err.println(c.getStandardError());
		System.out.println("Exit code " + c.getExitStatus());
		
	}

}
