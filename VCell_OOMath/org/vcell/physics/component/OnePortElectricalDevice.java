package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

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
	Variable V = new Variable("V(t)",VCUnitDefinition.UNIT_mV);
	Variable I = new Variable("I(t)",VCUnitDefinition.UNIT_pA);
	addSymbol(V);
	addSymbol(I);
	Connector con = new Connector(this,"con",V,I);
	setConnectors(new Connector[] {con});
}
}
