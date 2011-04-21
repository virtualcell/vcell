package cbit.vcell.client;

import java.awt.Component;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ListSelectionModel;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager.GeometrySelectionInfo;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.desktop.controls.DataListener;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 12:53:14 AM)
 * @author: Ion Moraru
 */
public abstract class TopLevelWindowManager {
	private RequestManager requestManager = null;
	protected transient Vector<DataListener> aDataListener = null;
	protected transient Vector<ExportListener> aExportListener = null;	
	protected transient Vector<DataJobListener> aDataJobListener = null;

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:57:35 AM)
 * @param requestManager cbit.vcell.client.RequestManager
 */
public TopLevelWindowManager(RequestManager requestManager) {
	this.requestManager = requestManager;
}


/**
 * Add a cbit.vcell.desktop.controls.ExportListener.
 */
public void addDataJobListener(DataJobListener newListener) {
	if (aDataJobListener == null) {
		aDataJobListener = new Vector<DataJobListener>();
	};
	aDataJobListener.addElement(newListener);
}


/**
 * Add a cbit.vcell.desktop.controls.DataListener.
 */
public void addDataListener(DataListener newListener) {
	if (aDataListener == null) {
		aDataListener = new Vector<DataListener>();
	};
	aDataListener.addElement(newListener);
}


/**
 * Add a cbit.vcell.desktop.controls.ExportListener.
 */
public void addExportListener(ExportListener newListener) {
	if (aExportListener == null) {
		aExportListener = new Vector<ExportListener>();
	};
	aExportListener.addElement(newListener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void closeWindow() {
	// user initiated
	getRequestManager().closeWindow(getManagerID(), true);
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:58:46 PM)
 */
public void exitApplication() {
	getRequestManager().exitApplication();
}


/**
 * Comment
 */
public void exportDocument() {
	getRequestManager().exportDocument(this);
}


/**
 * Method to support listener events.
 */
protected void fireDataJobMessage(DataJobEvent event) {
	if (aDataJobListener == null) {
		return;
	};
	int currentSize = aDataJobListener.size();
	DataJobListener tempListener = null;
	for (int index = 0; index < currentSize; index++){
		tempListener = aDataJobListener.elementAt(index);
		if (tempListener != null) {
			tempListener.dataJobMessage(event);
		};
	};
}


/**
 * Method to support listener events.
 */
protected void fireExportMessage(ExportEvent event) {
	if (aExportListener == null) {
		return;
	};
	int currentSize = aExportListener.size();
	ExportListener tempListener = null;
	for (int index = 0; index < currentSize; index++){
		tempListener = aExportListener.elementAt(index);
		if (tempListener != null) {
			tempListener.exportMessage(event);
		};
	};
}


/**
 * Method to support listener events.
 */
protected void fireNewData(DataEvent event) {
	if (aDataListener == null) {
		return;
	};
	int currentSize = aDataListener.size();
	DataListener tempListener = null;
	for (int index = 0; index < currentSize; index++){
		tempListener = aDataListener.elementAt(index);
		if (tempListener != null) {
			tempListener.newData(event);
		};
	};
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 1:03:08 AM)
 * @return java.lang.String
 */
public abstract Component getComponent();

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 1:03:08 AM)
 * @return java.lang.String
 */
public abstract String getManagerID();

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 2:45:48 AM)
 * @return cbit.vcell.client.RequestManager
 */
public RequestManager getRequestManager() {
	return requestManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 2:16:26 AM)
 * @return cbit.vcell.client.UserPreferences
 */
public UserPreferences getUserPreferences() {
	return getRequestManager().getUserPreferences();
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2005 3:12:25 PM)
 * @return boolean
 */
public boolean isApplet() {
	return getRequestManager().isApplet();
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:31:27 PM)
 * @return boolean
 */
public abstract boolean isRecyclable();


/**
 * Remove a cbit.vcell.desktop.controls.DataListener.
 */
public void removeDataJobListener(DataJobListener djListener) {
	if (aDataJobListener != null) {
		aDataJobListener.removeElement(djListener);
	};
}


/**
 * Remove a cbit.vcell.desktop.controls.DataListener.
 */
public void removeDataListener(DataListener newListener) {
	if (aDataListener != null) {
		aDataListener.removeElement(newListener);
	};
}


/**
 * Remove a cbit.vcell.desktop.controls.ExportListener.
 */
public void removeExportListener(ExportListener newListener) {
	if (aExportListener != null) {
		aExportListener.removeElement(newListener);
	};
}


/**
 * Comment
 */
public AsynchClientTask[] newDocument(VCDocument.DocumentCreationInfo documentCreationInfo) {
	return getRequestManager().newDocument(this, documentCreationInfo);
}

public void prepareDocumentToLoad(VCDocument doc, boolean bInNewWindow) throws Exception {
	if (doc instanceof BioModel) {
		Simulation[] simulations = ((BioModel)doc).getSimulations();
		VCSimulationIdentifier simIDs[] = new VCSimulationIdentifier[simulations.length];
		for (int i = 0; i < simulations.length; i++){
			simIDs[i] = simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
		}
		getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs);
	} else if (doc instanceof MathModel) {
		Geometry geometry = ((MathModel)doc).getMathDescription().getGeometry();
		geometry.precomputeAll();
		Simulation[] simulations = ((MathModel)doc).getSimulations();
		VCSimulationIdentifier simIDs[] = new VCSimulationIdentifier[simulations.length];
		for (int i = 0; i < simulations.length; i++){
			simIDs[i] = simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
		}
		getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs);
	} else if (doc instanceof Geometry) {
		((Geometry)doc).precomputeAll();
	}
}

GeometrySelectionInfo selectGeometry(boolean bShowCurrentGeomChoice,String dialogText) throws Exception,UserCancelException{
	final int ANALYTIC_1D = 0;
	final int ANALYTIC_2D = 1;
	final int ANALYTIC_3D = 2;
	final int IMAGE_DB = 3;
	final int IMAGE_FILE = 4;
	final int COPY_FROM_BIOMODEL = 5;
	final int COPY_FROM_MATHMODEL = 6;
	final int COPY_FROM_GEOMETRY = 7;
	final int FROM_SCRATCH = 8;
	final int FROM_CURRENT_GEOM = 9;
	int[] geomType = null;

	String[][] choices = new String[][] {{"Analytic Equations (1D)"},{"Analytic Equations (2D)"},{"Analytic Equations (3D)"},
			{"Image based (legacy from database)"},{"Image based (import from file, zip or directory)"},
			{"Copy from BioModel application"},{"Copy from MathModel"},{"Copy from saved Geometry"},
			{"From scratch"}};
	if(bShowCurrentGeomChoice){
		Vector<String[]> choiceV = new Vector<String[]>();
		choiceV.addAll(Arrays.asList(choices));
		choiceV.add(new String[] {"From Current Geometry"});
		choices = choiceV.toArray(new String[0][]);
	}
	geomType = DialogUtils.showComponentOKCancelTableList(
			getComponent(), 
			dialogText,
			new String[] {"Geometry Type"}, 
			choices, ListSelectionModel.SINGLE_SELECTION);

	VCDocument.DocumentCreationInfo documentCreationInfo = null;
	VCDocumentInfo vcDocumentInfo = null;
	if(geomType[0] == ANALYTIC_1D){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_1D);
	}else if(geomType[0] == ANALYTIC_2D){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_2D);
	}else if(geomType[0] == ANALYTIC_3D){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_3D);
	}else if(geomType[0] == IMAGE_DB){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_DBIMAGE);
	}else if(geomType[0] == IMAGE_FILE){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_FILE);
	}else if(geomType[0] == COPY_FROM_BIOMODEL){
		vcDocumentInfo = ((ClientRequestManager)getRequestManager()).selectDocumentFromType(VCDocument.BIOMODEL_DOC, this);
	}else if(geomType[0] == COPY_FROM_MATHMODEL){
		vcDocumentInfo = ((ClientRequestManager)getRequestManager()).selectDocumentFromType(VCDocument.MATHMODEL_DOC, this);
	}else if(geomType[0] == COPY_FROM_GEOMETRY){
		vcDocumentInfo = ((ClientRequestManager)getRequestManager()).selectDocumentFromType(VCDocument.GEOMETRY_DOC, this);
	}else if(geomType[0] == FROM_SCRATCH){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_FROM_SCRATCH);
	}else if(geomType[0] == FROM_CURRENT_GEOM){
		return new DocumentWindowManager.GeometrySelectionInfo();
	}else{
		throw new IllegalArgumentException("Error selecting geometry, Unknown Geometry type "+geomType[0]);
	}
	DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo = null;
	if(documentCreationInfo != null){
		geometrySelectionInfo = new DocumentWindowManager.GeometrySelectionInfo(documentCreationInfo);
	}else{
		geometrySelectionInfo = new DocumentWindowManager.GeometrySelectionInfo(vcDocumentInfo);
	}
	
	return geometrySelectionInfo;
}

public static final String B_SHOW_OLD_GEOM_EDITOR = "B_SHOW_OLD_GEOM_EDITOR";
public static final String DEFAULT_CREATEGEOM_SELECT_DIALOG_TITLE = "Choose new geometry type to create";
void createGeometry(final Geometry currentGeometry,final AsynchClientTask[] afterTasks,String selectDialogTitle,final String applyGeometryButtonText){
	
	try{
		final Hashtable<String, Object> hash = new Hashtable<String, Object>();
		Vector<AsynchClientTask> createGeomTaskV = new Vector<AsynchClientTask>();
		final DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo =
			selectGeometry(currentGeometry != null && currentGeometry.getDimension() >0,selectDialogTitle);
		hash.put(B_SHOW_OLD_GEOM_EDITOR, false);
		if(geometrySelectionInfo.getDocumentCreationInfo() != null){
			if(ClientRequestManager.isImportGeometryType(geometrySelectionInfo.getDocumentCreationInfo())){
				//Create imported Geometry
				createGeomTaskV.addAll(Arrays.asList(
					((ClientRequestManager)getRequestManager()).createNewGeometryTasks(this,
						geometrySelectionInfo.getDocumentCreationInfo(),
						afterTasks,
						applyGeometryButtonText)));
			}else{//Create Analytic Geometry
				hash.put(B_SHOW_OLD_GEOM_EDITOR, true);
				createGeomTaskV.addAll(Arrays.asList(((ClientRequestManager)getRequestManager()).createNewDocument(this,
						geometrySelectionInfo.getDocumentCreationInfo())));
				createGeomTaskV.addAll(Arrays.asList(afterTasks));
			}
			hash.put("guiParent", (Component)getComponent());
			hash.put("requestManager", getRequestManager());
		}else{//Copy from existing BioModel,MathModel,Geometry
			createGeomTaskV.add(new AsynchClientTask("loading Geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					Geometry copiedGeom = null;
					if(geometrySelectionInfo.bFromCurrentGeom()){
						copiedGeom = XmlHelper.XMLToGeometry(new XMLSource(XmlHelper.geometryToXML(currentGeometry)));
					}else{
						copiedGeom =
							((ClientRequestManager)getRequestManager()).getGeometryFromDocumentSelection(
								getComponent(),geometrySelectionInfo.getVCDocumentInfo(),true);
					}
					final Vector<AsynchClientTask> runtimeTasksV = new Vector<AsynchClientTask>();
					if(copiedGeom.getGeometrySpec().getImage() != null &&
						copiedGeom.getGeometrySpec().getNumAnalyticSubVolumes() == 0){
						runtimeTasksV.addAll(Arrays.asList(((ClientRequestManager)getRequestManager()).createNewGeometryTasks(TopLevelWindowManager.this,
								new VCDocument.DocumentCreationInfo(VCDocument.GEOMETRY_DOC, VCDocument.GEOM_OPTION_DBIMAGE),
								afterTasks,
								applyGeometryButtonText)));
						hashTable.put("guiParent", (Component)getComponent());
						hashTable.put("requestManager", getRequestManager());
						hashTable.put(ClientRequestManager.IMAGE_FROM_DB, copiedGeom.getGeometrySpec().getImage());
					}else{
						hashTable.put(B_SHOW_OLD_GEOM_EDITOR, true);
						//preload sampledimage to prevent gui delay later
						copiedGeom.precomputeAll();
						hashTable.put("doc",copiedGeom);
						runtimeTasksV.addAll(Arrays.asList(afterTasks));
					}
					new Thread(
						new Runnable() {
							public void run() {
								ClientTaskDispatcher.dispatch(getComponent(),
										hash,runtimeTasksV.toArray(new AsynchClientTask[0]), false,false,null,true);
							}
						}
					).start();
				}			
			});
		}
		ClientTaskDispatcher.dispatch(getComponent(), hash, createGeomTaskV.toArray(new AsynchClientTask[0]), false,false,null,true);
		
	} catch (UserCancelException e1) {
		return;
	} catch (Exception e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(getComponent(), e1.getMessage(), e1);
	}
	


}

}