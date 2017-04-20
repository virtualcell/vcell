/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest;

import cbit.vcell.parser.*;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2003 10:58:02 AM)
 * @author: Anuradha Lakshminarayana
 */
public class SolutionTemplate {
	private java.lang.String fieldVarName = null;
	private java.lang.String fieldDomainName = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.parser.Expression fieldTemplateExpression = null;
	private cbit.vcell.math.Constant[] fieldConstants = null;
/**
 * SolutionTemplate constructor comment.
 */
public SolutionTemplate(String varName, String domainName, Expression templateExpression) {
	super();
	this.fieldVarName = varName;
	this.fieldDomainName = domainName;
	this.fieldTemplateExpression = templateExpression;
	// String symbols[] = templateExpression.getSymbols();
	if (templateExpression != null) {
		String symbols[] = templateExpression.getSymbols();
		fieldConstants = new cbit.vcell.math.Constant[symbols.length];
		for (int i = 0; i < symbols.length; i++){
			fieldConstants[i] = new cbit.vcell.math.Constant(symbols[i],new Expression(1.0));
		}
	} 
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
 * Gets the constants property (cbit.vcell.math.Constant[]) value.
 * @return The constants property value.
 * @see #setConstants
 */
public cbit.vcell.math.Constant[] getConstants() {
	return fieldConstants;
}
/**
 * Gets the domainName property (java.lang.String) value.
 * @return The domainName property value.
 * @see #setDomainName
 */
public java.lang.String getDomainName() {
	return fieldDomainName;
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
 * Gets the templateExpression property (cbit.vcell.parser.Expression) value.
 * @return The templateExpression property value.
 * @see #setTemplateExpression
 */
public cbit.vcell.parser.Expression getTemplateExpression() {
	return fieldTemplateExpression;
}
/**
 * Gets the varName property (java.lang.String) value.
 * @return The varName property value.
 * @see #setVarName
 */
public java.lang.String getVarName() {
	return fieldVarName;
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
 * Sets the constants property (cbit.vcell.math.Constant[]) value.
 * @param constants The new value for the property.
 * @see #getConstants
 */
private void setConstants(cbit.vcell.math.Constant[] constants) {
	cbit.vcell.math.Constant[] oldValue = fieldConstants;
	fieldConstants = constants;
	firePropertyChange("constants", oldValue, constants);
}
/**
 * Sets the templateExpression property (cbit.vcell.parser.Expression) value.
 * @param templateExpression The new value for the property.
 * @see #getTemplateExpression
 */
public void setTemplateExpression(cbit.vcell.parser.Expression templateExpression) {
	cbit.vcell.parser.Expression oldValue = fieldTemplateExpression;
	fieldTemplateExpression = templateExpression;
	firePropertyChange("templateExpression", oldValue, templateExpression);
}
}
