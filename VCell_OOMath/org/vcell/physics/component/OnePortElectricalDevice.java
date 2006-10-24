package org.vcell.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/18/2006 12:56:55 PM)
 * @author: Jim Schaff
 */
public class OnePortElectricalDevice extends ModelComponent {
/**
 * Ground constructor comment.
 * @param argName java.lang.String
 */
public OnePortElectricalDevice(String argName) {
	super(argName);
	Variable V = new Variable("V");
	Variable I = new Variable("I");
	addSymbol(V);
	addSymbol(I);
	Connector con = new Connector(this,"con",V,I);
	setConnectors(new Connector[] {con});
}
}
