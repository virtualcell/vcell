package org.vcell.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (11/18/2005 11:46:49 AM)
 * @author: Jim Schaff
 */
public class Connection {
	private Connector[] connectors = new Connector[0];

/**
 * Connection constructor comment.
 */
public Connection() {
}


/**
 * Connection constructor comment.
 */
public Connection(Connector[] argConnectors) {
	super();
	this.connectors = (Connector[])argConnectors.clone();
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2006 2:04:48 PM)
 * @param connector ncbc.physics2.component.Connector
 */
public void addConnector(Connector connector) {
	if (contains(connector)){
		throw new RuntimeException("connection already contains connector "+connector);
	}
	connectors = (Connector[])cbit.util.BeanUtils.addElement(connectors,connector);
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2006 2:06:22 PM)
 * @return boolean
 * @param connector ncbc.physics2.component.Connector
 */
public boolean contains(Connector connector) {
	for (int i = 0; connectors!=null && i < connectors.length; i++){
		if (connectors[i].equals(connector)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2006 6:15:32 AM)
 * @return ncbc.physics2.component.Connector
 */
public Connector[] getConnectors() {
	return connectors;
}
}