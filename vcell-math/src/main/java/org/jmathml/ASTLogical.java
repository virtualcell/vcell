package org.jmathml;

public final class ASTLogical extends ASTNode {

	public enum ASTLogicalType implements ASTTypeI {
		AND(1), OR(2), NOT(3), XOR(4);
		private int code;

		private ASTLogicalType(int code) {
			this.code = code;
		}

		public String getString() {
			switch (code) {
			case 1:
				return "&&";
			case 2:
				return "||";
			case 3:
				return "!";
			case 4:
				return "^";
			default:
				return "";
			}
		}
	}

	public ASTLogical(ASTLogicalType type) {
		super(type);
		setName(getType().toString().toLowerCase());
		// TODO Auto-generated constructor stub
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
	ASTNumber doEvaluate(IEvaluationContext con) {
		if (getType().equals(ASTLogicalType.AND)) {
			for (ASTNode node : getChildren()) {
				if (node.evaluate(con).isFalse()) {
					return ASTNumber.FALSE();
				}
			}
			return ASTNumber.TRUE();

		} else if (getType().equals(ASTLogicalType.OR)) {

			for (ASTNode node : getChildren()) {
				if (node.evaluate(con).isTruth()) {
					return ASTNumber.TRUE();
				}
			}
			return ASTNumber.FALSE();
		} else if (getType().equals(ASTLogicalType.NOT)) {

			if (firstChild().evaluate(con).isTruth()) {
				return ASTNumber.FALSE();
			} else {
				return ASTNumber.TRUE();
			}
		} else if (getType().equals(ASTLogicalType.XOR)) {
			int numTrue = 0;
			for (ASTNode node : getChildren()) {
				if (node.evaluate(con).isTruth()) {
					if (++numTrue > 1) {
						return ASTNumber.FALSE();
					}
				}
			}
			return numTrue == 1 ? ASTNumber.TRUE() : ASTNumber.FALSE();
		}
		return ASTNumber.TRUE();

	}

	@Override
	public String getString() {
		return getType().getString();
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getType().equals(ASTLogicalType.NOT) && getNumChildren() == 1
				|| getNumChildren() >= 2;
	}

	@Override
	public boolean isLogical() {
		return true;
	}

}
