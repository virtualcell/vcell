package org.vcell.physics.component;
import java.util.HashSet;

import org.vcell.expression.ExpressionUtilities;
import org.vcell.physics.math.MathSystem;

import cbit.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

/**
 * Insert the type's description here.
 * Creation date: (1/22/2006 9:46:58 PM)
 * @author: Jim Schaff
 */
public class Reaction extends ModelComponent {
/**
 * Reaction constructor comment.
 * @param argName java.lang.String
 */
public Reaction(String argName, String[] species, int[] stoich, String rateSymbolName, Expression[] equations) throws ParseException {
	super(argName);
	HashSet<String> knownSymbolHash = new HashSet<String>();
	HashSet<String> parameterHash = new HashSet<String>();
	for (int i = 0; i < species.length; i++) {
		knownSymbolHash.add(species[i]+"(t)");
	}
	knownSymbolHash.add("t");
	knownSymbolHash.add("x");
	knownSymbolHash.add("y");
	knownSymbolHash.add("z");
	
	for (int i = 0; i < equations.length; i++) {
		jscl.plugin.Variable[] variables = Expression.getVariables(equations[i]);
		for (int j = 0; j < variables.length; j++) {
			// if symbol not in species list, then add as parameter
			if (!knownSymbolHash.contains(variables[j].infix())){
				parameterHash.add(variables[j].infix());
			}
		}
	}
	for (String parameterName : parameterHash) {
		addSymbol(new Variable(parameterName,VCUnitDefinition.UNIT_TBD));
	}
	for (int i = 0; i < equations.length; i++) {
		addEquation(equations[i]);
	}
	Connector[] connectors = new Connector[species.length];
	for (int i = 0; i < species.length; i++){
		Variable speciesEffort = new Variable(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"(t)"),VCUnitDefinition.UNIT_uM);
		addSymbol(speciesEffort);
		Variable speciesRate = new Variable(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"_rate(t)"),VCUnitDefinition.UNIT_uM_per_s);
		addSymbol(speciesRate);
		Expression eqn = Expression.valueOf(ExpressionUtilities.getEscapedTokenJSCL(species[i]+"_rate(t)"))
									.subtract(Expression.valueOf(stoich[i]+"*"+ExpressionUtilities.getEscapedTokenJSCL(rateSymbolName+"(t)")));
		addEquation(eqn);
		connectors[i] = new Connector(this,"conn_"+species[i],speciesEffort,speciesRate);
	}
	setConnectors(connectors);
}
}