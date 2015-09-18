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
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JPanel;

import org.vcell.util.VCAssert;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.importer.DataImporter;
import org.vcell.util.importer.PathwayImportPanel;
import org.vcell.util.importer.PathwayImportPanel.PathwayImportOption;
import org.vcell.util.importer.PathwayImporter;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.ApplicationComponents;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.biomodel.MathematicsPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.desktop.simulation.SimulationWindow.LocalState;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.gui.GeometryViewer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataEvent;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.gui.MIRIAMAnnotationViewer;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:17:07 PM)
 * @author: Ion Moraru
 */
public class BioModelWindowManager extends DocumentWindowManager implements java.beans.PropertyChangeListener, java.awt.event.ActionListener {	
	/**
	 * context for ChildWindowManager
	 */
	private static final String MIRIAM_WINDOW = "MIRIAM_WINDOW";
	private BioModel bioModel = null;
	private Hashtable<SimulationContext, ApplicationComponents> applicationsHash = new Hashtable<SimulationContext, ApplicationComponents>();
	private BioModelEditor bioModelEditor = null;

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
			
	private PathwayImporter pathwayImporter = new PathwayImporter();
	private PathwayImportPanel pathwayImportPanel = null;
	

/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 8:31:18 PM)
 * @param documentWindow cbit.vcell.client.desktop.DocumentWindow
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
public BioModelWindowManager(JPanel panel, RequestManager requestManager, final BioModel bioModel) {
	super(panel, requestManager, bioModel);
	getJPanel().setLayout(new BorderLayout());
	setBioModel(bioModel);
	setBioModelEditor(new BioModelEditor());
	getBioModelEditor().setBioModelWindowManager(this);
	getBioModelEditor().setBioModel(getBioModel());
	getJPanel().add(getBioModelEditor(), BorderLayout.CENTER);
	pathwayImportPanel = new PathwayImportPanel(pathwayImporter, getBioModelEditor().getSelectionManager());
}
public void specialLayout(){
	bioModelEditor.specialLayout();
}

	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	
	String actionCommand = e.getActionCommand();
	final Object source = e.getSource();
	
	if(source instanceof GeometryViewer && (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_GEOMETRY) || actionCommand.equals(GuiConstants.ACTIONCMD_EDITCURRENTSPATIAL_GEOMETRY))){
		final GeometryViewer geometryViewer = (GeometryViewer)source;
		
		AsynchClientTask precomputeAllTask = new AsynchClientTask("precomputeAll geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry newGeom = (Geometry)hashTable.get("doc");
				if(newGeom != null){
					newGeom.precomputeAll(new GeometryThumbnailImageFactoryAWT());
				}
			}
		};

		AsynchClientTask setGeomOnSimContextTask = new AsynchClientTask("Setting geometry on application",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
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
				new AsynchClientTask[] {/*oldEditorTask,*/precomputeAllTask,setGeomOnSimContextTask}
				,TopLevelWindowManager.DEFAULT_CREATEGEOM_SELECT_DIALOG_TITLE,TopLevelWindowManager.APPLY_GEOMETRY_BUTTON_TEXT,
				(actionCommand.equals(GuiConstants.ACTIONCMD_EDITCURRENTSPATIAL_GEOMETRY)?new DocumentWindowManager.GeometrySelectionInfo():null));
	}
	
	if (source instanceof MathematicsPanel && actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_MATH_MODEL)) {
		SimulationContext sc = ((MathematicsPanel)source).getSimulationContext();
		getRequestManager().createMathModelFromApplication(this, "Untitled", sc);
	}

	if (source instanceof GeometryViewer && actionCommand.equals(GuiConstants.ACTIONCMD_CHANGE_GEOMETRY)) {
		final GeometryViewer geometryViewer = (GeometryViewer)source;
		getRequestManager().changeGeometry(this,(SimulationContext)geometryViewer.getGeometryOwner());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:32:07 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void addResultsFrame(SimulationWindow simWindow) {	
	ApplicationComponents appComponents = getApplicationsHash().get(simWindow.getSimOwner());
	appComponents.addDataViewer(simWindow);
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getJPanel());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContext(simWindow);
	if (childWindow==null){
		childWindow = childWindowManager.addChildWindow(simWindow.getDataViewer(), simWindow);
		simWindow.setChildWindow(childWindow);
		childWindow.setIsCenteredOnParent();
		childWindow.pack();
	}
	childWindow.show();
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
		SimulationWindow sw = simWindows[i];
		if (hash.containsKey(simWindows[i].getVcSimulationIdentifier())) {
			sw.resetSimulation((Simulation)hash.get(sw.getVcSimulationIdentifier()));
		} 
		else if (sw.isShowingLocalSimulation()){
			sw.setLocalState(LocalState.LOCAL_SIMMODFIED);
		}
		else
		{
			ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getJPanel());
			ChildWindow childWindow = childWindowManager.getChildWindowFromContext(sw);
			if(childWindow != null) {
				childWindowManager.closeChildWindow(childWindow);
			}
		}
	}
}


/**
 * create components
 */
private void createAppComponents(SimulationContext simContext) {
	ApplicationComponents appComponents = new ApplicationComponents(simContext, this);
	getApplicationsHash().put(simContext, appComponents);
	// register for events
	simContext.addPropertyChangeListener(this);
//	appComponents.getAppEditor().addActionListener(this);
//	appComponents.getGeometrySummaryViewer().addActionListener(this);
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


//NOTE: I changed the visibility of this from private to public - Ed
public BioModel getBioModel() {
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
 * Creation date: (5/27/2004 1:46:17 PM)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel() {
	return (JPanel)getComponent();
}

/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 3:41:06 PM)
 * @return cbit.vcell.document.VCDocument
 */
public BioModel getVCDocument() {
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
	for (ChildWindow childWindow : appComponents.getDataViewerFrames(getJPanel())){
		childWindow.close();
	}
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
	ChildWindowManager.findChildWindowManager(getJPanel()).closeAllChildWindows();
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
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getJPanel());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContext(MIRIAM_WINDOW);
	if (childWindow!=null){
		childWindow.close();
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

	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getJPanel());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContext(MIRIAM_WINDOW);
	if (childWindow==null){
		MIRIAMAnnotationViewer miriamAnnotationViewer = new MIRIAMAnnotationViewer();
		miriamAnnotationViewer.setBiomodel(bioModel);
		childWindow = childWindowManager.addChildWindow(miriamAnnotationViewer, MIRIAM_WINDOW, "View/Add/Delete/Edit MIRIAM Annotation");
		childWindow.setSize(600,400);
		childWindow.setResizable(true);
	}
	else {
		VCAssert.assertTrue(childWindow.isShowing(), "Invisible MIRIAM Window");
	}

	childWindow.show();
}


public boolean hasBlankDocument() {
	return !getRequestManager().isDifferentFromBlank(VCDocumentType.BIOMODEL_DOC, getVCDocument());
}

@Override
public void updateConnectionStatus(ConnectionStatus connStatus) {
	bioModelEditor.updateConnectionStatus(connStatus);	
}

public DataImporter getPathwayImporter() { return pathwayImporter; }

public void importPathway(PathwayImportOption pathwayImportOption) {
	pathwayImportPanel.showDialog(getComponent(), pathwayImportOption);

}
@Override
public DocumentEditor getDocumentEditor() {
	return bioModelEditor; 
}

}

