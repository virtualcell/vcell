package cbit.vcell.message.server.htc.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;

import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.pbs.PbsJobID;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeJobID;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;

public class HtcTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandServiceSsh cmdssh = null;
		try {
			if (args.length != 4){
				System.out.println("Usage: HtcTest remotehost username password (PBS|SGE|SLURM)");
				System.exit(1);
			}
			String host = args[0];
			String username = args[1];
			String password = args[2];
			String htcType = args[3];
			cmdssh = new CommandServiceSsh(host, username, password);
			HtcProxy htcProxy = null;
			if (htcType.equalsIgnoreCase("PBS")){
				htcProxy = new PbsProxy(cmdssh,username);
			}else if (htcType.equalsIgnoreCase("SGE")){
				htcProxy = new SgeProxy(cmdssh,username);
			}else if (htcType.equalsIgnoreCase("SLURM")){
				htcProxy = new SlurmProxy(cmdssh,username);
			}else{
				throw new RuntimeException("unrecognized htc type = "+htcType);
			}
			System.setProperty(PropertyLoader.vcellServerIDProperty,"BETA");
			testHtcProxy1cmd(htcProxy);
//			testHtcProxy2cmd(htcProxy);
//			testServices(htcProxy, VCellServerID.getServerID("TEST2"));
//			testGetServiceJobInfos(htcProxy, VCellServerID.getServerID("TEST2"));
//			htcProxy.getCommandService().close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cmdssh != null){cmdssh.close();}
		}
	}

	private static void testHtcProxy1cmd(HtcProxy htcProxy)	throws ExecutableException, HtcException, HtcJobNotFoundException,IOException {
		System.out.println("<<<<<--------------  SUBMITTING SINGLE JOB ------------------>>>>>>");
		HtcJobID jobID = null;
		try {
			jobID = htcProxy.submitJob("myJob1","/home/VCELL/vcell/myJob1.sub",new String[] { "/home/VCELL/vcell/calculatePi.sh", "100000000" }, 1, 100, new String[] { "echo", "postCommand exit code is ", "EXIT_CODE"}, "EXIT_CODE");
		} catch (Exception e1) {
			e1.printStackTrace(System.out);
		}
		
//		System.out.println("<<<<<--------------  KILLING JOB --------------------->>>>>>");
//		try {
//			htcProxy.killJob(jobID);
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		}

		
		System.out.println("<<<<<--------------  printing running simulations --------------->>>>>>");
		List<HtcJobID> htcJobIDs = htcProxy.getRunningJobIDs("myJob1");
		if (htcProxy instanceof SgeProxy){
			htcJobIDs.add(new SgeJobID("12345"));
		}else{
			htcJobIDs.add(new PbsJobID("12345"));
		}
		Map<HtcJobID,HtcJobInfo> htcJobInfoMap = htcProxy.getJobInfos(htcJobIDs);
		for (HtcJobID htcJobId : htcJobInfoMap.keySet()){
			System.out.println("jobInfo = "+htcJobInfoMap.get(htcJobId));
		}
		
		System.out.println("<<<<<--------------  GETTING JOB EXIT STATUS--------------->>>>>>");
		HtcJobStatus jobStatus2 = null;
		while (jobStatus2 == null || jobStatus2.isRunning()){
			try {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				jobStatus2 = htcProxy.getJobStatus(jobID);
				Map<HtcJobID,HtcJobInfo> htcJobInfo = htcProxy.getJobInfos(Arrays.asList(jobID));
				System.out.println("jobInfo = "+htcJobInfo);
				System.out.println("jobStatus = "+jobStatus2);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
	}

	private static void testHtcProxy2cmd(HtcProxy htcProxy)	throws ExecutableException, HtcException, HtcJobNotFoundException {
		System.out.println("<<<<<--------------  SUBMITTING SINGLE JOB ------------------>>>>>>");
		HtcJobID jobID = null;
		try {
			jobID = htcProxy.submitJob("myJob2","/home/VCELL/vcell/myJob2.sub",new String[] { "cp", "dkdkdk" }, new String[] { "echo", "worked" }, 1, 100, new String[] { "echo", "postCommand exit code is ", "EXIT_CODE"}, "EXIT_CODE",null);
		} catch (Exception e1) {
			e1.printStackTrace(System.out);
		}
		
//		System.out.println("<<<<<--------------  KILLING JOB --------------------->>>>>>");
//		try {
//			htcProxy.killJob(jobID);
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		}

		System.out.println("<<<<<--------------  GETTING JOB EXIT STATUS--------------->>>>>>");
		HtcJobStatus jobStatus2 = null;
		while (jobStatus2 != null && jobStatus2.isRunning()){
			try {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				jobStatus2 = htcProxy.getJobStatus(jobID);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		System.out.println("jobStatus = "+jobStatus2);
	}

}
