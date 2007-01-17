package cbit.vcell.messaging.admin;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 2:24:48 PM)
 * @author: Fei Gao
 */
public class ServiceStatusTableModel extends ManageTableModel {
/**
 * ServiceStatusTableModel constructor comment.
 */
public ServiceStatusTableModel() {
	super();
	columns = new String[] {"Host", "Type", "Name", "Alive", "Start Date", "FJM", "TJM", "MJM"};
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void clear() {
	rows.clear();
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	if (columnIndex == 3) {
		return Boolean.class;
	} else if (columnIndex == 4) {
		return Date.class;
	} else if (columnIndex > 4) {
		return Number.class;
	} else {		
		return String.class;
	}
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row >= rows.size() || col >= columns.length) {
		return null;
	}		
	VCServiceInfo serviceInfo = (VCServiceInfo)rows.get(row);
	Object[] values = serviceInfo.toObjects();
	return values[col];
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void insert(List serviceList) {
	Iterator iter = serviceList.iterator();
	while (iter.hasNext()) {
		Object obj = iter.next();
		if (!rows.contains(obj)) {
			rows.add(obj);
		}
	}
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void remove(Object service) {
	rows.remove(service);
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void remove(List serviceList) {
	Iterator iter = serviceList.iterator();
	while (iter.hasNext()) {
		rows.remove(iter.next());
	}

	fireTableDataChanged();
}
}
