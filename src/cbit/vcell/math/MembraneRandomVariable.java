package cbit.vcell.math;

import org.vcell.util.CommentStringTokenizer;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class MembraneRandomVariable extends RandomVariable {
	public MembraneRandomVariable(String name, Expression seed, Distribution dist) {
		super(name, seed, dist);
	}
	
	public MembraneRandomVariable(String name, MathDescription mathDesc, CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		super(name, mathDesc, tokens);
	}

	@Override
	protected String getVCMLTag() {
		return VCML.MembraneRandomVariable;
	}
}
