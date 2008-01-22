package cbit.vcell.messaging.admin;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 10:46:32 AM)
 * @author: Fei Gao
 */
public class ServiceInstanceStatusTableModel extends ManageTableModel {

public ServiceInstanceStatusTableModel() {
	super();
	columns =  new String[]{"Site", "Type", "Ordinal", "Host", "Start Date", "Running"};
}


public synchronized boolean contains(ServiceSpec serviceSpec) {
	return rows.contains(serviceSpec);
}


public Class<?> getColumnClass(int columnIndex) {
	if (columnIndex == 0 || columnIndex == 1 || columnIndex == 3) {
		return String.class;
	}		
	if (columnIndex == 5) {
		return Boolean.class;
	}
	if (columnIndex == 2) {
		return Number.class;
	}
	if (columnIndex == 2) {
		return Date.class;
	}
	return Object.class;
}

public Object getValueAt(int row, int col) {
	if (row >= rows.size() || row < 0 || col < 0 || col >= columns.length) {
		return null;
	}

	ServiceInstanceStatus status = (ServiceInstanceStatus)rows.get(row);
	Object[] values = status.toObjects();
	return values[col];
}
}