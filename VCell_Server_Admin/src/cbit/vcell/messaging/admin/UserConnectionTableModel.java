package cbit.vcell.messaging.admin;
import java.util.Date;

import org.vcell.util.gui.sorttable.ManageTableModel;

/**
 * Insert the type's description here.
 * Creation date: (2/27/2006 10:21:21 AM)
 * @author: Fei Gao
 */
public class UserConnectionTableModel extends ManageTableModel {
	private final static int columnIndex_UserID = 0;
//	private final static int columnIndex_ElapsedTime = 2;
//	private final static int columnIndex_ConnectedTime = 1;

/**
 * UserConnectionTableModel constructor comment.
 */
public UserConnectionTableModel() {
	super();
	columns = new String[]{"User ID"};
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	if (columnIndex == columnIndex_UserID) {
		return String.class;
	}

	return null;
}


	/**
	 * Returns an attribute value for the cell at <I>columnIndex</I>
	 * and <I>rowIndex</I>.
	 *
	 * @param	rowIndex	the row whose value is to be looked up
	 * @param	columnIndex 	the column whose value is to be looked up
	 * @return	the value Object at the specified cell
	 */
public Object getValueAt(int row, int col) {
	if (row >= rows.size() || row < 0 || col < 0 || col >= columns.length) {
		return null;
	}

	SimpleUserConnection userconn = (SimpleUserConnection)rows.get(row);
	Object[] values = userconn.toObjects();
	return values[col];
}
}