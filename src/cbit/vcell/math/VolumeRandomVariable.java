package cbit.vcell.math;

import org.vcell.util.CommentStringTokenizer;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class VolumeRandomVariable extends RandomVariable {
	public VolumeRandomVariable(String name, Expression seed, Distribution dist, Domain domain) {
		super(name, seed, dist, domain);
	}
	
	public VolumeRandomVariable(String name, MathDescription mathDesc, CommentStringTokenizer tokens, Domain domain) throws MathFormatException, ExpressionException {
		super(name, mathDesc, tokens, domain);
	}

	@Override
	protected String getVCMLTag() {
		return VCML.VolumeRandomVariable;
	}
}
