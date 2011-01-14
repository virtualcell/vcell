package org.vcell.util.gui.sorttable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public abstract class DefaultSortTableModel<T>  extends AbstractTableModel  implements SortTableModel {
	private static final String PROPERTY_NAME_SORT_PREFERENCE = "sortPreference";
	private transient java.beans.PropertyChangeSupport propertyChange;
	private SortPreference fieldSortPreference = new SortPreference(true, -1);
	private List<T> rows = Collections.synchronizedList(new ArrayList<T>());
	private String columns[] = null;		

	public DefaultSortTableModel() {}
	  
	public DefaultSortTableModel(String[] cols) {
		super();
		columns = cols;
	}

	/**
	 * getColumnCount method comment.
	 */
	public int getColumnCount() {
		return columns.length;
	}


	public String getColumnName(int column) {
		return columns[column];
	}

	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() {
		return getDataSize();
	}

	public int getRowIndex(T rowObject) {
		for (int i = 0; i < rows.size(); i ++) {
			if (rows.get(i) == rowObject) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * getValueAt method comment.
	 */
	public T getValueAt(int row) {
		return rows.get(row);
	}
	
	public void removeValueAt(int row) {
		rows.remove(row);
		fireTableDataChanged();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/19/2003 10:50:18 AM)
	 * @param list java.util.List
	 */
	public void setData(List<? extends T> list) {
		rows.clear();	
		if (list != null) {
			rows.addAll(list);
			resortColumn();		
		}		
		fireTableDataChanged();
	}

	public int getDataSize() {
		return rows.size();
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

/**
 * Accessor for the propertyChange field.
 */
private java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the sortPreference property (cbit.vcell.messaging.admin.sorttable.SortPreference) value.
 * @return The sortPreference property value.
 * @see #setSortPreference
 */
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
	if (getSortPreference() != null && getSortPreference().getSortedColumnIndex() != -1) {
		Collections.sort(rows, getComparator(getSortPreference().getSortedColumnIndex(), getSortPreference().isSortedColumnAscending()));
	}	
}

/**
 * Sets the sortPreference property (cbit.vcell.messaging.admin.sorttable.SortPreference) value.
 * @param sortPreference The new value for the property.
 * @see #getSortPreference
 */
public void setSortPreference(SortPreference sortPreference) {
	SortPreference oldValue = fieldSortPreference;
	fieldSortPreference = sortPreference;
	resortColumn();
	fireTableDataChanged();
	firePropertyChange(PROPERTY_NAME_SORT_PREFERENCE, oldValue, sortPreference);
}

protected abstract Comparator<T> getComparator(int col, boolean ascending);
}