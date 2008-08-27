package cbit.vcell.client;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import cbit.gui.DialogUtils;
import cbit.gui.JInternalFrameEnhanced;
import cbit.sql.KeyValue;
import cbit.util.EventDispatchRunWithException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.ApplicationComponents;
import cbit.vcell.client.desktop.biomodel.ApplicationEditor;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.xml.MIRIAMAnnotatable;
import cbit.vcell.xml.MIRIAMAnnotationEditor;
import cbit.vcell.xml.MIRIAMHelper;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:17:07 PM)
 * @author: Ion Moraru
 */
public class BioModelWindowManager extends DocumentWindowManager implements java.beans.PropertyChangeListener, java.awt.event.ActionListener {
	private BioModel bioModel = null;
	private Hashtable<SimulationContext, ApplicationComponents> applicationsHash = new Hashtable<SimulationContext, ApplicationComponents>();
	private JDesktopPane jDesktopPane = null;
	private BioModelEditor bioModelEditor = null;
	private Vector<JInternalFrame> dataViewerPlotsFramesVector = new Vector<JInternalFrame>();

	private cbit.vcell.opt.solvers.LocalOptimizationService localOptService = null;
	
	private JInternalFrame mIRIAMAnnotationEditorFrame = null;
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
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			setJDesktopPane(new JDesktopPane());
			getJPanel().setLayout(new BorderLayout());
			getJPanel().add(getJDesktopPane(), BorderLayout.CENTER);
			setBioModel(bioModel);
			setBioModelEditor(new BioModelEditor());
			createBioModelFrame();
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	
	if(e.getSource() instanceof cbit.vcell.client.desktop.geometry.GeometrySummaryViewer && e.getActionCommand().equals("Open Geometry")){
		//KeyValue geometryKey = ((cbit.vcell.client.desktop.geometry.GeometrySummaryViewer.GeometrySummaryViewerEvent)e).getGeometry().getVersion().getVersionKey();
		openGeometryDocumentWindow(((cbit.vcell.client.desktop.geometry.GeometrySummaryViewer.GeometrySummaryViewerEvent)e).getGeometry());
	}
	
	if (e.getSource() instanceof ApplicationEditor && e.getActionCommand().equals("Create Math Model")) {
		SimulationContext sc = (SimulationContext)((ApplicationEditor)e.getSource()).getSimulationWorkspace().getSimulationOwner();
		getRequestManager().createMathModelFromApplication("Untitled", sc);
	}
	if (e.getSource() instanceof ApplicationEditor && e.getActionCommand().equals("View / Change Geometry")) {
		SimulationContext sc = (SimulationContext)((ApplicationEditor)e.getSource()).getSimulationWorkspace().getSimulationOwner();
		showGeometryViewerFrame(sc);
	}
	if (e.getSource() instanceof cbit.vcell.client.desktop.geometry.GeometrySummaryViewer && e.getActionCommand().equals("Change Geometry...")) {
		Geometry geom = ((cbit.vcell.client.desktop.geometry.GeometrySummaryViewer.GeometrySummaryViewerEvent)e).getGeometry();
		// Lookup application components based on instance of GeometrySummaryViewer
 		Enumeration<ApplicationComponents> appComponentsEnum = getApplicationsHash().elements();
		while (appComponentsEnum.hasMoreElements()) {
			ApplicationComponents appComponents = (ApplicationComponents)appComponentsEnum.nextElement();
			GeometrySummaryViewer geomViewer = appComponents.getGeometrySummaryViewer();
			if (geomViewer == (GeometrySummaryViewer)e.getSource()) {
				SimulationOwner simOwner  = (SimulationOwner)appComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner();
				if (simOwner instanceof SimulationContext) {
					getRequestManager().changeGeometry(this, (SimulationContext)simOwner);
					return;
				} 
			}
		}
		DialogUtils.showErrorDialog("Geometry "+geom.getName()+" key="+geom.getVersion().getVersionKey()+" not found in application hash");
	}
	if (e.getSource() instanceof cbit.vcell.client.desktop.geometry.GeometrySummaryViewer && e.getActionCommand().equals("View Surfaces")) {
		Geometry geom = ((cbit.vcell.client.desktop.geometry.GeometrySummaryViewer.GeometrySummaryViewerEvent)e).getGeometry();
		// Lookup application components based on instance of GeometrySummaryViewer
 		Enumeration<ApplicationComponents> appComponentsEnum = getApplicationsHash().elements();
		while (appComponentsEnum.hasMoreElements()) {
			ApplicationComponents appComponents = (ApplicationComponents)appComponentsEnum.nextElement();
			GeometrySummaryViewer geomViewer = appComponents.getGeometrySummaryViewer();
			if (geomViewer == (GeometrySummaryViewer)e.getSource()) {
				SimulationOwner simOwner  = (SimulationOwner)appComponents.getAppEditor().getSimulationWorkspace().getSimulationOwner();
				if (simOwner instanceof SimulationContext) {
					appComponents.getSurfaceViewer().setGeometry(geom);
					showSurfaceViewerFrame((SimulationContext)simOwner);
					setDefaultTitle(appComponents.getSurfaceViewerFrame());
					return;
				} 
			}
		}
		DialogUtils.showErrorDialog("Geometry "+geom.getName()+" ley="+geom.getVersion().getVersionKey()+" not found in application hash");
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:32:07 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void addResultsFrame(final SimulationWindow simWindow) {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			if (!getApplicationsHash().containsKey(simWindow.getSimOwner())) {
				// it shouldn't happen, but check anyway...
				try {
					throw new RuntimeException("we are asked to show results but we don't have the simOwner");
				} catch (Exception exc) {
					exc.printStackTrace(System.out);
				}
				return null;
			}
			ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(simWindow.getSimOwner());
			appComponents.addDataViewer(simWindow);
			if (appComponents.getAppEditorFrame().isShowing()) {
				// should be showing, but you never know...
				int count = appComponents.getDataViewerFrames().length;
				simWindow.getFrame().setLocation(appComponents.getAppEditorFrame().getLocation().x + 100 + count * 20, appComponents.getAppEditorFrame().getLocation().y + 100 + count * 15);
				showFrame(simWindow.getFrame());
			}
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();

}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 2:33:41 AM)
 */
private void checkValidApplicationFrames(boolean reset) {
	Enumeration<SimulationContext> en = getApplicationsHash().keys();
	while (en.hasMoreElements()) {
		SimulationContext sc = (SimulationContext)en.nextElement();
		ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(sc);
		if (!getBioModel().contains(sc)) {
			if (reset) {
				// find one with the same name, if available
				SimulationContext[] scs = getBioModel().getSimulationContexts();
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
		cbit.vcell.solver.SimulationInfo simInfo = sims[i].getSimulationInfo();
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
	appComponents.getGeometrySummaryViewer().addActionListener(this);
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void createBioModelFrame() {
	getBioModelEditor().setBioModelWindowManager(this);
	getBioModelEditor().setBioModel(getBioModel());
	getBioModelEditor().setDocumentManager(getRequestManager().getDocumentManager());
	cbit.gui.JInternalFrameEnhanced editorFrame = new cbit.gui.JInternalFrameEnhanced("Model", true, false, true, true);
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
private cbit.vcell.biomodel.BioModel getBioModel() {
	return bioModel;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 1:02:55 AM)
 * @return cbit.vcell.client.desktop.biomodel.BioModelEditor
 */
private cbit.vcell.client.desktop.biomodel.BioModelEditor getBioModelEditor() {
	return bioModelEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:49:01 PM)
 * @return javax.swing.JDesktopPane
 */
protected javax.swing.JDesktopPane getJDesktopPane() {
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
public cbit.vcell.opt.solvers.OptimizationService getOptimizationService() {
	if (localOptService==null){
		localOptService = new cbit.vcell.opt.solvers.LocalOptimizationService();
	}

	return localOptService;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 3:41:06 PM)
 * @return cbit.vcell.document.VCDocument
 */
public cbit.vcell.document.VCDocument getVCDocument() {
	return getBioModel();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:57:44 AM)
 * @return cbit.vcell.document.VCDocument
 */
cbit.vcell.client.desktop.simulation.SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
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
public void preloadApps() {
//System.out.println("+++++++++++"+(new Date(System.currentTimeMillis())));
	SimulationContext[] scs = getBioModel().getSimulationContexts();
	for (int i = 0; i < scs.length; i++){
		createAppComponents(scs[i]);
	}
//System.out.println("+++++++++++"+(new Date(System.currentTimeMillis())));
}


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

		appComponents.getGeometrySummaryViewer().setGeometry(((SimulationContext)evt.getSource()).getGeometry());
		setDefaultTitle(appComponents.getGeometrySummaryViewerFrame());
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
		JInternalFrameEnhanced mathFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getMathViewerFrame();
		JInternalFrameEnhanced geoFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getGeometrySummaryViewerFrame();
		JInternalFrameEnhanced surfaceFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getSurfaceViewerFrame();
		editorFrame.setTitle("APPLICATION: "+simContext.getName());
		mathFrame.setTitle("MATH for: "+simContext.getName());
		geoFrame.setTitle("GEOMETRY for: "+simContext.getName());
		surfaceFrame.setTitle("SURFACE for: "+simContext.getName()+"'s Geometry");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 1:20:40 PM)
 */
private void remove(ApplicationComponents appComponents, SimulationContext sc) {
	appComponents.getAppEditor().removeActionListener(this);
	appComponents.getGeometrySummaryViewer().removeActionListener(this);
	sc.removePropertyChangeListener(this);
	getApplicationsHash().remove(sc);
	close(appComponents.getAppEditorFrame(), getJDesktopPane());
	close(appComponents.getMathViewerFrame(), getJDesktopPane());
	close(appComponents.getGeometrySummaryViewerFrame(), getJDesktopPane());
	close(appComponents.getSurfaceViewerFrame(), getJDesktopPane());
	close(appComponents.getDataViewerFrames(), getJDesktopPane());
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:40:45 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void resetDocument(final cbit.vcell.document.VCDocument newDocument) {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			setBioModel((BioModel)newDocument);
			setDocumentID(getBioModel());
			getBioModelEditor().setBioModel(getBioModel());
			checkValidApplicationFrames(true);
			Enumeration<JInternalFrame> en = dataViewerPlotsFramesVector.elements();
			while (en.hasMoreElements()) {
				close((JInternalFrame)en.nextElement(), getJDesktopPane());
			}
			getRequestManager().updateStatusNow();
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 8:37:05 PM)
 * @param newBioModel cbit.vcell.biomodel.BioModel
 */
private void setBioModel(cbit.vcell.biomodel.BioModel newBioModel) {
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
private void setBioModelEditor(cbit.vcell.client.desktop.biomodel.BioModelEditor newBioModelEditor) {
	bioModelEditor = newBioModelEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:49:01 PM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void setJDesktopPane(javax.swing.JDesktopPane newJDesktopPane) {
	jDesktopPane = newJDesktopPane;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
public void showApplicationFrame(SimulationContext simContext) {
	try {
		cbit.util.BeanUtils.setCursorThroughout(getJDesktopPane(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (! getApplicationsHash().containsKey(simContext)) {
			// create components
			createAppComponents(simContext);
		}
		JInternalFrameEnhanced editorFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getAppEditorFrame();
		showFrame(editorFrame);
	} finally {	
		cbit.util.BeanUtils.setCursorThroughout(getJDesktopPane(), Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
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


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showGeometryViewerFrame(SimulationContext simContext) {
	JInternalFrameEnhanced editorFrame = null;
	if (getApplicationsHash().containsKey(simContext)) {
		editorFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getGeometrySummaryViewerFrame();
		setDefaultTitle(editorFrame);
		showFrame(editorFrame);
	}
	editorFrame.requestFocus();
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showSurfaceViewerFrame(SimulationContext simContext) {
	JInternalFrameEnhanced editorFrame = null;
	if (getApplicationsHash().containsKey(simContext)) {
		editorFrame = ((ApplicationComponents)getApplicationsHash().get(simContext)).getSurfaceViewerFrame();
		showFrame(editorFrame);
	}
	editorFrame.requestFocus();


	//Generate surfaces if necessary
	if(editorFrame != null){
		try{
			cbit.vcell.client.desktop.geometry.SurfaceViewerPanel surfaceViewerPanel = ((ApplicationComponents)getApplicationsHash().get(simContext)).getSurfaceViewer();

			if(simContext.getGeometry().getGeometrySurfaceDescription() != null &&
				simContext.getGeometry().getGeometrySurfaceDescription().getSurfaceCollection() == null){

					surfaceViewerPanel.updateSurfaces();
			}
			
		}catch(Exception e){
			DialogUtils.showErrorDialog("Error Generating Surfaces\n"+e.getMessage());
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
		fireNewData(new DataEvent(this, new cbit.vcell.solver.VCSimulationDataIdentifier(simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simStatusEvent.getJobIndex())));
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
		final MIRIAMAnnotationEditor miriamAnnotationEditor =
			new MIRIAMAnnotationEditor();
		miriamAnnotationEditor.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						try{
							if(e.getActionCommand().equals(MIRIAMAnnotationEditor.ACTION_OK)){
								close(mIRIAMAnnotationEditorFrame,getJDesktopPane());
							}else if(e.getActionCommand().equals(MIRIAMAnnotationEditor.ACTION_DELETE)){
								MIRIAMAnnotatable miriamAnnotatable = miriamAnnotationEditor.getSelectedMIRIAMAnnotatable();
								if(miriamAnnotatable == null || miriamAnnotatable.getMIRIAMAnnotation() == null){
									return;
								}
								MIRIAMHelper.deleteDescriptiveHeirarchy(miriamAnnotationEditor.getSelectedDescriptionHeirarchy());
								if(miriamAnnotatable.getMIRIAMAnnotation().getAnnotation().getChildren().size() == 0){
									miriamAnnotatable.setMIRIAMAnnotation(null);
								}
								TreeMap<MIRIAMAnnotatable, Vector<MIRIAMHelper.DescriptiveHeirarchy>> mirimaDescrHeir =
									MIRIAMHelper.showList(getBioModel());
								miriamAnnotationEditor.setMIRIAMAnnotation(mirimaDescrHeir);
							}else if(e.getActionCommand().equals(MIRIAMAnnotationEditor.ACTION_ADD)){
								final String ID_CHOICE = "Identifier";
								final String DATE_CHOICE = "Date";
								final String CREATOR_CHOICE = "Creator";
								String choice =
									(String)DialogUtils.showListDialog(getJDesktopPane(), new String[] {CREATOR_CHOICE,ID_CHOICE,DATE_CHOICE/*,"Creator","Date"*/}, "Choose Annotation Type");
								if(choice != null){
									if(choice.equals(ID_CHOICE)){
										miriamAnnotationEditor.addIdentifierDialog();
									}else if(choice.equals(DATE_CHOICE)){
										miriamAnnotationEditor.addTimeUTCDialog();
									}else if(choice.equals(CREATOR_CHOICE)){
										miriamAnnotationEditor.addCreatorDialog();
									}
									TreeMap<MIRIAMAnnotatable, Vector<MIRIAMHelper.DescriptiveHeirarchy>> mirimaDescrHeir =
										MIRIAMHelper.showList(getBioModel());
									miriamAnnotationEditor.setMIRIAMAnnotation(mirimaDescrHeir);
								}
							}
						}catch(Exception e2){
							DialogUtils.showErrorDialog("Error during Edit action\n"+e2.getMessage());
						}
					}
				}
			);

		mIRIAMAnnotationEditorFrame = new JInternalFrame();
		mIRIAMAnnotationEditorFrame.setTitle("View/Add/Delete/Edit MIRIAM Annotation");
		mIRIAMAnnotationEditorFrame.getContentPane().add(miriamAnnotationEditor);
		mIRIAMAnnotationEditorFrame.setSize(600,400);
		mIRIAMAnnotationEditorFrame.setClosable(true);
		mIRIAMAnnotationEditorFrame.setResizable(true);

	}

	if(!mIRIAMAnnotationEditorFrame.isShowing()){
		TreeMap<MIRIAMAnnotatable, Vector<MIRIAMHelper.DescriptiveHeirarchy>> mirimaDescrHeir =
			MIRIAMHelper.showList(getBioModel());
		((MIRIAMAnnotationEditor)mIRIAMAnnotationEditorFrame.getContentPane().getComponent(0)).setMIRIAMAnnotation(mirimaDescrHeir);
	}

//	showDataViewerPlotsFrame(mIRIAMAnnotationEditorFrame);
	showFrame(mIRIAMAnnotationEditorFrame);
}

}