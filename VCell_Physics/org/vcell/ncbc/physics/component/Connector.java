package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 10:11:51 AM)
 * @author: Jim Schaff
 */
public abstract class Connector {
	private String name = null;
	private Port ports[] = null;
	private Device device = null;

/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:12:23 AM)
 * @param name java.lang.String
 * @param ports ncbc.physics.component.Port[]
 */
Connector(Device argDevice, String argName, Port[] argPorts) {
	this.name = argName;
	this.ports = argPorts;
	this.device = argDevice;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 3:37:20 PM)
 * @return ncbc.physics.component.Device
 */
public Device getDevice() {
	return device;
}


/**
 * Insert the method's description here.
 * Creation date: (1/6/2004 10:14:57 AM)
 * @return java.lang.String
 */
public final String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:45:28 PM)
 * @return ncbc.physics.component.Port
 * @param argName java.lang.String
 */
public Port getPortByVariableName(String argVariableName) {
	for (int i = 0; ports!=null && i < ports.length; i++){
		if (ports[i].getVariable().getName().equals(argVariableName)){
			return ports[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:11:51 AM)
 * @return ncbc.physics.component.Port[]
 */
public final Port[] getPorts() {
	return ports;
}
}