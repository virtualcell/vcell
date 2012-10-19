package cbit.vcell.message.server.htc.test;

import java.util.TreeMap;

import org.vcell.util.ExecutableException;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.pbs.PbsJobID;
import cbit.vcell.message.server.htc.sge.SgeJobID;
import cbit.vcell.message.server.htc.sge.SgeProxy;

public class HtcTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandServiceSsh cmdssh = null;
		try {
			if (args.length != 2){
				System.out.println("Usage: HtcTest username password");
				System.exit(1);
			}
			String username = args[0];
			String password = args[1];
			cmdssh = new CommandServiceSsh("sigcluster2.cam.uchc.edu", username, password);
//			HtcProxy htcProxy = new PbsProxy(cmdssh);
			HtcProxy htcProxy = new SgeProxy(cmdssh);
//			testHtcProxy1cmd(htcProxy);
//			testHtcProxy2cmd(htcProxy);
			testServices(htcProxy, VCellServerID.getServerID("TEST2"));
			testGetServiceJobInfos(htcProxy, VCellServerID.getServerID("TEST2"));
//			htcProxy.getCommandService().close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cmdssh != null){cmdssh.close();}
		}
	}

	public static void testGetServiceJobInfos(HtcProxy htcProxy, VCellServerID serverID) throws Exception{
		cbit.vcell.mongodb.VCMongoMessage.enabled = false;
		java.util.Vector<cbit.vcell.message.server.htc.HtcProxy.ServiceJobInfo> sjinfos = htcProxy.getServiceJobInfos(serverID);
		for(cbit.vcell.message.server.htc.HtcProxy.ServiceJobInfo sjInfo : sjinfos){
			String jobID = null;
			if(sjInfo.getHtcJobID() instanceof cbit.vcell.message.server.htc.pbs.PbsJobID){
				jobID = ((cbit.vcell.message.server.htc.pbs.PbsJobID)sjInfo.getHtcJobID()).getPbsJobID();
			}else if(sjInfo.getHtcJobID() instanceof cbit.vcell.message.server.htc.sge.SgeJobID){
				jobID = ((cbit.vcell.message.server.htc.sge.SgeJobID)sjInfo.getHtcJobID()).getSgeJobID();
			}
			System.out.println(sjInfo.getServiceJobName()+" "+jobID+" "+sjInfo.getOutputPath()+" "+sjInfo.getErrorPath());
		}
	}
	private static void testServices(HtcProxy htcProxy, VCellServerID serverID)	throws ExecutableException, HtcException, HtcJobNotFoundException {
		try {
			System.out.println("getting services");
			TreeMap<HtcJobID, String> jobIDs = htcProxy.getRunningServiceJobIDs(serverID);
			for (HtcJobID jobID : jobIDs.keySet()){
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
			jobID = htcProxy.submitJob("myJob1","/home/VCELL/vcell/myJob1.sub",new String[] { "/home/VCELL/vcell/calculatePi.sh", "1000" }, 1, 100);
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

	private static void testHtcProxy2cmd(HtcProxy htcProxy)	throws ExecutableException, HtcException, HtcJobNotFoundException {
		System.out.println("<<<<<--------------  SUBMITTING SINGLE JOB ------------------>>>>>>");
		HtcJobID jobID = null;
		try {
			jobID = htcProxy.submitJob("myJob2","/home/VCELL/vcell/myJob2.sub",new String[] { "cp", "dkdkdk" }, new String[] { "echo", "worked" }, 1, 100);
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
