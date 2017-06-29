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
import cbit.vcell.client.server.ConnectionStatus;
/**
 * Insert the type's description here.
 * Creation date: (5/10/2004 1:13:41 PM)
 * @author: Ion Moraru
 */
public class StatusUpdater extends org.vcell.util.gui.AsynchGuiUpdater {
	private MDIManager mdiManager = null;
	private int progressRunner = 0;
	private ConnectionStatus connectionStatus = null;

/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 1:14:37 PM)
 */
public StatusUpdater(MDIManager mdiManager) {
	setMdiManager(mdiManager);
	setDelay(5000); // defaulting to 5 seconds
	start(); // start automatically
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 4:48:18 AM)
 */
private void animationDuringInit() {
	getMdiManager().updateWhileInitializing(progressRunner % 100);
	progressRunner += 5;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 4:36:03 PM)
 * @return cbit.vcell.client.MDIManager
 */
private MDIManager getMdiManager() {
	return mdiManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 2:36:54 AM)
 */
protected void guiToDo() {
	// update memory periodically
	updateMemory(Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory());
	// also animate connection initialization period
	if (connectionStatus != null && connectionStatus.getStatus() == ConnectionStatus.INITIALIZING) {
		animationDuringInit();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 2:36:54 AM)
 */
protected void guiToDo(java.lang.Object params) {
	// do memory again... usefull on brand new windows
	updateMemory(Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory());
	// do connection update on demand
	if (params instanceof ConnectionStatus) {
		ConnectionStatus currentConnStatus = (ConnectionStatus)params;
		updateConnectionStatus(currentConnStatus);
		if (currentConnStatus.getStatus() == ConnectionStatus.INITIALIZING) {
			animationDuringInit();
			setDelay(100);
			restart();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 4:36:03 PM)
 * @param newMdiManager cbit.vcell.client.MDIManager
 */
private void setMdiManager(MDIManager newMdiManager) {
	mdiManager = newMdiManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 4:46:00 AM)
 */
private void updateConnectionStatus(ConnectionStatus currentConnStatus) {
	this.connectionStatus = currentConnStatus;
	getMdiManager().updateConnectionStatus(currentConnStatus);
	progressRunner = 0;
	setDelay(5000);
	restart();
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 3:34:10 AM)
 */
private void updateMemory(long free, long total) {
	getMdiManager().updateMemoryStatus(free, total);
}
}
