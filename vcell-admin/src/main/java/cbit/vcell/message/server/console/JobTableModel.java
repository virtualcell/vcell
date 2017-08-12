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

/**
 * Insert the type's description here.
 * Creation date: (8/28/2003 1:38:32 PM)
 * @author: Fei Gao
 */
public class JobTableModel extends VCellSortTableModel<ComparableObject> {
	public final static int columnIndex_ModelName = 0;
	public final static int columnIndex_UserID = 1;
	public final static int columnIndex_SimID = 2;
	private final static int columnIndex_JobIndex = 3;
	private final static int columnIndex_ScanCount = 4;
	private final static int columnIndex_SolverType = 5;
	private final static int columnIndex_Status = 6;
	private final static int columnIndex_ComputeHost = 7;
	private final static int columnIndex_ServerID = 8;
	private final static int columnIndex_TaskID = 9;
	private final static int columnIndex_SubmitDate = 10;
	private final static int columnIndex_StartDate = 11;
	private final static int columnIndex_EndDate = 12;
	private final static int columnIndex_ElapsedTime = 13;
	private final static int columnIndex_MeshSize = 14;

/**
 * JobTableModel constructor comment.
 */
public JobTableModel() {
	super(new String[]{"Model Name", "User ID", "Sim ID", "Job Index", "Scan Count", "Solver Type", "Status", "Compute Host", "Server ID", "Task ID", "Submit Date", "Start Date", "End Date", "Elapsed (h:m:s)", "Mesh Size"});
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 2:14:05 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	if (columnIndex == columnIndex_SimID || columnIndex == columnIndex_TaskID || columnIndex == columnIndex_JobIndex || columnIndex == columnIndex_ScanCount) {
		return Number.class;
	} else if (columnIndex == columnIndex_SubmitDate || columnIndex == columnIndex_StartDate || columnIndex == columnIndex_EndDate) {
		return Date.class;
	} else if (columnIndex == columnIndex_ElapsedTime){
		return Long.class;
	} else if (columnIndex == columnIndex_MeshSize){
		return Number.class;
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
