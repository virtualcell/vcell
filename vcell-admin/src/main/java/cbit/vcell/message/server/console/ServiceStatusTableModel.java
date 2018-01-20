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
import java.util.Date;

import org.vcell.util.ComparableObject;
import org.vcell.util.gui.sorttable.ColumnComparator;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.message.server.bootstrap.ServiceType;


/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 2:24:48 PM)
 * @author: Fei Gao
 */
@SuppressWarnings("serial")
public class ServiceStatusTableModel extends VCellSortTableModel<ComparableObject> {
/**
 * ServiceStatusTableModel constructor comment.
 */
public ServiceStatusTableModel() {
	super(new String[] {"Site", "Type", "Ordinal", "Startup Type", "MemoryMB", "Date", "Status", "Status Message", "HTC Job ID"});
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
