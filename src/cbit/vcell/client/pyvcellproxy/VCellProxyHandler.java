package cbit.vcell.client.pyvcellproxy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.archivers.zip.ZipUtil;
import org.apache.thrift.TException;

import cbit.vcell.client.pyvcellproxy.DataAccessException;

import org.vcell.util.FileUtils;
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
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class VCellProxyHandler implements VCellProxy.Iface{

	VCellClient vcellClient = null;
	
	
	User user = null;
	VCSimulationDataIdentifier vcSimulationDataIdentifier = null;
	VCSimulationIdentifier vcSimulationIdentifier = null;
	//SimulationData simulationData = null;

	private File localVisDataDir = null;
	
	public VCellProxyHandler(VCellClient vcellClient) {
		this.vcellClient = vcellClient;
		this.localVisDataDir = ResourceUtil.getLocalVisDataDir();
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
		ListIterator<VariableInfo> iter = exportRequestSpec.getVariables().listIterator();
		while (iter.hasNext()) {
			VariableInfo varInfo = iter.next();
			System.out.println(varInfo.getVariableName());
			varList = varInfo +"  " + varList;
		}	
			
			user = vcellClient.getClientServerManager().getUser();
			KeyValue simKeyValue = new KeyValue(exportRequestSpec.getSimID());
			String modelKeyStr = exportRequestSpec.getSimID();
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
					e.printStackTrace();
				}
			}
		
		
		System.out.println("From time t="+exportRequestSpec.getStartTime()+" to t="+exportRequestSpec.getEndTime());
		
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
		
		//exportedPath = "Path = "+exportRequestSpec.getSimID()+"_Exported_Path...  Variables = "+varList+"    EndTime = "+Integer.toString(exportRequestSpec.getEndTime());
		

		return exportedPath;
	}




	@Override
	public List<SimulationDataSetRef> getSimsFromModel(ModelRef modelRef)
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {
		
		return null;
	}

	@Override
	public List<VariableInfo> getVariableList(
			SimulationDataSetRef simulationDataSetRef)
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {

		user = vcellClient.getClientServerManager().getUser();
		KeyValue simKeyValue = new KeyValue(simulationDataSetRef.simId);
		vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
		
		DataIdentifier[] dataIdentifiers = null;
		try {
			VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
			if (vcSimulationDataIdentifier != null)  {
				dataIdentifiers = vcellClient.getRequestManager().getDataManager(null, vcSimulationDataIdentifier, true).getDataIdentifiers();
			}
		} catch (org.vcell.util.DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<VariableInfo> variableInfos = new ArrayList<VariableInfo>();
		for (int ii=0; ii<dataIdentifiers.length; ii++){
			VariableInfo varInfo = new VariableInfo();
			varInfo.setVariableName(dataIdentifiers[ii].getDisplayName());
			if (dataIdentifiers[ii].getDomain()!=null){
				varInfo.setDomainName(dataIdentifiers[ii].getDomain().getName());
			} else {
				varInfo.setDomainName("None");
			}
			varInfo.setVariableDomainType(dataIdentifiers[ii].getVariableType().getTypeName());
			varInfo.setUnitsLabel(dataIdentifiers[ii].getVariableType().getDefaultUnits());
			variableInfos.add(varInfo);
			
		}
		return variableInfos;
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
	public cbit.vcell.client.pyvcellproxy.User getUser() throws cbit.vcell.client.pyvcellproxy.DataAccessException, TException {
		cbit.vcell.client.pyvcellproxy.User user = new cbit.vcell.client.pyvcellproxy.User();
		user.setUserKey(this.vcellClient.getClientServerManager().getUser().getID().toString());
		user.setUserName(this.vcellClient.getClientServerManager().getUser().getName());
		return user;
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
			
		//	vcellClient.getClientServerManager().getDataSetController().getDataIdentifiers(null, vcSimulationDataIdentifier)[0].;
			
			exportSpecs = new ExportSpecs(vcSimulationDataIdentifier, ExportConstants.FORMAT_VTK_UNSTRUCT, variableSpecs, timeSpecs, geometrySpecs, formatSpecificSpecs, simulationDataSetRef.simName, null);
		
			try {
				ExportEvent event = vcellClient.getClientServerManager().getDataSetController().makeRemoteFile(outputContext,exportSpecs);
				System.out.println("Export location = "+event.getLocation());
				String[] urlSplit = event.getLocation().split("/");
				File exportFile = new File(new File(PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirProperty)), urlSplit[urlSplit.length - 1]);
				exportedPath = exportFile.toURL().toString();
				System.out.println("ExportedPath = "+exportedPath);
				// ignore; we'll get two downloads otherwise... getClientServerManager().getAsynchMessageManager().fireExportEvent(event);
			} catch (org.vcell.util.DataAccessException | RemoteException | MalformedURLException exc) {
				exc.printStackTrace();
				throw new RuntimeException(exc.getMessage());
			} 
		return exportedPath;
	}

	@Override
	public String getDataSetFileOfDomainAtTimeIndex(
			SimulationDataSetRef simulationDataSetRef, String domainName,
			int timeIndex) throws DataAccessException, TException {
		
	//see if data point exists first
	File vtuSimDataFolder = new File(localVisDataDir,simulationDataSetRef.getSimId());
	if (!(vtuSimDataFolder.exists() && vtuSimDataFolder.isDirectory())){
		vtuSimDataFolder.mkdirs();
		try {
			System.out.println("Just made dir: "+vtuSimDataFolder.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	String timeIndexStr = String.format("%06d" , timeIndex);
	String vtuFileNameStr = "SimID_"+simulationDataSetRef.getSimId()+"_0_"+simulationDataSetRef.getSimId()+"_0_"+domainName+"_"+timeIndexStr+".vtu";
	System.out.println("looking for file: "+vtuFileNameStr);
	if (! (new File(vtuSimDataFolder, vtuFileNameStr).exists())){
		//WAY OVERKILL FOR NOW:
		System.out.println("Didn't find data. Exporting....");
		String zipExportPath = exportAllRequest(simulationDataSetRef);
		
		System.out.println("zipExportPath = "+zipExportPath);
		File copiedZipFile = new File(vtuSimDataFolder, simulationDataSetRef.getSimId()+".zip");
		try {
			FileUtils.saveUrlToFile(copiedZipFile, zipExportPath);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		try {
			info.aduna.io.ZipUtil.extract(copiedZipFile, vtuSimDataFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	File vtuDataFile = new File(vtuSimDataFolder, vtuFileNameStr);
	if (vtuDataFile.exists()) {
		System.out.println("vtuData file exists.");
		try {
			System.out.println("path = "+vtuDataFile.getCanonicalPath());
			return vtuDataFile.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("returning null");
			return null;
		}
		} else {
			System.out.println("returning null");
			return null;
		}
	}

	@Override
	public String getDataSetFileOfDomainAtTimePoint(
			SimulationDataSetRef simulationDataSetRef, String domainName,
			double timePoint) throws DataAccessException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEndTimeIndex(SimulationDataSetRef simulationDataSetRef)
			throws DataAccessException, TException {
		
		user = vcellClient.getClientServerManager().getUser();		
		KeyValue simKeyValue = new KeyValue(simulationDataSetRef.getSimId());
		vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
		vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);

		try {
			VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
			if (vcSimulationDataIdentifier != null)  {
				return vcellClient.getRequestManager().getDataManager(null, this.vcSimulationDataIdentifier, true).getDataSetTimes().length - 1;
			}
		} catch (org.vcell.util.DataAccessException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public List<Double> getTimePoints(SimulationDataSetRef simulationDataSetRef)
			throws DataAccessException, TException {
		user = vcellClient.getClientServerManager().getUser();		
		KeyValue simKeyValue = new KeyValue(simulationDataSetRef.getSimId());
		vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
		vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
		ArrayList<Double> timesList = new ArrayList<Double>();
		try {
			if (vcSimulationDataIdentifier != null) {				
				double[] timesArray = vcellClient.getRequestManager().getDataManager(null, this.vcSimulationDataIdentifier, true).getDataSetTimes();
				for (int i=0; i<timesArray.length; i++){
				    timesList.add(new Double(timesArray[i]));
				}
				return timesList;			 
			}
		} catch (org.vcell.util.DataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
