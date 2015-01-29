package cbit.vcell.client.pyvcellproxy;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.apache.thrift.TException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuFileContainer.VtuMesh;
import org.vcell.vis.io.VtuFileContainer.VtuVarInfo;

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
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class VCellProxyHandler implements VCellProxy.Iface{

	private final VCellClient vcellClient;
	private final File localVisDataDir;
	private final HashMap<SimulationDataSetRef, List<VariableInfo>> varInfoHash = new HashMap<SimulationDataSetRef, List<VariableInfo>>();
	
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
	public List<VariableInfo> getVariableList(SimulationDataSetRef simulationDataSetRef)
			throws cbit.vcell.client.pyvcellproxy.DataAccessException {

		if (varInfoHash.containsKey(simulationDataSetRef)){
			ArrayList<VariableInfo> varInfoList = new ArrayList<VariableInfo>();
			for (VariableInfo varInfo : varInfoHash.get(simulationDataSetRef)){
				varInfoList.add(varInfo);
			}
			return varInfoList;
		}else{
			VtuFileContainer vtuFileContainer = getVtuFileContainer(simulationDataSetRef, 0);
			ArrayList<VariableInfo> varInfoList = new ArrayList<VariableInfo>();
			for (VtuVarInfo vtuVarInfo : vtuFileContainer.getVtuVarInfos()){
				DomainType variableDomainType = null;
				switch (vtuVarInfo.variableDomain){
					case VARIABLEDOMAIN_MEMBRANE:{
						variableDomainType = DomainType.MEMBRANE;
						break;
					}
					case VARIABLEDOMAIN_VOLUME:{
						variableDomainType = DomainType.VOLUME;
						break;
					}
					default:{
						break;
					}
				}
				String unitsLabel = "<unknown unit>";
				varInfoList.add(new VariableInfo(vtuVarInfo.name, vtuVarInfo.displayName, vtuVarInfo.domainName, variableDomainType, unitsLabel));
				varInfoHash.put(simulationDataSetRef,varInfoList);
			}
			return varInfoList;
		}
	}


	
	private File getMeshFileLocation(SimulationDataSetRef simulationDataSetRef, String domainName, int timeIndex){
		
		VCSimulationDataIdentifier vcSimulationDataIdentifier = getVCSimulationDataIdentifier(simulationDataSetRef);
		
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
		File vtuDataFile = new File(vtuSimDataFolder, vcSimulationDataIdentifier.getID()+"_"+domainName+"_"+timeIndexStr+".vtu");
		return vtuDataFile;
	}

	@Override
	public String getDataSetFileOfDomainAtTimeIndex(SimulationDataSetRef simulationDataSetRef, String domainName, int timeIndex) throws DataAccessException {
		
		File vtuDataFile = getMeshFileLocation(simulationDataSetRef, domainName, timeIndex);

		System.out.println("looking for file: "+vtuDataFile);
		
		//
		// if requested mesh file is not found, get files for this timepoint
		//
		if (!vtuDataFile.exists()){
			try {
				//WAY OVERKILL FOR NOW:
				System.out.println("Didn't find data. Exporting....");
				VtuFileContainer vtuFileContainer = getVtuFileContainer(simulationDataSetRef, timeIndex);
				for (VtuFileContainer.VtuMesh mesh : vtuFileContainer.getVtuMeshes()){
					FileUtils.writeByteArrayToFile(mesh.vtuMeshContents, getMeshFileLocation(simulationDataSetRef, mesh.domainName, timeIndex));
				}
				
				if (!vtuDataFile.exists()){
					System.out.println("after export, couldn't find requested mesh file "+vtuDataFile);
					throw new DataAccessException("after export, couldn't find requested mesh file "+vtuDataFile);
				}
				System.out.println("vtuData file exists, " + vtuDataFile);

			}catch (IOException e){
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

	private VtuFileContainer getVtuFileContainer(SimulationDataSetRef simulationDataSetRef, int timeIndex) throws DataAccessException {
		try {
			VCSimulationDataIdentifier vcSimulationDataIdentifier = getVCSimulationDataIdentifier(simulationDataSetRef);
			PDEDataManager pdeDataManager = (PDEDataManager)vcellClient.getRequestManager().getDataManager(null, vcSimulationDataIdentifier, true);
			List<Double> times = getTimePoints(simulationDataSetRef);
			double time = times.get(timeIndex);
			VtuFileContainer vtuFileContainer = pdeDataManager.getVtuMeshFiles(time);
			return vtuFileContainer;
		}catch (org.vcell.util.DataAccessException e){
			e.printStackTrace();
			throw new DataAccessException("failed to get data for simulation "+simulationDataSetRef+" at time index "+timeIndex+": "+e.getMessage());
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
	public List<Double> getTimePoints(SimulationDataSetRef simulationDataSetRef) throws DataAccessException {
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
