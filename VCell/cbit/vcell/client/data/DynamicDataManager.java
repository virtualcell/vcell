package cbit.vcell.client.data;
import cbit.util.DataAccessException;
import cbit.vcell.desktop.controls.*;
import javax.swing.*;
import cbit.vcell.server.*;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 1:51:53 PM)
 * @author: Ion Moraru
 */
public interface DynamicDataManager extends DataListener {
/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:33:04 PM)
 * @return javax.swing.JPanel
 */
DataViewer createViewer(boolean expectODEData) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:43:41 PM)
 * @exception cbit.util.DataAccessException The exception description.
 */
void refreshData() throws DataAccessException;
}