package cbit.vcell.messaging.admin;
import java.util.*;

import cbit.vcell.messaging.SimpleJobStatus;
import cbit.vcell.messaging.admin.sorttable.ManageTableModel;

/**
 * Insert the type's description here.
 * Creation date: (8/28/2003 1:38:32 PM)
 * @author: Fei Gao
 */
public class JobTableModel extends ManageTableModel {
	private final static int columnIndex_UserID = 0;
	private final static int columnIndex_SimID = 1;
	private final static int columnIndex_SolverType = 2;
	private final static int columnIndex_Status = 3;
	private final static int columnIndex_ComputeHost = 4;
	private final static int columnIndex_ServerID = 5;
	private final static int columnIndex_TaskID = 6;
	private final static int columnIndex_SubmitDate = 7;
	private final static int columnIndex_StartDate = 8;
	private final static int columnIndex_EndDate = 9;

/**
 * JobTableModel constructor comment.
 */
public JobTableModel() {
	super();
	columns = new String[]{"User ID", "Sim ID", "Solver Type", "Status", "Compute Host", "Server ID", "Task ID", "Submit Date", "Start Date", "End Date"};
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	if (columnIndex == columnIndex_SimID || columnIndex == columnIndex_TaskID) {
		return Number.class;
	} else if (columnIndex == columnIndex_SubmitDate || columnIndex == columnIndex_StartDate || columnIndex == columnIndex_EndDate) {
		return Date.class;
	} else {		
		return String.class;
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row >= rows.size() || row < 0 || col < 0 || col >= columns.length) {
		return null;
	}

	SimpleJobStatus jobStatus = (SimpleJobStatus)rows.get(row);
	Object[] values = jobStatus.toObjects();
	return values[col];
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
}