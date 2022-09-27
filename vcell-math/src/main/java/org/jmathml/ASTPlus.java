package org.jmathml;

public class ASTPlus extends ASTOperator {

	public ASTPlus() {
		super(ASTType.PLUS);
		setName(PLUS_NAME);
	}

	static final String PLUS_NAME = "plus";

	public String getString() {
		return "+";
	}

	ASTNumber doEvaluate(IEvaluationContext con) {
		double currAnswer = 0d;
		for (ASTNode n : getChildren()) {
			currAnswer = currAnswer + n.evaluate(con).getValue();
		}
		return ASTNumber.createNumber(currAnswer);

	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() >= 1;
	}

	public int getPrecedence() {
		return 2;
	}

}
