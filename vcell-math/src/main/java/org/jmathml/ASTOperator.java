package org.jmathml;

/**
 * Abstract class for mathematical operators +,-,*,/
 * 
 * @author radams
 *
 */
public abstract class ASTOperator extends ASTNode {

	public ASTOperator(ASTType type) {
		super(type);
		setOperator(true);
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

	public boolean isPower() {
		return false;
	}

}
