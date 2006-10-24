package org.vcell.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (11/17/2005 3:55:16 PM)
 * @author: Jim Schaff
 */
public class VariableDerivative extends VariableReference {
/**
 * VariableDerivative constructor comment.
 * @param argVariable ncbc.physics2.Variable
 * @param argName java.lang.String
 */
public VariableDerivative(Variable argVariable, String argDerivativeName){
	super(argVariable, argDerivativeName);
	if (!argDerivativeName.endsWith(DERIVATIVE_SUFFIX)){
		throw new RuntimeException("derivative \""+argDerivativeName+"\" for variable \""+argVariable.getName()+"\" should end with \""+DERIVATIVE_SUFFIX+"\"");
	}
} 
}
