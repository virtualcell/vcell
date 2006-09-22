package cbit.vcell.messaging.admin;
import cbit.vcell.messaging.VCServiceInfo;
import cbit.vcell.messaging.VCellServiceConfiguration;
import cbit.vcell.messaging.admin.sorttable.ManageTableModel;

/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 10:46:32 AM)
 * @author: Fei Gao
 */
public class ServiceConfigTableModel extends ManageTableModel {	
	private String columns2[] = {"Host", "Type", "Name", "Start Command", "Stop Command", "Log File", "Auto Start"};
	private String columns1[] = {"Host", "Type", "Name", "Start Command", "Stop Command", "Auto Start"};

/**
 * SubserviceTableModel constructor comment.
 */
public ServiceConfigTableModel(boolean isServerManager) {
	super();
	setServerType(isServerManager);		
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized boolean contains(VCServiceInfo serviceInfo) {
	return rows.contains(serviceInfo.getConfiguration());
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	if (columns == columns1 && columnIndex == 5 || columnIndex == 6)
		return Boolean.class;
		
	return String.class;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row) {
	if (row >= rows.size() || row < 0) {
		return null;
	}

	VCellServiceConfiguration config = (VCellServiceConfiguration)rows.get(row);
	return new VCServiceInfo(config);
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row >= rows.size() || row < 0 || col < 0 || col >= columns.length) {
		return null;
	}

	VCellServiceConfiguration config = (VCellServiceConfiguration)rows.get(row);
	Object[] values = config.toObjects();
	return values[col];	
}

/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void insert(Object serviceInfo) {
	rows.add(((VCServiceInfo)serviceInfo).getConfiguration());
	fireTableDataChanged();
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void remove(int index) {
	if (index >= rows.size() || index < 0) {
		System.out.println("Array index out of bound " + index);		
	} else {
		rows.remove(index);
		fireTableDataChanged();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void remove(Object obj) {
	VCServiceInfo serviceInfo = (VCServiceInfo)obj;
	if (rows.contains(serviceInfo.getConfiguration())) {
		rows.remove(serviceInfo.getConfiguration());
		fireTableDataChanged();
	} else {
		System.out.println("ServiceConfigTableModel::remove - Service doesn't exist " + serviceInfo);		
	}
}


/**
 * SubserviceTableModel constructor comment.
 */
private void setServerType(boolean isservermanager) {
	if (isservermanager) {
		columns = columns1;
	} else {
		columns = columns2;
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void setValueAt(int row, Object obj) {
	VCServiceInfo serviceInfo = (VCServiceInfo)obj;
	rows.set(row, serviceInfo.getConfiguration());
	fireTableDataChanged();
}
}