package org.vcell.physics.component;
import org.vcell.expression.ExpressionUtilities;

import cbit.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;


/**
 * Insert the type's description here.
 * Creation date: (1/22/2006 9:46:58 PM)
 * @author: Jim Schaff
 */
public class MassActionReaction extends ModelComponent {
/**
 * Reaction constructor comment.
 * @param argName java.lang.String
 */
public MassActionReaction(String argName, String[] species, int[] stoich, boolean bFast) throws ParseException {
	super(argName);
	if (!bFast){
		Variable rate = new Variable("rate(t)",VCUnitDefinition.UNIT_uM_per_s);
		addSymbol(rate);
		Variable kon = new Variable("kon",VCUnitDefinition.UNIT_per_s);
		Variable koff = new Variable("koff",VCUnitDefinition.UNIT_per_s);
		addSymbol(kon);
		addSymbol(koff);
		Expression forwardRate = Expression.valueOf("kon");
		Expression reverseRate = Expression.valueOf("koff");

		Connector[] connectors = new Connector[species.length];
		for (int i = 0; i < species.length; i++){
			Variable speciesEffort = new Variable(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"(t)"),VCUnitDefinition.UNIT_uM);
			addSymbol(speciesEffort);
			Variable speciesRate = new Variable(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"_rate(t)"),VCUnitDefinition.UNIT_uM_per_s);
			addSymbol(speciesRate);
			addEquation(Expression.valueOf(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"_rate(t)")).subtract(Expression.valueOf(stoich[i]+"*rate(t)")));
			connectors[i] = new Connector(this,ExpressionUtilities.getEscapedTokenJSCL("conn_"+species[i]),speciesEffort,speciesRate);
			if (stoich[i] > 0){
				reverseRate = (Expression)reverseRate.multiply(Expression.valueOf(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"(t)")).pow(stoich[i]));
			}else if (stoich[i] < 0){
				forwardRate = (Expression)forwardRate.multiply(Expression.valueOf(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"(t)")).pow(-stoich[i]));
			}
		}
		addEquation(Expression.valueOf("rate(t)").subtract(forwardRate).add(reverseRate));
		addEquation(Expression.valueOf("kon-1"));
		addEquation(Expression.valueOf("koff-1"));
		setConnectors(connectors);
	}else{ // fast
		Variable rate = new Variable("rate(t)",VCUnitDefinition.UNIT_uM_per_s);
		addSymbol(rate);
		Variable kon = new Variable("kon",VCUnitDefinition.UNIT_per_s);
		Variable koff = new Variable("koff",VCUnitDefinition.UNIT_per_s);
		addSymbol(kon);
		addSymbol(koff);
		Expression forwardRate = Expression.valueOf("kon");
		Expression reverseRate = Expression.valueOf("koff");

		Connector[] connectors = new Connector[species.length];
		for (int i = 0; i < species.length; i++){
			Variable speciesEffort = new Variable(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"(t)"),VCUnitDefinition.UNIT_uM);
			addSymbol(speciesEffort);
			Variable speciesRate = new Variable(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"_rate(t)"),VCUnitDefinition.UNIT_uM_per_s);
			addSymbol(speciesRate);
			addEquation(Expression.valueOf(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"_rate(t)")).subtract(Expression.valueOf(stoich[i]+"*rate(t)")));
			connectors[i] = new Connector(this,ExpressionUtilities.getEscapedTokenJSCL("conn_"+species[i]),speciesEffort,speciesRate);
			if (stoich[i] > 0){
				reverseRate = (Expression)reverseRate.multiply(Expression.valueOf(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"(t)")).pow(stoich[i]));
			}else if (stoich[i] < 0){
				forwardRate = (Expression)forwardRate.multiply(Expression.valueOf(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"(t)")).pow(-stoich[i]));
			}
		}
		addEquation(Expression.valueOf("rate(t)").subtract(forwardRate).add(reverseRate));
		addEquation(Expression.valueOf("kon-1"));
		addEquation(Expression.valueOf("koff-1"));
		//addEquation(new Expression("rate-0"));
		setConnectors(connectors);
	}
}
}