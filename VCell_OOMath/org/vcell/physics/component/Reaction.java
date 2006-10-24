package org.vcell.physics.component;
import java.util.HashSet;

import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

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
public Reaction(String argName, String[] species, int[] stoich, String rateSymbolName, IExpression[] equations) throws org.vcell.expression.ExpressionException {
	super(argName);
	HashSet<String> speciesHash = new HashSet<String>();
	HashSet<String> parameterHash = new HashSet<String>();
	for (int i = 0; i < species.length; i++) {
		speciesHash.add(species[i]);
	}
	for (int i = 0; i < equations.length; i++) {
		String[] symbols = equations[i].getSymbols();
		for (int j = 0; j < symbols.length; j++) {
			// if symbol not in species list, then add as parameter
			if (!speciesHash.contains(symbols[j])){
				parameterHash.add(symbols[j]);
			}
		}
	}
	for (String parameterName : parameterHash) {
		addSymbol(new Variable(parameterName));
	}
	for (int i = 0; i < equations.length; i++) {
		addEquation(equations[i]);
	}
	Symbol rateSymbol = getSymbol(rateSymbolName);
	Connector[] connectors = new Connector[species.length];
	for (int i = 0; i < species.length; i++){
		Variable speciesEffort = new Variable(species[i]);
		addSymbol(speciesEffort);
		Variable speciesRate = new Variable(species[i]+"_rate");
		addSymbol(speciesRate);
		addEquation(ExpressionFactory.createExpression(species[i]+"_rate - "+stoich[i]+"*"+rateSymbolName));
		connectors[i] = new Connector(this,"conn_"+species[i],speciesEffort,speciesRate);
	}
	setConnectors(connectors);
}
}