package cbit.vcell.client.data;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.VCAssert;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ClientSimManager.LocalVCSimulationDataIdentifier;
import cbit.vcell.client.LocalDataSetControllerProvider;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.pyvcellproxy.SimulationDataSetRef;
import cbit.vcell.client.pyvcellproxy.VCellClientDataService;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.simdata.VtkManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class VCellClientDataServiceImpl implements VCellClientDataService {
		private final VCellClient vcellClient;
		
		public VCellClientDataServiceImpl(VCellClient vcellClient){
			this.vcellClient = vcellClient;
		}
		private boolean isVtkSupported(Simulation simulation){
			if (simulation.getMathDescription().getGeometry().getDimension()<2){
				return false;
			}
			return true;
		}

		@Override
		public List<SimulationDataSetRef> getSimsFromOpenModels() {
			ArrayList<SimulationDataSetRef> simulationDataSetRefs = new ArrayList<SimulationDataSetRef>();

			for ( TopLevelWindowManager windowManager :  vcellClient.getMdiManager().getWindowManagers() ) {
				Simulation[] simulations = null;
				VCDocument modelDocument = null;
				
				if (windowManager instanceof BioModelWindowManager){
					BioModelWindowManager selectedBioWindowManager = (BioModelWindowManager)windowManager;
					BioModel bioModel = selectedBioWindowManager.getBioModel();
					simulations = bioModel.getSimulations();
					modelDocument = bioModel;
//					simOwnerCount = bioModel.getNumSimulationContexts();
					
				}else if (windowManager instanceof MathModelWindowManager){
					MathModelWindowManager selectedMathWindowManager = (MathModelWindowManager)windowManager; 
					MathModel mathModel = selectedMathWindowManager.getMathModel();
					simulations = mathModel.getSimulations();
					modelDocument = mathModel;
//					simOwnerCount = 1;
				}
				if (simulations != null){
					for (Simulation simulation : simulations){
						if (!isVtkSupported(simulation)){
							continue;
						}
						Origin origin = simulation.getMathDescription().getGeometry().getOrigin();
						Extent extent = simulation.getMathDescription().getGeometry().getExtent();
					    SimulationInfo simInfo=simulation.getSimulationInfo();
						SimulationStatus simStatus = vcellClient.getRequestManager().getServerSimulationStatus(simInfo);
						for (int jobIndex = 0; jobIndex<simulation.getScanCount(); jobIndex++){
							if (simStatus!=null && simStatus.getHasData()){
								SimulationDataSetRef simulationDataSetReference = VCellClientDataServiceImpl.createSimulationDataSetRef(simulation, modelDocument, jobIndex, false);
								simulationDataSetRefs.add(simulationDataSetReference);
							}
						}
					}
				}
			}
			File localSimDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());
			String[] simtaskFilenames = localSimDir.list((dir,name) -> (name.endsWith(".simtask.xml")));
			for (String simtaskFilename : simtaskFilenames){
				try {
					SimulationTask simTask = XmlHelper.XMLToSimTask(org.apache.commons.io.FileUtils.readFileToString(new File(localSimDir,simtaskFilename)));
					VCDocument modelDocument = null;
					SimulationDataSetRef simulationDataSetReference = VCellClientDataServiceImpl.createSimulationDataSetRef(simTask.getSimulation(), modelDocument, simTask.getSimulationJob().getJobIndex(), true);
					simulationDataSetRefs.add(simulationDataSetReference);
				}catch (ExpressionException | XmlParseException | IOException e){
					e.printStackTrace();
				}
			}
			return simulationDataSetRefs;
		}
		
		@Override
		public VtkManager getVtkManager(SimulationDataSetRef simulationDataSetRef) throws FileNotFoundException, DataAccessException {
			VCSimulationDataIdentifier vcSimulationDataIdentifier = getVCSimulationDataIdentifier(simulationDataSetRef);
			VtkManager vtkManager = null;
			if (!simulationDataSetRef.isLocal) {
				vtkManager = vcellClient.getRequestManager().getVtkManager(null,vcSimulationDataIdentifier);
			} else {
				// ---- preliminary : construct the localDatasetControllerProvider
				File primaryDir = ResourceUtil.getLocalRootDir();
				User usr = User.tempUser;
				DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(null,primaryDir,null);
				ExportServiceImpl localExportServiceImpl = new ExportServiceImpl();
				LocalDataSetControllerProvider localDSCProvider = new LocalDataSetControllerProvider(usr, dataSetControllerImpl, localExportServiceImpl);
				VCDataManager vcDataManager = new VCDataManager(localDSCProvider);
				vtkManager = new VtkManager(null, vcDataManager, vcSimulationDataIdentifier);
			}
			return vtkManager;
		}

		public VCSimulationDataIdentifier getVCSimulationDataIdentifier(SimulationDataSetRef simulationDataSetRef) throws FileNotFoundException{
			User user = new User(simulationDataSetRef.getUsername(),new KeyValue(simulationDataSetRef.getUserkey()));
			KeyValue simKeyValue = new KeyValue(simulationDataSetRef.getSimId());
			VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKeyValue, user);
			if (simulationDataSetRef.isIsLocal()){
				File primaryDir = ResourceUtil.getLocalRootDir();
				DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(null,primaryDir,null);
				ExportServiceImpl localExportServiceImpl = new ExportServiceImpl();
				LocalDataSetControllerProvider localDSCProvider = new LocalDataSetControllerProvider(user, dataSetControllerImpl, localExportServiceImpl);
				VCDataManager vcDataManager = new VCDataManager(localDSCProvider);
				File localSimDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());
				VCSimulationDataIdentifier simulationDataIdentifier = new LocalVCSimulationDataIdentifier(vcSimulationIdentifier, 0, localSimDir);
				return simulationDataIdentifier;
			}else{
				VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, simulationDataSetRef.getJobIndex());
				return vcSimulationDataIdentifier;
			}
		}

		@Override
		public void displayPostProcessingDataInVCell(SimulationDataSetRef simulationDataSetRef) throws NumberFormatException, DataAccessException {
				 User vcUser = vcellClient.getRequestManager().getDocumentManager().getUser();
				 VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(new KeyValue(simulationDataSetRef.getSimId()), vcUser );
				 ClientDocumentManager clientDocumentManager = (ClientDocumentManager) vcellClient.getClientServerManager().getDocumentManager();
				 SimulationOwner simulationOwner = null;

					 if(simulationDataSetRef.isMathModel){
						 simulationOwner = clientDocumentManager.getMathModel(new KeyValue(simulationDataSetRef.getModelId()));
					 }else{
						 BioModel bioModel = clientDocumentManager.getBioModel(new KeyValue(simulationDataSetRef.getModelId()));
						 simulationOwner = bioModel.getSimulationContext(simulationDataSetRef.getSimulationContextName());
					 }

				 ArrayList<AnnotatedFunction> outputFunctionsList = simulationOwner.getOutputFunctionContext().getOutputFunctionsList();
				 OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
				 VCSimulationDataIdentifier vcSimDataId = new VCSimulationDataIdentifier(vcSimId, simulationDataSetRef.getJobIndex());
				 PDEDataManager pdeDataManager = (PDEDataManager)vcellClient.getRequestManager().getDataManager(outputContext, vcSimDataId , true);
				 final ClientPDEDataContext newClientPDEDataContext = pdeDataManager.getPDEDataContext();
				
//				 this was the code before the windows refactoring; appears to just always get the first window???
//				Enumeration<TopLevelWindowManager> windowManagers = vcellClient.getMdiManager().getWindowManagers();
//				final Window window = FindWindow.getWindow(windowManagers.nextElement().getComponent());
				 
				Optional<TopLevelWindowManager> first = vcellClient.getMdiManager().getWindowManagers().stream( ).findFirst();
				VCAssert.assertTrue(first.isPresent(), "window manager not present?" );
				
				final Window window = getWindow(first.get().getComponent());
			
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
		}

		private Window getWindow(Component component) {
			if (component == null) {
				return JOptionPane.getRootFrame();
			} else if (component instanceof Window) {
				return (Window) component;
			} else {
				return getWindow(component.getParent());
			}
		}
		

		public static SimulationDataSetRef createSimulationDataSetRef(Simulation simulation, VCDocument modelDocument, int jobIndex, boolean isLocal){
			SimulationDataSetRef simulationDataSetReference = new SimulationDataSetRef();
			Origin origin = simulation.getMathDescription().getGeometry().getOrigin();
			Extent extent = simulation.getMathDescription().getGeometry().getExtent();
		    SimulationInfo simInfo=simulation.getSimulationInfo();
			simulationDataSetReference.setSimName(simInfo.getName());
		//	String simName = simInfo.getName();
		//	if (jobIndex!=0){
		//		simName = simName + " job#"+String.valueOf(jobIndex);
		//	}
			final String modelId;
			final boolean isMathModel;
			final String modelName;
			final String simContextName;
			
			if (modelDocument!=null){
				modelId = modelDocument.getVersion().getVersionKey().toString();
				isMathModel = (modelDocument instanceof MathModel);
				modelName = modelDocument.getName();
				if (modelDocument instanceof BioModel && simulation.getSimulationOwner() != null){
					simContextName = simulation.getSimulationOwner().getName();
				}else{
					simContextName = null;
				}
			}else{
				modelId = "no id";
				isMathModel = false;
				modelName = "no model";
				simContextName = null;
			}
			simulationDataSetReference.setSimId(simInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey().toString());
			simulationDataSetReference.setModelId(modelId);
			simulationDataSetReference.setUsername(simInfo.getVersion().getOwner().getName());
			simulationDataSetReference.setUserkey(simInfo.getVersion().getOwner().getID().toString());
			simulationDataSetReference.setIsMathModel(isMathModel);
			simulationDataSetReference.setJobIndex(jobIndex);
			simulationDataSetReference.setModelName(modelName);
			simulationDataSetReference.setOriginXYZ(Arrays.asList(new Double[] {origin.getX(),origin.getY(),origin.getZ()}));
			simulationDataSetReference.setExtentXYZ(Arrays.asList(new Double[] {extent.getX(),extent.getY(),extent.getZ()}));
			if (simContextName!=null){
				simulationDataSetReference.setSimulationContextName(simContextName);
			}
			simulationDataSetReference.setSimName(simInfo.getName());
			boolean movingBoundarySolver = simulation.getSolverTaskDescription().getSolverDescription().isMovingBoundarySolver();
			simulationDataSetReference.setIsTimeVaryingMesh(movingBoundarySolver);
			simulationDataSetReference.setIsLocal(isLocal);
			return simulationDataSetReference;
		}

		
	}