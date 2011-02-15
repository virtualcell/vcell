package cbit.vcell.client;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.vcell.sybil.gui.space.DialogParentProvider;
import org.vcell.sybil.gui.space.GUIJInternalFrameSpace;
import org.vcell.sybil.init.SybilApplication;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.JTaskBar;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.ApplicationComponents;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.MathematicsPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.gui.GeometryViewer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.opt.solvers.LocalOptimizationService;
import cbit.vcell.opt.solvers.OptimizationService;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.xml.gui.MIRIAMAnnotationViewer;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:17:07 PM)
 * @author: Ion Moraru
 */
public class BioModelWindowManager extends DocumentWindowManager implements java.beans.PropertyChangeListener, java.awt.event.ActionListener {	
	private BioModel bioModel = null;
	private Hashtable<SimulationContext, ApplicationComponents> applicationsHash = new Hashtable<SimulationContext, ApplicationComponents>();
	private JDesktopPaneEnhanced jDesktopPane = null;
	private BioModelEditor bioModelEditor = null;
	private Vector<JInternalFrame> dataViewerPlotsFramesVector = new Vector<JInternalFrame>();

	private LocalOptimizationService localOptService = null;
	
	private SybilApplication sybilApplication = null;
	
	private JInternalFrame mIRIAMAnnotationEditorFrame = null;
	private JInternalFrame sybilFrame = null;
	private PropertyChangeListener miriamPropertyChangeListener =
		new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				closeMIRIAMWindow();
			}};
	private VetoableChangeListener miriamVetoableChangeListener =
		new VetoableChangeListener(){
			public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
				closeMIRIAMWindow();
			}};


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 8:31:18 PM)
 * @param documentWindow cbit.vcell.client.desktop.DocumentWindow
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
public BioModelWindowManager(JPanel panel, RequestManager requestManager, final BioModel bioModel, int newlyCreatedDesktops) {
	super(panel, requestManager, bioModel, newlyCreatedDesktops);
	setJDesktopPane(new JDesktopPaneEnhanced());
	getJPanel().setLayout(new BorderLayout());
	getJPanel().add(getJDesktopPane(), BorderLayout.CENTER);
	if (!UIManager.getBoolean("InternalFrame.useTaskBar")) {
		JTaskBar taskBar = new JTaskBar(getJDesktopPane());
		getJPanel().add(taskBar, BorderLayout.SOUTH);
	}
	setBioModel(bioModel);
	setBioModelEditor(new BioModelEditor());
	createBioModelFrame();
}

	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	
	String actionCommand = e.getActionCommand();
	final Object source = e.getSource();
	
//	if(source == this && actionCommand.equals(GuiConstants.ACTIONCMD_EDIT_OCCURRED_GEOMETRY)){
//		ApplicationComponents applicationComponents = findAppComponentsForSimContextGeomViewer((ApplicationEditor)source);
//		if(applicationComponents != null){
//			showSurfaceViewerFrame((SimulationContext)applicationComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner(), false);
//		}
//	}
//
	if(source instanceof GeometryViewer && actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_GEOMETRY)){
		final GeometryViewer geometryViewer = (GeometryViewer)source;
		
		AsynchClientTask oldEditorTask = new AsynchClientTask("Show Old Editor",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry newGeom = (Geometry)hashTable.get("doc");
				if(newGeom == null){
					throw new IllegalArgumentException("No template geometry found during create.");
				}
				Boolean bShowOldGeomEditor = (Boolean)hashTable.get(DocumentWindowManager.B_SHOW_OLD_GEOM_EDITOR);
				if(bShowOldGeomEditor){
					GeometryViewer localGeometryViewer = new GeometryViewer(false);
					localGeometryViewer.setGeometry(newGeom);
					localGeometryViewer.setPreferredSize(new Dimension(700,500));
					int result = DialogUtils.showComponentOKCancelDialog(getComponent(), localGeometryViewer, "Edit Geometry: '"+/*origGeom*/newGeom.getName()+"'");
					localGeometryViewer.setGeometry(null);//force cleanup so localGeometryViewer can be garbage collected
					if(result != JOptionPane.OK_OPTION){
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
			}
		};
		AsynchClientTask precomputeAllTask = new AsynchClientTask("precomputeAll geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry newGeom = (Geometry)hashTable.get("doc");
				if(newGeom != null){
					newGeom.precomputeAll();
				}
			}
		};

		AsynchClientTask setGeomOnSimContextTask = new AsynchClientTask("Set Geometry On SimContext",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry newGeom = (Geometry)hashTable.get("doc");
				for (SimulationContext simulationContext : getBioModel().getSimulationContexts()) {
					if (simulationContext == geometryViewer.getGeometryOwner()) {
						if(newGeom.getName() == null){
							newGeom.setName(
								getBioModel().getName()+"_"+
								(simulationContext.getName()+"_"+
								ClientRequestManager.generateDateTimeString()));
						}
						simulationContext.setGeometry(newGeom);
						return;
					} 
				}
				Geometry origGeom = geometryViewer.getGeometryOwner().getGeometry();
				throw new IllegalArgumentException(
					"Couldn't find matching application editor for orig geom '"+origGeom.getName()+"' key="+origGeom.getKey()+" in application hash.");
			}
		};

		Geometry currentGeometry = geometryViewer.getGeometryOwner().getGeometry();
		createGeometry(currentGeometry,
				new AsynchClientTask[] {oldEditorTask,precomputeAllTask,setGeomOnSimContextTask}
				,TopLevelWindowManager.DEFAULT_CREATEGEOM_SELECT_DIALOG_TITLE,"Apply Geometry");
	}
	
	if (source instanceof MathematicsPanel && actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_MATH_MODEL)) {
		SimulationContext sc = ((MathematicsPanel)source).getSimulationContext();
		getRequestManager().createMathModelFromApplication(this, "Untitled", sc);
	}
////	if (source instanceof ApplicationEditor && actionCommand.equals(GuiConstants.ACTIONCMD_VIEW_CHANGE_GEOMETRY)) {
////		SimulationContext sc = (SimulationContext)((ApplicationEditor)source).getSimulationWorkspace().getSimulationOwner();
////		showGeometryViewerFrame(sc);
////	}
	if (source instanceof GeometryViewer && actionCommand.equals(GuiConstants.ACTIONCMD_CHANGE_GEOMETRY)) {
		final GeometryViewer geometryViewer = (GeometryViewer)source;
		getRequestManager().changeGeometry(this,(SimulationContext)geometryViewer.getGeometryOwner());
	}
}


//private ApplicationComponents findAppComponentsForSimContextGeomViewer(GeometrySummaryViewer geometrySummaryViewer){
//		Enumeration<ApplicationComponents> appComponentsEnum = getApplicationsHash().elements();
//		while (appComponentsEnum.hasMoreElements()) {
//			ApplicationComponents appComponents = (ApplicationComponents)appComponentsEnum.nextElement();
//			ApplicationEditor appEditor = appComponents.getAppEditor();
//			if (appComponents.get == applicationEditor) {
//				SimulationOwner simOwner  = (SimulationOwner)appComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner();
//				if (simOwner instanceof SimulationContext) {
//					return appComponents;
//				} 
//			}
//		}
//		Geometry geom = applicationEditor.getSimulationWorkspace().getSimulationOwner().getGeometry();
//		DialogUtils.showErrorDialog(getComponent(), "Geometry "+(geom!= null?geom.getName():null)+" key="+(geom != null?geom.getVersion().getVersionKey():null)+" not found in application hash");
//
//		return null;
//}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:32:07 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void addResultsFrame(SimulationWindow simWindow) {
//	if (!getApplicationsHash().containsKey(simWindow.getSimOwner())) {
//			// create components
//		createAppComponents((SimulationContext) simWindow.getSimOwner());
//	}
	
	ApplicationComponents appComponents = getApplicationsHash().get(simWindow.getSimOwner());
	appComponents.addDataViewer(simWindow);
	simWindow.getFrame().setLocation(10,10);
	showFrame(simWindow.getFrame());
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 2:33:41 AM)
 */
private void updateApplicationHash(boolean reset) {
	SimulationContext[] scs = getBioModel().getSimulationContexts();
	
	Enumeration<SimulationContext> en = getApplicationsHash().keys();
	while (en.hasMoreElements()) {
		SimulationContext sc = (SimulationContext)en.nextElement();
		ApplicationComponents appComponents = getApplicationsHash().get(sc);
		if (!getBioModel().contains(sc)) {
			if (reset) {
				// find one with the same name, if available
				SimulationContext found = null;
				if (scs != null) {
					for (int i = 0; i < scs.length; i++){
						if (scs[i].getName().equals(sc.getName())) {
							found = scs[i];
							break;
						}
					}
				}
				if (found != null) {
					// update hash
					getApplicationsHash().remove(sc);
					getApplicationsHash().put(found, appComponents);
					
					appComponents.resetSimulationContext(found);
					// check simulation data windows
					updateSimulationDataViewers(appComponents, found);
					// rewire listener
					sc.removePropertyChangeListener(this);
					found.removePropertyChangeListener(this);
					found.addPropertyChangeListener(this);
				} else {
					// we didn't find one, so remove from hash and close all of its windows
					remove(appComponents, sc);
				}
			} else {
				// shouldn't have it
				remove(appComponents, sc);
			}
		}
	}
}
				
/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 1:13:06 PM)
 */
private void updateSimulationDataViewers(ApplicationComponents appComponents, SimulationContext found) {
	SimulationWindow[] simWindows = appComponents.getSimulationWindows();
	Simulation[] sims = found.getSimulations();
	Hashtable<VCSimulationIdentifier, Simulation> hash = new Hashtable<VCSimulationIdentifier, Simulation>();
	for (int i = 0; i < sims.length; i++){
		SimulationInfo simInfo = sims[i].getSimulationInfo();
		if (simInfo != null) {
			VCSimulationIdentifier vcSimulationIdentifier = simInfo.getAuthoritativeVCSimulationIdentifier();
			hash.put(vcSimulationIdentifier, sims[i]);
		}
	}
	for (int i = 0; i < simWindows.length; i++){
		if (hash.containsKey(simWindows[i].getVcSimulationIdentifier())) {
			simWindows[i].resetSimulation((Simulation)hash.get(simWindows[i].getVcSimulationIdentifier()));
		} else {
			close(simWindows[i].getFrame(), getJDesktopPane());
		}
	}
}


/**
 * create components
 */
private void createAppComponents(SimulationContext simContext) {
	ApplicationComponents appComponents = new ApplicationComponents(simContext, this, getJDesktopPane());
	getApplicationsHash().put(simContext, appComponents);
	// register for events
	simContext.addPropertyChangeListener(this);
//	appComponents.getAppEditor().addActionListener(this);
//	appComponents.getGeometrySummaryViewer().addActionListener(this);
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void createBioModelFrame() {	
	getBioModelEditor().setBioModelWindowManager(this);
	getBioModelEditor().setBioModel(getBioModel());
	JInternalFrameEnhanced editorFrame = new JInternalFrameEnhanced("BioModel", true, false, true, true);
	editorFrame.setFrameIcon(new ImageIcon(getClass().getResource("/images/bioModel_16x16.gif")));	
	editorFrame.add(bioModelEditor);
	getJDesktopPane().add(editorFrame);
	editorFrame.setMinimumSize(new Dimension(400, 300));
	editorFrame.setSize(850, 680);
	editorFrame.setLocation(0,0);
	editorFrame.show();
	try {
		editorFrame.setMaximum(true);
	} catch (PropertyVetoException e) {
		e.printStackTrace();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 2:33:22 AM)
 * @return java.util.Hashtable
 */
private java.util.Hashtable<SimulationContext, ApplicationComponents> getApplicationsHash() {
	return applicationsHash;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 8:37:05 PM)
 * @return cbit.vcell.biomodel.BioModel
 */
private BioModel getBioModel() {
	return bioModel;
}

public ApplicationComponents getApplicationComponents(SimulationContext simulationContext) {
	if (!getApplicationsHash().containsKey(simulationContext)) {
		// create components
		updateApplicationHash(true);
		if (!getApplicationsHash().containsKey(simulationContext)) {
			createAppComponents(simulationContext);
		}
	}
	return getApplicationsHash().get(simulationContext);
}

/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 1:02:55 AM)
 * @return cbit.vcell.client.desktop.biomodel.BioModelEditor
 */
private BioModelEditor getBioModelEditor() {
	return bioModelEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:49:01 PM)
 * @return javax.swing.JDesktopPane
 */
protected JDesktopPaneEnhanced getJDesktopPane() {
	return jDesktopPane;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:46:17 PM)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel() {
	return (JPanel)getComponent();
}


/**
 * Insert the method's description here.
 * Creation date: (12/19/2005 9:45:48 PM)
 * @return cbit.vcell.opt.solvers.OptimizationService
 */
public OptimizationService getOptimizationService() {
	if (localOptService==null){
		localOptService = new LocalOptimizationService();
	}

	return localOptService;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 3:41:06 PM)
 * @return cbit.vcell.document.VCDocument
 */
public VCDocument getVCDocument() {
	return getBioModel();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:57:44 AM)
 * @return cbit.vcell.document.VCDocument
 */
SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
	Enumeration<ApplicationComponents> en = getApplicationsHash().elements();
	while (en.hasMoreElements()) {
		ApplicationComponents appComponents = en.nextElement();
		SimulationWindow window = appComponents.haveSimulationWindow(vcSimulationIdentifier);
		if (window != null) {
			return window;
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:31:38 PM)
 * @return boolean
 */
public boolean isRecyclable() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 11:17:41 PM)
 */
//public void preloadApps() {
////System.out.println("+++++++++++"+(new Date(System.currentTimeMillis())));
//	SimulationContext[] scs = getBioModel().getSimulationContexts();
//	for (int i = 0; i < scs.length; i++){
//		createAppComponents(scs[i]);
//	}
////System.out.println("+++++++++++"+(new Date(System.currentTimeMillis())));
//}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getBioModel() && evt.getPropertyName().equals("simulationContexts")) {
		// close any window we should not have anymore
		updateApplicationHash(false);
	}
	if (evt.getSource() == getBioModel() && evt.getPropertyName().equals("simulations")) {
		// close any window we should not have anymore
		Enumeration<SimulationContext> en = getApplicationsHash().keys();
		while (en.hasMoreElements()) {
			SimulationContext sc = (SimulationContext)en.nextElement();
			ApplicationComponents appComponents = getApplicationsHash().get(sc);
			updateSimulationDataViewers(appComponents, sc);
			appComponents.cleanSimWindowsHash();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 1:20:40 PM)
 */
private void remove(ApplicationComponents appComponents, SimulationContext sc) {
	sc.removePropertyChangeListener(this);
	getApplicationsHash().remove(sc);
	close(appComponents.getDataViewerFrames(), getJDesktopPane());
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:40:45 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void resetDocument(VCDocument newDocument) {
	setBioModel((BioModel)newDocument);
	setDocumentID(getBioModel());
	updateApplicationHash(true);
	getBioModelEditor().setBioModel(getBioModel());
	Enumeration<JInternalFrame> en = dataViewerPlotsFramesVector.elements();
	while (en.hasMoreElements()) {
		close((JInternalFrame)en.nextElement(), getJDesktopPane());
	}
	getRequestManager().updateStatusNow();
}

/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 8:37:05 PM)
 * @param newBioModel cbit.vcell.biomodel.BioModel
 */
private void setBioModel(BioModel newBioModel) {
	if(getBioModel() == newBioModel) {
		return;
	}
	refreshMIRIAMDependencies(getBioModel(), newBioModel);
	if (getBioModel() != null) {
		getBioModel().removePropertyChangeListener(this);
	}
	bioModel = newBioModel;
	if (getBioModel() != null) {
		getBioModel().addPropertyChangeListener(this);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 1:02:55 AM)
 * @param newBioModelEditor cbit.vcell.client.desktop.biomodel.BioModelEditor
 */
private void setBioModelEditor(BioModelEditor newBioModelEditor) {
	bioModelEditor = newBioModelEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:49:01 PM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void setJDesktopPane(JDesktopPaneEnhanced newJDesktopPane) {
	jDesktopPane = newJDesktopPane;
}

//public void showApplicationFrame(final SimulationContext simContext) {
//	AsynchClientTask task1 = new AsynchClientTask("preload the application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//		
//		@Override
//		public void run(Hashtable<String, Object> hashTable) throws Exception {
//			if (!getApplicationsHash().containsKey(simContext)) {
//				simContext.getGeometry().precomputeAll();
//				Simulation[] simulations = simContext.getSimulations();
//				if (simulations != null) {
//					// preload simulation status
//					ArrayList<VCSimulationIdentifier> simIDs = new ArrayList<VCSimulationIdentifier>();
//					for (int i = 0; i < simulations.length; i++){
//						if (simulations[i].getSimulationInfo()!=null){
//							simIDs.add(simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier());
//						}
//					}
//					getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs.toArray(new VCSimulationIdentifier[simIDs.size()]));
//				}
//			}
//		}
//	};
//		
//	AsynchClientTask task2 = new AsynchClientTask("show application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
//		
//		@Override
//		public void run(Hashtable<String, Object> hashTable) throws Exception {
//			if (! getApplicationsHash().containsKey(simContext)) {
//				// create components
//				createAppComponents(simContext);
//			}
//		}
//	};
//	ClientTaskDispatcher.dispatch(getJDesktopPane(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });		
//	
//}

/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:55:40 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
private void showDataViewerPlotsFrame(final javax.swing.JInternalFrame plotFrame) {
	dataViewerPlotsFramesVector.add(plotFrame);
	showFrame(plotFrame);
	plotFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
			dataViewerPlotsFramesVector.remove(plotFrame);
		}
	});
}
	
/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:55:40 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void showDataViewerPlotsFrames(javax.swing.JInternalFrame[] plotFrames) {
	for (int i = 0; i < plotFrames.length; i++){
		showDataViewerPlotsFrame(plotFrames[i]);
	}
}
	
/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:40:41 PM)
 */
public void showFrame(javax.swing.JInternalFrame frame) {
	showFrame(frame,getJDesktopPane());
}


///**
// * Insert the method's description here.
// * Creation date: (5/5/2004 9:44:15 PM)
// */
//private void showGeometryViewerFrame(SimulationContext simContext) {
//	JInternalFrameEnhanced editorFrame = null;
//	if (getApplicationsHash().containsKey(simContext)) {
//		editorFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getGeometrySummaryViewerFrame();
//		setDefaultTitle(editorFrame);
//		showFrame(editorFrame);
//	}
//	editorFrame.requestFocus();
//}

/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:58:21 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simStatusChanged(SimStatusEvent simStatusEvent) {
	// ** events are only generated from server side job statuses **
	KeyValue simKey = simStatusEvent.getVCSimulationIdentifier().getSimulationKey();
	// do we have the sim?
	Simulation[] sims = getBioModel().getSimulations();
	if (sims == null) {
		// we don't have it
		return;
	}
	Simulation simulation = null;
	for (int i = 0; i < sims.length; i++){
		if (sims[i].getSimulationInfo() != null && simKey.equals(sims[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier().getSimulationKey())) {
			simulation = sims[i];
			break;
		}	
	}
	if (simulation == null) {
		// we don't have it
		return;
	}
	// we have it; get current server side status
	SimulationStatus simStatus = getRequestManager().getServerSimulationStatus(simulation.getSimulationInfo());
	// if failed, notify
	if (simStatusEvent.isNewFailureEvent()) {
		String qualifier = "";
		if (simulation.getScanCount() > 1) {
			qualifier += "One job from ";
		}
		PopupGenerator.showErrorDialog(this, qualifier + "Simulation '" + simulation.getName() + "' failed\n" + simStatus.getDetails());
	}
	// was the gui on it ever opened?
	SimulationContext simContext = null;
	simulation = null;
	Enumeration<SimulationContext> en = getApplicationsHash().keys();
	while (en.hasMoreElements()) {
		SimulationContext sc = (SimulationContext)en.nextElement();
		sims = sc.getSimulations();
		if (sims != null) {
		}
		for (int i = 0; i < sims.length; i++){
		if (sims[i].getSimulationInfo() != null && simKey.equals(sims[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier().getSimulationKey())) {
				simulation = sims[i];
				break;
			}	
		}
		if (simulation != null) {
			simContext = sc;
			break;
		}
	}
	if (simulation == null || simContext == null) {
		return;
	}
	// the gui was opened, update status display
	ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(simContext);
	ClientSimManager simManager = appComponents.getSimulationWorkspace().getClientSimManager();
	simManager.updateStatusFromServer(simulation);
	// is there new data?
	if (simStatusEvent.isNewDataEvent()) {
		fireNewData(new DataEvent(this, new VCSimulationDataIdentifier(simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simStatusEvent.getJobIndex())));
	}
}


private void closeMIRIAMWindow(){
	try{
		if(mIRIAMAnnotationEditorFrame != null && getJDesktopPane() != null){
			close(mIRIAMAnnotationEditorFrame,getJDesktopPane());
		}
	}catch(Exception e){
		//ignore
	}
}

//private void closeSybilWindow(){
//	try{
//		if(sybilFrame != null && getJDesktopPane() != null){
//			close(sybilFrame,getJDesktopPane());
//		}
//	}catch(Exception e){
//		//ignore
//	}
//}

protected void refreshMIRIAMDependencies(BioModel oldBioModel,BioModel newBioModel){
	try {
		if (oldBioModel != null) {
			oldBioModel
					.removePropertyChangeListener(miriamPropertyChangeListener);
			oldBioModel
					.removeVetoableChangeListener(miriamVetoableChangeListener);
			if (oldBioModel.getModel() != null) {
				oldBioModel.getModel().removePropertyChangeListener(
						miriamPropertyChangeListener);
				oldBioModel.getModel().removeVetoableChangeListener(
						miriamVetoableChangeListener);
				for (int i = 0; i < oldBioModel.getModel().getStructures().length; i++) {
					oldBioModel.getModel().getStructures()[i]
							.removePropertyChangeListener(miriamPropertyChangeListener);
					oldBioModel.getModel().getStructures()[i]
							.removeVetoableChangeListener(miriamVetoableChangeListener);
				}
				for (int i = 0; i < oldBioModel.getModel()
						.getReactionSteps().length; i++) {
					oldBioModel.getModel().getReactionSteps()[i]
							.removePropertyChangeListener(miriamPropertyChangeListener);
					oldBioModel.getModel().getReactionSteps()[i]
							.removeVetoableChangeListener(miriamVetoableChangeListener);
				}
				for (int i = 0; i < oldBioModel.getModel()
						.getSpeciesContexts().length; i++) {
					oldBioModel.getModel().getSpeciesContexts()[i]
							.removePropertyChangeListener(miriamPropertyChangeListener);
					oldBioModel.getModel().getSpeciesContexts()[i]
							.removeVetoableChangeListener(miriamVetoableChangeListener);
				}
				for (int i = 0; i < oldBioModel.getModel().getSpecies().length; i++) {
					oldBioModel.getModel().getSpecies()[i]
							.removePropertyChangeListener(miriamPropertyChangeListener);
					oldBioModel.getModel().getSpecies()[i]
							.removeVetoableChangeListener(miriamVetoableChangeListener);
				}
			}
		}
		if (newBioModel != null) {
			newBioModel
					.addPropertyChangeListener(miriamPropertyChangeListener);
			newBioModel
					.addVetoableChangeListener(miriamVetoableChangeListener);
			if (newBioModel.getModel() != null) {
				newBioModel.getModel().addPropertyChangeListener(
						miriamPropertyChangeListener);
				newBioModel.getModel().addVetoableChangeListener(
						miriamVetoableChangeListener);
				for (int i = 0; i < newBioModel.getModel().getStructures().length; i++) {
					newBioModel.getModel().getStructures()[i]
							.addPropertyChangeListener(miriamPropertyChangeListener);
					newBioModel.getModel().getStructures()[i]
							.addVetoableChangeListener(miriamVetoableChangeListener);
				}
				for (int i = 0; i < newBioModel.getModel()
						.getReactionSteps().length; i++) {
					newBioModel.getModel().getReactionSteps()[i]
							.addPropertyChangeListener(miriamPropertyChangeListener);
					newBioModel.getModel().getReactionSteps()[i]
							.addVetoableChangeListener(miriamVetoableChangeListener);
				}
				for (int i = 0; i < newBioModel.getModel()
						.getSpeciesContexts().length; i++) {
					newBioModel.getModel().getSpeciesContexts()[i]
							.addPropertyChangeListener(miriamPropertyChangeListener);
					newBioModel.getModel().getSpeciesContexts()[i]
							.addVetoableChangeListener(miriamVetoableChangeListener);
				}
				for (int i = 0; i < newBioModel.getModel().getSpecies().length; i++) {
					newBioModel.getModel().getSpecies()[i]
							.addPropertyChangeListener(miriamPropertyChangeListener);
					newBioModel.getModel().getSpecies()[i]
							.addVetoableChangeListener(miriamVetoableChangeListener);
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		//ignore
	}

}

public void showMIRIAMWindow() {

	if(mIRIAMAnnotationEditorFrame == null){
		MIRIAMAnnotationViewer miriamAnnotationViewer = new MIRIAMAnnotationViewer();
		miriamAnnotationViewer.setBiomodel(bioModel);
		mIRIAMAnnotationEditorFrame = new JInternalFrame();
		mIRIAMAnnotationEditorFrame.setTitle("View/Add/Delete/Edit MIRIAM Annotation");
		mIRIAMAnnotationEditorFrame.addInternalFrameListener (
				new InternalFrameListener(){
					public void internalFrameActivated(InternalFrameEvent e) {
					}
					public void internalFrameClosed(InternalFrameEvent e) {
					}
					public void internalFrameClosing(InternalFrameEvent e) {
						close(mIRIAMAnnotationEditorFrame, getJDesktopPane());
					}
					public void internalFrameDeactivated(InternalFrameEvent e) {
					}
					public void internalFrameDeiconified(InternalFrameEvent e) {
					}
					public void internalFrameIconified(InternalFrameEvent e) {
					}
					public void internalFrameOpened(InternalFrameEvent e) {
					}
				});

		mIRIAMAnnotationEditorFrame.getContentPane().add(miriamAnnotationViewer);
		mIRIAMAnnotationEditorFrame.setSize(600,400);
		mIRIAMAnnotationEditorFrame.setClosable(true);
		mIRIAMAnnotationEditorFrame.setResizable(true);
	}

	if(!mIRIAMAnnotationEditorFrame.isShowing()){
		((MIRIAMAnnotationViewer)mIRIAMAnnotationEditorFrame.getContentPane().getComponent(0)).setBiomodel(bioModel);
	}

//	showDataViewerPlotsFrame(mIRIAMAnnotationEditorFrame);
	showFrame(mIRIAMAnnotationEditorFrame);
}


public void showSybilWindow() {

	boolean firstTime = (sybilFrame==null);
	if(sybilFrame == null){
		sybilFrame = new JInternalFrame();
		sybilFrame.setTitle("Sybil Application Window");
		sybilFrame.setSize(800,600);
		sybilFrame.setClosable(true);
		sybilFrame.setResizable(true);
		DialogParentProvider dialogParentProvider = new DialogParentProvider(){
			public Component getDialogParent() {
				return ((JPanel)BioModelWindowManager.this.getComponent()).getTopLevelAncestor();
			}
		};
		BioModel bioModel = getBioModel();
		sybilApplication = 
			new SybilApplication(new GUIJInternalFrameSpace(dialogParentProvider, sybilFrame), bioModel);
	}

//	if(!sybilFrame.isShowing()){
//		sybilApplication.setBioModel(getBioModel());
//	}
//
	showFrame(sybilFrame);
	if (firstTime){
		sybilApplication.runVCell();
	}
}

void prepareToLoad(BioModel doc) throws Exception {
	if (applicationsHash.size() == 0) {
		return;
	}
	if (!doc.getName().equals(getVCDocument().getName())) {
		return;
	}
	ArrayList<Simulation> simulations = new ArrayList<Simulation>();
	BioModel bioModel = (BioModel)doc;
	SimulationContext[] simContexts = bioModel.getSimulationContexts();
	Enumeration<SimulationContext> openApps = applicationsHash.keys();
	while (openApps.hasMoreElements()) {
		SimulationContext openApp = openApps.nextElement();
		for (SimulationContext simContext : simContexts) {
			if (openApp.getName().equals(simContext.getName())) {// same application
				simContext.getGeometry().precomputeAll();
				simulations.addAll(Arrays.asList(simContext.getSimulations()));
				break;
			}
		}
	}
	
	if (simulations.size() > 0) {	
		// preload simulation status
		VCSimulationIdentifier simIDs[] = new VCSimulationIdentifier[simulations.size()];
		for (int i = 0; i < simulations.size(); i++){
			simIDs[i] = simulations.get(i).getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
		}
		getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs);
	}
}

public boolean hasBlankDocument() {
	return !getRequestManager().isDifferentFromBlank(VCDocument.BIOMODEL_DOC, getVCDocument());
}

@Override
public void updateConnectionStatus(ConnectionStatus connStatus) {
	bioModelEditor.updateConnectionStatus(connStatus);	
}

}