package org.vcell.monitor.nagios;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingFormatArgumentException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.axis.transport.http.SocketInputStream;
import org.vcell.util.BigString;
import org.vcell.util.Executable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.modeldb.VCInfoContainer;
import cbit.vcell.server.SimulationStatus;
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
	public static void main(String[] args) {
		try{
			final NagArgsHelper nagArgsHelper = new NagArgsHelper(args);
			
			if(nagArgsHelper.monitorSocket <= 1024 || nagArgsHelper.monitorSocket >= 65536){
				throw new IllegalArgumentException("monitorSocket must be within 1025 and 65535,val="+nagArgsHelper.monitorSocket);
			}
			final ServerSocket serverSocket = claimSocket(nagArgsHelper.monitorSocket);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						nagiosRequestServicingLoop(serverSocket);
					}catch(Exception e){
						
					}
				}
			}).start();
			
			//Run Connection and Simulation tests
			new Thread(new Runnable() {
				@Override
				public void run() {
//					Long lastSimTestTime = System.currentTimeMillis();
					final int SLEEP_TIME = 60000;//millseconds time between each quick test
					final int SIM_RUN_WAIT = 1;// iterations of SLEEP_TIME to wait between simulation RUN tests
					final int MAX_MESSY = 1;// iterations of SLEEP_TIME to wait before forcing cleanup
					int simRunCount = 0;
					int messyCount = 0;
//					VCELL_CHECK_LEVEL currentVCellCheckLevel = null;
					HashMap<Integer, CheckResults> lastResult;
					while(true){
						if(getVCellStatus(true) instanceof VCellHoldStatus){
							//end this thread
							//deploy is happening, quit testing, nagios replies will continue in other thread
							break;
						}
//						if(getVCellStatus().getMonitormoMode().equals(MonitorMode.kill)){
//							//This monitor is being shut down
//							break;
//						}
//						if(getVCellStatus().getMonitormoMode().equals(MonitorMode.deploy)){
//							//waiting for command to start executing tests (turn off deploy mode)
//							try{Thread.sleep(1000);}catch(InterruptedException e){e.printStackTrace();}
//							continue;
//						}
						lastResult = null;
						boolean bFullCheck = (simRunCount == SIM_RUN_WAIT);
						try{
							if(bFullCheck){//full test
								simRunCount = 0;//reset sim run wait
								nagArgsHelper.checkLevel = VCELL_CHECK_LEVEL.RUN_5;
								lastResult = mainArgs(nagArgsHelper,messyCount>MAX_MESSY);								
//								exceededTimeouts(nagArgsHelper, lastResult);
//								setVCellStatus(new VCellNagioStatus(NAGIOS_STATUS.OK, nagArgsHelper.checkLevel,
//										"OK "+nagArgsHelper.host+" "+nagArgsHelper.rmiPorts.get(0)+(nagArgsHelper.rmiPorts.size()>1?":"+nagArgsHelper.rmiPorts.get(0):""),
//										null,lastResult),true);
							}else{//quick test
								nagArgsHelper.checkLevel = VCELL_CHECK_LEVEL.INFOS_2;
								lastResult = mainArgs(nagArgsHelper, false);
//								exceededTimeouts(nagArgsHelper, lastResult);
//								setVCellStatus(new VCellNagioStatus(NAGIOS_STATUS.OK, nagArgsHelper.checkLevel,
//										"OK "+nagArgsHelper.host+" "+nagArgsHelper.rmiPorts.get(0)+(nagArgsHelper.rmiPorts.size()>1?":"+nagArgsHelper.rmiPorts.get(0):""),
//										null,lastResult),false);
							}
							messyCount = 0;
							exceededTimeouts(nagArgsHelper, lastResult);
							setVCellStatus(new VCellNagioStatus(nagArgsHelper.checkLevel,nagArgsHelper.host,lastResult),bFullCheck);
						}catch(MessyTestEnvironmentException e){
							messyCount++;
							e.printStackTrace();
						}catch(Exception e){
							messyCount = 0;
							e.printStackTrace(System.out);
//							if(e instanceof UnexpectedTestStateException){
//								setVCellStatus(new VCellNagioStatus(NAGIOS_STATUS.UNKNOWN, nagArgsHelper.checkLevel, e.getMessage(), e,null),bFullCheck);
//							}else if(e instanceof WarningTestConditionException){
//								setVCellStatus(new VCellNagioStatus(NAGIOS_STATUS.WARNING, nagArgsHelper.checkLevel, e.getMessage(), e,null),bFullCheck);
//							}else{
								setVCellStatus(new VCellNagioStatus(nagArgsHelper.checkLevel,nagArgsHelper.host+" "+e.getMessage(),null),bFullCheck);
//							}
						}finally{
							simRunCount++;
							try{Thread.sleep(SLEEP_TIME);}catch(InterruptedException e2){e2.printStackTrace();}
						}
					}
				}
			}).start();
			
//			System.exit(mainArgs(args).ordinal());
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
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
				
//				throw new Exception(nagArgsHelper.checkLevel.toString()+" test exceeded criticalTimeout="+nagArgsHelper.criticalTimeout+
//					(checkResults.lastSimStatus==null?"":" seconds lastSimStatus="+checkResults.lastSimStatus));
			}else if(nagArgsHelper.warningTimeout != -1 &&
					(checkResults.totalTime/1000) > nagArgsHelper.warningTimeout){
				
				checkResultsHash.put(rmiPort, new CheckResults(checkResults.vcellVersion, checkResults.levelTimesMillisec, checkResults.lastSimStatus, checkResults.totalTime, 
						new WarningTestConditionException(nagArgsHelper.checkLevel.toString()+" test exceeded warningTimeout="+nagArgsHelper.warningTimeout)));
				
//				throw new WarningTestConditionException(nagArgsHelper.checkLevel.toString()+" test exceeded warningTimeout="+nagArgsHelper.warningTimeout+
//						(checkResults.lastSimStatus==null?"":" seconds lastSimStatus="+checkResults.lastSimStatus));
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
	public static HashMap<Integer, CheckResults> mainArgs(NagArgsHelper nagArgsHelper,boolean bForceCleanup) throws Exception{
		if(nagArgsHelper == null){
			throw new UnexpectedTestStateException("Main args for testing loop cannot be null");
		}
//		PrintStream sysout = System.out;
//		PrintStream syserr = System.err;
//		try{
//			ByteArrayOutputStream baos_out = new ByteArrayOutputStream();
//			ByteArrayOutputStream baos_err = new ByteArrayOutputStream();
//			System.setOut(new PrintStream(baos_out));
//			System.setErr(new PrintStream(baos_err));

//			String host=null;
//			ArrayList<Integer> rmiPorts = new ArrayList<Integer>();
//			String password=null;
//			VCELL_CHECK_LEVEL checkLevel=null;
//			int warningTimeout = -1; //seconds
//			int criticalTimeout = -1; //seconds
//			for(int i = 0;i < args.length;i++){
//				if(args[i].equals("-H")){
//					i++;
//					host = args[i];
//				}else if(args[i].equals("-i")){
//					i++;
//					String latestToken = null;
//					try{
//						StringTokenizer st  = new StringTokenizer(args[i], ":");
//						latestToken = st.nextToken();
//						rmiPorts.add(Integer.parseInt(latestToken));
//						if(st.hasMoreTokens()){
//							latestToken = st.nextToken();
//							rmiPorts.add(Integer.parseInt(latestToken));
//						}
//					}catch(NumberFormatException e){
//						throw new UnexpectedTestStateException("Error parsing rmiPort Integer "+latestToken);
//					}
//				}else if(args[i].equals("-p")){
//					i++;
//					password = args[i];
//				}else if(args[i].equals("-L")){
//					i++;
//					checkLevel = VCELL_CHECK_LEVEL.valueOf(args[i]);
//				}else if(args[i].equals("-w")){
//					i++;
//					try{
//						warningTimeout = Integer.parseInt(args[i]);
//					}catch(NumberFormatException e){
//						throw new UnexpectedTestStateException("Error parsing warningTimeout Integer "+args[i]);
//					}
//				}else if(args[i].equals("-c")){
//					i++;
//					try{
//						criticalTimeout = Integer.parseInt(args[i]);
//					}catch(NumberFormatException e){
//						throw new UnexpectedTestStateException("Error parsing criticalTimeout Integer "+args[i]);
//					}
//				}else{
//					throw new UnexpectedTestStateException("Usage: java -jar "+NagiosVCellCheck.class.getSimpleName()+".jar -L checkLevel{"+
//						VCELL_CHECK_LEVEL.RMI_ONLY_0.toString()+","+
//						VCELL_CHECK_LEVEL.CONNECT_1.toString()+","+
//						VCELL_CHECK_LEVEL.INFOS_2.toString()+","+
//						VCELL_CHECK_LEVEL.LOAD_3.toString()+","+
//						VCELL_CHECK_LEVEL.DATA_4.toString()+","+
//						VCELL_CHECK_LEVEL.RUN_5.toString()+
//						"} -H vcellRMIHost -i rmiPort -p vcellNagiosPassword -w warningTimeout -c criticalTimeout -m monitorSocket");
//				}
//			}
			//Test multiple ports if present, both return the same result
			HashMap<Integer, CheckResults> rmiPortResults = new HashMap<>();
			for(Integer rmiPort:nagArgsHelper.rmiPorts){
				if(rmiPort == -1){
					continue;
				}
				CheckResults result = checkVCell(nagArgsHelper.checkLevel,bForceCleanup,nagArgsHelper.host, rmiPort,"VCellBootstrapServer",
						nagArgsHelper.vcellNagiosPassword,nagArgsHelper.warningTimeout,nagArgsHelper.criticalTimeout);
				if(result == null){
					throw new UnexpectedTestStateException("test result not expected to be null");
				}
				rmiPortResults.put(rmiPort, result);
			}
			return rmiPortResults;
			
//		}finally{
//			System.setOut(sysout);
//			System.setErr(syserr);
//		}
	}
	private static CheckResults checkVCell(VCELL_CHECK_LEVEL checkLevel, boolean bForceCleanup,String rmiHostName,int rmiPort, String rmiBootstrapStubName,String vcellNagiosPassword,int warningTimeout,int criticalTimeout) throws Exception{
//		vcellVersion, levelTimes, lastSimStatus, totalTime
		SimulationStatusPersistent lastSimStatus = null;
		String vcellVersion = null;
		TreeMap<VCELL_CHECK_LEVEL, Long> levelTimesMillisec = new TreeMap<NagiosVCellMonitor.VCELL_CHECK_LEVEL, Long>();
		long startTime = System.currentTimeMillis();
		try{
//		ArrayList<Long> checkTimes = new ArrayList<Long>(VCELL_CHECK_LEVEL.values().length);
//		for (long i = 0; i < VCELL_CHECK_LEVEL.values().length; i++) {
//			checkTimes.add(i);
//		}
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
//		checkTimes.set(VCELL_CHECK_LEVEL.RMI_ONLY_0.ordinal(), System.currentTimeMillis()-startTime);
//		System.out.println(VCELL_CHECK_LEVEL.RMI_ONLY_0.name()+" "+checkTimes.get(VCELL_CHECK_LEVEL.RMI_ONLY_0.ordinal()));
		if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.CONNECT_1.ordinal()){
			if(vcellNagiosPassword == null){
				throw new UnexpectedTestStateException("vcellNagios Password required for "+VCELL_CHECK_LEVEL.CONNECT_1.toString()+" and above");
			}
			UserLoginInfo userLoginInfo = new UserLoginInfo("vcellNagios", new DigestedPassword(vcellNagiosPassword));
			VCellConnection vcellConnection = vcellBootstrap.getVCellConnection(userLoginInfo);
			levelTimesMillisec.put(VCELL_CHECK_LEVEL.CONNECT_1, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.RMI_ONLY_0));
//			checkTimes.set(VCELL_CHECK_LEVEL.CONNECT_1.ordinal(), System.currentTimeMillis()-startTime-checkTimes.get(VCELL_CHECK_LEVEL.RMI_ONLY_0.ordinal()));
			if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.INFOS_2.ordinal()){
				VCInfoContainer vcInfoContainer = vcellConnection.getUserMetaDbServer().getVCInfoContainer();
				levelTimesMillisec.put(VCELL_CHECK_LEVEL.INFOS_2, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.CONNECT_1));
//				checkTimes.set(VCELL_CHECK_LEVEL.INFOS_2.ordinal(), System.currentTimeMillis()-startTime-checkTimes.get(VCELL_CHECK_LEVEL.CONNECT_1.ordinal()));
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
//					checkTimes.set(VCELL_CHECK_LEVEL.LOAD_3.ordinal(), System.currentTimeMillis()-startTime-checkTimes.get(VCELL_CHECK_LEVEL.INFOS_2.ordinal()));
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
//							SimDataBlock simDataBlock = vcellConnection.getDataSetController().getSimDataBlock(outputContext, vcSimulationDataIdentifier, "RanC_cyt",times[times.length-1]);
						levelTimesMillisec.put(VCELL_CHECK_LEVEL.DATA_4, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.LOAD_3));
//						checkTimes.set(VCELL_CHECK_LEVEL.DATA_4.ordinal(), System.currentTimeMillis()-startTime-checkTimes.get(VCELL_CHECK_LEVEL.LOAD_3.ordinal()));
						if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.RUN_5.ordinal()){
							KeyValue copy1Key = null;
							KeyValue copy2Key = null;
							try{
								if(simulationContext.getSimulations().length != 1){
									throw new UnexpectedTestStateException("Expecting only 1 sim to be copied for "+checkLevel.toString());
								}
								SimulationStatusPersistent simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(simulation.getVersion().getVersionKey());
								if(!simulationStatus.isCompleted()){
									throw new UnexpectedTestStateException("Expecting completed sim to copy for "+checkLevel.toString());
								}
								
								String copyModelName = testModelName+"_"+"rmiport"+"_"+rmiPort;
								for(BioModelInfo bioModelInfo:vcInfoContainer.getBioModelInfos()){
									if(userLoginInfo.getUserName().equals(bioModelInfo.getVersion().getOwner().getName()) && bioModelInfo.getVersion().getName().equals(copyModelName)){
										if(bForceCleanup){
											try{vcellConnection.getUserMetaDbServer().deleteBioModel(bioModelInfo.getVersion().getVersionKey());}catch(Exception e){e.printStackTrace();}		
										}else{
											throw new MessyTestEnvironmentException("Messy test environment, not expecting "+copyModelName+" to exist at this point");
										}
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
								VCSimulationIdentifier newSimID = new VCSimulationIdentifier(newSimulation.getVersion().getVersionKey(), copyBioModel.getVersion().getOwner());
								vcellConnection.getSimulationController().startSimulation(newSimID, 1);
								
								lastSimStatus = simulationStatus;
								MessageEvent[] messageEvents = null;
								while(simulationStatus == null || (!simulationStatus.isStopped() && !simulationStatus.isCompleted() && !simulationStatus.isFailed())){
									Thread.sleep(200);
									if(((System.currentTimeMillis()-startTime)/1000) > criticalTimeout){
										vcellConnection.getSimulationController().stopSimulation(newSimID);
										vcellConnection.getMessageEvents();
										break;
									}
									simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(newSimulation.getVersion().getVersionKey());
									if(simulationStatus !=null && !simulationStatus.toString().equals((lastSimStatus==null?null:lastSimStatus.toString()))){
										lastSimStatus = simulationStatus;
									}
									if(simulationStatus!=null && simulationStatus.isFailed()){
										throw new Exception("time "+((System.currentTimeMillis()-startTime)/1000)+", Sim execution failed key:"+newSimID.getSimulationKey()+" sim "+newSimulation.getName()+" model "+copyBioModel.getVersion().getName()+" messg "+simulationStatus.getFailedMessage());
									}
									messageEvents = vcellConnection.getMessageEvents();
								}
							}finally{
								try{if(copy1Key != null){vcellConnection.getUserMetaDbServer().deleteBioModel(copy1Key);}}catch(Exception e){e.printStackTrace();}
								try{if(copy2Key != null){vcellConnection.getUserMetaDbServer().deleteBioModel(copy2Key);}}catch(Exception e){e.printStackTrace();}
							}
							levelTimesMillisec.put(VCELL_CHECK_LEVEL.RUN_5, System.currentTimeMillis()-startTime-levelTimesMillisec.get(VCELL_CHECK_LEVEL.DATA_4));
//							checkTimes.set(VCELL_CHECK_LEVEL.RUN_5.ordinal(), System.currentTimeMillis()-startTime-checkTimes.get(VCELL_CHECK_LEVEL.DATA_4.ordinal()));
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

	private static class VCellStatus {
//		private NAGIOS_STATUS nagiosStatus;
		private String message;
//		private MonitorMode monitorMode;
		private HashMap<Integer, CheckResults> checkResultsMap;
		private VCellStatus(String message,HashMap<Integer, CheckResults> checkResultsMap){
			if(message == null){
				throw new IllegalArgumentException("message cannot be null message="+message);
			}
			this.message = message;
			this.checkResultsMap = (checkResultsMap==null?new HashMap<Integer, CheckResults>():checkResultsMap);
		}
//		public MonitorMode getMonitormoMode(){
//			return monitorMode;
//		}
		private NAGIOS_STATUS getNagiosStatus(){
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
			for(Integer rmiPort:checkResultsMap.keySet()){
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
					sb.replace(i, i+1, "(equal)");
				}else if(sb.charAt(i) == '\''){
					sb.replace(i, i+1, "(snglquot)");
				}else if(sb.charAt(i) == '|'){
					sb.replace(i, i+1, "(pipe)");
				}
			}
			return sb.toString();
		}
		public String getNagiosReply(){
			StringBuffer sb = new StringBuffer();
			// Add performance info for nagios
			for(Integer rmiPort:checkResultsMap.keySet()){
				CheckResults checkResults = checkResultsMap.get(rmiPort);
				if(checkResults != null && checkResults.levelTimesMillisec != null){
					for (VCELL_CHECK_LEVEL vcellCheckLevel:checkResults.levelTimesMillisec.keySet()) {
						sb.append(" "+vcellCheckLevel.name()+"(rmi"+rmiPort+")"+"="+(checkResults.levelTimesMillisec.get(vcellCheckLevel))/1000+"s");
					}
				}
			}
			return getNagiosStatus().ordinal()+getMessage()+(sb.length()==0?"":" |"+sb.toString());
		}
	}
	private static class VCellNagioStatus extends VCellStatus {
		private VCELL_CHECK_LEVEL vcellCheckLevel;
		public VCellNagioStatus(VCELL_CHECK_LEVEL vcellCheckLevel, String message,HashMap<Integer, CheckResults> checkResultsMap) {
			super(message,checkResultsMap);
//			if(nagiosStatus == null || message == null){
//				throw new IllegalArgumentException("status and message cannot be null status="+nagiosStatus+" message="+message);
//			}
			this.vcellCheckLevel = vcellCheckLevel;
		}
		public VCELL_CHECK_LEVEL getVCellCheckLevel(){
			return vcellCheckLevel;
		}
	}
	private static class VCellHoldStatus extends VCellStatus{
		public VCellHoldStatus(String message){
			super(message, null);
		}
	}
//	private static enum MonitorMode {normal,deploy,kill};
//	private static String deployReply = NAGIOS_STATUS.OK.ordinal()+"Deploy In Progress";
//	private static MonitorMode monitorMode = MonitorMode.normal;
//	private static Socket latestSocket = null;
	private static String holdMessage = null;
	private static VCellStatus vcellStatusConnect = new VCellStatus("Starting...",null);
	private static VCellStatus vcellStatusFull = new VCellStatus("Starting...",null);
	private static synchronized void setVCellStatus(VCellStatus status,boolean bFull){
		if(status instanceof VCellHoldStatus){
			// Sets on hold during deploy, stops testing but keeps replies going
			vcellStatusConnect = status;
			vcellStatusFull = status;
			return;
		}else if (vcellStatusFull instanceof VCellHoldStatus){
			// On hold, used during deploy while VCell is reset to keep nagios happy
			return;
		}
		// If not on hold
		if(bFull){
			vcellStatusFull = status;
		}else{
			vcellStatusConnect = status;
		}
	}
	private static synchronized VCellStatus getVCellStatus(boolean bFull){
		return (bFull?vcellStatusFull:vcellStatusConnect);
	}
//	private static synchronized void setLatestSocket(Socket socket) throws Exception{
//		latestSocket = socket;
//	}
//	private static synchronized Socket getLatestSocket(){
//		Socket socket = latestSocket;
//		latestSocket = null;
//		return socket;
//	}
//	private static synchronized boolean isSocketClear(){
//		return latestSocket == null;
//	}
//	private static final String KILL_RESPONSE = NAGIOS_STATUS.OK.ordinal()+"Exiting...";
	
	public static void nagiosRequestServicingLoop(ServerSocket serverSocket) throws IOException,NumberFormatException{
		
//		ServerSocket serverSocket = null;
//		try{
//		ServerSocket serverSocket = new ServerSocket(monitorSocket);
			while(true){
				//Wait for nagiosServer or another monitor to contact us
//				setLatestSocket(serverSocket.accept());
//				if(monitorMode == MonitorMode.kill){
//					// Don't service any request, we are exiting
//					Thread.sleep(50);
//					continue;
//				}
				Socket threadSocket = serverSocket.accept();
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
						PrintWriter out = null;
						try{
							out = new PrintWriter(threadSocket.getOutputStream(), true);
							// Read request
							BufferedReader in = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
							String request = in.readLine();
							
//							if(request.equals(MonitorMode.kill.name())){
//								//nagios never sends this request
//								// A newer monitor is ready to take our place
//								setVCellStatus(new VCellStatus(MonitorMode.kill, NAGIOS_STATUS.OK, "Normal Exit..."));
//								out.println(getVCellStatus().getNagiosReply());
////								monitorMode = MonitorMode.kill;
////								try{
////									threadSocket.close();
////									threadSocket = null;
////									serverSocket.close();
////								}catch(Exception e){
////									e.printStackTrace();
////								}
////								System.exit(0);
////							}else if(request.equals("start")){
////								//nagios never sends this request
////								// Command from external DeployVCell to start main testing thread
////								setVCellStatus(new VCellStatus(MonitorMode.normal, NAGIOS_STATUS.OK, "Begin Tests..."));
////								out.println(getVCellStatus().getNagiosReply());
//							}else 
							if(request.startsWith("hold:")){
								setVCellStatus(new VCellHoldStatus(request), true);
								//nagios never sends this request
								// Request for VCell test status from nagios server
								out.println(getVCellStatus(true).getNagiosReply());
							}else if(request.equals("statusConnect")){
								//nagios only sends this request
								// Request for VCell test status from nagios server
								out.println(getVCellStatus(false).getNagiosReply());
							}else if(request.equals("statusFull")){
								//nagios only sends this request
								// Request for VCell test status from nagios server
								out.println(getVCellStatus(true).getNagiosReply());
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
//							if(getVCellStatus().getMonitormoMode().equals(MonitorMode.kill)){
//								serverSocket.close();
//								return;
//							}
						}
//					}
//				}).start();
				
//				//wait for thread to start, socket to be cleared
//				while(!isSocketClear()){
//					Thread.sleep(50);
//				}
			}
//		}catch(Exception e){
//			e.printStackTrace();
//			if(serverSocket != null){
//				serverSocket.close();
//			}
//		}
		
	}
	
	public static ServerSocket claimSocket(int port) throws Exception{
		killMonitorProcess(port);
		return new ServerSocket(port);

//		boolean bKillSuccess = true;
//		Socket clientSocket = null;
//		try {
//			clientSocket = new Socket("localhost", port);
//			clientSocket.setSoTimeout(60000);//block reads for only 60 seconds
//			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//			System.out.println("kill...");
//			//Send kill to other monitor so it can gracefully shutdown
//			out.println(MonitorMode.kill.name());
//			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//			System.out.println("Waiting...");
//			String killResponse = null;
//			try{
//				//Read othermonitor response to kill
//				killResponse = in.readLine();//wait for response or timeout
//			}catch(SocketTimeoutException sto){
//				//ignore, continue with other options
//				sto.printStackTrace();
//			}
//			if(killResponse.equals(KILL_RESPONSE)){
//				System.out.println("Success");
//			}else{
//				throw new ProtocolException("Unexpected response: '"+killResponse+"'");
//			}
//		}catch(Exception e){
//			bKillSuccess = false;
//		}finally{
//			if(clientSocket != null){
//				try{clientSocket.close();}catch(Exception e){e.printStackTrace();}
//			}
//		}
//		//wait up to a minute if necessary for service socket to be free so we can claim it
//		ServerSocket serverSocket = null;
//		long startTime = System.currentTimeMillis();
//		while(bKillSuccess && (System.currentTimeMillis()-startTime)<60000){
//			try{
//				Thread.sleep(1000);
//				serverSocket = new ServerSocket(port);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		if(serverSocket == null){
//			//try to find who has this socket and kill their process if they are a monitor
//			killMonitorProcess(port);
//			return new ServerSocket(port);
//		}
//
//		return serverSocket;
//
//		
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
