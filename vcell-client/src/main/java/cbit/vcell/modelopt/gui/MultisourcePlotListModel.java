/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.vcell.util.gui.DefaultListModelCivilized;

import cbit.vcell.modelopt.DataReference;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;

/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:07:05 PM)
 * @author: Jim Schaff
 */
public class MultisourcePlotListModel extends DefaultListModelCivilized implements java.beans.PropertyChangeListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private DataSource[] fieldDataSources = null;

/**
 * MultisourcePlotListModel constructor comment.
 */
public MultisourcePlotListModel() {
	super();
	addPropertyChangeListener(this);
}


@Override
public Object getElementAt(int index) {
	// TODO Auto-generated method stub
	return super.getElementAt(index);
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
public DataSource[] getDataSources() {
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
		fireIntervalAdded(this,0,(getSize() == 0?0:getSize()-1));
	}
}

private ArrayList<SortDataReferenceHelper> sortDataReferenceHelpers;
Comparator<SortDataReferenceHelper> comparatorDataSource;
public void setSort(Comparator<SortDataReferenceHelper> comparatorDataSource){
	this.comparatorDataSource = comparatorDataSource;
	firePropertyChange("dataSources", null, fieldDataSources);
}
public ArrayList<SortDataReferenceHelper> getSortedDataReferences(){
	return sortDataReferenceHelpers;
}

public static class SortDataReferenceHelper{
	public int unsortedIndex;
	public DataReference dataReference;
	private ReferenceDataMappingSpec referenceDataMappingSpec;
	public Integer matchCount = null;
	public SortDataReferenceHelper(int unsortedIndex,DataReference dataReference) {
		this.unsortedIndex = unsortedIndex;
		this.dataReference = dataReference;
	}
	public void setReferenceDataMappingSpec(ReferenceDataMappingSpec referenceDataMappingSpec){
		if(this.referenceDataMappingSpec != null && this.referenceDataMappingSpec != referenceDataMappingSpec){
			throw new RuntimeException("Unexpected this.referenceDataMappingSpec change");
		}
		this.referenceDataMappingSpec = referenceDataMappingSpec;
	}
	public ReferenceDataMappingSpec getReferenceDataMappingSpec(){
		return referenceDataMappingSpec;
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
	sortDataReferenceHelpers = new ArrayList<SortDataReferenceHelper>();
	for (int i = 0; getDataSources()!=null && i < getDataSources().length; i++){
		DataSource dataSource = getDataSources(i);
		String[] columnNames = dataSource.getColumnNames();
		int timeIndex = dataSource.getTimeColumnIndex();
		
		for (int j = 0; j < columnNames.length; j++){
			if (j == timeIndex){
				continue;
			}
			sortDataReferenceHelpers.add(new SortDataReferenceHelper(sortDataReferenceHelpers.size(),new DataReference(dataSource, columnNames[j])));
//			System.out.println("unsort="+(sortDataReferenceHelpers.size()-1)+" i="+i+" j="+j+" columnames[j]="+columnNames[j]);
		}
	}
	if(sortDataReferenceHelpers.size() > 0){
		if(comparatorDataSource != null){
			Collections.sort(sortDataReferenceHelpers,comparatorDataSource);
		}else{//default sort
			Collections.sort(sortDataReferenceHelpers,new Comparator<SortDataReferenceHelper>() {
				@Override
				public int compare(SortDataReferenceHelper o1, SortDataReferenceHelper o2) {
					int idCompare = o1.dataReference.getIdentifier().compareToIgnoreCase(o2.dataReference.getIdentifier());
					if(idCompare == 0){
						return o1.dataReference.getDataSource().getName().compareToIgnoreCase(o2.dataReference.getDataSource().getName());
					}
					return idCompare;
				}
			});
		}
	}
	if(sortDataReferenceHelpers.size() > 0){
		DataReference[] dataReferences = new DataReference[sortDataReferenceHelpers.size()];
		for (int i = 0; i < dataReferences.length; i++) {
			dataReferences[i] = sortDataReferenceHelpers.get(i).dataReference;
		}
		setContents(dataReferences);		
	}else{
		setContents(null);
	}
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
public void setDataSources(final DataSource[] dataSources) {
//	if(dataSources != null && dataSources.length > 0){
//		sortIndexes = new Integer[dataSources.length];
//		for (int i = 0; i < dataSources.length; i++) {
//			sortIndexes[i] = i;
//		}
//		Arrays.sort(sortIndexes, new Comparator<Integer>() {
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				dataSources[o1].
//				return ;
//			}
//		});
//	}else{
//		sortIndexes = null;
//	}
	DataSource[] oldValue = fieldDataSources;
	fieldDataSources = dataSources;
	firePropertyChange("dataSources", oldValue, dataSources);
}

//
///**
// * Sets the dataSources index property (cbit.vcell.modelopt.gui.DataSource[]) value.
// * @param index The index value into the property array.
// * @param dataSources The new value for the property.
// * @see #getDataSources
// */
//public void setDataSources(int index, DataSource dataSources) {
//	DataSource oldValue = fieldDataSources[index];
//	fieldDataSources[index] = dataSources;
//	if (oldValue != null && !oldValue.equals(dataSources)) {
//		firePropertyChange("dataSources", null, fieldDataSources);
//	};
//}
}
