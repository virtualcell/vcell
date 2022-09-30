package org.jmathml;

/**
 * Binary operator. This node should have two children with the meaning :
 * <p>
 * pow(firstChild(), getChild(1)) i.e., the expression means the 1st child
 * raised to the second child.
 * 
 * @author radams
 *
 */
public class ASTPower extends ASTFunction {

	public ASTPower() {
		super(ASTFunctionType.POWER);
		setName(POWER_NAME);
	}

	static final String POWER_NAME = "power";

	public String getString() {
		return POWER_NAME;
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 2;
	}

	public int getPrecedence() {
		return 5;
	}

	public boolean isPower() {
		return true;
	}

	@Override
	ASTNumber evaluate0(IEvaluationContext con) {
		double rc = Math.pow(firstChild().evaluate(con).getValue(),
				getChildAtIndex(1).evaluate(con).getValue());
		return ASTNumber.createNumber(rc);
	}

}
