/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui.sorttable;

import java.util.List;

import org.vcell.util.Issue;

/**
 * Insert the type's description here.
 * Creation date: (9/11/2003 10:50:03 AM)
 * @author: Fei Gao
 */
public interface SortTableModel extends javax.swing.table.TableModel {
	public void setSortPreference(SortPreference sortPreference);
	public SortPreference getSortPreference();
	public boolean isSortable(int col);
	public List<Issue> getIssues(int row, int col, Issue.Severity severity);
}
