package cbit.vcell.message.server.htc.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.VCellServerID;

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

public class HtcTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandServiceSsh cmdssh = null;
		try {
			if (args.length != 4){
				System.out.println("Usage: HtcTest remotehost username password (PBS|SGE)");
				System.exit(1);
			}
			String host = args[0];
			String username = args[1];
			String password = args[2];
			String htcType = args[3];
			cmdssh = new CommandServiceSsh(host, username, password);
			HtcProxy htcProxy = null;
			if (htcType.equalsIgnoreCase("PBS")){
				htcProxy = new PbsProxy(cmdssh);
			}else if (htcType.equalsIgnoreCase("SGE")){
				htcProxy = new SgeProxy(cmdssh);
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

	public static void testGetServiceJobInfos(HtcProxy htcProxy, VCellServerID serverID) throws Exception{
		cbit.vcell.mongodb.VCMongoMessage.enabled = false;
		List<HtcJobID> htcJobIDs = htcProxy.getRunningServiceJobIDs(serverID); 
		List<HtcJobInfo> sjinfos = htcProxy.getJobInfos(htcJobIDs);
		for(HtcJobInfo sjInfo : sjinfos){
			String jobID = null;
			if(sjInfo.getHtcJobID() instanceof cbit.vcell.message.server.htc.pbs.PbsJobID){
				jobID = ((cbit.vcell.message.server.htc.pbs.PbsJobID)sjInfo.getHtcJobID()).getPbsJobID();
			}else if(sjInfo.getHtcJobID() instanceof cbit.vcell.message.server.htc.sge.SgeJobID){
				jobID = ((cbit.vcell.message.server.htc.sge.SgeJobID)sjInfo.getHtcJobID()).getSgeJobID();
			}
			System.out.println(sjInfo.getJobName()+" "+jobID+" "+sjInfo.getOutputPath()+" "+sjInfo.getErrorPath());
		}
	}
	private static void testServices(HtcProxy htcProxy, VCellServerID serverID)	throws ExecutableException, HtcException, HtcJobNotFoundException {
		try {
			System.out.println("getting services");
			List<HtcJobID> htcJobIDs = htcProxy.getRunningServiceJobIDs(serverID); 
			for (HtcJobID jobID : htcJobIDs){
				if (jobID instanceof PbsJobID){
					System.out.println("serviceJobID : "+((PbsJobID)jobID).getPbsJobID());
					htcProxy.killJob(jobID);
				}else if (jobID instanceof SgeJobID){
					System.out.println("serviceJobID : "+((SgeJobID)jobID).getSgeJobID());
					htcProxy.killJob(jobID);
				}
			}
			System.out.println("done getting services");
		} catch (Exception e1) {
			e1.printStackTrace(System.out);
		}
	}

	private static void testHtcProxy1cmd(HtcProxy htcProxy)	throws ExecutableException, HtcException, HtcJobNotFoundException {
		System.out.println("<<<<<--------------  SUBMITTING SINGLE JOB ------------------>>>>>>");
		HtcJobID jobID = null;
		try {
			jobID = htcProxy.submitJob("myJob1","/home/VCELL/vcell/myJob1.sub",new String[] { "/home/VCELL/vcell/calculatePi.sh", "100000" }, 1, 100, new String[] { "echo", "postCommand exit code is ", "EXIT_CODE"}, "EXIT_CODE");
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
		List<HtcJobID> htcJobIDs = htcProxy.getRunningJobIDs("ALPHA");
		List<HtcJobInfo> htcJobInfos = htcProxy.getJobInfos(htcJobIDs);
		for (HtcJobInfo htcJobInfo : htcJobInfos){
			System.out.println("jobInfo = "+htcJobInfo);
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
				List<HtcJobInfo> htcJobInfo = htcProxy.getJobInfos(Arrays.asList(jobID));
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
			jobID = htcProxy.submitJob("myJob2","/home/VCELL/vcell/myJob2.sub",new String[] { "cp", "dkdkdk" }, new String[] { "echo", "worked" }, 1, 100, new String[] { "echo", "postCommand exit code is ", "EXIT_CODE"}, "EXIT_CODE");
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
