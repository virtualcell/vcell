/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.vcell.util.Issue;
import org.vcell.util.Issue.Severity;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.sorttable.SortPreference;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;
import cbit.vcell.mapping.ReactionSpec.ReactionCombo;
import cbit.vcell.solver.OutputFunctionContext.OutputFunctionIssueSource;

@SuppressWarnings("serial")
public abstract class VCellSortTableModel<T> extends AbstractTableModel  implements SortTableModel, IssueEventListener {
	protected IssueManager issueManager;
	protected ScrollTable ownerTable = null;
	protected int currentPageIndex = 0;
	private int MAX_ROWS_PER_PAGE = 200;
	private List<T> allRows = Collections.synchronizedList(new ArrayList<T>());

	private static final String PROPERTY_NAME_SORT_PREFERENCE = "sortPreference";
	private transient java.beans.PropertyChangeSupport propertyChange;
	private SortPreference fieldSortPreference = new SortPreference(true, -1);
	private List<T> visibleRows = Collections.synchronizedList(new ArrayList<T>());
	private String columns[] = null;		
	protected static Logger lg = Logger.getLogger(VCellSortTableModel.class);

	public VCellSortTableModel() {
		this(null, null);
	}
	public VCellSortTableModel(ScrollTable table) {
		this(table, null);
	}
	public VCellSortTableModel(String[] cols) {
		this(null, cols);
	}
	public VCellSortTableModel(String[] cols,int MAX_ROWS_PER_PAGE) {
		this(null, cols);
		this.MAX_ROWS_PER_PAGE = MAX_ROWS_PER_PAGE;
	}	
	public VCellSortTableModel(ScrollTable table, String[] cols) {
		ownerTable = table;
		columns = cols;
	}
	
	protected int getMaxRowsPerPage(){
		return MAX_ROWS_PER_PAGE;
	}
	public void setMaxRowsPerPage(int newMax) {
		this.MAX_ROWS_PER_PAGE = newMax;
	}
	
	public int getColumnCount() {
		return columns.length;
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	public int getRowIndex(T rowObject) {
		for (int i = 0; i < visibleRows.size(); i ++) {
			if (visibleRows.get(i) == rowObject) {
				return i;
			}
		}
		return -1;
	}

	public T getValueAt(int row) {
		if (row >= 0 && row < visibleRows.size()) {
			return visibleRows.get(row);
		}
		return null;
	}
	
	public void removeRowsAt(int[] rows) {
		List<T> objToRemoveList = new ArrayList<T>();
		for (int row : rows) {
			T object = visibleRows.get(row);
			objToRemoveList.add(object);
		}
		boolean bModified = allRows.removeAll(objToRemoveList);
		if (bModified) {			
			if (currentPageIndex >= getNumPages()) {
				currentPageIndex = getNumPages() - 1;
			}
			updateVisibleRows();
		}
	}
	
	public void setColumns(String[] newValue) {
		columns = newValue;
		fireTableStructureChanged();
	}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

private java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}

public SortPreference getSortPreference() {
	return fieldSortPreference;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


public boolean isSortable(int col) {
	return true;
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


protected void resortColumn() {
	if (getSortPreference() != null && getSortPreference().getSortedColumnIndex() != -1 && getSortPreference().getSortedColumnIndex()<getColumnCount()) {
		Comparator <T> c = getComparator(getSortPreference().getSortedColumnIndex(), getSortPreference().isSortedColumnAscending());
		Collections.sort(allRows, c);
	}	
}

/**
 * Sets the sortPreference property (cbit.vcell.messaging.admin.sorttable.SortPreference) value.
 * @param sortPreference The new value for the property.
 * @see #getSortPreference
 */
public void setSortPreference(SortPreference sortPreference) {
	if (fieldSortPreference == sortPreference || fieldSortPreference.equals(sortPreference)) {
		return;
	}
	SortPreference oldValue = fieldSortPreference;
	fieldSortPreference = sortPreference;
	resortColumn();
	currentPageIndex = 0;
	updateVisibleRows();
	firePropertyChange(PROPERTY_NAME_SORT_PREFERENCE, oldValue, sortPreference);
}

protected abstract Comparator<T> getComparator(final int col, final boolean ascending);
	
	public void gotoFirstPage() {
		currentPageIndex = 0;
		updateVisibleRows();
	}

	public void gotoPreviousPage() {
		if (currentPageIndex > 0) {
			currentPageIndex --;
		}
		updateVisibleRows();
	}
	
	public void gotoNextPage() {
		if (currentPageIndex < getNumPages() - 1) {
			currentPageIndex ++;
		}
		updateVisibleRows();
	}	
	public void gotoLastPage() {
		currentPageIndex = getNumPages() - 1;
		updateVisibleRows();
	}
	public boolean hasNextPage() {
		return currentPageIndex < getNumPages() - 1;
	}
	public boolean hasPreviousPage() {
		return currentPageIndex > 0;
	}
	
	public String getPageDescription() {
		int pageOffset = currentPageIndex * MAX_ROWS_PER_PAGE;
		return (pageOffset + 1) + " - " + (pageOffset + getDataRowCount()) + " of " + allRows.size();
	}
	
	public int getNumPages() {
		int size = allRows.size();
		int numPages = size / MAX_ROWS_PER_PAGE;
		if (size % MAX_ROWS_PER_PAGE != 0) {
			numPages ++;
		}
		return Math.max(1, numPages);
	}
	
	public int getDataRowCount() {
		return Math.min(allRows.size() - currentPageIndex * MAX_ROWS_PER_PAGE, MAX_ROWS_PER_PAGE);
	}
	
	public int getRowCount() {
		return getDataRowCount();
	}
	
	public void issueChange(IssueEvent issueEvent) {		
		//fireTableDataChanged();
		ownerTable.repaint();
	}
	
	public void setData(List<? extends T> list) {
		Object[] selectedObjects = null;
		if (ownerTable != null) {
			int rows[] = ownerTable.getSelectedRows();
			if (rows.length > 0) {
				selectedObjects = new Object[rows.length];
				for (int i = 0; i < rows.length; i ++) {
					selectedObjects[i] = getValueAt(rows[i]);
				}
			}
		}
		allRows.clear();
		if (list != null) {
			allRows.addAll(list);
			resortColumn();
		}		
		if (currentPageIndex >= getNumPages()) {
			currentPageIndex = getNumPages() - 1;
		}
		updateVisibleRows();
		if (selectedObjects != null) {
			setTableSelections(selectedObjects, ownerTable);
		}
		
	}
	
	private void updateVisibleRows() {
		visibleRows.clear();	
		int pageOffset = currentPageIndex * MAX_ROWS_PER_PAGE;
		for (int i = 0; i < getDataRowCount(); i ++) {
			visibleRows.add(allRows.get(i + pageOffset));
		}
		fireTableDataChanged();	
	}

	@Override
	public List<Issue> getIssues(int row, int col, Severity severity) {
		List<Issue> iL = new ArrayList<Issue>();
		Object rowAt = getValueAt(row);
		if (rowAt != null && issueManager != null) {
			List<Issue> allIssueList = issueManager.getIssueList();
			for (Issue issue: allIssueList) {
				Object source = issue.getSource();
				if (issue.getSeverity() == severity) {
					if(source instanceof ReactionCombo) {
						if(((ReactionCombo)source).getReactionSpec() == rowAt) {
							iL.add(issue);
						}
					} else if (source instanceof OutputFunctionIssueSource) {
						if(((OutputFunctionIssueSource)source).getAnnotatedFunction() == rowAt) {
							iL.add(issue);
						}
//					} else if (source instanceof SpeciesPattern) {
//						if(rowAt instanceof ReactionRule && issue.getIssueContext().hasContextType(ContextType.ReactionRule)) {
//							ReactionRule thing = (ReactionRule)issue.getIssueContext().getContextObject(ContextType.ReactionRule);
//							if(thing == rowAt) {
//								iL.add(issue);
//							}
//						} else if (rowAt instanceof SpeciesContext && issue.getIssueContext().hasContextType(ContextType.SpeciesContext)) {
//							SpeciesContext thing = (SpeciesContext)issue.getIssueContext().getContextObject(ContextType.SpeciesContext);
//							if(thing == rowAt) {
//								iL.add(issue);
//							}
//						} else if (rowAt instanceof RbmObservable && issue.getIssueContext().hasContextType(ContextType.RbmObservable)) {
//							RbmObservable thing = (RbmObservable)issue.getIssueContext().getContextObject(ContextType.RbmObservable);
//							if(thing == rowAt) {
//								iL.add(issue);
//							}
//						}
					} else {
						if (rowAt == source) {
							iL.add(issue);
						}
					}
				}
			}
		}
		return iL;
	}
	
	public final void setIssueManager(IssueManager newValue) {
		if (newValue == issueManager) {
			return;
		}
		IssueManager oldValue = this.issueManager;
		if (oldValue != null) {
			oldValue.removeIssueEventListener(this);
		}
		this.issueManager = newValue;
		if (newValue != null) {
			newValue.addIssueEventListener(this);		
		}
		issueManagerChange(oldValue, newValue);
	}

	void issueManagerChange(IssueManager oldValue, IssueManager newValue) {}
	
	public void setTableSelections(Object[] selectedObjects, JTable table) {
		if (table != ownerTable || table.getModel() != this) {
			throw new RuntimeException("VCellSortTableModel.setTableSelections(), wrong table");
		}
		if (selectedObjects == null || selectedObjects.length == 0) {
			table.clearSelection();
			return;
		}
		int firstObjectRowIndex = -1;
		for (int i = 0; i < allRows.size(); i ++) {
			if (allRows.get(i) == selectedObjects[0]) {
				firstObjectRowIndex = i;
				break;
			}
		}
		if (firstObjectRowIndex < 0) {
			return;
		}
		int oldValue = currentPageIndex;
		currentPageIndex = firstObjectRowIndex / MAX_ROWS_PER_PAGE;
		if (oldValue != currentPageIndex) {
			updateVisibleRows();
		}
		
		Set<Integer> oldSelectionSet = new HashSet<Integer>();
		for (int row : table.getSelectedRows()) {
			oldSelectionSet.add(row);
		}
		Set<Integer> newSelectionSet = new HashSet<Integer>();
		for (Object object : selectedObjects) {
			for (int i = 0; i < getRowCount(); i ++) {
				if (getValueAt(i) == object) {
					newSelectionSet.add(i);
					break;
				}
			}
		}
		
		Set<Integer> removeSet = new HashSet<Integer>(oldSelectionSet);
		removeSet.removeAll(newSelectionSet);
		Set<Integer> addSet = new HashSet<Integer>(newSelectionSet);
		addSet.removeAll(oldSelectionSet);
		for (int row : removeSet) {
			table.removeRowSelectionInterval(row, row);
		}
		for (int row : addSet) {
			table.addRowSelectionInterval(row, row);
		}
		if (removeSet.size() > 0 || addSet.size() > 0) {
			Rectangle r = table.getCellRect(table.getSelectedRow(), 0, true);
			table.scrollRectToVisible(r);
		}
	}
}
