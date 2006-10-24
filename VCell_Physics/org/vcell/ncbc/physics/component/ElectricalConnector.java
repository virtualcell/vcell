package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/7/2004 9:11:28 AM)
 * @author: Jim Schaff
 */
public class ElectricalConnector extends Connector {
/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:13:57 AM)
 * @param name java.lang.String
 */
public ElectricalConnector(Device argDevice, String argName, int voltageRole, int currentRole) {
	super(argDevice, argName, new Port[] { new VoltagePort(voltageRole), new CurrentPort(currentRole) } );
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 5:18:11 PM)
 * @return ncbc.physics.component.VoltagePort
 */
public CurrentPort getCurrentPort() {
	return (CurrentPort)getPortByVariableName(CurrentPort.NAME);
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 5:18:11 PM)
 * @return ncbc.physics.component.VoltagePort
 */
public VoltagePort getVoltagePort() {
	return (VoltagePort)getPortByVariableName(VoltagePort.NAME);
}
}