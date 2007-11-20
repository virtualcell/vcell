package org.vcell.physics.component;
import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:26:30 PM)
 * @author: Jim Schaff
 */
public class VoltageSource extends TwoPortElectricalComponent {
/**
 * VoltageSource constructor comment.
 */
public VoltageSource(String argName, double voltage) {
	super(argName);
	Parameter VConstant = new Parameter("VCC",VCUnitDefinition.UNIT_mV);
	addSymbol(VConstant);
	try {
		addEquation(Expression.valueOf("VCC - V"));
		addEquation(Expression.valueOf("VCC - "+voltage));
	}catch (ParseException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}