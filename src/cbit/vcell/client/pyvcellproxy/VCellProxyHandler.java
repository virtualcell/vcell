package cbit.vcell.client.pyvcellproxy;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.thrift.TException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;

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
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class VCellProxyHandler implements VCellProxy.Iface{

	private final VCellClient vcellClient;
	private final File localVisDataDir;
	
	public VCellProxyHandler(VCellClient vcellClient) {
		this.vcellClient = vcellClient;
		this.localVisDataDir = ResourceUtil.getLocalVisDataDir();
	}

	@Override 
	public List<SimulationDataSetRef> getSimsFromOpenModels() throws cbit.vcell.client.pyvcellproxy.DataAccessException, org.apache.thrift.TException {
		ArrayList<SimulationDataSetRef> simulationDataSetRefs = new ArrayList<SimulationDataSetRef>();
		TopLevelWindowManager windowManager = null;
		Enumeration<TopLevelWindowManager> windowManagers = vcellClient.getMdiManager().getWindowManagers();

		while (windowManagers.hasMoreElements()) {
			windowManager = windowManagers.nextElement();
			Simulation[] simulations = null;
			VCDocument modelDocument = null;
			if (windowManager instanceof BioModelWindowManager){
				BioModelWindowManager selectedBioWindowManager = (BioModelWindowManager)windowManager;
				BioModel bioModel = selectedBioWindowManager.getBioModel();
				simulations = bioModel.getSimulations();
				modelDocument = bioModel;
			}else if (windowManager instanceof MathModelWindowManager){
				MathModelWindowManager selectedMathWindowManager = (MathModelWindowManager)windowManager; 
				MathModel mathModel = selectedMathWindowManager.getMathModel();
				simulations = mathModel.getSimulations();
				modelDocument = mathModel;
			}
			if (simulations != null){
				for (Simulation simulation : simulations){
				    SimulationInfo simInfo=simulation.getSimulationInfo();
					SimulationStatus simStatus = vcellClient.getRequestManager().getServerSimulationStatus(simInfo);
					if (simStatus!=null && simStatus.getHasData()){
						SimulationDataSetRef simulationDataSetReference = new SimulationDataSetRef();
						simulationDataSetReference.setSimName(simInfo.getName());
						simulationDataSetReference.setSimId(simInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey().toString());
						simulationDataSetReference.setModelId(modelDocument.getVersion().getVersionKey().toString());
						simulationDataSetRefs.add(simulationDataSetReference);
					}
				}
			}
		}
		return simulationDataSetRefs;
	}
	


	@Override
	public List<VariableInfo> getVariableList(
			SimulationDataSetRef simulationDataSetRef)
			throws cbit.vcell.client.pyvcellproxy.DataAccessException,
			TException {

		User user = vcellClient.getClientServerManager().getUser();
		KeyValue simKeyValue = new KeyValue(simulationDataSetRef.simId);
		VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
		
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
			
			DomainType domainType = null;
			
			switch (dataIdentifiers[ii].getVariableType().getVariableDomain()){
			case VARIABLEDOMAIN_MEMBRANE:{
				domainType = DomainType.MEMBRANE;
				break;
			}
			case VARIABLEDOMAIN_VOLUME:{
				domainType = DomainType.VOLUME;
				break;
			}
			default:{
				break;
			}
			}
			varInfo.setVariableDomainType(domainType);
			varInfo.setUnitsLabel(dataIdentifiers[ii].getVariableType().getDefaultUnits());
			variableInfos.add(varInfo);
			
		}
		return variableInfos;
	}


	/**
	 * Exports all simulation results to a zip file ... no caching of results here.
	 * 
	 * @param simulationDataSetRef
	 * @return zip file containing all results
	 * @throws org.vcell.util.DataAccessException 
	 * @throws TException
	 * 
	 * Deprecated - implements
	 */
	@Deprecated
	private File exportAllRequest_internal(SimulationDataSetRef simulationDataSetRef) throws org.vcell.util.DataAccessException {

		User user = vcellClient.getClientServerManager().getUser();		
		KeyValue simKeyValue = new KeyValue(simulationDataSetRef.getSimId());
		VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
		VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
		double dataTimes[] = null;
				
		DataIdentifier[] dataIdentifiers =  vcellClient.getRequestManager().getDataManager(null, vcSimulationDataIdentifier, true).getDataIdentifiers();
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
		
		ExportSpecs exportSpecs = new ExportSpecs(vcSimulationDataIdentifier, ExportConstants.FORMAT_VTK_UNSTRUCT, variableSpecs, timeSpecs, geometrySpecs, formatSpecificSpecs, simulationDataSetRef.simName, null);
	
		try {
			OutputContext outputContext = null;
			ExportEvent event = vcellClient.getClientServerManager().getDataSetController().makeRemoteFile(outputContext,exportSpecs);
			System.out.println("Export location = "+event.getLocation());
			String[] urlSplit = event.getLocation().split("/");
			File exportFile = new File(new File(PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirProperty)), urlSplit[urlSplit.length - 1]);
			System.out.println("ExportedPath = "+exportFile);
			return exportFile;
			// ignore; we'll get two downloads otherwise... getClientServerManager().getAsynchMessageManager().fireExportEvent(event);
		} catch (org.vcell.util.DataAccessException | RemoteException exc) {
			exc.printStackTrace();
			throw new RuntimeException(exc.getMessage());
		} 
	}

	@Override
	public String getDataSetFileOfDomainAtTimeIndex(SimulationDataSetRef simulationDataSetRef, String domainName, int timeIndex) throws DataAccessException {
		
		//
		// create the dataset directory if necessary
		//
		File vtuSimDataFolder = new File(localVisDataDir,simulationDataSetRef.getSimId());
		if (!(vtuSimDataFolder.exists() && vtuSimDataFolder.isDirectory())){
			vtuSimDataFolder.mkdirs();
		}
		
		//
		// compose the specific mesh filename
		//
		String timeIndexStr = String.format("%06d" , timeIndex);
		File vtuDataFile = new File(vtuSimDataFolder, "SimID_"+simulationDataSetRef.getSimId()+"_0_"+simulationDataSetRef.getSimId()+"_0_"+domainName+"_"+timeIndexStr+".vtu");
		System.out.println("looking for file: "+vtuDataFile);
		
		//
		// if requested mesh file is not found, do full export and unzip into the dataset directory
		//
		if (!vtuDataFile.exists()){
			try {
				//WAY OVERKILL FOR NOW:
				System.out.println("Didn't find data. Exporting....");
				File exportedZipFile = exportAllRequest_internal(simulationDataSetRef);
				
				System.out.println("exportedZipFile = "+exportedZipFile);
				File copiedZipFile = new File(vtuSimDataFolder, simulationDataSetRef.getSimId()+".zip");
				FileUtils.copyFile(exportedZipFile, copiedZipFile);
				info.aduna.io.ZipUtil.extract(copiedZipFile, vtuSimDataFolder);
				
				if (!vtuDataFile.exists()){
					System.out.println("after export, couldn't find requested mesh file "+vtuDataFile);
					throw new DataAccessException("after export, couldn't find requested mesh file "+vtuDataFile);
				}
				System.out.println("vtuData file exists, " + vtuDataFile);

			}catch (IOException | org.vcell.util.DataAccessException e){
				e.printStackTrace();
				System.out.println("failed to export entire dataset: "+e.getMessage());
				throw new DataAccessException("failed to export entire dataset: "+e.getMessage());
			}
		}
		
		try {
			return vtuDataFile.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataAccessException("failed to retrieve cannonical file path for "+vtuDataFile);
		}
	}
	
	private VCSimulationDataIdentifier getVCSimulationDataIdentifier(SimulationDataSetRef simulationDataSetRef){
		User user = vcellClient.getClientServerManager().getUser();		
		KeyValue simKeyValue = new KeyValue(simulationDataSetRef.getSimId());
		VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
		VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
		
		return vcSimulationDataIdentifier;
	}

	@Override
	public List<Double> getTimePoints(SimulationDataSetRef simulationDataSetRef) throws DataAccessException, TException {
		VCSimulationDataIdentifier vcSimulationDataIdentifier = getVCSimulationDataIdentifier(simulationDataSetRef);

		ArrayList<Double> timesList = new ArrayList<Double>();
		try {
			if (vcSimulationDataIdentifier != null) {				
				double[] timesArray = vcellClient.getRequestManager().getDataManager(null, vcSimulationDataIdentifier, true).getDataSetTimes();
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
