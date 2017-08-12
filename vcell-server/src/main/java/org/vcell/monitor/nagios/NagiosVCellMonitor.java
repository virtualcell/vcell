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

import org.jdom.Element;
import org.vcell.util.BigString;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.exe.Executable;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
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

	private static final Exception argsException =
		new UnexpectedTestStateException("Usage: java -jar "+NagiosVCellMonitor.class.getSimpleName()+".jar -H vcellRMIHost -i rmiPort -p vcellNagiosPassword -m monitorSocket -t paramString");
	
	public static enum NAGIOS_STATUS {OK,WARNING,CRITICAL,UNKNOWN};
	public static enum VCELL_CHECK_LEVEL {RMI_ONLY_0,CONNECT_1,INFOS_2,LOAD_3,DATA_4,RUN_5};
	//
	//NVM_PARAMS order of elements is important, don't change!
	//
	public static enum NVM_PARAMS {sleepTimeSec,simRunCnt,connectWarningTimeout,connectCriticalTimeout,fullWarningTimeout,fullCriticalTimeout,holdStaleTimeoutSeconds};
	public static class NagArgsHelper {
		public String host=null;
		public ArrayList<Integer> rmiPorts = new ArrayList<Integer>();
		public String vcellNagiosPassword=null;
		public VCELL_CHECK_LEVEL checkLevel=null;
		public HashMap<NVM_PARAMS, Integer> nvmParams = new HashMap<>();
		public int monitorSocket = -1; //within 1025-65535
//		public long lastSimRanTime = System.currentTimeMillis();
		public int calcConnectStaleTimeout(){
			return nvmParams.get(NVM_PARAMS.connectCriticalTimeout)+3*nvmParams.get(NVM_PARAMS.sleepTimeSec); //seconds, default error if connect status older than this
		}
		public int calcFullStaleTimeout(){
			return nvmParams.get(NVM_PARAMS.fullCriticalTimeout)+(nvmParams.get(NVM_PARAMS.simRunCnt)*nvmParams.get(NVM_PARAMS.sleepTimeSec))+(2*nvmParams.get(NVM_PARAMS.sleepTimeSec)); //seconds, default error if full status older than this
		}
		public NagArgsHelper(String[] args) throws Exception{
			super();
			//default param values if none supplied on command line
			tryUpdateParameters(createParamCmd(createParamHash(new int[] {60,2,120,300,180,300,20*60})));
			System.out.println(printableParamValues());

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
				}else if(args[i].equals("-m")){
					i++;
					try{
						this.monitorSocket = Integer.parseInt(args[i]);
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing monitorSocket Integer "+args[i]);
					}

				}else if(args[i].equals("-t")){
					i++;
					tryUpdateParameters(args[i]);
				}else{
					throw argsException;
				}
			}
			if(host == null || rmiPorts.size() == 0 || vcellNagiosPassword == null || monitorSocket == -1){
				throw new IllegalArgumentException("Error - Host, rmiPort(s), dbPassword, monitorSocket must all be non null\n"+argsException.getMessage());
			}
		}
		public static HashMap<NVM_PARAMS, Integer> createParamHash(Element element){
			HashMap<NVM_PARAMS, Integer> newParamHash = new HashMap<NVM_PARAMS, Integer>();
			for(NVM_PARAMS param:NVM_PARAMS.values()){
				newParamHash.put(param, Integer.parseInt(element.getAttribute(param.name()).getValue()));
			}
			return newParamHash;
		}
		public static HashMap<NVM_PARAMS, Integer> createParamHash(int[] valuesInNVMParamsOrder){
			HashMap<NVM_PARAMS, Integer> newParamHash = new HashMap<NVM_PARAMS, Integer>();
			for(NVM_PARAMS param:NVM_PARAMS.values()){
				newParamHash.put(param, valuesInNVMParamsOrder[param.ordinal()]);
			}
			return newParamHash;
		}
		public static String createParamCmd(HashMap<NVM_PARAMS, Integer> nvmParamValues){
			StringBuffer sb = new StringBuffer();
			sb.append(NagiosVCellMonitor.PARAM_CMD+":");
			for(NVM_PARAMS param:NVM_PARAMS.values()){
				sb.append((param.ordinal() != 0?",":"")+nvmParamValues.get(param));
			}
			return sb.toString();
		}
		public String printableParamValues(){
			StringBuffer sb = new StringBuffer();
			sb.append(createParamCmd(nvmParams)+"  ");
			for(NVM_PARAMS param:NVM_PARAMS.values()){
				sb.append((param.ordinal() != 0?",":"")+param.name()+"="+nvmParams.get(param));
			}
			sb.append(",connectStaleTimeout(calc)="+calcConnectStaleTimeout());
			sb.append(",fullStaleTimeout(calc)="+calcFullStaleTimeout());
			return sb.toString();
		}
		public String tryUpdateParameters(String paramStr) throws Exception{
			StringTokenizer st = new StringTokenizer(paramStr, ":,");
			st.nextToken();//paramcmd
			HashMap<NVM_PARAMS, Integer> tempParams = new HashMap<>();
			//check all params are present and  parsable
			for(NVM_PARAMS param:NVM_PARAMS.values()){
				tempParams.put(param, Integer.parseInt(st.nextToken()));
			}
			//all OK
			nvmParams.putAll(tempParams);
			return printableParamValues();
		}
		public String printUsage(){
			StringBuffer sb = new StringBuffer();
			sb.append(PARAM_CMD+"{:");
			for(NVM_PARAMS param:NVM_PARAMS.values()){
				sb.append((param.ordinal() != 0?",":"")+param.name());
			}
			sb.append("}");
			return sb.toString();
		}
	}
	
	public static enum StatusType {statusConnect,statusFull,hold};

	public NagiosVCellMonitor(){
	}
	
	public static String getHoldCommand(){
		return StatusType.hold.name()+":";
	}
	public static final VCELL_CHECK_LEVEL connectCheckLevel = VCELL_CHECK_LEVEL.INFOS_2;
	public static final VCELL_CHECK_LEVEL fullCheckLevel = VCELL_CHECK_LEVEL.RUN_5;
	public void startMonitor(NagArgsHelper newNagArgsHelper){
		try{
			nagArgsHelper = newNagArgsHelper;
//			final int SLEEP_TIME = 1000*60;//millseconds time between each quick connect test
//			final int SIM_RUN_WAIT = 2;// iterations of SLEEP_TIME to wait between simulation RUN tests
//			statusMap.setStaleParameters(new Long(5*SLEEP_TIME), new Long(SIM_RUN_WAIT*2*SLEEP_TIME),new Long(1000*60*20));
			
			if(nagArgsHelper.monitorSocket <= 1024 || nagArgsHelper.monitorSocket >= 65536){
				throw new IllegalArgumentException("monitorSocket must be within 1025 and 65535,val="+nagArgsHelper.monitorSocket);
			}
			final ServerSocket serverSocket = claimSocketLinuxLocal(nagArgsHelper.monitorSocket);

			// Request servicing thread handles nagios queries
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						nagiosRequestServicingLoop(serverSocket);
					}catch(Exception e){
						
					}
				}
			},"NagiosRequestLoop").start();
			
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
						StatusType statusType = (simRunCount == nagArgsHelper.nvmParams.get(NVM_PARAMS.simRunCnt)?StatusType.statusFull:StatusType.statusConnect);
						try{
							VCellStatus vcellStatus = null;
							if(statusType == StatusType.statusFull){//full test
								simRunCount = 0;//reset sim run wait
								nagArgsHelper.checkLevel = fullCheckLevel;
								lastResult = mainArgs(nagArgsHelper);
								vcellStatus = new VCellFullStatus(nagArgsHelper.checkLevel+":"+nagArgsHelper.host,lastResult);
							}else{//quick test
								nagArgsHelper.checkLevel = connectCheckLevel;
								lastResult = mainArgs(nagArgsHelper);
								vcellStatus = new VCellConnectStatus(nagArgsHelper.checkLevel+":"+nagArgsHelper.host,lastResult);
							}
							exceededTimeouts(nagArgsHelper, lastResult);
							setVCellStatus(vcellStatus);
						}catch(Exception e){
							e.printStackTrace(System.out);
							setVCellStatus(new VCellErrorStatus(nagArgsHelper.checkLevel+":"+nagArgsHelper.host+" "+e.getMessage(),statusType));
						}finally{
							simRunCount++;
							try{Thread.sleep(1000*nagArgsHelper.nvmParams.get(NVM_PARAMS.sleepTimeSec));}catch(InterruptedException e2){e2.printStackTrace();}
						}
					}
				}
			},"VCellTestLoop").start();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private NagArgsHelper nagArgsHelper;
	private Integer myProcessID;
	public static void main(String[] args) {
		try{
			NagiosVCellMonitor nagiosVCellMonitor = new NagiosVCellMonitor();
			nagiosVCellMonitor.startMonitor(new NagArgsHelper(args));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void exceededTimeouts(NagArgsHelper nagArgsHelper,HashMap<Integer, CheckResults> checkResultsHash) throws Exception{
		for(Integer rmiPort:checkResultsHash.keySet()){
			CheckResults checkResults = checkResultsHash.get(rmiPort);
			if(checkResults.exception != null){//only check normal results for timeouts
				continue;
			}
			long totalTimeSec = checkResults.totalTime/1000;
			if(nagArgsHelper.checkLevel == fullCheckLevel){
				if(	nagArgsHelper.nvmParams.get(NVM_PARAMS.fullCriticalTimeout) != -1 && totalTimeSec > nagArgsHelper.nvmParams.get(NVM_PARAMS.fullCriticalTimeout)){
					checkResultsHash.put(rmiPort, new CheckResults(checkResults.vcellVersion, checkResults.levelTimesMillisec, checkResults.lastSimStatus, checkResults.totalTime, 
							new Exception(nagArgsHelper.checkLevel.toString()+" test exceeded criticalTimeout="+nagArgsHelper.nvmParams.get(NVM_PARAMS.fullCriticalTimeout))));					
				}else if(nagArgsHelper.nvmParams.get(NVM_PARAMS.fullWarningTimeout) != -1 && totalTimeSec > nagArgsHelper.nvmParams.get(NVM_PARAMS.fullWarningTimeout)){
					checkResultsHash.put(rmiPort, new CheckResults(checkResults.vcellVersion, checkResults.levelTimesMillisec, checkResults.lastSimStatus, checkResults.totalTime, 
							new WarningTestConditionException(nagArgsHelper.checkLevel.toString()+" test exceeded warningTimeout="+nagArgsHelper.nvmParams.get(NVM_PARAMS.fullWarningTimeout))));										
				}	
			}else if(nagArgsHelper.checkLevel == connectCheckLevel){
				if(	nagArgsHelper.nvmParams.get(NVM_PARAMS.connectCriticalTimeout) != -1 && totalTimeSec > nagArgsHelper.nvmParams.get(NVM_PARAMS.connectCriticalTimeout)){
					checkResultsHash.put(rmiPort, new CheckResults(checkResults.vcellVersion, checkResults.levelTimesMillisec, checkResults.lastSimStatus, checkResults.totalTime, 
							new Exception(nagArgsHelper.checkLevel.toString()+" test exceeded criticalTimeout="+nagArgsHelper.nvmParams.get(NVM_PARAMS.connectCriticalTimeout))));					
				}else if(nagArgsHelper.nvmParams.get(NVM_PARAMS.connectWarningTimeout) != -1 && totalTimeSec > nagArgsHelper.nvmParams.get(NVM_PARAMS.connectWarningTimeout) ){
					checkResultsHash.put(rmiPort, new CheckResults(checkResults.vcellVersion, checkResults.levelTimesMillisec, checkResults.lastSimStatus, checkResults.totalTime, 
							new WarningTestConditionException(nagArgsHelper.checkLevel.toString()+" test exceeded warningTimeout="+nagArgsHelper.nvmParams.get(NVM_PARAMS.connectWarningTimeout) )));										
				}
					
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
		int checkedPortCount = 0;
		for(Integer rmiPort:nagArgsHelper.rmiPorts){
			if(rmiPort == -1){
				continue;
			}
			//Only do 1 sim run (choose first port) even if there are 2 ports (but always check connection)
			VCELL_CHECK_LEVEL adjustedCheckLevel = (checkedPortCount != 0?connectCheckLevel:nagArgsHelper.checkLevel);
			CheckResults result = checkVCell(adjustedCheckLevel,nagArgsHelper.host, rmiPort,"VCellBootstrapServer",
					nagArgsHelper.vcellNagiosPassword,
					(adjustedCheckLevel == connectCheckLevel?nagArgsHelper.nvmParams.get(NVM_PARAMS.connectCriticalTimeout) :nagArgsHelper.nvmParams.get(NVM_PARAMS.fullCriticalTimeout) ),
					nagArgsHelper.monitorSocket);
			if(result == null){
				throw new UnexpectedTestStateException("test result not expected to be null");
			}
			rmiPortResults.put(rmiPort, result);
			checkedPortCount++;
		}
		return rmiPortResults;
	}
	private CheckResults checkVCell(VCELL_CHECK_LEVEL checkLevel, String rmiHostName,int rmiPort, String rmiBootstrapStubName,String vcellNagiosPassword,int criticalTimeout,int monitorPort) throws Exception{
		SimulationStatusPersistent lastSimStatus = null;
		String vcellVersion = null;
		TreeMap<VCELL_CHECK_LEVEL, Long> levelTimesMillisec = new TreeMap<NagiosVCellMonitor.VCELL_CHECK_LEVEL, Long>();
		long startTime = System.currentTimeMillis();
		VCellConnection vcellConnection = null;
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
			vcellConnection = vcellBootstrap.getVCellConnection(userLoginInfo);
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
								
								String copyModelName = testModelName+"_"+rmiHostName+"_rmi"+rmiPort+"_siteprt"+monitorPort;
								boolean bForceCleanup = true;
								while(true){
									boolean bMessy = false;
									for(BioModelInfo bioModelInfo:vcInfoContainer.getBioModelInfos()){
										if(userLoginInfo.getUserName().equals(bioModelInfo.getVersion().getOwner().getName()) && bioModelInfo.getVersion().getName().equals(copyModelName)){
											bMessy = true;
											if(bForceCleanup){
												try{vcellConnection.getUserMetaDbServer().deleteBioModel(bioModelInfo.getVersion().getVersionKey());}catch(Exception e){e.printStackTrace();}
											}else{
												throw new MessyTestEnvironmentException("Messy test environment, not expecting "+copyModelName+" and couldn't cleanup");
											}
										}
									}
									if(!bMessy){
										break;
									}
									//get new vcInfoContainer without cleaned-up model
									vcInfoContainer = vcellConnection.getUserMetaDbServer().getVCInfoContainer();
									bForceCleanup = false;
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
		}finally{
			vcellConnection = null;
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
	public abstract static class VCellStatus {
		private String message;
		private HashMap<Integer, CheckResults> checkResultsMap;
		private Date statusCreateTime = new Date();
		public abstract StatusType getStatusType();

		private VCellStatus(String message,HashMap<Integer, CheckResults> checkResultsMap){
			if(message == null){
				throw new IllegalArgumentException("message cannot be null");
			}
			this.message = message;
			this.checkResultsMap = (checkResultsMap==null?new HashMap<Integer, CheckResults>():checkResultsMap);
			System.out.println(getNagiosReply());
		}

		private NAGIOS_STATUS getNagiosStatus(){
			if(this instanceof VCellStaleStatus){
				return NAGIOS_STATUS.UNKNOWN;
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
		public VCellHoldStatus(String message){
			super("Monitor on hold"+(message==null?"":", "+message), null);
		}
		@Override
		public StatusType getStatusType() {
			return StatusType.hold;
		}
	}
	private static class VCellConnectStatus extends VCellStatus{
		public VCellConnectStatus(String message,HashMap<Integer, CheckResults> checkResultsMap){
			super(message, checkResultsMap);
		}
		@Override
		public StatusType getStatusType() {
			return StatusType.statusConnect;
		}
	}
	private static class VCellFullStatus extends VCellStatus{
		public VCellFullStatus(String message,HashMap<Integer, CheckResults> checkResultsMap){
			super(message, checkResultsMap);
		}
		@Override
		public StatusType getStatusType() {
			return StatusType.statusFull;
		}
	}
	private static class VCellErrorStatus extends VCellStatus{
		private StatusType kindOfCheckWhenErrorHappened;
		public VCellErrorStatus(String message,StatusType kindOfCheckWhenErrorHappened){
			super(message, null);
			this.kindOfCheckWhenErrorHappened = kindOfCheckWhenErrorHappened;
		}
		@Override
		public StatusType getStatusType() {
			return kindOfCheckWhenErrorHappened;
		}
	}
	private static class VCellStaleStatus extends VCellStatus{
		//Always Generated, not stored in StatusMap
		public VCellStaleStatus(String message){
			super(message, null);
		}
		@Override
		public StatusType getStatusType() {
			return null;
		}
	}
	public static class StatusMap extends HashMap<StatusType, VCellStatus>{
		private static final int MILLISEC = 1000;
		public StatusMap() {
			super();
			this.put(StatusType.statusConnect, new VCellConnectStatus("Starting...",null));
			this.put(StatusType.statusFull, new VCellFullStatus("Starting...",null));
		}
		private VCellStatus anyStale(StatusType statusType,NagArgsHelper nagArgsHelper){
			if(this.get(StatusType.hold) != null){// 'hold' overrides all other status
				Integer staleStatusHoldSec = nagArgsHelper.nvmParams.get(NVM_PARAMS.holdStaleTimeoutSeconds);
				if(staleStatusHoldSec != null && (System.currentTimeMillis()-this.get(StatusType.hold).getStatusCreateTime()) > (staleStatusHoldSec*MILLISEC)){
					return this.get(StatusType.hold);
				}
				return null;
			}else{
				Integer staleStatusFull = nagArgsHelper.calcFullStaleTimeout();
				Integer staleStatusConnect = nagArgsHelper.calcConnectStaleTimeout();
				if(staleStatusFull != null && StatusType.statusFull.equals(statusType) && (staleStatusFull*MILLISEC) < (System.currentTimeMillis()-this.get(statusType).getStatusCreateTime())){
					return this.get(StatusType.statusFull);
				}else if(staleStatusConnect != null && StatusType.statusConnect.equals(statusType) && (staleStatusConnect*MILLISEC) < (System.currentTimeMillis()-this.get(statusType).getStatusCreateTime())){
					return this.get(StatusType.statusConnect);
				}
				return null;
			}
		}
		public VCellStatus getStaleStatus(StatusType statusType,NagArgsHelper nagArgsHelper){
			VCellStatus anyStale = anyStale(statusType,nagArgsHelper);
			if(anyStale != null){
				return new VCellStaleStatus("Stale status after "+
					(((System.currentTimeMillis()-anyStale.getStatusCreateTime())/MILLISEC)/60)+" minutes, use command (jstack NagiosVCellMonitorProcessID) to debug.  lastStatus="+anyStale.getNagiosReply());		
			}
			return null;
		}
	}
	private StatusMap statusMap = new StatusMap();//default has no stale parameters defined
	private synchronized void setVCellStatus(VCellStatus status){
		if(status.getStatusType() == null){
			throw new IllegalArgumentException("Not allowed to set "+status.getClass().getName()+" explicitly, it will be generated based on other status.");
		}
		statusMap.put(status.getStatusType(), status);
	}
	private synchronized VCellStatus getVCellStatus(StatusType statusType){
		// 'stale' has higher priority than {hold,connect,full}
		// 'hold' has higher priority than {connect,full}
		// return the highest priority message
		VCellStatus staleStatus = statusMap.getStaleStatus(statusType,nagArgsHelper);// any status can be stale
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
	
	private static final String PARAM_CMD = "params";
	private String processParamCmd(String request) throws Exception{
		if(request.equals(PARAM_CMD)){
			return nagArgsHelper.printableParamValues();
		}else if(request.startsWith(PARAM_CMD+":")){
			try{
				return nagArgsHelper.tryUpdateParameters(request);
			}catch(Exception e){
				return "Error parsing '"+request+"' err="+e.getMessage()+"  usage="+nagArgsHelper.printUsage();
			}
		}
		return "Error parsing paramCmd '"+request+"' usage="+nagArgsHelper.printUsage();
	}
	private void processHoldCmd(String request) throws Exception{
		StringTokenizer st = new StringTokenizer(request, ":");
		st.nextToken();
		String params = st.nextToken();
		if(!Character.isDigit(params.charAt(0))){
			throw new Exception("Expecting 'hold' command format, hold:staleSeconds(0 never stale) message(e.g.deploy)");
		}
		st = new StringTokenizer(params, " ");
		nagArgsHelper.nvmParams.put(NVM_PARAMS.holdStaleTimeoutSeconds, Integer.parseInt(st.nextToken()));
		setVCellStatus(new VCellHoldStatus(st.nextToken()));
	}
	public void nagiosRequestServicingLoop(ServerSocket serverSocket) throws IOException,NumberFormatException{
		while(true){
			//Wait for nagiosServer or another monitor to contact us
			Socket threadSocket = serverSocket.accept();
			PrintWriter socketWriter = null;
			try{
				socketWriter = new PrintWriter(threadSocket.getOutputStream(), true);
				// Read request
				BufferedReader socketReader = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
				String request = socketReader.readLine();
				if(request.startsWith(PARAM_CMD)){
					//nagios never sends this request
					socketWriter.println(processParamCmd(request));
				}else if(request.startsWith(getHoldCommand())){
					processHoldCmd(request);
					//nagios never sends this request
					// Stop testing but keep responding with hold message
					socketWriter.println(getVCellStatus(StatusType.hold).getNagiosReply());
				}else if(request.equals(StatusType.statusConnect.name())){
					//nagios only sends this request
					// Request for VCell test status from nagios server
					socketWriter.println(getVCellStatus(StatusType.statusConnect).getNagiosReply());
				}else if(request.equals(StatusType.statusFull.name())){
					//nagios only sends this request
					// Request for VCell test status from nagios server
					socketWriter.println(getVCellStatus(StatusType.statusFull).getNagiosReply());
				}else{
					socketWriter.println(NAGIOS_STATUS.UNKNOWN.ordinal()+"Unknown Request '"+request);
				}
			}catch(Exception e){
				e.printStackTrace();
				if(socketWriter != null){
					socketWriter.println(NAGIOS_STATUS.UNKNOWN.ordinal()+"Request Error '"+e.getClass().getName()+" "+(e.getMessage()==null?"":e.getMessage()));
				}
			}finally{
				if(threadSocket != null){
					try{threadSocket.close();}catch(Exception e){e.printStackTrace();}
				}
			}
		}
	}
	
	public ServerSocket claimSocketLinuxLocal(int port) throws Exception{
		int numRetries = 0;
		while(numRetries < 10){
			try{
				killMonitorProcessLinuxLocal(port);
				ServerSocket newServerSocket =  new ServerSocket(port);
				Thread.sleep(2000);
				myProcessID = getPidOnPortLinuxLocal(port);
				System.out.println("Monitor process ID pid="+myProcessID);
				return newServerSocket;
			}catch(Exception e){
				numRetries+= 1;
				System.out.println("claimSocket failed, "+e.getMessage());
				e.printStackTrace();
				System.out.flush();
				try{Thread.sleep(60000);}catch(InterruptedException ie){e.printStackTrace();}
			}
		}
		System.out.println("claimSocket failed, too many retries");
		System.out.flush();
		throw new Exception("claimSocket failed, too many retries");
		
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

	public static String[] createLSOFPort_LinuxCmdParams(int tcpPort){
		return new String[] {"/usr/sbin/lsof","-P","-i","TCP:"+tcpPort};
	}
	public static String[] createChekcMonitorProcess_LinuxCmdParams(Integer linuxProcessID){
		return new String[] {"/bin/ps","-fww","-p",linuxProcessID.toString()};
	}
	public static String[] createKillProcess_LinuxCmdParams(Integer linuxProcessID){
		return new String[] {"/bin/kill","-9",linuxProcessID.toString()};
	}
	public static Integer getPidOnPortLinuxLocal(int port) throws Exception{
		//lsof -iTCP -sTCP:LISTEN -P -n
		Executable executable = new Executable(createLSOFPort_LinuxCmdParams(port));
		try{
			executable.start();
		}catch(Exception e){
			//this can happen is there are no processes we have permission to list
			//assume the other monitor isn't running
			e.printStackTrace();
			return null;
		}
		String s = executable.getStdoutString();
		return parseLSOF_ReturnPID(s,port);
	}
	
	public static Integer parseLSOF_ReturnPID(String s,int port) throws Exception{
		System.out.println(s);
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		String line = null;
		Integer pid = null;
		while((line = br.readLine())!=null){
			System.out.println(line);
			StringTokenizer st = new StringTokenizer(line," ");
			String socketElement = null;
			if(st.nextToken().equals("java")){
				pid = new Integer(st.nextToken().trim());
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				socketElement = st.nextToken().trim();
			}
			if(socketElement != null){
				st = new StringTokenizer(socketElement,":");
				String currentPort = null;
				// Get port from last element
				while(st.hasMoreElements()){
					currentPort = st.nextToken();
				}
				if(currentPort.equals(""+port)){
					return pid;
				}
			}
		}
		throw new Exception("Error parsing pid/port information, pid="+pid+" port="+port);
	}
	public static String createJvmPortProperty(int port){
		return "-D"+PropertyLoader.nagiosMonitorPort+"="+port;
	}
	public static void killMonitorProcessLinuxLocal(int port) throws Exception{
		Integer linuxProcessID = getPidOnPortLinuxLocal(port);
		if(linuxProcessID != null){
			System.out.println("pid="+linuxProcessID+" port="+port);
			//Find out if this is a monitor
			Executable executable = new Executable(createChekcMonitorProcess_LinuxCmdParams(linuxProcessID));
			executable.start();
			String pStr = executable.getStdoutString();
			System.out.println(pStr);
			if(pStr.contains(createJvmPortProperty(port))){
				executable = new Executable(createKillProcess_LinuxCmdParams(linuxProcessID));
				executable.start();
			}
		}
	}

}
