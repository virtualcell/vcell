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
//	public final static String CONNECTOR_NUM = "numConn";
	public final static String CONNECTOR_CONC = "concConn";
	public final static String CONNECTOR_SIZE = "sizeConn";
/**
 * Reaction constructor comment.
 * @param argName java.lang.String
 */
public Species(String argName, Expression initialAmount) throws ParseException {
	super(argName);
	Parameter KMOLE = new Parameter("KMOLE",VCUnitDefinition.UNIT_uM_um3_per_molecules);
	addSymbol(KMOLE);
	Variable compartmentSize = new Variable("compartmentSize(t)",VCUnitDefinition.UNIT_um3);
	addSymbol(compartmentSize);
	Variable concentration = new Variable("conc(t)",VCUnitDefinition.UNIT_uM);
	addSymbol(concentration);
	Variable initial = new Variable("num(0)",VCUnitDefinition.UNIT_molecules);
	addSymbol(initial);
	Variable num = new Variable("num(t)",VCUnitDefinition.UNIT_molecules);
	addSymbol(num);
	Variable numRate = new Variable("numRate(t)",VCUnitDefinition.UNIT_molecules_per_s);
	addSymbol(numRate);
	
//	addEquation(Expression.valueOf("d(conc(t),t) + efflux(t)"));
	addEquation(Expression.valueOf("num(0) - ("+initialAmount.infix()+")"));
	addEquation(Expression.valueOf("conc(t) - KMOLE*num(t)/compartmentSize(t)"));
	addEquation(Expression.valueOf("KMOLE - "+(1.0/602.0)));
	addEquation(Expression.valueOf("d(num(t),t) + numRate(t)"));
	Connector concConn = new Connector(this,CONNECTOR_CONC,concentration,numRate);
	Connector sizeConn = new Connector(this,CONNECTOR_SIZE,compartmentSize,null);
	setConnectors(new Connector[] { concConn, sizeConn });
}
}
