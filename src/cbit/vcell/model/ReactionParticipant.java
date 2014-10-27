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
import org.vcell.util.Compare;
import org.vcell.util.document.KeyValue;

public abstract class ReactionParticipant implements ModelProcessParticipant, org.vcell.util.Cacheable, org.vcell.util.Matchable, java.beans.PropertyChangeListener, java.io.Serializable {
	private KeyValue key = null;
	protected SpeciesContext speciesContext = null;
	protected ReactionStep parent = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldStoichiometry = 0;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;

/**
 * This method was created in VisualAge.
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
protected ReactionParticipant(KeyValue key, ReactionStep reactionStep) {
	this(key,reactionStep,null,0);
}


protected ReactionParticipant(KeyValue key, ReactionStep parent,SpeciesContext speciesContext, int stoichiometry) {
	this.parent = parent;
	this.key = key;
	try {
		setSpeciesContext(speciesContext);
	}catch (java.beans.PropertyVetoException e){
		//
		// this will never get called, because there are no listeners yet.
		//
		throw new RuntimeException(e.getMessage());
	}
	setStoichiometry(stoichiometry);
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
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param rp cbit.vcell.model.ReactionParticipant
 */
protected boolean compareEqual0(ReactionParticipant rp) {

	if (rp == null){
		return false;
	}

	if (!Compare.isEqual(getSpecies(),rp.getSpecies())){
		return false;
	}
	if (!Compare.isEqual(getStructure(),rp.getStructure())){
		return false;
	}
//	if (!Compare.isEqual(getReactionStep().getName(),rp.getReactionStep().getName())){  // name of parent only (to avoid recursion)
//		return false;
//	}
	if (getStoichiometry() != rp.getStoichiometry()){
		return false;
	}
	
	return true;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
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
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public abstract void fromTokens(org.vcell.util.CommentStringTokenizer tokens, Model model) throws Exception;


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getKey() {
	return key;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/01 3:59:00 PM)
 * @return java.lang.String
 */
public String getName() {
	return speciesContext.getName();
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
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionStep
 */
public ReactionStep getReactionStep() {
	return parent;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Species
 */
public Species getSpecies() {
	return speciesContext.getSpecies();
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.SpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return speciesContext;
}


/**
 * Gets the stoichiometry property (int) value.
 * @return The stoichiometry property value.
 * @see #setStoichiometry
 */
public int getStoichiometry() {
	return fieldStoichiometry;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Species
 */
public Structure getStructure() {
	return speciesContext.getStructure();
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
 * The hasListeners method was generated to support the vetoPropertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getVetoPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/01 3:52:50 PM)
 * @param evt java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
    if (evt.getSource() == getSpeciesContext()
        && evt.getPropertyName().equals("name")) {
        firePropertyChange("name", evt.getOldValue(), evt.getNewValue());
    }
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/01 3:51:46 PM)
 */
public void refreshDependencies() {
	speciesContext.removePropertyChangeListener(this);
	speciesContext.addPropertyChangeListener(this);
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
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * This method was created in VisualAge.
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public void setReactionStep(ReactionStep reactionStep) {
	this.parent = reactionStep;
}


/**
 * This method was created in VisualAge.
 * @param speciesContext cbit.vcell.model.SpeciesContext
 */
public void setSpeciesContext(SpeciesContext argSpeciesContext) throws java.beans.PropertyVetoException {
	String oldName = null;
	SpeciesContext oldSpeciesContext = this.speciesContext;
	fireVetoableChange("speciesContext", oldSpeciesContext, argSpeciesContext);
	
	if (this.speciesContext!=null){
		oldName = this.speciesContext.getName();
		this.speciesContext.removePropertyChangeListener(this);
	}
	
	this.speciesContext = argSpeciesContext;
	
	String newName = null;
	if (this.speciesContext!=null){
		newName = this.speciesContext.getName();
		this.speciesContext.addPropertyChangeListener(this);
	}
	
	firePropertyChange("name",oldName,newName);
	firePropertyChange("speciesContext",oldSpeciesContext,argSpeciesContext);
}


/**
 * Sets the stoichiometry property (int) value.
 * @param stoichiometry The new value for the property.
 * @see #getStoichiometry
 */
public void setStoichiometry(int stoichiometry) {
	int oldValue = fieldStoichiometry;
	fieldStoichiometry = stoichiometry;
	firePropertyChange("stoichiometry", new Integer(oldValue), new Integer(stoichiometry));
}


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public abstract void writeTokens(java.io.PrintWriter pw);
}
