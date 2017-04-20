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
import javax.swing.*;

import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.server.*;

import java.util.*;
public interface MDIManager {
/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:45:21 PM)
 * @param windowID java.lang.String
 */
JFrame blockWindow(String windowID);


/**
 * @return number of visible windows remaining 
 */
long closeWindow(String windowID);


/**
 * @param manager non-null
 * @return new window
 */
DocumentWindow createNewDocumentWindow(DocumentWindowManager manager);


///**
// * Insert the method's description here.
// * Creation date: (5/24/2004 8:57:45 PM)
// * @return cbit.vcell.client.desktop.DatabaseWindowManager
// */
//BNGWindowManager getBNGWindowManager();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:57:45 PM)
 * @return cbit.vcell.client.desktop.DatabaseWindowManager
 */
DatabaseWindowManager getDatabaseWindowManager();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:39:55 PM)
 * @return int
 */
int getNumCreatedDocumentWindows();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:57:45 PM)
 * @return cbit.vcell.client.desktop.DatabaseWindowManager
 */
TestingFrameworkWindowManager getTestingFrameworkWindowManager();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 7:55:40 PM)
 * @return cbit.vcell.client.desktop.TopLevelWindowManager
 * @param windowID java.lang.String
 */
TopLevelWindowManager getWindowManager(String windowID);


Collection<TopLevelWindowManager> getWindowManagers();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:28:53 PM)
 * @return boolean
 * @param windowID java.lang.String
 */
boolean haveWindow(String windowID);


/**
 * Insert the method's description here.
 * Creation date: (1/19/2005 5:29:57 PM)
 */
void refreshRecyclableWindows();


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:15:19 AM)
 * @param windowID java.lang.String
 */
void setCanonicalTitle(String windowID);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:14:53 AM)
 * @param windowID java.lang.String
 */
void showWindow(String windowID);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:45:21 PM)
 * @param windowID java.lang.String
 */
void unBlockWindow(String windowID);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:38:08 PM)
 * @param connectionStatus cbit.vcell.client.server.ConnectionStatus
 */
void updateConnectionStatus(ConnectionStatus connectionStatus);


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:16:02 AM)
 * @param oldID java.lang.String
 * @param newID java.lang.String
 */
void updateDocumentID(String oldID, String newID);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:38:44 PM)
 * @param freeBytes long
 * @param totalBytes long
 */
void updateMemoryStatus(long freeBytes, long totalBytes);


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:39:09 PM)
 * @param progress int
 */
void updateWhileInitializing(int progress);

FieldDataWindowManager getFieldDataWindowManager();

TopLevelWindowManager getFocusedWindowManager();
}
