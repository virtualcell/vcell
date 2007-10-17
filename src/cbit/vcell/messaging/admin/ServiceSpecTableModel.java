package cbit.vcell.messaging.admin;

/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 10:46:32 AM)
 * @author: Fei Gao
 */
public class ServiceSpecTableModel extends ManageTableModel {

public ServiceSpecTableModel() {
	super();
	columns =  new String[]{"Site", "Type", "Ordinal", "Startup Type", "MemoryMB", "Running"};
}


public synchronized boolean contains(ServiceSpec serviceSpec) {
	return rows.contains(serviceSpec);
}


public Class<?> getColumnClass(int columnIndex) {
	if (columnIndex == 0 || columnIndex == 1) {
		return String.class;
	}		
	if (columnIndex == 5) {
		return Boolean.class;
	}
	return Number.class;
}

public Object getValueAt(int row, int col) {
	if (row >= rows.size() || row < 0 || col < 0 || col >= columns.length) {
		return null;
	}

	ServiceSpec serviceSpec = (ServiceSpec)rows.get(row);
	Object[] values = serviceSpec.toObjects();
	return values[col];
}
}