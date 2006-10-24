package org.vcell.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (11/17/2005 3:56:53 PM)
 * @author: Jim Schaff
 */
public class VariableInitial extends VariableReference {
/**
 * VariableInitial constructor comment.
 * @param argVariable ncbc.physics2.Variable
 * @param argName java.lang.String
 */
public VariableInitial(Variable argVariable, String argName) {
	super(argVariable, argName);
	if (!argName.endsWith(INITIAL_SUFFIX)){
		throw new RuntimeException("initialvalue \""+argName+"\" for variable \""+argVariable.getName()+"\" should end with \""+INITIAL_SUFFIX+"\"");
	}
}
}
