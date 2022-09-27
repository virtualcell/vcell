package org.jmathml;

/**
 * Root node for ASTNode. In an AST, this is the only node where the parent is
 * null.<br>
 * In this implementation, each root node has only a single child.
 * <p>
 * E.g., to start creating an AST:
 * <p>
 * ASTRootNode root = new ASTRootNode(); ASTPlus = new ASTPlus();
 * root.add(plus);
 * 
 * @author radams
 *
 */
public class ASTRootNode extends ASTNode {
	static final String ROOT_NAME = "root";

	public ASTRootNode() {
		super(ASTType.ROOTNODE);
		setLevel(0);
		setParent(null);
		setName(ROOT_NAME);
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
	public String getString() {
		return "";
	}

	ASTNumber doEvaluate(IEvaluationContext con) {
		return firstChild().evaluate(con);
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 1;
	}
}
