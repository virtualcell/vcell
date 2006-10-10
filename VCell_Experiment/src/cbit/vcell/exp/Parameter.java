package cbit.vcell.exp;

	/*©
	 * (C) Copyright University of Connecticut Health Center 2001.
	 * All rights reserved.
	©*/
	import java.util.*;
	import java.io.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTableEntry;

	import cbit.vcell.parser.*;
	import cbit.vcell.units.VCUnitDefinition;
import cbit.util.*;

	public abstract class Parameter implements SymbolTableEntry, Serializable, Matchable
	{
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
	 * @param parm cbit.vcell.model.Parameter
	 */
	protected boolean compareEqual0(Parameter parm) {
		if (!Compare.isEqual(getName(),parm.getName())){
			return false;
		}
		if (!Compare.isEqualOrNull(getExpression(),parm.getExpression())){
			return false;
		}
		if (!VCUnitDefinition.isEqual(getUnitDefinition(),parm.getUnitDefinition())){
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
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.parser.Expression
	 */
	public abstract IExpression getExpression();
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

}