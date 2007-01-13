package cbit.vcell.math;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.IExpression;
import org.vcell.expression.SymbolTable;


/**
 * Used by JumpProcess. Action describes how does a variable change
 * in a specific jump process. The operation can be increase, decrease
 * or assignment. The quantitive change is evaluated by the operand expression.
 * Creation date: (6/21/2006 4:15:02 PM)
 * @author:Tracy LI
 * @see:JumpProcess
 */
public class Action implements cbit.util.Matchable,java.io.Serializable
{
	private Variable var;
	private String operation;
	private IExpression operand;
	
/**
 * Action constructor comment.
 */
public Action(Variable arg_var, String arg_operation, IExpression arg_operand) 
{
	var=arg_var;
	operation=arg_operation;
	operand=arg_operand;
}


/**
 * Bind symboltable to the operand expression.
 * Creation date: (6/22/2006 8:46:17 AM)
 * @param symbolTable SymbolTable
 */
public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException
{
	operand.bindExpression(symbolTable);
	
}


/**
 * Compare two Actions.Return true if two actions are the same.
 * Creation date: (6/22/2006 11:01:21 AM)
 * @return boolean
 */
public boolean compareEqual(cbit.util.Matchable object) 
{
	Action action=(Action) object;
	if(!action.var.compareEqual(var)) return false;//var
	if(action.operation.compareTo(operation)!=0) return false;//operation
	if(!action.operand.compareEqual(operand)) return false;//operand
	return true;
}


/**
 * Get the constant value of operand if it is.
 * Creation date: (6/29/2006 3:17:01 PM)
 * @return double
 */
public double evaluateOperand() throws ExpressionException 
{
	if(operand.isNumeric())
	{
		return operand.evaluateConstant();
	}
	else
	{
		throw new ExpressionException();	
	}
}


/**
 * Get operand value by evaluating it's expression.
 * Creation date: (6/22/2006 8:50:37 AM)
 * @param values double[]
 */
public double evaluateOperand(double[] values) throws ExpressionException
{
	return operand.evaluateVector(values);
}


/**
 * Get operand as an expression.
 * Creation date: (6/21/2006 4:22:53 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getOperand() {
	return operand;
}


/**
 * Get the operation.
 * Creation date: (6/21/2006 4:22:53 PM)
 * @return java.lang.String
 */
public java.lang.String getOperation() {
	return operation;
}


/**
 * Get the variable referenced in the action.
 * Creation date: (6/21/2006 4:22:53 PM)
 * @return cbit.vcell.math.Variable
 */
public Variable getVar() {
	return var;
}


/**
 * Write the instance of the class to VCML
 * Creation date: (7/6/2006 1:31:05 PM)
 */
public String getVCML()
{
	StringBuffer buffer = new StringBuffer();
	int value = 0;
	try
	{
		value = (int)(getOperand().evaluateConstant());
	}catch (ExpressionException e) {e.printStackTrace();}
	buffer.append("\t\t"+VCML.Action+"\t"+getVar().getName()+"\t"+getOperation()+"\t"+value+";\n");
	return buffer.toString();	
}


/**
 * Insert the method's description here.
 * Creation date: (9/28/2006 5:18:07 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	int value = 0;
	try
	{
		value = (int)(getOperand().evaluateConstant());
	}catch (ExpressionException e) {e.printStackTrace();}
	buffer.append(getVar().getName()+"  "+getOperation()+"  "+value);
	return buffer.toString();
}
}