package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 10:09:54 AM)
 * @author: Jim Schaff
 */
public abstract class Device {
	private String name = null;
	private Location location = null;
	private Connector connectors[] = new Connector[0];
	private Equation equations[] = new Equation[0];
	private Identifier identifiers[] = new Identifier[0];

/**
 * Insert the method's description here.
 * Creation date: (1/7/2004 8:58:41 AM)
 * @param argName java.lang.String
 */
Device(String argName, Location argLocation) {
	this.name = argName;
	this.location = argLocation;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 3:34:15 PM)
 * @param connector ncbc.physics.component.Connector
 */
void addConnector(Connector connector) {
	this.connectors = (Connector[])cbit.util.BeanUtils.addElement(connectors,connector);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:30:43 PM)
 * @param equation ncbc.physics.component.Equation
 */
void addEquation(Equation equation) {
	this.equations = (Equation[])cbit.util.BeanUtils.addElement(equations,equation);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 3:34:15 PM)
 * @param identifier ncbc.physics.component.Identifier
 */
void addIdentifier(Identifier identifier) {
	this.identifiers = (Identifier[])cbit.util.BeanUtils.addElement(identifiers,identifier);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:45:28 PM)
 * @return ncbc.physics.component.Port
 * @param argName java.lang.String
 */
public Connector getConnectorByName(String argName) {
	for (int i = 0; connectors!=null && i < connectors.length; i++){
		if (connectors[i].getName().equals(argName)){
			return connectors[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/6/2004 10:12:06 AM)
 * @return cbit.vcell.mapping.component.DevicePin[]
 */
public final Connector[] getConnectors() {
	return connectors;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:30:05 PM)
 * @return ncbc.physics.component.Equation[]
 */
public Equation[] getEquations() {
	return equations;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:45:28 PM)
 * @return ncbc.physics.component.Port
 * @param argName java.lang.String
 */
public Identifier getIdentifierByName(String argName) {
	for (int i = 0; identifiers!=null && i < identifiers.length; i++){
		if (identifiers[i].getName().equals(argName)){
			return identifiers[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:32:16 PM)
 * @return ncbc.physics.component.Identifier
 */
public Identifier[] getIdentifiers() {
	return identifiers;
}


/**
 * Insert the method's description here.
 * Creation date: (1/7/2004 6:40:31 AM)
 * @return cbit.vcell.model.Structure
 */
public final Location getLocation(){
	return location;
}


/**
 * Insert the method's description here.
 * Creation date: (1/6/2004 10:10:27 AM)
 * @return java.lang.String
 */
public final String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 10:32:08 PM)
 */
public void show() {
	System.out.println(toString()+": "+name+" @ "+location);
	//private String name = null;
	//private Location location = null;
	//private Connector connectors[] = new Connector[0];
	//private Equation equations[] = new Equation[0];
	//private Identifier identifiers[] = new Identifier[0];
	

}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 10:35:28 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName() + "@" + Integer.toHexString(hashCode())+": "+name+" in "+location.getName();
}
}