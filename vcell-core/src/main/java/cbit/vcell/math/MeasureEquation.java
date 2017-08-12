package cbit.vcell.math;

import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.CommentStringTokenizer;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;


public abstract class MeasureEquation extends Equation {

	protected MeasureEquation(Variable var) {
		super(var);
	}

	@Override
	protected final void flatten(MathSymbolTable simSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {
		return;  // no expressions
	}

	@Override
	public final Vector<Expression> getExpressions(MathDescription mathDesc) {
		return new Vector<Expression>();
	}

	@Override
	public final Enumeration<Expression> getTotalExpressions() throws ExpressionException {
		return new Vector<Expression>().elements();
	}

	@Override
	public void read(CommentStringTokenizer tokens, MathDescription mathDesc) throws MathFormatException {
		String token = null;
		token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}			
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}			
			throw new MathFormatException("unexpected identifier "+token);
		}		
	}
	
	@Override
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t"+getVCMLName()+" "+getVariable().getName()+" { }\n");
		return buffer.toString();		
	}

	abstract String getVCMLName();

}
