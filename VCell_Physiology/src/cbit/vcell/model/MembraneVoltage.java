package cbit.vcell.model;
import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2002 4:16:31 PM)
 * @author: Jim Schaff
 */
public class MembraneVoltage implements org.vcell.expression.SymbolTableEntry, java.io.Serializable, org.vcell.util.Matchable {
	private java.lang.String fieldName = new String();
	private Membrane fieldMembrane = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	public static final String MEMBRANE_VOLTAGE_REGION_NAME = "SpatialMembraneVoltage";

/**
 * MembraneVoltage constructor comment.
 */
public MembraneVoltage(String name, Membrane membrane) {
	super();
	fieldName = name;
	fieldMembrane = membrane;
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
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof MembraneVoltage){
		MembraneVoltage mv = (MembraneVoltage)obj;
		if (!mv.getName().equals(getName())){
			return false;
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
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws org.vcell.expression.ExpressionException {
	throw new org.vcell.expression.ExpressionException("MembraneVoltage.getConstantValue(): not supported");
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public IExpression getExpression() throws org.vcell.expression.ExpressionException {
	return null;
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
public org.vcell.expression.NameScope getNameScope() {
	return fieldMembrane.getNameScope();
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
 * Creation date: (3/31/2004 12:09:04 PM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
	return cbit.vcell.units.VCUnitDefinition.UNIT_mV;
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
public boolean isConstant() throws org.vcell.expression.ExpressionException {
	return false;
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