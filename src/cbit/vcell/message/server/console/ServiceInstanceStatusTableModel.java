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
import cbit.vcell.message.server.ServiceSpec.ServiceType;


/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 10:46:32 AM)
 * @author: Fei Gao
 */
@SuppressWarnings("serial")
public class ServiceInstanceStatusTableModel extends VCellSortTableModel<ComparableObject> {

public ServiceInstanceStatusTableModel() {
	super(new String[]{"Site", "Type", "Ordinal", "Host", "Start Date", "Running"});
}

public Class<?> getColumnClass(int columnIndex) {
	if (columnIndex == 0 || columnIndex == 3) {
		return String.class;
	}		
	if (columnIndex == 1) {
		return ServiceType.class;
	}
	if (columnIndex == 5) {
		return Boolean.class;
	}
	if (columnIndex == 2) {
		return Number.class;
	}
	if (columnIndex == 4) {
		return Date.class;
	}
	return Object.class;
}

public Object getValueAt(int row, int col) {
	ComparableObject status = getValueAt(row);
	Object[] values = status.toObjects();
	return values[col];
}

public Comparator<ComparableObject> getComparator(int col, boolean ascending) {
	return new ColumnComparator(col, ascending);
}
}
