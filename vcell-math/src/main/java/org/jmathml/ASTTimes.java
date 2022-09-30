package org.jmathml;

public class ASTTimes extends ASTOperator {

	static final String TIMES_NAME = "times";

	public ASTTimes() {
		super(ASTType.TIMES);
		setName(TIMES_NAME);
	}

	public String getString() {
		return "*";
	}

	ASTNumber doEvaluate(IEvaluationContext con) {
		double currAnswer = 1d;
		for (ASTNode n : getChildren()) {
			currAnswer = currAnswer * n.evaluate(con).getValue();
		}
		return ASTNumber.createNumber(currAnswer);
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() >= 1;
	}

	public int getPrecedence() {
		return 3;
	}

}
