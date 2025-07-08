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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.*;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageSizeInfo;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.DataJobListenerHolder;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.DocumentWindowManager.GeometrySelectionInfo;
import org.vcell.api.server.ConnectionStatus;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.simdata.DataEvent;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 12:53:14 AM)
 * @author: Ion Moraru
 */
public abstract class TopLevelWindowManager implements DataJobListenerHolder {
	public static final String WORK_SPACE_SAMPLESIZE = "WORK_SPACE_SAMPLESIZE";
	private RequestManager requestManager = null;
	protected transient Vector<DataListener> aDataListener = null;
	protected transient Vector<ExportListener> aExportListener = null;
	protected transient Vector<DataJobListener> aDataJobListener = null;
	private static final Logger lg = LogManager.getLogger(TopLevelWindowManager.class);
	private static final LinkedList<WeakReference<TopLevelWindowManager>> allManagers = new LinkedList<>();
	private static final Object PREFERENCES_WINDOW = "PREFERENCES_WINDOW";

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:57:35 AM)
 * @param requestManager cbit.vcell.client.RequestManager
 */
public TopLevelWindowManager(RequestManager requestManager) {
	this.requestManager = requestManager;
	allManagers.add(new WeakReference<>(this));
}

public static TopLevelWindowManager activeManager( ) {
	LWTopFrame activetf = null;
	Optional<WeakReference<LWTopFrame>> first = LWTopFrame.liveWindows().findFirst();
	if (first.isPresent()) {
		activetf = first.get( ).get( );
	}
	TopLevelWindowManager fallback = null;
	Iterator<WeakReference<TopLevelWindowManager>> iter = allManagers.iterator();
	while (iter.hasNext()) {
		WeakReference<TopLevelWindowManager> wr = iter.next();
		TopLevelWindowManager tlwm = wr.get();
		if (tlwm == null) {
			iter.remove();
			continue;
		}
		if (activetf == null) {
			if (lg.isWarnEnabled()) {
				lg.warn("no active LWTopFrame in TopLevelWindowManager?");
			}
			return tlwm; //guess
		}
		if (fallback == null) {
			fallback = tlwm; //return this if can't find the right window
		}
		Component cmpt = tlwm.getComponent();
		if (cmpt != null && SwingUtilities.isDescendingFrom(cmpt,activetf)) {
			return tlwm;
		}
	}

	return fallback;

}

public final void showPreferencesWindow(){

	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContext(PREFERENCES_WINDOW);
	VCellConfigurationPanel vcellConfigurationPanel = new VCellConfigurationPanel(this);
	if (childWindow==null){
		childWindow = childWindowManager.addChildWindow(vcellConfigurationPanel, PREFERENCES_WINDOW, "View VCell Properties");
		childWindow.setSize(650,300);
		childWindow.setResizable(true);
		vcellConfigurationPanel.setChildWindow(childWindow);
	}
	else {
		VCAssert.assertTrue(childWindow.isShowing(), "Invisible Preferences Window");
	}

	childWindow.show();
	
if(vcellConfigurationPanel.getButtonPushed() == VCellConfigurationPanel.ActionButtons.Close) {
				// we do nothing on close
	} else {
		
	}
	
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
public void exportDocument(FileFilter forceFileFilter) {
	getRequestManager().exportDocument(this,forceFileFilter);
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
 * @return java.awt.Component
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
		ArrayList<VCSimulationIdentifier> simIDs = new ArrayList<VCSimulationIdentifier>();
		for (int i = 0; i < simulations.length; i++){
			SimulationInfo simulationInfo = simulations[i].getSimulationInfo();
			if (simulationInfo != null) {
				simIDs.add(simulationInfo.getAuthoritativeVCSimulationIdentifier());
			}
		}
		RequestManager rm = getRequestManager();
		ConnectionStatus stat = rm.getConnectionStatus();
		if (stat.getStatus( ) == ConnectionStatus.CONNECTED) {
			rm.getDocumentManager().preloadSimulationStatus(simIDs.toArray(new VCSimulationIdentifier[0]));
		}
	} else if (doc instanceof MathModel) {
		Geometry geometry = ((MathModel)doc).getMathDescription().getGeometry();
		geometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
		Simulation[] simulations = ((MathModel)doc).getSimulations();
//		VCSimulationIdentifier simIDs[] = new VCSimulationIdentifier[simulations.length];
		ArrayList<VCSimulationIdentifier> simIDs = new ArrayList<>();
		for (int i = 0; i < simulations.length; i++){
			if(simulations[i].getSimulationInfo() != null){
				simIDs.add(simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier());
			}
		}
		getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs.toArray(new VCSimulationIdentifier[0]));
	} else if (doc instanceof Geometry) {
		((Geometry)doc).precomputeAll(new GeometryThumbnailImageFactoryAWT());
	}
}

GeometrySelectionInfo selectGeometry(boolean bShowCurrentGeomChoice,String dialogText) throws Exception,UserCancelException{
	final int ANALYTIC_1D = 0;
	final int ANALYTIC_2D = 1;
	final int ANALYTIC_3D = 2;
	final int IMAGE_FILE = 3;
	final int MESH_FILE = 4;
	final int FROM_SCRATCH = 5;
	final int CSGEOMETRY_3D = 6;
//	final int FIJI_IMAGEJ = 7;
//	final int BLENDER_SURF = 8;

	int[] geomType = null;

	String[][] choices = new String[][] {
			{"Analytic Equations (1D)"},
			{"Analytic Equations (2D)"},
			{"Analytic Equations (3D)"},
			{"Image based (import from file, zip or directory)"},
			{"Mesh based (import from STL file)"},
			{"New Blank Image Canvas"},
			{"Constructive Solid Geometry (3D)"},
//			{"Import from Fiji/Imagej"},
//			{"Import from Blender"}
			};
	geomType = DialogUtils.showComponentOKCancelTableList(
			getComponent(),
			dialogText,
			new String[] {"Geometry Type"},
			choices, ListSelectionModel.SINGLE_SELECTION);

	VCDocument.DocumentCreationInfo documentCreationInfo = null;
	if(geomType[0] == ANALYTIC_1D){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_1D);
	}else if(geomType[0] == ANALYTIC_2D){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_2D);
	}else if(geomType[0] == ANALYTIC_3D){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_3D);
	}else if(geomType[0] == IMAGE_FILE || geomType[0] == MESH_FILE){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_FILE);
	}else if(geomType[0] == FROM_SCRATCH){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_FROM_SCRATCH);
	}else if(geomType[0] == CSGEOMETRY_3D){
		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_CSGEOMETRY_3D);
	}
//	else if(geomType[0] == FIJI_IMAGEJ){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_FIJI_IMAGEJ);
//	}else if(geomType[0] == BLENDER_SURF){
//		documentCreationInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, VCDocument.GEOM_OPTION_BLENDER);
//	}
	else{
		throw new IllegalArgumentException("Error selecting geometry, Unknown Geometry type "+geomType[0]);
	}
	DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo = null;
	if(documentCreationInfo != null){
		geometrySelectionInfo = new DocumentWindowManager.GeometrySelectionInfo(documentCreationInfo);
	}

	return geometrySelectionInfo;
}

public static final String B_SHOW_OLD_GEOM_EDITOR = "B_SHOW_OLD_GEOM_EDITOR";
public static final String DEFAULT_CREATEGEOM_SELECT_DIALOG_TITLE = "Choose new geometry type to create";
public static final String APPLY_GEOMETRY_BUTTON_TEXT = "Finish";
void createGeometry(final Geometry currentGeometry,final AsynchClientTask[] afterTasks,String selectDialogTitle,final String applyGeometryButtonText,DocumentWindowManager.GeometrySelectionInfo preSelect){
	boolean bCancellable = false;
	try{
		final Hashtable<String, Object> hash = new Hashtable<String, Object>();
		Vector<AsynchClientTask> createGeomTaskV = new Vector<AsynchClientTask>();
		final DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo =
			(preSelect==null?selectGeometry(currentGeometry != null && currentGeometry.getDimension() >0,selectDialogTitle):preSelect);
		hash.put(B_SHOW_OLD_GEOM_EDITOR, false);
		if(geometrySelectionInfo.getDocumentCreationInfo() != null){
			if(ClientRequestManager.isImportGeometryType(geometrySelectionInfo.getDocumentCreationInfo())){
//				bCancellable =
//					geometrySelectionInfo.getDocumentCreationInfo().getOption() == VCDocument.GEOM_OPTION_FIJI_IMAGEJ ||
//					geometrySelectionInfo.getDocumentCreationInfo().getOption() == VCDocument.GEOM_OPTION_BLENDER;
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
			hash.put("guiParent", getComponent());
			hash.put("requestManager", getRequestManager());
		}else{//Copy from WorkSpace
			createGeomTaskV.add(new AsynchClientTask("loading Geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					if(selectDialogTitle.equals(BioModelWindowManager.FIELD_DATA_FLAG)) {
						addWorkspaceGeomSizeSelection(hash,currentGeometry);
						hash.put(BioModelWindowManager.FIELD_DATA_FLAG, true);
					}
					final Vector<AsynchClientTask> runtimeTasksV = new Vector<>();
					VCDocument.DocumentCreationInfo workspaceDocCreateInfo;
					if(currentGeometry.getGeometrySpec().getNumAnalyticOrCSGSubVolumes() > 0 && currentGeometry.getGeometrySpec().getImage() == null){
						workspaceDocCreateInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC,VCDocument.GEOM_OPTION_FROM_WORKSPACE_ANALYTIC);
					}else if(currentGeometry.getGeometrySpec().getImage() != null && currentGeometry.getGeometrySpec().getNumAnalyticOrCSGSubVolumes() == 0){
						workspaceDocCreateInfo = new VCDocument.DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC,VCDocument.GEOM_OPTION_FROM_WORKSPACE_IMAGE);
					}else{
						throw new Exception("Unexpected image configuration for workspace geometry.");
					}
					runtimeTasksV.addAll(Arrays.asList(((ClientRequestManager)getRequestManager()).createNewGeometryTasks(TopLevelWindowManager.this,
							workspaceDocCreateInfo,
							afterTasks,
							applyGeometryButtonText)));
					hashTable.put("guiParent", getComponent());
					hashTable.put("requestManager", getRequestManager());
					hashTable.put(ClientRequestManager.GEOM_FROM_WORKSPACE, currentGeometry);
					new Thread(
                            () -> ClientTaskDispatcher.dispatch(getComponent(),hash,runtimeTasksV.toArray(new AsynchClientTask[0]), false,false,null,true)
                    ).start();
				}

			});
		}
		ClientTaskDispatcher.dispatch(getComponent(), hash, createGeomTaskV.toArray(new AsynchClientTask[0]), false,bCancellable,null,true);

	} catch (UserCancelException ignored) {
    } catch (Exception e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(getComponent(), e1.getMessage(), e1);
	}
}

private void addWorkspaceGeomSizeSelection(final Hashtable<String, Object> hash,Geometry sourceGeom) throws Exception{
	if(TopLevelWindowManager.this instanceof DocumentWindowManager) {
		Simulation[] simulations = null;
		SimulationContext[] simContexts = null;
		SimulationContext selectedSC = null;
		
	
		ArrayList<Object[]> allRows = new ArrayList<Object[]>();
		ArrayList<ISize> iSizes = new ArrayList<ISize>();

		if(this instanceof BioModelWindowManager) {
			BioModelWindowManager bmwm = ((BioModelWindowManager)this);
			//System.out.println(bmwm);
			if(bmwm.getDocumentEditor() instanceof cbit.vcell.client.desktop.biomodel.BioModelEditor) {
				cbit.vcell.client.desktop.biomodel.BioModelEditor bme = (cbit.vcell.client.desktop.biomodel.BioModelEditor)bmwm.getDocumentEditor();
				//System.out.println(bme);
				selectedSC = bme.getSelectedSimulationContext();
//				System.out.println(selectedSC);
			}
		}
		if(((DocumentWindowManager)TopLevelWindowManager.this).getVCDocument() instanceof BioModel) {
			simContexts = ((BioModel)((DocumentWindowManager)TopLevelWindowManager.this).getVCDocument()).getSimulationContexts();
		}else if (((DocumentWindowManager)TopLevelWindowManager.this).getVCDocument() instanceof MathModel) {
			simulations = ((MathModel)((DocumentWindowManager)TopLevelWindowManager.this).getVCDocument()).getSimulations();
		}
		String[] simColumnNames = null;
		if(simContexts != null) {
			simColumnNames = new String[] {"x","y","z","appName","simName"};	
		}else {
			simColumnNames = new String[] {"x","y","z","simName"};
		}
		ArrayList<Object[]> simRows = new ArrayList<Object[]>();
		ArrayList<CartesianMesh> meshes = new ArrayList<CartesianMesh>();
		for (int j=0;j<(simContexts==null?1:simContexts.length);j++) {
			Geometry geom = null;
			if(simContexts != null) {
				if(simContexts[j] != selectedSC) {
					continue;
				}
				simulations = simContexts[j].getSimulations();
				geom = simContexts[j].getGeometry();
			}else {
				geom = ((MathModel)((DocumentWindowManager)TopLevelWindowManager.this).getVCDocument()).getGeometry();
			}
//			ISize defaultSamplesize = geom.getGeometrySpec().getDefaultSampledImageSize();
			if(simulations != null && simulations.length > 0) {
				for(int i=0;i<simulations.length;i++) {
					Object[] row = new Object[simColumnNames.length];
//					if(i==0) {
//						row[0] = defaultSamplesize.getX();
//						row[1] = defaultSamplesize.getY();
//						row[2] = defaultSamplesize.getZ();
//						row[3] = (simContexts!=null?simContexts[j].getName():"default");
//						if(simContexts!=null) {
//							row[4] = "default";
//						}
//						simRows.add(row);						
//						VCImageUncompressed vcImageUnc = new VCImageUncompressed(null, new byte[defaultSamplesize.getXYZ()], geom.getExtent(), defaultSamplesize.getX(), defaultSamplesize.getY(), defaultSamplesize.getZ());
//						CartesianMesh simpleMesh = CartesianMesh.createSimpleCartesianMesh(
//							geom.getOrigin(), 
//							geom.getExtent(),
//							defaultSamplesize,
//							new RegionImage(vcImageUnc, geom.getDimension(), geom.getExtent(), geom.getOrigin(), RegionImage.NO_SMOOTHING));
//						meshes.add(simpleMesh);
//					}
					if(simulations[i].getMeshSpecification() != null &&
						simulations[i].getMeshSpecification() != null) {
						row = new Object[simColumnNames.length];
						ISize samplingSize = simulations[i].getMeshSpecification().getSamplingSize();
						row[0] = samplingSize.getX();
						row[1] = samplingSize.getY();
						row[2] = samplingSize.getZ();
						row[3] = (simContexts!=null?simContexts[j].getName():simulations[i].getName());
						if(simContexts!=null) {
							row[4] = simulations[i].getName();
						}
						simRows.add(row);
						
						VCImageUncompressed vcImageUnc = new VCImageUncompressed(null, new byte[samplingSize.getXYZ()], geom.getExtent(), samplingSize.getX(), samplingSize.getY(), samplingSize.getZ());
						CartesianMesh simpleMesh = CartesianMesh.createSimpleCartesianMesh(
							geom.getOrigin(), 
							geom.getExtent(),
							samplingSize,
							new RegionImage(vcImageUnc, geom.getDimension(), geom.getExtent(), geom.getOrigin(), RegionImage.NO_SMOOTHING));
						meshes.add(simpleMesh);
					}
				}
			}
		}
		
		for(int i=0;i<simRows.size();i++) {
			ISize iSize = meshes.get(i).getISize();
			iSizes.add(iSize);
			allRows.add(new Object[] {iSize.getX(),iSize.getY(),iSize.getZ(),"Simulation="+(simColumnNames.length==4?simRows.get(i)[3]:simRows.get(i)[3]+":"+simRows.get(i)[4])});
		}

//		DocumentManager documentManager = this.getRequestManager().getDocumentManager();
//		FieldDataDBOperationSpec fdos = FieldDataDBOperationSpec.createGetExtDataIDsSpec(documentManager.getUser());
//		FieldDataDBOperationResults fieldDataDBOperationResults = documentManager.fieldDataDBOperation(fdos);
//		ExternalDataIdentifier[] externalDataIdentifierArr = fieldDataDBOperationResults.extDataIDArr;
//		Arrays.sort(externalDataIdentifierArr, new Comparator<ExternalDataIdentifier>() {
//			@Override
//			public int compare(ExternalDataIdentifier o1, ExternalDataIdentifier o2) {
//				// TODO Auto-generated method stub
//				return o1.getName().compareToIgnoreCase(o2.getName());
//			}});
//		for(int i=0;externalDataIdentifierArr != null && i<externalDataIdentifierArr.length;i++) {
//			try {
//				FieldDataFileOperationSpec fieldDataFileOperationSpec = FieldDataFileOperationSpec.createInfoFieldDataFileOperationSpec(externalDataIdentifierArr[i].getSimulationKey(), externalDataIdentifierArr[i].getOwner(), externalDataIdentifierArr[i].getJobIndex());
//				FieldDataFileOperationResults fieldDataFileOperationResults = documentManager.fieldDataFileOperation(fieldDataFileOperationSpec);
////				System.out.println(externalDataIdentifierArr[i].getName()+" "+fieldDataFileOperationResults.iSize);
//				ISize iSize = fieldDataFileOperationResults.iSize;
//				iSizes.add(iSize);
//				//isizes.add(iSize);
//				allRows.add(new Object[] {iSize.getX(),iSize.getY(),iSize.getZ(),"FieldData='"+externalDataIdentifierArr[i].getName()+"'"});
//				VCImageUncompressed vcImageUnc = new VCImageUncompressed(null, new byte[iSize.getXYZ()], fieldDataFileOperationResults.extent,iSize.getX(),iSize.getY(),iSize.getZ());
//				int dimension = 1 + (iSize.getY()>1?1:0) + (iSize.getZ()>1?1:0);
//				CartesianMesh simpleMesh = CartesianMesh.createSimpleCartesianMesh(
//					fieldDataFileOperationResults.origin, 
//					fieldDataFileOperationResults.extent,
//					iSize,
//					new RegionImage(vcImageUnc, dimension, fieldDataFileOperationResults.extent, fieldDataFileOperationResults.origin, RegionImage.NO_SMOOTHING));
//				meshes.add(simpleMesh);
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//			}			
//		}
//
//		if(allRows.size() > 0) {
		if(simRows.size() > 0) {
			Object[][] rowData = simRows.toArray(new Object[0][]);
			int[] selections = DialogUtils.showComponentOKCancelTableList(TopLevelWindowManager.this.getComponent(), "Select Simulation for Geom Size",
					simColumnNames, rowData, ListSelectionModel.SINGLE_SELECTION);
//			Object[][] rowData = allRows.toArray(new Object[0][]);
//			int[] selections = DialogUtils.showComponentOKCancelTableList(TopLevelWindowManager.this.getComponent(), "Select Simulation for Geom Size",
//					new String[] {"X","Y","Z","SourceType"}, rowData, ListSelectionModel.SINGLE_SELECTION);

			if(selections != null && selections.length == 1) {
//				ImageSizeInfo imagesizeInfo = new ImageSizeInfo("internal",meshes.get(selections[0]).getISize(),1,new double[] {0},0);
				ImageSizeInfo imagesizeInfo = new ImageSizeInfo("internal",iSizes.get(selections[0]),1,new double[] {0},0);
				hash.put(ClientRequestManager.NEW_IMAGE_SIZE_INFO, imagesizeInfo);
				
				VCImage image = null;
				if(sourceGeom.getGeometrySpec().getImage() == null) {
					image = sourceGeom.getGeometrySpec().createSampledImage(iSizes.get(selections[0]));
				}else {
					image = sourceGeom.getGeometrySpec().getImage();
				}
				ISize samplingSize = new ISize(image.getNumX(),image.getNumY(),image.getNumZ());//
				VCImageUncompressed vcImageUnc = new VCImageUncompressed(null, new byte[samplingSize.getXYZ()], sourceGeom.getExtent(), samplingSize.getX(), samplingSize.getY(), samplingSize.getZ());
				CartesianMesh sourceMesh = CartesianMesh.createSimpleCartesianMesh(
					sourceGeom.getOrigin(), 
					sourceGeom.getExtent(),
					samplingSize,
					new RegionImage(vcImageUnc, sourceGeom.getDimension(), sourceGeom.getExtent(), sourceGeom.getOrigin(), RegionImage.NO_SMOOTHING));

				hash.put("newMesh", meshes.get(selections[0]));
				hash.put("sourceMesh", sourceMesh);
			}
		}
	}
}

public static abstract class OpenModelInfoHolder{
	private final String simName;
	private final SimulationInfo simInfo;
	private final int jobIndex;
	//public final boolean isTimeUniform;
	private final boolean isCompartmental;
	protected OpenModelInfoHolder(
			String simName,//use this when the sim is unsaved (simInfo==null)
			SimulationInfo argSimInfo,
			int argJobIndex,
			//boolean argistu,
			boolean argisc
			){
		this.simName = simName;
		simInfo = argSimInfo;
		jobIndex = argJobIndex;
		//isTimeUniform = argistu;
		isCompartmental = argisc;
		if(this.simInfo != null && this.simName != null && !this.simName.equals(this.simInfo.getName())){
			throw new IllegalArgumentException("simName '"+this.simName+"' does not match simInfo.getName '"+this.simInfo.getName()+"'");
		}
	}
	public String getSimName() {
		return simName;
	}
	public SimulationInfo getSimInfo() {
		return simInfo;
	}
	public int getJobIndex() {
		return jobIndex;
	}
	public boolean isCompartmental() {
		return isCompartmental;
	}

}
public static class FDSimMathModelInfo extends OpenModelInfoHolder{
	private Version version;
	private MathDescription mathDescription;
	public FDSimMathModelInfo(
			String simName,
			Version version,
			MathDescription mathDescription,
			SimulationInfo argSI,
			int jobIndex,
			//boolean argistu,
			boolean argisc
			){
		super(simName,argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,argistu,*/argisc);
		this.version = version;
		this.mathDescription = mathDescription;
	}
	public Version getMathModelVersion(){
		return version;
	}
	public MathDescription getMathDescription(){
		return mathDescription;
	}
}
public static class FDSimBioModelInfo extends OpenModelInfoHolder{
	private Version version;
	private SimulationContext simulationContext;
	public FDSimBioModelInfo(
			String simName,
			Version version,
			SimulationContext simulationContext,
			SimulationInfo argSI,
			int jobIndex,
			//boolean argistu,
			boolean argisc
		){
		super(simName,argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,argistu,*/argisc);
		this.version = version;
		this.simulationContext = simulationContext;
	}
	public Version getBioModelVersion(){
		return version;
	}
	public SimulationContext getSimulationContext(){
		return simulationContext;
	}
}

public static OpenModelInfoHolder selectOpenModelsFromDesktop(Container requester,RequestManager requestManager,boolean bIncludeSimulations,String title,boolean bExcludeCompartmental) throws UserCancelException,DataAccessException{
	try {
		GeneralGuiUtils.setCursorThroughout(requester, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		OpenModelInfoHolder[] simInfoHolders = requestManager.getOpenDesktopDocumentInfos(bIncludeSimulations);
		if(simInfoHolders == null || simInfoHolders.length == 0){
			return null;
		}
		String[] colNames = null;
		if(bIncludeSimulations){
			colNames = new String[] {"Simulation","Scan Index","Model","Type","Application","Owner","Date"};
		}else{
			colNames = new String[] {"Model","Type","Application","Owner","Date"};
		}
		Vector<String[]> rowsV = new Vector<String[]>();
		Vector<OpenModelInfoHolder> simInfoHolderV = new Vector<OpenModelInfoHolder>();
		for(int i=0;i<simInfoHolders.length;i+= 1){
			if(bExcludeCompartmental && simInfoHolders[i].isCompartmental){
				continue;
			}
			String[] rows = new String[colNames.length];
			int colIndex = 0;
			if(simInfoHolders[i] instanceof FDSimMathModelInfo){
				MathModelInfo mmInfo = null;
				if(((FDSimMathModelInfo)simInfoHolders[i]).getMathModelVersion() != null){
					mmInfo = requestManager.getDocumentManager().getMathModelInfo(
							((FDSimMathModelInfo)simInfoHolders[i]).getMathModelVersion().getVersionKey());
				}
				if(bIncludeSimulations){
					rows[colIndex++] = simInfoHolders[i].getSimName();
					rows[colIndex++] = simInfoHolders[i].jobIndex+"";
				}
				rows[colIndex++] = (mmInfo==null?"New Document":mmInfo.getVersion().getName());
				rows[colIndex++] = "MathModel";
				rows[colIndex++] = "";
				rows[colIndex++] = (simInfoHolders[i].simInfo==null?"never saved":simInfoHolders[i].simInfo.getOwner().getName());
				rows[colIndex++] = (mmInfo==null?"never saved":mmInfo.getVersion().getDate().toString());

			}else if(simInfoHolders[i] instanceof FDSimBioModelInfo){
				BioModelInfo bmInfo = null;
				if(((FDSimBioModelInfo)simInfoHolders[i]).getBioModelVersion() != null){
					bmInfo = requestManager.getDocumentManager().getBioModelInfo(
							((FDSimBioModelInfo)simInfoHolders[i]).getBioModelVersion().getVersionKey());
				}
				if(bIncludeSimulations){
					rows[colIndex++] = simInfoHolders[i].getSimName();
					rows[colIndex++] = simInfoHolders[i].jobIndex+"";
				}
				rows[colIndex++] = (bmInfo==null?"New Document":bmInfo.getVersion().getName());
				rows[colIndex++] = "BioModel";
				rows[colIndex++] = ((FDSimBioModelInfo)simInfoHolders[i]).getSimulationContext().getName();
				rows[colIndex++] = (simInfoHolders[i].simInfo==null?"never saved":simInfoHolders[i].simInfo.getOwner().getName());
				rows[colIndex++] = (bmInfo==null?"never saved":bmInfo.getVersion().getDate().toString());
			}
			rowsV.add(rows);
			simInfoHolderV.add(simInfoHolders[i]);
		}
		if(rowsV.size() == 0){
			return null;
		}
		String[][] rows = new String[rowsV.size()][];
		rowsV.copyInto(rows);
		GeneralGuiUtils.setCursorThroughout(requester, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		int[] selectionIndexArr =  PopupGenerator.showComponentOKCancelTableList(
				requester, title,
				colNames, rows, ListSelectionModel.SINGLE_SELECTION);
		if(selectionIndexArr != null && selectionIndexArr.length > 0){
			return simInfoHolderV.elementAt(selectionIndexArr[0]);//simInfoHolders[selectionIndexArr[0]];
		}
		throw UserCancelException.CANCEL_GENERIC;
	} finally {
		GeneralGuiUtils.setCursorThroughout(requester, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}

}
