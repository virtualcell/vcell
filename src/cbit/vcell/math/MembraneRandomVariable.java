package cbit.vcell.math;

import org.vcell.util.CommentStringTokenizer;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class MembraneRandomVariable extends RandomVariable {
	public MembraneRandomVariable(String name, Expression seed, Distribution dist, Domain domain) {
		super(name, seed, dist, domain);
	}
	
	public MembraneRandomVariable(String name, MathDescription mathDesc, CommentStringTokenizer tokens, Domain domain) throws MathFormatException, ExpressionException {
		super(name, mathDesc, tokens, domain);
	}

	@Override
	protected String getVCMLTag() {
		return VCML.MembraneRandomVariable;
	}
}
