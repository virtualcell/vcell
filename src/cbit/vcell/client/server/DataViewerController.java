package cbit.vcell.client.server;

import cbit.vcell.client.data.*;
import cbit.vcell.desktop.controls.*;
import org.vcell.util.DataAccessException;

/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 1:51:53 PM)
 * @author: Ion Moraru
 */
public interface DataViewerController extends DataListener {
/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:33:04 PM)
 * @return javax.swing.JPanel
 */
DataViewer createViewer() throws DataAccessException;

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:43:41 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
void refreshData() throws DataAccessException;
}