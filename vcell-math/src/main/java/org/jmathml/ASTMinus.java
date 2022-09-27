package org.jmathml;

public class ASTMinus extends ASTOperator {

	public ASTMinus() {
		super(ASTType.MINUS);
		setName(MINUS_NAME);
	}

	int type = 0;
	static final int UNARY = 1;
	static final int BINARY = 2;

	static final String MINUS_NAME = "minus";

	public String getString() {
		return "-";
	}

	public ASTNumber doEvaluate(IEvaluationContext con) {
		double currAnswer = 0d;
		if (getNumChildren() == 2) {
			currAnswer = firstChild().evaluate(con).getValue()
					- getRightChild().evaluate(con).getValue();
		} else {
			currAnswer = firstChild().evaluate(con).getValue() * -1;
		}
		return ASTNumber.createNumber(currAnswer);

	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() >= 1 && getNumChildren() <= 2;
	}

	boolean isUnary() {
		return getNumChildren() == 1;
	}

	public int getPrecedence() {
		return isUnary() ? 5 : 2;
	}

	public ASTTypeI getType() {
		return isUnary() ? ASTType.UMINUS : ASTType.MINUS;
	}

}
