package cbit.vcell.field;

import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;

public class FieldFunctionDefinition extends SimpleSymbolTableFunctionEntry implements SymbolTableFunctionEntry.Differentiable {
	
	public final static String FUNCTION_name				= "vcField";
	public final static FieldFunctionDefinition fieldFunctionDefinition = new FieldFunctionDefinition();
	
	private FieldFunctionDefinition() {
		super("vcField", 
				new String[]{"DatasetName","VariableName","Time","VariableType"}, 
				new FunctionArgType[] { FunctionArgType.LITERAL, FunctionArgType.LITERAL, FunctionArgType.NUMERIC, FunctionArgType.LITERAL }, 
				null,  // expression 
				null,  // units
				null); // namescope
	}

	public Expression differentiate(Expression[] args, String variable) throws ExpressionException {
		if (variable==null){
			throw new RuntimeException("cannot differentiate with respect to a null variable");
		}
		if (args.length!=4){
			throw new IllegalArgumentException("expecting 4 arguments for vcField()");
		}
		if (variable.equals(ReservedVariable.X.getName()) || variable.equals(ReservedVariable.Y.getName()) || variable.equals(ReservedVariable.Z.getName())){
			throw new ExpressionException("differentiation with respect to x,y,z not supported for fields");
		}
		Expression timeArgument = args[2];
		Expression exp = timeArgument.differentiate(variable);
		if (exp.isZero()){
			return new Expression(0.0);
		}else{
			throw new ExpressionException("differentiation with respect to '"+variable+"' not supported for function vcField() with time argument '"+timeArgument.infix()+"'");
		}
	}

}
