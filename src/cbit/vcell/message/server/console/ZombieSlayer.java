package cbit.vcell.message.server.console;

import java.util.ArrayList;
import java.util.Iterator;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.pbs.PbsProxy.RunningPbsJobRecord;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ResultSetCrawler;
import cbit.vcell.modeldb.ResultSetDBTopLevel;

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
			ResultSetDBTopLevel resultSetDbTopLevel = new ResultSetDBTopLevel(conFactory, log);
			SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(resultSetDbTopLevel, adminDbTopLevel, databaseServerImpl,log);
		    ArrayList<SuspectSimJobID> zombieCandidateIDs = new ArrayList<SuspectSimJobID>();
		    ArrayList<RunningPbsJobRecord> runningPbsJobRecords = null;

			//now query qstat via PbsProxy
			//initialize PbsProxy
		    CommandService commandService = null;
			if (args.length == 0) {
				commandService = new CommandServiceLocal();
			} else {
				String remoteHostName = args[0];
				String username = args[1];
				String password = args[2];
				commandService = new CommandServiceSsh(remoteHostName, username, password);
			}
			PbsProxy pbsProxy = new PbsProxy(commandService);
			runningPbsJobRecords = pbsProxy.getRunningPBSJobs();

		
			RunningPbsJobRecord suspectPbsJobRecord = null;
			SimulationJobStatus.SchedulerStatus schedulerStatus = null;
			int foundRunningJobsCount = 0;
			int surefireZombieCount = 0;
				
			Iterator<RunningPbsJobRecord> jobRecordIter = runningPbsJobRecords.iterator();
			while (jobRecordIter.hasNext()){
				schedulerStatus = null;
				suspectPbsJobRecord = jobRecordIter.next();
				SimulationJobStatus simJobStatus = simulationDatabase.getLatestSimulationJobStatus(suspectPbsJobRecord.getSimID(), suspectPbsJobRecord.getSimJobIndex());
		
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
				} else {
					foundRunningJobsCount++;
				}
				
			}	
			//Print out which ones we have left
			SuspectSimJobID zombieSuspect = null;
			Iterator<SuspectSimJobID> zombieCandidateIter = zombieCandidateIDs.iterator();
			
			System.out.println("Suspected zombies:");
			String killForSureString = "/cm/shared/apps/torque/2.5.5/bin/qdel";
			while (zombieCandidateIter.hasNext()){
				zombieSuspect = zombieCandidateIter.next();
				System.out.println(String.valueOf(zombieSuspect.getPbsJobId())+"  "+zombieSuspect.getKeyValue().toString()+"_"+zombieSuspect.getJobIndex()+"   status in DB = "+zombieSuspect.getSchedulerStatus());
				if ((zombieSuspect.getSchedulerStatus()==null) || (zombieSuspect.getSchedulerStatus().toString().equals(SimulationJobStatus.SchedulerStatus.STOPPED.toString()))) {
					killForSureString = killForSureString+" "+String.valueOf(zombieSuspect.getPbsJobId());
					surefireZombieCount++;
				}
			}
	
			
			System.out.println();
			System.out.println("Total number of jobs reported by \"qstat | grep S_\" : "+runningPbsJobRecords.size());
			System.out.println("Number of jobs found running as expected by the database: "+foundRunningJobsCount);
			System.out.println("Number of suspected zombies = "+zombieCandidateIDs.size()+"\n");
			System.out.println("Number of surefire (null SchedulerStatus) zombies is: "+surefireZombieCount);
			System.out.println("qdel command to slay the surefire (null SchedulerStatus) zombies is:");
			System.out.println(killForSureString);
	
			System.exit(0);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
}
