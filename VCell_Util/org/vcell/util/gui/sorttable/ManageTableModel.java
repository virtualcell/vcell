package org.vcell.util.gui.sorttable;
import java.util.*;


/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 8:54:09 AM)
 * @author: Fei Gao
 */
public abstract class ManageTableModel extends org.vcell.util.gui.sorttable.DefaultSortTableModel  {
	protected List rows = Collections.synchronizedList(new ArrayList());
	protected String columns[] = null;		

/**
 * ManageTableModel constructor comment.
 */
public ManageTableModel() {
	super();
}


/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columns.length;
}


public String getColumnName(int column) {
	return columns[column];
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 10:50:18 AM)
 * @param list java.util.List
 */
public List getData() {
	return rows;
}


/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return rows.size();
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row) {
	return rows.get(row);
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized int indexOf(Object service) {
	return rows.indexOf(service);
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void insert(Object service) {
	if (!rows.contains(service)) {
		rows.add(service);
		fireTableDataChanged();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 10:50:18 AM)
 * @param list java.util.List
 */
public void setData(List list) {
	rows.clear();	
	if (list != null) {
		rows.addAll(list);
	}
	
	resortColumn();		
	fireTableDataChanged();
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void setValueAt(int row, Object server) {
	rows.set(row, server);
	fireTableDataChanged();
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2004 11:34:00 AM)
 * @param col int
 * @param ascending boolean
 */
public void sortColumn(int col, boolean ascending) {
	Collections.sort(rows, new ColumnComparator(col, ascending));
}
}