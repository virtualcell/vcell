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

import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;

import cbit.vcell.client.constants.GuiConstants;

/**
 * Insert the type's description here.
 * Creation date: (11/28/2005 5:48:28 PM)
 * @author: Jim Schaff
 */
public class ReferenceDataMappingSpec implements java.io.Serializable, org.vcell.util.Matchable, IssueSource {
	private java.lang.String referenceDataColumnName = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.parser.SymbolTableEntry fieldModelObject = null;

/**
 * ReferenceDataMappingSpec constructor comment.
 */
public ReferenceDataMappingSpec(ReferenceDataMappingSpec referenceDataMappingSpecToCopy) {
	super();
	this.referenceDataColumnName = referenceDataMappingSpecToCopy.referenceDataColumnName;
	this.fieldModelObject = referenceDataMappingSpecToCopy.fieldModelObject;
}


/**
 * ReferenceDataMappingSpec constructor comment.
 */
public ReferenceDataMappingSpec(String argRefDataColumnName) {
	super();
	this.referenceDataColumnName = argRefDataColumnName;
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof ReferenceDataMappingSpec){
		ReferenceDataMappingSpec rdms = (ReferenceDataMappingSpec)obj;

		if (!org.vcell.util.Compare.isEqual(referenceDataColumnName,rdms.referenceDataColumnName)){
			return false;
		}

		if ((fieldModelObject == null && rdms.fieldModelObject != null) ||
			(fieldModelObject != null && rdms.fieldModelObject == null)){
			return false;
		}

		//
		// if both are null, everything is ok
		// if both are non-null, then check for equivalence.
		//
		if (fieldModelObject != null && rdms.fieldModelObject != null){
			if (fieldModelObject instanceof org.vcell.util.Matchable){
				if (rdms.fieldModelObject instanceof org.vcell.util.Matchable){
					if (!org.vcell.util.Compare.isEqualOrNull((org.vcell.util.Matchable)this.fieldModelObject,(org.vcell.util.Matchable)rdms.fieldModelObject)){
						return false;
					}
				}else{
					return false;
				}
			}else{
				//
				// else compare symbol type, name, and nameScope name
				//				
				if (!fieldModelObject.getClass().equals(rdms.fieldModelObject.getClass())){
					return false;
				}
				if (!fieldModelObject.getName().equals(rdms.fieldModelObject.getName())){
					return false;
				}
				if (!fieldModelObject.getNameScope().getName().equals(rdms.fieldModelObject.getNameScope().getName())){
					return false;
				}
			}
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
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Gets the modelObject property (cbit.vcell.parser.SymbolTableEntry) value.
 * @return The modelObject property value.
 * @see #setModelObject
 */
public cbit.vcell.parser.SymbolTableEntry getModelObject() {
	return fieldModelObject;
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
 * Insert the method's description here.
 * Creation date: (11/28/2005 5:51:01 PM)
 * @return java.lang.String
 */
public java.lang.String getReferenceDataColumnName() {
	return referenceDataColumnName;
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
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * Sets the modelObject property (cbit.vcell.parser.SymbolTableEntry) value.
 * @param modelObject The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getModelObject
 */
public void setModelObject(cbit.vcell.parser.SymbolTableEntry modelObject) throws java.beans.PropertyVetoException {
	cbit.vcell.parser.SymbolTableEntry oldValue = fieldModelObject;
	fireVetoableChange("modelObject", oldValue, modelObject);
	fieldModelObject = modelObject;
	firePropertyChange("modelObject", oldValue, modelObject);
}

public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	if (getModelObject() == null) {
		issueList.add(new Issue(this,issueContext,IssueCategory.ParameterEstimationRefereceDataNotMapped,"There is unmapped experimental data column." +
				"Go to '" + GuiConstants.PARAMETER_ESTIMATION_TAB_EXPDATAMAPPING + "' tab and do the mapping.",Issue.SEVERITY_ERROR));
	}
}
}
