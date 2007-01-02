package org.vcell.physics.component;


/**
 * Insert the type's description here.
 * Creation date: (11/26/2005 11:03:14 AM)
 * @author: Jim Schaff
 */
public class OOModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private org.vcell.physics.component.ModelComponent[] fieldModelComponents = new ModelComponent[0];
	private org.vcell.physics.component.Connection[] fieldConnections = new Connection[0];
	private String name = "unnamed";

/**
 * Model constructor comment.
 */
public OOModel() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:36:30 PM)
 * @param connection ncbc.physics2.component.Connection
 */
public void addConnection(Connection connection) {
	setConnections((Connection[])cbit.util.BeanUtils.addElement(fieldConnections,connection));
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:52 PM)
 * @param modelComponent ncbc.physics2.component.ModelComponent
 */
public void addModelComponent(ModelComponent modelComponent) {
	setModelComponents((ModelComponent[])cbit.util.BeanUtils.addElement(fieldModelComponents,modelComponent));
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
 * Gets the connections property (ncbc.physics2.component.Connection[]) value.
 * @return The connections property value.
 * @see #setConnections
 */
public org.vcell.physics.component.Connection[] getConnections() {
	return fieldConnections;
}


/**
 * Gets the connections index property (ncbc.physics2.component.Connection) value.
 * @return The connections property value.
 * @param index The index value into the property array.
 * @see #setConnections
 */
public Connection getConnections(int index) {
	return getConnections()[index];
}


/**
 * Gets the modelComponents index property (ncbc.physics2.component.ModelComponent) value.
 * @return The modelComponents property value.
 * @param index The index value into the property array.
 * @see #setModelComponents
 */
public ModelComponent getModelComponent(String name) {
	for (int i = 0;fieldModelComponents!=null && i < fieldModelComponents.length; i++){
		if (fieldModelComponents[i].getName().equals(name)){
			return fieldModelComponents[i];
		}
	}
	return null;
}


/**
 * Gets the modelComponents property (ncbc.physics2.component.ModelComponent[]) value.
 * @return The modelComponents property value.
 * @see #setModelComponents
 */
public org.vcell.physics.component.ModelComponent[] getModelComponents() {
	return fieldModelComponents;
}


/**
 * Gets the modelComponents index property (ncbc.physics2.component.ModelComponent) value.
 * @return The modelComponents property value.
 * @param index The index value into the property array.
 * @see #setModelComponents
 */
public ModelComponent getModelComponents(int index) {
	return getModelComponents()[index];
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
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:36:30 PM)
 * @param connection ncbc.physics2.component.Connection
 */
public void joinConnection(Connector connector1, Connector connector2) {
	for (int i = 0; i < fieldConnections.length; i++){
		Connector[] connectors = fieldConnections[i].getConnectors();
		Connector foundConnector = null;
		boolean connector1Present = false;
		boolean connector2Present = false;
		for (int j = 0; j < connectors.length; j++){
			if (connectors[j] == connector1){
				connector1Present = true;
			}
			if (connectors[j] == connector2){
				connector2Present = true;
			}
		}
		if (connector1Present && connector2Present){
			throw new RuntimeException("connection already exists between "+connector1+" and "+connector2);
		}
		if (connector1Present){
			// connector1 found connector2 not found
			fieldConnections[i].addConnector(connector2);
			return;
		}else if (connector2Present){
			// connector2 found connector1 not found
			fieldConnections[i].addConnector(connector1);
			return;
		}
	}
	//
	// neither present, make new connection
	//
	addConnection(new Connection(new Connector[] { connector1, connector2 }));
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
 * Sets the connections property (ncbc.physics2.component.Connection[]) value.
 * @param connections The new value for the property.
 * @see #getConnections
 */
public void setConnections(org.vcell.physics.component.Connection[] connections) {
	org.vcell.physics.component.Connection[] oldValue = fieldConnections;
	fieldConnections = connections;
	firePropertyChange("connections", oldValue, connections);
}


/**
 * Sets the connections index property (ncbc.physics2.component.Connection[]) value.
 * @param index The index value into the property array.
 * @param connections The new value for the property.
 * @see #getConnections
 */
public void setConnections(int index, Connection connections) {
	Connection oldValue = fieldConnections[index];
	fieldConnections[index] = connections;
	if (oldValue != null && !oldValue.equals(connections)) {
		firePropertyChange("connections", null, fieldConnections);
	};
}


/**
 * Sets the modelComponents property (ncbc.physics2.component.ModelComponent[]) value.
 * @param modelComponents The new value for the property.
 * @see #getModelComponents
 */
public void setModelComponents(org.vcell.physics.component.ModelComponent[] modelComponents) {
	org.vcell.physics.component.ModelComponent[] oldValue = fieldModelComponents;
	fieldModelComponents = modelComponents;
	firePropertyChange("modelComponents", oldValue, modelComponents);
}


public String getName() {
	return name;
}


public void setName(String name) {
	this.name = name;
}

}