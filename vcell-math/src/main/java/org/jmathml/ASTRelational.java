package org.jmathml;

import org.jmathml.ASTRelational.ASTRelationalType;

/**
 * Encapsulates ASTNodes representing relations in MathML: <br>
 * eq, neq, gt, lt, geq, leq.<br>
 * 
 * @author radams
 *
 */
public class ASTRelational extends ASTNode {

	public static final double TOLERANCE = 0.0001;

	public enum ASTRelationalType implements ASTTypeI {
		EQ(1), NEQ(2), GT(3), GEQ(4), LT(5), LEQ(6);
		private int code;

		private ASTRelationalType(int code) {
			this.code = code;
		}

		public String getString() {
			switch (code) {
			case 1:
				return "==";
			case 2:
				return "!=";
			case 3:
				return ">";
			case 4:
				return ">=";
			case 5:
				return "<";
			case 6:
				return "<=";
			default:
				return "";
			}
		}

	}

	public ASTRelational(ASTRelationalType type) {
		super(type);
		setName(getType().toString().toLowerCase());
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

	/**
	 * Evaluates a relationship expression. The basic semantics are that: <br/>
	 * <ul>
	 * <li>An empty expression returns <code>ASTNumber.FALSE</code>
	 * <li>An expression with a single child returns
	 * <code>ASTNumber.FALSE</code> (unless this node is of type
	 * {@link ASTRelationalType.EQ}), in which case this method returns
	 * ASTNumber.TRUE.
	 * <li>An expression with > 1 child is tested for the relation.
	 * </ul>
	 * 
	 * @param an
	 *            {@link IEvaluationContext}
	 * @return {@link ASTNumber.TRUTH} if relation is true,
	 *         code>ASTNumber.FALSE</code> otherwise.
	 */
	@Override
	ASTNumber doEvaluate(IEvaluationContext con) {
		if (getType().equals(ASTRelationalType.EQ)) {
			return test(con, EQ);
		} else if (getType().equals(ASTRelationalType.GEQ)) {
			return test(con, GET);
		} else if (getType().equals(ASTRelationalType.GT)) {
			return test(con, GT);
		} else if (getType().equals(ASTRelationalType.LT)) {
			return test(con, LT);
		} else if (getType().equals(ASTRelationalType.LEQ)) {
			return test(con, LEQ);
		} else if (getType().equals(ASTRelationalType.NEQ)) {
			return test(con, NE);
		}
		return ASTNumber.FALSE();
	}

	private ASTNumber test(IEvaluationContext con, Tester tester) {
		ASTNode currChild = firstChild();
		if (getNumChildren() == 1) {
			return getType().equals(ASTRelationalType.EQ) ? ASTNumber.TRUE()
					: ASTNumber.FALSE();
		}
		// test 1st and 2nd child, then 2nd & 3rd child etc.,- > if any relation
		// false then return false
		for (int i = 1; i < getNumChildren(); i++) {
			if (tester.test(currChild, getChildAtIndex(i), con)) {
				currChild = getChildAtIndex(i);
				continue;
			}
			return ASTNumber.FALSE();
		}
		return ASTNumber.TRUE();
	}

	/*
	 * Internal interface for relationship comparisons during evaluation.
	 */
	interface Tester {
		boolean test(ASTNode num1, ASTNode num2, IEvaluationContext con);
	}

	Tester GET = new Tester() {
		public boolean test(ASTNode num1, ASTNode num2, IEvaluationContext con) {
			return num1.evaluate(con).getValue() >= num2.evaluate(con)
					.getValue();
		}
	};
	Tester GT = new Tester() {
		public boolean test(ASTNode num1, ASTNode num2, IEvaluationContext con) {
			return num1.evaluate(con).getValue() > num2.evaluate(con)
					.getValue();
		}
	};

	Tester EQ = new Tester() {
		public boolean test(ASTNode num1, ASTNode num2, IEvaluationContext con) {
			return Math.abs(num1.evaluate(con).getValue()
					- num2.evaluate(con).getValue()) < TOLERANCE;
		}
	};

	Tester LEQ = new Tester() {
		public boolean test(ASTNode num1, ASTNode num2, IEvaluationContext con) {
			return num1.evaluate(con).getValue() <= num2.evaluate(con)
					.getValue();
		}
	};

	Tester LT = new Tester() {
		public boolean test(ASTNode num1, ASTNode num2, IEvaluationContext con) {
			return num1.evaluate(con).getValue() < num2.evaluate(con)
					.getValue();
		}
	};

	Tester NE = new Tester() {
		public boolean test(ASTNode num1, ASTNode num2, IEvaluationContext con) {
			return Math.abs(num1.evaluate(con).getValue()
					- num2.evaluate(con).getValue()) > TOLERANCE;
		}
	};

	@Override
	public String getString() {
		return getType().getString();
	}

	@Override
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() >= 2;
	}

	@Override
	public boolean isRelational() {
		return true;
	}

}
