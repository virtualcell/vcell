package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 1:44:25 PM)
 * @author: Jim Schaff
 */
public class Connection {
	private Connector connector1 = null;
	private Connector connector2 = null;

/**
 * DevicePinConnectionImpl constructor comment.
 */
public Connection(Connector argConnector1, Connector argConnector2) {
	super();
	this.connector1 = argConnector1;
	this.connector2 = argConnector2;
}


/**
 * Insert the method's description here.
 * Creation date: (1/6/2004 1:44:25 PM)
 * @return cbit.vcell.mapping.component.Connector
 */
public Connector getConnector1() {
	return connector1;
}


/**
 * Insert the method's description here.
 * Creation date: (1/6/2004 1:44:25 PM)
 * @return cbit.vcell.mapping.component.Connector
 */
public Connector getConnector2() {
	return connector2;
}
}