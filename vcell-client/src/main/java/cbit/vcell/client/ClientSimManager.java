/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.vcell.solver.smoldyn.SmoldynFileWriter;
import org.vcell.solver.smoldyn.SmoldynSolver;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.UtilCancelException;
import org.vcell.util.document.LocalVCDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vis.io.VtuVarInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.DataViewerController;
import cbit.vcell.client.data.SimResultsViewer;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.data.VCellClientDataServiceImpl;
import cbit.vcell.client.desktop.simulation.SimulationStatusDetails;
import cbit.vcell.client.desktop.simulation.SimulationStatusDetailsPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.desktop.simulation.SimulationWindow.LocalState;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.pyvcellproxy.SimulationDataSetRef;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.AsynchClientTaskFunction;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.test.VCellClientTest;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.VCellConfiguration;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.simdata.VtkManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TempSimulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.util.ColumnDescription;

/**
 * Insert the type's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @author: Ion Moraru
 */
public class ClientSimManager implements java.beans.PropertyChangeListener {
	
@SuppressWarnings("serial")
public static class LocalVCSimulationDataIdentifier extends VCSimulationDataIdentifier implements LocalVCDataIdentifier {

	private File localSimDir = null;
	public LocalVCSimulationDataIdentifier(VCSimulationIdentifier vcSimID, int jobIndex, File localDir) {
		super(vcSimID, jobIndex);
		localSimDir = localDir;
	}

	public File getLocalDirectory() {
		return localSimDir;
	}
}


	private DocumentWindowManager documentWindowManager = null;
	private SimulationWorkspace simWorkspace = null;
	private SimulationStatusHash simHash = new SimulationStatusHash();
	
	// Hash Keys
	private final static String H_DATA_VIEWER_CONTROLLERS = "dataViewerControllers";
//	private final static String H_SIM_DATASET_REFS = "simDataSetRefs";
	private final static String H_FAILURES = "failures";
	private final static String H_LOCAL_SIM = "showingLocal";
	private final static String H_VIEWER_TYPE = "viewerType";

/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:48:50 AM)
 * @param documentWindowManager cbit.vcell.client.DocumentWindowManager
 * @param simulationOwner cbit.vcell.document.SimulationOwner
 */
public ClientSimManager(DocumentWindowManager documentWindowManager, SimulationWorkspace simWorkspace) {
	this.documentWindowManager = documentWindowManager;
	this.simWorkspace = simWorkspace;
	getSimWorkspace().addPropertyChangeListener(this);
	initHash(getSimWorkspace().getSimulations());
}

public User getLoggedInUser() {
	return getDocumentWindowManager().getUser();
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:50:45 PM)
 * @return cbit.vcell.client.DocumentWindowManager
 */
DocumentWindowManager getDocumentWindowManager() {
	return documentWindowManager;
}

public UserPreferences getUserPreferences() {
	UserPreferences up = documentWindowManager.getUserPreferences();
	return up;
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param simulation cbit.vcell.solver.Simulation
 */
public SimulationStatus getSimulationStatus(Simulation simulation) {
	SimulationStatus cachedSimStatus = simHash.getSimulationStatus(simulation);
	if (cachedSimStatus!=null){
		if (simulation.getIsDirty()) {
			return SimulationStatus.newNotSaved(simulation.getScanCount());
		} else {
			return cachedSimStatus;
		}
	} else {
		// shouldn't really happen
		try {
			throw new RuntimeException("shouldn't really happen");
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 1:17:36 PM)
 * @return cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
public SimulationWorkspace getSimWorkspace() {
	return simWorkspace;
}

public void preloadSimulationStatus(Simulation[] simulations) {
	initHash(simulations);
}

/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:55:18 PM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
private void initHash(Simulation[] simulations) {
	simHash.changeSimulationInstances(simulations);
	if (simulations != null) {
		for (int i = 0; i < simulations.length; i++){
			SimulationStatus simStatus = simHash.getSimulationStatus(simulations[i]);
			if (simStatus==null || simStatus.isUnknown()){
				// try to get status from server
				simStatus = getDocumentWindowManager().getRequestManager().getServerSimulationStatus(simulations[i].getSimulationInfo());
				if (simStatus != null) {
					simHash.setSimulationStatus(simulations[i], simStatus);
				} else {
					simHash.setSimulationStatus(simulations[i], SimulationStatus.newNeverRan(simulations[i].getScanCount()));
				}
			}
		}
	}
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getPropertyName().equals("simulations")) {
		simHash.changeSimulationInstances((Simulation[])evt.getNewValue());
	}
	if (evt.getPropertyName().equals("simulationOwner")) {
		initHash(((SimulationOwner)evt.getNewValue()).getSimulations());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulation cbit.vcell.solver.Simulation[]
 */
public void runSimulations(Simulation[] simulations) {
	runSimulations(simulations, null);
}
public void runSimulations(Simulation[] simulations,AsynchClientTask[] endTasks) {
	getDocumentWindowManager().getRequestManager().runSimulations(this, simulations,endTasks);
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void showSimulationResults(OutputContext outputContext, Simulation[] simulations, ViewerType viewerType) {
	if (simulations == null) {
		return;
	}
	
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < simulations.length; i++){
		if (simulations[i].getSimulationInfo() != null && getSimulationStatus(simulations[i]).getHasData()) {
			v.add(simulations[i]);
		}
	}
	final Simulation[] simsToShow = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
	hashTable.put("outputContext", outputContext);
	hashTable.put("simsArray", simsToShow);
	AsynchClientTask[] taskArray = showSimulationResults0(false, viewerType);
	ClientTaskDispatcher.dispatch(getDocumentWindowManager().getComponent(), hashTable, taskArray, false, true, null);
}

public enum ViewerType {
	NativeViewer_only
}

private static void saveFailure(Hashtable<String, Object>hashTable,Simulation sim,Throwable throwable){
	Hashtable<Simulation,Throwable> failures = (Hashtable<Simulation,Throwable>)hashTable.get(H_FAILURES);
	if (failures == null) {
		failures = new Hashtable<Simulation, Throwable>();
		hashTable.put(H_FAILURES, failures);
	}
	failures.put(sim, throwable);
}

public void getBatchSimulationsResults(OutputContext outputContext, Simulation simulation) throws java.beans.PropertyVetoException {

	

	// simulation should be a template simulation
	if(simulation.getName().contains(SimulationContext.ReservedBatchExtensionString)) {
		throw new RuntimeException("Not a valid name for a batch template Simulation: '" + simulation.getName() + "'.");
	}

	SimulationOwner simOwner = getSimWorkspace().getSimulationOwner();
	if(!(simOwner instanceof SimulationContext)) {
		throw new RuntimeException("Template Simulation Owner must be a SimulationContext");
	}
	SimulationContext simContext = (SimulationContext)simOwner;
	BioModel bioModel = simContext.getBioModel();
	if(bioModel==null) {
		throw new RuntimeException("Cannot add simulation, bioModel not set");
	}
	
	File batchResultsDir = ResourceUtil.getLocalBatchDir();
//	File localSimDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());
	
	ArrayList<AsynchClientTask> taskList = new ArrayList<AsynchClientTask>();
	AsynchClientTask retrieveResultsTask = new AsynchClientTask("Retrieving results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING)  {
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			// recover the list of batch simulations that belong to this template
			Simulation allSims[] = bioModel.getSimulations();
			LinkedHashMap<String, String> importsMap = new LinkedHashMap<>();
			LinkedHashMap <String, Boolean> successMap = new LinkedHashMap<>();
			String namePrefix = simulation.getName() + SimulationContext.ReservedBatchExtensionString;
			
			for(Simulation simCandidate : allSims) {
				if(simCandidate.getName().startsWith(namePrefix) && simCandidate.getName().contains("_bat_")) {
					
					int pos = simCandidate.getName().lastIndexOf("_");
					String indexName = simCandidate.getName().substring(pos+1);
					File currentSimulation = new File(batchResultsDir, indexName + ".txt");
					
					ODESimData simData = null;
					importsMap.put(simCandidate.getName(), simCandidate.getSimulationID());
					successMap.put(simCandidate.getName(), true);	// on failure we'll change to false

					try {
						simData = importBatchSimulation(outputContext, simCandidate);
					} catch(Exception e) {		// whatever fails, we keep going, this is a batch run
						System.out.println(simCandidate.getName() + ": failed to recover simulation results");

						// TODO: also make a report with detailed exception text
//						e.printStackTrace();
						successMap.put(simCandidate.getName(), false);
					}
					if(simData != null) {		// write the file
//						double[] res = osrs.extractColumn(4);

						StringBuilder sb = new StringBuilder();
						for(ColumnDescription cd : simData.getDataColumnDescriptions()) {
							 sb.append(cd.getName() + " ");
						}
						sb.append("\r\n");
						
						for(double[] row : simData.getRows()) {
							for(double entry : row) {
								sb.append(entry);
								sb.append(" ");
							}
							sb.append("\r\n");
						}
						PrintWriter out = new PrintWriter(currentSimulation);
						out.print(sb.toString());
						out.flush();
						out.close();
					}
				}
			}
			
//			for(String name : importsMap.keySet()) {
//			String value = importsMap.get(name);
//				write some manifest?
//				// at least some basic info like the name of the simulation / biomodel / dat file
//			}
			System.out.println("Done !!!");
		}										// --- end run()
	};
	taskList.add(retrieveResultsTask);
	AsynchClientTask[] taskArray = new AsynchClientTask[taskList.size()];
	taskList.toArray(taskArray);
	// knowProgress, cancelable, progressDialogListener
	ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), taskArray, false, false, null);
}

private ODESimData importBatchSimulation(OutputContext outputContext, Simulation sim) throws DataAccessException, RemoteProxyException, ExpressionException {

	boolean success = false;
	ODESimData simData = null;
	
	if(sim.getVersion() == null) {
		throw new RuntimeException("Missing Version.");
	}
	SimulationInfo simInfo = sim.getSimulationInfo();
	if(simInfo == null) {
		throw new RuntimeException("Missing Simulation Info.");
	}
	
	User usr = sim.getVersion().getOwner();
	

	VCSimulationIdentifier asi = simInfo.getAuthoritativeVCSimulationIdentifier();
	// sim.getScanCount()  number of jobs must be one, so job index is 0
	VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(asi, 0);

	ClientServerManager csm = VCellClientTest.getVCellClient().getClientServerManager();
	DataSetController dsc = csm.getDataSetController();
	simData = dsc.getODEData(vcSimulationDataIdentifier);
	
	System.out.println(sim.getName() + ": simulation results recovered");
	return simData;
	
	
	
	/*
	
	final VCSimulationIdentifier vcSimulationIdentifier = simInfo.getAuthoritativeVCSimulationIdentifier();
	DataManager dataManager = null;

	try {
//		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(null,localBatchDir,null);
		ExportServiceImpl localExportServiceImpl = new ExportServiceImpl();
	
//		LocalDataSetControllerProvider localDSCProvider = new LocalDataSetControllerProvider(usr, dataSetControllerImpl, localExportServiceImpl);
//		VCDataManager vcDataManager = new VCDataManager(localDSCProvider);
//		LocalVCDataIdentifier vcDataId = new LocalVCSimulationDataIdentifier(vcSimulationIdentifier, 0, localSimDir);
//		if (sim.isSpatial()) {
//			dataManager = new PDEDataManager(outputContext,vcDataManager, vcDataId);
//		} else {
//			dataManager = new ODEDataManager(outputContext,vcDataManager, vcDataId);
//		}	
		System.out.println("start!");
		DataViewerController dataViewerController = documentWindowManager.getRequestManager().getDataViewerController(outputContext,sim, 0);
		
//		if(!(dataViewerController instanceof SimResultsViewerController)) {
//			throw new RuntimeException("Unable to initialize the SimResultsViewerController");
//		}
//		SimResultsViewerController rvc = (SimResultsViewerController)dataViewerController;
		DataViewer dw = dataViewerController.createViewer();
//		DataViewerManager dwm = dw.getDataViewerManager();
		if(!(dw instanceof SimResultsViewer)) {
			throw new RuntimeException("Wrong data viewer type, must be SimResultsViewer.");
		}
		SimResultsViewer srw = (SimResultsViewer)dw;

		// I need DataViewer.ODEDataManager.ODESimData (Ode Solver Result Set !!!!)
		
		
		System.out.println("done!");
//	} catch (FileNotFoundException e) {
//		e.printStackTrace();
	} catch (DataAccessException e) {
		e.printStackTrace();
	}
	
	*/

}

private AsynchClientTask[] showSimulationResults0(final boolean isLocal, final ViewerType viewerType) {

	// Create the AsynchClientTasks 
	ArrayList<AsynchClientTask> taskList = new ArrayList<AsynchClientTask>();
	
	taskList.add(  new AsynchClientTaskFunction( h -> { h.put(H_LOCAL_SIM, isLocal); h.put(H_VIEWER_TYPE, viewerType); } , "setLocal", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) ); 

	final DocumentWindowManager documentWindowManager = getDocumentWindowManager();
	AsynchClientTask retrieveResultsTask = new AsynchClientTask("Retrieving results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING)  {
		@SuppressWarnings("unchecked")
		public void run(Hashtable<String, Object> hashTable) throws Exception {						
			Simulation[] simsToShow = (Simulation[])hashTable.get("simsArray");
			for (int i = 0; i < simsToShow.length; i++){		
				final Simulation sim  = simsToShow[i];
				final VCSimulationIdentifier vcSimulationIdentifier = sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
				final SimulationWindow simWindow = documentWindowManager.haveSimulationWindow(vcSimulationIdentifier);
				OutputContext outputContext = (OutputContext)hashTable.get("outputContext");
				
				if (simWindow == null && ( viewerType==ViewerType.NativeViewer_only)) {			
					try {
						// make the manager and wire it up
						DataViewerController dataViewerController = null;
						if (!isLocal) {
							dataViewerController = documentWindowManager.getRequestManager().getDataViewerController(outputContext,sim, 0);
							documentWindowManager.addDataListener(dataViewerController);//For changes in time or variable
						} else {
							// ---- preliminary : construct the localDatasetControllerProvider
							File primaryDir = ResourceUtil.getLocalRootDir();
							User usr = sim.getVersion().getOwner();
							DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(null,primaryDir,null);
							ExportServiceImpl localExportServiceImpl = new ExportServiceImpl();
							LocalDataSetControllerProvider localDSCProvider = new LocalDataSetControllerProvider(usr, dataSetControllerImpl, localExportServiceImpl);
							VCDataManager vcDataManager = new VCDataManager(localDSCProvider);
							File localSimDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());
							LocalVCDataIdentifier vcDataId = new LocalVCSimulationDataIdentifier(vcSimulationIdentifier, 0, localSimDir);
							DataManager dataManager = null;
							if (sim.isSpatial()) {
								dataManager = new PDEDataManager(outputContext,vcDataManager, vcDataId);
							} else {
								dataManager = new ODEDataManager(outputContext,vcDataManager, vcDataId);
							}	
							dataViewerController = new SimResultsViewerController(dataManager, sim);
							dataSetControllerImpl.addDataJobListener(documentWindowManager);
						}
						// make the viewer
						Hashtable<VCSimulationIdentifier, DataViewerController> dataViewerControllers = (Hashtable<VCSimulationIdentifier, DataViewerController>)hashTable.get(H_DATA_VIEWER_CONTROLLERS);
						if (dataViewerControllers == null) {
							dataViewerControllers = new Hashtable<VCSimulationIdentifier, DataViewerController>();
							hashTable.put(H_DATA_VIEWER_CONTROLLERS, dataViewerControllers);
						}						
						dataViewerControllers.put(vcSimulationIdentifier, dataViewerController);
					} catch (Throwable exc) {
						exc.printStackTrace(System.out);
						saveFailure(hashTable,sim, exc);
					}
				}
			}			
		}	
	};
	taskList.add(retrieveResultsTask);
	
	AsynchClientTask displayResultsTask = new AsynchClientTask("Showing results", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@SuppressWarnings("unchecked")
		public void run(Hashtable<String, Object> hashTable) throws Exception {					
			boolean isLocal = fetch(hashTable,H_LOCAL_SIM,Boolean.class,true);
			SimulationWindow.LocalState localState = isLocal ? LocalState.LOCAL : LocalState.SERVER;
			Hashtable<Simulation,Throwable> failures = (Hashtable<Simulation,Throwable>)hashTable.get(H_FAILURES);
			Simulation[] simsToShow = (Simulation[])hashTable.get("simsArray");
			for (int i = 0; i < simsToShow.length; i++){
				final Simulation sim  = simsToShow[i];
				if(failures != null && failures.containsKey(sim)){
					continue;
				}
				final VCSimulationIdentifier vcSimulationIdentifier = simsToShow[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();	
				
				final SimulationWindow simWindow = documentWindowManager.haveSimulationWindow(vcSimulationIdentifier);
				if (simWindow != null) {
					ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(documentWindowManager.getComponent());
					ChildWindow childWindow = childWindowManager.getChildWindowFromContext(simWindow);
					if (childWindow==null){
						childWindow = childWindowManager.addChildWindow(simWindow.getDataViewer(), simWindow);
						childWindow.pack();
						childWindow.setIsCenteredOnParent();
						childWindow.show();
					}
					setFinalWindow(hashTable, childWindow); 
					simWindow.setLocalState(localState);
				} else {					
					// wire it up the viewer
					Hashtable<VCSimulationIdentifier, DataViewerController> dataViewerControllers = (Hashtable<VCSimulationIdentifier, DataViewerController>)hashTable.get(H_DATA_VIEWER_CONTROLLERS);
					DataViewerController viewerController = dataViewerControllers.get(vcSimulationIdentifier);
					Throwable ex = (failures==null?null:failures.get(sim)); 
					if (viewerController != null && ex == null) { // no failure
						DataViewer viewer = viewerController.createViewer();
						getSimWorkspace().getSimulationOwner().getOutputFunctionContext().addPropertyChangeListener(viewerController);
						documentWindowManager.addExportListener(viewer);
						documentWindowManager.addDataJobListener(viewer);//For data related activities such as calculating statistics
						
						viewer.setSimulationModelInfo(new SimulationWorkspaceModelInfo(getSimWorkspace().getSimulationOwner(),sim.getName()));
						viewer.setDataViewerManager(documentWindowManager);
						
						SimulationWindow newWindow = new SimulationWindow(vcSimulationIdentifier, sim, getSimWorkspace().getSimulationOwner(), viewer);						
						BeanUtils.addCloseWindowKeyboardAction(newWindow.getDataViewer());
						documentWindowManager.addResultsFrame(newWindow);
						setFinalWindow(hashTable, viewer); 
						newWindow.setLocalState(localState);
					}
				}
			}
			StringBuffer failMessage = new StringBuffer();
			if (failures != null) {
				if (!failures.isEmpty()) {
					failMessage.append("Error, "+failures.size()+" of "+simsToShow.length+" sim results failed to display:\n");
					Enumeration<Simulation> en = failures.keys();
					while (en.hasMoreElements()) {
						Simulation sim = en.nextElement();
						Throwable exc = (Throwable)failures.get(sim);
						failMessage.append("'"+sim.getName()+"' - "+exc.getMessage());
					}
				}
			}
			if(failMessage.length() > 0){
				PopupGenerator.showErrorDialog(ClientSimManager.this.getDocumentWindowManager(), failMessage.toString());
			}
		}			
	};
	if ( viewerType==ViewerType.NativeViewer_only){
		taskList.add(displayResultsTask);
	}
	
	// Dispatch the tasks using the ClientTaskDispatcher.		
	AsynchClientTask[] taskArray = new AsynchClientTask[taskList.size()];
	taskList.toArray(taskArray);
	return taskArray;
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void showSimulationStatusDetails(Simulation[] simulations) {
	if (simulations != null) {
		final Simulation[] simsToShow = simulations; //(Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
		for (int i = 0; i < simsToShow.length; i ++) {
			SimulationStatusDetailsPanel ssdp = new SimulationStatusDetailsPanel();
			ssdp.setPreferredSize(new Dimension(800, 350));
			ssdp.setSimulationStatusDetails(new SimulationStatusDetails(getSimWorkspace(), simsToShow[i]));
			DialogUtils.showComponentCloseDialog(getDocumentWindowManager().getComponent(), ssdp, "Simulation Status Details");			
			ssdp.setSimulationStatusDetails(null);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void stopSimulations(Simulation[] simulations) {
	getDocumentWindowManager().getRequestManager().stopSimulations(this, simulations);
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:04:12 PM)
 */
void updateStatusFromServer(Simulation simulation) {
	// 
	// get cached status
	//
	SimulationStatus oldStatus = getSimulationStatus(simulation);
	SimulationStatus serverStatus = getDocumentWindowManager().getRequestManager().getServerSimulationStatus(simulation.getSimulationInfo());

	SimulationStatus newStatus = null;
	if (oldStatus.isStopRequested() && serverStatus.numberOfJobsDone() < simulation.getScanCount()) {
		// if stop requested but still going, get updated server info but adjust status
		newStatus = SimulationStatus.newStopRequest(serverStatus);
	} else {
		// otherwise accept server information
		newStatus = serverStatus;
	}

	// update cache
	simHash.setSimulationStatus(simulation,newStatus);
	
	System.out.println("---ClientSimManager.updateStatusFromServer[newStatus=" + newStatus + "], simulation="+simulation.toString());
	if (oldStatus!=newStatus){
		int simIndex = getSimWorkspace().getSimulationIndex(simulation);
		getSimWorkspace().firePropertyChange(SimulationWorkspace.PROPERTY_NAME_SIMULATION_STATUS, new Integer(-1), new Integer(simIndex));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void updateStatusFromStartRequest(final Simulation simulation, SimulationStatus newStatusFromServer) {
	// asynchronous call - from start request worker thread
	simHash.setSimulationStatus(simulation,newStatusFromServer);
	int simIndex = getSimWorkspace().getSimulationIndex(simulation);
	getSimWorkspace().firePropertyChange(SimulationWorkspace.PROPERTY_NAME_SIMULATION_STATUS, new Integer(-1), new Integer(simIndex));
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 3:01:29 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void updateStatusFromStopRequest(final Simulation simulation, SimulationStatus newStatusFromServer) {
	// asynchronous call - from stop request worker thread
	simHash.setSimulationStatus(simulation,newStatusFromServer);
	int simIndex = getSimWorkspace().getSimulationIndex(simulation);
	getSimWorkspace().firePropertyChange(SimulationWorkspace.PROPERTY_NAME_SIMULATION_STATUS, new Integer(-1), new Integer(simIndex));
}

public void runSmoldynParticleView(final Simulation originalSimulation) {
	SimulationOwner simulationOwner = simWorkspace.getSimulationOwner();
	Collection<AsynchClientTask> tasks;
	if (simulationOwner instanceof SimulationContext) {
		tasks = ClientRequestManager.updateMath(documentWindowManager.getComponent(), (SimulationContext)simulationOwner, false, NetworkGenerationRequirements.ComputeFullStandardTimeout);
	} else {
		tasks = new ArrayList<>(); 
	}
		
	AsynchClientTask pv = new AsynchClientTask("starting particle view", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			File exes[] = SolverUtilities.getExes(SolverDescription.Smoldyn);
			assert exes.length == 1 : "one and only one smoldyn solver expected";
			File smoldynExe = exes[0];
	
			Simulation simulation = new TempSimulation(originalSimulation, false);
			SimulationTask simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
			File inputFile = new File(ResourceUtil.getLocalSimDir(User.tempUser.getName()), simTask.getSimulationJobID() + SimDataConstants.SMOLDYN_INPUT_FILE_EXTENSION);
			inputFile.deleteOnExit();
			PrintWriter pw = new PrintWriter(inputFile);
			SmoldynFileWriter smf = new SmoldynFileWriter(pw, true, null, simTask, false);
			smf.write();
			pw.close();	
			String[] cmd = new String[] {smoldynExe.getAbsolutePath(), inputFile.getAbsolutePath()};
			StringBuilder commandLine = new StringBuilder();
			for (int i = 0; i < cmd.length; i ++) {
				if (i > 0) {
					commandLine.append(" ");
				}		
				commandLine.append(TokenMangler.getEscapedPathName(cmd[i]));
			}
			System.out.println(commandLine);
			char charArrayOut[] = new char[10000];
			char charArrayErr[] = new char[10000];
			ProcessBuilder processBuilder = new ProcessBuilder(cmd);
			final Process process = processBuilder.start();
			getClientTaskStatusSupport().addProgressDialogListener(new ProgressDialogListener() {

				public void cancelButton_actionPerformed(EventObject newEvent) {
					process.destroy();
				}
			});
			InputStream errorStream = process.getErrorStream();
			InputStreamReader errisr = new InputStreamReader(errorStream);
			InputStream outputStream = process.getInputStream();
			InputStreamReader outisr = new InputStreamReader(outputStream);
			
			StringBuilder sb = new StringBuilder();
			
			boolean running = true;
			while(running)
			{
				try {
					process.exitValue();
					running = false;
				} catch (IllegalThreadStateException e) {
					// process didn't exit yet, do nothing
				}
				
				if (outputStream.available() > 0) {
					outisr.read(charArrayOut, 0, charArrayOut.length);
				}
				if (errorStream.available() > 0) {
					errisr.read(charArrayErr, 0, charArrayErr.length);
					sb.append(new String(charArrayErr));
				}
			}
			if (sb.length() > 0)
			{
				throw new RuntimeException(sb.toString());
			}
		}
	};
	tasks.add(pv);
	ClientTaskDispatcher.dispatchColl(documentWindowManager.getComponent(), new Hashtable<String, Object>(), tasks, false, true, null);
}

public void runQuickSimulation(final Simulation originalSimulation, ViewerType viewerType) {
	Collection<AsynchClientTask> taskList = new ArrayList<AsynchClientTask>();
	final SimulationOwner simulationOwner = simWorkspace.getSimulationOwner();
	
	// ----------- update math if it is from biomodel (simulationContext)
	if (simulationOwner instanceof SimulationContext) {
		Collection<AsynchClientTask> ut = ClientRequestManager.updateMath(documentWindowManager.getComponent(), ((SimulationContext)simulationOwner), false, NetworkGenerationRequirements.ComputeFullStandardTimeout);
		taskList.addAll(ut);
	}
	//Let user tell how many simultaneous processes to run if this is local paramscan
	final int[] simultaneousSimsSetting = new int[] {1};
	if(originalSimulation.getScanCount() > 1) {
		try {
			String simultaneousSims = DialogUtils.showInputDialog0(getDocumentWindowManager().getComponent(), "Local multi-scan simulation, enter maximum simulataneous sims to run at once", "1");
			simultaneousSimsSetting[0] = Integer.parseInt(simultaneousSims);
		} catch (UtilCancelException e) {
			return;
		}
	}
	// ----------- run simulation(s)
	final File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());	
	AsynchClientTask runSimTask = new AsynchClientTask("running simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
						
			Simulation simulation = new TempSimulation(originalSimulation, false);
			SimulationTask simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
			Solver solver = createQuickRunSolver(localSimDataDir, simTask);
			if (solver == null) {
				throw new RuntimeException("null solver");
			}
			// check if spatial stochastic simulation (smoldyn solver) has data processing instructions with field data - need to access server for field data, so cannot do local simulation run. 
			if (solver instanceof SmoldynSolver) {
				DataProcessingInstructions dpi = simulation.getDataProcessingInstructions();
				if (dpi != null) {
					FieldDataIdentifierSpec fdis = dpi.getSampleImageFieldData(simulation.getVersion().getOwner());	
					if (fdis != null) {
						throw new RuntimeException("Spatial Stochastic simulation '" + simulation.getName() + "' (Smoldyn solver) with field data (in data processing instructions) cannot be run locally at this time since field data needs to be retrieved from the VCell server.");
					}
				}
			}
			solver.addSolverListener(new SolverListener() {
				public void solverStopped(SolverEvent event) {
					getClientTaskStatusSupport().setMessage(event.getSimulationMessage().getDisplayMessage());
				}
				public void solverStarting(SolverEvent event) {
					String displayMessage = event.getSimulationMessage().getDisplayMessage();
					System.out.println(displayMessage);
					getClientTaskStatusSupport().setMessage(displayMessage);
					if(displayMessage.equals(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT.getDisplayMessage())) {
						getClientTaskStatusSupport().setProgress(75);
					} else if(displayMessage.equals(SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE.getDisplayMessage())) {
						getClientTaskStatusSupport().setProgress(90);
					}
				}
				public void solverProgress(SolverEvent event) {
					getClientTaskStatusSupport().setMessage("Running...");
					int progress = (int)(event.getProgress() * 100);
					getClientTaskStatusSupport().setProgress(progress);
				}
				public void solverPrinted(SolverEvent event) {
					getClientTaskStatusSupport().setMessage("Running...");
				}
				public void solverFinished(SolverEvent event) {
					getClientTaskStatusSupport().setMessage(event.getSimulationMessage().getDisplayMessage());
				}
				public void solverAborted(SolverEvent event) {
					getClientTaskStatusSupport().setMessage(event.getSimulationMessage().getDisplayMessage());
				}
			});
			solver.startSolver();

			while (true){
				try { 
					Thread.sleep(500); 
				} catch (InterruptedException e) {
				}

				if (getClientTaskStatusSupport().isInterrupted()) {
					solver.stopSolver();
					throw UserCancelException.CANCEL_GENERIC;
				}
				SolverStatus solverStatus = solver.getSolverStatus();
				if (solverStatus != null) {
					if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
						String simulationMessage = solverStatus.getSimulationMessage().getDisplayMessage();
						String translatedMessage = solver.translateSimulationMessage(simulationMessage);
						if(translatedMessage.startsWith(BeanUtils.FD_EXP_MESSG)) {
							throw new RuntimeException("Sims with FieldData can only be run remotely (cannot use QuickRun).\n"+translatedMessage);
						}else {
							throw new RuntimeException(translatedMessage);
						}
					}
					if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
						solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
						solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
						break;
					}
				}		
			}
			
			ArrayList<AnnotatedFunction> outputFunctionsList = getSimWorkspace().getSimulationOwner().getOutputFunctionContext().getOutputFunctionsList();
			OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
			Simulation[] simsArray = new Simulation[] {simulation};
			hashTable.put("outputContext", outputContext);
			hashTable.put("simsArray", simsArray);
		}
	};
	taskList.add(runSimTask);
	
	// --------- add tasks from showSimResults : retrieve data, display results
	AsynchClientTask[] showResultsTask = showSimulationResults0(true, viewerType);
	for (AsynchClientTask task : showResultsTask) {
		taskList.add(task);
	}

	AsynchClientTask runOthers = new AsynchClientTask("running scans", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (getClientTaskStatusSupport().isInterrupted()) {
				throw UserCancelException.CANCEL_GENERIC;
			}			
			Simulation[] sims = (Simulation[])hashTable.get("simsArray");
			Simulation simulation = sims[0];
			//Run param scans to generate data for scansnum > 0
			if(simulation.getScanCount() > 1) {
				//Start master thread so clientdispatcher modal dialog can end
				new Thread(new Runnable() {
					@Override
					public void run() {
						//Flag (set by dataviewer being closed) so sim scan threads know if they should stop
						final boolean[] bWinCloseHolder = new boolean[] {false};
						//Add close listener to dataviewer to end all scans and exit
						final SimulationWindow haveSimulationWindow = getDocumentWindowManager().haveSimulationWindow(simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier());
						final Window window = (Window)BeanUtils.findTypeParentOfComponent(haveSimulationWindow.getDataViewer(), Window.class);
						window.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosing(WindowEvent e) {
								// TODO Auto-generated method stub
								super.windowClosing(e);
								bWinCloseHolder[0] = true;
							}});
						//First sim scan (0) is done already before viewer is shown
						((SimResultsViewer)haveSimulationWindow.getDataViewer()).setLocalScanProgress(1);
						//Counter of how many simultaneous param scan threads are running (decremented when scan thread finishes)
						final int[] currentlyRunningCountHolder = new int[] {0};
						//Run other scans starting at 1 (scan 0 is already done if we got here)
						for (int i = 1; i < simulation.getScanCount(); i++) {
							//Check if we can start another new param scan thread
							while(currentlyRunningCountHolder[0] >= simultaneousSimsSetting[0]) {
								try {
									Thread.sleep(50);
									//Check if user closed the viewer window, no need to continue
									if(bWinCloseHolder[0]) {
										return;
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							final int scanNum = i;
							//increment the concurrent running counter
							currentlyRunningCountHolder[0]+= 1;
							//Start new param scan thread
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										SimulationTask simTask = new SimulationTask(new SimulationJob(simulation, scanNum, null), 0);
										Solver solver = createQuickRunSolver(localSimDataDir, simTask);
										solver.startSolver();
										while(true) {
											SolverStatus solverStatus = solver.getSolverStatus();
//											System.out.println("ScanNum="+scanNum+" "+solverStatus);
											try { 
												Thread.sleep(100); 
											} catch (InterruptedException e) {
											}
											//Stop if user closed the dataviewer window
											if (bWinCloseHolder[0]) {
												solver.stopSolver();
												break;
											}

											if (solverStatus != null) {
												if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
													String simulationMessage = solverStatus.getSimulationMessage().getDisplayMessage();
													String translatedMessage = solver.translateSimulationMessage(simulationMessage);
													if(translatedMessage.startsWith(BeanUtils.FD_EXP_MESSG)) {
														throw new RuntimeException("Sims with FieldData can only be run remotely (cannot use QuickRun).\n"+translatedMessage);
													}else {
														throw new RuntimeException(translatedMessage);
													}
												}
												if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
													solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
													solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
													break;
												}
											}		

										}
//										SolverStatus solverStatus = solver.getSolverStatus();
//										System.out.println("ScanNum="+scanNum+" "+"FinalStatus="+solverStatus);
									} catch (Exception e) {
										e.printStackTrace();
									}finally {
										//decrement the concurrent running counter
										currentlyRunningCountHolder[0]-= 1;
										//Set progress on dataviewer
										if(((SimResultsViewer)haveSimulationWindow.getDataViewer()).getLocalScanProgress() < (scanNum+1)) {
											((SimResultsViewer)haveSimulationWindow.getDataViewer()).setLocalScanProgress(scanNum+1);
										}
									}
								}}).start();
						}						
					}}).start();
			}		
		}
		
	};
	taskList.add(runOthers);
	// ------- dispatch
	AsynchClientTask[] taskArray = new AsynchClientTask[taskList.size()];
	taskList.toArray(taskArray);
	ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), taskArray, true, true, null);
}


public static Solver createQuickRunSolver(File directory, SimulationTask simTask) throws SolverException, IOException {
	SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
	if (solverDescription == null) {
		throw new IllegalArgumentException("SolverDescription cannot be null");
	}
	
	// ----- 'FiniteVolume, Regular Grid' solver (semi-implicit) solver is not supported for quick run; throw exception.
	if (solverDescription.equals(SolverDescription.FiniteVolume)) {
		throw new IllegalArgumentException("Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step) solver not allowed for quick run of simulations.");
	}
	
	SolverUtilities.prepareSolverExecutable(solverDescription);	
	// create solver from SolverFactory
	Solver solver = SolverFactory.createSolver(directory, simTask, false);

	return solver;
}

}
