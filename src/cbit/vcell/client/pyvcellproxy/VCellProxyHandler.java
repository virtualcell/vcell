package cbit.vcell.client.pyvcellproxy;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.thrift.TException;
import org.vcell.util.FileUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuFileContainer.VtuVarInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.data.DataProcessingResultsPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.NewClientPDEDataContext;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationOwner;
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
public List<SimulationDataSetRef> getSimsFromOpenModels() throws cbit.vcell.client.pyvcellproxy.ThriftDataAccessException, org.apache.thrift.TException {
	ArrayList<SimulationDataSetRef> simulationDataSetRefs = new ArrayList<SimulationDataSetRef>();
	TopLevelWindowManager windowManager = null;
	Enumeration<TopLevelWindowManager> windowManagers = vcellClient.getMdiManager().getWindowManagers();

	while (windowManagers.hasMoreElements()) {
		windowManager = windowManagers.nextElement();
		Simulation[] simulations = null;
		VCDocument modelDocument = null;
		int simContextCount = 0;
		
		if (windowManager instanceof BioModelWindowManager){
			BioModelWindowManager selectedBioWindowManager = (BioModelWindowManager)windowManager;
			BioModel bioModel = selectedBioWindowManager.getBioModel();
			simulations = bioModel.getSimulations();
			modelDocument = bioModel;
			simContextCount = bioModel.getNumSimulationContexts();
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
				for (int simContextIndex = 0; simContextIndex<simContextCount; simContextIndex++){
					for (int jobIndex = 0; jobIndex<=simulation.getScanCount(); jobIndex++){
						if (simStatus!=null && simStatus.getHasData()){
							SimulationDataSetRef simulationDataSetReference = new SimulationDataSetRef();
							simulationDataSetReference.setSimName(simInfo.getName());
							String simName = simInfo.getName();
							if (jobIndex!=0){
								simName = simName + " job#"+String.valueOf(jobIndex);
							}
							
							simulationDataSetReference.setSimId(simInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey().toString());
							simulationDataSetReference.setModelId(modelDocument.getVersion().getVersionKey().toString());
							simulationDataSetReference.setUsername(simInfo.getVersion().getOwner().getName());
							simulationDataSetReference.setUserkey(simInfo.getVersion().getOwner().getID().toString());
							simulationDataSetReference.setIsMathModel(modelDocument instanceof MathModel);
							simulationDataSetReference.setJobIndex(jobIndex);
							if (modelDocument instanceof BioModel){
								BioModel bm = (BioModel) modelDocument; 
								simulationDataSetReference.setSimulationContextName(bm.getSimulationContext(simContextIndex).getName());
								if (simContextIndex!=0) {
									simName = simName + " simContext#"+String.valueOf(simContextIndex);
								}
							}
							simulationDataSetReference.setSimName(simInfo.getName());
							simulationDataSetRefs.add(simulationDataSetReference);
						
						}
					}
				}
			}
		}
	}
	return simulationDataSetRefs;
}



@Override
public List<VariableInfo> getVariableList(SimulationDataSetRef simulationDataSetRef)
		throws cbit.vcell.client.pyvcellproxy.ThriftDataAccessException {

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
public String getDataSetFileOfDomainAtTimeIndex(SimulationDataSetRef simulationDataSetRef, String domainName, int timeIndex) throws ThriftDataAccessException {
	
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
				throw new ThriftDataAccessException("after export, couldn't find requested mesh file "+vtuDataFile);
			}
			System.out.println("vtuData file exists, " + vtuDataFile);

		}catch (IOException e){
			e.printStackTrace();
			System.out.println("failed to export entire dataset: "+e.getMessage());
			throw new ThriftDataAccessException("failed to export entire dataset: "+e.getMessage());
		}
	}
	
	try {
		return vtuDataFile.getCanonicalPath();
	} catch (IOException e) {
		e.printStackTrace();
		throw new ThriftDataAccessException("failed to retrieve cannonical file path for "+vtuDataFile);
	}
}

private VtuFileContainer getVtuFileContainer(SimulationDataSetRef simulationDataSetRef, int timeIndex) throws ThriftDataAccessException {
	try {
		VCSimulationDataIdentifier vcSimulationDataIdentifier = getVCSimulationDataIdentifier(simulationDataSetRef);
		PDEDataManager pdeDataManager = (PDEDataManager)vcellClient.getRequestManager().getDataManager(null, vcSimulationDataIdentifier, true);
		List<Double> times = getTimePoints(simulationDataSetRef);
		double time = times.get(timeIndex);
		VtuFileContainer vtuFileContainer = pdeDataManager.getVtuMeshFiles(time);
		return vtuFileContainer;
	}catch (Exception e){
		e.printStackTrace();
		throw new ThriftDataAccessException("failed to get data for simulation "+simulationDataSetRef+" at time index "+timeIndex+": "+e.getMessage());
	}
}

private VCSimulationDataIdentifier getVCSimulationDataIdentifier(SimulationDataSetRef simulationDataSetRef){
	User user = new User(simulationDataSetRef.getUsername(),new KeyValue(simulationDataSetRef.getUserkey()));
	KeyValue simKeyValue = new KeyValue(simulationDataSetRef.getSimId());
	VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
	VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, simulationDataSetRef.getJobIndex());
	
	return vcSimulationDataIdentifier;
}

@Override
public List<Double> getTimePoints(SimulationDataSetRef simulationDataSetRef) throws ThriftDataAccessException {
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

@Override
public PostProcessingData getPostProcessingData(
		SimulationDataSetRef simulationDataSetRef)
		throws ThriftDataAccessException, TException {
	return getMockPostProcessingData(simulationDataSetRef);
	//return null;
}

private PostProcessingData getMockPostProcessingData(SimulationDataSetRef simulationDataSetRef) throws ThriftDataAccessException {
	PostProcessingData mockPostProcessingData = new PostProcessingData();
	mockPostProcessingData.setVariableList(getVariableList(simulationDataSetRef));
	System.out.println("Var list size should be "+mockPostProcessingData.getVariableListSize());
	PlotData mockPlotData = new PlotData();
	ArrayList<Double> timePoints = (ArrayList<Double>) getTimePoints(simulationDataSetRef);
	mockPlotData.setTimePoints(timePoints);
	
	ArrayList<Double> mockData = new ArrayList<Double>();
	ListIterator<Double> listIterator = timePoints.listIterator();
	while (listIterator.hasNext()) {
		mockData.add(new Double(2+java.lang.Math.sin(listIterator.next().doubleValue())));
	}
	mockPlotData.setData(mockData);
	mockPostProcessingData.setPlotData(new ArrayList<PlotData>());
	ListIterator<VariableInfo> variableIter = mockPostProcessingData.getVariableList().listIterator();
	while (variableIter.hasNext()){
		VariableInfo var = variableIter.next();
		mockPostProcessingData.getPlotData().add(mockPlotData);
	}
	return mockPostProcessingData;		 
}

private static class FindWindow {
	public static Window getWindow(Component component) {
		if (component == null) {
			return JOptionPane.getRootFrame();
		} else if (component instanceof Window) {
			return (Window) component;
		} else {
			return getWindow(component.getParent());
		}
	}
}
	
@Override
public void displayPostProcessingDataInVCell(SimulationDataSetRef simulationDataSetRef)throws ThriftDataAccessException, TException {
	 try {
		 User vcUser = vcellClient.getRequestManager().getDocumentManager().getUser();
		 VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(new KeyValue(simulationDataSetRef.getSimId()), vcUser );
		 ClientDocumentManager clientDocumentManager = (ClientDocumentManager) vcellClient.getClientServerManager().getDocumentManager();
		 SimulationOwner simulationOwner = null;
		 try {
			 if(simulationDataSetRef.isMathModel){
				 simulationOwner = clientDocumentManager.getMathModel(new KeyValue(simulationDataSetRef.getModelId()));
			 }else{
				 BioModel bioModel = clientDocumentManager.getBioModel(new KeyValue(simulationDataSetRef.getModelId()));
				 simulationOwner = bioModel.getSimulationContext(simulationDataSetRef.getSimulationContextName());
			 }
		 } catch (Exception e1) {
				e1.printStackTrace();
				throw new ThriftDataAccessException(e1.getMessage());
		 }
		 ArrayList<AnnotatedFunction> outputFunctionsList = simulationOwner.getOutputFunctionContext().getOutputFunctionsList();
		 OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
		 VCSimulationDataIdentifier vcSimDataId = new VCSimulationDataIdentifier(vcSimId, simulationDataSetRef.getJobIndex());
		 PDEDataManager pdeDataManager = null;
		 try {
			 pdeDataManager = (PDEDataManager)vcellClient.getRequestManager().getDataManager(outputContext, vcSimDataId , true);
		} catch (org.vcell.util.DataAccessException e1) {
			e1.printStackTrace();
			throw new ThriftDataAccessException(e1.getMessage());
		}
		 final NewClientPDEDataContext newClientPDEDataContext = pdeDataManager.getPDEDataContext();
		
		Enumeration<TopLevelWindowManager> windowManagers = vcellClient.getMdiManager().getWindowManagers();
		final Window window = FindWindow.getWindow(windowManagers.nextElement().getComponent());
	
		AsynchClientTask task = new AsynchClientTask("Display Post Processing Statistics", AsynchClientTask.TASKTYPE_SWING_NONBLOCKING,false,false) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				DataProcessingResultsPanel dataProcessingResultsPanel = new DataProcessingResultsPanel();
				dataProcessingResultsPanel.update(newClientPDEDataContext);
				DialogUtils.showComponentOKCancelDialog(window, dataProcessingResultsPanel, "Post Processing Statistics");			
			}
		};
		Hashtable<String, Object> hash = new Hashtable<String, Object>();
		Vector<AsynchClientTask> tasksV = new Vector<AsynchClientTask>();
		tasksV.add(task);
		AsynchClientTask[] tasks = new AsynchClientTask[tasksV.size()];
		tasksV.copyInto(tasks);
		ClientTaskDispatcher.dispatch(window, hash, tasks, true);
	} catch (Throwable exc2) {
		 exc2.printStackTrace();
		 throw new ThriftDataAccessException(exc2.getMessage());
	}
}

double[] ListOfDoublesToPrimitiveArrayOfdoubles(List<Double> arrayListOfDoubles){
	ListIterator<Double> listIter = arrayListOfDoubles.listIterator();
	double[] doubles = new double[arrayListOfDoubles.size()];
	int i = 0;
	while (listIter.hasNext()){
		doubles[i] = listIter.next().doubleValue();
	}
	return doubles;
}
}