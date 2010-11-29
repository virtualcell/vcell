package cbit.vcell.messaging.admin;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.vcell.util.ComparableObject;
import org.vcell.util.gui.sorttable.ColumnComparator;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

/**
 * Insert the type's description here.
 * Creation date: (8/28/2003 1:38:32 PM)
 * @author: Fei Gao
 */
public class JobTableModel extends DefaultSortTableModel<ComparableObject> {
	private final static int columnIndex_UserID = 0;
	private final static int columnIndex_SimID = 1;
	private final static int columnIndex_JobIndex = 2;
	private final static int columnIndex_SolverType = 3;
	private final static int columnIndex_Status = 4;
	private final static int columnIndex_ComputeHost = 5;
	private final static int columnIndex_ServerID = 6;
	private final static int columnIndex_TaskID = 7;
	private final static int columnIndex_SubmitDate = 8;
	private final static int columnIndex_StartDate = 9;
	private final static int columnIndex_EndDate = 10;
	private final static int columnIndex_ElapsedTime = 11;

/**
 * JobTableModel constructor comment.
 */
public JobTableModel() {
	super(new String[]{"User ID", "Sim ID", "Job Index", "Solver Type", "Status", "Compute Host", "Server ID", "Task ID", "Submit Date", "Start Date", "End Date", "Elapsed (h:m:s)"});
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	if (columnIndex == columnIndex_SimID || columnIndex == columnIndex_TaskID || columnIndex == columnIndex_JobIndex) {
		return Number.class;
	} else if (columnIndex == columnIndex_SubmitDate || columnIndex == columnIndex_StartDate || columnIndex == columnIndex_EndDate) {
		return Date.class;
	} else if (columnIndex == columnIndex_ElapsedTime){
		return Long.class;
	} else {		
		return String.class;
	}
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	ComparableObject jobStatus = getValueAt(row);
	Object[] values = jobStatus.toObjects();
	return values[col];
}

public Comparator<ComparableObject> getComparator(int col, boolean ascending) {
	return new ColumnComparator(col, ascending);
}

}