package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VersionableTypeVersion;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.JTaskBar;

import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.desktop.mathmodel.EquationViewerPanel;
import cbit.vcell.client.desktop.mathmodel.MathModelEditor;
import cbit.vcell.client.desktop.mathmodel.VCMLEditorPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.gui.GeometryViewer;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatus;
/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 10:52:17 AM)
 * @author: Anuradha Lakshminarayana
 */
public class MathModelWindowManager extends DocumentWindowManager implements java.beans.PropertyChangeListener, java.awt.event.ActionListener{
	private MathModel mathModel = null;
	private JDesktopPaneEnhanced jDesktopPane = null;
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
	equnsViewer = new EquationViewerPanel();
	geoViewer = new GeometrySummaryViewer();
	simsPanel = new SimulationListPanel();
	vcmlEditor = new VCMLEditorPanel();
	surfaceViewer = new SurfaceViewerPanel();
	setJDesktopPane(new JDesktopPaneEnhanced());
	getJPanel().setLayout(new BorderLayout());
	getJPanel().add(getJDesktopPane(), BorderLayout.CENTER);
	if (!UIManager.getBoolean("InternalFrame.useTaskBar")) {
		JTaskBar taskBar = new JTaskBar(getJDesktopPane());
		getJPanel().add(taskBar, BorderLayout.SOUTH);
	}
	setMathModel(aMathModel);
	createMathModelEditor();

	initializeInternalFrames();

	geoViewer.addActionListener(MathModelWindowManager.this);
	getJPanel().add(getMathModelEditor(), BorderLayout.NORTH);
	showVCMLEditor(true);
}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	
	String actionCommand = e.getActionCommand();
	final Object source = e.getSource();

	if(source instanceof GeometrySummaryViewer && actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_GEOMETRY)){
			AsynchClientTask editSelectTask = new AsynchClientTask("Edit/Apply Geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					Geometry newGeom = (Geometry)hashTable.get("doc");
					if(newGeom != null){
//						DocumentCreationInfo documentCreationInfo = ((DocumentWindowManager.GeometrySelectionInfo)
//								hashTable.get(DocumentWindowManager.GEOMETRY_SELECTIONINFO_KEY)).getDocumentCreationInfo();
						if((Boolean)hashTable.get(B_SHOW_OLD_GEOM_EDITOR)
								/*documentCreationInfo == null || !ClientRequestManager.isImportGeometryType(documentCreationInfo)*/){
							GeometryViewer localGeometryViewer = new GeometryViewer();
							localGeometryViewer.setGeometry(newGeom);
							localGeometryViewer.setSize(800,600);
							int result = DialogUtils.showComponentOKCancelDialog(
									getComponent(), localGeometryViewer, "Edit Geometry: '"+newGeom.getName()+"'");
							if(result != JOptionPane.OK_OPTION){
								throw UserCancelException.CANCEL_GENERIC;
							}
						}
						showSurfaceViewerFrame(false);
					}else{
						throw new Exception("No Geometry found in edit task");
					}
				}
			};
			AsynchClientTask geomRegionsTask = new AsynchClientTask("Update Geometric regions", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Geometry newGeom = (Geometry)hashTable.get("doc");
						ClientRequestManager.continueAfterMathModelGeomChangeWarning(MathModelWindowManager.this, newGeom);
						newGeom.precomputeAll();					}
			};
			AsynchClientTask applyGeomTask = new AsynchClientTask("Apply Geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
				@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Geometry newGeom = (Geometry)hashTable.get("doc");
						if(newGeom.getName() == null){
							newGeom.setName(
								getMathModel().getName()+"_"+
								ClientRequestManager.generateDateTimeString());
						}
						((MathModel)getVCDocument()).getMathDescription().setGeometry(newGeom);
						vcmlEditor.updateWarningText(((MathModel)getVCDocument()).getMathDescription());
					}
			};

		createGeometry(getMathModel().getMathDescription().getGeometry(),
				new AsynchClientTask[] {editSelectTask,geomRegionsTask,applyGeomTask}
		,TopLevelWindowManager.DEFAULT_CREATEGEOM_SELECT_DIALOG_TITLE,"Apply Geometry");
	}

	if (source instanceof GeometrySummaryViewer && actionCommand.equals(GuiConstants.ACTIONCMD_CHANGE_GEOMETRY)) {
		showSurfaceViewerFrame(false);
		getRequestManager().changeGeometry(this,vcmlEditor);
	}
	if (source instanceof GeometrySummaryViewer && actionCommand.equals(GuiConstants.ACTIONCMD_VIEW_SURFACES)) {
		if(getMathModel() != null && getMathModel().getMathDescription() != null &&
			getMathModel().getMathDescription().getGeometry() != null &&
			getMathModel().getMathDescription().getGeometry().getGeometrySurfaceDescription() != null &&
			(surfaceViewer.getGeometry() == null || 
				getMathModel().getMathDescription().getGeometry().getGeometrySurfaceDescription().getSurfaceCollection() == null)){
				try{
					Geometry geom = getMathModel().getMathDescription().getGeometry();
					surfaceViewer.setGeometry(geom);
					setDefaultTitle(surfaceViewerFrame);
					surfaceViewer.updateSurfaces();					
				} catch(Exception e2){
					PopupGenerator.showErrorDialog(this, "Error Generating Surfaces"+"\n"+e2.getClass().getName()+"\n"+e2.getMessage(), e2);
				}
		}
		showSurfaceViewerFrame(true);
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
	SimulationWindow[] simWindows = (SimulationWindow[])BeanUtils.getArray(simulationWindowsHash.elements(), SimulationWindow.class);
	Simulation[] sims = getMathModel().getSimulations();
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
 * Creation date: (5/14/2004 11:08:35 AM)
 * @return cbit.vcell.mathmodel.MathModel
 */
public MathModel getMathModel() {
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
public VCDocument getVCDocument() {
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
SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
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
	// Initialize Geometry Viewer internal frame
	geoViewer.setGeometry(getMathModel().getMathDescription().getGeometry());
	
	geometryViewerEditorFrame = createDefaultFrame(geoViewer);
	geometryViewerEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
			getMathModelEditor().setToggleButtonSelected("Geometry Viewer", false);
		};
	});

	// Initialize Surface Viewer internal frame
	surfaceViewer.setGeometry(getMathModel().getMathDescription().getGeometry());
	surfaceViewerFrame = createDefaultFrame(surfaceViewer);

		
	// Initialize SimulationsList Viewer internal frame
	String simsListTitle = "Simulations";
	simsPanel.setSimulationWorkspace(new SimulationWorkspace(MathModelWindowManager.this, getMathModel()));
	simsListEditorFrame = new JInternalFrameEnhanced(simsListTitle, true, true, true, true);
	simsListEditorFrame.setFrameIcon(new ImageIcon(getClass().getResource("/images/simulations.gif")));
	simsListEditorFrame.setContentPane(simsPanel);
	simsListEditorFrame.setSize(800, 600);
	simsListEditorFrame.setLocation(500, 300);
	simsListEditorFrame.setMinimumSize(new Dimension(500, 500));
	simsListEditorFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
			getMathModelEditor().setToggleButtonSelected("Simulations", false);
		};
	});
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
	if(evt.getSource() instanceof GeometrySpec && evt.getPropertyName().equals("sampledImage") && evt.getNewValue() != null){
		updateGeometryRegions(false);
	}
	if(evt.getSource() instanceof SubVolume && evt.getPropertyName().equals("name")){
		if(getMathModel() != null && getMathModel().getMathDescription() != null){
			updateGeometryRegions(false);
		}
	}

	if(evt.getSource() instanceof MathDescription && evt.getPropertyName().equals("geometry")){
		resetGeometryListeners((Geometry)evt.getOldValue(),(Geometry)evt.getNewValue());
		surfaceViewer.setGeometry(null);
		if(surfaceViewerFrame != null){
			close(surfaceViewerFrame,getJDesktopPane());
		}
		Geometry geom = ((MathDescription)evt.getSource()).getGeometry();
		geoViewer.setGeometry(geom);
		setDefaultTitle(geometryViewerEditorFrame);
	}
	
	if (evt.getSource() == getMathModel() && evt.getPropertyName().equals("mathDescription")) {
		resetMathDescriptionListeners((MathDescription)evt.getOldValue(),(MathDescription)evt.getNewValue());
	}
	
	if (evt.getSource() == getMathModel() && evt.getPropertyName().equals("simulations")) {
		checkValidSimulationDataViewerFrames();
	}
}

private void updateGeometryRegions(final boolean bChange){
	AsynchClientTask closeSurfaceViewer = new AsynchClientTask("Close surface viewer", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				showSurfaceViewerFrame(false);
			}
		};

	AsynchClientTask geomRegionsTask = new AsynchClientTask("Update Geometric regions", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry newGeom = ((MathModel)getVCDocument()).getMathDescription().getGeometry();
				if (newGeom.getGeometrySurfaceDescription()!=null){
					newGeom.getGeometrySurfaceDescription().updateAll();
				}
			}
	};
	
	AsynchClientTask updateWarningTask = new AsynchClientTask("Update Math warnings", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				vcmlEditor.updateWarningText(((MathModel)getVCDocument()).getMathDescription());
			}
	};
	
		ClientTaskDispatcher.dispatch(getComponent(),
				new Hashtable<String, Object>(),
				new AsynchClientTask[] {closeSurfaceViewer,geomRegionsTask,updateWarningTask}, false);
}

private void resetGeometryListeners(Geometry oldGeometry, Geometry newGeometry){
	if(oldGeometry != null){
		oldGeometry.removePropertyChangeListener(this);
		if(oldGeometry.getGeometrySpec() != null){
			oldGeometry.getGeometrySpec().removePropertyChangeListener(this);
			SubVolume subVolumes[] = oldGeometry.getGeometrySpec().getSubVolumes();
			for (int i = 0;subVolumes!=null && i < subVolumes.length; i++){
				subVolumes[i].removePropertyChangeListener(this);
			}
		}
	}

	if(newGeometry != null ){
		newGeometry.removePropertyChangeListener(this);
		newGeometry.addPropertyChangeListener(this);
		if(newGeometry.getGeometrySpec() != null){
			newGeometry.getGeometrySpec().removePropertyChangeListener(this);
			newGeometry.getGeometrySpec().addPropertyChangeListener(this);
			SubVolume subVolumes[] = newGeometry.getGeometrySpec().getSubVolumes();
			for (int i = 0;subVolumes!=null && i < subVolumes.length; i++){
				subVolumes[i].removePropertyChangeListener(this);
				subVolumes[i].addPropertyChangeListener(this);
			}
		}
	}

}

/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:40:45 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void resetDocument(org.vcell.util.document.VCDocument newDocument) {
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
	setDefaultTitle(geometryViewerEditorFrame);
	setDefaultTitle(surfaceViewerFrame);
	getRequestManager().updateStatusNow();
}

/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 1:58:14 PM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void setJDesktopPane(JDesktopPaneEnhanced newJDesktopPane) {
	jDesktopPane = newJDesktopPane;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 11:08:35 AM)
 * @param newMathModel cbit.vcell.mathmodel.MathModel
 */
private void setMathModel(cbit.vcell.mathmodel.MathModel newMathModel) {
	
	resetGeometryListeners((getMathModel() != null?(getMathModel().getMathDescription() != null?getMathModel().getMathDescription().getGeometry():null):null),
			(newMathModel != null?(newMathModel.getMathDescription() != null?newMathModel.getMathDescription().getGeometry():null):null));
	
	resetMathDescriptionListeners(
		(getMathModel() != null?getMathModel().getMathDescription():null),
		(newMathModel != null?newMathModel.getMathDescription():null));
	
	if (getMathModel() != null) {
		getMathModel().removePropertyChangeListener(this);
//		if(getMathModel().getMathDescription() != null){
//			getMathModel().getMathDescription().removePropertyChangeListener(this);
//		}
	}
	mathModel = newMathModel;
	if (getMathModel() != null) {
		getMathModel().addPropertyChangeListener(this);
//		if(getMathModel().getMathDescription() != null){
//			getMathModel().getMathDescription().addPropertyChangeListener(this);
//		}
	}
}

private void resetMathDescriptionListeners(MathDescription oldMathDescription,MathDescription newMathDescription){
	if(oldMathDescription != null){
		oldMathDescription.removePropertyChangeListener(this);
	}
	if(newMathDescription != null){
		newMathDescription.addPropertyChangeListener(this);
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
private void showSurfaceViewerFrame(boolean bOpen) {
	if(!bOpen){
		close(surfaceViewerFrame, getJDesktopPane());
	}else{
		showFrame(surfaceViewerFrame);
	}
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
		fireNewData(new DataEvent(this, new VCSimulationDataIdentifier(simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simStatusEvent.getJobIndex())));
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