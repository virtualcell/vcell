package cbit.vcell.client;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.ApplicationComponents;
import cbit.vcell.client.desktop.biomodel.ApplicationEditor;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.document.SimulationOwner;
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
	setBioModel(bioModel);
	setBioModelEditor(new BioModelEditor());
	createBioModelFrame();
}


private void editSelectGeometry(boolean bShowOldGeomEditor/*DocumentCreationInfo documentCreationInfo*/,Geometry newGeom,ApplicationEditor applicationEditor){
	SimulationOwner simulationOwner = applicationEditor.getSimulationWorkspace().getSimulationOwner();
	Geometry origGeom = simulationOwner.getGeometry();
	if(bShowOldGeomEditor
			/*documentCreationInfo == null || !ClientRequestManager.isImportGeometryType(documentCreationInfo)*/){
		GeometryViewer localGeometryViewer = new GeometryViewer();
		localGeometryViewer.setGeometry(newGeom);
		localGeometryViewer.setSize(800,600);
		int result = DialogUtils.showComponentOKCancelDialog(getComponent(), localGeometryViewer, "Edit Geometry: '"+/*origGeom*/newGeom.getName()+"'");
		if(result != JOptionPane.OK_OPTION){
			throw UserCancelException.CANCEL_GENERIC;
		}
	}
 		Enumeration<ApplicationComponents> appComponentsEnum = getApplicationsHash().elements();
		while (appComponentsEnum.hasMoreElements()) {
			ApplicationComponents appComponents = (ApplicationComponents)appComponentsEnum.nextElement();
			ApplicationEditor appEditor = appComponents.getAppEditor();
			if (appEditor == applicationEditor) {
				if (simulationOwner instanceof SimulationContext) {
					try {
						if(newGeom.getName() == null){
							newGeom.setName(
								getBioModel().getName()+"_"+
								((SimulationContext)simulationOwner).getName()+"_"+
								ClientRequestManager.generateDateTimeString());
						}
						showSurfaceViewerFrame((SimulationContext)simulationOwner, false);
						((SimulationContext)simulationOwner).setGeometry(newGeom);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						DialogUtils.showErrorDialog(getComponent(), e1.getMessage(), e1);
					}
					return;
				} 
			}
		DialogUtils.showErrorDialog(getComponent(), "Geometry "+origGeom.getName()+" key="+origGeom.getVersion().getVersionKey()+" not found in application hash");
	}
}

//private void createGeometry(final GeometrySummaryViewer source){
//		
//	AsynchClientTask editSelectTask = new AsynchClientTask("Edit/Apply Geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
//		@Override
//		public void run(Hashtable<String, Object> hashTable) throws Exception {
//			Geometry newGeom = (Geometry)hashTable.get("doc");
//			if(newGeom != null){
//				editSelectGeometry(newGeom, (GeometrySummaryViewer)source);
//			}else{
//				DialogUtils.showErrorDialog(getComponent(), "No Geometry found in edit task");
//			}
//		}
//	};
//
//	try{
//		final DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo = selectGeometry();
//		final Hashtable<String, Object> hash = new Hashtable<String, Object>();
//		AsynchClientTask[] createGeomTaskArr = null;
//		if(geometrySelectionInfo.getDocumentCreationInfo() != null){
//			createGeomTaskArr = ((ClientRequestManager)getRequestManager()).createNewDocument(this, geometrySelectionInfo.getDocumentCreationInfo());
//			hash.put("guiParent", (Component)getComponent());
//			hash.put("requestManager", getRequestManager());
//		}else{
//			createGeomTaskArr = new AsynchClientTask[1]; 
//			createGeomTaskArr[0] = new AsynchClientTask("loading Geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//				@Override
//				public void run(Hashtable<String, Object> hashTable) throws Exception {
//					Geometry copiedGeom =
//						((ClientRequestManager)getRequestManager()).getGeometryFromDocumentSelection(geometrySelectionInfo.getVCDocumentInfo(),true);
//					hash.put("doc",copiedGeom);
//				}			
//			};
//
//		}
//		createGeomTaskArr = (AsynchClientTask[])BeanUtils.addElement(createGeomTaskArr, editSelectTask);
//		ClientTaskDispatcher.dispatch(getComponent(), hash, createGeomTaskArr, false);
//		
//	} catch (UserCancelException e1) {
//		return;
//	} catch (Exception e1) {
//		e1.printStackTrace();
//		DialogUtils.showErrorDialog(getComponent(), e1.getMessage());
//	}
//	
//
//
//}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	
	String actionCommand = e.getActionCommand();
	final Object source = e.getSource();
	
	if(source instanceof ApplicationEditor && actionCommand.equals(GuiConstants.ACTIONCMD_EDIT_OCCURRED_GEOMETRY)){
		ApplicationComponents applicationComponents = findAppComponentsForSimContextGeomViewer((ApplicationEditor)source);
		if(applicationComponents != null){
			showSurfaceViewerFrame((SimulationContext)applicationComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner(), false);
		}
	}

	if(source instanceof ApplicationEditor && actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_GEOMETRY)){
		AsynchClientTask editSelectTask = new AsynchClientTask("Edit/Apply Geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry newGeom = (Geometry)hashTable.get("doc");
				if(newGeom != null){
					editSelectGeometry(
							(Boolean)hashTable.get(DocumentWindowManager.B_SHOW_OLD_GEOM_EDITOR),
//						((DocumentWindowManager.GeometrySelectionInfo)
//						hashTable.get(DocumentWindowManager.GEOMETRY_SELECTIONINFO_KEY)).getDocumentCreationInfo(),
						newGeom, (ApplicationEditor)source);
				}else{
					DialogUtils.showErrorDialog(getComponent(), "No Geometry found in edit task");
				}
			}
		};

		Geometry currentGeometry =
			findAppComponentsForSimContextGeomViewer(
				(ApplicationEditor)source).getAppEditor().getSimulationWorkspace().getSimulationOwner().getGeometry();
		createGeometry(
				currentGeometry,
				new AsynchClientTask[] {editSelectTask});
	}
	
	if (source instanceof ApplicationEditor && actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_MATH_MODEL)) {
		SimulationContext sc = (SimulationContext)((ApplicationEditor)source).getSimulationWorkspace().getSimulationOwner();
		getRequestManager().createMathModelFromApplication(this, "Untitled", sc);
	}
//	if (source instanceof ApplicationEditor && actionCommand.equals(GuiConstants.ACTIONCMD_VIEW_CHANGE_GEOMETRY)) {
//		SimulationContext sc = (SimulationContext)((ApplicationEditor)source).getSimulationWorkspace().getSimulationOwner();
//		showGeometryViewerFrame(sc);
//	}
	if (source instanceof ApplicationEditor && actionCommand.equals(GuiConstants.ACTIONCMD_CHANGE_GEOMETRY)) {
		ApplicationComponents applicationComponents = findAppComponentsForSimContextGeomViewer((ApplicationEditor)source);
		if(applicationComponents != null){
			showSurfaceViewerFrame((SimulationContext)applicationComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner(), false);
			getRequestManager().changeGeometry(this,(SimulationContext)applicationComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner());
		}
	}
	if (source instanceof ApplicationEditor && actionCommand.equals(GuiConstants.ACTIONCMD_VIEW_SURFACES)) {
		ApplicationComponents applicationComponents = findAppComponentsForSimContextGeomViewer((ApplicationEditor)source);
		if(applicationComponents != null){
			applicationComponents.getSurfaceViewer().setGeometry(((ApplicationEditor)source).getSimulationWorkspace().getSimulationOwner().getGeometry());
			showSurfaceViewerFrame((SimulationContext)applicationComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner(),true);
			setDefaultTitle(applicationComponents.getSurfaceViewerFrame());
		}
	}	
}


private ApplicationComponents findAppComponentsForSimContextGeomViewer(ApplicationEditor applicationEditor){
		Enumeration<ApplicationComponents> appComponentsEnum = getApplicationsHash().elements();
		while (appComponentsEnum.hasMoreElements()) {
			ApplicationComponents appComponents = (ApplicationComponents)appComponentsEnum.nextElement();
			ApplicationEditor appEditor = appComponents.getAppEditor();
			if (appEditor == applicationEditor) {
				SimulationOwner simOwner  = (SimulationOwner)appComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner();
				if (simOwner instanceof SimulationContext) {
					return appComponents;
				} 
			}
		}
		Geometry geom = applicationEditor.getSimulationWorkspace().getSimulationOwner().getGeometry();
		DialogUtils.showErrorDialog(getComponent(), "Geometry "+(geom!= null?geom.getName():null)+" key="+(geom != null?geom.getVersion().getVersionKey():null)+" not found in application hash");

		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:32:07 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void addResultsFrame(SimulationWindow simWindow) {
	if (!getApplicationsHash().containsKey(simWindow.getSimOwner())) {
		// it shouldn't happen, but check anyway...
		try {
			throw new RuntimeException("we are asked to show results but we don't have the simOwner");
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
	}
	ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(simWindow.getSimOwner());
	appComponents.addDataViewer(simWindow);
	if (appComponents.getAppEditorFrame().isShowing()) {
		// should be showing, but you never know...
		int count = appComponents.getDataViewerFrames().length;
		simWindow.getFrame().setLocation(appComponents.getAppEditorFrame().getLocation().x + 100 + count * 20, appComponents.getAppEditorFrame().getLocation().y + 100 + count * 15);
		showFrame(simWindow.getFrame());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 2:33:41 AM)
 */
private void checkValidApplicationFrames(boolean reset) {
	SimulationContext[] scs = getBioModel().getSimulationContexts();
	
	Enumeration<SimulationContext> en = getApplicationsHash().keys();
	while (en.hasMoreElements()) {
		SimulationContext sc = (SimulationContext)en.nextElement();
		ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(sc);
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
					// reset the sc everywhere
					appComponents.resetSimulationContext(found);
					// check simulation data windows
					checkValidSimulationDataViewerFrames(appComponents, found);
					// rewire listener
					sc.removePropertyChangeListener(this);
					found.addPropertyChangeListener(this);
					// update hash
					getApplicationsHash().remove(sc);
					getApplicationsHash().put(found, appComponents);
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
private void checkValidSimulationDataViewerFrames(ApplicationComponents appComponents, SimulationContext found) {
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
	appComponents.getAppEditor().addActionListener(this);
//	appComponents.getGeometrySummaryViewer().addActionListener(this);
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void createBioModelFrame() {
	getBioModelEditor().setBioModelWindowManager(this);
	getBioModelEditor().setBioModel(getBioModel());
	getBioModelEditor().setDocumentManager(getRequestManager().getDocumentManager());
	JInternalFrameEnhanced editorFrame = new JInternalFrameEnhanced("Model", true, false, true, true);
	editorFrame.setContentPane(bioModelEditor);
	getJDesktopPane().add(editorFrame);
	editorFrame.show();
	editorFrame.setSize(900,600);
	editorFrame.setMinimumSize(new Dimension(400, 300));
	editorFrame.setLocation(10,10);
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
		ApplicationComponents appComponents = (ApplicationComponents)en.nextElement();
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

	if(evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("geometry")){
		ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get((SimulationContext)evt.getSource());

		appComponents.getSurfaceViewer().setGeometry(null);
		close(appComponents.getSurfaceViewerFrame(),getJDesktopPane());

//		appComponents.getGeometrySummaryViewer().setGeometry(((SimulationContext)evt.getSource()).getGeometry());
//		setDefaultTitle(appComponents.getGeometrySummaryViewerFrame());
	}
	
	if (evt.getSource() == getBioModel() && evt.getPropertyName().equals("simulationContexts")) {
		// close any window we should not have anymore
		checkValidApplicationFrames(false);
	}
	if (evt.getSource() == getBioModel() && evt.getPropertyName().equals("simulations")) {
		// close any window we should not have anymore
		Enumeration<SimulationContext> en = getApplicationsHash().keys();
		while (en.hasMoreElements()) {
			SimulationContext sc = (SimulationContext)en.nextElement();
			ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(sc);
			checkValidSimulationDataViewerFrames(appComponents, sc);
			appComponents.cleanSimWindowsHash();
		}
	}
	if (evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("name")) {
		SimulationContext simContext = (SimulationContext)evt.getSource();
		JInternalFrameEnhanced editorFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getAppEditorFrame();
//		JInternalFrameEnhanced geoFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getGeometrySummaryViewerFrame();
		JInternalFrameEnhanced surfaceFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getSurfaceViewerFrame();
		editorFrame.setTitle("APPLICATION: "+simContext.getName());
//		geoFrame.setTitle("GEOMETRY for: "+simContext.getName());
		surfaceFrame.setTitle("SURFACE for: "+simContext.getName()+"'s Geometry");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 1:20:40 PM)
 */
private void remove(ApplicationComponents appComponents, SimulationContext sc) {
	appComponents.getAppEditor().removeActionListener(this);
//	appComponents.getGeometrySummaryViewer().removeActionListener(this);
	sc.removePropertyChangeListener(this);
	getApplicationsHash().remove(sc);
	close(appComponents.getAppEditorFrame(), getJDesktopPane());
//	close(appComponents.getGeometrySummaryViewerFrame(), getJDesktopPane());
	close(appComponents.getSurfaceViewerFrame(), getJDesktopPane());
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
	getBioModelEditor().setBioModel(getBioModel());
	checkValidApplicationFrames(true);
	Enumeration<JInternalFrame> en = dataViewerPlotsFramesVector.elements();
	while (en.hasMoreElements()) {
		close((JInternalFrame)en.nextElement(), getJDesktopPane());
	}
	Enumeration<ApplicationComponents> enumAppComp = applicationsHash.elements();
	while (enumAppComp.hasMoreElements()){
		ApplicationComponents appcomp = (ApplicationComponents)enumAppComp.nextElement();
//		setDefaultTitle(appcomp.getGeometrySummaryViewerFrame());
		setDefaultTitle(appcomp.getSurfaceViewerFrame());
	}
	getRequestManager().updateStatusNow();
}

/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 8:37:05 PM)
 * @param newBioModel cbit.vcell.biomodel.BioModel
 */
private void setBioModel(BioModel newBioModel) {
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

public void showApplicationFrame(SimulationContext simContext) {
	showApplicationFrame(simContext, -1);
}

/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
public void showApplicationFrame(final SimulationContext simContext, final int tabIndex) {
	AsynchClientTask task1 = new AsynchClientTask("preload the application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (!getApplicationsHash().containsKey(simContext)) {
				simContext.getGeometry().precomputeAll();
				Simulation[] simulations = simContext.getSimulations();
				if (simulations != null) {
					// preload simulation status
					ArrayList<VCSimulationIdentifier> simIDs = new ArrayList<VCSimulationIdentifier>();
					for (int i = 0; i < simulations.length; i++){
						if (simulations[i].getSimulationInfo()!=null){
							simIDs.add(simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier());
						}
					}
					getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs.toArray(new VCSimulationIdentifier[simIDs.size()]));
				}
			}
		}
	};
		
	AsynchClientTask task2 = new AsynchClientTask("show application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (! getApplicationsHash().containsKey(simContext)) {
				// create components
				createAppComponents(simContext);
			}
			ApplicationComponents applicationComponents = (ApplicationComponents)getApplicationsHash().get(simContext);
			if (tabIndex >= 0) {
				applicationComponents.getAppEditor().setTabIndex(tabIndex);
			}
			JInternalFrameEnhanced editorFrame = applicationComponents.getAppEditorFrame();
			showFrame(editorFrame);
		}
	};
	ClientTaskDispatcher.dispatch(getJDesktopPane(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });		
	
}

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
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showSurfaceViewerFrame(SimulationContext simContext,boolean bOpen) {
	JInternalFrameEnhanced editorFrame = null;
	if (getApplicationsHash().containsKey(simContext)) {
		editorFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getSurfaceViewerFrame();
		if(!bOpen){
			close(editorFrame, getJDesktopPane());
			return;
		}
		showFrame(editorFrame);
	}


	//Generate surfaces if necessary
	if(editorFrame != null){
		editorFrame.requestFocus();
		try{
			SurfaceViewerPanel surfaceViewerPanel = ((ApplicationComponents)getApplicationsHash().get(simContext)).getSurfaceViewer();

			if(simContext.getGeometry().getGeometrySurfaceDescription() != null &&
				simContext.getGeometry().getGeometrySurfaceDescription().getSurfaceCollection() == null){

					surfaceViewerPanel.updateSurfaces();
			}
			
		}catch(Exception e){
			DialogUtils.showErrorDialog(getComponent(), "Error Generating Surfaces\n"+e.getMessage(), e);
		}
	}
}


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
	ClientSimManager simManager = appComponents.getAppEditor().getSimulationWorkspace().getClientSimManager();
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
						close(mIRIAMAnnotationEditorFrame, getJDesktopPane());
					}
					public void internalFrameClosing(InternalFrameEvent e) {
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

public void BioModelEditor_ApplicationMenu_ActionPerformed(ActionEvent e)
{
	if(getBioModelEditor() != null)
	{
		getBioModelEditor().bioModelTreePanel1_ActionPerformed(e);
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
}