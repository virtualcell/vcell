package cbit.vcell.math;

import java.util.Random;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

public class UniformDistribution extends Distribution {
	private Expression minimum, maximum;

	public UniformDistribution(Expression minExp, Expression maxExp) {
		minimum = new Expression(minExp);
		maximum = new Expression(maxExp);
	}
	
	public UniformDistribution(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		read(tokens);
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
			if(token.equalsIgnoreCase(VCML.UniformDistribution_Minimum))
			{
				minimum = new Expression(tokens);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.UniformDistribution_Maximum)) {
				maximum = new Expression(tokens);
				continue;
			}
			else throw new MathFormatException("unexpected identifier "+token);
		}
		
	}

	@Override
	public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
		minimum.bindExpression(symbolTable);
		maximum.bindExpression(symbolTable);
	}
	
	@Override
	public String getVCML() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t" + VCML.UniformDistribution + " " + VCML.BeginBlock + "\n");
		sb.append("\t\t" + VCML.UniformDistribution_Minimum + " " + minimum.infix() + ";\n");
		sb.append("\t\t" + VCML.UniformDistribution_Maximum + " " + maximum.infix() + ";\n");
		sb.append("\t" + VCML.EndBlock + "\n");
		
		return sb.toString();
	}

	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof UniformDistribution)){
			return false;
		}
		
		UniformDistribution ud = (UniformDistribution)obj;			
		if (!Compare.isEqual(minimum, ud.minimum)) {
			return false;
		}
		if (!Compare.isEqual(maximum, ud.maximum)) {
			return false;
		}
		
		return true;
	}

	public final Expression getMinimum() {
		return minimum;
	}

	public final Expression getMaximum() {
		return maximum;
	}
}