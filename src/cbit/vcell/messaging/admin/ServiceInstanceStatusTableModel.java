package cbit.vcell.messaging.admin;

import java.util.Collections;
import java.util.Date;

import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.gui.sorttable.ColumnComparator;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;


/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 10:46:32 AM)
 * @author: Fei Gao
 */
public class ServiceInstanceStatusTableModel extends DefaultSortTableModel<ServiceInstanceStatus> {

public ServiceInstanceStatusTableModel() {
	super(new String[]{"Site", "Type", "Ordinal", "Host", "Start Date", "Running"});
}


public synchronized boolean contains(ServiceSpec serviceSpec) {
	return rows.contains(serviceSpec);
}


public Class<?> getColumnClass(int columnIndex) {
	if (columnIndex == 0 || columnIndex == 3) {
		return String.class;
	}		
	if (columnIndex == 1) {
		return ServiceType.class;
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

public void sortColumn(int col, boolean ascending) {
	Collections.sort(rows, new ColumnComparator(col, ascending));
}
}