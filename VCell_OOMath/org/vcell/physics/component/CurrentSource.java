package org.vcell.physics.component;
import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;


/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:24:50 PM)
 * @author: Jim Schaff
 */
public class CurrentSource extends TwoPortElectricalComponent {
/**
 * CurrentSource constructor comment.
 */
public CurrentSource(String argName, double current) {
	super(argName);
	Parameter IConstant = new Parameter("IConstant", VCUnitDefinition.UNIT_pA);
	addSymbol(IConstant);
	try {
		addEquation(Expression.valueOf("IConstant - Ip(t)"));
		addEquation(Expression.valueOf("IConstant - "+current));
	}catch (ParseException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}