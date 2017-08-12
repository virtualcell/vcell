/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints;
/**
 * Insert the type's description here.
 * Creation date: (6/25/01 4:40:26 PM)
 * @author: Jim Schaff
 */
import net.sourceforge.interval.ia_math.RealInterval;

public class ConstraintContainerImpl {
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private GeneralConstraint[] fieldGeneralConstraints = new GeneralConstraint[0];
	private SimpleBounds[] fieldSimpleBounds = new SimpleBounds[0];

	private class ConstraintStatus {
		private boolean bActive = true;
		private boolean bConsistent = true;
		private ConstraintStatus(boolean argActive, boolean argConsistent){
			bActive = argActive;
			bConsistent = argConsistent;
		}
	};
	private java.util.HashMap statusMap = new java.util.HashMap();

/**
 * ConstraintContainer constructor comment.
 */
public ConstraintContainerImpl() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/01 4:41:49 PM)
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public void addGeneralConstraint(GeneralConstraint constraint) throws java.beans.PropertyVetoException {
	if (org.vcell.util.BeanUtils.arrayContains(fieldGeneralConstraints,constraint)){
		throw new RuntimeException(constraint+" already exists");
	}
	GeneralConstraint newGeneralConstraint[] = (GeneralConstraint[])org.vcell.util.BeanUtils.addElement(fieldGeneralConstraints,constraint);
	setGeneralConstraints(newGeneralConstraint);
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
 * Insert the method's description here.
 * Creation date: (6/25/01 4:41:49 PM)
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public void addSimpleBound(SimpleBounds bounds) throws java.beans.PropertyVetoException {
	if (org.vcell.util.BeanUtils.arrayContains(fieldSimpleBounds,bounds)){
		throw new RuntimeException(bounds+" already exists");
	}
	SimpleBounds newSimpleBounds[] = (SimpleBounds[])org.vcell.util.BeanUtils.addElement(fieldSimpleBounds,bounds);
	setSimpleBounds(newSimpleBounds);
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
 * Insert the method's description here.
 * Creation date: (1/4/2005 12:18:54 PM)
 * @return boolean
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public boolean getActive(AbstractConstraint constraint) {
	ConstraintStatus status = (ConstraintStatus)statusMap.get(constraint);
	if (status!=null){
		return status.bActive;
	}else{
		statusMap.put(constraint, new ConstraintStatus(true,true));
		return true;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/01 5:22:43 PM)
 * @return cbit.vcell.constraints.SimpleBounds
 * @param identifierName java.lang.String
 */
public RealInterval getBounds(String identifierName) {

	//
	// if only one, then just return it, else return intersection of it
	//
	RealInterval bounds = new RealInterval();
	for (int i = 0; i < fieldSimpleBounds.length; i++){
		if (fieldSimpleBounds[i].getIdentifier().equals(identifierName) && getActive(fieldSimpleBounds[i])){
			bounds.intersect(fieldSimpleBounds[i].getBounds());
		}
	}
	return bounds;
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/2005 12:18:54 PM)
 * @return boolean
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public boolean getConsistent(AbstractConstraint constraint) {
	ConstraintStatus status = (ConstraintStatus)statusMap.get(constraint);
	if (status!=null){
		return status.bConsistent;
	}else{
		statusMap.put(constraint, new ConstraintStatus(true,true));
		return true;
	}
}


/**
 * Gets the generalConstraints property (cbit.vcell.constraints.GeneralConstraint[]) value.
 * @return The generalConstraints property value.
 * @see #setGeneralConstraints
 */
public cbit.vcell.constraints.GeneralConstraint[] getGeneralConstraints() {
	return fieldGeneralConstraints;
}


/**
 * Gets the generalConstraints index property (cbit.vcell.constraints.GeneralConstraint) value.
 * @return The generalConstraints property value.
 * @param index The index value into the property array.
 * @see #setGeneralConstraints
 */
public GeneralConstraint getGeneralConstraints(int index) {
	return getGeneralConstraints()[index];
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
 * Gets the simpleBounds property (cbit.vcell.constraints.SimpleBounds[]) value.
 * @return The simpleBounds property value.
 * @see #setSimpleBounds
 */
public cbit.vcell.constraints.SimpleBounds[] getSimpleBounds() {
	return fieldSimpleBounds;
}


/**
 * Gets the simpleBounds index property (cbit.vcell.constraints.SimpleBounds) value.
 * @return The simpleBounds property value.
 * @param index The index value into the property array.
 * @see #setSimpleBounds
 */
public SimpleBounds getSimpleBounds(int index) {
	return getSimpleBounds()[index];
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
 * Creation date: (6/25/01 4:41:49 PM)
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public void removeGeneralConstraint(GeneralConstraint constraint) throws java.beans.PropertyVetoException {
	if (!org.vcell.util.BeanUtils.arrayContains(fieldGeneralConstraints,constraint)){
		throw new RuntimeException(constraint+" not found");
	}
	GeneralConstraint newGeneralConstraint[] = (GeneralConstraint[])org.vcell.util.BeanUtils.removeElement(fieldGeneralConstraints,constraint);
	setGeneralConstraints(newGeneralConstraint);
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
 * Insert the method's description here.
 * Creation date: (6/25/01 4:41:49 PM)
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public void removeSimpleBound(SimpleBounds bounds) throws java.beans.PropertyVetoException {
	if (!org.vcell.util.BeanUtils.arrayContains(fieldSimpleBounds,bounds)){
		throw new RuntimeException(bounds+" not found");
	}
	SimpleBounds newSimpleBounds[] = (SimpleBounds[])org.vcell.util.BeanUtils.removeElement(fieldSimpleBounds,bounds);
	setSimpleBounds(newSimpleBounds);
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
 * Insert the method's description here.
 * Creation date: (1/4/2005 12:18:54 PM)
 * @return boolean
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public void setActive(AbstractConstraint constraint, boolean bActive) {
	ConstraintStatus status = (ConstraintStatus)statusMap.get(constraint);
	if (status!=null){
		status.bActive = bActive;
	}else{
		statusMap.put(constraint, new ConstraintStatus(bActive,true));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/2005 12:18:54 PM)
 * @return boolean
 * @param constraint cbit.vcell.constraints.AbstractConstraint
 */
public void setConsistent(AbstractConstraint constraint, boolean bConsistent) {
	ConstraintStatus status = (ConstraintStatus)statusMap.get(constraint);
	if (status!=null){
		status.bConsistent = bConsistent;
	}else{
		statusMap.put(constraint, new ConstraintStatus(true,bConsistent));
	}
}


/**
 * Sets the generalConstraints property (cbit.vcell.constraints.GeneralConstraint[]) value.
 * @param generalConstraints The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getGeneralConstraints
 */
public void setGeneralConstraints(cbit.vcell.constraints.GeneralConstraint[] generalConstraints) throws java.beans.PropertyVetoException {
	cbit.vcell.constraints.GeneralConstraint[] oldValue = fieldGeneralConstraints;
	fireVetoableChange("generalConstraints", oldValue, generalConstraints);
	fieldGeneralConstraints = generalConstraints;
	firePropertyChange("generalConstraints", oldValue, generalConstraints);
}


/**
 * Sets the simpleBounds property (cbit.vcell.constraints.SimpleBounds[]) value.
 * @param simpleBounds The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimpleBounds
 */
public void setSimpleBounds(cbit.vcell.constraints.SimpleBounds[] simpleBounds) throws java.beans.PropertyVetoException {
	cbit.vcell.constraints.SimpleBounds[] oldValue = fieldSimpleBounds;
	fireVetoableChange("simpleBounds", oldValue, simpleBounds);
	fieldSimpleBounds = simpleBounds;
	firePropertyChange("simpleBounds", oldValue, simpleBounds);
}


/**
 * Insert the method's description here.
 * Creation date: (12/28/2004 5:47:43 PM)
 * @return java.lang.String
 */
public String toECLiPSe() {
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < fieldSimpleBounds.length; i++){
		if (getActive(fieldSimpleBounds[i])==false){
			continue;
		}
		String symbol = fieldSimpleBounds[i].getIdentifier();
		RealInterval bounds = fieldSimpleBounds[i].getBounds();
		String lowBoundsString = Double.toString(bounds.lo());
		if (bounds.lo()==Double.POSITIVE_INFINITY){
			lowBoundsString = "1.0Inf";
		}else if (bounds.lo()==Double.NEGATIVE_INFINITY){
			lowBoundsString = "-1.0Inf";
		}
		String hiBoundsString = Double.toString(bounds.hi());
		if (bounds.hi()==Double.POSITIVE_INFINITY){
			hiBoundsString = "1.0Inf";
		}else if (bounds.hi()==Double.NEGATIVE_INFINITY){
			hiBoundsString = "-1.0Inf";
		}
		buffer.append(cbit.vcell.parser.SymbolUtils.getEscapedTokenECLiPSe(symbol)+" $>= "+lowBoundsString+",");
		buffer.append(cbit.vcell.parser.SymbolUtils.getEscapedTokenECLiPSe(symbol)+" $=< "+hiBoundsString+",");
	}

	for (int i = 0; i < fieldGeneralConstraints.length; i++){
		if (getActive(fieldGeneralConstraints[i])==false){
			continue;
		}
		buffer.append(fieldGeneralConstraints[i].getExpression().infix_ECLiPSe()+", ");
	}
	return buffer.toString();
}

public void show() {
	for (GeneralConstraint gc : fieldGeneralConstraints) {
		System.out.println(gc.toString());
	}
	for (SimpleBounds sb : fieldSimpleBounds) {
		System.out.println(sb.toString());
	}
}
}
