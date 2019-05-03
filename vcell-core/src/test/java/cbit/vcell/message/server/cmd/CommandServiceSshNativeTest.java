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
public class CommandServiceSshNativeTest {

	@Before
	public void setUp() throws Exception {
		VCMongoMessage.enabled = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_install_secret_keyfile() throws IOException, ExecutableException {
		CommandServiceSshNative cmd = null;
		try {
			cmd = new CommandServiceSshNative("hpc-ext-1.cam.uchc.edu", "vcell", new File("/Users/schaff/.ssh/schaff_rsa"), new File("/Users/schaff"));
			System.out.println("after created cmdService");
			CommandOutput output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -9" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
		}catch (Exception e) {
			e.printStackTrace();
			fail("exception thrown: "+e.getMessage());
		}finally {
			if (cmd != null) {
				cmd.close();
			}
		}
	}
	
	@Test
	public void test() throws IOException, ExecutableException {
		CommandServiceSshNative cmd = null;
		try {
			cmd = new CommandServiceSshNative("hpc-ext-1.cam.uchc.edu", "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
			System.out.println("after created cmdService");
			CommandOutput output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -9" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
		}catch (Exception e) {
			e.printStackTrace();
			fail("exception thrown: "+e.getMessage());
		}finally {
			if (cmd != null) {
				cmd.close();
			}
		}
	}

}
