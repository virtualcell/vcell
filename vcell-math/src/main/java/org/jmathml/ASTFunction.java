package org.jmathml;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for unary classical function operators.<br>
 * E.g., trig, ln, log, exp, ceil, floor.
 * 
 * @author radams
 * 
 */
public abstract class ASTFunction extends ASTNode {

	static Map<String, ASTFunctionType> ENUMNAME_2_ENUM = new HashMap<String, ASTFunctionType>();

	static {
		for (ASTFunctionType type : ASTFunctionType.values()) {
			ENUMNAME_2_ENUM.put(type.name().toLowerCase(), type);
		}
	}

	/**
	 * Given a function name, will return the {@link ASTFunctionType} for that
	 * name, or <code>null</code> if the function type with that name does not
	 * exist.
	 * 
	 * @param fName A lower-case <code>String</code> corresponding to a function
	 *            supported by this library. <br>
	 *            E.g., 'sin', 'cos'.
	 * @return An {@link ASTFunctionType}, or null if it does not exist
	 */
	public static ASTFunctionType getFunctionTypeForName(String fName) {
		ASTFunctionType rc;
		rc = ENUMNAME_2_ENUM.get(fName);
		if (rc == null) {
			rc = ASTFunctionType.MISCELLANEOUS;
		}
		return rc;
	}

	public enum ASTFunctionType implements ASTTypeI {
		//
		// the following functions are defined in MathML version 2, but not implemented
		//
		// SECH, CSCH, COTH, ARCCOSH, ARCCOT, ARCCOTH, ARCCSC, ARCCSCH, ARCSEC, ARCSECH, ARCSINH, ARCTANH,
		SIN, COS, TAN, ARCSIN, ARCCOS, ARCTAN, SEC, CSC, COT, SINH, COSH, TANH, LOG, ROOT, LN, EXP, ABS, FLOOR, CEIL, FACTORIAL, MISCELLANEOUS, POWER;

		public String getString() {
			return toString().toLowerCase();
		}
	}

	/**
	 * Tolerance for evaluation of nodes.
	 */
	public static double ABSTOLERANCE = 1e-9;

	ASTFunction(ASTFunctionType type) {
		super(type);
	}

	public static class ASTAbs extends ASTFunction {
		ASTAbs(ASTFunctionType type) {
			super(type);
			setName("abs");
		}

		ASTNumber evaluate0(IEvaluationContext con) {
			return ASTNumber.createNumber(Math
					.abs(getFirstChildEvaluatedValue(con)));
		}

		public String getString() {
			return "abs";
		}
	}

	/**
	 * Represents the factorial element in MathML. Evaluation is limited to
	 * integers; the gamma function is not supported. If the single argument is
	 * a double, it is first converted to an integer.
	 * 
	 * @author radams
	 *
	 */
	public static class ASTFactorial extends ASTFunction {
		ASTFactorial(ASTFunctionType type) {
			super(type);
			setName("factorial");
		}

		/**
		 * CAlculates the factorial of an integer representation of its child
		 * element.
		 * @param con An {@link IEvaluationContext}
		 * @return the factorial of the first child element, or 1 if first child
		 *         element evaluates to < 1.
		 */
		ASTNumber evaluate0(IEvaluationContext con) {
			int facParam = (int) getFirstChildEvaluatedValue(con);
			return ASTNumber.createNumber(factorial(facParam));
		}

		private long factorial(int n) {
			if (n <= 1) // base case
				return 1;
			else
				return n * factorial(n - 1);
		}

		public String getString() {
			return "factorial";
		}
	}

	public static class ASTCeil extends ASTFunction {

		ASTCeil(ASTFunctionType type) {
			super(type);
			setName("ceil");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			return ASTNumber.createNumber(Math
					.ceil(getFirstChildEvaluatedValue(con)));
		}

		@Override
		public String getString() {
			return getName();
		}
	}

	public static class ASTFloor extends ASTFunction {

		ASTFloor(ASTFunctionType type) {
			super(type);
			setName("floor");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			return ASTNumber.createNumber(Math
					.floor(getFirstChildEvaluatedValue(con)));
		}

		@Override
		public String getString() {
			return "floor";
		}
	}

	/**
	 * Constructor for creating a logarithm node to a particular base.
	 * 
	 * @param base
	 *            A positive <code>int</code>
	 * @return A logElement with the set base.
	 * @throws IllegalArgumentException
	 *             if <code>base</code> &lt; 1
	 */
	public static ASTLog createASTLog(int base) {
		if (base < 1) {
			throw new IllegalArgumentException();
		}
		ASTLog log = new ASTFunction.ASTLog(ASTFunctionType.LOG);
		log.addChildNode(ASTNumber.createNumber(base));
		return log;
	}

	/**
	 * Constructor for creating a logarithm node to a particular base.
	 * 
	 * @param degree   A positive <code>int</code>
	 * @return A logElement with the set base.
	 * @throws IllegalArgumentException
	 *             if <code>base</code> &lt; 1
	 */
	public static ASTRoot createASTRoot(int degree) {
		if (degree < 1) {
			throw new IllegalArgumentException();
		}
		ASTRoot root = new ASTFunction.ASTRoot(ASTFunctionType.ROOT);
		root.addChildNode(ASTNumber.createNumber(degree));
		return root;
	}

	/**
	 * Factory class for creating function nodes.
	 * 
	 * @param type  A non-null function type
	 * @return An {@link ASTFunction} object.
	 * @throws IllegalArgumentException
	 *             if <code>type</code> parameter is null.
	 */
	public static ASTFunction createFunctionNode(ASTFunctionType type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		if (type.equals(ASTFunctionType.SIN)) {
			return new ASTSin(type);
		} else if (type.equals(ASTFunctionType.ARCSIN)) {
			return new ASTArcSin(type);
		} else if (type.equals(ASTFunctionType.SINH)) {
			return new ASTSinh(type);
		} else if (type.equals(ASTFunctionType.ARCCOS)) {
			return new ASTArcCos(type);
		} else if (type.equals(ASTFunctionType.COS)) {
			return new ASTCos(type);
		} else if (type.equals(ASTFunctionType.TAN)) {
			return new ASTFunction.ASTTan(type);
		} else if (type.equals(ASTFunctionType.ARCTAN)) {
			return new ASTFunction.ASTArcTan(type);
		} else if (type.equals(ASTFunctionType.TANH)) {
			return new ASTFunction.ASTTanh(type);
		} else if (type.equals(ASTFunctionType.SEC)) {
			return new ASTFunction.ASTSec(type);
		} else if (type.equals(ASTFunctionType.CSC)) {
			return new ASTFunction.ASTCsc(type);
		} else if (type.equals(ASTFunctionType.COT)) {
			return new ASTFunction.ASTCot(type);
		} else if (type.equals(ASTFunctionType.LOG)) {
			return new ASTFunction.ASTLog(type);
		} else if (type.equals(ASTFunctionType.COSH)) {
			return new ASTFunction.ASTCosh(type);
		} else if (type.equals(ASTFunctionType.ROOT)) {
			return new ASTFunction.ASTRoot(type);
		} else if (type.equals(ASTFunctionType.LN)) {
			return new ASTFunction.ASTLn(type);
		} else if (type.equals(ASTFunctionType.EXP)) {
			return new ASTFunction.ASTExp(type);
		} else if (type.equals(ASTFunctionType.FLOOR)) {
			return new ASTFunction.ASTFloor(type);
		} else if (type.equals(ASTFunctionType.CEIL)) {
			return new ASTFunction.ASTCeil(type);
		} else if (type.equals(ASTFunctionType.ABS)) {
			return new ASTFunction.ASTAbs(type);
		} else if (type.equals(ASTFunctionType.FACTORIAL)) {
			return new ASTFunction.ASTFactorial(type);
		} else if (type.equals(ASTFunctionType.POWER)) {
			return new ASTPower();
		} else {
			return new ASTMiscellaneousFunction("misc");
		}
	}

	public boolean isFunction() {
		return true;
	}

	public String toString() {
		return getName();
	}

	public boolean isSqrt() {
		return false;
	}

	/**
	 * Most functions are unary - the default is that one child is the correct
	 * number of arguments.
	 * 
	 * @return <code>true</code> if has 1 child
	 */
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 1;
	}

	/**
	 * @returns an {@link ASTNumber} of the result, or ASTNumber.AST_NULL_NUMBER
	 *          if it cannot be evaluated, or node has no children.
	 */
	ASTNumber doEvaluate(IEvaluationContext con) {
		return evaluate0(con);
	}

	double getFirstChildEvaluatedValue(IEvaluationContext con) {
		return firstChild().evaluate(con).getValue();
	}

	abstract ASTNumber evaluate0(IEvaluationContext con);

	public static class ASTSin extends ASTFunction {
		ASTSin(ASTFunctionType type) {
			super(type);
			setName("sin");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double rc = Math.sin(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "sin";
		}
	}

	/**
	 * Represents a miscellaneous or unknown function; will evaluate to
	 * ASTNumber.NULL_NUMBER
	 * @author radams
	 */
	public static class ASTMiscellaneousFunction extends ASTFunction {
		/**
		 * @param name
		 *            The name of this function
		 */
		ASTMiscellaneousFunction(String name) {
			super(ASTFunctionType.MISCELLANEOUS);
			setName(name);
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			return ASTNumber.AST_NULL_NUMBER;
		}

		@Override
		public String getString() {
			return getName();
		}
	}

	public static class ASTSinh extends ASTFunction {
		ASTSinh(ASTFunctionType type) {
			super(type);
			setName("sinh");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double x = getFirstChildEvaluatedValue(con);
			double rc = (Math.exp(x) - Math.exp(x * -1)) / 2;
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "sinh";
		}
	}

	public static class ASTArcSin extends ASTFunction {
		ASTArcSin(ASTFunctionType type) {
			super(type);
			setName("arcsin");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double rc = Math.asin(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "arcsin";
		}
	}

	public static class ASTCos extends ASTFunction {
		ASTCos(ASTFunctionType type) {
			super(type);
			setName("cos");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double rc = Math.cos(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "cos";
		}
	}

	public static class ASTCosh extends ASTFunction {
		ASTCosh(ASTFunctionType type) {
			super(type);
			setName("cosh");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double x = getFirstChildEvaluatedValue(con);
			double rc = (Math.exp(x) + Math.exp(x * -1)) / 2;
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "cosh";
		}
	}

	public static class ASTArcCos extends ASTFunction {
		ASTArcCos(ASTFunctionType type) {
			super(type);
			setName("arccos");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double rc = Math.acos(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "arccos";
		}
	}

	public static class ASTExp extends ASTFunction {

		ASTExp(ASTFunctionType type) {
			super(type);
			setName("exp");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			return ASTNumber.createNumber(Math
					.exp(getFirstChildEvaluatedValue(con)));
		}

		@Override
		public String getString() {
			return "exp";
		}
	}

	public static class ASTLn extends ASTFunction {

		ASTLn(ASTFunctionType type) {
			super(type);
			setName("ln");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			return ASTNumber.createNumber(Math
					.log(getFirstChildEvaluatedValue(con)));
		}

		public String getString() {
			return "ln";
		}

	}

	public static class ASTTan extends ASTFunction {
		ASTTan(ASTFunctionType type) {
			super(type);
			setName("tan");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double rc = Math.tan(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "tan";
		}
	}

	public static class ASTArcTan extends ASTFunction {

		ASTArcTan(ASTFunctionType type) {
			super(type);
			setName("arctan");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double rc = Math.atan(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "arctan";
		}

	}

	public static class ASTTanh extends ASTFunction {
		ASTTanh(ASTFunctionType type) {
			super(type);
			setName("tanh");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			final int INFINITY_CUTOFF = 500;
			double x = getFirstChildEvaluatedValue(con);
			double e2x = Math.exp(x);
			double e2minusX = Math.exp(x * -1);
			double cosh = (e2x + e2minusX) / 2;
			double sinh = (e2x - e2minusX) / 2;
			if (x > INFINITY_CUTOFF) {
				return ASTNumber.createNumber(1);
			} else if (x < -INFINITY_CUTOFF) {
				return ASTNumber.createNumber(-1);
			} else {
				return ASTNumber.createNumber(sinh / cosh);
			}
		}

		@Override
		public String getString() {
			return "tanh";
		}
	}

	public static class ArcTan extends ASTFunction {
		ArcTan(ASTFunctionType type) {
			super(type);
			setName("arctan");
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double rc = Math.atan(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "arctan";
		}

	}

	/**
	 * ASTLog contains optional 'base' attribute which will be first child. IF
	 * there is only one child number, assume is log base 10 .IEvaluation will
	 * return Double.NaN if nodes cannot be evaluated.
	 * 
	 * @author radams
	 * 
	 */
	public static class ASTLog extends ASTFunction {
		private int base = 10;

		ASTLog(ASTFunctionType type) {
			super(type);
			setName("log");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double answer = Double.NaN;

			if (logHasBaseOtherThan10()) {
				ASTNumber number = getChildAtIndex(1).evaluate(con);
				answer = Math.log(number.getValue())
						/ Math.log(firstChild().evaluate(con).getValue());
			} else {
				answer = Math.log10(((ASTNumber) firstChild().evaluate(con))
						.getValue());
			}
			return ASTNumber.createNumber(answer);
		}

		void setBase(int base) {
			assert (base > 0);
			this.base = base;
			addChildNode(ASTNumber.createNumber(base));
		}

		private boolean logHasBaseOtherThan10() {
			return getNumChildren() > 1;
		}

		// only call if hasBase istrue
		public int getBase() {
			return base;
		}

		/*
		 * 
		 * 
		 * @see model.UnaryFunction#hasCorrectNumberArguments()
		 */
		public boolean hasCorrectNumberChildren() {
			return getNumChildren() == 1 || getNumChildren() == 2;
		}

		@Override
		public String getString() {
			return "log";
		}

		/*
		 * Is log10 if either no base is specified, or base is 10. (non-Javadoc)
		 * 
		 * @see model.ASTNode#isLog10()
		 */
		public boolean isLog10() {
			return getNumChildren() == 1;
		}

	}

	/**
	 * ASTRoot contains optional 'base' attribute which will be first child. IF
	 * there is only one child number, assume is log base 10 .IEvaluation will
	 * return Double.NaN if nodes cannot be evaluated.
	 * 
	 * @author radams
	 * 
	 */
	public static class ASTRoot extends ASTFunction {
		private int degree = 2;

		ASTRoot(ASTFunctionType type) {
			super(type);
			setName("root");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double answer = Double.NaN;

			ASTNumber number = firstChild().evaluate(con);
			if (isSqrt()) {
				answer = Math.sqrt(getRightChild().evaluate(con).getValue());
			} else if (isCbrt(con)) {
				answer = Math.cbrt(getChildAtIndex(1).evaluate(con).getValue());
			} else {
				double degree = number.getValue();
				if (degree == 0) {
					degree = 2;
				}
				answer = Math.pow(getRightChild().evaluate(con).getValue(),
						(double) 1 / number.getValue());
			}

			return ASTNumber.createNumber(answer);
		}

		/*
		 * Can have optional child for the root degree. (non-Javadoc)
		 * 
		 * @see model.UnaryFunction#hasCorrectNumberArguments()
		 */
		public boolean hasCorrectNumberChildren() {
			return getNumChildren() == 1 || getNumChildren() == 2;
		}

		@Override
		public String getString() {
			return "root";
		}

		public boolean isSqrt() {
			return getNumChildren() == 1 || getNumChildren() == 2
					&& firstChild().getName().equals("2");
		}

		boolean isCbrt(IEvaluationContext con) {
			return getNumChildren() == 2
					&& getFirstChildEvaluatedValue(con) - 3 < 0.00001;
		}

	}

	public static class ASTSec extends ASTFunction {
		ASTSec(ASTFunctionType type) {
			super(type);
			setName("sec");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			if (Math.cos(getFirstChildEvaluatedValue(con)) < ABSTOLERANCE) {
				return ASTNumber.createNumber(Double.POSITIVE_INFINITY);
			}
			double rc = 1 / Math.cos(getFirstChildEvaluatedValue(con));
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "1/cos";
		}

	}

	public static class ASTCsc extends ASTFunction {
		ASTCsc(ASTFunctionType type) {
			super(type);
			setName("csc");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double evaluated = getFirstChildEvaluatedValue(con);
			if (Math.sin(evaluated) == 0) {
				return ASTNumber.createNumber(Double.POSITIVE_INFINITY);
			}
			double rc = 1 / Math.sin(evaluated);
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "1/sin";
		}
	}

	public static class ASTCot extends ASTFunction {
		ASTCot(ASTFunctionType type) {
			super(type);
			setName("cot");
			// TODO Auto-generated constructor stub
		}

		@Override
		ASTNumber evaluate0(IEvaluationContext con) {
			double evaluated = getFirstChildEvaluatedValue(con);
			if (Math.sin(evaluated) == 0) {
				return ASTNumber.createNumber(Double.POSITIVE_INFINITY);
			}
			double rc = Math.cos(evaluated) / Math.sin(evaluated);
			return ASTNumber.createNumber(rc);
		}

		@Override
		public String getString() {
			return "1/tan";
		}
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

}
