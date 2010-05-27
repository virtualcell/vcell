package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 ©*/
/**
 * A <code>SymbolTableEntry</code> describes an interface for any object that
 * can represent the value corresponding to a symbol in an expression.  
 * A <code>SymbolTableEntry</code> is returned by a <code>SymbolTable</code>
 * to resolve the reference of a symbol defined in an <code>Expression</code> 
 * or a <code>ReactionParticipant</code>
 * <p>
 */
public interface SymbolTableFunctionEntry extends SymbolTableEntry {

	public interface Differentiable {
		Expression differentiate(Expression args[], String variable) throws ExpressionException;
	}

	public interface Evaluable {
		double evaluateConstant(Expression[] arguments) throws ExpressionException;
		double evaluateVector(Expression[] arguments, double[] vectorValues) throws ExpressionException;
	}

enum FunctionArgType {
	NUMERIC,
	LITERAL
};

public int getNumArguments();
public FunctionArgType[] getArgTypes();
public String[] getArgNames();
public String getFunctionName();

}
