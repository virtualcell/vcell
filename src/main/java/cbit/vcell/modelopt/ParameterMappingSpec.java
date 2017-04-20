/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt;

import org.vcell.util.Matchable;

import cbit.vcell.model.Parameter;
import cbit.vcell.parser.ExpressionException;

/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 9:35:15 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ParameterMappingSpec implements java.io.Serializable, Matchable {
	private Parameter modelParameter = null;
	private double low = Double.NEGATIVE_INFINITY;
	private double high = Double.POSITIVE_INFINITY;
	private double current; // must be set in constructor (based on parameter value).
	private boolean selected = false;
	protected transient java.beans.PropertyChangeSupport propertyChange;

/**
 * ParameterMapping constructor comment.
 */
public ParameterMappingSpec(Parameter argModelParameter) throws ExpressionException {
	super();
	this.modelParameter = argModelParameter;
	this.current = argModelParameter.getExpression().evaluateConstant();
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof ParameterMappingSpec){
		ParameterMappingSpec pms = (ParameterMappingSpec)obj;

		if (!org.vcell.util.Compare.isEqual(modelParameter,pms.modelParameter)){
			return false;
		}
		
		if (high != pms.high){
			return false;
		}

		if (low != pms.low){
			return false;
		}

		//if (current != pms.current){
			//return false;
		//}

		if (selected != pms.selected){
			return false;
		}

		return true;
	}
	return false;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 11:04:18 AM)
 * @return double
 */
public double getCurrent() {
	return current;
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 11:04:18 AM)
 * @return double
 */
public double getHigh() {
	return high;
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 11:04:18 AM)
 * @return double
 */
public double getLow() {
	return low;
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 9:37:50 AM)
 * @return cbit.vcell.model.Parameter
 */
public Parameter getModelParameter() {
	return modelParameter;
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
 * Insert the method's description here.
 * Creation date: (8/22/2005 10:54:33 AM)
 * @return boolean
 */
public boolean isSelected() {
	return selected;
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 11:04:18 AM)
 * @param newCurrent double
 */
public void setCurrent(double newCurrent) {
	double oldValue = current;
	current = newCurrent;
	firePropertyChange("current", new Double(oldValue), new Double(newCurrent));
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 11:04:18 AM)
 * @param newHigh double
 */
public void setHigh(double newHigh) {
	double oldValue = high;
	high = newHigh;
	firePropertyChange("high", new Double(oldValue), new Double(newHigh));
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 11:04:18 AM)
 * @param newLow double
 */
public void setLow(double newLow) {
	double oldValue = low;
	low = newLow;
	firePropertyChange("low", new Double(oldValue), new Double(newLow));
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 10:54:33 AM)
 * @param newSelected boolean
 */
public void setSelected(boolean newSelected) {
	boolean oldValue = selected;
	selected = newSelected;
	firePropertyChange("selected", oldValue, newSelected);
}

@Override
public String toString() {
	return getModelParameter().getName() + " " + getModelParameter().getNameScope().getName() + " " + isSelected();
}
}
