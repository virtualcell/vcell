package cbit.vcell.messaging.admin;

import java.util.*;

import cbit.vcell.messaging.VCServerInfo;
import cbit.vcell.messaging.admin.sorttable.ManageTableModel;

/**
 * Insert the type's description here.
 * Creation date: (8/15/2003 2:24:49 PM)
 * @author: Fei Gao
 */
public class ServerStatusTableModel extends ManageTableModel {
/**
 * ServerTableModel constructor comment.
 */
public ServerStatusTableModel() {
	super();
	columns = new String[] {"Host", "Type", "Name", "Alive", "Start Date", "CPU%", "Free Memory", "FJM", "TJM", "MJM"};
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	if (columnIndex == 3)
		return Boolean.class;

	if (columnIndex == 4)
		return Date.class;

	if (columnIndex > 4)
		return Number.class;
		
	return String.class;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row >= rows.size() || col >= columns.length) {
		return null;
	}		
	VCServerInfo vcServerInfo = (VCServerInfo)rows.get(row);
	Object[] values = vcServerInfo.toObjects();		
	return values[col];
}
}
