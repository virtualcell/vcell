package cbit.vcell.client;
import cbit.vcell.desktop.controls.DataListener;
import cbit.rmi.event.ExportListener;
import cbit.vcell.desktop.controls.DataEvent;

import java.io.*;
import java.net.*;
import swingthreads.SwingWorker;
import java.awt.*;
import cbit.vcell.client.desktop.*;
import cbit.rmi.event.ExportEvent;
import cbit.util.AsynchProgressPopup;
import cbit.util.FileFilters;

import javax.swing.*;

import org.vcell.util.document.VCDocument;

import cbit.vcell.client.server.*;
import cbit.vcell.client.*;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 12:53:14 AM)
 * @author: Ion Moraru
 */
public abstract class TopLevelWindowManager {
	private cbit.vcell.client.RequestManager requestManager = null;

	protected transient java.util.Vector aDataListener = null;

	protected transient java.util.Vector aExportListener = null;
	
	protected transient java.util.Vector aDataJobListener = null;

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
public void addDataJobListener(cbit.rmi.event.DataJobListener newListener) {
	if (aDataJobListener == null) {
		aDataJobListener = new java.util.Vector();
	};
	aDataJobListener.addElement(newListener);
}


/**
 * Add a cbit.vcell.desktop.controls.DataListener.
 */
public void addDataListener(DataListener newListener) {
	if (aDataListener == null) {
		aDataListener = new java.util.Vector();
	};
	aDataListener.addElement(newListener);
}


/**
 * Add a cbit.vcell.desktop.controls.ExportListener.
 */
public void addExportListener(ExportListener newListener) {
	if (aExportListener == null) {
		aExportListener = new java.util.Vector();
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
protected void fireDataJobMessage(cbit.rmi.event.DataJobEvent event) {
	if (aDataJobListener == null) {
		return;
	};
	int currentSize = aDataJobListener.size();
	cbit.rmi.event.DataJobListener tempListener = null;
	for (int index = 0; index < currentSize; index++){
		tempListener = (cbit.rmi.event.DataJobListener)aDataJobListener.elementAt(index);
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
		tempListener = (ExportListener)aExportListener.elementAt(index);
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
	cbit.vcell.desktop.controls.DataListener tempListener = null;
	for (int index = 0; index < currentSize; index++){
		tempListener = (cbit.vcell.desktop.controls.DataListener)aDataListener.elementAt(index);
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
abstract Component getComponent();

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
public cbit.vcell.client.RequestManager getRequestManager() {
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
public void removeDataJobListener(cbit.rmi.event.DataJobListener djListener) {
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
public void newDocument(VCDocument.DocumentCreationInfo documentCreationInfo) {
	getRequestManager().newDocument(documentCreationInfo);
}
}