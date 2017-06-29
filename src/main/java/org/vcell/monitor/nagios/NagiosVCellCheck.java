package org.vcell.monitor.nagios;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.vcell.util.BigString;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCInfoContainer;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
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

public class NagiosVCellCheck {

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
	
	public static enum NAGIOS_STATUS {OK,WARNING,CRITICAL,UNKNOWN};
	public static enum VCELL_CHECK_LEVEL {RMI_ONLY_0,CONNECT_1,INFOS_2,LOAD_3,DATA_4,RUN_5};
	
	public static void main(String[] args) {
		System.exit(mainArgs(args).ordinal());
	}
	
	public static NAGIOS_STATUS mainArgs(String[] args) {
		PrintStream sysout = System.out;
		PrintStream syserr = System.err;
		try{
			ByteArrayOutputStream baos_out = new ByteArrayOutputStream();
			ByteArrayOutputStream baos_err = new ByteArrayOutputStream();
			System.setOut(new PrintStream(baos_out));
			System.setErr(new PrintStream(baos_err));

			String host=null;
			ArrayList<Integer> rmiPorts = new ArrayList<Integer>();
			String password=null;
			VCELL_CHECK_LEVEL checkLevel=null;
			int warningTimeout = -1; //seconds
			int criticalTimeout = -1; //seconds
			for(int i = 0;i < args.length;i++){
				if(args[i].equals("-H")){
					i++;
					host = args[i];
				}else if(args[i].equals("-i")){
					i++;
					String latestToken = null;
					try{
						StringTokenizer st  = new StringTokenizer(args[i], ":");
						latestToken = st.nextToken();
						rmiPorts.add(Integer.parseInt(latestToken));
						if(st.hasMoreTokens()){
							latestToken = st.nextToken();
							rmiPorts.add(Integer.parseInt(latestToken));
						}
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing rmiPort Integer "+latestToken);
					}
				}else if(args[i].equals("-p")){
					i++;
					password = args[i];
				}else if(args[i].equals("-L")){
					i++;
					checkLevel = VCELL_CHECK_LEVEL.valueOf(args[i]);
				}else if(args[i].equals("-w")){
					i++;
					try{
						warningTimeout = Integer.parseInt(args[i]);
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing warningTimeout Integer "+args[i]);
					}
				}else if(args[i].equals("-c")){
					i++;
					try{
						criticalTimeout = Integer.parseInt(args[i]);
					}catch(NumberFormatException e){
						throw new UnexpectedTestStateException("Error parsing criticalTimeout Integer "+args[i]);
					}
				}else{
					throw new UnexpectedTestStateException("Usage: java -jar "+NagiosVCellCheck.class.getSimpleName()+".jar -L checkLevel{"+
						VCELL_CHECK_LEVEL.RMI_ONLY_0.toString()+","+
						VCELL_CHECK_LEVEL.CONNECT_1.toString()+","+
						VCELL_CHECK_LEVEL.INFOS_2.toString()+","+
						VCELL_CHECK_LEVEL.LOAD_3.toString()+","+
						VCELL_CHECK_LEVEL.DATA_4.toString()+","+
						VCELL_CHECK_LEVEL.RUN_5.toString()+
						"} -H vcellRMIHost -i rmiPort -p vcellNagiosPassword -w warningTimeout -c criticalTimeout");
				}
			}
			//Test multiple ports if present, both return the same result
			String result = null;
			for(Integer rmiPort:rmiPorts){
				if(rmiPort == -1){
					continue;
				}
				String temp = checkVCell(checkLevel,host, rmiPort,"VCellBootstrapServer",password,warningTimeout,criticalTimeout);
				if(result != null && !result.equals(temp)){
					throw new UnexpectedTestStateException("Not expecting rmiport="+rmiPorts.get(0)+" result="+result+" and rmiport="+rmiPort+" result="+temp+" to be different");
				}
				result = temp;
			}
			if(result == null){
				throw new UnexpectedTestStateException("test result not expected to be null");
			}
			sysout.println(result);
			return NAGIOS_STATUS.OK;
		}catch(UnexpectedTestStateException e){
			sysout.println(e.getMessage());
			return NAGIOS_STATUS.UNKNOWN;
		}catch(WarningTestConditionException e){
			sysout.println(e.getMessage());
			return NAGIOS_STATUS.WARNING;
		}catch(Exception e){
			sysout.println((e.getCause()==null?"":e.getCause().getClass().getSimpleName()+" "+e.getCause().getMessage()+":")+e.getClass().getName()+" "+e.getMessage());
			return NAGIOS_STATUS.CRITICAL;
		}finally{
			System.setOut(sysout);
			System.setErr(syserr);
		}
	}
	private static String checkVCell(VCELL_CHECK_LEVEL checkLevel, String rmiHostName,int rmiPort, String rmiBootstrapStubName,String vcellNagiosPassword,int warningTimeout,int criticalTimeout) throws Exception{
		long startTime = System.currentTimeMillis();
		SimulationStatusPersistent lastSimStatus = null;

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
		if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.CONNECT_1.ordinal()){
			if(vcellNagiosPassword == null){
				throw new UnexpectedTestStateException("vcellNagios Password required for "+VCELL_CHECK_LEVEL.CONNECT_1.toString()+" and above");
			}
			VCellConnection vcellConnection = vcellBootstrap.getVCellConnection(new UserLoginInfo("vcellNagios", new DigestedPassword(vcellNagiosPassword)));
			if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.INFOS_2.ordinal()){
				VCInfoContainer vcInfoContainer = vcellConnection.getUserMetaDbServer().getVCInfoContainer();
				if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.LOAD_3.ordinal()){
					KeyValue bioModelKey = null;
					final String testModelName = "Solver Suite 5.1 (BETA only ode)";
					for(BioModelInfo bioModelInfo:vcInfoContainer.getBioModelInfos()){
						if(bioModelInfo.getVersion().getName().equals(testModelName)){
							bioModelKey = bioModelInfo.getVersion().getVersionKey();
							break;
						}
					}
					BigString bioModelXML = vcellConnection.getUserMetaDbServer().getBioModelXML(bioModelKey);
					BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
					bioModel.refreshDependencies();
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
								
								String copyModelName = testModelName+"_"+rmiHostName+"_"+rmiPort;
								for(BioModelInfo bioModelInfo:vcInfoContainer.getBioModelInfos()){
									if(bioModelInfo.getVersion().getName().equals(copyModelName)){
										throw new UnexpectedTestStateException("Messy test environment, not expecting "+copyModelName+" to exist at this point");
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
									messageEvents = vcellConnection.getMessageEvents();
								}
							}finally{
								try{if(copy1Key != null){vcellConnection.getUserMetaDbServer().deleteBioModel(copy1Key);}}catch(Exception e){e.printStackTrace();}
								try{if(copy2Key != null){vcellConnection.getUserMetaDbServer().deleteBioModel(copy2Key);}}catch(Exception e){e.printStackTrace();}
							}
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		if(criticalTimeout != -1 && ((endTime-startTime)/1000) > criticalTimeout){throw new Exception(checkLevel.toString()+" test exceeded criticalTimeout="+criticalTimeout+" seconds lastSimStatus="+lastSimStatus);}
		if(warningTimeout != -1 && ((endTime-startTime)/1000) > warningTimeout){throw new WarningTestConditionException(checkLevel.toString()+" test exceeded warningTimeout="+warningTimeout+" seconds lastSimStatus="+lastSimStatus);}

		return vcellBootstrap.getVCellSoftwareVersion();
	}

}
