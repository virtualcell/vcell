package cbit.vcell.client.data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vcell.util.DataAccessException;
import org.vcell.util.DataJobListenerHolder;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.plot.PlotData;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.DataViewerManager;
import cbit.vcell.client.server.DataSetControllerProvider;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskDispatcher.BlockingTimer;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.VariableType;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputInfoOP;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo.PostProcessDataType;
import cbit.vcell.simdata.DataSetMetadata;
import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.PDEDataInfo;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;

public class PDEDataViewerPostProcess extends JPanel implements DataJobListener{

	private PDEDataViewer postProcessPDEDataViewer;
	
	public static PostProcessDataPDEDataContext createPostProcessPDEDataContext(final ClientPDEDataContext parentPDEDataContext) throws Exception{
		final DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo = (DataOperationResults.DataProcessingOutputInfo)
				parentPDEDataContext.doDataOperation(new DataOperation.DataProcessingOutputInfoOP(parentPDEDataContext.getVCDataIdentifier(),false,parentPDEDataContext.getDataManager().getOutputContext()));

		if(dataProcessingOutputInfo == null){
			return null;
		}
		DataSetControllerProvider dataSetControllerProvider = new DataSetControllerProvider() {
			@Override
			public DataSetController getDataSetController() throws DataAccessException {
				return new DataSetController() {
//					DataIdentifier[] dataIdentifiers;
					
					@Override
					public ExportEvent makeRemoteFile(OutputContext outputContext,ExportSpecs exportSpecs) throws DataAccessException,RemoteException {
						throw new DataAccessException("Not implemented");
					}
					
					@Override
					public TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext, VCDataIdentifier vcdataID,TimeSeriesJobSpec timeSeriesJobSpec) throws RemoteException,DataAccessException {
//						return parentPDEDataContext.getDataManager().getTimeSeriesValues(timeSeriesJobSpec);
						DataOperation.DataProcessingOutputTimeSeriesOP dataProcessingOutputTimeSeriesOP =
								new DataOperation.DataProcessingOutputTimeSeriesOP(vcdataID, timeSeriesJobSpec,outputContext,getDataSetTimes(vcdataID));
						DataOperationResults.DataProcessingOutputTimeSeriesValues dataopDataProcessingOutputTimeSeriesValues =
								(DataOperationResults.DataProcessingOutputTimeSeriesValues)parentPDEDataContext.doDataOperation(dataProcessingOutputTimeSeriesOP);
						return dataopDataProcessingOutputTimeSeriesValues.getTimeSeriesJobResults();
					}
					
					@Override
					public SimDataBlock getSimDataBlock(OutputContext outputContext,VCDataIdentifier vcdataID, String varName, double time) throws RemoteException, DataAccessException {
//						return parentPDEDataContext.getDataManager().getSimDataBlock(varName, time);
						DataOperationResults.DataProcessingOutputDataValues dataProcessingOutputValues = (DataOperationResults.DataProcessingOutputDataValues)
								parentPDEDataContext.doDataOperation(new DataOperation.DataProcessingOutputDataValuesOP(vcdataID, varName, TimePointHelper.createSingleTimeTimePointHelper(time),DataIndexHelper.createAllDataIndexesDataIndexHelper(),outputContext,null));
						PDEDataInfo pdeDataInfo = new PDEDataInfo(vcdataID.getOwner(), vcdataID.getID(), varName, time, Long.MIN_VALUE);
						SimDataBlock simDataBlock = new SimDataBlock(pdeDataInfo, dataProcessingOutputValues.getDataValues()[0], VariableType.POSTPROCESSING);
						return simDataBlock;
					}
					
					@Override
					public boolean getParticleDataExists(VCDataIdentifier vcdataID)
							throws DataAccessException, RemoteException {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdataID,
							double time) throws DataAccessException, RemoteException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public ODESimData getODEData(VCDataIdentifier vcdataID)
							throws DataAccessException, RemoteException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public CartesianMesh getMesh(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
						return null;//throw new DataAccessException("PostProcessData mesh not available at this level");
					}
					
					@Override
					public PlotData getLineScan(OutputContext outputContext,VCDataIdentifier vcdataID, String variable, double time,SpatialSelection spatialSelection) throws RemoteException,DataAccessException {
						throw new DataAccessException("Remote getLineScan method should not be called for PostProcess");
					}
					
					@Override
					public AnnotatedFunction[] getFunctions(OutputContext outputContext,VCDataIdentifier vcdataID) throws DataAccessException,RemoteException {
						return outputContext.getOutputFunctions();
					}
					
					@Override
					public double[] getDataSetTimes(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
						return dataProcessingOutputInfo.getVariableTimePoints();
					}
					
					@Override
					public DataSetTimeSeries getDataSetTimeSeries(VCDataIdentifier vcdataID,
							String[] variableNames) throws DataAccessException, RemoteException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public DataSetMetadata getDataSetMetadata(VCDataIdentifier vcdataID)
							throws DataAccessException, RemoteException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,VCDataIdentifier vcdataID) throws RemoteException,DataAccessException {
//						return parentPDEDataContext.getDataIdentifiers();
						ArrayList<DataIdentifier> postProcessDataIDs = new ArrayList<DataIdentifier>();
						if(outputContext != null && outputContext.getOutputFunctions() != null){
							for (int i = 0; i < outputContext.getOutputFunctions().length; i++) {
								if(outputContext.getOutputFunctions()[i].isPostProcessFunction()){
									postProcessDataIDs.add(new DataIdentifier(outputContext.getOutputFunctions()[i].getName(), VariableType.POSTPROCESSING,null, false, outputContext.getOutputFunctions()[i].getDisplayName()));
								}
							}
						}
//						//get output functions
//						DataIdentifier[] parentDataIdentifiers = parentPDEDataContext.getDataIdentifiers();//parent pdedatacontext
//						for (int i = 0; i < parentDataIdentifiers.length; i++) {
//							if(parentDataIdentifiers[i].isFunction() && parentDataIdentifiers[i].getVariableType().equals(VariableType.POSTPROCESSING)){
//								if(!postProcessDataIDs.contains(parentDataIdentifiers[i])){
//									postProcessDataIDs.add(parentDataIdentifiers[i]);
//								}
//							}
//						}
						//get 'state' variables
						for (int i = 0; i < dataProcessingOutputInfo.getVariableNames().length; i++) {
							if(dataProcessingOutputInfo.getPostProcessDataType(dataProcessingOutputInfo.getVariableNames()[i]).equals(PostProcessDataType.image)){
								DataIdentifier dataIdentifier = new DataIdentifier(dataProcessingOutputInfo.getVariableNames()[i], VariableType.POSTPROCESSING,null, false, dataProcessingOutputInfo.getVariableNames()[i]);
								postProcessDataIDs.add(dataIdentifier);
							}
						}
						if(postProcessDataIDs.size() > 0){
							return postProcessDataIDs.toArray(new DataIdentifier[0]);
						}
						return null;
					}
					
					@Override
					public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec)
							throws RemoteException, DataAccessException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public DataOperationResults doDataOperation(DataOperation dataOperation)
							throws DataAccessException, RemoteException {
						// TODO Auto-generated method stub
						return parentPDEDataContext.doDataOperation(dataOperation);
					}

					@Override
					public VtuFileContainer getEmptyVtuMeshFiles(VCDataIdentifier vcdataID, int timeIndex) throws DataAccessException {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public double[] getVtuMeshData(OutputContext outputContext, VCDataIdentifier vcdataID,
							VtuVarInfo var, double time) throws RemoteException,
							DataAccessException {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public VtuVarInfo[] getVtuVarInfos(OutputContext outputContext,
							VCDataIdentifier vcDataIdentifier) {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}
		};

		VCDataManager postProcessVCDataManager = new VCDataManager(dataSetControllerProvider);
		PDEDataManager postProcessPDEDataManager =
			new PDEDataManager(((ClientPDEDataContext)parentPDEDataContext).getDataManager().getOutputContext(), postProcessVCDataManager, parentPDEDataContext.getVCDataIdentifier());
		PostProcessDataPDEDataContext postProcessDataPDEDataContext = new PostProcessDataPDEDataContext(postProcessPDEDataManager/*,dataProcessingOutputInfo*/);
		return postProcessDataPDEDataContext;
	}

	public static class PostProcessDataPDEDataContext extends ClientPDEDataContext{
		DataProcessingOutputInfo dataProcessingOutputInfo;
		public PostProcessDataPDEDataContext(PDEDataManager pdeDataManager/*,DataProcessingOutputInfo dataProcessingOutputInfo*/) throws Exception{
			super(pdeDataManager);
			refreshDataProcessingOutputInfo(pdeDataManager.getOutputContext());
		}
		
		private void refreshDataProcessingOutputInfo(OutputContext outputContext) throws Exception{
			dataProcessingOutputInfo = (DataOperationResults.DataProcessingOutputInfo)
				doDataOperation(new DataOperation.DataProcessingOutputInfoOP(getVCDataIdentifier(),false,outputContext));
		
		}
		public void reset(OutputContext parentOutputContext) throws Exception{
			refreshDataProcessingOutputInfo(parentOutputContext);
			PDEDataManager pdeDatamanager = ((PDEDataManager)getDataManager()).createNewPDEDataManager(getVCDataIdentifier(), (ClientPDEDataContext)this);	
			pdeDatamanager.setOutputContext(parentOutputContext);
			this.setDataManager(pdeDatamanager);
		}
		public static CartesianMesh createMesh(DataProcessingOutputInfo dataProcessingOutputInfo,String varName) throws Exception{
			//create mesh here because we know the variablename
			ISize varISize = dataProcessingOutputInfo.getVariableISize(varName);
			Extent varExtent = dataProcessingOutputInfo.getVariableExtent(varName);
			Origin varOrigin = dataProcessingOutputInfo.getVariableOrigin(varName);
			VCImage vcImage = new VCImageUncompressed(null,
				new byte[varISize.getXYZ()],
				varExtent,
				varISize.getX(),
				varISize.getY(),
				varISize.getZ());
			RegionImage regionImage = new RegionImage(vcImage,
					1+(varISize.getY()>0?1:0)+(varISize.getZ()>0?1:0),
					varExtent,
					varOrigin,
					RegionImage.NO_SMOOTHING);
			return CartesianMesh.createSimpleCartesianMesh(
						dataProcessingOutputInfo.getVariableOrigin(varName),
						varExtent,
						varISize, regionImage,true);
		}

		@Override
		public CartesianMesh getCartesianMesh() {
			try{
				return createMesh(dataProcessingOutputInfo,getVariableName());
			}catch(Exception e){
				throw new RuntimeException("Error PostProcessDataPDEDataContext.createMesh()",e);
			}
		}	
	}

	public boolean isInitialized(){
		return postProcessPDEDataViewer != null;
	}
	public void activatePanel(boolean bActivate){
		ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(postProcessPDEDataViewer);
		ChildWindow[] childwindows = childWindowManager.getAllChildWindows();
		for (int i = 0; i < childwindows.length; i++) {
			if(childwindows[i].getTitle().startsWith(PDEDataViewer.POST_PROCESS_PREFIX)){
//				System.out.println("showing "+childwindows[i].getTitle()+" "+childwindows[i].getContentPane().getClass().getName());
				if(bActivate){
					childwindows[i].show();
				}else{
					childwindows[i].hide();
				}
			}
		}
		if(bActivate){
			update();
		}
	}
	private PDEDataContext parentPdeDataContext;
	public void setParentPDEDataContext(ClientPDEDataContext parentPdeDataContext){
		this.parentPdeDataContext = parentPdeDataContext;
	}
	public PDEDataContext getParentPdeDataContext(){
		return this.parentPdeDataContext;
	}
	private Simulation simulation;
	public void setsimulation(Simulation simulation){
		this.simulation = simulation;
	}
	public Simulation getSimulation(){
		return simulation;
	}
	private DataViewerManager dataViewerManager;
	public void setDataViewerManager(DataViewerManager dataViewerManager){
		this.dataViewerManager = dataViewerManager;
	}
	public DataViewerManager getDataViewerManager(){
		return dataViewerManager;
	}
	private SimulationModelInfo simulationModelInfo;
	public void setSimulationModelInfo(SimulationModelInfo simulationModelInfo){
		this.simulationModelInfo = simulationModelInfo;
	}
	public SimulationModelInfo getSimulationModelInfo(){
		return simulationModelInfo;
	}
	private BlockingTimer updateTimer;
	public void update(){
		if((updateTimer = ClientTaskDispatcher.getBlockingTimer(this,getParentPdeDataContext(),null,updateTimer,new ActionListener() {@Override public void actionPerformed(ActionEvent e2) {update();}},"PDEDataViewerPostProcess update..."))!=null){
			return;
		}
		dispatchPostProcessUpdate((ClientPDEDataContext)getParentPdeDataContext());
	}
	private void dispatchPostProcessUpdate(ClientPDEDataContext newClientPDEDataContext) {
		ArrayList<AsynchClientTask> allTasks = new ArrayList<>();
		final String SPATIAL_ERROR_KEY = "SPATIAL_ERROR_KEY";
		AsynchClientTask postProcessInfoTask = new AsynchClientTask("",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(postProcessPDEDataViewer != null){//already initialized
					return;
				}
				if(getClientTaskStatusSupport() != null){
					getClientTaskStatusSupport().setMessage("Getting Simulation status...");
				}
				SimulationStatus simStatus =
						PDEDataViewerPostProcess.this.getDataViewerManager().getRequestManager().getServerSimulationStatus(PDEDataViewerPostProcess.this.getSimulation().getSimulationInfo());
				if(simStatus == null){
					hashTable.put(SPATIAL_ERROR_KEY, "PostProcessing Image, no simulation status");
					return;
				}else if(!simStatus.isCompleted()){
					//sim still busy, no postprocessing data
					hashTable.put(SPATIAL_ERROR_KEY, "PostProcessing Image, waiting for completed simulation: "+simStatus.toString());
					return;
				}
				if(getClientTaskStatusSupport() != null){
					getClientTaskStatusSupport().setMessage("Getting Post Process Info...");
				}
				//Get PostProcess Image state variables info
				DataProcessingOutputInfoOP dataProcessingOutputInfoOP =
					new DataProcessingOutputInfoOP(PDEDataViewerPostProcess.this.getParentPdeDataContext().getVCDataIdentifier(), false, null);
				DataProcessingOutputInfo dataProcessingOutputInfo = 
						(DataProcessingOutputInfo)PDEDataViewerPostProcess.this.getParentPdeDataContext().doDataOperation(dataProcessingOutputInfoOP);
				boolean bFoundImageStateVariables = false;
				if(dataProcessingOutputInfo != null && dataProcessingOutputInfo.getVariableNames() != null){
					for (int i = 0; i < dataProcessingOutputInfo.getVariableNames().length; i++) {
						if(dataProcessingOutputInfo.getPostProcessDataType(dataProcessingOutputInfo.getVariableNames()[i]).equals(DataProcessingOutputInfo.PostProcessDataType.image)){
							bFoundImageStateVariables = true;
							break;
						}
					}
				}
				if(!bFoundImageStateVariables){
					hashTable.put(SPATIAL_ERROR_KEY,"No spatial PostProcessing variables found. (see Application->Protocols->Microscope Measurement)");
				}
			};
		};

		AsynchClientTask addPanelTask = new AsynchClientTask("",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(postProcessPDEDataViewer != null){
					return;
				}
				if(hashTable.get(SPATIAL_ERROR_KEY) != null){
					if(PDEDataViewerPostProcess.this.getComponentCount() == 0){
						PDEDataViewerPostProcess.this.add(new JLabel((String)hashTable.get(SPATIAL_ERROR_KEY)),BorderLayout.CENTER);
					}else{
						((JLabel)PDEDataViewerPostProcess.this.getComponent(0)).setText((String)hashTable.get(SPATIAL_ERROR_KEY));
					}
					throw UserCancelException.CANCEL_GENERIC;
				}
				postProcessPDEDataViewer = new PDEDataViewer();
				parentDataJobListenerHolder.addDataJobListener(postProcessPDEDataViewer);
				PDEDataViewerPostProcess.this.postProcessPDEDataViewer.setPostProcessingPanelVisible(false);
				PDEDataViewerPostProcess.this.add(PDEDataViewerPostProcess.this.postProcessPDEDataViewer,BorderLayout.CENTER);
				postProcessPDEDataViewer.setDataViewerManager(getDataViewerManager());
				postProcessPDEDataViewer.setSimulationModelInfo(getSimulationModelInfo());
				postProcessPDEDataViewer.setSimulation(getSimulation());	
			}
		};
		allTasks.addAll(Arrays.asList(PDEDataViewerPostProcess.this.getUpdateTasks()));
//		AsynchClientTask refreshTask = new AsynchClientTask("refreshPostProcess",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//			@Override
//			public void run(Hashtable<String, Object> hashTable) throws Exception {
//				((ArrayList<AsynchClientTask>)hashTable.get(ClientTaskDispatcher.INTERMEDIATE_TASKS)).addAll(Arrays.asList(PDEDataViewerPostProcess.this.postProcessPDEDataViewer.getRefreshTasks()));
//			}
//		};
//		allTasks.add(refreshTask);

		allTasks.add(0, addPanelTask);
		allTasks.add(0,postProcessInfoTask);
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), allTasks.toArray(new AsynchClientTask[0]),false, false, false, null,true);
	}

	public PDEDataViewerPostProcess() {
		super();
		setLayout(new BorderLayout());
	}
	public AsynchClientTask[] getUpdateTasks(){
		final String POST_PROCESS_PDEDC_KEY = "POST_PROCESS_PDEDC_KEY";
		AsynchClientTask createPostProcessPDETask = new AsynchClientTask("create postprocess pdedc",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				hashTable.put(POST_PROCESS_PDEDC_KEY,createPostProcessPDEDataContext((ClientPDEDataContext)getParentPdeDataContext()));
				}
		};
		AsynchClientTask setPostProcessPDETask = new AsynchClientTask("set postproces pdedc",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				postProcessPDEDataViewer.setPdeDataContext((ClientPDEDataContext)hashTable.get(POST_PROCESS_PDEDC_KEY));
			}
		};
		return new AsynchClientTask[] {/*listenersTask,*/createPostProcessPDETask,setPostProcessPDETask};
//		ClientTaskDispatcher.dispatch(myPDEDataViewer, new Hashtable<>(), new AsynchClientTask[] {listenersTask,createPostProcessPDETask,setPostProcessPDETask},false,false,null);
	}
	private DataJobListenerHolder parentDataJobListenerHolder;
	public void init(DataJobListenerHolder parentDataJobListenerHolder){
		this.parentDataJobListenerHolder = parentDataJobListenerHolder;
		update();
	}
//	public void initPostProcessImageDataPanel(){
//		try{
////			if(myPDEDataViewer != null){
////				//Setup is done already
//////				PostProcessDataPDEDataContext myPostProcessDataPDEDataContext = (PostProcessDataPDEDataContext)myPDEDataViewer.getPdeDataContext();
//////				if(!myPostProcessDataPDEDataContext.getVCDataIdentifier().equals(parentPDEDataViewer.getPdeDataContext().getVCDataIdentifier())){
////					update((NewClientPDEDataContext)parentPDEDataViewer.getPdeDataContext());					
//////				}
////				return;
////			}
//			final DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(this, DocumentWindow.class);
//			final String SPATIAL_ERROR_KEY = "SPATIAL_ERROR_KEY";
//				AsynchClientTask postProcessInfoTask = new AsynchClientTask("",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//					@Override
//					public void run(Hashtable<String, Object> hashTable) throws Exception {
//						if(getClientTaskStatusSupport() != null){
//							getClientTaskStatusSupport().setMessage("Getting Simulation status...");
//						}
//						SimulationStatus simStatus =
//								PDEDataViewerPostProcess.this.getDataViewerManager().getRequestManager().getServerSimulationStatus(PDEDataViewerPostProcess.this.getSimulation().getSimulationInfo());
//						if(simStatus == null){
//							hashTable.put(SPATIAL_ERROR_KEY, "PostProcessing Image, no simulation status");
//							return;
//						}else if(!simStatus.isCompleted()){
//							//sim still busy, no postprocessing data
//							hashTable.put(SPATIAL_ERROR_KEY, "PostProcessing Image, waiting for completed simulation: "+simStatus.toString());
//							return;
//						}
//						if(getClientTaskStatusSupport() != null){
//							getClientTaskStatusSupport().setMessage("Getting Post Process Info...");
//						}
//						//Get PostProcess Image state variables info
//						DataProcessingOutputInfoOP dataProcessingOutputInfoOP =
//							new DataProcessingOutputInfoOP(PDEDataViewerPostProcess.this.getPdeDataContext().getVCDataIdentifier(), false, null);
//						DataProcessingOutputInfo dataProcessingOutputInfo = 
//								(DataProcessingOutputInfo)PDEDataViewerPostProcess.this.getPdeDataContext().doDataOperation(dataProcessingOutputInfoOP);
//						boolean bFoundImageStateVariables = false;
//						if(dataProcessingOutputInfo != null && dataProcessingOutputInfo.getVariableNames() != null){
//							for (int i = 0; i < dataProcessingOutputInfo.getVariableNames().length; i++) {
//								if(dataProcessingOutputInfo.getPostProcessDataType(dataProcessingOutputInfo.getVariableNames()[i]).equals(DataProcessingOutputInfo.PostProcessDataType.image)){
//									bFoundImageStateVariables = true;
//									break;
//								}
//							}
//						}
//						if(!bFoundImageStateVariables){
//							hashTable.put(SPATIAL_ERROR_KEY,"No spatial PostProcessing variables found. (see Application->Protocols->Microscope Measurement)");
//						}
//					};
//				};
////				final String POST_PROCESS_PDEDV = "POST_PROCESS_PDEDV";
//				AsynchClientTask createPostProcessPDEDataViewer = new AsynchClientTask("",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
//					@Override
//					public void run(Hashtable<String, Object> hashTable) throws Exception {
//						if(PDEDataViewerPostProcess.this.getComponentCount() > 0){
//							PDEDataViewerPostProcess.this.removeAll();
//						}
//						if(hashTable.get(SPATIAL_ERROR_KEY) != null){
//							PDEDataViewerPostProcess.this.add(new JLabel((String)hashTable.get(SPATIAL_ERROR_KEY)),BorderLayout.CENTER);
//							throw UserCancelException.CANCEL_GENERIC;
//						}
//						if(getClientTaskStatusSupport() != null){
//							getClientTaskStatusSupport().setMessage("Creating Post Process GUI...");
//						}
//						PDEDataViewerPostProcess.this.postProcessPDEDataViewer = new PDEDataViewer();
//						PDEDataViewerPostProcess.this.postProcessPDEDataViewer.setName("PostProcesPDEDV");
////						PDEDataViewerPostProcess.this.postProcessPDEDataViewer.getPDEPlotControlPanel1().setName("PostProcessPDEPCP");
////						PDEPlotControlPanel.DefaultDataIdentifierFilter plotFilter = new PDEPlotControlPanel.DefaultDataIdentifierFilter((SimulationWorkspaceModelInfo)parentPDEDataViewer.getSimulationModelInfo());
////						myPDEDataViewer.getPDEPlotControlPanel1().setDataIdentifierFilter(plotFilter);
////						myPDEDataViewer.getPDEPlotControlPanel1().getDataIdentifierFilter().setPostProcessingMode(true);
//	//System.out.println("----------parentPDEDV="+PDEDataViewer.this.hashCode()+" PostProcessPDEDV="+postProcessPdeDataViewer.hashCode());
//						PDEDataViewerPostProcess.this.add(PDEDataViewerPostProcess.this.postProcessPDEDataViewer,BorderLayout.CENTER);
////						myPDEDataViewer.getPDEPlotControlPanel1().setPostProcessingMode(true);
//						PDEDataViewerPostProcess.this.postProcessPDEDataViewer.setPostProcessingPanelVisible(false);
//						PDEDataViewerPostProcess.this.postProcessPDEDataViewer.setDataViewerManager((DocumentWindowManager)documentWindow.getTopLevelWindowManager());
////						hashTable.put(POST_PROCESS_PDEDV, postProcessPdeDataViewer);
//						parentPDEDataViewer.addDataJobListener(myPDEDataViewer);
//					}
//				};
////				final String POST_PROCESS_PDEDC = "POST_PROCESS_PDEDC";
////				AsynchClientTask postProcessPDEDCTask = new AsynchClientTask("",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
////					@Override
////					public void run(Hashtable<String, Object> hashTable) throws Exception {
////						if(getClientTaskStatusSupport() != null){
////							getClientTaskStatusSupport().setMessage("Creating Post Process PDEDataContext...");
////						}
////						PostProcessDataPDEDataContext postProcessDataPDEDataContext = createPostProcessPDEDataContext((NewClientPDEDataContext)parentPDEDataViewer.getPdeDataContext());
////						hashTable.put(POST_PROCESS_PDEDC,postProcessDataPDEDataContext);
////					}
////				};
//				AsynchClientTask setPostProcessPDEDatacontext = new AsynchClientTask("",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
//					@Override
//					public void run(Hashtable<String, Object> hashTable) throws Exception {
//						if(getClientTaskStatusSupport() != null){
//							getClientTaskStatusSupport().setMessage("Getting Post Process Data...");
//						}
////						myPDEDataViewer.setPdeDataContext((PostProcessDataPDEDataContext)hashTable.get(POST_PROCESS_PDEDC));
////						update((NewClientPDEDataContext)parentPDEDataViewer.getPdeDataContext());
//						SimulationWorkspaceModelInfo simulationWorkspaceModelInfo =
//								new SimulationWorkspaceModelInfo(PDEDataViewerPostProcess.this.getSimulation().getSimulationOwner(),PDEDataViewerPostProcess.this.getSimulation().getName()) {
//									@Override
//									public String getVolumeNamePhysiology(int subVolumeID) {
//										return "PostProcess";
//									}
//									@Override
//									public String getVolumeNameGeometry(int subVolumeID) {
//										return "PostProcess";
//									}
//									@Override
//									public String getSimulationName() {
//										return PDEDataViewerPostProcess.this.getSimulationModelInfo().getSimulationName();
//									}
//									@Override
//									public String getMembraneName(int subVolumeIdIn, int subVolumeIdOut,boolean bFromGeometry) {
//										return "PostProcess";
//									}
//									@Override
//									public String getContextName() {
//										return PDEDataViewerPostProcess.this.getSimulationModelInfo().getContextName();
//									}
//								};
//							myPDEDataViewer.setSimulation(parentPDEDataViewer.getSimulation());
//							myPDEDataViewer.setSimulationModelInfo(simulationWorkspaceModelInfo);
//							myPDEDataViewer.setSimNameSimDataID(
//							new ExportSpecs.SimNameSimDataID(parentPDEDataViewer.getSimulation().getName(),
//								parentPDEDataViewer.getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), null));
//							if(hashTable.get(ClientTaskDispatcher.INTERMEDIATE_TASKS) != null){
////								AsynchClientTask filterTask = new AsynchClientTask("set PostProcess filter",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
////									@Override
////									public void run(Hashtable<String, Object> hashTable) throws Exception {
////										PDEPlotControlPanel.DefaultDataIdentifierFilter plotFilter = new PDEPlotControlPanel.DefaultDataIdentifierFilter((SimulationWorkspaceModelInfo)parentPDEDataViewer.getSimulationModelInfo());
////										plotFilter.setPostProcessingMode(true);
////										myPDEDataViewer.getPDEPlotControlPanel1().setDataIdentifierFilter(plotFilter);
////									}
////								};
////								((ArrayList<AsynchClientTask>)hashTable.get(ClientTaskDispatcher.INTERMEDIATE_TASKS)).add(filterTask);
//								((ArrayList<AsynchClientTask>)hashTable.get(ClientTaskDispatcher.INTERMEDIATE_TASKS)).addAll(Arrays.asList(myPDEDataViewer.getPDEPlotControlPanel1().getFilterVarNamesTasks()));
//							}
//
//					}
//				};
//				ArrayList<AsynchClientTask> allTasks = new ArrayList<>();
//				allTasks.add(postProcessInfoTask);
//				allTasks.add(createPostProcessPDEDataViewer);
//				allTasks.addAll(Arrays.asList(getUpdateTasks((NewClientPDEDataContext)parentPDEDataViewer.getPdeDataContext())));
//				allTasks.add(setPostProcessPDEDatacontext);
//								ClientTaskDispatcher.dispatch(parentPDEDataViewer, new Hashtable<String, Object>(), allTasks.toArray(new AsynchClientTask[0]),true, false, false, null, true);
//		}catch(Exception e2){
//			e2.printStackTrace();
//		}
//
//	}
//	private void resetPDEDataContext() throws Exception {
//		NewClientPDEDataContext parentNewClientPDEDataContext = (NewClientPDEDataContext)parentPDEDataViewer.getPdeDataContext();
//		PostProcessDataPDEDataContext postProcessPDEDataContext = (PostProcessDataPDEDataContext)myPDEDataViewer.getPdeDataContext();
//		if(postProcessPDEDataContext != null){
//			postProcessPDEDataContext.reset(parentNewClientPDEDataContext.getDataManager().getOutputContext());
//		}
//	}
	@Override
	public void dataJobMessage(DataJobEvent event) {
		postProcessPDEDataViewer.dataJobMessage(event);
	}

}
