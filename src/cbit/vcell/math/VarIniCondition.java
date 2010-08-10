package cbit.vcell.math;
import cbit.vcell.parser.*;
/**
 * The class is tentatively used to store variable's initial value
 * for stochastic simulation. A better solution would be adding one
 * more attribute in class Variable to store it's initial value.
 * It takes Variable and initial value(expression) as input parameters.
 * Creation date: (6/27/2006 9:26:32 AM)
 * @author: Tracy LI
 */
public class VarIniCondition implements org.vcell.util.Matchable,java.io.Serializable
{
	Variable var = null;
	Expression iniVal = null;

/**
 * VarIniCondition constructor comment.
 */
public VarIniCondition(Variable arg_var, Expression arg_iniVal)
{
	var = arg_var;
	iniVal = arg_iniVal;
}


/**
 * Bind symtoltable to the initial value expression.
 * Creation date: (6/27/2006 9:47:12 AM)
 * @param symbolTable SymbolTable
 */
public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException
{
	iniVal.bindExpression(symbolTable);
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj)
{
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof VarIniCondition)) {
		return false;
	}
	
	VarIniCondition varIniCondition = (VarIniCondition) obj;
	if(!iniVal.compareEqual(varIniCondition.iniVal) ) return false;//initial value
	if(!var.compareEqual(varIniCondition.getVar())) return false; //variable
	
	return true;
}


/**
 * Return a constant as initial value if it is.
 * Creation date: (6/29/2006 3:01:29 PM)
 * @return double
 */
public double evaluateIniVal() throws ExpressionException 
{
	if(iniVal.isNumeric())
	{
		return iniVal.evaluateConstant();
	}
	else
	{
		throw new ExpressionException();	
	}
}


/**
 * Get initial value by evaluating it's expression.
 * Creation date: (6/27/2006 9:52:22 AM)
 * @param values double[]
 */
public double evaluateIniVal(double[] values) throws ExpressionException
{
	return iniVal.evaluateVector(values);
}

/**
 * Get variable initial value represented by an expression.
 * Creation date: (6/27/2006 9:42:50 AM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getIniVal() {
	return iniVal;
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
public String getVCML() 
{
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCML.VarIniCondition+"\t"+getVar().getName()+"\t"+getIniVal().infix()+";\n");
	return buffer.toString();
}


/**
 * Set variable's initial value.(expression)
 * Creation date: (6/27/2006 9:42:50 AM)
 * @param newIniVal cbit.vcell.parser.Expression
 */
public void setIniVal(cbit.vcell.parser.Expression newIniVal) {
	iniVal = newIniVal;
}


/**
 * Set variable. (reference)
 * Creation date: (6/27/2006 9:42:50 AM)
 * @param newVar cbit.vcell.math.Variable
 */
public void setVar(Variable newVar) {
	var = newVar;
}


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