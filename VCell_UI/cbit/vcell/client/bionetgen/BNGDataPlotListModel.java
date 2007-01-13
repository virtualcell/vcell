package cbit.vcell.client.bionetgen;

import cbit.plot.DataReference;
import cbit.plot.DataSource;

/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:07:05 PM)
 * @author: Jim Schaff
 */
public class BNGDataPlotListModel extends cbit.gui.DefaultListModelCivilized implements java.beans.PropertyChangeListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private DataSource fieldDataSource = null;

/**
 * MultisourcePlotListModel constructor comment.
 */
public BNGDataPlotListModel() {
	super();
	addPropertyChangeListener(this);
}


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
 * Gets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @return The dataSource property value.
 * @see #setDataSource
 */
public DataSource getDataSource() {
	return fieldDataSource;
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
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("dataSource")){
		refreshAll();
		fireIntervalAdded(this,0,getSize()-1);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:18:09 PM)
 */
private void refreshAll() {
	//
	// get list of objects (data names)
	//
	DataSource dataSource = getDataSource();

	if (dataSource == null) {
		return;
	}
	int numColumns = dataSource.getColumnCount();
	DataReference[] dataReference = new DataReference[numColumns-1];
	int i = 0;
	String[] colNames = dataSource.getColumnNames();
	for (int j = 0; j < numColumns; j++){
		if (colNames[j].equals("t")){
			continue;
		}
		dataReference[i++] = new DataReference(getDataSource(),colNames[j]);
	}
	setContents(dataReference);

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


/**
 * Sets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @param dataSource The new value for the property.
 * @see #getDataSource
 */
public void setDataSource(DataSource dataSource) {
	DataSource oldValue = fieldDataSource;
	fieldDataSource = dataSource;
	firePropertyChange("dataSource", oldValue, dataSource);
}
}