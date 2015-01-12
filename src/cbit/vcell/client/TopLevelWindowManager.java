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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ListSelectionModel;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.Version;
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
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.simdata.DataEvent;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationIdentifier;
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
		getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs.toArray(new VCSimulationIdentifier[0]));
	} else if (doc instanceof MathModel) {
		Geometry geometry = ((MathModel)doc).getMathDescription().getGeometry();
		geometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
		Simulation[] simulations = ((MathModel)doc).getSimulations();
		VCSimulationIdentifier simIDs[] = new VCSimulationIdentifier[simulations.length];
		for (int i = 0; i < simulations.length; i++){
			simIDs[i] = simulations[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
		}
		getRequestManager().getDocumentManager().preloadSimulationStatus(simIDs);
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
	
	int[] geomType = null;

	String[][] choices = new String[][] {
			{"Analytic Equations (1D)"},
			{"Analytic Equations (2D)"},
			{"Analytic Equations (3D)"},
			{"Image based (import from file, zip or directory)"},
			{"Mesh based (import from STL file)"},
			{"New Blank Image Canvas"},
			{"Constructed Solid Geometry (3D)"}};
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
	}else{
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
	
	try{
		final Hashtable<String, Object> hash = new Hashtable<String, Object>();
		Vector<AsynchClientTask> createGeomTaskV = new Vector<AsynchClientTask>();
		final DocumentWindowManager.GeometrySelectionInfo geometrySelectionInfo =
			(preSelect==null?selectGeometry(currentGeometry != null && currentGeometry.getDimension() >0,selectDialogTitle):preSelect);
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
		}else{//Copy from WorkSpace
			createGeomTaskV.add(new AsynchClientTask("loading Geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					final Vector<AsynchClientTask> runtimeTasksV = new Vector<AsynchClientTask>();
					VCDocument.DocumentCreationInfo workspaceDocCreateInfo = null;
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
					hashTable.put("guiParent", (Component)getComponent());
					hashTable.put("requestManager", getRequestManager());
					hashTable.put(ClientRequestManager.GEOM_FROM_WORKSPACE, currentGeometry);
					new Thread(
						new Runnable() {
							public void run() {
								ClientTaskDispatcher.dispatch(getComponent(),hash,runtimeTasksV.toArray(new AsynchClientTask[0]), false,false,null,true);
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

public static abstract class OpenModelInfoHolder{
	public final SimulationInfo simInfo;
	public final int jobIndex;
	//public final boolean isTimeUniform;
	public final boolean isCompartmental;
	protected OpenModelInfoHolder(
			SimulationInfo argSimInfo,
			int argJobIndex,
			//boolean argistu,
			boolean argisc
			){
		simInfo = argSimInfo;
		jobIndex = argJobIndex;
		//isTimeUniform = argistu;
		isCompartmental = argisc;
		
	}
}
public static class FDSimMathModelInfo extends OpenModelInfoHolder{
	private Version version;
	private MathDescription mathDescription;
	public FDSimMathModelInfo(
			Version version,
			MathDescription mathDescription,
			SimulationInfo argSI,
			int jobIndex,
			//boolean argistu,
			boolean argisc
			){
		super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,argistu,*/argisc);
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
			Version version,
			SimulationContext simulationContext,
			SimulationInfo argSI,
			int jobIndex,
			//boolean argistu,
			boolean argisc
		){
		super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,argistu,*/argisc);
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
		BeanUtils.setCursorThroughout(requester, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
					rows[colIndex++] = simInfoHolders[i].simInfo.getName();
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
					rows[colIndex++] = simInfoHolders[i].simInfo.getName();
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
		BeanUtils.setCursorThroughout(requester, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		int[] selectionIndexArr =  PopupGenerator.showComponentOKCancelTableList(
				requester, title,
				colNames, rows, ListSelectionModel.SINGLE_SELECTION);
		if(selectionIndexArr != null && selectionIndexArr.length > 0){
			return simInfoHolderV.elementAt(selectionIndexArr[0]);//simInfoHolders[selectionIndexArr[0]];
		}
		throw UserCancelException.CANCEL_GENERIC;
	} finally {
		BeanUtils.setCursorThroughout(requester, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}

}
