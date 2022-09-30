package org.jmathml;

public class ASTPiecewise extends ASTNode {

	ASTPiecewise() {
		super(ASTType.PIECEWISE);
		setName("piecewise");
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
		for (ASTNode child : getChildren()) {
			if (!isValidChild(child)) {
				continue;
			}
			if (child.getType().equals(ASTType.PIECE)) {
				ASTNumber rc = child.evaluate(context);
				if (!rc.equals(ASTNumber.AST_NULL_NUMBER)) {
					return rc;
				}
			}
		}
		for (ASTNode n : getChildren()) {
			if (n.getType().equals(ASTType.OTHERWISE)) {
				return n.firstChild().evaluate(context);
			}
		}
		return ASTNumber.AST_NULL_NUMBER;
	}

	private boolean isValidChild(ASTNode n) {
		return n.getType().equals(ASTType.PIECE)
				|| n.getType().equals(ASTType.OTHERWISE);
	}

	@Override
	public String getString() {
		return "piecewise";
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() > 0;
	}

}
