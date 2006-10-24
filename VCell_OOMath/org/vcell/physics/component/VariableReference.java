package org.vcell.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (11/17/2005 3:51:55 PM)
 * @author: Jim Schaff
 */
public abstract class VariableReference extends Symbol {
	Variable variable = null;
		protected VariableReference(Variable argVariable, String argName){
			super(argName);
			variable = argVariable;
		}
/**
 * Insert the method's description here.
 * Creation date: (1/3/2006 11:53:49 AM)
 * @return ncbc.physics2.component.Variable
 */
public Variable getVariable() {
	return variable;
}
}
