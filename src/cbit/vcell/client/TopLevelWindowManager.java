package cbit.vcell.client;

import java.awt.Component;
import java.util.Vector;

import org.vcell.util.document.VCDocument;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.desktop.controls.DataEvent;
import cbit.vcell.desktop.controls.DataListener;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
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
		if (!bInNewWindow) {
			// don't have to preload when opening a biomodel.			
			// only preload when there are open applications which could only happen 
			// when the document is loaded into the same window 
			// for saveAs, save, save new edition and revert to saved
			((BioModelWindowManager)this).prepareToLoad((BioModel)doc); 
			return;
		}
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
}