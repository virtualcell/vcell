package cbit.vcell.model;

import cbit.vcell.parser.*;
/**
 * Insert the type's description here.
 * Creation date: (2/20/2002 4:16:31 PM)
 * @author: Jim Schaff
 */
public class ChargeCarrierValence implements org.vcell.util.Matchable, SymbolTableEntry, java.io.Serializable {
	private java.lang.String fieldName = null;
	private cbit.vcell.parser.NameScope fieldNameScope = null;
	private cbit.vcell.parser.Expression fieldExpression = null;
	
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
/**
 * MembraneVoltage constructor comment.
 */
public ChargeCarrierValence(String name, NameScope nameScope) {
	super();
	fieldName = name;
	fieldNameScope = nameScope;
	fieldExpression = new Expression(0.0);
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
	if (obj instanceof ChargeCarrierValence){
		ChargeCarrierValence ccv = (ChargeCarrierValence)obj;
		if (!ccv.getName().equals(getName())){
			return false;
		}
		return true;
	}
	return false;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
private void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
private void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
	return fieldExpression.evaluateConstant();
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public cbit.vcell.parser.Expression getExpression() {
	return fieldExpression;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getIndex() {
	return -1;
}
/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 10:28:46 AM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
	return fieldNameScope;
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
 * Creation date: (3/31/2004 10:35:33 AM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
	return cbit.vcell.units.VCUnitDefinition.UNIT_DIMENSIONLESS;
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
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public boolean isConstant() throws cbit.vcell.parser.ExpressionException {
	return true;
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
 * Sets the expression property (cbit.vcell.parser.Expression) value.
 * @param expression The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getExpression
 */
public void setExpression(cbit.vcell.parser.Expression expression) throws java.beans.PropertyVetoException {
	Expression oldValue = fieldExpression;
	fireVetoableChange("expression", oldValue, expression);
	fieldExpression = expression;
	firePropertyChange("expression", oldValue, expression);
}
/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}
}
