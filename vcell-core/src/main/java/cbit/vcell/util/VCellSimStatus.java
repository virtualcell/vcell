package cbit.vcell.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.vcell.util.BeanUtils;

import cbit.vcell.server.SimulationJobStatus;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.FileKeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;

public class VCellSimStatus {

	public static class CommandExecError extends Exception{
    	public CommandExecError(String message){
    		super(message);
    	}
    }
	
    public static class CommandOutput {
    	private String commandStr;
    	private String standardOutput;
    	private String standardError;
    	private Integer exitStatus;

    	public CommandOutput(String commandStr, Command command) throws IOException {
    	this.commandStr = commandStr;
    	this.standardOutput = IOUtils.readFully(command.getInputStream()).toString();
    	this.standardError = IOUtils.readFully(command.getErrorStream()).toString();
    	this.exitStatus = command.getExitStatus();
    	}

    	public String getCommandStr() {
    	return commandStr;
    	}
    	public String getStandardOutput() {
    	return standardOutput;
    	}
    	public String getStandardError() {
    	return standardError;
    	}
    	public Integer getExitStatus() {
    	return exitStatus;
    	}
    }

	private static class VCellSlurmAssoc {
		public String slurmJobName;
		public Long vc_simulationjobID;
		public Long slurmJobID;
		public Long slurmJobArrIndex;
		public Long vcellSchedulerStatus;
		public String slurmState;
		public String vcellUserName;
		public String vcellSubmitDate;
		public Timestamp vcellQueueDate;
		public String vcellStartDate;
		public String vcellLastUpdateDate;
		public String vcellStatusMsg;
		public String slurmSubmitDate;
		public String slurmStartDate;
		public String slurmNodeList;
		public VCellSlurmAssoc() {
			
		}
		public VCellSlurmAssoc(String slurmJobName,Long vc_simulationjobID,Long slurmJobID,Long slurmJobArrIndex,Long vcellSchedulerStatus, String slurmState, String vcellUserName,String vcellSubmitDate, Timestamp vcellQueueDate,
				String vcellStartDate, String vcellLastUpdateDate, String vcellStatusMsg, String slurmSubmitDate, String slurmStartDate,String slurmNodeList) {
			super();
			this.slurmJobName = slurmJobName;
			this.vc_simulationjobID = vc_simulationjobID;
			this.slurmJobID = slurmJobID;
			this.slurmJobArrIndex = slurmJobArrIndex;
			this.vcellSchedulerStatus = vcellSchedulerStatus;
			this.slurmState = slurmState;
			this.vcellUserName = vcellUserName;
			this.vcellSubmitDate = vcellSubmitDate;
			this.vcellQueueDate = vcellQueueDate;
			this.vcellStartDate = vcellStartDate;
			this.vcellLastUpdateDate = vcellLastUpdateDate;
			this.vcellStatusMsg = vcellStatusMsg;
			this.slurmSubmitDate = slurmSubmitDate;
			this.slurmStartDate = slurmStartDate;
			this.slurmNodeList = slurmNodeList;
		}
		public VCellSlurmAssoc(VCellSlurmAssoc other) {
			this(other.slurmJobName,other.vc_simulationjobID,other.slurmJobID,other.slurmJobArrIndex,other.vcellSchedulerStatus, other.slurmState, other.vcellUserName,other.vcellSubmitDate, other.vcellQueueDate,
					other.vcellStartDate, other.vcellLastUpdateDate, other.vcellStatusMsg, other.slurmSubmitDate, other.slurmStartDate,other.slurmNodeList);
		}
	}
	public static void main(String[] args) {
		if(args.length != 6){
			System.out.println("Usage: "+VCellSimStatus.class.getSimpleName()+" dbHost dbName dbUser dbPassword sshRSAKeyFile slurmHost");
			System.exit(1);
		}
		String dbHost = args[0];
		String dbName = args[1];
		String dbUser = args[2];
		String dbPassword = args[3];
		String sshKeyFile = args[4];
		String slurmSubmitHost = args[5];
		
		SSHClient sshClient = null;
		Connection con = null;//oracleConnection.getConnection(new Object());
		Statement stmt = null;//con.createStatement();
		try {
			TreeMap<String, VCellSlurmAssoc> dbSlurmMap0= new TreeMap<>();
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = java.sql.DriverManager.getConnection("jdbc:oracle:thin:@"+dbHost+":1521:"+dbName, dbUser,dbPassword);
			con.setAutoCommit(false);
			con.setReadOnly(true);
			//TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS')
			stmt = con.createStatement();
			String sql = 
					"select 'V_' || vc_simulationjob.serverid || '_' || simref || '_' || vc_simulationjob.jobindex || '_' || vc_simulationjob.taskid slurnjobname,vc_userinfo.userid,"+
						"vc_simulationjob.*"+
					" from vc_simulationjob,vc_simulation,vc_userinfo"+
					" where schedulerstatus not in (4,5,6) and vc_simulation.id=vc_simulationjob.simref"+
					" and vc_userinfo.id=vc_simulation.ownerref"+
					" order by simref,jobindex";

			//"select unique vc_simulationjob.submitdate, 'V_' || vc_simulationjob.serverid || '_' || simref || '_' || vc_simulationjob.jobindex || '_' || vc_simulationjob.taskid,vc_simulationjob.enddate,vc_simulationjob.id,vc_simulationjob.hasdata,vc_userinfo.userid from vc_simulationjob,vc_userinfo,vc_simulation where vc_simulation.id=vc_simulationjob.simref and vc_userinfo.id=vc_simulation.ownerref and (lower(statusmsg) like 'job_dispatch%' or lower(statusmsg) like 'job_waiting%') and submitdate < to_date('25-Nov-18','DD-Mon-YY') order by vc_simulationjob.submitdate";
			ResultSet rset = stmt.executeQuery(sql);
			StringBuffer slurmJobNames = new StringBuffer();
			System.out.println("//--------------------\n// DB Sim Job Status query\n//--------------------");
			while (rset.next()) {
				String slurmJobName = rset.getString("slurnjobname");
				System.out.println(rset.getString("userid")+
					" simjobid "+rset.getLong("id")+
					" simref "+rset.getLong("simref")+
					" jobid "+rset.getLong("jobindex")+
					" taskid "+rset.getLong("taskid")+
					" schedstatus "+rset.getLong("schedulerstatus")+
					" statusmsg "+rset.getString("statusmsg")+
					" startdate "+rset.getString("startdate")+
					" computehost "+rset.getString("computehost")+
					" slurmjobname "+slurmJobName
					);
				slurmJobNames.append((slurmJobNames.length()>0?",":"")+slurmJobName);
				
    			VCellSlurmAssoc entry = dbSlurmMap0.get(slurmJobName);
    			if(entry == null) {
    				entry = new VCellSlurmAssoc(slurmJobName,rset.getLong("id"),null,null,rset.getLong("schedulerstatus"), null, rset.getString("userid"), rset.getString("submitdate"), rset.getTimestamp("queuedate"), rset.getString("startdate"), rset.getString("latestupdatedate"),rset.getString("statusmsg"), null, null,null);
    				dbSlurmMap0.put(slurmJobName, entry);
        			//entry[0] = rset.getLong("schedulerstatus")+"_"+rset.getString("userid");
    			}else {
    				throw new Exception("not expecting dup db");
    			}
			}
			rset.close();
			
			System.out.println(slurmJobNames.toString());
			
			sshClient = new SSHClient();
			sshClient.addHostKeyVerifier(new PromiscuousVerifier());
			sshClient.connect(slurmSubmitHost);
			File keyFile = new File(sshKeyFile);
			System.out.println("keyfile="+keyFile.getAbsolutePath());
	    	FileKeyProvider keyProvider = new OpenSSHKeyFile();
	    	keyProvider.init(keyFile);
			sshClient.authPublickey("vcell",keyProvider);
			
	    	Session session = null;
	    	Command command = null;
	    	TreeMap<String, VCellSlurmAssoc> jobArrMap= new TreeMap<>();
	    	try {
//	    		System.out.println(commandStr);
	    		Integer specialError = null;
	    		//String commandStr = "/share/apps/vcell2/deployed/test/configs/mytest";
//	    		String commandStr = "source /etc/bashrc ; /share/apps/vcell2/deployed/test/configs/vcellservice < /dev/null >& /dev/null";
//	    		String commandStr = "source /etc/profile ; /share/apps/vcell2/deployed/test/configs/vcellservice";
//	    		String commandStr = "ps x | grep /share/apps/vcell2/deployed/test/ | grep com.sun.management.jmxremote.port=12307 | grep -v grep";
//	    		String commandStr = "ps x";
//	    		String commandStr = "whoami";
//	    		String commandStr = "sacct --format=\"JobID,JobName%30,State,Submit,Start,User,ExitCode\" | grep -i 139363583";
//	    		String commandStr = "sacct --jobs 281425,304556 --format=\"JobID,JobName%30,State,Submit,Start,User,ExitCode\"";
//	    		String commandStr = "sacct --user=vcell --name "+sb.toString()+" --format=\"JobID,JobName%30,State,Submit,Start,User,ExitCode\"";
//	    		String commandStr = "sacct --user=vcell --format=\"JobID%20,JobName%30,State%20,Submit,Start,User,ExitCode\" -S 2020-06-01 -E 2020-06-06 -s R,PD,CA";//-s R,PD
	    		Calendar instance = GregorianCalendar.getInstance();
	    		instance.add(Calendar.DAY_OF_YEAR, -14);//Set 2 weeks before current date
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    		String sacctDate = sdf.format(instance.getTime());
	    		String commandStr = "sacct --user=vcell --name "+slurmJobNames.toString()+" --format=\"JobID%20,JobName%30,State%20,Submit,Start,User,NodeList,ExitCode\" -S "+sacctDate;
	    		session = sshClient.startSession();
				command = session.exec(commandStr);
				command.join();
				CommandOutput commandOutput = new CommandOutput(commandStr, command);
				if(command.getExitStatus() != null && ((specialError==null && command.getExitStatus() != 0) || (specialError != null && command.getExitStatus() >= specialError))){
					throw new CommandExecError("Error status for command '"+commandStr+"'\n"+commandOutput.getStandardError());
				}
		    	System.out.println("startVCellServiceDaemon cmd= "+commandOutput.getCommandStr());
		    	String standardOutput = commandOutput.getStandardOutput();
		    	System.out.println("//--------------------\n// Slurm query VCell sim jobs\n//--------------------");
				System.out.println("startVCellServiceDaemon stdout=\n"+standardOutput);
		    	System.out.println("startVCellServiceDaemon stderr=\n"+commandOutput.getStandardError());
		    	System.out.println("startVCellServiceDaemon exit= "+commandOutput.getExitStatus());
		    	StringReader sr = new StringReader(standardOutput);
		    	BufferedReader br = new BufferedReader(sr);
		    	String nextStr = null;
		    	while((nextStr = br.readLine()) != null) {
		    		
//		    		Long slurmJobID = Long.parseLong(st.nextToken());
//		    		if(!st.hasMoreTokens()) {
//		    			continue;
//		    		}
//		    		String s = st.nextToken();
		    		if(nextStr.contains("V_")) {
		    			StringTokenizer st = new StringTokenizer(nextStr, " ",false);
		    			//GEt slurmjobid (and array job index if present)
		    			String slurmJobIDAndArrayIndex = st.nextToken();
		    			Long arrIndex = null;
		    			Long slurmJobID = null;
		    			if(slurmJobIDAndArrayIndex.contains("_")) {
		    				slurmJobID = Long.parseLong(slurmJobIDAndArrayIndex.substring(0,slurmJobIDAndArrayIndex.indexOf("_")));
		    				arrIndex = Long.parseLong(slurmJobIDAndArrayIndex.substring(slurmJobIDAndArrayIndex.indexOf("_")+1));
		    			}else {
		    				slurmJobID = Long.parseLong(slurmJobIDAndArrayIndex);
		    			}
		    			String slurmJobName = st.nextToken();
		    			VCellSlurmAssoc entry = dbSlurmMap0.remove(slurmJobName);;//dbSlurmMap0.get(slurmJobName);
//		    			String slurmJobArrName = slurmJobID+(arrIndex==null?"":"_"+arrIndex);
						if(entry == null) {
		    				entry = new VCellSlurmAssoc();
//		    				jobArrMap.put(slurmJobIDAndArrayIndex, entry);
		    			}else {
		    				entry = new VCellSlurmAssoc(entry);
//		    				jobArrMap.put(slurmJobIDAndArrayIndex, entry);
		    			}
	    				jobArrMap.put(slurmJobIDAndArrayIndex, entry);
//		    			else if(entry.slurmState != null) {
//		    				throw new Exception("not expecting dup slurm");
//		    			}
		    			entry.slurmJobID = slurmJobID;
		    			entry.slurmJobArrIndex = arrIndex;
		    			entry.slurmState = st.nextToken();
		    			String temp = st.nextToken();
		    			if(temp.equals("by")) {// process entries like "CANCELLED by 0"
		    				st.nextToken();
		    				temp = st.nextToken();
		    			}
		    			entry.slurmSubmitDate = temp;//st.nextToken();
		    			entry.slurmStartDate = st.nextToken();
		    			st.nextToken();//skip User
		    			entry.slurmNodeList = st.nextToken();
		    		}
		    	}
			}finally {
	    		if(command != null){try{command.close();}catch(Exception e){e.printStackTrace();}}
	    		if(session != null){try{session.close();}catch(Exception e){e.printStackTrace();}}
			}
	    	String completeStr = "update vc_simulationjob set schedulerstatus=4,statusmsg='WORKEREVENT_COMPLETED|completed' where id in (";
			StringBuffer updateComplete = new StringBuffer();
			String failStr = "update vc_simulationjob set schedulerstatus=6,statusmsg='JOB_FAILED|failed:slurm NODE_FAIL' where id in (";
			StringBuffer updateFail = new StringBuffer();
			System.out.println("//--------------------\n// Entries Match DB and Slurm\n//--------------------");
		    for(String jobname:jobArrMap.keySet()) {
		    	VCellSlurmAssoc entry = jobArrMap.get(jobname);
		    	boolean bSuspicious = false;
		    	if(entry.vcellSchedulerStatus != null && entry.slurmState != null) {
		    		bSuspicious = entry.vcellSchedulerStatus==3 && !entry.slurmState.equals("RUNNING");
		    		if(bSuspicious && entry.slurmState.equals("COMPLETED")) {
		    			updateComplete.append((updateComplete.length()==0?completeStr:",")+entry.vc_simulationjobID);
		    		}else if(bSuspicious && entry.slurmState.equals("NODE_FAIL")) {
		    			updateFail.append((updateFail.length()==0?failStr:",")+entry.vc_simulationjobID);
		    		}
		    	}
		    	System.out.println((bSuspicious?"*":" ")+" "+f(entry.slurmJobName,30)+
		    			" sjid="+f(entry.slurmJobID+"",12)+" sjarr="+f(entry.slurmJobArrIndex+"",3)+
		    			" sState="+f(entry.slurmState,12)+" sNodeList="+f(entry.slurmNodeList,12)+
		    			" vjid="+f(entry.vc_simulationjobID+"",15)+" vusr="+f(entry.vcellUserName,20)+
		    			" vstate="+f(entry.vcellSchedulerStatus.intValue()+"",2)+
		    			" vstateTxt="+f(SimulationJobStatus.SchedulerStatus.fromDatabaseNumber(entry.vcellSchedulerStatus.intValue())+"",12)+
		    			" vstatus="+f(entry.vcellStatusMsg+"",60)
		    			);
		    }
		    if(updateComplete.length()>0) {updateComplete.append(");");}
		    if(updateFail.length()>0) {updateFail.append(");");}
		    System.out.println(updateComplete);
		    System.out.println(updateFail);
		    
		    //Entries in DB but not Slurm
		    // See C:\\Users\frm\vcellSchedulerQueriesForSlurm.sql
		    // update vc_simulationjob set schedulerstatus=5,statusmsg='stopped' where id in (xxx);
		    long currTime = Calendar.getInstance().getTimeInMillis();
		    System.out.println("//--------------------\n// Entries in DB but not Slurm (may be caused by quota waiting (OK) or may be some other problem (NOT OK))\n//--------------------");
		    StringBuffer vc_simulationjob_ids = new StringBuffer();
		    for(String slurnJobName:dbSlurmMap0.keySet()) {
		    	VCellSlurmAssoc entry = dbSlurmMap0.get(slurnJobName);
		    	System.out.println(/*(bSuspicious?"*":" ")+*/" "+f(entry.slurmJobName,30)+
		    			" qdate="+f(entry.vcellQueueDate,30)+" diff="+((currTime-entry.vcellQueueDate.getTime())/(1000*60*60))+
		    			" sjid="+f(entry.slurmJobID+"",12)+" sjarr="+f(entry.slurmJobArrIndex+"",3)+
		    			" sState="+f(entry.slurmState,12)+" sNodeList="+f(entry.slurmNodeList,12)+
		    			" vjid="+f(entry.vc_simulationjobID+"",15)+" vusr="+f(entry.vcellUserName,20)+
		    			" vstate="+f(entry.vcellSchedulerStatus.intValue()+"",2)+
		    			" vstateTxt="+f(SimulationJobStatus.SchedulerStatus.fromDatabaseNumber(entry.vcellSchedulerStatus.intValue())+"",12)+
		    			" vstatus="+f(entry.vcellStatusMsg+"",60)
		    			);
		    	if(entry.vcellSchedulerStatus.intValue() == SimulationJobStatus.SchedulerStatus.DISPATCHED.getDatabaseNumber()) {
		    		vc_simulationjob_ids.append((vc_simulationjob_ids.length()!=0?",":"")+entry.vc_simulationjobID);
		    	}
		    }
		    System.out.println("\nCommand example to change VCellDB status of dispatched but no running slurm to 'stopped' ONLY USE THIS ID YOU KNOW IT'S APPROPRIATE");
		    System.out.println("update vc_simulationjob set schedulerstatus=5,statusmsg='stopped' where id in ("+vc_simulationjob_ids.toString()+");\n");
		    
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con != null){try{con.close();}catch(Exception e){e.printStackTrace();}}
			if(sshClient != null){try{sshClient.disconnect();}catch(Exception e2){e2.printStackTrace();}}
		}
	}

	private static String f(Object obj, int size) {
		if(obj == null || obj instanceof String) {
			String s = (String)obj;
			return BeanUtils.forceStringSize(s, size, " ", false);
		}else if(obj instanceof Timestamp) {
			Timestamp ts = (Timestamp)obj;
			return BeanUtils.forceStringSize(ts.toString(), size, " ", false);
		}
		throw new RuntimeException("Not Expecting Object type "+obj.getClass().getName());
	}
}
