package cbit.vcell.messaging.admin;

import java.util.Comparator;
import java.util.Date;

import org.vcell.util.ComparableObject;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.gui.sorttable.ColumnComparator;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;


/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 2:24:48 PM)
 * @author: Fei Gao
 */
@SuppressWarnings("serial")
public class ServiceStatusTableModel extends DefaultSortTableModel<ComparableObject> {
/**
 * ServiceStatusTableModel constructor comment.
 */
public ServiceStatusTableModel() {
	super(new String[] {"Site", "Type", "Ordinal", "Startup Type", "MemoryMB", "Date", "Status", "Status Message", "PBS Job ID"});
}

/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class<?> getColumnClass(int columnIndex) {
	if (columnIndex == 5) {
		return Date.class;
	} else if (columnIndex == 2 || columnIndex == 4) {
		return Number.class;
	} else if (columnIndex == 1) {
		return ServiceType.class;
	} else {
		return String.class;
	}
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {	
	ComparableObject serviceStatus = getValueAt(row);
	Object[] values = serviceStatus.toObjects();
	return values[col];
}

public Comparator<ComparableObject> getComparator(int col, boolean ascending) {
	return new ColumnComparator(col, ascending);
}
}
