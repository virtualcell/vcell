package cbit.vcell.util;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.rmi.Naming;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.management.openmbean.KeyAlreadyExistsException;
import javax.swing.Timer;

import org.vcell.monitor.pushover.Notify;
import org.vcell.util.BeanUtils;
import org.vcell.util.BigString;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.desktop.biomodel.ApplicationSimulationsPanel.SimulationsPanelTabID;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.SimulationData.SimDataAmplistorInfo;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatusPersistent;
import cbit.vcell.util.AmplistorUtils;
import cbit.vcell.util.AmplistorUtils.AmplistorCredential;
import cbit.vcell.xml.XmlHelper;


public class TestMissingSimData {

	private static class SimIDAndJobID{
		public KeyValue simID;
		public int jobID;
		public User user;
		public int dimension;
		public String modelName;
		public String appName;
		public String simName;
		public SimulationJobStatus.SchedulerStatus schedulerStatus;
		public Boolean bHasData;
		public BigDecimal privacy;
		public String softareVersion;
		public Timestamp  timestamp;
		public SimIDAndJobID(KeyValue simID, int jobID,User user,int dimension,String modelName,String appName,String simName,SimulationJobStatus.SchedulerStatus schedulerStatus,Boolean bHasData,BigDecimal privacy,String softareVersion,Timestamp  timestamp) {
			super();
			this.simID = simID;
			this.jobID = jobID;
			this.user = user;
			this.dimension = dimension;
			this.modelName = modelName;
			this.appName = appName;
			this.simName = simName;
			this.schedulerStatus = schedulerStatus;
			this.bHasData = bHasData;
			this.privacy = privacy;
			this.softareVersion = softareVersion;
			this.timestamp = timestamp;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
//		if(true){
//			//runSim(new VCSimulationIdentifier(new KeyValue("84915441"), new User("frm",new KeyValue("227"))), new UserLoginInfo("frm", new DigestedPassword("frmfrm")));
//			startClient(new VCSimulationIdentifier(new KeyValue("84915441"), new User("frm",new KeyValue("227"))), new UserLoginInfo("frm", new DigestedPassword("frmfrm")));
//			return;
//		}
		String driverName = "oracle.jdbc.driver.OracleDriver";
		String host = "dbs6.cam.uchc.edu";
		String db = "orcl";
		String connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
		String dbSchemaUser = "vcell";
		String dbPassword = args[0];
		java.sql.Connection con = null; 
		try{
//			Class.forName(driverName);
//			con = java.sql.DriverManager.getConnection(connectURL, dbSchemaUser, dbPassword);
//			con.setAutoCommit(false);
			
			
			
//			Statement stmt = con.createStatement();
////			String sql = 
////					"select t1.simid,t1.jobid,vc_userinfo.id userkey,vc_userinfo.userid,vc_biomodelsim.biomodelref,vc_mathmodelsim.mathmodelref,vc_geometry.dimension,t1.taskid,t1.hasdata,t1.schedulerstatus"+
////					" from (select vc_simulation.id simid ,vc_simulationjob.jobindex jobid,vc_simulation.ownerref,vc_simulation.mathref,vc_simulationjob.hasdata,vc_simulationjob.taskid,vc_simulationjob.schedulerstatus from vc_simulation,vc_simulationjob where vc_simulationjob.simref(+) = vc_simulation.id) t1,"+
////					" vc_userinfo,vc_biomodelsim,vc_mathmodelsim,vc_math,vc_geometry"+
////					" where vc_biomodelsim.simref(+)=t1.simid and vc_mathmodelsim.simref(+)=t1.simid and t1.ownerref=vc_userinfo.id and t1.mathref = vc_math.id and vc_math.geometryref=vc_geometry.id and t1.schedulerstatus=4  and lower(t1.hasdata)='y'"+
//////					" and vc_userinfo.userid='frm'"+
////					" order by userid,t1.simid,t1.taskid";
//////					"select t1.simid,t1.jobid,vc_userinfo.id userkey,vc_userinfo.userid,vc_biomodelsim.biomodelref,vc_mathmodelsim.mathmodelref from "+
//////					" (select vc_simulation.id simid ,vc_simulationjob.jobindex jobid,vc_simulation.ownerref from vc_simulation,vc_simulationjob where vc_simulationjob.simref(+) = vc_simulation.id) t1, vc_userinfo,vc_biomodelsim,vc_mathmodelsim"+
//////					" where vc_biomodelsim.simref(+)=t1.simid and vc_mathmodelsim.simref(+)=t1.simid and t1.ownerref=vc_userinfo.id"+
////////					" and vc_userinfo.userid='$ch@ck$'"+
//////					" and vc_userinfo.userid='frm'"+
//////					" order by userid,t1.simid";
//			String sql =
//					" select vc_simulation.id,vc_simulationjob.jobindex,vc_userinfo.id userkey,vc_userinfo.userid,decode(t1.type,'biomodel',t1.modelid) biomodelid,decode(t1.type,'mathmodel',t1.modelid) mathmodelid,vc_geometry.dimension, t1.name modelName,t1.appname,t1.simName,vc_simulationjob.schedulerstatus,vc_simulationjob.taskid,vc_simulationjob.hasdata,t1.privacy,vc_softwareversion.softwareversion,t1.versiondate"+
//							" from "+
//							" ( "+
//							" select 'biomodel' type,vc_biomodel.name,vc_biomodel.id modelid,vc_biomodelsim.simref,vc_biomodel.ownerref,vc_biomodel.privacy,vc_simcontext.name appname,vc_simulation.parentsimref,vc_simulation.name simName,vc_biomodel.versiondate"+
//							"     from vc_biomodel,vc_biomodelsim,vc_simcontext,vc_biomodelsimcontext,vc_simulation "+
//							"     where "+
//							"         vc_biomodelsim.simref=vc_simulation.id and vc_biomodelsim.biomodelref=vc_biomodel.id and vc_simcontext.id=vc_biomodelsimcontext.simcontextref and vc_biomodel.id=vc_biomodelsimcontext.biomodelref and vc_simcontext.mathref=vc_simulation.mathref "+
//							" union "+
//							" select 'mathmodel' type,vc_mathmodel.name,vc_mathmodel.id modelid,vc_mathmodelsim.simref,vc_mathmodel.ownerref,vc_mathmodel.privacy,null appname,vc_simulation.parentsimref,vc_simulation.name simName,vc_mathmodel.versiondate"+
//							"     from vc_mathmodel ,vc_mathmodelsim,vc_simulation "+
//							"     where vc_mathmodelsim.mathmodelref=vc_mathmodel.id and vc_simulation.id=vc_mathmodelsim.simref "+
//							" ) t1,vc_userinfo,vc_simulation,vc_simulationjob,vc_math,vc_geometry,vc_softwareversion "+
//							" where "+
////							" vc_simulation.id=nvl(t1.parentsimref,t1.simref) "+
//							"  t1.parentsimref is null " +
//							" and vc_simulation.id=t1.simref "+
//							" and vc_simulationjob.simref=vc_simulation.id "+
//							" and vc_simulationjob.hasdata is not null AND lower(vc_simulationjob.hasdata)='y' "+
////							" and vc_simulationjob.schedulerstatus=4 "+
////							" and t1.privacy!=0 "+
//							" and vc_math.id=vc_simulation.mathref "+
//							" and vc_math.geometryref=vc_geometry.id "+
//							" and vc_userinfo.id=t1.ownerref "+
//							" AND vc_softwareversion.versionableref(+)=vc_simulation.id "+
//							" and vc_userinfo.userid='mblinov' "+
//							" order by vc_simulation.id,vc_simulationjob.taskid ";
//			ResultSet rset = stmt.executeQuery(sql);
//			checkFiles2(createArrayList(rset));
//			rset.close();
			
			
//			checkDataExists(con,false);
			runSimsNew(connectURL,dbSchemaUser,dbPassword);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con != null){
				con.close();
			}
		}
	}
	
	private static Hashtable<KeyValue, RerunMissing> threadHolder = new Hashtable<>();
	private static boolean hasKey(VCSimulationIdentifier vcSimulationIdentifier){
		synchronized(threadHolder){
			return threadHolder.containsKey(vcSimulationIdentifier.getSimulationKey());
		}
	}
	private static Timer printTimer = null;
	private static void printThreadHolder(){
		synchronized(threadHolder){
			if(printTimer == null){
				printTimer = new Timer(5000,null);
				printTimer.setRepeats(false);
			}
			if(printTimer.isRunning()){
				return;
			}
//			Iterator<RerunMissing> rerunIter = threadHolder.values().iterator();
			RerunMissing[] rerunArr = threadHolder.values().toArray(new RerunMissing[0]);
			Arrays.sort(rerunArr, new Comparator<RerunMissing>() {
				@Override
				public int compare(RerunMissing o1, RerunMissing o2) {
					return o1.getVCSimulationIdentifier().getSimulationKey().compareTo(o2.getVCSimulationIdentifier().getSimulationKey());
				}
			});
			System.out.println("-----\n-----");
			for(int i=0;i<rerunArr.length;i++){
				System.out.println(
					BeanUtils.forceStringSize(rerunArr[i].getVCSimulationIdentifier().getSimulationKey().toString(), 12, " ", true)+" "+
					BeanUtils.forceStringSize((rerunArr[i].getSimulation()!=null?"'"+rerunArr[i].getSimulation().getVersion().getOwner().getName()+"'":"null"), 15, " ", false)+" "+
					BeanUtils.forceStringSize((rerunArr[i].getSimulation()!=null?"scan="+rerunArr[i].getSimulation().getScanCount():"null"), 10, " ", false)+" "+
					rerunArr[i].getLastSimStatus());
			}
			System.out.println("-----\n-----");
			printTimer.restart();
		}
	}
	private static boolean acquireThread(VCSimulationIdentifier vcSimulationIdentifier,UserLoginInfo userLoginInfo,
			String connectURL,String dbSchemaUser, String dbPassword) throws Exception{
		synchronized(threadHolder){
			Iterator<RerunMissing> rerunIter = threadHolder.values().iterator();
			while(rerunIter.hasNext()){
				RerunMissing rerunMissing = rerunIter.next();
				if(rerunMissing.getSimulation() != null){
					if(rerunMissing.getSimulation().getScanCount() > 1){
						//don't proceed until scancounts are done
						return false;
					}
				}else{
					//can't get sccancount info, try back later
					return false;
				}
			}
			
			if(threadHolder.size() < 10){
				threadHolder.put(vcSimulationIdentifier.getSimulationKey(), new RerunMissing(vcSimulationIdentifier,userLoginInfo, connectURL, dbSchemaUser, dbPassword));
				new Thread(threadHolder.get(vcSimulationIdentifier.getSimulationKey()),"rerun_"+vcSimulationIdentifier.getID()).start();
				return true;
			}
			threadHolder.wait();
			return false;
		}
	}
	private static void freeThread(VCSimulationIdentifier vcSimulationIdentifier,Statement stmt) throws Exception{
		synchronized(threadHolder){
			try{
				RerunMissing rerunMissing = threadHolder.remove(vcSimulationIdentifier.getSimulationKey());
				if(rerunMissing == null){
					throw new Exception("Couldn't find "+vcSimulationIdentifier+" key in threadHolder");
				}
				if(rerunMissing.getException() == null && rerunMissing.getSuccessMessage() == null){
					throw new Exception("Couldn't find exit status for "+vcSimulationIdentifier);	
				}
				if(rerunMissing.getException() != null && rerunMissing.getSuccessMessage() != null){
					throw new Exception("Unexpected success and failure exit status for "+vcSimulationIdentifier);	
				}
				if(rerunMissing.getException() != null){
					System.out.println(rerunMissing.getException().getMessage());
					stmt.executeUpdate(rerunMissing.getException().getMessage());
				}else if(rerunMissing.getSuccessMessage() != null){
					System.out.println(rerunMissing.getSuccessMessage());
					stmt.executeUpdate(rerunMissing.getSuccessMessage());
				}
			}finally{
				threadHolder.notifyAll();
			}
		}
	}
	private static class RerunMissing implements Runnable {

		private VCSimulationIdentifier vcSimulationIdentifier;
		private VCellBootstrap vCellBootstrap;
		private UserLoginInfo userLoginInfo;
		private Exception exception;
		private String successMessage;
		private String connectURL;
		private String dbSchemaUser;
		private String dbPassword;
		private SimulationStatusPersistent lastSimStatus;
		private Simulation simulation;
		public RerunMissing(VCSimulationIdentifier vcSimulationIdentifier,UserLoginInfo userLoginInfo,String connectURL,String dbSchemaUser, String dbPassword) throws Exception{
			this.vcSimulationIdentifier = vcSimulationIdentifier;
//			this.vCellBootstrap = vCellBootstrap;
			this.userLoginInfo = userLoginInfo;
			this.connectURL = connectURL;
			this.dbSchemaUser = dbSchemaUser;
			this.dbPassword = dbPassword;
//			VCellBootstrap vCellBootstrap = getVCellBootstrap("rmi-beta.cam.uchc.edu", 40105, "VCellBootstrapServer", 12, false);
//			this.vCellBootstrap = getVCellBootstrap("rmi-alpha.cam.uchc.edu", 40106, "VCellBootstrapServer", 12, false);//Alpha
			this.vCellBootstrap = getVCellBootstrap("rmi-alpha.cam.uchc.edu", 40111, "VCellBootstrapServer", 12, false);//Test2
			

		}
		public Simulation getSimulation(){
			return simulation;
		}
		public VCSimulationIdentifier getVCSimulationIdentifier(){
			return vcSimulationIdentifier;
		}
		public SimulationStatusPersistent getLastSimStatus(){
			return lastSimStatus;
		}
		public Exception getException(){
			return exception;
		}
		public String getSuccessMessage(){
			return successMessage;
		}
		@Override
		public void run() {
			if(!hasKey(vcSimulationIdentifier)){
				new Exception("Sim key "+vcSimulationIdentifier.getSimulationKey()+" not found").printStackTrace();
				System.exit(1);
			}
			try{
				VCellConnection vcellConnection = vCellBootstrap.getVCellConnection(userLoginInfo);
				BigString simXML = vcellConnection.getUserMetaDbServer().getSimulationXML(vcSimulationIdentifier.getSimulationKey());
				simulation = XmlHelper.XMLToSim(simXML.toString());				

				long startTime = System.currentTimeMillis();
				SimulationStatusPersistent initSimulationStatus = null;
				while(initSimulationStatus == null){
					vcellConnection.getMessageEvents();
					initSimulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
					Thread.sleep(5000);
					if((System.currentTimeMillis()-startTime) > 15000){
						System.out.println("-----Couldn't get initial simulation status");
						break;
					}
				}
				System.out.println("initial status="+initSimulationStatus);
//				if(!initSimulationStatus.isCompleted() || !initSimulationStatus.getHasData()){
//					return;
//				}

				
//				if(!sim.isSpatial()){
//					continue;
//				}
//				if(sim.getSolverTaskDescription().isSerialParameterScan()/* || sim.getSolverTaskDescription().getExpectedNumTimePoints() > 20*/){
//					continue;
//				}
				
				int scanCount = simulation.getScanCount();
//				if(true){return;}
				SimulationStatusPersistent simulationStatus = null;
				SimulationInfo simulationInfo = simulation.getSimulationInfo();
				if(!simulationInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey().equals(vcSimulationIdentifier.getSimulationKey())){
					throw new Exception("Unexpected authoritative and sim id are not the same");
				}

			vcellConnection.getSimulationController().startSimulation(vcSimulationIdentifier, scanCount);
			startTime = System.currentTimeMillis();
//			SimulationStatusPersistent lastSimStatus = simulationStatus;
			while(simulationStatus == null || simulationStatus.isStopped() || simulationStatus.isCompleted() || simulationStatus.isFailed()){
				for(int i = 0;i<1;i++){
					MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
					Thread.sleep(2000);
				}
				simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
				if(simulationStatus.isFailed() && (initSimulationStatus==null || !initSimulationStatus.isFailed())){
					break;
				}
				if((System.currentTimeMillis()-startTime) > 30000){
					throw new Exception("-----Sim finished too fast or took too long to start, status= "+simulationStatus);
				}
				if(!simulationStatus.toString().equals((lastSimStatus==null?null:lastSimStatus.toString()))){
					lastSimStatus = simulationStatus;
					printThreadHolder();
//					System.out.println("initial status="+simulationStatus);
				}
			}
			lastSimStatus = simulationStatus;
			while(!simulationStatus.isStopped() && !simulationStatus.isCompleted() && !simulationStatus.isFailed()){
				for(int i = 0;i<2;i++){
					MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
					Thread.sleep(2000);
				}
				simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
				if(!simulationStatus.toString().equals(lastSimStatus.toString())){
					lastSimStatus = simulationStatus;
					printThreadHolder();
//					System.out.println("running status="+simulationStatus);
				}
			}

			if(!lastSimStatus.isCompleted()){
				throw new Exception("Unexpected run status: "+lastSimStatus.toString());
			}
			
			String updatestr = "update missingdata set notes='"+DB_NOTES_SIM_RUNOK_CODE+"' where simjobsimref="+vcSimulationIdentifier.getSimulationKey();
			System.out.println(vcSimulationIdentifier+" "+updatestr);
			successMessage = updatestr;
			exception = null;
		}catch(Exception e){
			e.printStackTrace();
			String errString = TokenMangler.fixTokenStrict(DB_NOTES_SIM_ERROR_CODE+e.getClass().getSimpleName()+" "+e.getMessage());
			if(errString.length() > 256){
				errString = errString.substring(0, 256);
			}
			String updatestr = "update missingdata set notes='"+errString+"' where simjobsimref="+vcSimulationIdentifier.getSimulationKey();
			System.out.println(vcSimulationIdentifier+" "+updatestr);
			successMessage = null;
			exception = new Exception(updatestr,e);
		}finally{
			Connection con = null;
			Statement stmt = null;
			try{
				con = java.sql.DriverManager.getConnection(connectURL, dbSchemaUser, dbPassword);
				stmt = con.createStatement();
				freeThread(vcSimulationIdentifier,stmt);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}finally{
				if(stmt!=null){try{stmt.close();}catch(Exception e2){e2.printStackTrace();}}
				if(con!=null){try{con.close();}catch(Exception e2){e2.printStackTrace();System.exit(1);}}
			}
		}
	
		}
	}
	
	
	
	
	
	private static Hashtable<KeyValue, UserLoginInfo> doQuery(String connectURL,String dbSchemaUser, String dbPassword) throws Exception{
//		String itemSelectSQL = " select vc_userinfo.userid,vc_userinfo.id userkey,vc_userinfo.digestpw,vc_simulation.id simjobsimref,vc_softwareversion.softwareversion ";
		String itemSelectSQL = " select vc_userinfo.userid,vc_userinfo.id userkey,vc_userinfo.digestpw,vc_simulation.id simjobsimref ";

		String sqlPart =
				" from missingdata,vc_simulation,vc_userinfo,vc_softwareversion "+
//				" from missingdata,vc_simulation,vc_userinfo,vc_softwareversion,vc_biomodelsim,vc_biomodel "+
//				" from vc_simulation,vc_userinfo,vc_biomodelsim,vc_biomodel "+
				" where "+
//				" (vc_simulation.id in (select simref from vc_biomodelsim)) and " +
//				" (vc_simulation.id in (select simref from vc_mathmodelsim)) and " +
//				" vc_userinfo.userid='les' and "+
				" vc_userinfo.id = vc_simulation.ownerref and "+
//				" vc_biomodelsim.simref = vc_simulation.id and" +
//				" vc_biomodelsim.biomodelref = vc_biomodel.id and" +
//				" vc_biomodel.name='Solver Suite alpha' and" +
//				" to_char(vc_biomodel.versiondate,'DD-MM-YYYY')='10-05-2015' " +
				" missingdata.simjobsimref = vc_simulation.id and "+
				" (missingdata.dataexists='false') "+
				" and missingdata.notes is null " +
//				" and (missingdata.notes like '%Compiled_solvers_no_longer%' or missingdata.notes like '%Connection_refused%')" +
//				" and (missingdata.notes like '%exceeded_maximum%')" +
//				" and (missingdata.notes like '%too_many_jobs%')" +
				" and vc_simulation.parentsimref is null "+
				" and (softwareversion is null or regexp_substr(softwareversion,'^((release)|(rel)|(alpha)|(beta))_version_([[:digit:]]+\\.?)+_build_([[:digit:]]+\\.?)+',1,1,'i') is not null) "+
				" and vc_softwareversion.versionableref (+) = vc_simulation.id " +
				" order by vc_userinfo.userid";

//		(mdt.dataexists = 'false' or mdt.dataexists like 'error - %') and
//		(mdt.notes is null or mdt.notes not like 'reran OK%') and

		Hashtable<KeyValue, UserLoginInfo> keyUserLoginInfoHash = new Hashtable<>();
		
		Connection con = null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = java.sql.DriverManager.getConnection(connectURL, dbSchemaUser, dbPassword);
			con.setAutoCommit(false);

			Statement queryStatement = con.createStatement();
			//Get Count
			ResultSet rset = queryStatement.executeQuery("select count(*) "+sqlPart);
			rset.next();
			int totalCount = rset.getInt(1);
			rset.close();
			//Get sims
			rset = queryStatement.executeQuery(itemSelectSQL+sqlPart);
			UserLoginInfo userLoginInfo = null;
			int currentCount = 1;
			while(rset.next()){
				KeyValue simJobSimRef = new KeyValue(rset.getString("simjobsimref"));
	
//					String softwareVersion = rset.getString("softwareversion");
					String userid = rset.getString("userid");
	
	//				if(!rset.wasNull() && softwareVersion != null){
	//					StringTokenizer st = new StringTokenizer(softwareVersion, "_");
	//					st.nextToken();//site name
	//					st.nextToken();//'Version' literal string
	//					String majorVersion = st.nextToken();//major version number
	//					if(majorVersion.equals("5.4")){
	//						throw new Exception("Alpha-5.4 sims are not being re-run using Beta-5.3 code");
	//					}
	//				}
	
					if(userid.toLowerCase().equals("vcelltestaccount")
//						 || userid.toLowerCase().equals("anu")
//						 || userid.toLowerCase().equals("fgao")
//						 || userid.toLowerCase().equals("liye")
//						 || userid.toLowerCase().equals("schaff")
//						 || userid.toLowerCase().equals("ignovak")
	//					 || userid.toLowerCase().equals("jditlev")
	//					 || userid.toLowerCase().equals("sensation")
						 ){
						continue;
					}
					String userkey = rset.getString("userkey");
					if(userLoginInfo == null || !userLoginInfo.getUserName().equals(userid)){
						userLoginInfo = new UserLoginInfo(userid,DigestedPassword.createAlreadyDigested(rset.getString("digestpw")));
						userLoginInfo.setUser(new User(userid, new KeyValue(userkey)));
					}
					VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simJobSimRef, userLoginInfo.getUser());
					keyUserLoginInfoHash.put(vcSimulationIdentifier.getSimulationKey(), userLoginInfo);
			}
			return keyUserLoginInfoHash;	
		}finally{
			if(con!=null){try{con.close();}catch(Exception e2){e2.printStackTrace();}}
		}
	}
	private static final String DB_NOTES_SIM_ERROR_CODE = "error - ";
	private static final String DB_NOTES_SIM_RUNOK_CODE = "reran OK";
	
	private static void runSimsNew(String connectURL,String dbSchemaUser, String dbPassword) throws Exception{
		
		Hashtable<KeyValue, UserLoginInfo> keyUserLoginInfo = doQuery(connectURL, dbSchemaUser, dbPassword);
		Enumeration<KeyValue> keys = keyUserLoginInfo.keys();
		int currentCount = 0;
		while(keys.hasMoreElements()){
			KeyValue simKey = keys.nextElement();
			UserLoginInfo userLoginInfo = keyUserLoginInfo.get(simKey);
			VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKey, userLoginInfo.getUser());
			while(acquireThread(vcSimulationIdentifier, userLoginInfo, connectURL, dbSchemaUser, dbPassword)==false){
				Thread.sleep(2000);
			}
			System.out.println("-----");
			System.out.println("-----running "+currentCount+" of "+keyUserLoginInfo.size()+"   user="+userLoginInfo.getUser()+" simjobsimref="+vcSimulationIdentifier.getSimulationKey());
			currentCount+= 1;
			System.out.println("-----");
		}
	}
//	private static void runSimsNew(String connectURL,String dbSchemaUser, String dbPassword) throws Exception{
//		
////		VCellBootstrap vCellBootstrap = getVCellBootstrap("rmi-beta.cam.uchc.edu", 40105, "VCellBootstrapServer", 12, false);
////		VCellBootstrap vCellBootstrap = getVCellBootstrap("rmi-alpha.cam.uchc.edu", 40106, "VCellBootstrapServer", 12, false);
//		VCellBootstrap vCellBootstrap = getVCellBootstrap("rmi-alpha.cam.uchc.edu", 40111, "VCellBootstrapServer", 12, false);//Test2
//
//		if(true){
//			Hashtable<KeyValue, UserLoginInfo> keyUserLoginInfo = doQuery(connectURL, dbSchemaUser, dbPassword);
//			Enumeration<KeyValue> keys = keyUserLoginInfo.keys();
//			while(keys.hasMoreElements()){
//				KeyValue simKey = keys.nextElement();
//				UserLoginInfo userLoginInfo = keyUserLoginInfo.get(simKey);
//				VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKey, userLoginInfo.getUser());
//				acquireThread(vcSimulationIdentifier, vCellBootstrap, userLoginInfo, connectURL, dbSchemaUser, dbPassword);
//			}
//			return;
//		}
//		
//		
//		String itemSelectSQL = " select vc_userinfo.userid,vc_userinfo.id userkey,vc_userinfo.digestpw,missingdata.simjobsimref,vc_softwareversion.softwareversion ";
//
//		String sqlPart =
//				" from missingdata,vc_simulation,vc_userinfo,vc_softwareversion "+
//				" where "+
//				" (vc_simulation.id in (select simref from vc_biomodelsim)) and " +
////				" (vc_simulation.id in (select simref from vc_mathmodelsim)) and " +
//				" vc_userinfo.userid='fgao5' and "+
//				" vc_userinfo.id = vc_simulation.ownerref and "+
//				" missingdata.simjobsimref = vc_simulation.id and "+
////				" missingdata.dataexists not like 'readable%' and "+
//				" (missingdata.dataexists = 'false') "+
//				" and missingdata.notes is not null and " +
//				" (missingdata.notes like '%Compiled_solvers_no_longer%' or missingdata.notes like '%Connection_refused%')" +
////				" and missingdata.simjobsimref = 34080002 " +
////				" or missingdata.dataexists like 'error - %') "+
////				" and (missingdata.notes is null ) "+
////				" or missingdata.notes not like 'reran OK%')" +
//				" and vc_simulation.parentsimref is null and "+
//				" (softwareversion is null or regexp_substr(softwareversion,'^((release)|(rel)|(alpha)|(beta))_version_([[:digit:]]+\\.?)+_build_([[:digit:]]+\\.?)+',1,1,'i') is not null) and "+
//				" vc_softwareversion.versionableref (+) = vc_simulation.id " +
////				" and rownum = 1 ";
////				" and vc_simulation.id=39116536"
//				" order by vc_userinfo.userid";
//
////		(mdt.dataexists = 'false' or mdt.dataexists like 'error - %') and
////		(mdt.notes is null or mdt.notes not like 'reran OK%') and
//
//		//Create hash of sims and userlogininfo
//		Hashtable<KeyValue, UserLoginInfo> simToUserLoginInfoHash = new Hashtable<>();
//		Statement updateStatement = con.createStatement();
//		Statement queryStatement = con.createStatement();
//		//Get Count
//		ResultSet rset = queryStatement.executeQuery("select count(*) "+sqlPart);
//		rset.next();
//		int totalCount = rset.getInt(1);
//		rset.close();
//		//Get sims
//		rset = queryStatement.executeQuery(itemSelectSQL+sqlPart);
//		UserLoginInfo userLoginInfo = null;
//		VCellConnection vcellConnection = null;
//		int currentCount = 1;
//		while(rset.next()){
//			KeyValue simJobSimRef = new KeyValue(rset.getString("simjobsimref"));
//			try{
//				String softwareVersion = rset.getString("softwareversion");
//				String userid = rset.getString("userid");
//				System.out.println("-----");
//				System.out.println("-----running "+currentCount+" of "+totalCount+"   user="+userid+" simjobsimref="+simJobSimRef);
//				currentCount+= 1;
//				System.out.println("-----");
//
//				if(!rset.wasNull() && softwareVersion != null){
//					StringTokenizer st = new StringTokenizer(softwareVersion, "_");
//					st.nextToken();//site name
//					st.nextToken();//'Version' literal string
//					String majorVersion = st.nextToken();//major version number
//					if(majorVersion.equals("5.4")){
//						throw new Exception("Alpha-5.4 sims are not being re-run using Beta-5.3 code");
//					}
//				}
//
//				if(userid.toLowerCase().equals("vcelltestaccount")
////					 || userid.toLowerCase().equals("anu")
////					 || userid.toLowerCase().equals("fgao")
////					 || userid.toLowerCase().equals("liye")
////					 || userid.toLowerCase().equals("schaff")
////					 || userid.toLowerCase().equals("ignovak")
////					 || userid.toLowerCase().equals("jditlev")
////					 || userid.toLowerCase().equals("sensation")
//					 ){
//					continue;
//				}
//				String userkey = rset.getString("userkey");
//				if(userLoginInfo == null || !userLoginInfo.getUserName().equals(userid)){
//					userLoginInfo = new UserLoginInfo(userid,DigestedPassword.createAlreadyDigested(rset.getString("digestpw")));
//					userLoginInfo.setUser(new User(userid, new KeyValue(userkey)));
//					vcellConnection = null;
//				}
//				VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simJobSimRef, userLoginInfo.getUser());
//				
//				
//				//
//				//
//				//
//				if(true){
//					acquireThread(vcSimulationIdentifier, vCellBootstrap, userLoginInfo, connectURL, dbSchemaUser, dbPassword);
//					continue;
//				}
//				
//				try{
//					if(vcellConnection != null){
//						vcellConnection.getMessageEvents();
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//					//assume disconnected
//					vcellConnection = null;
//				}
//				if(vcellConnection == null){
//					vcellConnection = vCellBootstrap.getVCellConnection(userLoginInfo);
//					vcellConnection.getMessageEvents();
//				}
//				
//				SimulationStatusPersistent initSimulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
//				
//				System.out.println("initial status="+initSimulationStatus);
////				if(!initSimulationStatus.isCompleted() || !initSimulationStatus.getHasData()){
////					continue;
////				}
//
//				
//				BigString simXML = vcellConnection.getUserMetaDbServer().getSimulationXML(simJobSimRef);
//				Simulation sim = XmlHelper.XMLToSim(simXML.toString());
////				SolverDescription solverDescription = sim.getSolverTaskDescription().getSolverDescription();
////				if(solverDescription.equals(SolverDescription.StochGibson)  || solverDescription.equals(SolverDescription.FiniteVolume)){
////					//These 2 solvers give too much trouble so skip
////					System.out.println("--skipping solver");
//////					notCompletedSimIDs.add(simIDAndJobID.simID.toString());
////					return;
////				}
//				
//				
////				if(!sim.isSpatial()){
////					continue;
////				}
////				if(sim.getSolverTaskDescription().isSerialParameterScan()/* || sim.getSolverTaskDescription().getExpectedNumTimePoints() > 20*/){
////					continue;
////				}
//				
//				int scanCount = sim.getScanCount();
////				if(true){return;}
//				SimulationStatusPersistent simulationStatus = null;
//				SimulationInfo simulationInfo = sim.getSimulationInfo();
//				if(!simulationInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey().equals(vcSimulationIdentifier.getSimulationKey())){
//					throw new Exception("Unexpected authoritative and sim id are not the same");
//				}
//				vcellConnection.getSimulationController().startSimulation(vcSimulationIdentifier, scanCount);
//				long startTime = System.currentTimeMillis();
//				while(simulationStatus == null || simulationStatus.isStopped() || simulationStatus.isCompleted() || simulationStatus.isFailed()){
//					Thread.sleep(2000);
//					simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
//					if(simulationStatus.isFailed() && !initSimulationStatus.isFailed()){
//						break;
//					}
//					MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
//					if((System.currentTimeMillis()-startTime) > 30000){
//						throw new Exception("-----Sim finished too fast or took too long to start, status= "+simulationStatus);
//					}
//				System.out.println(simulationStatus);
//				}
//				SimulationStatusPersistent lastSimStatus = simulationStatus;
//				while(!simulationStatus.isStopped() && !simulationStatus.isCompleted() && !simulationStatus.isFailed()){
//					for(int i = 0;i<3;i++){
//						MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
//						Thread.sleep(1000);
//					}
//					simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
//					if(!simulationStatus.toString().equals(lastSimStatus.toString())){
//						lastSimStatus = simulationStatus;
//						System.out.println("running status="+simulationStatus);
//					}
////					MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
////					for (int i = 0; messageEvents != null && i < messageEvents.length; i++) {
////						System.out.println(messageEvents[i]);
////					}
//				}
//
//				if(!lastSimStatus.isCompleted()){
//					throw new Exception("Unexpected run status: "+lastSimStatus.toString());
//				}
//				
//				
//				String updatestr = "update missingdata set notes='"+DB_NOTES_SIM_RUNOK_CODE+"' where simjobsimref="+simJobSimRef;
//				System.out.println(updatestr);
//				updateStatement.executeUpdate(updatestr);
//				con.commit();
//			}catch(Exception e){
//				e.printStackTrace();
//				String errString = TokenMangler.fixTokenStrict(DB_NOTES_SIM_ERROR_CODE+e.getClass().getSimpleName()+" "+e.getMessage());
//				if(errString.length() > 256){
//					errString = errString.substring(0, 256);
//				}
//				String updatestr = "update missingdata set notes='"+errString+"' where simjobsimref="+simJobSimRef;
//				System.out.println(updatestr);
//				updateStatement.executeUpdate(updatestr);
//				con.commit();
//			}
//		}
//
//	}
	
	private static void /* Hashtable<KeyValue, Exception>*/ checkDataExists(Connection con,boolean bExistOnly) throws SQLException{
		AmplistorCredential amplistorCredential = AmplistorUtilsTest.getAmplistorCredential();
		
		Statement queryStatement = con.createStatement();
		Statement updateStatement = con.createStatement();
		
//		Hashtable<KeyValue, Exception> errorHash = new Hashtable<>();
		String sql =
			"select missingdata.*,parentsimref "+
			" from missingdata,vc_simulation,vc_userinfo"+
			" where vc_simulation.id = missingdata.simjobsimref and"+
			" vc_simulation.parentsimref is null and "+
			" vc_simulation.ownerref=vc_userinfo.id and "+
			" missingdata.notes is not null and " +
			" (missingdata.notes like '%exceeded_maximum%' " +
			" or missingdata.notes ='recheck dataexists' )" +
//			" vc_userinfo.userid='schaff' "+
			" order by missingdata.userid";
		ResultSet rset = queryStatement.executeQuery(sql);
		while(rset.next()){
//			if(!rset.getString("dataexists").equals("tbd")){
//				continue;
//			}
			KeyValue simJobSimRef = null;
			KeyValue parentsimref = null;
			User user = null;
			try{
				simJobSimRef = new KeyValue(rset.getString("simjobsimref"));
				parentsimref = (rset.getString("parentsimref")==null?null:new KeyValue(rset.getString("parentsimref")));
				user = new User(rset.getString("userid"),new KeyValue(rset.getString("userinfoid")));
				int jobIndex =  rset.getInt("jobindex");
				File primaryDataDir = new File("\\\\cfs02\\ifs\\raid\\vcell\\users\\"+user.getName());

				String updatestr = null;
				File filePathNamePrime = new File(primaryDataDir,SimulationData.createCanonicalSimLogFileName(simJobSimRef, 0, false));
				if(filePathNamePrime.exists()){
					updatestr = "fileNewPrime";
				}else if(new File(primaryDataDir,SimulationData.createCanonicalSimLogFileName(simJobSimRef, 0, true)).exists()){
					updatestr = "fileOldPrime";
				}else if(parentsimref != null && new File(primaryDataDir,SimulationData.createCanonicalSimLogFileName(parentsimref, 0, false)).exists()){
					updatestr = "fileNewParent";
				}else if(parentsimref != null && new File(primaryDataDir,SimulationData.createCanonicalSimLogFileName(parentsimref, 0, true)).exists()){
					updatestr = "fileOldParent";
				}else if(AmplistorUtils.bFileExists(new URL(AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+user.getName()+"/"+filePathNamePrime.getName()), amplistorCredential)){
					updatestr = "ampliNewPrime";
				}else if(AmplistorUtils.bFileExists(new URL(AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+user.getName()+"/"+SimulationData.createCanonicalSimLogFileName(simJobSimRef, 0, true)), amplistorCredential)){
					updatestr = "ampliOldPrime";
				}else if(parentsimref != null && AmplistorUtils.bFileExists(new URL(AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+user.getName()+"/"+SimulationData.createCanonicalSimLogFileName(parentsimref, 0, false)), amplistorCredential)){
					updatestr = "ampliNewParent";
				}else if(parentsimref != null && AmplistorUtils.bFileExists(new URL(AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+user.getName()+"/"+SimulationData.createCanonicalSimLogFileName(parentsimref, 0, true)), amplistorCredential)){
					updatestr = "ampliOldParent";
				}else{
					updatestr = "false";
				}
				if(bExistOnly || updatestr.equals("false")){
					updateStatement.executeUpdate("update missingdata set dataexists='"+updatestr+"' where simjobsimref="+simJobSimRef.toString());
					con.commit();
					continue;
				}
				
				// Log file exists, now check if the data can really be read
				VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simJobSimRef, user);
				VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier,jobIndex);
				//Try to read log,times and simdata to see if this data is well formed
				SimDataAmplistorInfo simDataAmplistorInfo = AmplistorUtils.getSimDataAmplistorInfoFromPropertyLoader();
				SimulationData simData = new SimulationData(vcSimulationDataIdentifier, primaryDataDir, null, simDataAmplistorInfo);
				double[] dataTimes = simData.getDataTimes();
				DataIdentifier[] dataIdentifiers = simData.getVarAndFunctionDataIdentifiers(null);
				DataIdentifier readDataIdentifier = null;
				for(DataIdentifier dataIdentifier:dataIdentifiers){
					if(!dataIdentifier.isFunction()){
						if(simData.getIsODEData()){
							ODEDataBlock odeDataBlock = simData.getODEDataBlock();
							odeDataBlock.getODESimData().getRow(dataTimes.length-1);
						}else{
							simData.getSimDataBlock(null, dataIdentifier.getName(), dataTimes[dataTimes.length-1]);
						}
						readDataIdentifier = dataIdentifier;
						break;
					}
				}
				System.out.println(
					BeanUtils.forceStringSize(
						"user= "+user.getName(), 20, " ", false)+
						" simref= "+BeanUtils.forceStringSize(simJobSimRef.toString(), 14, " ", false)+
						" numTimes= "+BeanUtils.forceStringSize(dataTimes.length+"",8, " ", true)+
						" readDataID= "+BeanUtils.forceStringSize(readDataIdentifier.getName()+"",20, " ", true));
				
				updatestr = "update missingdata set dataexists='readable' where simjobsimref="+simJobSimRef.toString();

//				String updatestr = "update missingdata set dataexists='true' where"+
//						" userinfoid="+user.getID().toString()+
//						" and simjobsimref="+simJobSimRef.toString()+
//						" and maxtaskid="+rset.getString("maxtaskid")+
//						" and jobindex="+jobIndex;
				
				
//				updateStatement.executeUpdate(updatestr);
//				con.commit();
			}catch(Exception e){
				if(simJobSimRef == null){
					e.printStackTrace();
//					throw new SQLException("Error querying",e);
				}else{
					String errString = e.getClass().getSimpleName()+" "+e.getMessage();
					if(errString.length() > 512){
						errString = errString.substring(0, 512);
					}
					String updatestr = "update missingdata set dataexists='error - "+TokenMangler.fixTokenStrict(errString)+"' where simjobsimref="+simJobSimRef.toString();
//					updateStatement.executeUpdate(updatestr);
//					con.commit();
					System.out.println(BeanUtils.forceStringSize("user= "+(user==null?"unavailable":user.getName()), 20, " ", false)+
							" simref= "+BeanUtils.forceStringSize(simJobSimRef.toString(), 14, " ", false)+
							" parentsimref= "+BeanUtils.forceStringSize((parentsimref==null?"NULL":parentsimref.toString()), 14, " ", false)+
							" failed= "+e.getMessage());
//					errorHash.put(simJobSimRef,e);
				}
			}
		}
		rset.close();
//		return errorHash;
	}
	
	
	private static HashMap<String, ArrayList<SimIDAndJobID>> createArrayList(ResultSet rset) throws Exception{
		int lastTaskid = -1;
		long lastSimID = -1;
		HashMap<String, ArrayList<SimIDAndJobID>> mapUserToSimJobs = new HashMap<String, ArrayList<SimIDAndJobID>>();
		while(rset.next()){
			int jobID = rset.getInt(2);
			if(rset.wasNull()){
				jobID = 0;
			}
			int dimension = rset.getInt(7);
			User user = new User(rset.getString(4), new KeyValue(rset.getBigDecimal(3)));
			ArrayList<SimIDAndJobID> simIDAndJobIDs = mapUserToSimJobs.get(user.getName());//;
			if(simIDAndJobIDs == null){
				simIDAndJobIDs = new ArrayList<SimIDAndJobID>();
				mapUserToSimJobs.put(user.getName(), simIDAndJobIDs);
			}
			String modelName = rset.getString("modelName");
			String appName = rset.getString("appname");
			String simName = rset.getString("simName");
			int schedulerStatus = rset.getInt("schedulerStatus");
			int taskid = rset.getInt("taskid");
			BigDecimal simID = rset.getBigDecimal(1);
			if(lastSimID != simID.longValue()){
				lastTaskid = -1;
			}
			String hasData = rset.getString("hasdata");
			Boolean bHasData = (hasData==null?null:hasData.toLowerCase().equals("y"));
			BigDecimal privacy = rset.getBigDecimal("privacy");
			String softwareVersion = rset.getString("softwareversion");
			Timestamp timestamp = rset.getTimestamp("versiondate");
			SimIDAndJobID simIDAndJobID = new SimIDAndJobID(new KeyValue(simID),jobID,user,dimension,modelName,appName,simName,SimulationJobStatus.SchedulerStatus.values()[schedulerStatus],bHasData,privacy,softwareVersion,timestamp);
			if(lastTaskid == taskid){
				simIDAndJobIDs.remove(simIDAndJobIDs.size()-1);
			}
			lastTaskid = taskid;
			simIDAndJobIDs.add(simIDAndJobID);
		}
		return mapUserToSimJobs;
	}
	
	private static void checkFiles2(HashMap<String, ArrayList<SimIDAndJobID>> mapUserToSimJobs) throws Exception{
		int notFoundTotalODE = 0;
		int notFoundTotalPDE = 0;
		for(String userid:mapUserToSimJobs.keySet()){
			System.out.println("scanning "+userid);
			int notFoundODECount = 0;
			int notFoundPDECount = 0;
			ArrayList<SimIDAndJobID> simIDs = mapUserToSimJobs.get(userid);
			FileWriter fw = null;
			File primaryDataDir = new File("\\\\cfs02\\ifs\\raid\\vcell\\users\\"+userid);
			File secondaryDataDir = new File("\\\\cfs01\\ifs\\raid\\vcell\\users\\"+userid);
			for (int i = 0; i < simIDs.size(); i++) {
				boolean bFound = false;
				for (int j = 0; j < 2; j++) {
					boolean bOldStyle = j!=0;
					int jobID = simIDs.get(i).jobID;
					if(jobID != 0 && bOldStyle){
						continue;
					}
					String logName = SimulationData.createCanonicalSimLogFileName(simIDs.get(i).simID,jobID,bOldStyle);
					for (int k = 0; k < 2; k++) {
						Thread.sleep(50);
						File logFile = new File((k==0?primaryDataDir:secondaryDataDir),logName);
						if(logFile.exists()){
							bFound = true;
							break;
						}
					}
					if(bFound){break;}
				}
				String statusDescr = bFound+","+simIDs.get(i).schedulerStatus+","+simIDs.get(i).bHasData+","+(simIDs.get(i).privacy.equals(GroupAccess.GROUPACCESS_ALL)?"public":(simIDs.get(i).privacy.equals(GroupAccess.GROUPACCESS_NONE)?"private":"custom"))+",\""+simIDs.get(i).softareVersion+"\""+",\""+simIDs.get(i).timestamp.toString()+"\"";
				if(bFound && !simIDs.get(i).schedulerStatus.equals(SimulationJobStatus.SchedulerStatus.COMPLETED)){
//					System.out.println("BAD STATUS "+statusDescr+","+simIDs.get(i).simID.toString()+","+simIDs.get(i).jobID+","+simIDs.get(i).user.getName()+","+simIDs.get(i).dimension+","+simIDs.get(i).modelName+","+simIDs.get(i).appName+","+simIDs.get(i).simName+","+simIDs.get(i).schedulerStatus);
				}else if(!bFound && (/*simIDs.get(i).schedulerStatus.equals(SimulationJobStatus.SchedulerStatus.COMPLETED) ||*/ (simIDs.get(i).bHasData != null && simIDs.get(i).bHasData))){
					notFoundODECount+= (simIDs.get(i).dimension==0?1:0);
					notFoundPDECount+= (simIDs.get(i).dimension==0?0:1);
					String badStatus =
							statusDescr+","+simIDs.get(i).simID.toString()+","+simIDs.get(i).jobID+",\""+simIDs.get(i).user.getName()+"\","+simIDs.get(i).dimension+",\""+simIDs.get(i).modelName+"\",\""+simIDs.get(i).appName+"\",\""+simIDs.get(i).simName+"\","+simIDs.get(i).schedulerStatus+"\r\n";
					System.out.print(badStatus);
					if(fw == null && false/*write output files*/){
						File missingSimFiles = new File("C:\\temp\\MissingFiles\\"+userid+"\\missingSimFiles.txt");
						if(!missingSimFiles.getParentFile().exists()){
							missingSimFiles.getParentFile().mkdir();
						}
						missingSimFiles.delete();
						fw = new FileWriter(missingSimFiles);
					}

					if(fw != null){fw.write(badStatus);}
					runSim(simIDs.get(i));
				}else{
//					System.out.println("Appropriate STATUS "+statusDescr+","+simIDs.get(i).simID.toString()+","+simIDs.get(i).jobID+","+simIDs.get(i).user.getName()+","+simIDs.get(i).dimension+","+simIDs.get(i).modelName+","+simIDs.get(i).appName+","+simIDs.get(i).simName+","+simIDs.get(i).schedulerStatus);					
				}
			}
			if(fw != null){fw.close();}
			
			notFoundTotalODE+= notFoundODECount;
			notFoundTotalPDE+= notFoundPDECount;
			System.out.println(BeanUtils.forceStringSize(userid, 15, " ", false)+
				BeanUtils.forceStringSize(notFoundODECount+"", 15, " ", true)+" "+BeanUtils.forceStringSize(notFoundPDECount+"", 15, " ", true)+
				BeanUtils.forceStringSize(notFoundTotalODE+"", 15, " ", true)+" "+BeanUtils.forceStringSize(notFoundTotalPDE+"", 15, " ", true)+
				BeanUtils.forceStringSize((notFoundTotalODE+notFoundTotalPDE)+"", 15, " ", true));
		}
	}

	
	@SuppressWarnings("serial")
	private static class VCellBootstrapTimeoutException extends Exception {}
	public static VCellBootstrap getVCellBootstrap(String rmiHostName,int rmiPort, String rmiBootstrapStubName,int retryCount,boolean bWaitForNull) throws Exception{
		String rmiUrl = "//" + rmiHostName + ":" +rmiPort + "/"+rmiBootstrapStubName;
		retryCount = (retryCount < 1?1:retryCount);
		for (int i = 0; i < retryCount; i++) {
			VCellBootstrap vcellBootstrap = null;
			try{
				vcellBootstrap = (VCellBootstrap)Naming.lookup(rmiUrl);
			}catch(Exception ce){
				if(bWaitForNull){
					return null;
				}
				System.out.println(rmiUrl+" VCellBootstrap not responding, trying "+(retryCount-1-i)+" more times...");
				//probably no rmiregistry started yet, keep trying
				//throw ce;
			}
			if(!bWaitForNull && vcellBootstrap != null){
				return vcellBootstrap;
			}else if(bWaitForNull && vcellBootstrap == null){
				return null;
			}
			try{
				Thread.sleep(10000);
			}catch(InterruptedException e){
				e.printStackTrace();
				//ignore
			}
		}
		throw new VCellBootstrapTimeoutException();
	}

	private static Hashtable<User, VCellConnection> userConnections = new Hashtable<User, VCellConnection>();
	private static TreeSet<String> notCompletedSimIDs = new TreeSet<String>();//used to skip over failed/skipped parameter scan groups
	private static TreeSet<String> completedSimIDs = new TreeSet<String>();
	private static void runSim(SimIDAndJobID simIDAndJobID) throws Exception{
//		DigestedPassword digestedPassword = new DigestedPassword("frmfrm");
//		ClientServerInfo clientServerInfo = ClientServerInfo.createRemoteServerInfo(new String[] {"rmi-alpha.cam.uchc.edu:40111"}, "frm",digestedPassword);
//		ClientServerManager clientServerManager = new ClientServerManager();
//		clientServerManager.setClientServerInfo(clientServerInfo);
//		clientServerManager.get
//		GeometryInfo[] geometryInfos = clientServerManager.getUserMetaDbServer().getGeometryInfos(false);
//		for (int i = 0; i < geometryInfos.length; i++) {
//			System.out.println(geometryInfos[i].getVersion().getName());
//		}
		
		if(notCompletedSimIDs.contains(simIDAndJobID.simID.toString())){
//			System.out.println("--skipping notCompleted");
			return;
		}else if(completedSimIDs.contains(simIDAndJobID.simID.toString())){
			System.out.println("-----unexpected sim rerun already completed once");
			return;
		}
		VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simIDAndJobID.simID, simIDAndJobID.user);
		UserLoginInfo userLoginInfo = new UserLoginInfo(simIDAndJobID.user.getName(), new DigestedPassword("xoxoxox"));
		//getVcellClient().getClientServerManager().getConnectionStatus()
		VCellConnection vcellConnection = userConnections.get(simIDAndJobID.user);
		try{
			if(vcellConnection != null){
				vcellConnection.getMessageEvents();
			}
		}catch(Exception e){
			e.printStackTrace();
			//assume disconnected
			vcellConnection = null;
		}
		if(vcellConnection == null){
			VCellBootstrap vCellBootstrap = getVCellBootstrap("rmi-alpha.cam.uchc.edu", 40106, "VCellBootstrapServer", 12, false);
			vcellConnection = vCellBootstrap.getVCellConnection(userLoginInfo);
			userConnections.put(simIDAndJobID.user,vcellConnection);
		}
		SimulationStatusPersistent finalSimStatus = null;
		try {
			SimulationStatusPersistent simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			System.out.println("initial status="+simulationStatus);

			BigString simXML = vcellConnection.getUserMetaDbServer().getSimulationXML(vcSimulationIdentifier.getSimulationKey());
			Simulation sim = XmlHelper.XMLToSim(simXML.toString());
			SolverDescription solverDescription = sim.getSolverTaskDescription().getSolverDescription();
			if(solverDescription.equals(SolverDescription.StochGibson)  || solverDescription.equals(SolverDescription.FiniteVolume)){
				//These 2 solvers give too much trouble so skip
				System.out.println("--skipping solver");
//				notCompletedSimIDs.add(simIDAndJobID.simID.toString());
				return;
			}
			int scanCount = sim.getScanCount();
			vcellConnection.getSimulationController().startSimulation(vcSimulationIdentifier, scanCount);
			long startTime = System.currentTimeMillis();
			while(simulationStatus.isStopped() || simulationStatus.isCompleted() || simulationStatus.isFailed()){
				Thread.sleep(250);
				simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
				MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
				if((System.currentTimeMillis()-startTime) > 60000){
					System.out.println("-----Sim finished too fast or took too long to start");
					return;
				}
//			System.out.println(simulationStatus);
			}
			SimulationStatusPersistent lastSimStatus = simulationStatus;
			while(!simulationStatus.isStopped() && !simulationStatus.isCompleted() && !simulationStatus.isFailed()){
				Thread.sleep(3000);
				simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
				if(!simulationStatus.toString().equals(lastSimStatus.toString())){
					lastSimStatus = simulationStatus;
					System.out.println("running status="+simulationStatus);
				}
	//			System.out.println(simulationStatus);
				MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
	//			for (int i = 0; messageEvents != null && i < messageEvents.length; i++) {
	//				System.out.println(messageEvents[i]);
	//			}
			}
			finalSimStatus = simulationStatus;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}finally{
			System.out.println("final status="+finalSimStatus+"\n");
			if(finalSimStatus == null || !finalSimStatus.isCompleted()){
				notCompletedSimIDs.add(simIDAndJobID.simID.toString());
			}else{
				completedSimIDs.add(simIDAndJobID.simID.toString());
			}
		}
	}
	
	private static void startClient(VCSimulationIdentifier vcSimulationIdentifier,UserLoginInfo userLoginInfo) throws Exception{
		ClientServerInfo clientServerInfo = ClientServerInfo.createRemoteServerInfo(new String[] {"rmi-alpha.cam.uchc.edu:40111"}, userLoginInfo.getUserName(), userLoginInfo.getDigestedPassword());
		VCellClient vCellClient = VCellClient.startClient(null, clientServerInfo);
		while(vCellClient.getClientServerManager() == null || vCellClient.getClientServerManager().getConnectionStatus() == null || vCellClient.getClientServerManager().getConnectionStatus().getStatus() != ConnectionStatus.CONNECTED){
			Thread.sleep(1000);
			System.out.println("trying connect");
		}
		BigString simXML = vCellClient.getClientServerManager().getUserMetaDbServer().getSimulationXML(vcSimulationIdentifier.getSimulationKey());
		Simulation sim = XmlHelper.XMLToSim(simXML.toString());
		vCellClient.getClientServerManager().getJobManager().startSimulation(vcSimulationIdentifier, sim.getScanCount());
		SimulationStatusPersistent simulationStatus = null;
		while(true){
			simulationStatus = vCellClient.getClientServerManager().getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			System.out.println(simulationStatus);
			if(simulationStatus.isCompleted() || simulationStatus.isFailed()){
				break;
			}
			Thread.sleep(1000);
//			MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
//			for (int i = 0; messageEvents != null && i < messageEvents.length; i++) {
//				System.out.println(messageEvents[i]);
//			}
		}
	}
}
