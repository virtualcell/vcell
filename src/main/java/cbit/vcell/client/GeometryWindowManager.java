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

import javax.swing.JPanel;

import org.vcell.util.document.VCDocument;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.geometry.GeometryDisplayPanel;
import cbit.vcell.client.desktop.geometry.GeometryEditor;
import cbit.vcell.client.desktop.geometry.SurfaceViewerPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (5/25/2004 2:46:07 AM)
 * @author: Ion Moraru
 */
public class GeometryWindowManager extends DocumentWindowManager implements java.beans.PropertyChangeListener {
	private Geometry geometry = null;
	private GeometryEditor geometryEditor = null;

	private SurfaceViewerPanel surfaceViewer;
	private GeometryDisplayPanel geoViewer;
	
/**
 * GeometryWindowManager constructor comment.
 * @param pane javax.swing.JDesktopPane
 * @param requestManager cbit.vcell.client.RequestManager
 * @param vcDocument cbit.vcell.document.VCDocument
 * @param newlyCreatedDesktops int
 */
public GeometryWindowManager(JPanel panel, RequestManager requestManager, final Geometry aGeometry) {
	super(panel, requestManager, aGeometry);
	setGeometry(aGeometry);
	getJPanel().setLayout(new BorderLayout());
	createGeometryEditor();
	getJPanel().add(getGeometryEditor(), BorderLayout.NORTH);
	surfaceViewer = new SurfaceViewerPanel();
	geoViewer = new GeometryDisplayPanel();
}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:26:51 AM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void addResultsFrame(SimulationWindow simWindow) {
	// ignore
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void createGeometryEditor() {
	geometryEditor = new GeometryEditor();
	geometryEditor.setGeometry(getGeometry());
	geometryEditor.setGeometryWindowManager(this);
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
public void geometryEditorButtonPressed(boolean bGeoEditorButtonSelected) {
	showGeometryViewer(bGeoEditorButtonSelected);
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @return cbit.vcell.geometry.Geometry
 */
private Geometry getGeometry() {
	return geometry;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @return cbit.vcell.client.desktop.geometry.GeometryEditor
 */
private GeometryEditor getGeometryEditor() {
	return geometryEditor;
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
 * Creation date: (5/25/2004 2:46:07 AM)
 * @return cbit.vcell.document.VCDocument
 */
public VCDocument getVCDocument() {
	return getGeometry();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 7:57:23 AM)
 * @return cbit.vcell.document.VCDocument
 */
SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier) {
	return null;
}


private void hideGeometryViewer(){
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContentPane(geoViewer);
	if (childWindow != null){
		childWindow.close();
	}
}

private void hideSurfaceViewer(){
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContentPane(surfaceViewer);
	if (childWindow != null){
		childWindow.close();
	}
}

private void showGeometryViewer() {

	String geoViewerTitle = "Geometry Editor";
	geoViewer.setGeometry(getGeometry());
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContentPane(geoViewer);
	if (childWindow == null){
		childWindow = childWindowManager.addChildWindow(geoViewer,geoViewer,geoViewerTitle);
		childWindow.setSize(600, 600);
		childWindow.setIsCenteredOnParent();
		childWindow.addChildWindowListener(new ChildWindowListener() {
			public void closing(ChildWindow childWindow) {
				getGeometryEditor().setToggleButtonSelected("Geometry Editor", false);
			}
		});
	}
	childWindow.show();
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void showSurfaceViewer() {

	String surfaceViewerTitle = "Surface Viewer";
	surfaceViewer.setGeometry(getGeometry());
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
	ChildWindow childWindow = childWindowManager.getChildWindowFromContentPane(surfaceViewer);
	if (childWindow == null){
		childWindow = childWindowManager.addChildWindow(surfaceViewer, surfaceViewer, surfaceViewerTitle);
		childWindow.setSize(600, 600);
		childWindow.addChildWindowListener(new ChildWindowListener() {
			public void closing(ChildWindow childWindow) {
				getGeometryEditor().setToggleButtonSelected("Surface Viewer", false);
			}
		});
	}
	childWindow.show();
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 2:46:07 AM)
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

	if(evt.getSource() instanceof GeometrySpec){
		getGeometryEditor().setToggleButtonSelected("Surface Viewer",false);
		//do this because button.setSelected does not fire action event
		ChildWindow childWindow = ChildWindowManager.findChildWindowManager(getComponent()).getChildWindowFromContentPane(surfaceViewer);
		if (childWindow!=null && childWindow.isShowing()){
			surfaceViewerButtonPressed(false);
		}
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 2:00:39 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void resetDocument(VCDocument newDocument) {
	setGeometry((Geometry)newDocument);
	setDocumentID(getGeometry());
	geoViewer.setGeometry(getGeometry());
	surfaceViewer.setGeometry(getGeometry());
	getRequestManager().updateStatusNow();
	System.out.println("XXXXXXXXXXXXXXXXXXXX not yet finished");
}

/**
 * Comment
 */
public void saveDocumentAsNew() {
	getRequestManager().saveDocumentAsNew(this);
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newGeometry cbit.vcell.geometry.Geometry
 */
private void setGeometry(Geometry newGeometry) {
	if(geometry != null && geometry.getGeometrySpec() != null){
		geometry.getGeometrySpec().removePropertyChangeListener(this);
	}
	
	geometry = newGeometry;

	if(newGeometry != null && newGeometry.getGeometrySpec() != null){
		newGeometry.getGeometrySpec().addPropertyChangeListener(this);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void showGeometryViewer(boolean bGeoEditorButtonSelected) {
	if (bGeoEditorButtonSelected) {
		showGeometryViewer();
	}else{
		hideGeometryViewer();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
private void showSurfaceViewer(boolean bSurfaceViewerButtonSelected) {
	if (bSurfaceViewerButtonSelected) {
		showSurfaceViewer();
		
		if(getGeometry() != null && getGeometry().getGeometrySurfaceDescription() != null && getGeometry().getGeometrySurfaceDescription().getSurfaceCollection() == null){
			try{
				surfaceViewer.updateSurfaces();
			}catch(Exception e){
				PopupGenerator.showErrorDialog(this, "Error initializing Surfaces\n"+e.getClass().getName()+"\n"+e.getMessage(), e);
			}
		}
		
	}else{
		hideSurfaceViewer();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 1:38:49 AM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simStatusChanged(SimStatusEvent simStatusEvent) {
	// ignore, geometries don't have simulations to worry about...
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 10:19:49 AM)
 * @param newJDesktopPane javax.swing.JDesktopPane
 */
public void surfaceViewerButtonPressed(boolean bSurfaceViewerButtonSelected) {
	showSurfaceViewer(bSurfaceViewerButtonSelected);
}


@Override
public void updateConnectionStatus(ConnectionStatus connStatus) {
	// TODO Auto-generated method stub
	
}


@Override
public DocumentEditor getDocumentEditor() {
	return null;
}
}
