package cbit.vcell.client.pyvcellproxy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

import org.apache.thrift.TException;

import cbit.vcell.client.pyvcellproxy.DataAccessException;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Species;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class VCellProxyHandler implements VCellProxy.Iface{

	VCellClient vcellClient = null;
	
	String simID = "SimID_xxxxx";
	
	User user = null;
	VCSimulationDataIdentifier vcSimulationDataIdentifier = null;
	VCSimulationIdentifier vcSimulationIdentifier = null;
	SimulationData simulationData = null;
	
	public VCellProxyHandler(VCellClient vcellClient) {
		this.vcellClient = vcellClient;
	}
	
	public VCellProxyHandler() {
	}

	@Override 
	public List<SimulationDataSetRef> getSimsFromOpenModels() throws cbit.vcell.client.pyvcellproxy.DataAccessException, org.apache.thrift.TException {
		ArrayList<SimulationDataSetRef> simulationDataSetRefs= new ArrayList<SimulationDataSetRef>();
		//ArrayList<>
		TopLevelWindowManager windowManager = null;
		Enumeration<TopLevelWindowManager> windowManagers = vcellClient.getMdiManager().getWindowManagers();

		while (windowManagers.hasMoreElements()) {
			windowManager = windowManagers.nextElement();
			if ((true || windowManager.getComponent()!=null)) {
				if (windowManager.getClass().getName().contains("BioModelWindowManager")){
					BioModelWindowManager selectedBioWindowManager = (BioModelWindowManager)windowManager;
					int numSimulations = selectedBioWindowManager.getBioModel().getNumSimulations();
					for (int i=0; i<numSimulations; i++){
						
					    BioModel bioModel =  selectedBioWindowManager.getBioModel();
					    SimulationInfo simInfo= bioModel.getSimulation(i).getSimulationInfo();
						SimulationStatus simStatus = vcellClient.getRequestManager().getServerSimulationStatus(simInfo);
						if (simStatus!=null && simStatus.getHasData()){
							SimulationDataSetRef simulationDataSetReference = new SimulationDataSetRef();
							simulationDataSetReference.setSimName(simInfo.getName());
							simulationDataSetReference.setSimId(simInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey().toString());
							simulationDataSetReference.setModelId(bioModel.getVersion().getVersionKey().toString());
//							DataIdentifier[] dataIdentifiers = null;
//							try {
//								VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
//								if (vcSimulationDataIdentifier != null)  {
//									dataIdentifiers = vcellClient.getRequestManager().getDataManager(null, this.vcSimulationDataIdentifier, true).getDataIdentifiers();
//								}
//							} catch (org.vcell.util.DataAccessException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							ArrayList<String> vars = new ArrayList<String>();
//							for (int ii=0; ii<dataIdentifiers.length; ii++){
//								vars.add(dataIdentifiers[i].getDisplayName());
//							}
//							simulationDataSetReference.setVariableList(vars);
							simulationDataSetRefs.add(simulationDataSetReference);
		
						}
					}
					
				}
//				} else {
//					if (windowManager.getClass().getName()=="MathModelWindowManager"){
//						MathModelWindowManager selectedMathWindowManager = (MathModelWindowManager)windowManager; 
//						int numSimulations = selectedMathWindowManager.getMathModel().getNumSimulations();
//						for (int i=0; i<numSimulations; i++){
//							
//						
//							SimulationInfo simInfo= selectedMathWindowManager.getMathModel().getSimulations(i).getSimulationInfo();
//							SimulationStatus simStatus = vcellClient.getRequestManager().getServerSimulationStatus(simInfo);
//							if (simStatus.getHasData()){
//								simulationDataSetRefs.add(simInfo.getName());
//								simInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey().toString();
//							}
//						}
//					} 
//				}
//				
			} 
		}

			return simulationDataSetRefs;
	
			
	}
	
	@Override
	public String exportRequest(ExportRequestSpec exportRequestSpec)
			throws ExportException, TException {
		// TODO Auto-generated method stub
		String varList = "";
		String exportedPath = "";
		System.out.println("VCellProxyHandler: I'm being asked to export SimID="+exportRequestSpec.getSimID()+" with the following variable(s):");
		ListIterator<String> iter = exportRequestSpec.getVariables().listIterator();
		while (iter.hasNext()) {
			String varStr = iter.next();
			System.out.println(varStr);
			varList = varStr +"  " + varList;
		}	
			

			user = vcellClient.getClientServerManager().getUser();

			KeyValue simKeyValue = new KeyValue("92733070");
			//KeyValue bioModelKey = new KeyValue("92733091");
			String modelKeyStr = "Model Key String";
			vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
			vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
			ExportSpecs exportSpecs = null;
			OutputContext outputContext = null;

				
			VariableSpecs variableSpecs = new VariableSpecs((String[]) exportRequestSpec.getVariables().toArray(new String[0]), ExportConstants.VARIABLE_ALL);
			double dataTimes[] = null;
					

					
			try {
				dataTimes = vcellClient.getClientServerManager().getDataSetController().getDataSetTimes(vcSimulationDataIdentifier);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TimeSpecs timeSpecs = new TimeSpecs(0, dataTimes.length-1 , dataTimes, ExportConstants.TIME_RANGE);


			
			GeometrySpecs geometrySpecs = new GeometrySpecs(null, 0, 0, ExportConstants.GEOMETRY_FULL);
			
			FormatSpecificSpecs formatSpecificSpecs = null;
            
			exportSpecs = new ExportSpecs(vcSimulationDataIdentifier, ExportConstants.FORMAT_VTK_UNSTRUCT, variableSpecs, timeSpecs, geometrySpecs, formatSpecificSpecs, modelKeyStr, modelKeyStr);
			
		
			try {
				ExportEvent event = vcellClient.getClientServerManager().getDataSetController().makeRemoteFile(outputContext,exportSpecs);
				System.out.println("Export location = "+event.getLocation());
				// ignore; we'll get two downloads otherwise... getClientServerManager().getAsynchMessageManager().fireExportEvent(event);
			} catch (org.vcell.util.DataAccessException | RemoteException exc) {
				try {
					throw new RemoteException(exc.getMessage());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
		System.out.println("From time t="+exportRequestSpec.getStartTime()+" to t="+exportRequestSpec.getEndTime());
		
		exportedPath = "Path = "+exportRequestSpec.getSimID()+"_Exported_Path...  Variables = "+varList+"    EndTime = "+Integer.toString(exportRequestSpec.getEndTime());
		

		return exportedPath;
	}




	@Override
	public List<SimulationDataSetRef> getSimsFromModel(ModelRef modelRef)
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getVariableList(
			SimulationDataSetRef simulationDataSetRef)
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ModelRef> getBioModels()
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ModelRef> getMathModels()
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public cbit.vcell.client.pyvcellproxy.User getUser()
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportAllRequest(SimulationDataSetRef simulationDataSetRef)
			throws ExportException, TException {

		String varList = "";
		String exportedPath = "";




			user = vcellClient.getClientServerManager().getUser();
		
			KeyValue simKeyValue = new KeyValue(simulationDataSetRef.getSimId());
			vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
			vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
			ExportSpecs exportSpecs = null;
			OutputContext outputContext = null;

				

			double dataTimes[] = null;
					
			DataIdentifier[] dataIdentifiers = null;
			try {
				VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
				if (vcSimulationDataIdentifier != null)  {
					dataIdentifiers = vcellClient.getRequestManager().getDataManager(null, this.vcSimulationDataIdentifier, true).getDataIdentifiers();
				}
			} catch (org.vcell.util.DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<String> vars = new ArrayList<String>();
			for (int ii=0; ii<dataIdentifiers.length; ii++){
				vars.add(dataIdentifiers[ii].getDisplayName());
			}
			VariableSpecs variableSpecs = new VariableSpecs(vars.toArray(new String[0]), ExportConstants.VARIABLE_MULTI);
			try {
				dataTimes = vcellClient.getClientServerManager().getDataSetController().getDataSetTimes(vcSimulationDataIdentifier);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TimeSpecs timeSpecs = new TimeSpecs(0, dataTimes.length-1 , dataTimes, ExportConstants.TIME_RANGE);


			
			GeometrySpecs geometrySpecs = new GeometrySpecs(null, 0, 0, ExportConstants.GEOMETRY_FULL);
			
			FormatSpecificSpecs formatSpecificSpecs = null;
            
			exportSpecs = new ExportSpecs(vcSimulationDataIdentifier, ExportConstants.FORMAT_VTK_UNSTRUCT, variableSpecs, timeSpecs, geometrySpecs, formatSpecificSpecs, simulationDataSetRef.simName, null);
			
		
			try {
				ExportEvent event = vcellClient.getClientServerManager().getDataSetController().makeRemoteFile(outputContext,exportSpecs);
				System.out.println("Export location = "+event.getLocation());
				String[] urlSplit = event.getLocation().split("/");
				File exportFile = new File(new File(PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirProperty)), urlSplit[urlSplit.length - 1]);
				exportedPath = exportFile.toURL().toString();
				System.out.print("ExportedPath = "+exportedPath);
				// ignore; we'll get two downloads otherwise... getClientServerManager().getAsynchMessageManager().fireExportEvent(event);
			} catch (org.vcell.util.DataAccessException | RemoteException | MalformedURLException exc) {
				exc.printStackTrace();
				throw new RuntimeException(exc.getMessage());
			} 
		
		

		return exportedPath;
	}
}
