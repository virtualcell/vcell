package cbit.vcell.math;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

public class ProjectionDataGenerator extends DataGenerator {

	public enum Operation {
		max,
		min,
		avg,
		sum,
	}
	
	public enum Axis {
		x,
		y,
		z,
	}
	
	private Operation operation;
	private Axis axis;
	private Expression function;
	
	public ProjectionDataGenerator(String argName, Domain argDomain, Axis axis, Operation op, Expression function) {
		super(argName, argDomain);
		this.axis = axis;
		this.operation = op;
		if (function == null) {
			throw new IllegalArgumentException("Projection '" + argName + "' does not have an expression.");
		}
		this.function = function;
	}

	public ProjectionDataGenerator(String argName, Domain argDomain, String axisStr, String opStr, Expression function) {
		super(argName, argDomain);
		setAxis(axisStr);
		setOperation(opStr);
		if (function == null) {
			throw new IllegalArgumentException("Projection '" + argName + "' does not have an expression.");
		}
		this.function = function;
	}
	
	public ProjectionDataGenerator(String argName, Domain argDomain, CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		super(argName, argDomain);
		read(tokens);
	}
	
	private void setAxis(String axisStr) {
		try {
			axis = Axis.valueOf(axisStr);
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (Axis axis : Axis.values()) {
				sb.append(axis.name() + ",");
			}
			sb.append(")");
			throw new IllegalArgumentException("invalid value for axis, must be one in " + sb);
		}
	}
	
	private void setOperation(String opStr) {
		try {
			operation = Operation.valueOf(opStr);
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (Operation op : Operation.values()) {
				sb.append(op.name() + ",");
			}
			throw new IllegalArgumentException("invalid value for operation, must be one in " + sb);
		}
	}
	private void read(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}			
			if(token.equalsIgnoreCase(VCML.ProjectionAxis))	{
				token = tokens.nextToken();
				setAxis(token);
			} else if (token.equalsIgnoreCase(VCML.ProjectionOperation)) {
				token = tokens.nextToken();
				setOperation(token);
			} else if (token.equalsIgnoreCase(VCML.Function)) {
				function = new Expression(tokens.readToSemicolon());				
			} else {
				throw new MathFormatException("unexpected identifier "+token);
			}

		}	
	}

	@Override
	public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
		if (function!=null){
			function.bindExpression(symbolTable);
		}
	}

	public Expression getFunction() {
		return function;
	}

	@Override
	public boolean compareEqual(Matchable object, boolean bIgnoreMissingDomain) {
		if (!(object instanceof ProjectionDataGenerator)){
			return false;
		}
		if (!compareEqual0(object,bIgnoreMissingDomain)){
			return false;
		}
		ProjectionDataGenerator pdg = (ProjectionDataGenerator)object;
		if (operation != pdg.operation) {
			return false;
		}
		if (axis != pdg.axis) {
			return false;
		}
		if (!Compare.isEqualOrNull(function, pdg.function)){
			return false;
		}
		return true;
	}

	@Override
	public String getVCML() throws MathException {
		return VCML.ProjectionDataGenerator + "  " + getQualifiedName() + " " + VCML.BeginBlock + "\n"
				+ "\t\t" + VCML.ProjectionAxis + "\t" + axis.name() + "\n"
				+ "\t\t" + VCML.ProjectionOperation + "\t" + operation.name() + "\n"
				+ "\t\t" + VCML.Function + "\t" + function.infix()+";\n"
				+ "\t" + VCML.EndBlock + "\n";
	}

	public final Operation getOperation() {
		return operation;
	}

	public final Axis getAxis() {
		return axis;
	}
	
	

}
