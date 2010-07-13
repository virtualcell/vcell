package org.vcell.util.gui.sorttable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 8:54:09 AM)
 * @author: Fei Gao
 */
public abstract class ManageTableModel<T> extends DefaultSortTableModel  {
	protected List<T> rows = Collections.synchronizedList(new ArrayList<T>());
	protected String columns[] = null;		

/**
 * ManageTableModel constructor comment.
 */
public ManageTableModel() {
	super();
}

public ManageTableModel(String[] cols) {
	super();
	columns = cols;
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
public List<T> getData() {
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
public T getValueAt(int row) {
	return rows.get(row);
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized int indexOf(T value) {
	return rows.indexOf(value);
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 8:24:43 AM)
 * @param status cbit.vcell.messaging.admin.PerformanceStatus
 */
public synchronized void insert(T value) {
	if (!rows.contains(value)) {
		rows.add(value);
		fireTableDataChanged();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 10:50:18 AM)
 * @param list java.util.List
 */
public void setData(List<T> list) {
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
public synchronized void setValueAt(int row, T value) {
	rows.set(row, value);
	fireTableDataChanged();
}

public synchronized void clear() {
	rows.clear();
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