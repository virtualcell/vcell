package org.vcell.util.gui.sorttable;
import java.util.*;
import javax.swing.table.*;

public abstract class DefaultSortTableModel  extends AbstractTableModel  implements SortTableModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	
	private SortPreference fieldSortPreference = new SortPreference(true, -1);

  public DefaultSortTableModel() {}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
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


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


public void resortColumn() {
	if (getSortPreference() != null && getSortPreference().getSortedColumnIndex() != -1) {
		sortColumn(getSortPreference().getSortedColumnIndex(), getSortPreference().isSortedColumnAscending());
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
	firePropertyChange("sortPreference", oldValue, sortPreference);
}


public abstract void sortColumn(int col, boolean ascending);
}