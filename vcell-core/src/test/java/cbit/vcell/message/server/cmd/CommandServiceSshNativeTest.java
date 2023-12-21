package cbit.vcell.message.server.cmd;

import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.mongodb.VCMongoMessage;
import org.junit.jupiter.api.*;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@Tag("Fast")
public class CommandServiceSshNativeTest {

	@BeforeEach
	public void setUp() throws Exception {
		VCMongoMessage.enabled = false;
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void test_install_secret_keyfile() throws IOException, ExecutableException {
		CommandServiceSshNative cmd = null;
		try {
			cmd = new CommandServiceSshNative(new String[] {"vcell-service.cam.uchc.edu"}, "vcell", new File("/Users/schaff/.ssh/schaff_rsa"), new File("/Users/schaff"));
			System.out.println("after created cmdService");
			CommandOutput output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -9" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
		}catch (Exception e) {
			e.printStackTrace();
            fail("exception thrown: " + e.getMessage());
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
			cmd = new CommandServiceSshNative(new String[] {"vcell-service.cam.uchc.edu"}, "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
			System.out.println("after created cmdService");
			CommandOutput output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -9" });
			System.out.println("ls output is: "+output.getStandardOutput());
			output = cmd.command(new String[] { "ls -al | head -4" });
			System.out.println("ls output is: "+output.getStandardOutput());
		}catch (Exception e) {
			e.printStackTrace();
            fail("exception thrown: " + e.getMessage());
		}finally {
			if (cmd != null) {
				cmd.close();
			}
		}
	}

}
