/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.console;
import java.util.Comparator;

import org.vcell.util.ComparableObject;
import org.vcell.util.gui.sorttable.ColumnComparator;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
/**
 * Insert the type's description here.
 * Creation date: (2/27/2006 10:21:21 AM)
 * @author: Fei Gao
 */
@SuppressWarnings("serial")
public class UserConnectionTableModel extends VCellSortTableModel<ComparableObject> {
	private final static int columnIndex_UserID = 0;
	//private final static int columnIndex_ElapsedTime = 2;
	//private final static int columnIndex_ConnectedTime = 1;

/**
 * UserConnectionTableModel constructor comment.
 */
public UserConnectionTableModel() {
	super(new String[]{"User ID"});
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class<?> getColumnClass(int columnIndex) {
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
	ComparableObject userconn = getValueAt(row);
	Object[] values = userconn.toObjects();
	return values[col];
}

public Comparator<ComparableObject> getComparator(int col, boolean ascending) {
	return new ColumnComparator(col, ascending);
}
}
