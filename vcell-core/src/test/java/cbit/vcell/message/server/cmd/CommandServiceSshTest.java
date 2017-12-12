package cbit.vcell.message.server.cmd;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.mongodb.VCMongoMessage;

@Ignore
public class CommandServiceSshTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException, ExecutableException {
		try {
			VCMongoMessage.enabled=true;
			CommandServiceSsh cmd = new CommandServiceSsh("vcell-service.cam.uchc.edu", "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
			System.out.println("after created cmdService");
			CommandOutput output = cmd.command(new String[] { "ls" });
			System.out.println("ls output is: "+output.getStandardOutput());
		}catch (Exception e) {
			fail("exception thrown: "+e.getMessage());
		}
	}

}
