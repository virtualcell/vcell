package cbit.vcell.math;
import org.vcell.util.Matchable;

import cbit.vcell.parser.*;
/**
 * The class is tentatively used to store variable's initial value
 * for stochastic simulation. A better solution would be adding one
 * more attribute in class Variable to store it's initial value.
 * It takes Variable and initial value(expression) as input parameters.
 * Creation date: (6/27/2006 9:26:32 AM)
 * @author: Tracy LI
 */
public abstract class VarIniCondition implements org.vcell.util.Matchable,java.io.Serializable
{
	private Variable var = null;
	private Expression iniValue = null;

/**
 * VarIniCondition constructor comment.
 */
public VarIniCondition(Variable arg_var, Expression arg_iniVal)
{
	var = arg_var;
	iniValue = arg_iniVal;
}


/**
 * Bind symtoltable to the initial value expression.
 * Creation date: (6/27/2006 9:47:12 AM)
 * @param symbolTable SymbolTable
 */
public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException
{
	iniValue.bindExpression(symbolTable);
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj)
{
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof VarIniCondition)) {
		return false;
	}
	
	VarIniCondition varIniCondition = (VarIniCondition) obj;
	if(!iniValue.compareEqual(varIniCondition.iniValue) ) return false;//initial value
	if(!var.compareEqual(varIniCondition.getVar())) return false; //variable
	
	return true;
}

/**
 * Get variable initial value represented by an expression.
 * Creation date: (6/27/2006 9:42:50 AM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getIniVal() {
	return iniValue;
}


/**
 * Get the variable.
 * Creation date: (6/27/2006 9:42:50 AM)
 * @return cbit.vcell.math.Variable
 */
public Variable getVar() {
	return var;
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/2006 5:42:05 PM)
 * @return java.lang.String
 */
public abstract String getVCML();

/**
 * Insert the method's description here.
 * Creation date: (9/28/2006 3:05:32 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	String initialValue = getIniVal().infix(); // display the constant/function name only
	
	buffer.append(getVar().getName()+" = "+initialValue);
	return buffer.toString();
}
}