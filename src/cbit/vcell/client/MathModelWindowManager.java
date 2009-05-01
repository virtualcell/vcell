package cbit.vcell.client;
import cbit.util.SwingDispatcherSync;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.solver.*;
import cbit.vcell.modeldb.VersionableTypeVersion;
import cbit.sql.*;
import cbit.vcell.client.desktop.simulation.*;
import java.awt.*;
import javax.swing.*;

import org.vcell.util.document.KeyValue;

import cbit.vcell.client.RequestManager;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.gui.JInternalFrameEnhanced;
import cbit.vcell.client.desktop.mathmodel.*;
import cbit.vcell.mathmodel.MathModel;
import java.util.Hashtable;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 10:52:17 AM)
 * @author: Anuradha Lakshminarayana
 */
public class MathModelWindowManager extends DocumentWindowManager implements java.beans.PropertyChangeListener, java.awt.event.ActionListener{
	private MathModel mathModel = null;
	private JDesktopPane jDesktopPane = null;
	private MathModelEditor mathModelEditor = null;

	private EquationViewerPanel equnsViewer;
	private GeometrySummaryViewer geoViewer;
	private SimulationListPanel simsPanel;
	private VCMLEditorPanel vcmlEditor;
	private SurfaceViewerPanel surfaceViewer;

	// Internal frames for the above panels ...
	private JInternalFrameEnhanced VCMLEditorFrame = null;		
	private JInternalFrameEnhanced equationsViewerEditorFrame = null;
	private JInternalFrameEnhanced geometryViewerEditorFrame = null;		
	private JInternalFrameEnhanced simsListEditorFrame = null;
	private JInternalFrameEnhanced surfaceViewerFrame = null;	
	
	// results windows and plots
	private Hashtable<VCSimulationIdentifier, SimulationWindow> simulationWindowsHash = new Hashtable<VCSimulationIdentifier, SimulationWindow>();
	private Vector<JInternalFrame> dataViewerPlotsFramesVector = new Vector<JInternalFrame>();
	
	//Field Data help.  Set if copied from a BioModel Application.
	//Used to substitute Field Data while saving a MathModel.
	private VersionableTypeVersion copyFromBioModelAppVersionableTypeVersion = null;

/**
 * MathModelManager constructor comment.
 * @param documentWindow cbit.vcell.client.desktop.DocumentWindow
 * @param vcellClient cbit.vcell.client.VCellClient
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public MathModelWindowManager(JPanel panel, RequestManager aRequestManager, final MathModel aMathModel, int newlyCreatedDesktops) {
	super(panel, aRequestManager, aMathModel, newlyCreatedDesktops);
	new SwingDispatcherSync() {
		public Object runSwing() throws Exception{
			equnsViewer = new EquationViewerPanel();
			geoViewer = new GeometrySummaryViewer();
			simsPanel = new SimulationListPanel();
			vcmlEditor = new VCMLEditorPanel();
			surfaceViewer = new SurfaceViewerPanel();
			setJDesktopPane(new JDesktopPane());
			getJPanel().setLayout(new BorderLayout());
			getJPanel().add(getJDesktopPane(), BorderLayout.CENTER);
			setMathModel(aMathModel);
			createMathModelEditor();
			return null;
		}
	}.dispatchWrapRuntime();

	initializeInternalFrames();
	
	new SwingDispatcherSync() {
		public Object runSwing() throws Exception{
			geoViewer.addActionListener(MathModelWindowManager.this);
			getJPanel().add(getMathModelEditor(), BorderLayout.NORTH);
			if (System.getProperty("java.version").compareTo("1.3") >= 0) {
				showVCMLEditor(true);
			}	
			return null;
		}
	}.dispatchWrapRuntime();

}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	
	if(e.getSource() instanceof cbit.vcell.client.desktop.geometry.GeometrySummaryViewer && e.getActionCommand().equals("Open Geometry")){
		//KeyValue geometryKey = ((cbit.vcell.client.desktop.geometry.GeometrySummaryViewer.GeometrySummaryViewerEvent)e).getGeometry().getVersion().getVersionKey();
		openGeometryDocumentWindow(((cbit.vcell.client.desktop.geometry.GeometrySummaryViewer.GeometrySummaryViewerEvent)e).getGeometry());
	}

	if (e.getSource() instanceof GeometrySummaryViewer && e.getActionCommand().equals("Change Geometry...")) {
		getRequestManager().changeGeometry(this, null);
	}
	if (e.getSource() instanceof GeometrySummaryViewer && e.getActionCommand().equals("View Surfaces")) {
		showSurfaceViewerFrame();
		if(getMathModel() != null && getMathModel().getMathDescription() != null &&
			getMathModel().getMathDescription().getGeometry() != null &&
			getMathModel().getMathDescription().getGeometry().getGeometrySurfaceDescription() != null &&
			(	surfaceViewer.getGeometry() == null || 
				getMathModel().getMathDescription().getGeometry().getGeometrySurfaceDescription().getSurfaceCollection() == null)){
			//Thread surfThread =
			//new Thread(
				//new Runnable (){
					//public void run(){
						try{
							final cbit.vcell.geometry.Geometry geom = getMathModel().getMathDescription().getGeometry();
							//SwingUtilities.invokeAndWait(
								//new Runnable() {
									//public void run(){
										//try{
											surfaceViewer.setGeometry(geom);
											setDefaultTitle(surfaceViewerFrame);
										//}catch(Throwable e){
											//cbit.gui.DialogUtils.showErrorDialog("Error Generating Surfaces"+"\n"+e.getClass().getName()+"\n"+e.getMessage());
										//}
									//}
								//}
							//);
							surfaceViewer.updateSurfaces();
						}catch(Exception e2){
							cbit.gui.DialogUtils.showErrorDialog("Error Generating Surfaces"+"\n"+e2.getClass().getName()+"\n"+e2.getMessage());
						}
					//}
				//}
			//);
			//surfThread.start();
		}
		//else{
			//surfaceViewer.setGeometry(null);
		//}
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:32:07 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void addResultsFrame(SimulationWindow simWindow) {
	if (simWindow.getSimOwner() != getMathModel()) {
		// it shouldn't happen, but check anyway...
		try {
			throw new RuntimeException("we are asked to show results but we don't have the simOwner");
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
		return;
	}
	simulationWindowsHash.put(simWindow.getVcSimulationIdentifier(), simWindow);
	simWindow.getFrame().setLocation(100 + 20 * simulationWindowsHash.size(), 100 + 15 * simulationWindowsHash.size());
	showFrame(simWindow.getFrame());
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 1:13:06 PM)
 */
private void checkValidSimulationDataViewerFrames() {
	SimulationWindow[] simWindows = (SimulationWindow[])org.vcell.util.BeanUtils.getArray(simulationWindowsHash.elements(), SimulationWindow.class);
	Simulation[] sims = getMathModel().getSimulations();
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
 * Insert the method's description here.
 * Creation date: (6/21/2005 12:36:41 PM)
 */
private void cleanSimWindowsHash() {

	Enumeration<VCSimulationIdentifier> enum1 = simulationWindowsHash.keys();
	Vector<VCSimulationIdentifier> toRemove = new Vector<VCSimulationIdentifier>();
	while(enum1.hasMoreElements()){
		VCSimulationIdentifier vcsid = enum1.nextElement();
		Simulation[] sims = getMathModel().getSimulations();
		boolean bFound = false;
		for(int i=0;i<sims.length;i+= 1){
			if(sims[i].getSimulationInfo() != null && sims[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier().equals(vcsid)){
				bFound = true;
				break;
			}
		}
		if(!bFound){
			toRemove.add(vcsid);
		}
	}
	if(toRemove.size() > 0){
		for(int i=0;i<toRemove.size();i+= 1){
			simulationWindowsHash.remove(toRemove.elementAt(i));
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void createMathModelEditor() {
	mathModelEditor = new MathModelEditor();
	mathModelEditor.setMathDescription(getMathModel().getMathDescription());
	mathModelEditor.setMathModelWindowManager(this);
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
public void equationsViewerButtonPressed(boolean bEqunButtonSelected) {
	showEquationsViewer(bEqunButtonSelected);
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
public void geometryViewerButtonPressed(boolean bGeoButtonSelected) {
	showGeometryViewer(bGeoButtonSelected);
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:58:14 PM)
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
 * Creation date: (5/14/2004 11:08:35 AM)
 * @return cbit.vcell.mathmodel.MathModel
 */
public cbit.vcell.mathmodel.MathModel getMathModel() {
	return mathModel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 5:19:11 PM)
 * @return cbit.vcell.desktop.controls.MathWorkspace
 */
private MathModelEditor getMathModelEditor() {
	return mathModelEditor;
}

public VersionableTypeVersion getCopyFromBioModelAppVersionableTypeVersion(){
	return copyFromBioModelAppVersionableTypeVersion;
}
public void setCopyFromBioModelAppVersionableTypeVersion(VersionableTypeVersion bioModelAppVTV){
	copyFromBioModelAppVersionableTypeVersion = bioModelAppVTV;
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 4:56:56 PM)
 * @return cbit.vcell.document.VCDocument
 */
public cbit.vcell.document.VCDocument getVCDocument() {
	return getMathModel();
}

public boolean hasUnappliedChanges() {
	if (vcmlEditor.hasUnappliedChanges()) {
		return true;
	}
	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:55:48 AM)
 * @return boolean
 * @param vcSimulationIdentifier cbit.vcell.server.VCSimulationIdentifier
 */
cbit.vcell.client.desktop.simulation.SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
	if (simulationWindowsHash.containsKey(vcSimulationIdentifier)) {
		return (SimulationWindow)simulationWindowsHash.get(vcSimulationIdentifier);
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void initializeInternalFrames() {

	new SwingDispatcherSync() {
		public Object runSwing() throws Exception{
			// Initialize VCML Editor internal frame
			String vcmlEditorTitle = "VCML Editor";
			vcmlEditor.setMathModel(getMathModel());
			VCMLEditorFrame = new JInternalFrameEnhanced(vcmlEditorTitle, true, true, true, true);
			JMenuBar mb = new JMenuBar();
			JMenu menu = vcmlEditor.getEditMenu();
			mb.add(menu);
			VCMLEditorFrame.setJMenuBar(mb);
			
			VCMLEditorFrame.setContentPane(vcmlEditor);
			VCMLEditorFrame.setSize(550, 550);
			VCMLEditorFrame.setLocation(10, 10);
			VCMLEditorFrame.setMinimumSize(new Dimension(250, 250));
			VCMLEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
				public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
					getMathModelEditor().setToggleButtonSelected("VCML Editor", false);
				};
			});		
			
			
			// Initialize Equations Viewer internal frame
			String equnsViewerTitle = "Equations Viewer";
			equnsViewer.setMathModel(getMathModel());
			equationsViewerEditorFrame = new JInternalFrameEnhanced(equnsViewerTitle, true, true, true, true);
			equationsViewerEditorFrame.setContentPane(equnsViewer);
			equationsViewerEditorFrame.setSize(400, 400);
			equationsViewerEditorFrame.setLocation(300, 100);
			equationsViewerEditorFrame.setMinimumSize(new Dimension(250, 250));
			equationsViewerEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
				public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
					getMathModelEditor().setToggleButtonSelected("Equations Viewer", false);
				};
			});
			return null;
		}
	}.dispatchWrapRuntime();

	
	
	// Initialize Geometry Viewer internal frame
	//String geoViewerTitle = "Geometry Viewer";
	geoViewer.setGeometry(getMathModel().getMathDescription().getGeometry());
	
	new SwingDispatcherSync() {
		public Object runSwing() throws Exception{
			//disable changeGeometry and openGeometry button in geometry summary viewer if it is a stochastic app.
			if(getMathModel().getMathDescription().isStoch())
			{
				geoViewer.setChangeGeometryEnabled(false);
				geoViewer.setOpenGeometryEnabled(false);
			}
			else
			{
				geoViewer.setChangeGeometryEnabled(true);
				geoViewer.setOpenGeometryEnabled(true);
			}
			geometryViewerEditorFrame = createDefaultFrame(geoViewer);
			//geometryViewerEditorFrame = new JInternalFrameEnhanced(geoViewerTitle, true, true, true, true);
			//geometryViewerEditorFrame.setContentPane(geoViewer);
			//geometryViewerEditorFrame.setSize(700, 400);
			//geometryViewerEditorFrame.setLocation(200, 200);
			//geometryViewerEditorFrame.setMinimumSize(new Dimension(600, 400));
			geometryViewerEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
				public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
					getMathModelEditor().setToggleButtonSelected("Geometry Viewer", false);
				};
			});

			// Initialize Surface Viewer internal frame
			//String surfaceViewerTitle = "Surface Viewer";
			surfaceViewer.setGeometry(getMathModel().getMathDescription().getGeometry());
			surfaceViewerFrame = createDefaultFrame(surfaceViewer);
			//surfaceViewerFrame = new JInternalFrameEnhanced(surfaceViewerTitle, true, true, true, true);
			//surfaceViewerFrame.setContentPane(surfaceViewer);
			//surfaceViewerFrame.setSize(500, 500);
			//surfaceViewerFrame.setLocation(400, 200);
			//surfaceViewerFrame.setMinimumSize(new Dimension(400, 400));

				
			// Initialize SimulationsList Viewer internal frame
			String simsListTitle = "Simulations";
			simsPanel.setSimulationWorkspace(new SimulationWorkspace(MathModelWindowManager.this, getMathModel()));
			simsListEditorFrame = new JInternalFrameEnhanced(simsListTitle, true, true, true, true);
			simsListEditorFrame.setContentPane(simsPanel);
			simsListEditorFrame.setSize(750, 600);
			simsListEditorFrame.setLocation(500, 300);
			simsListEditorFrame.setMinimumSize(new Dimension(500, 500));
			simsListEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
				public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
					getMathModelEditor().setToggleButtonSelected("Simulations", false);
				};
			});
			return null;
		}
	}.dispatchWrapRuntime();

}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:32:02 PM)
 * @return boolean
 */
public boolean isRecyclable() {
	return false;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {

	if(evt.getSource() instanceof cbit.vcell.math.MathDescription && evt.getPropertyName().equals("geometry")){
		surfaceViewer.setGeometry(null);
		if(surfaceViewerFrame != null){
			close(surfaceViewerFrame,getJDesktopPane());
		}
		cbit.vcell.geometry.Geometry geom = ((cbit.vcell.math.MathDescription)evt.getSource()).getGeometry();
		geoViewer.setGeometry(geom);
		setDefaultTitle(geometryViewerEditorFrame);
	}
	
	if (evt.getSource() == getMathModel() && evt.getPropertyName().equals("simulations")) {
		checkValidSimulationDataViewerFrames();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:40:45 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void resetDocument(cbit.vcell.document.VCDocument newDocument) {
	setMathModel((MathModel)newDocument);
	setDocumentID(getMathModel());
	setMathModel((MathModel)newDocument);
	vcmlEditor.setMathModel(getMathModel());
	equnsViewer.setMathModel(getMathModel());
	geoViewer.setGeometry(getMathModel().getMathDescription().getGeometry());
	SimulationWorkspace simWorkspace = simsPanel.getSimulationWorkspace();
	if (simWorkspace==null){
		simsPanel.setSimulationWorkspace(new SimulationWorkspace(this, getMathModel()));
	}else{
		simWorkspace.setSimulationOwner((MathModel)newDocument);
	}
	checkValidSimulationDataViewerFrames();
	Enumeration<JInternalFrame> en = dataViewerPlotsFramesVector.elements();
	while (en.hasMoreElements()) {
		close(en.nextElement(), getJDesktopPane());
	}
	getRequestManager().updateStatusNow();
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:58:14 PM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void setJDesktopPane(javax.swing.JDesktopPane newJDesktopPane) {
	jDesktopPane = newJDesktopPane;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 11:08:35 AM)
 * @param newMathModel cbit.vcell.mathmodel.MathModel
 */
private void setMathModel(cbit.vcell.mathmodel.MathModel newMathModel) {
	if (getMathModel() != null) {
		getMathModel().removePropertyChangeListener(this);
		if(getMathModel().getMathDescription() != null){
			getMathModel().getMathDescription().removePropertyChangeListener(this);
		}
	}
	mathModel = newMathModel;
	if (getMathModel() != null) {
		getMathModel().addPropertyChangeListener(this);
		if(getMathModel().getMathDescription() != null){
			getMathModel().getMathDescription().addPropertyChangeListener(this);
		}
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
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showEquationsViewer(boolean bEqunButtonSelected) {
	if (bEqunButtonSelected) {
		// If toggleButton is selected, check if it is open. If not, open it, add it to desktopPane
		// If it is iconized, 'de-iconify' it.
		showFrame(equationsViewerEditorFrame);
	} else {
		// If toggleButton is unselected, check if vcmlEditor is iconized. If not closed, dispose it.
		// If it is iconized on the desktop, de-iconify vcmlEditor & dispose it.
		close(equationsViewerEditorFrame, getJDesktopPane());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:55:40 PM)
 */
public void showFrame(javax.swing.JInternalFrame frame) {
	showFrame(frame, getJDesktopPane());
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showGeometryViewer(boolean bGeoButtonSelected) {
	if (bGeoButtonSelected) {
		// If toggleButton is selected, check if it is open. If not, open it, add it to desktopPane
		// If it is iconized, 'de-iconify' it.
		setDefaultTitle(geometryViewerEditorFrame);
		if(geoViewer != null)
		{
			if(getMathModel().getMathDescription().isStoch())
			{
				geoViewer.setChangeGeometryEnabled(false);
				geoViewer.setOpenGeometryEnabled(false);
			}
			else
			{
				geoViewer.setChangeGeometryEnabled(true);
				geoViewer.setOpenGeometryEnabled(true);
			}
		}
		showFrame(geometryViewerEditorFrame);
	} else {
		// If toggleButton is unselected, check if vcmlEditor is iconized. If not closed, dispose it.
		// If it is iconized on the desktop, de-iconify vcmlEditor & dispose it.
		close(geometryViewerEditorFrame, getJDesktopPane());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showSimulationsList(boolean bSimsButtonSelected) {
	if (bSimsButtonSelected) {
		// If toggleButton is selected, check if it is open. If not, open it, add it to desktopPane
		// If it is iconized, 'de-iconify' it.
		showFrame(simsListEditorFrame);
	} else {
		// If toggleButton is unselected, check if vcmlEditor is iconized. If not closed, dispose it.
		// If it is iconized on the desktop, de-iconify vcmlEditor & dispose it.
		close(simsListEditorFrame, getJDesktopPane());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showSurfaceViewerFrame() {

	showFrame(surfaceViewerFrame);
}

/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showVCMLEditor(boolean bVCMLButtonSelected) {
	if (bVCMLButtonSelected) {
		// If toggleButton is selected, check if it is open. If not, open it, add it to desktopPane
		// If it is iconized, 'de-iconify' it.
		showFrame(VCMLEditorFrame);
	} else {
		// If toggleButton is unselected, check if vcmlEditor is iconized. If not closed, dispose it.
		// If it is iconized on the desktop, de-iconify vcmlEditor & dispose it.
		close(VCMLEditorFrame, getJDesktopPane());
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
	Simulation[] sims = getMathModel().getSimulations();
	if (sims == null) {
		// we don't have it
		return;
	}
	Simulation simulation = null;
	for (int i = 0; i < sims.length; i++){
		if (simKey.equals(sims[i].getKey()) || ((sims[i].getSimulationVersion() != null) && simKey.equals(sims[i].getSimulationVersion().getParentSimulationReference()))) {
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
	// update status display
	ClientSimManager simManager = simsPanel.getSimulationWorkspace().getClientSimManager();
	simManager.updateStatusFromServer(simulation);
	// is there new data?
	if (simStatusEvent.isNewDataEvent()) {
		fireNewData(new DataEvent(this, new cbit.vcell.solver.VCSimulationDataIdentifier(simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simStatusEvent.getJobIndex())));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
public void simulationsButtonPressed(boolean bSimsButtonSelected) {
	showSimulationsList(bSimsButtonSelected);
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
public void vcmlEditorButtonPressed(boolean bVCMLButtonSelected) {
	showVCMLEditor(bVCMLButtonSelected);
}

}