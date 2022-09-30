package org.jmathml;

/**
 * Encapsulates a MathML piece element. Evaluating this object will return the
 * value to be assigned, if the condition is true. If the condition is false, or
 * this object does not have 2 children, evaluating will return
 * {@link ASTNumber#AST_NULL_NUMBER}
 * 
 * @author radams
 * 
 */
public class ASTPiece extends ASTNode {

	ASTPiece() {
		super(ASTType.PIECE);
		setName("piece");
	}

	@Override
	boolean doAccept(ASTVisitor v) {
		if (v.visit(this)) {
			for (ASTNode n : getChildren()) {
				if (!n.accept(v)) {
					break;
				}
			}
		}
		return v.endVisit(this);
	}

	@Override
	ASTNumber doEvaluate(IEvaluationContext context) {
		if (!hasCorrectNumberChildren()) {
			return ASTNumber.AST_NULL_NUMBER;
		}
		if (getChildAtIndex(1).evaluate(context).isTruth()) {
			return firstChild().evaluate(context);
		} else {
			return ASTNumber.AST_NULL_NUMBER;
		}

	}

	@Override
	public String getString() {
		return "piece";
	}

	/**
	 * This element should have exactly two children.
	 */
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 2;
	}

}
