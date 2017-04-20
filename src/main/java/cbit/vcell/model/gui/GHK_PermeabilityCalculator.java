/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import java.beans.PropertyVetoException;

import cbit.vcell.model.Kinetics;

/**
 * Insert the type's description here.
 * Creation date: (4/4/2002 4:59:42 PM)
 * @author: Jim Schaff
 */
public class GHK_PermeabilityCalculator implements java.beans.VetoableChangeListener {
    protected transient java.beans.PropertyChangeSupport propertyChange;
    private cbit.vcell.model.GHKKinetics fieldGhkKinetics = null;
    private double fieldInsideConcentration = 1000;
    private double fieldOutsideConcentration = 1000;
    private double fieldPermeability = 0;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private double fieldConductanceAtNernst = 0;
/**
 * GHK_PermeabilityCalculator constructor comment.
 */
public GHK_PermeabilityCalculator() {
	super();
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
public synchronized void addVetoableChangeListener(String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
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
public void fireVetoableChange(String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * Gets the conductanceAtNernst property (double) value.
 * @return The conductanceAtNernst property value.
 * @see #setConductanceAtNernst
 */
public double getConductanceAtNernst() {
	return fieldConductanceAtNernst;
}
/**
 * Gets the ghkKinetics property (cbit.vcell.model.GHKKinetics) value.
 * @return The ghkKinetics property value.
 * @see #setGhkKinetics
 */
public cbit.vcell.model.GHKKinetics getGhkKinetics() {
	return fieldGhkKinetics;
}
/**
 * Gets the insideConcentration property (double) value.
 * @return The insideConcentration property value.
 * @see #setInsideConcentration
 */
public double getInsideConcentration() {
	return fieldInsideConcentration;
}
/**
 * Gets the outsideConcentration property (double) value.
 * @return The outsideConcentration property value.
 * @see #setOutsideConcentration
 */
public double getOutsideConcentration() {
	return fieldOutsideConcentration;
}
/**
 * Gets the permeability property (double) value.
 * @return The permeability property value.
 * @see #setPermeability
 */
public double getPermeability() {
	return fieldPermeability;
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
public synchronized void removeVetoableChangeListener(String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}
/**
 * Sets the conductanceAtNernst property (double) value.
 * @param conductanceAtNernst The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getConductanceAtNernst
 */
public void setConductanceAtNernst(double conductanceAtNernst) throws java.beans.PropertyVetoException {
	double oldValue = fieldConductanceAtNernst;
	fireVetoableChange("conductanceAtNernst", new Double(oldValue), new Double(conductanceAtNernst));
	fieldConductanceAtNernst = conductanceAtNernst;
	updatePermeability();
	firePropertyChange("conductanceAtNernst", new Double(oldValue), new Double(conductanceAtNernst));
}
/**
 * Sets the ghkKinetics property (cbit.vcell.model.GHKKinetics) value.
 * @param ghkKinetics The new value for the property.
 * @see #getGhkKinetics
 */
public void setGhkKinetics(cbit.vcell.model.GHKKinetics ghkKinetics) {
	cbit.vcell.model.GHKKinetics oldValue = fieldGhkKinetics;
	fieldGhkKinetics = ghkKinetics;
	updatePermeability();
	firePropertyChange("ghkKinetics", oldValue, ghkKinetics);
}
/**
 * Sets the insideConcentration property (double) value.
 * @param insideConcentration The new value for the property.
 * @see #getInsideConcentration
 */
public void setInsideConcentration(double insideConcentration) throws java.beans.PropertyVetoException {
	double oldValue = fieldInsideConcentration;
	fireVetoableChange("insideConcentration", new Double(oldValue), new Double(insideConcentration));
	fieldInsideConcentration = insideConcentration;
	updatePermeability();
	firePropertyChange("insideConcentration", new Double(oldValue), new Double(insideConcentration));
}
/**
 * Sets the outsideConcentration property (double) value.
 * @param outsideConcentration The new value for the property.
 * @see #getOutsideConcentration
 */
public void setOutsideConcentration(double outsideConcentration) throws java.beans.PropertyVetoException {
	double oldValue = fieldOutsideConcentration;
	fireVetoableChange("outsideConcentration", new Double(oldValue), new Double(outsideConcentration));
	fieldOutsideConcentration = outsideConcentration;
	updatePermeability();
	firePropertyChange("outsideConcentration", new Double(oldValue), new Double(outsideConcentration));
}
/**
 * Sets the permeability property (double) value.
 * @param permeability The new value for the property.
 * @see #getPermeability
 */
private void setPermeability(double permeability) {
	double oldValue = fieldPermeability;
	fieldPermeability = permeability;
	firePropertyChange("permeability", new Double(oldValue), new Double(permeability));
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2002 5:20:32 PM)
 */
private void updatePermeability() {
	try {
		if (getGhkKinetics()==null){
			return;
		}
		double z = getGhkKinetics().getKineticsParameterFromRole(Kinetics.ROLE_CarrierChargeValence).getExpression().evaluateConstant();
		double R = getGhkKinetics().getReactionStep().getModel().getGAS_CONSTANT().getConstantValue();
		double T = 300;
		double F = getGhkKinetics().getReactionStep().getModel().getFARADAY_CONSTANT().getConstantValue();
		double Co = fieldOutsideConcentration;
		double Ci = fieldInsideConcentration;
		double g = getConductanceAtNernst();

		if (Math.abs(Ci - Co) < 1e-6){
			setPermeability(R*T/(z*z*F*F) * g * (1/Co));
		}else{
			setPermeability(R*T/(z*z*F*F) * g * ((Co-Ci)/(Co*Ci*Math.log(Co/Ci))));
		}
		
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
	}
}
	/**
	 * This method gets called when a constrained property is changed.
	 *
	 * @param     evt a <code>PropertyChangeEvent</code> object describing the
	 *   	      event source and the property that has changed.
	 * @exception PropertyVetoException if the recipient wishes the property
	 *              change to be rolled back.
	 */
public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	if (evt.getSource() == this && evt.getPropertyName().equals("conductanceAtNernst")){
		Double value = (Double)evt.getNewValue();
		if (value.doubleValue() <= 0){
			throw new java.beans.PropertyVetoException("conductance must be positive",evt);
		}
	}
	if (evt.getSource() == this && evt.getPropertyName().equals("insideConcentration")){
		Double value = (Double)evt.getNewValue();
		if (value.doubleValue() <= 0){
			throw new java.beans.PropertyVetoException("inside concentration must be positive",evt);
		}
	}
	if (evt.getSource() == this && evt.getPropertyName().equals("outsideConcentration")){
		Double value = (Double)evt.getNewValue();
		if (value.doubleValue() <= 0){
			throw new java.beans.PropertyVetoException("outside concentration must be positive",evt);
		}
	}
}
}
