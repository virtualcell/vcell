package cbit.vcell.client.desktop;
import cbit.vcell.client.*;
import cbit.vcell.client.server.*;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 3:37:07 PM)
 * @author: Ion Moraru
 */
public interface TopLevelWindow {
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
}