package cbit.vcell.message.server.htc.slurm;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandServiceSshNative;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy.JobInfoAndStatus;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;

@Ignore
public class SlurmProxyTest {

	@Test
	public void testSLURM() throws IOException, ExecutableException {
		System.setProperty(PropertyLoader.vcellServerIDProperty, "Test2");
		System.setProperty(PropertyLoader.htcLogDirExternal, "/Volumes/vcell/htclogs");
		VCMongoMessage.enabled=false;
		
		CommandServiceSshNative cmd = null;
		try {
			cmd = new CommandServiceSshNative("vcell-service.cam.uchc.edu", "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
			SlurmProxy slurmProxy = new SlurmProxy(cmd, "vcell");
			Map<HtcJobID, JobInfoAndStatus> runningJobs = slurmProxy.getRunningJobs();
			for (HtcJobID job : runningJobs.keySet()) {
				HtcJobStatus jobStatus = runningJobs.get(job).status;
				System.out.println("job "+job.toString()+", status="+jobStatus.toString());
			}
		}catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}finally {
			if (cmd != null) {
				cmd.close();
			}
		}
	}

	

}
