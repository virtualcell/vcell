package org.vcell.physics.component;
import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:21:27 PM)
 * @author: Jim Schaff
 */
public class TwoPortElectricalComponent extends ModelComponent {
	public static final String CONNECTOR_POS = "pos";
	public static final String CONNECTOR_NEG = "neg";
	
/**
 * TwoPortElectricalComponent constructor comment.
 */
public TwoPortElectricalComponent(String argName) {
	super(argName);
	Variable Vp = new Variable("Vp(t)",VCUnitDefinition.UNIT_mV);
	Variable Vn = new Variable("Vn(t)",VCUnitDefinition.UNIT_mV);
	Variable Ip = new Variable("Ip(t)",VCUnitDefinition.UNIT_pA);
	Variable In = new Variable("In(t)",VCUnitDefinition.UNIT_pA);
	Variable V = new Variable("V(t)",VCUnitDefinition.UNIT_mV);
	addSymbol(Vp);
	addSymbol(Vn);
	addSymbol(Ip);
	addSymbol(In);
	addSymbol(V);
	Connector conPos = new Connector(this,CONNECTOR_POS,Vp,Ip);
	Connector conNeg = new Connector(this,CONNECTOR_NEG,Vn,In);
	setConnectors(new Connector[] {conPos, conNeg});
	try {
		addEquation(Expression.valueOf("(Vp(t)-Vn(t)) - V(t)"));
		addEquation(Expression.valueOf("Ip(t) + In(t)"));
	}catch (ParseException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}