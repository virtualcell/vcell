package org.jmathml;

public class ASTOtherwise extends ASTNode {

	public ASTOtherwise() {
		super(ASTType.OTHERWISE);
		setName("otherwise");
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
		return firstChild().evaluate(context);
	}

	@Override
	public String getString() {
		return "otherwise";
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 1;
	}

}
