package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;



/**
 * Insert the type's description here.
 * Creation date: (1/22/2006 9:46:58 PM)
 * @author: Jim Schaff
 */
public class Species extends ModelComponent {
/**
 * Reaction constructor comment.
 * @param argName java.lang.String
 */
public Species(String argName, Expression initialConditions) throws ParseException {
	super(argName);
	Variable rate = new Variable("efflux(t)",VCUnitDefinition.UNIT_uM_per_s);
	addSymbol(rate);
	Variable initial = new Variable("conc(0)",VCUnitDefinition.UNIT_uM);
	addSymbol(initial);
	Variable concentration = new Variable("conc(t)",VCUnitDefinition.UNIT_uM);
	addSymbol(concentration);
	
	addEquation(Expression.valueOf("d(conc(t),t) + efflux(t)"));
	addEquation(Expression.valueOf("conc(0) - "+initialConditions.infix()));
	setConnectors(new Connector[] { new Connector(this,"conn",concentration,rate) });
}
}
