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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.solver.smoldyn.SmoldynFileWriter;
import org.vcell.solver.smoldyn.SmoldynSolver;
import org.vcell.util.BeanUtils;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.LocalVCDataIdentifier;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.desktop.simulation.SimulationStatusDetails;
import cbit.vcell.client.desktop.simulation.SimulationStatusDetailsPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.server.DataViewerController;
import cbit.vcell.client.server.SimResultsViewerController;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.LicenseManager;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;

/**
 * Insert the type's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @author: Ion Moraru
 */
public class ClientSimManager implements java.beans.PropertyChangeListener {
	
@SuppressWarnings("serial")
public class LocalVCSimulationDataIdentifier extends VCSimulationDataIdentifier implements LocalVCDataIdentifier {

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


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:50:45 PM)
 * @return cbit.vcell.client.DocumentWindowManager
 */
DocumentWindowManager getDocumentWindowManager() {
	return documentWindowManager;
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
	getDocumentWindowManager().getRequestManager().runSimulations(this, simulations);
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 10:31:36 AM)
 * @param simulations cbit.vcell.solver.Simulation[]
 */
public void showSimulationResults(OutputContext outputContext, Simulation[] simulations) {
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
	AsynchClientTask[] taskArray = showSimulationResults0(false);
	ClientTaskDispatcher.dispatch(getDocumentWindowManager().getComponent(), hashTable, taskArray, false, true, null);
}

private AsynchClientTask[] showSimulationResults0(final boolean isLocal) {

	// Create the AsynchClientTasks 
	ArrayList<AsynchClientTask> taskList = new ArrayList<AsynchClientTask>();
	final String dataViewerControllers_string = "dataViewerControllers";
	final String failures_string = "failures";

	final DocumentWindowManager documentWindowManager = getDocumentWindowManager();
	AsynchClientTask retrieveResultsTask = new AsynchClientTask("Retrieving results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING)  {
		@SuppressWarnings("unchecked")
		public void run(Hashtable<String, Object> hashTable) throws Exception {						
			Simulation[] simsToShow = (Simulation[])hashTable.get("simsArray");
			for (int i = 0; i < simsToShow.length; i++){		
				final Simulation sim  = simsToShow[i];
				final VCSimulationIdentifier vcSimulationIdentifier = sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
				final SimulationWindow simWindow = documentWindowManager.haveSimulationWindow(vcSimulationIdentifier);
				
				if (simWindow == null) {			
					Hashtable<Simulation,Throwable> failures = (Hashtable<Simulation,Throwable>)hashTable.get(failures_string);
					if (failures == null) {
						failures = new Hashtable<Simulation, Throwable>();
						hashTable.put(failures_string, failures);
					}
						
					Hashtable<VCSimulationIdentifier, DataViewerController> dataViewerControllers = (Hashtable<VCSimulationIdentifier, DataViewerController>)hashTable.get(dataViewerControllers_string);
					if (dataViewerControllers == null) {
						dataViewerControllers = new Hashtable<VCSimulationIdentifier, DataViewerController>();
						hashTable.put(dataViewerControllers_string, dataViewerControllers);
					}
				
					try {
						// make the manager and wire it up
						OutputContext outputContext = (OutputContext)hashTable.get("outputContext");
						DataViewerController dataViewerController = null;
						if (!isLocal) {
							dataViewerController = documentWindowManager.getRequestManager().getDataViewerController(outputContext,sim, 0);
							documentWindowManager.addDataListener(dataViewerController);//For changes in time or variable
						} else {
							// ---- preliminary : construct the localDatasetControllerProvider
							StdoutSessionLog sessionLog = new StdoutSessionLog("Local");
							File primaryDir = ResourceUtil.getLocalRootDir();
							User usr = sim.getVersion().getOwner();
							DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(sessionLog,null,primaryDir,null);
							ExportServiceImpl localExportServiceImpl = new ExportServiceImpl(sessionLog);
							LocalDataSetControllerProvider localDSCProvider = new LocalDataSetControllerProvider(sessionLog, usr, dataSetControllerImpl, localExportServiceImpl);
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
						dataViewerControllers.put(vcSimulationIdentifier, dataViewerController);
					} catch (Throwable exc) {
						exc.printStackTrace(System.out);
						failures.put(sim, exc);
					}
				}
			}			
		}	
	};
	taskList.add(retrieveResultsTask);
		
	AsynchClientTask displayResultsTask = new AsynchClientTask("Showing results", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@SuppressWarnings("unchecked")
		public void run(Hashtable<String, Object> hashTable) throws Exception {					
			Hashtable<VCSimulationIdentifier, DataViewerController> dataViewerControllers = (Hashtable<VCSimulationIdentifier, DataViewerController>)hashTable.get(dataViewerControllers_string);
			Hashtable<Simulation,Throwable> failures = (Hashtable<Simulation,Throwable>)hashTable.get(failures_string);
			Simulation[] simsToShow = (Simulation[])hashTable.get("simsArray");
			for (int i = 0; i < simsToShow.length; i++){
				final Simulation sim  = simsToShow[i];
				final VCSimulationIdentifier vcSimulationIdentifier = simsToShow[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();				
				final SimulationWindow simWindow = documentWindowManager.haveSimulationWindow(vcSimulationIdentifier);
				if (simWindow != null) {
					ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(documentWindowManager.getComponent());
					ChildWindow childWindow = childWindowManager.getChildWindowFromContext(simWindow);
					if (childWindow==null){
						childWindow = childWindowManager.addChildWindow(simWindow.getDataViewer(), simWindow, simWindow.getTitle());
					}
					childWindow.pack();
					childWindow.setIsCenteredOnParent();
					childWindow.show();
//					JInternalFrame existingFrame = simWindow.getFrame();
//					documentWindowManager.showFrame(existingFrame);
				} else {					
					// wire it up the viewer
					DataViewerController viewerController = dataViewerControllers.get(vcSimulationIdentifier);
					Throwable ex = failures.get(sim); 
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
					}
				}
			}
			if (failures != null) {
				if (!failures.isEmpty()) {
					Enumeration<Simulation> en = failures.keys();
					while (en.hasMoreElements()) {
						Simulation sim = en.nextElement();
						Throwable exc = (Throwable)failures.get(sim);
						// notify user
						PopupGenerator.showErrorDialog(ClientSimManager.this.getDocumentWindowManager(), "Failed to retrieve results for simulation '"+sim.getName()+"':\n"+exc.getMessage(), exc);
					}
				}
			}
		}			
	};
	taskList.add(displayResultsTask);

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
	AsynchClientTask[] tasks = null;	
	if (simulationOwner instanceof SimulationContext) {
		AsynchClientTask[] updateTask = ClientRequestManager.updateMath(documentWindowManager.getComponent(), ((SimulationContext)simulationOwner), false);
		tasks = new AsynchClientTask[updateTask.length + 1];
		System.arraycopy(updateTask, 0, tasks, 0, updateTask.length);
	} else {
		tasks = new AsynchClientTask[1];
	}
		
	tasks[tasks.length - 1] = new AsynchClientTask("starting particle view", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
		
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
	ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), tasks, false, true, null);
}

@SuppressWarnings("serial")
private class TempSimulation extends Simulation {
	final private SimulationVersion tempSimVersion = SimulationVersion.createTempSimulationVersion();
	public TempSimulation(Simulation simulation, boolean bCloneMath) {
		super(simulation, bCloneMath);
	}

	@Override
	public Version getVersion() {
		return tempSimVersion;
	}

	@Override
	public String getSimulationID() {
		return createSimulationID(tempSimVersion.getVersionKey());
	}

	@Override
	public SimulationInfo getSimulationInfo() {
		return new SimulationInfo(null, tempSimVersion, VCellSoftwareVersion.fromSystemProperty());
	}
}


public void runQuickSimulation(final Simulation originalSimulation) {
	ArrayList<AsynchClientTask> taskList = new ArrayList<AsynchClientTask>();
	final SimulationOwner simulationOwner = simWorkspace.getSimulationOwner();
	
	// ----------- update math if it is from biomodel (simulationContext)
	if (simulationOwner instanceof SimulationContext) {
		AsynchClientTask[] updateTasks = ClientRequestManager.updateMath(documentWindowManager.getComponent(), ((SimulationContext)simulationOwner), false);
		for (AsynchClientTask task : updateTasks) {
			taskList.add(task);
		}
	}	
	
	// ----------- run simulation(s)
	final File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());	
	AsynchClientTask runSimTask = new AsynchClientTask("running simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
						
			Simulation simulation = new TempSimulation(originalSimulation, false);
			StdoutSessionLog log = new StdoutSessionLog("Quick run");
			SimulationTask simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
			SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
			if (!solverDescription.licensedLibrary.isLicensed()) {
				
			}
			if (!solverDescription.licensedLibrary.isLicensed()) {
				LicenseManager.promptForLicense(getDocumentWindowManager().getComponent(),solverDescription.licensedLibrary,true);
			}
			Solver solver = createQuickRunSolver(log, localSimDataDir, simTask);
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
					getClientTaskStatusSupport().setMessage(event.getSimulationMessage().getDisplayMessage());
				}
				public void solverProgress(SolverEvent event) {
					getClientTaskStatusSupport().setMessage("Running...");
					getClientTaskStatusSupport().setProgress((int)(event.getProgress() * 100));
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
						throw new RuntimeException(solverStatus.getSimulationMessage().getDisplayMessage());
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
	AsynchClientTask[] showResultsTask = showSimulationResults0(true);
	for (AsynchClientTask task : showResultsTask) {
		taskList.add(task);
	}

	// ------- dispatch
	AsynchClientTask[] taskArray = new AsynchClientTask[taskList.size()];
	taskList.toArray(taskArray);
	ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), taskArray, true, true, null);
}


private Solver createQuickRunSolver(StdoutSessionLog sessionLog, File directory, SimulationTask simTask) throws SolverException, IOException {
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
	Solver solver = SolverFactory.createSolver(sessionLog, directory, simTask, false);

	return solver;
}

}
