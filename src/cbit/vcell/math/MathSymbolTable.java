package cbit.vcell.math;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

public interface MathSymbolTable extends SymbolTable {

	public Expression substituteFunctions(Expression newExp) throws MathException, ExpressionException;

	public Variable getVariable(String string);

	public MathDescription getMathDescription();

}
