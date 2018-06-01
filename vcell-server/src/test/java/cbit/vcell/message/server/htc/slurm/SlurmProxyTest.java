package cbit.vcell.message.server.htc.slurm;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandServiceSshNative;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;

@Ignore
public class SlurmProxyTest {

    @BeforeClass
    public static void setLogger() throws MalformedURLException
    {
//        System.setProperty("log4j.configurationFile","/Users/schaff/Documents/workspace-modular/vcell/docker/trace.log4j2.xml");
    }
    
	
	@Test
	public void testSingularitySupport() throws IOException, ExecutableException {
		CommandServiceSshNative cmd = null;
		try {
			Random r = new Random();
			System.setProperty("log4j2.trace","true");
			System.setProperty(PropertyLoader.vcellServerIDProperty, "Test2");
			System.setProperty(PropertyLoader.htcLogDirExternal, "/Volumes/vcell/htclogs");
			VCMongoMessage.enabled=false;
			String partitions[] = new String[] { "vcell", "vcell2" };
			System.setProperty(PropertyLoader.slurm_partition, partitions[1]);
			
			
			cmd = new CommandServiceSshNative("vcell-service.cam.uchc.edu", "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
			SlurmProxy slurmProxy = new SlurmProxy(cmd, "vcell");

			String jobName = "V_TEST2_999999999_0_"+r.nextInt(10000);
			System.out.println("job name is "+jobName);
			File sub_file_localpath = new File("/Volumes/vcell/htclogs/"+jobName+".slurm.sub");
			File sub_file_remotepath = new File("/share/apps/vcell3/htclogs/"+jobName+".slurm.sub");
			
			StringBuffer subfileContent = new StringBuffer();
			subfileContent.append("#!/usr/bin/bash\n");
			String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
			subfileContent.append("#SBATCH --partition="+partition+"\n");
			subfileContent.append("#SBATCH -J "+jobName+"\n");
			subfileContent.append("#SBATCH -o /share/apps/vcell3/htclogs/"+jobName+".slurm.log\n");
			subfileContent.append("#SBATCH -e /share/apps/vcell3/htclogs/"+jobName+".slurm.log\n");
			subfileContent.append("#SBATCH --mem=1000M\n");
			subfileContent.append("#SBATCH --no-kill\n");
			subfileContent.append("#SBATCH --no-requeue\n");
			subfileContent.append("env\n");
			subfileContent.append("echo `hostname`\n");
			subfileContent.append("python -c \"some_str = ' ' * 51200000\"\n");
			subfileContent.append("retcode=$?\n");
			subfileContent.append("echo \"return code was $retcode\"\n");
			subfileContent.append("if [[ $retcode == 137 ]]; then\n");
			subfileContent.append("   echo \"job was killed via kill -9 (probably out of memory)\"\n");
			subfileContent.append("fi\n");
			subfileContent.append("exit $retcode\n");
			//subfileContent.append("export MODULEPATH=/isg/shared/modulefiles:/tgcapps/modulefiles\n");
			//subfileContent.append("source /usr/share/Modules/init/bash\n");
//			subfileContent.append("module load singularity/2.4.2\n");
//			subfileContent.append("if command -v singularity >/dev/null 2>&1; then\n");
//			subfileContent.append("   echo 'singularity command exists'\n");
//			subfileContent.append("   exit 0\n");
//			subfileContent.append("else\n");
//			subfileContent.append("   echo 'singularity command not found'\n");
//			subfileContent.append("   exit 1\n");
//			subfileContent.append("fi\n");

			FileUtils.writeStringToFile(sub_file_localpath, subfileContent.toString());
			HtcJobID htcJobId = slurmProxy.submitJobFile(sub_file_remotepath);
			System.out.println("running job "+htcJobId);
			HtcJobInfo htcJobInfo = new HtcJobInfo(htcJobId, jobName);

			ArrayList<HtcJobInfo> jobInfos = new ArrayList<HtcJobInfo>();
			jobInfos.add(htcJobInfo);
			
			Map<HtcJobInfo, HtcJobStatus> jobStatusMap = slurmProxy.getJobStatus(jobInfos);
			int attempts = 0;
			while (attempts<80 && (jobStatusMap.get(htcJobInfo)==null || !jobStatusMap.get(htcJobInfo).isDone())){
				try { Thread.sleep(1000); } catch (InterruptedException e){}
				jobStatusMap = slurmProxy.getJobStatus(jobInfos);
				System.out.println(jobStatusMap.get(htcJobInfo));
				attempts++;
			}
			System.out.println(jobStatusMap.get(htcJobInfo));
			
		}catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}finally {
			if (cmd != null) {
				cmd.close();
			}
		}
	}

	
	@Test
	public void testSLURM() throws IOException, ExecutableException {
		System.setProperty("log4j2.trace","true");
		System.setProperty(PropertyLoader.vcellServerIDProperty, "Test2");
		System.setProperty(PropertyLoader.htcLogDirExternal, "/Volumes/vcell/htclogs");
		VCMongoMessage.enabled=false;
		String partitions[] = new String[] { "vcell", "vcell2" };
		System.setProperty(PropertyLoader.slurm_partition, partitions[0]);
		
		CommandServiceSshNative cmd = null;
		try {
			cmd = new CommandServiceSshNative("vcell-service.cam.uchc.edu", "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
			SlurmProxy slurmProxy = new SlurmProxy(cmd, "vcell");
			Map<HtcJobInfo, HtcJobStatus> runningJobs = slurmProxy.getRunningJobs();
			for (HtcJobInfo jobInfo : runningJobs.keySet()) {
				HtcJobStatus jobStatus = runningJobs.get(jobInfo);
				System.out.println("job "+jobInfo.getHtcJobID()+" "+jobInfo.getJobName()+", status="+jobStatus.toString());
			}
			for (String partition : partitions) {
				System.setProperty(PropertyLoader.slurm_partition, partition);
				PartitionStatistics partitionStatistics = slurmProxy.getPartitionStatistics();
				System.out.println("partition statistics for partition "+partition+": "+partitionStatistics);
				System.out.println("number of cpus allocated = "+partitionStatistics.numCpusAllocated);
				System.out.println("load = "+partitionStatistics.load);
				System.out.println("number of cpus total = "+partitionStatistics.numCpusTotal);
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
