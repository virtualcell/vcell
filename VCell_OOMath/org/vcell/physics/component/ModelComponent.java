package org.vcell.physics.component;
import jscl.plugin.Expression;

/**
 * Insert the type's description here.
 * Creation date: (11/17/2005 3:44:41 PM)
 * @author: Jim Schaff
 */
public class ModelComponent {
	//
	// may have children/scope???? later
	//
	private java.lang.String name = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private org.vcell.physics.component.Connector[] fieldConnectors = new Connector[0];
	private Expression[] fieldEquations = new Expression[0];
	private org.vcell.physics.component.PhysicalSymbol[] fieldSymbols = new PhysicalSymbol[0];
	private OOModel ooModel = null;

	
/**
 * Component constructor comment.
 */
public ModelComponent(String argName) {
	super();
	this.name = argName;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 10:51:09 PM)
 * @param symbol ncbc.physics2.component.Symbol
 */
public void addConnector(Connector connector) {
	setConnectors((Connector[])org.vcell.util.BeanUtils.addElement(fieldConnectors,connector));
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 10:51:09 PM)
 * @param symbol ncbc.physics2.component.Symbol
 */
public void addEquation(Expression exp) {
	setEquations((Expression[])org.vcell.util.BeanUtils.addElement(fieldEquations,exp));
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 10:51:09 PM)
 * @param physicalSymbol ncbc.physics2.component.Symbol
 */
public void addSymbol(PhysicalSymbol physicalSymbol) {
	setSymbols((PhysicalSymbol[])org.vcell.util.BeanUtils.addElement(fieldSymbols,physicalSymbol));
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the symbols index property (ncbc.physics2.component.Symbol) value.
 * @return The symbols property value.
 * @param index The index value into the property array.
 * @see #setSymbols
 */
public Connector getConnector(String name) {
	for (int i = 0; i < fieldConnectors.length; i++){
		if (fieldConnectors[i].getName().equals(name)){
			return fieldConnectors[i];
		}
	}
	return null;
}


/**
 * Gets the connectors property (ncbc.physics2.component.Connector[]) value.
 * @return The connectors property value.
 * @see #setConnectors
 */
public org.vcell.physics.component.Connector[] getConnectors() {
	return fieldConnectors;
}


/**
 * Gets the connectors index property (ncbc.physics2.component.Connector) value.
 * @return The connectors property value.
 * @param index The index value into the property array.
 * @see #setConnectors
 */
public Connector getConnectors(int index) {
	return getConnectors()[index];
}

public Connector getConnectors(String connectorName) {
	for (int i = 0; i < fieldConnectors.length; i++) {
		if (fieldConnectors[i].getName().equals(connectorName)){
			return fieldConnectors[i];
		}
	}
	return null;
}


/**
 * Gets the equations property (cbit.vcell.parser.Expression[]) value.
 * @return The equations property value.
 * @see #setEquations
 */
public Expression[] getEquations() {
	return fieldEquations;
}


/**
 * Gets the equations index property (cbit.vcell.parser.Expression) value.
 * @return The equations property value.
 * @param index The index value into the property array.
 * @see #setEquations
 */
public Expression getEquations(int index) {
	return getEquations()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2005 3:45:33 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
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
 * Gets the symbols index property (ncbc.physics2.component.Symbol) value.
 * @return The symbols property value.
 * @param index The index value into the property array.
 * @see #setSymbols
 */
public PhysicalSymbol getSymbol(String name) {
	for (int i = 0; i < fieldSymbols.length; i++){
		if (fieldSymbols[i].getName().equals(name)){
			return fieldSymbols[i];
		}
	}
	return null;
}


/**
 * Gets the symbols property (ncbc.physics2.component.Symbol[]) value.
 * @return The symbols property value.
 * @see #setSymbols
 */
public org.vcell.physics.component.PhysicalSymbol[] getSymbols() {
	return fieldSymbols;
}


/**
 * Gets the symbols index property (ncbc.physics2.component.Symbol) value.
 * @return The symbols property value.
 * @param index The index value into the property array.
 * @see #setSymbols
 */
public PhysicalSymbol getSymbols(int index) {
	return getSymbols()[index];
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
 * Sets the connectors property (ncbc.physics2.component.Connector[]) value.
 * @param connectors The new value for the property.
 * @see #getConnectors
 */
public void setConnectors(org.vcell.physics.component.Connector[] connectors) {
	org.vcell.physics.component.Connector[] oldValue = fieldConnectors;
	fieldConnectors = connectors;
	firePropertyChange("connectors", oldValue, connectors);
}


/**
 * Sets the equations property (cbit.vcell.parser.Expression[]) value.
 * @param equations The new value for the property.
 * @see #getEquations
 */
public void setEquations(Expression[] equations) {
	Expression[] oldValue = fieldEquations;
	fieldEquations = equations;
	firePropertyChange("equations", oldValue, equations);
}


/**
 * Sets the equations index property (cbit.vcell.parser.Expression[]) value.
 * @param index The index value into the property array.
 * @param equations The new value for the property.
 * @see #getEquations
 */
public void setEquations(int index, Expression equations) {
	Expression oldValue = fieldEquations[index];
	fieldEquations[index] = equations;
	if (oldValue != null && !oldValue.equals(equations)) {
		firePropertyChange("equations", null, fieldEquations);
	};
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2005 3:45:33 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}


/**
 * Sets the symbols property (ncbc.physics2.component.Symbol[]) value.
 * @param physicalSymbols The new value for the property.
 * @see #getSymbols
 */
public void setSymbols(org.vcell.physics.component.PhysicalSymbol[] physicalSymbols) {
	org.vcell.physics.component.PhysicalSymbol[] oldValue = fieldSymbols;
	fieldSymbols = physicalSymbols;
	firePropertyChange("symbols", oldValue, physicalSymbols);
}


/**
 * Sets the symbols index property (ncbc.physics2.component.Symbol[]) value.
 * @param index The index value into the property array.
 * @param physicalSymbols The new value for the property.
 * @see #getSymbols
 */
public void setSymbols(int index, PhysicalSymbol physicalSymbols) {
	PhysicalSymbol oldValue = fieldSymbols[index];
	fieldSymbols[index] = physicalSymbols;
	if (oldValue != null && !oldValue.equals(physicalSymbols)) {
		firePropertyChange("symbols", null, fieldSymbols);
	};
}

}