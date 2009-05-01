package cbit.vcell.model;
import java.beans.*;

import org.jdom.Element;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.DbDictionary;
import cbit.vcell.xml.MIRIAMAnnotatable;
import cbit.vcell.xml.MIRIAMAnnotation;

public class Species
	implements
		java.beans.VetoableChangeListener,org.vcell.util.Cacheable,MIRIAMAnnotatable {

	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private java.lang.String fieldCommonName = null;
	private String fieldAnnotation = null;
	private cbit.vcell.dictionary.DBSpecies fieldDBSpecies = null;
	private MIRIAMAnnotation miriamAnnotation = null;

public Species(String commonName,String argAnnotation) throws IllegalArgumentException {
	this(commonName,argAnnotation,null);
}      


public Species(String commonName,String argAnnotation, cbit.vcell.dictionary.DBSpecies argDBSpecies) throws IllegalArgumentException {
	this.fieldAnnotation = argAnnotation;
	this.fieldDBSpecies = argDBSpecies;
	
	addVetoableChangeListener(this);
	try {
		setCommonName(commonName);
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new IllegalArgumentException(e.getMessage());
	}
}      

public MIRIAMAnnotation getMIRIAMAnnotation() {
	return miriamAnnotation;
}
public void setMIRIAMAnnotation(MIRIAMAnnotation miriamAnnotation) {
	this.miriamAnnotation = miriamAnnotation;
	
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
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
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
		if(!Compare.isEqualOrNull(getMIRIAMAnnotation(), s.getMIRIAMAnnotation())){
			return false;
		}
		return true;
	}else{
		return false;
	}
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
 * Gets the speciesReference property (cbit.vcell.dictionary.SpeciesReference) value.
 * @return The speciesReference property value.
 * @see #setSpeciesReference
 */
public String getAnnotation() {
	return fieldAnnotation;
}


/**
 * Gets the commonName property (java.lang.String) value.
 * @return The commonName property value.
 * @see #setCommonName
 */
public java.lang.String getCommonName() {
	return fieldCommonName;
}


/**
 * Gets the speciesReference property (cbit.vcell.dictionary.SpeciesReference) value.
 * @return The speciesReference property value.
 * @see #setSpeciesReference
 */
public cbit.vcell.dictionary.DBSpecies getDBSpecies() {
	return fieldDBSpecies;
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
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:16:46 PM)
 */
public void refreshDependencies() {
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
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
 * Sets the commonName property (java.lang.String) value.
 * @param commonName The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getCommonName
 */
public void setAnnotation(java.lang.String annotation) /*throws java.beans.PropertyVetoException*/{
	//if(annotation != null && annotation.length() == 0){
		//throw new java.beans.PropertyVetoException("Zero length string should be null",new java.beans.PropertyChangeEvent(this,"annotation",this.fieldAnnotation,annotation));
	//}
	String oldValue = fieldAnnotation;
	fieldAnnotation = annotation;
	firePropertyChange("annotation", oldValue, annotation);
}


/**
 * Sets the commonName property (java.lang.String) value.
 * @param commonName The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getCommonName
 */
public void setCommonName(java.lang.String commonName) throws java.beans.PropertyVetoException {
	String oldValue = fieldCommonName;
	fireVetoableChange("commonName", oldValue, commonName);
	fieldCommonName = commonName;
	firePropertyChange("commonName", oldValue, commonName);
}


/**
 * Sets the commonName property (java.lang.String) value.
 * @param commonName The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getCommonName
 */
public void setDBSpecies(cbit.vcell.dictionary.DBSpecies argDBSpecies) throws java.beans.PropertyVetoException {
	cbit.vcell.dictionary.DBSpecies oldValue = fieldDBSpecies;
	fireVetoableChange("dbFormalSpecies", oldValue, argDBSpecies);
	fieldDBSpecies = argDBSpecies;
	firePropertyChange("dbFormalSpecies", oldValue, argDBSpecies);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Species@"+Integer.toHexString(hashCode())+"("+"'commonName='"+getCommonName()+"')";
}


	/**
	 * This method gets called when a constrained property is changed.
	 *
	 * @param     evt a <code>PropertyChangeEvent</code> object describing the
	 *   	      event source and the property that has changed.
	 * @exception PropertyVetoException if the recipient wishes the property
	 *              change to be rolled back.
	 */
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
			if (!Character.isJavaIdentifierStart(newName.charAt(0))){
				throw new PropertyVetoException("species name '"+newName+"' can't start with a '"+newName.charAt(0)+"'",e);
			}
			for (int i=1;i<newName.length();i++){
				if (!Character.isJavaIdentifierPart(newName.charAt(i))){
					throw new PropertyVetoException("species name '"+newName+"' can't include a '"+newName.charAt(i)+"'",e);
				}
			}	
		}
	}
}
}