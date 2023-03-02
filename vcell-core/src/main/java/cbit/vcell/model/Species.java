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

import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import org.vcell.util.*;
import org.vcell.util.document.Identifiable;

import cbit.vcell.Historical;

@SuppressWarnings("serial")
public class Species implements VetoableChangeListener, Cacheable, Relatable, Identifiable, Displayable {

	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	private String fieldCommonName = null;
	private String fieldAnnotation = null;
	private DBSpecies fieldDBSpecies = null;

public Species(String commonName,String argAnnotation) throws IllegalArgumentException {
	this(commonName,argAnnotation,null);
}      


public Species(String commonName,String argAnnotation, DBSpecies argDBSpecies) throws IllegalArgumentException {
	this.fieldAnnotation = argAnnotation;
	this.fieldDBSpecies = argDBSpecies;
	
	addVetoableChangeListener(this);
	try {
		setCommonName(commonName);
	}catch (PropertyVetoException e){
		throw new IllegalArgumentException(e.getMessage(), e);
	}
}      

public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}

public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}

public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


public boolean compareEqual(Matchable obj) {
	if (obj instanceof Species){
		Species s = (Species)obj;

		if (!Compare.isEqualOrNull(getAnnotation(),s.getAnnotation())){
			return false;
		}
		if (!Compare.isEqual(getCommonName(),s.getCommonName())){
			return false;
		}
		if(!Compare.isEqualOrNull(getDBSpecies(),s.getDBSpecies())){
			return false;
		}
		return true;
	}else{
		return false;
	}
}

	@Override
	public boolean relate(Relatable obj, RelationVisitor rv) {
		if (!(obj instanceof Species)) {
			return false;
		}
		Species s = (Species)obj;

		if (!rv.relateOrNull(getAnnotation(),s.getAnnotation())){
			return false;
		}
		if (!rv.relate(getCommonName(),s.getCommonName())){
			return false;
		}
		if(!rv.relateOrNull(getDBSpecies(),s.getDBSpecies())){
			return false;
		}
		return true;
	}


public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}

public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}

public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

@Historical
public String getAnnotation() {
	return fieldAnnotation;
}


public java.lang.String getCommonName() {
	return fieldCommonName;
}


public DBSpecies getDBSpecies() {
	return fieldDBSpecies;
}


protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	}
	return propertyChange;
}


protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	}
	return vetoPropertyChange;
}


public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


public void refreshDependencies() {
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
}

public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}

public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}

public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}

public void setCommonName(java.lang.String commonName) throws java.beans.PropertyVetoException {
	String oldValue = fieldCommonName;
	fireVetoableChange("commonName", oldValue, commonName);
	fieldCommonName = commonName;
	firePropertyChange("commonName", oldValue, commonName);
}

public void setDBSpecies(cbit.vcell.model.DBSpecies argDBSpecies) throws java.beans.PropertyVetoException {
	cbit.vcell.model.DBSpecies oldValue = fieldDBSpecies;
	fireVetoableChange("dbFormalSpecies", oldValue, argDBSpecies);
	fieldDBSpecies = argDBSpecies;
	firePropertyChange("dbFormalSpecies", oldValue, argDBSpecies);
}

@Override
public String toString() {
	return "Species@"+Integer.toHexString(hashCode())+"("+"'commonName='"+getCommonName()+"')";
}


public void vetoableChange(java.beans.PropertyChangeEvent e) throws java.beans.PropertyVetoException {
	if (e.getSource()==this){
		if (e.getPropertyName().equals("commonName")){
			String newName = (String)e.getNewValue();
			if (newName == null){
				throw new PropertyVetoException("species name is null",e);
			}
			if (newName.length()<1){
				throw new PropertyVetoException("species name is zero length",e);
			}
//			if (!Character.isJavaIdentifierStart(newName.charAt(0))){
//				throw new PropertyVetoException("species name '"+newName+"' can't start with a '"+newName.charAt(0)+"'",e);
//			}
//			for (int i=1;i<newName.length();i++){
//				if (!Character.isJavaIdentifierPart(newName.charAt(i))){
//					throw new PropertyVetoException("species name '"+newName+"' can't include a '"+newName.charAt(i)+"'",e);
//				}
//			}	
		}
	}
}

	// Not really displayable but at times we need the display name and the type name
	public static final String typeName = "Species"; 
	@Override
	public String getDisplayName() {
		return getCommonName();
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}

}
