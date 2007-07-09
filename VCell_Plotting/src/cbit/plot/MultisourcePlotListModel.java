/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.plot;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:07:05 PM)
 * @author: Jim Schaff
 */
public class MultisourcePlotListModel extends org.vcell.util.gui.DefaultListModelCivilized implements java.beans.PropertyChangeListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.plot.DataSource[] fieldDataSources = null;

/**
 * MultisourcePlotListModel constructor comment.
 */
public MultisourcePlotListModel() {
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
 * Gets the dataSources property (cbit.vcell.modelopt.gui.DataSource[]) value.
 * @return The dataSources property value.
 * @see #setDataSources
 */
public cbit.plot.DataSource[] getDataSources() {
	return fieldDataSources;
}


/**
 * Gets the dataSources index property (cbit.vcell.modelopt.gui.DataSource) value.
 * @return The dataSources property value.
 * @param index The index value into the property array.
 * @see #setDataSources
 */
public DataSource getDataSources(int index) {
	return getDataSources()[index];
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
	if (evt.getSource() == this && evt.getPropertyName().equals("dataSources")){
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
	java.util.Vector<DataReference> dataReferenceList = new java.util.Vector<DataReference>();
	for (int i = 0; getDataSources()!=null && i < getDataSources().length; i++){
		DataSource dataSource = getDataSources(i);
		String[] columnNames = dataSource.getColumnNames();
		for (int j = 0; j < columnNames.length; j++){
			if (columnNames[j].equals("t")){
				continue;
			}
			dataReferenceList.add(new DataReference(dataSource,columnNames[j]));
		}
	}
	setContents(org.vcell.util.BeanUtils.getArray(dataReferenceList,DataReference.class));
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
 * Sets the dataSources property (cbit.vcell.modelopt.gui.DataSource[]) value.
 * @param dataSources The new value for the property.
 * @see #getDataSources
 */
public void setDataSources(cbit.plot.DataSource[] dataSources) {
	cbit.plot.DataSource[] oldValue = fieldDataSources;
	fieldDataSources = dataSources;
	firePropertyChange("dataSources", oldValue, dataSources);
}


/**
 * Sets the dataSources index property (cbit.vcell.modelopt.gui.DataSource[]) value.
 * @param index The index value into the property array.
 * @param dataSources The new value for the property.
 * @see #getDataSources
 */
public void setDataSources(int index, DataSource dataSources) {
	DataSource oldValue = fieldDataSources[index];
	fieldDataSources[index] = dataSources;
	if (oldValue != null && !oldValue.equals(dataSources)) {
		firePropertyChange("dataSources", null, fieldDataSources);
	};
}
}