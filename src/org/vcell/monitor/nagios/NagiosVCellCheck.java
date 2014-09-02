package org.vcell.monitor.nagios;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.Naming;
import java.util.ArrayList;

import org.vcell.util.BigString;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.modeldb.VCInfoContainer;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class NagiosVCellCheck {

	private static class NamingLookupException extends Exception {
		public NamingLookupException(String message,Exception exc){
			super(message, exc);
		}
	}
	public static enum NAGIOS_STATUS {OK,WARNING,CRITICAL,UNKNOWN};
	public static enum VCELL_CHECK_LEVEL {RMI_ONLY_0,CONNECT_1,INFOS_2,LOAD_3,DATA_4,RUN_5};
	
	public static void main(String[] args) {
		try{
			if(args.length != 4){
				System.out.println("Usage: java -jar "+NagiosVCellCheck.class.getSimpleName()+".jar checkLevel rmiHost rmiPort vcellNagiosPassword");
				System.exit(NAGIOS_STATUS.UNKNOWN.ordinal());
			}
			System.out.println(checkVCell(VCELL_CHECK_LEVEL.valueOf(args[0]),args[1], Integer.parseInt(args[2]),"VCellBootstrapServer",args[3]));
			System.exit(NAGIOS_STATUS.OK.ordinal());
		}catch(NumberFormatException e){
			System.out.println(e.getClass().getName()+" "+e.getMessage());
			System.exit(NAGIOS_STATUS.UNKNOWN.ordinal());
		}catch(NamingLookupException e){
			System.out.println(e.getCause().getClass().getName()+" "+e.getMessage());
			System.exit(NAGIOS_STATUS.UNKNOWN.ordinal());
		}catch(Exception e){
			System.out.println(e.getClass().getName()+" "+e.getMessage());
			System.exit(NAGIOS_STATUS.CRITICAL.ordinal());
		}
	}
	public static String checkVCell(VCELL_CHECK_LEVEL checkLevel, String rmiHostName,int rmiPort, String rmiBootstrapStubName,String vcellNagiosPassword) throws Exception{
		String rmiUrl = "//" + rmiHostName + ":" +rmiPort + "/"+rmiBootstrapStubName;
		VCellBootstrap vcellBootstrap = null;
		PrintStream sysout = System.out;
		PrintStream syserr = System.err;
		ByteArrayOutputStream baos_out = new ByteArrayOutputStream();
		ByteArrayOutputStream baos_err = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos_out));
		System.setErr(new PrintStream(baos_err));
		try{
			vcellBootstrap = (VCellBootstrap)Naming.lookup(rmiUrl);
			if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.CONNECT_1.ordinal()){
				VCellConnection vcellConnection = vcellBootstrap.getVCellConnection(
					new UserLoginInfo("vcellNagios", new DigestedPassword(vcellNagiosPassword)));
				if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.INFOS_2.ordinal()){
					VCInfoContainer vcInfoContainer = vcellConnection.getUserMetaDbServer().getVCInfoContainer();
					if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.LOAD_3.ordinal()){
						KeyValue bioModelKey = null;
						for(BioModelInfo bioModelInfo:vcInfoContainer.getBioModelInfos()){
							if(bioModelInfo.getVersion().getName().equals("Solver Suite 5.1 (ALPHA)")){
								bioModelKey = bioModelInfo.getVersion().getVersionKey();
								break;
							}
						}
						BigString bioModelXML = vcellConnection.getUserMetaDbServer().getBioModelXML(bioModelKey);
						BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
						if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.DATA_4.ordinal()){
							SimulationContext simulationContext = bioModel.getSimulationContext("3D pde");
							Simulation simulation = simulationContext.getSimulation("Copy of fully implicit");
							VCSimulationDataIdentifier vcSimulationDataIdentifier =
								new VCSimulationDataIdentifier(simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), 0);
							ArrayList<AnnotatedFunction> outputFunctionsList = simulationContext.getOutputFunctionContext().getOutputFunctionsList();
							OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
							double[] times = vcellConnection.getDataSetController().getDataSetTimes(vcSimulationDataIdentifier);
							SimDataBlock simDataBlock = vcellConnection.getDataSetController().getSimDataBlock(outputContext, vcSimulationDataIdentifier, "RanC_cyt",times[times.length-1]);
							if(checkLevel.ordinal() >= VCELL_CHECK_LEVEL.RUN_5.ordinal()){
								if(true){throw new RuntimeException("Not Yet Implemented");}
							}
						}
					}
				}
			}
			return vcellBootstrap.getVCellSoftwareVersion();
		}catch(Exception e){
			throw new NamingLookupException(e.getMessage(),e);
		}finally{
			System.setOut(sysout);
			System.setErr(syserr);
		}
	}

}
