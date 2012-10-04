package cbit.vcell.message.server.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.pbs.PbsProxy;
import cbit.vcell.message.server.pbs.PbsProxy.RunningPbsJobRecord;
import cbit.vcell.message.server.pbs.PbsProxyLocal;
import cbit.vcell.message.server.pbs.PbsProxySsh;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatusInfo;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ResultSetCrawler;

public class ZombieSlayer {

	private static class SuspectSimJobID {
		
		KeyValue keyValue;
		int jobIndex;
		int pbsJobId;
		SimulationJobStatus.SchedulerStatus schedulerStatus;   // null means the database knows nothing about it
		public SimulationJobStatus.SchedulerStatus getSchedulerStatus() {
			return schedulerStatus;
		}


		public SuspectSimJobID(KeyValue aKeyValue, int aJobIndex, SimulationJobStatus.SchedulerStatus aSchedulerStatus, int aPbsJobId){
			keyValue = aKeyValue;
			jobIndex = aJobIndex;
			schedulerStatus = aSchedulerStatus;
			pbsJobId = aPbsJobId;
		}
		
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SuspectSimJobID)){return false;} 
			SuspectSimJobID anotherSimJobID = (SuspectSimJobID)obj;
			if ((keyValue.equals(anotherSimJobID.getKeyValue())) && (jobIndex == anotherSimJobID.getJobIndex())) {
			return true;
		}
		return false;
		}

		public String toString(){
			return keyValue.toString()+"_"+String.valueOf(jobIndex);
		}
		
		public KeyValue getKeyValue() {
			return keyValue;
		}
		public int getJobIndex() {
			return jobIndex;
		}
		public int getPbsJobId(){
			return pbsJobId;
		}
	}
		

   private static PbsProxy init(String hostname,String username,String password, boolean isLocaLRun) throws IOException{
	   if (isLocaLRun){
		   return new PbsProxyLocal();
	   } else {
		  return new PbsProxySsh(hostname, username, password);
	   }   
   }
	
	public static void main(String[] args) {

		if ((args.length !=0) && (args.length !=3)) {
			System.out.println("Usage: remote: hostname, user, password");
			System.out.println("Usage: local: no arguments.");
			System.exit(1);
		}
		final SessionLog log = new StdoutSessionLog("Zombie Slayer");

		try {
			PropertyLoader.loadProperties();
			//init database connection
			KeyFactory keyFactory = new OracleKeyFactory();
			DbDriver.setKeyFactory(keyFactory);
			ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory, log);
			ResultSetCrawler resultSetCrawler = new ResultSetCrawler(conFactory, adminDbTopLevel, log);
			SimulationDatabase simulationDatabase = new SimulationDatabase(resultSetCrawler, adminDbTopLevel, databaseServerImpl,log);
		    ArrayList<SuspectSimJobID> zombieCandidateIDs = new ArrayList<SuspectSimJobID>();
		    ArrayList<RunningPbsJobRecord> runningPbsJobRecords = null;

		    PbsProxy pbsProxy = null;
	
		
			//now query qstat via PbsProxy
			//initialize PbsProxy
			if (args.length == 0) {
				pbsProxy = init(null, null, null, true);
			} else {
				pbsProxy = init(args[0], args[1], args[2], false);
			}
			runningPbsJobRecords = pbsProxy.getRunningPBSJobs();

		
			RunningPbsJobRecord suspectPbsJobRecord = null;
			SimulationJobStatus.SchedulerStatus schedulerStatus = null;
			SimulationJobStatus[] simJobs = null;
			SimulationJobStatus simJobStatus = null;
				
			Iterator<RunningPbsJobRecord> jobRecordIter = runningPbsJobRecords.iterator();
			while (jobRecordIter.hasNext()){
				schedulerStatus = null;
				simJobStatus = null;
				int highestTaskId, highestTaskIdIndex=0;
				suspectPbsJobRecord = jobRecordIter.next();
				simJobs = simulationDatabase.getSimulationJobStatusArray(suspectPbsJobRecord.getSimID(), suspectPbsJobRecord.getSimJobIndex());
		
				if (simJobs!=null && (simJobs.length>0)){
					highestTaskId=0;
					highestTaskIdIndex = 0;
					
					for (int i=0; i<simJobs.length; i++) {
						if (simJobs[i].getTaskID()>highestTaskId){
							highestTaskId = simJobs[i].getTaskID();
							highestTaskIdIndex = i;
						}
					}
				
					simJobStatus = simJobs[highestTaskIdIndex];
				
				}
				if (simJobStatus!=null) {
					schedulerStatus = simJobStatus.getSchedulerStatus();
				}
				
				suspectPbsJobRecord.setSchedulerStatus(schedulerStatus);
			}
	
			jobRecordIter = runningPbsJobRecords.iterator();
			while (jobRecordIter.hasNext()){
				suspectPbsJobRecord = jobRecordIter.next();
				
				if (suspectPbsJobRecord.getLastKnownSchedulerStatus()!=SimulationJobStatus.SchedulerStatus.RUNNING) {
					zombieCandidateIDs.add(new SuspectSimJobID(suspectPbsJobRecord.getSimID(), suspectPbsJobRecord.getSimJobIndex(), suspectPbsJobRecord.getLastKnownSchedulerStatus(), suspectPbsJobRecord.getPbsJobId()));
				}
				
			}	
			//Print out which ones we have left
			SuspectSimJobID zombieSuspect = null;
			Iterator<SuspectSimJobID> zombieCandidateIter = zombieCandidateIDs.iterator();
			
			System.out.println("Suspected zombies:");
			String killForSureString = "/cm/shared/apps/torque/2.5.5/bin/qdel";
			while (zombieCandidateIter.hasNext()){
				zombieSuspect = zombieCandidateIter.next();
				System.out.println(zombieSuspect.getKeyValue().toString()+"_"+zombieSuspect.getJobIndex()+"   status in DB = "+zombieSuspect.getSchedulerStatus());
				if (zombieSuspect.getSchedulerStatus()==null) {
					killForSureString = killForSureString+" "+String.valueOf(zombieSuspect.getPbsJobId());
				}
			}
	
			
			System.out.println();
			System.out.println("Number of suspected zombies = "+zombieCandidateIDs.size()+"\n");
			System.out.println("qdel command to slay the surefire zombies is:");
			System.out.println(killForSureString);
	
			System.exit(0);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
}
