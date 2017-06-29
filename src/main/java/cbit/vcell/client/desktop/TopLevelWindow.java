/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;
import org.vcell.client.logicalwindow.LWContainerHandle;

import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.server.ConnectionStatus;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 3:37:07 PM)
 * @author: Ion Moraru
 */
public interface TopLevelWindow extends LWContainerHandle {
/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:38:00 PM)
 * @return cbit.vcell.client.desktop.TopLevelWindowManager
 */
TopLevelWindowManager getTopLevelWindowManager();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 6:04:35 PM)
 * @param connectionStatus cbit.vcell.client.server.ConnectionStatus
 */
void updateConnectionStatus(ConnectionStatus connectionStatus);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 6:08:05 PM)
 * @param freeBytes long
 * @param totalBytes long
 */
void updateMemoryStatus(long freeBytes, long totalBytes);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 6:08:37 PM)
 * @param i int
 */
void updateWhileInitializing(int i);


ChildWindowManager getChildWindowManager();
}
