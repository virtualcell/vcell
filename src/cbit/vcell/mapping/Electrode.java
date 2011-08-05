/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 11:10:05 AM)
 * @author: Anuradha Lakshminarayana
 */
public class Electrode implements java.io.Serializable, org.vcell.util.Matchable {
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.model.Feature fieldFeature = null;
	private org.vcell.util.Coordinate fieldPosition = null;
/**
 * Electrode constructor comment.
 */
public Electrode(Electrode electrode) {
	fieldFeature = electrode.fieldFeature;
	fieldPosition = electrode.fieldPosition;
}
/**
 * Electrode constructor comment.
 */
public Electrode(cbit.vcell.model.Feature argFeature, org.vcell.util.Coordinate argPosition) {
	fieldFeature = argFeature;
	fieldPosition = argPosition;
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
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}
/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	Electrode electrode = null;
	if (!(obj instanceof Electrode)){
		return false;
	}else{
		electrode = (Electrode)obj;
	}

	if (!org.vcell.util.Compare.isEqualOrNull(getFeature(),electrode.getFeature())){
		return false;
	}
	if (!org.vcell.util.Compare.isEqualOrNull(getPosition(),electrode.getPosition())){
		return false;
	}
	return true;
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
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * Gets the feature property (cbit.vcell.model.Feature) value.
 * @return The feature property value.
 * @see #setFeature
 */
public cbit.vcell.model.Feature getFeature() {
	return fieldFeature;
}
/**
 * Gets the position property (cbit.vcell.geometry.Coordinate) value.
 * @return The position property value.
 * @see #setPosition
 */
public org.vcell.util.Coordinate getPosition() {
	return fieldPosition;
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
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
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
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}
/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}
/**
 * Sets the feature property (cbit.vcell.model.Feature) value.
 * @param feature The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getFeature
 */
public void setFeature(cbit.vcell.model.Feature feature) throws java.beans.PropertyVetoException {
	if (feature==null){
		return;
	}
	cbit.vcell.model.Feature oldValue = fieldFeature;
	fireVetoableChange("feature", oldValue, feature);
	fieldFeature = feature;
	firePropertyChange("feature", oldValue, feature);
}
/**
 * Sets the position property (cbit.vcell.geometry.Coordinate) value.
 * @param position The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getPosition
 */
public void setPosition(org.vcell.util.Coordinate position) throws java.beans.PropertyVetoException {
	org.vcell.util.Coordinate oldValue = fieldPosition;
	fireVetoableChange("position", oldValue, position);
	fieldPosition = position;
	firePropertyChange("position", oldValue, position);
}
}
