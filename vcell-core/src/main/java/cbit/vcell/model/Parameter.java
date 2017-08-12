/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.io.Serializable;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Matchable;
import org.vcell.util.PropertyChangeListenerProxyVCell;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public abstract class Parameter implements EditableSymbolTableEntry, Serializable, Matchable
{
	public static final String PROPERTYNAME_NAME = "name";
	public static final String PROPERTYNAME_EXPRESSION = "expression";
	//private NameScope nameScope = null;
	//private java.lang.String fieldName = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	//private cbit.vcell.parser.Expression fieldExpression = null;
	private java.lang.String fieldDescription = new String();
/**
 * This method was created by a SmartGuide.
 * @param feature cbit.vcell.model.Feature
 * @param name java.lang.String
 */
protected Parameter () {
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.addProxyListener(getPropertyChange(), listener);
}
/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(String propertyName, java.beans.VetoableChangeListener listener) {
	if (!propertyName.equals(PROPERTYNAME_NAME)){
		throw new RuntimeException("only "+PROPERTYNAME_NAME+" property is vetoable for parameter");
	}
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param parm cbit.vcell.model.Parameter
 */
protected boolean compareEqual0(Parameter parm) {
	if (!Compare.isEqual(getName(),parm.getName())){
		return false;
	}
	if (!Compare.isEqualOrNull(getExpression(),parm.getExpression())){
		return false;
	}
	if (!Compare.isEqual(getUnitDefinition(),parm.getUnitDefinition())){
		return false;
	}
	if (!Compare.isEqual(getNameScope().getName(),parm.getNameScope().getName())){
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
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
public abstract double getConstantValue() throws ExpressionException;
/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public java.lang.String getDescription() {
	return fieldDescription;
}
/**
 * may return null if implementing class can't provide appropriate default value  
 * @return cbit.vcell.parser.Expression 
 */
public abstract Expression getExpression();
   public abstract String getName();
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 2:05:08 PM)
 * @return cbit.vcell.parser.NameScope
 */
public abstract NameScope getNameScope();
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
 * Insert the method's description here.
 * Creation date: (3/31/2004 12:10:47 PM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
public abstract cbit.vcell.units.VCUnitDefinition getUnitDefinition();
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
 * This method was created by a SmartGuide.
 * @return boolean
 */
@Override
public boolean isConstant() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 11:04:02 AM)
 * @return boolean
 */
public abstract boolean isExpressionEditable();
/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 11:04:14 AM)
 * @return boolean
 */
public abstract boolean isNameEditable();
/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 11:04:14 AM)
 * @return boolean
 */
public abstract boolean isUnitEditable();

public boolean isDescriptionEditable() {
	return false;
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.removeProxyListener(getPropertyChange(), listener);
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}
/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) {
	String oldValue = fieldDescription;
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 3:36:39 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName()+"@"+Integer.toHexString(hashCode())+": <"+getName()+"> = '"+getExpression()+"' ["+getUnitDefinition()+"], \""+getDescription()+"\"";
}

public String getNullExpressionDescription() {
	return null;
}

public void gatherIssues(List<Issue> issueList) { }

/**
 * get default expression, if available
 * @return default expression or null 
 */
public Expression getDefaultExpression( ) { 
	return null;
}
}