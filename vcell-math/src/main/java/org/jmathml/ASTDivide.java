package org.jmathml;

/**
 * Encapsulates MathML's division operation
 * 
 * @author radams
 *
 */
public class ASTDivide extends ASTOperator {

	public ASTDivide() {
		super(ASTType.DIVIDE);
		setName(DIVIDE_NAME);
	}

	static final String DIVIDE_NAME = "divide";

	public String getString() {
		return "/";
	}

	ASTNumber doEvaluate(IEvaluationContext con) {
		double d = firstChild().evaluate(con).getValue()
				/ getChildAtIndex(1).evaluate(con).getValue();
		return ASTNumber.createNumber(d);
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 2;
	}

	public int getPrecedence() {
		return 3;
	}
}
