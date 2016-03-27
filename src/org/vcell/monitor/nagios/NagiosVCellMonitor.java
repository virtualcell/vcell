package org.vcell.monitor.nagios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.vcell.util.BigString;
import org.vcell.util.Executable;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.modeldb.VCInfoContainer;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.gui.SimulationStatusPersistent;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class NagiosVCellMonitor {

	public static final String VCELL_NAGIOS_USER = "vcellNagios";
	
	private static class UnexpectedTestStateException extends Exception {
		public UnexpectedTestStateException(String message){
			super(message);
		}
	}
	private static class WarningTestConditionException extends Exception{
		public WarningTestConditionException(String message){
			super(message);
		}
	}

	private static class MessyTestEnvironmentException extends Exception{
		public MessyTestEnvironmentException(String message){
			super(message);
		}
	}

	private static Exception argsException =
		new UnexpectedTestStateException("Usage: java -jar "+NagiosVCellMonitor.class.getSimpleName()+".jar -L checkLevel{"+
			VCELL_CHECK_LEVEL.RMI_ONLY_0.toString()+","+
			VCELL_CHECK_LEVEL.CONNECT_1.toString()+","+
			VCELL_CHECK_LEVEL.INFOS_2.toString()+","+
			VCELL_CHECK_LEVEL.LOAD_3.toString()+","+
			VCELL_CHECK_LEVEL.DATA_4.toString()+","+
			VCELL_CHECK_LEVEL.RUN_5.toString()+
			"} -H vcellRMIHost -i rmiPort -p vcellNagiosPassword -w warningTimeout -c criticalTimeout -m monitorSocket");
	
	public static enum NAGIOS_STATUS {OK,WARNING,CRITICAL,UNKNOWN};
	public static enum VCELL_CHECK_LEVEL {RMI_ONLY_0,CONNECT_1,INFOS_2,LOAD_3,DATA_4,RUN_5};
		
	private static class NagArgsHelper {
		public String host=null;
		public ArrayList<Integer> rmiPorts = new ArrayList<Integer>();
		public String vcellNagiosPassword=null;
		public VCELL_CHECK_LEVEL checkLevel=null;
		public int warningTimeout = 120; //seconds, default warn if test longer than 2 minutes
		public int criticalTimeout = 300; //seconds, default error if test longer than 5 minutes
		public int monitorSocket = -1; //within 1025-65535
//		public long lastSimRanTime = System.currentTimeMillis();
		public NagArgsHelper(String[] args) throws Exception{
			super();
			for(int i = 0;i < args.length;i++){
				if(args[i].equals("-H")){
					i++;
					this.host = args[i];
				}else if(args[i].equals("-i")){
					i++;
					String latestToken = null;
					try{
						StringTokenizer st  = new StringTokenizer(args[i], ":");
						latestToken = st.nextToken();
						this.rmiPorts.add(Integer.parseInt(latestToken));
						if(st.hasMoreTokens()){
							latestToken = st.nextToken();
							this.rmiPorts.add(Integer.parseInt(latestToken));
						}
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing rmiPort Integer "+latestToken);
					}
				}else if(args[i].equals("-p")){
					i++;
					this.vcellNagiosPassword = args[i];
				}else if(args[i].equals("-L")){
					i++;
					this.checkLevel = VCELL_CHECK_LEVEL.valueOf(args[i]);
				}else if(args[i].equals("-w")){
					i++;
					try{
						this.warningTimeout = Integer.parseInt(args[i]);
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing warningTimeout Integer "+args[i]);
					}
				}else if(args[i].equals("-c")){
					i++;
					try{
						this.criticalTimeout = Integer.parseInt(args[i]);
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing criticalTimeout Integer "+args[i]);
					}
				}else if(args[i].equals("-m")){
					i++;
					try{
						this.monitorSocket = Integer.parseInt(args[i]);
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing monitorSocket Integer "+args[i]);
					}

				}else{
					throw new UnexpectedTestStateException("Usage: java -jar "+NagiosVCellMonitor.class.getSimpleName()+".jar -L checkLevel{"+
						VCELL_CHECK_LEVEL.RMI_ONLY_0.toString()+","+
						VCELL_CHECK_LEVEL.CONNECT_1.toString()+","+
						VCELL_CHECK_LEVEL.INFOS_2.toString()+","+
						VCELL_CHECK_LEVEL.LOAD_3.toString()+","+
						VCELL_CHECK_LEVEL.DATA_4.toString()+","+
						VCELL_CHECK_LEVEL.RUN_5.toString()+
						"} -H vcellRMIHost -i rmiPorts -p vcellNagiosPassword -w warningTimeout -c criticalTimeout -m monitorSocket");
				}
			}
			if(host == null || rmiPorts.size() == 0 || vcellNagiosPassword == null || monitorSocket == -1){
				throw new IllegalArgumentException("Error - Host, rmiPort(s), dbPassword, monitorSocket must all be non null");
			}
		}

	}
	
	public static enum StatusType {statusConnect,statusFull,hold};

	public NagiosVCellMonitor(){
	}
	
	public static String getHoldCommand(){
		return StatusType.hold.name()+":";
	}
	public void startMonitor(NagArgsHelper nagArgsHelper){
		try{
			final int SLEEP_TIME = 1000*60;//millseconds time between each quick connect test
			final int SIM_RUN_WAIT = 2;// iterations of SLEEP_TIME to wait between simulation RUN tests
			statusMap.setStaleParameters(new Long(5*SLEEP_TIME), new Long(SIM_RUN_WAIT*2*SLEEP_TIME),new Long(1000*60*20));
			
			if(nagArgsHelper.monitorSocket <= 1024 || nagArgsHelper.monitorSocket >= 65536){
				throw new IllegalArgumentException("monitorSocket must be within 1025 and 65535,val="+nagArgsHelper.monitorSocket);
			}
			final ServerSocket serverSocket = claimSocket(nagArgsHelper.monitorSocket);

			// Request servicing thread handles nagios queries
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						nagiosRequestServicingLoop(serverSocket);
					}catch(Exception e){
						
					}
				}
			}).start();
			
			//Run Connection and Simulation tests thread
			new Thread(new Runnable() {
				@Override
				public void run() {
//					Long lastSimTestTime = System.currentTimeMillis();
					int simRunCount = 0;
					HashMap<Integer, CheckResults> lastResult;
					while(true){
						if(getVCellStatus(StatusType.hold) != null){
							//end this thread
							//deploy is happening, quit testing, nagios replies will continue in other thread
							break;
						}
						lastResult = null;
						StatusType statusType = (simRunCount == SIM_RUN_WAIT?StatusType.statusFull:StatusType.statusConnect);
						try{
							if(StatusType.statusFull.equals(statusType)){//full test
								simRunCount = 0;//reset sim run wait
								nagArgsHelper.checkLevel = VCELL_CHECK_LEVEL.RUN_5;
								lastResult = mainArgs(nagArgsHelper);								
							}else{//quick test
								nagArgsHelper.checkLevel = VCELL_CHECK_LEVEL.INFOS_2;
								lastResult = mainArgs(nagArgsHelper);
							}
							exceededTimeouts(nagArgsHelper, lastResult);
							setVCellStatus(new VCellStatus(nagArgsHelper.checkLevel+":"+nagArgsHelper.host,lastResult),statusType);
						}catch(Exception e){
							e.printStackTrace(System.out);
							setVCellStatus(new VCellStatus(nagArgsHelper.checkLevel+":"+nagArgsHelper.host+" "+e.getMessage(),null),statusType);
						}finally{
							simRunCount++;
							try{Thread.sleep(SLEEP_TIME);}catch(InterruptedException e2){e2.printStackTrace();}
						}
					}
				}
			}).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		try{
			NagArgsHelper nagArgsHelper = new NagArgsHelper(args);
			NagiosVCellMonitor nagiosVCellMonitor = new NagiosVCellMonitor();
			nagiosVCellMonitor.startMonitor(nagArgsHelper);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void exceededTimeouts(NagArgsHelper nagArgsHelper,HashMap<Integer, CheckResults> checkResultsHash) throws Exception{
		for(Integer rmiPort:checkResultsHash.keySet()){
			CheckResults checkResults = checkResultsHash.get(rmiPort);
			if(checkResults.exception != null){//only check normal results for timeouts
				continue;
			}
			if(nagArgsHelper.criticalTimeout != -1 &&
				(checkResults.totalTime/1000) > nagArgsHelper.criticalTimeout){
				
				checkResultsHash.put(rmiPort, new CheckResults(checkResults.vcellVersion, checkResults.levelTimesMillisec, checkResults.lastSimStatus, checkResults.totalTime, 
						new Exception(nagArgsHelper.checkLevel.toString()+" test exceeded criticalTimeout="+nagArgsHelper.criticalTimeout)));
			}else if(nagArgsHelper.warningTimeout != -1 &&
					(checkResults.totalTime/1000) > nagArgsHelper.warningTimeout){
				
				checkResultsHash.put(rmiPort, new CheckResults(checkResults.vcellVersion, checkResults.levelTimesMillisec, checkResults.lastSimStatus, checkResults.totalTime, 
						new WarningTestConditionException(nagArgsHelper.checkLevel.toString()+" test exceeded warningTimeout="+nagArgsHelper.warningTimeout)));
			}	
		}
	}
	private static class CheckResults {
		public String vcellVersion;
		public TreeMap<VCELL_CHECK_LEVEL, Long> levelTimesMillisec;
		public long totalTime;//milliseconds
		public SimulationStatusPersistent lastSimStatus;
		public Exception exception;
		public CheckResults(String vcellVersion, TreeMap<VCELL_CHECK_LEVEL, Long> levelTimes,
				SimulationStatusPersistent lastSimStatus,long totalTime,Exception exception) {
			super();
			this.vcellVersion = vcellVersion;
			this.levelTimesMillisec = levelTimes;
			this.lastSimStatus = lastSimStatus;
			this.totalTime = totalTime;
			this.exception = exception;
		}
	}
	public HashMap<Integer, CheckResults> mainArgs(NagArgsHelper nagArgsHelper) throws Exception{
		if(nagArgsHelper == null){
			throw new UnexpectedTestStateException("Main args for testing loop cannot be null");
		}
		//Test multiple ports if present
		HashMap<Integer, CheckResults> rmiPortResults = new HashMap<>();
		for(Integer rmiPort:nagArgsHelper.rmiPorts){
			if(rmiPort == -1){
				continue;
			}
			CheckResults result = checkVCell(nagArgsHelper.checkLevel,nagArgsHelper.host, rmiPort,"VCellBootstrapServer",
					nagArgsHelper.vcellNagiosPassword,nagArgsHelper.warningTimeout,nagArgsHelper.criticalTimeout);
			if(result == null){
				throw new UnexpectedTestStateException("test result not expected to be null");
			}
			rmiPortResults.put(rmiPort, result);
		}
		return rmiPortResults;
	}
	private CheckResults checkVCell(VCELL_CHECK_LEVEL checkLevel, String rmiHostName,int rmiPort, String rmiBootstrapStubName,String vcellNagiosPassword,int warningTimeout,int criticalTimeout) throws Exception{
		SimulationStatusPersistent lastSimStatus = null;
		String vcellVersion = null;
		TreeMap<VCELL_CHECK_LEVEL, Long> levelTimesMillisec = new TreeMap<NagiosVCellMonitor.VCELL_CHECK_LEVEL, Long>();
		long startTime = System.currentTimeMillis();
		try{
		if(rmiHostName == null || rmiPort == -1){
			throw new UnexpectedTestStateException("Host name/ip and rmiPort required for testing, rmihostname="+rmiHostName+" rmiport="+rmiPort);
		}
		String rmiUrl = "//" + rmiHostName + ":" +rmiPort + "/"+rmiBootstrapStubName;
		VCellBootstrap vcellBootstrap = null;
		try{
			vcellBootstrap = (VCellBootstrap)Naming.lookup(rmiUrl);
		}catch(Exception e){
			throw new UnexpectedTestStateException("Error during bootstrap lookup, "+e.getClass().getSimpleName()+" "+e.getMessage());
		}
		vcellVersion = vcellBootstrap.getVCellSoftwareVersion();
		levelTimesMillisec.put(VCELL_CHECK_LEVEL.RMI_ONLY_0, System.currentTimeMillis()-startTime);
		if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.CONNECT_1.ordinal()){
			if(vcellNagiosPassword == null){
				throw new UnexpectedTestStateException("vcellNagios Password required for "+VCELL_CHECK_LEVEL.CONNECT_1.toString()+" and above");
			}
			UserLoginInfo userLoginInfo = new UserLoginInfo(VCELL_NAGIOS_USER, new DigestedPassword(vcellNagiosPassword));
			VCellConnection vcellConnection = vcellBootstrap.getVCellConnection(userLoginInfo);
			levelTimesMillisec.put(VCELL_CHECK_LEVEL.CONNECT_1, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.RMI_ONLY_0));
			if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.INFOS_2.ordinal()){
				VCInfoContainer vcInfoContainer = vcellConnection.getUserMetaDbServer().getVCInfoContainer();
				levelTimesMillisec.put(VCELL_CHECK_LEVEL.INFOS_2, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.CONNECT_1));
				if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.LOAD_3.ordinal()){
					KeyValue bioModelKey = null;
					final String testModelName = "Solver Suite 5.1 (BETA only ode)";
					for(BioModelInfo bioModelInfo:vcInfoContainer.getBioModelInfos()){
						if(userLoginInfo.getUserName().equals(bioModelInfo.getVersion().getOwner().getName()) && bioModelInfo.getVersion().getName().equals(testModelName)){
							bioModelKey = bioModelInfo.getVersion().getVersionKey();
							break;
						}
					}
					BigString bioModelXML = vcellConnection.getUserMetaDbServer().getBioModelXML(bioModelKey);
					BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
					bioModel.refreshDependencies();
					levelTimesMillisec.put(VCELL_CHECK_LEVEL.LOAD_3, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.INFOS_2));
					if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.DATA_4.ordinal()){
						final String testSimContextName = "non-spatial ODE";
						SimulationContext simulationContext = bioModel.getSimulationContext(testSimContextName);
						final String testSimName = "Copy of combined ida/cvode";
						Simulation simulation = simulationContext.getSimulation(testSimName);
						if(simulation == null){
							throw new UnexpectedTestStateException("Couldn't find sim '"+testSimName+"' for "+checkLevel.toString());
						}
						VCSimulationDataIdentifier vcSimulationDataIdentifier =
							new VCSimulationDataIdentifier(simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), 0);
						ArrayList<AnnotatedFunction> outputFunctionsList = simulationContext.getOutputFunctionContext().getOutputFunctionsList();
						OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
						double[] times = vcellConnection.getDataSetController().getDataSetTimes(vcSimulationDataIdentifier);
						ODESimData odeSimData = vcellConnection.getDataSetController().getODEData(vcSimulationDataIdentifier);
						levelTimesMillisec.put(VCELL_CHECK_LEVEL.DATA_4, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.LOAD_3));
						if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.RUN_5.ordinal()){
							KeyValue copy1Key = null;
							KeyValue copy2Key = null;
							VCSimulationIdentifier testRunSimID = null;
							try{
								if(simulationContext.getSimulations().length != 1){
									throw new UnexpectedTestStateException("Expecting only 1 sim to be copied for "+checkLevel.toString());
								}
								SimulationStatusPersistent simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(simulation.getVersion().getVersionKey());
								if(!simulationStatus.isCompleted()){
									throw new UnexpectedTestStateException("Expecting completed sim to copy for "+checkLevel.toString());
								}
								
								String copyModelName = testModelName+"_"+"rmiport"+"_"+rmiPort;
								boolean bForceCleanup = true;
								while(true){
									boolean bMessy = false;
									for(BioModelInfo bioModelInfo:vcInfoContainer.getBioModelInfos()){
										if(userLoginInfo.getUserName().equals(bioModelInfo.getVersion().getOwner().getName()) && bioModelInfo.getVersion().getName().equals(copyModelName)){
											bMessy = true;
											if(bForceCleanup){
												bForceCleanup = false;
												try{vcellConnection.getUserMetaDbServer().deleteBioModel(bioModelInfo.getVersion().getVersionKey());}catch(Exception e){e.printStackTrace();}		
											}else{
												throw new MessyTestEnvironmentException("Messy test environment, not expecting "+copyModelName+" and couldn't cleanup");
											}
										}
									}
									if(!bMessy){
										break;
									}
								}
								BigString copyBioModelXMLStr = vcellConnection.getUserMetaDbServer().saveBioModelAs(bioModelXML, copyModelName, null);
								BioModel copyBioModel = XmlHelper.XMLToBioModel(new XMLSource(copyBioModelXMLStr.toString()));
								copy1Key = copyBioModel.getVersion().getVersionKey();
								copyBioModel.refreshDependencies();
								Simulation copySim = copyBioModel.getSimulationContext(testSimContextName).copySimulation(copyBioModel.getSimulationContext(testSimContextName).getSimulation(testSimName));
								final String copyTestSimName = "test";
								copySim.setName(copyTestSimName);
								copyBioModel.refreshDependencies();
								copyBioModelXMLStr = new BigString(XmlHelper.bioModelToXML(copyBioModel));
								copyBioModelXMLStr = vcellConnection.getUserMetaDbServer().saveBioModel(copyBioModelXMLStr, null);
								copyBioModel = XmlHelper.XMLToBioModel(new XMLSource(copyBioModelXMLStr.toString()));
								copy2Key = copyBioModel.getVersion().getVersionKey();
								copyBioModel.refreshDependencies();
								
								Simulation newSimulation = copyBioModel.getSimulationContext(testSimContextName).getSimulation(copyTestSimName);
								simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(newSimulation.getVersion().getVersionKey());
								if(simulationStatus != null && !simulationStatus.isNeverRan()){
									throw new UnexpectedTestStateException("Expecting new sim to have 'never ran' status for "+checkLevel.toString());
								}
								testRunSimID = new VCSimulationIdentifier(newSimulation.getVersion().getVersionKey(), copyBioModel.getVersion().getOwner());
								vcellConnection.getSimulationController().startSimulation(testRunSimID, 1);
								
								lastSimStatus = simulationStatus;
								MessageEvent[] messageEvents = null;
								while(simulationStatus == null || (!simulationStatus.isStopped() && !simulationStatus.isCompleted() && !simulationStatus.isFailed())){
									Thread.sleep(200);
									if(((System.currentTimeMillis()-startTime)/1000) > criticalTimeout){
										vcellConnection.getSimulationController().stopSimulation(testRunSimID);
										vcellConnection.getMessageEvents();
										break;
									}
									simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(newSimulation.getVersion().getVersionKey());
									if(simulationStatus !=null && !simulationStatus.toString().equals((lastSimStatus==null?null:lastSimStatus.toString()))){
										lastSimStatus = simulationStatus;
									}
									if(simulationStatus!=null && simulationStatus.isFailed()){
										throw new Exception("time "+((System.currentTimeMillis()-startTime)/1000)+", Sim execution failed key:"+testRunSimID.getSimulationKey()+" sim "+newSimulation.getName()+" model "+copyBioModel.getVersion().getName()+" messg "+simulationStatus.getFailedMessage());
									}
									messageEvents = vcellConnection.getMessageEvents();
								}
							}finally{
								try{if(copy1Key != null){vcellConnection.getUserMetaDbServer().deleteBioModel(copy1Key);}}catch(Exception e){e.printStackTrace();}
								try{if(copy2Key != null){vcellConnection.getUserMetaDbServer().deleteBioModel(copy2Key);}}catch(Exception e){e.printStackTrace();}
								if(testRunSimID != null){deleteSimData(testRunSimID);}
							}
							levelTimesMillisec.put(VCELL_CHECK_LEVEL.RUN_5, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.DATA_4));
						}
					}
				}
			}
		}
		return new CheckResults(vcellVersion, levelTimesMillisec,lastSimStatus,System.currentTimeMillis()-startTime,null);
		}catch(Exception e){
			return new CheckResults(vcellVersion, levelTimesMillisec, lastSimStatus, System.currentTimeMillis()-startTime, e);
		}
	}

	private boolean bPropertiesRead = false;
	private File userDataDir = null;
	private void deleteSimData(final VCSimulationIdentifier testRunSimID){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(!bPropertiesRead){
						bPropertiesRead = true;
						PropertyLoader.loadProperties();
						String dataDir = System.getProperty(PropertyLoader.primarySimDataDirProperty);
						if(dataDir == null){
							return;
						}
						userDataDir = new File(dataDir,VCELL_NAGIOS_USER);
					}
					if(userDataDir != null){
						String simID = Simulation.createSimulationID(testRunSimID.getSimulationKey());
						File[] deleteTheseFiles = userDataDir.listFiles(new FilenameFilter() {
							
							@Override
							public boolean accept(File dir, String name) {
								return name.startsWith(simID);
							}
						});
						if(deleteTheseFiles != null && deleteTheseFiles.length>0){
							for(File deleteFile:deleteTheseFiles){
								deleteFile.delete();
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", java.util.Locale.US);

	public static String formatDate(Date date){
		return dateTimeFormatter.format(date);
	}
	private static class VCellStatus {
		private String message;
		private HashMap<Integer, CheckResults> checkResultsMap;
		private Date statusCreateTime = new Date();
		private NAGIOS_STATUS staleStatus = null;
		public VCellStatus(String message,HashMap<Integer, CheckResults> checkResultsMap){
			if(message == null){
				throw new IllegalArgumentException("message cannot be null message="+message);
			}
			this.message = message;
			this.checkResultsMap = (checkResultsMap==null?new HashMap<Integer, CheckResults>():checkResultsMap);
		}
		public static VCellStatus createStaleStatus(String message){
			VCellStatus vcellStatus = new VCellStatus(message, new HashMap<Integer, CheckResults>());
			vcellStatus.staleStatus = NAGIOS_STATUS.UNKNOWN;
			return vcellStatus;
		}
		private NAGIOS_STATUS getNagiosStatus(){
			if(staleStatus != null){
				return staleStatus;
			}
			NAGIOS_STATUS worstStatus = NAGIOS_STATUS.OK;
			for(Integer rmiPort:checkResultsMap.keySet()){
				CheckResults checkResults = checkResultsMap.get(rmiPort);
				if(checkResults.exception != null){
					if(checkResults.exception instanceof UnexpectedTestStateException){
						if(worstStatus == NAGIOS_STATUS.OK || worstStatus == NAGIOS_STATUS.WARNING){
							worstStatus = NAGIOS_STATUS.UNKNOWN;
						}
					}else if(checkResults.exception instanceof WarningTestConditionException){
						if(worstStatus == NAGIOS_STATUS.OK){
							worstStatus = NAGIOS_STATUS.WARNING;
						}
					}else{
						worstStatus = NAGIOS_STATUS.CRITICAL;
					}
				}
			}
			return worstStatus;
		}
		private String getMessage(){
			StringBuffer sb = new StringBuffer();
			if(this instanceof VCellHoldStatus){
				return message;
			}
			sb.append(message+" ");
			for(Integer rmiPort:checkResultsMap.keySet()){//empty for stale status
				CheckResults checkResults = checkResultsMap.get(rmiPort);
				if(checkResults.exception != null){
					sb.append("rmi("+rmiPort+")"+checkResults.exception.getMessage()+" ");
				}else{
					sb.append("rmi("+rmiPort+")OK");
				}
			}
			// escape message characters for nagios
			for (int i = 0; i < sb.length(); i++) {
				if(sb.charAt(i) == '='){
					sb.replace(i, i+1, "(eql)");
				}else if(sb.charAt(i) == '\''){
					sb.replace(i, i+1, "(sqt)");
				}else if(sb.charAt(i) == '|'){
					sb.replace(i, i+1, "(pipe)");
				}
			}
			return sb.toString();
		}
		public String getNagiosReply(){
			StringBuffer performacneSB = new StringBuffer();
			// Add performance info for nagios
			for(Integer rmiPort:checkResultsMap.keySet()){//empty for stale results
				CheckResults checkResults = checkResultsMap.get(rmiPort);
				if(checkResults != null && checkResults.levelTimesMillisec != null){
					performacneSB.append(" totalTime(rmi"+rmiPort+")"+"="+(checkResults.totalTime)/1000+"s");
					for (VCELL_CHECK_LEVEL vcellCheckLevel:checkResults.levelTimesMillisec.keySet()) {
						performacneSB.append(" "+vcellCheckLevel.name()+"(rmi"+rmiPort+")"+"="+(checkResults.levelTimesMillisec.get(vcellCheckLevel))/1000+"s");
					}
				}
			}
			return getNagiosStatus().ordinal()+"("+formatDate(statusCreateTime)+")"+getMessage()+(performacneSB.length()==0?"":" |"+performacneSB.toString());
		}
		public long getStatusCreateTime(){
			return statusCreateTime.getTime();
		}
	}
	private static class VCellHoldStatus extends VCellStatus{
		public VCellHoldStatus(){
			super("Monitor placed on hold", null);
		}
	}
	private class StatusMap extends HashMap<StatusType, VCellStatus>{
		private Long staleStatusQuick;
		private Long staleStatusLong;
		private Long staleStatusHold;
		public StatusMap() {
			super();
			this.put(StatusType.statusConnect, new VCellStatus("Starting...",null));
			this.put(StatusType.statusFull, new VCellStatus("Starting...",null));
//			statusMap.put(MONITOR_HOLD_CMD, null);
		}
		public void setStaleParameters(long staleStatusQuick,long staleStatusLong,Long staleStatusHold){
			this.staleStatusQuick = staleStatusQuick;
			this.staleStatusLong = staleStatusLong;
			this.staleStatusHold = staleStatusHold;
		}
		private VCellStatus anyStale(StatusType statusType){
			if(this.get(StatusType.hold) != null){// 'hold' overrides all other status
				if(staleStatusHold != null && (System.currentTimeMillis()-this.get(StatusType.hold).getStatusCreateTime()) > staleStatusHold){
					return this.get(StatusType.hold);
				}
				return null;
			}else{
				if(staleStatusLong != null && StatusType.statusFull.equals(statusType) && staleStatusLong < (System.currentTimeMillis()-this.get(statusType).getStatusCreateTime())){
					return this.get(StatusType.statusFull);
				}else if(staleStatusQuick != null && StatusType.statusConnect.equals(statusType) && staleStatusQuick < (System.currentTimeMillis()-this.get(statusType).getStatusCreateTime())){
					return this.get(StatusType.statusConnect);
				}
				return null;
			}
		}
		public VCellStatus getStaleStatus(StatusType statusType){
			VCellStatus anyStale = anyStale(statusType);
			if(anyStale != null){
				return VCellStatus.createStaleStatus("Stale status after "+
					(((System.currentTimeMillis()-anyStale.getStatusCreateTime())/1000)/60)+" minutes: "+anyStale.getNagiosReply());				
			}
			return null;
		}
	}
	private StatusMap statusMap = new StatusMap();//default has no stale parameters defined
	private synchronized void setVCellStatus(VCellStatus status,StatusType statusType){
		statusMap.put(statusType, status);
	}
	private synchronized VCellStatus getVCellStatus(StatusType statusType){
		// 'stale' has higher priority than {hold,connect,full}
		// 'hold' has higher priority than {connect,full}
		// return the highest priority message
		VCellStatus staleStatus = statusMap.getStaleStatus(statusType);// any status can be stale
		if(staleStatus != null){
			return staleStatus;
		}
		VCellStatus holdStatus = statusMap.get(StatusType.hold);
		if(holdStatus != null){
			return holdStatus;
		}
		VCellStatus regularStatus = statusMap.get(statusType);
		return regularStatus;
	}
	
	public void nagiosRequestServicingLoop(ServerSocket serverSocket) throws IOException,NumberFormatException{
		while(true){
			//Wait for nagiosServer or another monitor to contact us
			Socket threadSocket = serverSocket.accept();
			PrintWriter out = null;
			try{
				out = new PrintWriter(threadSocket.getOutputStream(), true);
				// Read request
				BufferedReader in = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
				String request = in.readLine();
				if(request.startsWith(getHoldCommand())){
					setVCellStatus(new VCellHoldStatus(), StatusType.hold);
					//nagios never sends this request
					// Stop testing but keep responding with hold message
					out.println(getVCellStatus(StatusType.hold).getNagiosReply());
				}else if(request.equals(StatusType.statusConnect.name())){
					//nagios only sends this request
					// Request for VCell test status from nagios server
					out.println(getVCellStatus(StatusType.statusConnect).getNagiosReply());
				}else if(request.equals(StatusType.statusFull.name())){
					//nagios only sends this request
					// Request for VCell test status from nagios server
					out.println(getVCellStatus(StatusType.statusFull).getNagiosReply());
				}else{
					out.println(NAGIOS_STATUS.UNKNOWN.ordinal()+"Unknown Request '"+request);
				}
			}catch(Exception e){
				e.printStackTrace();
				if(out != null){
					out.println(NAGIOS_STATUS.UNKNOWN.ordinal()+"Request Error '"+e.getClass().getName()+" "+(e.getMessage()==null?"":e.getMessage()));
				}
			}finally{
				if(threadSocket != null){
					try{threadSocket.close();}catch(Exception e){e.printStackTrace();}
				}
			}
		}
	}
	
	public static ServerSocket claimSocket(int port) throws Exception{
		killMonitorProcess(port);
		return new ServerSocket(port);
//		ServerSocket myServersocket = null;
//	       for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//	           NetworkInterface intf = en.nextElement();
//	           for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//	               InetAddress inetAddress = enumIpAddr.nextElement();
//	               System.out.println("InetAddress="+inetAddress.toString()+" intf="+intf.toString());
//	               if (!inetAddress.isLoopbackAddress()) {
//	                   if (inetAddress instanceof Inet4Address && intf.getName().equals("eth0")) {
//	                	   System.out.println("socket="+inetAddress.toString());
//	                	   myServersocket = new ServerSocket(port,50,inetAddress); //((Inet4Address)inetAddress);
//	                   }
//	               }
//	           }
//	       }
//	       if(myServersocket != null){
//	    	   return myServersocket;
//	       }
//	       throw new Exception("InetAddress not found");
	}

	public static void killMonitorProcess(int port) throws Exception{
		//lsof -iTCP -sTCP:LISTEN -P -n
		Executable executable = new Executable(new String[] {"lsof","-i","TCP:"+port});
		try{
			executable.start();
		}catch(Exception e){
			//this can happen is there are no processes we have permission to list
			//assume the other monitor isn't running
			e.printStackTrace();
			return;
		}
		String s = executable.getStdoutString();
		System.out.println(s);
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		String line = null;
		while((line = br.readLine())!=null){
			StringTokenizer st = new StringTokenizer(line," ");
			String socketElement = null;
			String pid = null;
			if(st.nextToken().equals("java")){
				pid = st.nextToken().trim();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				socketElement = st.nextToken().trim();
			}
			String socket = null;
			if(socketElement != null){
				st = new StringTokenizer(socketElement,":");
				while(st.hasMoreElements()){
					socket = st.nextToken();
				}
			}
			if(socket != null){
				System.out.println("pid="+pid+" socket="+socket);
				//Find out if this is a monitor
				executable = new Executable(new String[] {"ps","-fww","-p",pid});
				executable.start();
				String pStr = executable.getStdoutString();
				System.out.println(pStr);
				if(pStr.contains("-Dtest.monitor.port="+port)){
					executable = new Executable(new String[] {"kill","-9",pid});
					executable.start();
				}
			}
		}
	}

}
