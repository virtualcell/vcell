package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/11/2004 11:37:02 PM)
 * @author: Jim Schaff
 */
public class PhysicalModel {
	private org.vcell.ncbc.physics.component.Device[] fieldDevices = new Device[0];
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private org.vcell.ncbc.physics.component.Connection[] fieldConnections = new Connection[0];
	private org.vcell.ncbc.physics.component.Location[] fieldLocations = new Location[0];

/**
 * PhysicalModel constructor comment.
 */
public PhysicalModel() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 7:22:13 PM)
 * @param newDevice ncbc_old.physics.component.Device
 */
public void addConnection(Connection newConnection) throws java.beans.PropertyVetoException {
	if (newConnection==null){
		throw new IllegalArgumentException("connection was null");
	}
	setConnections((Connection[])org.vcell.util.BeanUtils.addElement(fieldConnections,newConnection));
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 7:22:13 PM)
 * @param newDevice ncbc_old.physics.component.Device
 */
public void addDevice(Device newDevice) throws java.beans.PropertyVetoException {
	if (newDevice==null){
		throw new IllegalArgumentException("device was null");
	}
	setDevices((Device[])org.vcell.util.BeanUtils.addElement(fieldDevices,newDevice));
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 7:22:13 PM)
 * @param newDevice ncbc_old.physics.component.Device
 */
public void addLocation(Location newLocation) throws java.beans.PropertyVetoException {
	if (newLocation==null){
		throw new IllegalArgumentException("location was null");
	}
	setLocations((Location[])org.vcell.util.BeanUtils.addElement(fieldLocations,newLocation));
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
 * Gets the connections property (ncbc_old.physics.component.DevicePinConnection[]) value.
 * @return The connections property value.
 * @see #setConnections
 */
public org.vcell.ncbc.physics.component.Connection[] getConnections() {
	return fieldConnections;
}


/**
 * Gets the connections index property (ncbc_old.physics.component.DevicePinConnection) value.
 * @return The connections property value.
 * @param index The index value into the property array.
 * @see #setConnections
 */
public Connection getConnections(int index) {
	return getConnections()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 6:01:37 PM)
 * @return ncbc_old.physics.component.Device
 * @param name java.lang.String
 */
public Device getDeviceFromName(String name) {
	for (int i = 0; i < fieldDevices.length; i++){
		if (fieldDevices[i].getName().equals(name)){
			return fieldDevices[i];
		}
	}
	return null;
}


/**
 * Gets the devices property (ncbc_old.physics.component.Device[]) value.
 * @return The devices property value.
 * @see #setDevices
 */
public org.vcell.ncbc.physics.component.Device[] getDevices() {
	return fieldDevices;
}


/**
 * Gets the devices index property (ncbc_old.physics.component.Device) value.
 * @return The devices property value.
 * @param index The index value into the property array.
 * @see #setDevices
 */
public Device getDevices(int index) {
	return getDevices()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 6:28:06 PM)
 * @return ncbc_old.physics.component.Device[]
 * @param location ncbc_old.physics.component.Location
 * @param deviceClass java.lang.Class
 */
public Device[] getDevices(Location location, Class deviceClass) {

	java.util.Vector deviceList = new java.util.Vector();
	for (int i = 0; i < fieldDevices.length; i++){
		boolean bLocationOK = (location==null || location==fieldDevices[i].getLocation());
		boolean bClassOK = (deviceClass==null || deviceClass.isInstance(fieldDevices[i]));
		if (bLocationOK && bClassOK){
			deviceList.add(fieldDevices[i]);
		}
	}
	
	return (Device[])org.vcell.util.BeanUtils.getArray(deviceList,deviceClass);
}


/**
 * Gets the locations property (ncbc_old.physics.component.Location[]) value.
 * @return The locations property value.
 * @see #setLocations
 */
public org.vcell.ncbc.physics.component.Location getLocation(String argName) {
	for (int i = 0; i < fieldLocations.length; i++){
		if (fieldLocations[i].getName().equals(argName)){
			return fieldLocations[i];
		}
	}
	return null;
}


/**
 * Gets the locations property (ncbc_old.physics.component.Location[]) value.
 * @return The locations property value.
 * @see #setLocations
 */
public org.vcell.ncbc.physics.component.Location[] getLocations() {
	return fieldLocations;
}


/**
 * Gets the locations index property (ncbc_old.physics.component.Location) value.
 * @return The locations property value.
 * @param index The index value into the property array.
 * @see #setLocations
 */
public Location getLocations(int index) {
	return getLocations()[index];
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
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * Sets the connections property (ncbc_old.physics.component.DevicePinConnection[]) value.
 * @param connections The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getConnections
 */
public void setConnections(org.vcell.ncbc.physics.component.Connection[] connections) throws java.beans.PropertyVetoException {
	org.vcell.ncbc.physics.component.Connection[] oldValue = fieldConnections;
	fireVetoableChange("connections", oldValue, connections);
	fieldConnections = connections;
	firePropertyChange("connections", oldValue, connections);
}


/**
 * Sets the connections index property (ncbc_old.physics.component.DevicePinConnection[]) value.
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
 * Sets the devices property (ncbc_old.physics.component.Device[]) value.
 * @param devices The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getDevices
 */
public void setDevices(org.vcell.ncbc.physics.component.Device[] devices) throws java.beans.PropertyVetoException {
	org.vcell.ncbc.physics.component.Device[] oldValue = fieldDevices;
	fireVetoableChange("devices", oldValue, devices);
	fieldDevices = devices;
	firePropertyChange("devices", oldValue, devices);
}


/**
 * Sets the devices index property (ncbc_old.physics.component.Device[]) value.
 * @param index The index value into the property array.
 * @param devices The new value for the property.
 * @see #getDevices
 */
public void setDevices(int index, Device devices) {
	Device oldValue = fieldDevices[index];
	fieldDevices[index] = devices;
	if (oldValue != null && !oldValue.equals(devices)) {
		firePropertyChange("devices", null, fieldDevices);
	};
}


/**
 * Sets the locations property (ncbc_old.physics.component.Location[]) value.
 * @param locations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getLocations
 */
public void setLocations(org.vcell.ncbc.physics.component.Location[] locations) throws java.beans.PropertyVetoException {
	org.vcell.ncbc.physics.component.Location[] oldValue = fieldLocations;
	fireVetoableChange("locations", oldValue, locations);
	fieldLocations = locations;
	firePropertyChange("locations", oldValue, locations);
}


/**
 * Sets the locations index property (ncbc_old.physics.component.Location[]) value.
 * @param index The index value into the property array.
 * @param locations The new value for the property.
 * @see #getLocations
 */
public void setLocations(int index, Location locations) {
	Location oldValue = fieldLocations[index];
	fieldLocations[index] = locations;
	if (oldValue != null && !oldValue.equals(locations)) {
		firePropertyChange("locations", null, fieldLocations);
	};
}
}