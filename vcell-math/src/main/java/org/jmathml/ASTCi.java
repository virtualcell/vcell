package org.jmathml;

/**
 * Represents a MathML &lt;ci&gt; element.
 * @author radams
 */
public final class ASTCi extends ASTNode {
	private final String identifier;

	public enum AST_IDENTIFIABLE_TYPE implements ASTTypeI {
		AST_IDENTIFIABLE;

		public String getString() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public final boolean isVector() {
		ASTNode parent = getParentNode();
		if (parent == null) {
			return false;
		}
		while (parent != null) {
			if (parent.isSymbol() && ((ASTSymbol) parent).isVectorOperation()) {
				return true;
			}
			parent = parent.getParentNode();
		}
		return false;
	}

	/**
	 * @param identifier
	 *            the value of this element.
	 */
	public ASTCi(String identifier) {
		super(AST_IDENTIFIABLE_TYPE.AST_IDENTIFIABLE);
		this.identifier = identifier;
		setName(identifier);
	}

	@Override
	boolean doAccept(ASTVisitor v) {
		return v.visit(this);
	}

	@Override
	ASTNumber doEvaluate(IEvaluationContext con) {
		return ASTNumber.createNumber(con.getValueFor(identifier).iterator()
				.next());
	}

	public boolean isVariable() {
		return true;
	}

	@Override
	public String getString() {
		return identifier;
	}

	@Override
	/**
	 * This node should have no child nodes.
	 */
	public boolean hasCorrectNumberChildren() {
		return getChildren().size() == 0;
	}
}
