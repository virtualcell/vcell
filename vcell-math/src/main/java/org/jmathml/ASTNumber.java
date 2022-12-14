package org.jmathml;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing nuumeric MathML elements. It contains various factory
 * methods to create AST nodes of various types
 * 
 * @author radams
 *
 */
public abstract class ASTNumber extends ASTNode {

	public enum ASTNumberType implements ASTTypeI {
		PI(1), E(2), NaN(3), INFINITY(4), TRUE(5), FALSE(6), NUMBER(7);
		private int code;

		private ASTNumberType(int code) {
			this.code = code;
		}

		public String getString() {
			switch (code) {
			case 1:
				return "pi";
			case 2:
				return "exponentiale";
			case 3:
				return "notanumber";
			case 4:
				return "infinity";
			case 5:
				return "true";
			case 6:
				return "false";
			default:
				return "";
			}
		}
	}

	static Map<String, ASTNumberType> ENUMNAME_2_ENUM = new HashMap<String, ASTNumberType>();

	static {
		for (ASTNumberType type : ASTNumberType.values()) {
			ENUMNAME_2_ENUM.put(type.name().toLowerCase(), type);
		}
	}

	/**
	 * Given a function name, will return an {@link ASTNumber} for that name, or
	 * <code>null</code> if the number type with that name does not exist.
	 * <p>
	 * This is a convenience method for constructing values from text input.
	 * 
	 * @param constantName A lower-case <code>String</code> corresponding to a number
	 *            constant supported by this library. <br>
	 *            Strings that will return constants are case-insensitive
	 *            variants of:
	 *            <ul>
	 *            <li>pi <li>e <li>nan <li> infinity
	 *            </ul>
	 * @return An {@link ASTNumber}, or null if it does not exist.
	 */
	public static ASTNumber getConstant(String constantName) {
		if (constantName.equalsIgnoreCase("exponentiale")) {
			return E();
		} else if (constantName.equalsIgnoreCase("pi")) {
			return PI();
		} else if (constantName.equalsIgnoreCase("nan")) {
			return NaN();
		} else if (constantName.equalsIgnoreCase("infinity")) {
			return INFINITY();
		} else {
			return null;
		}
	}

	/**
	 * Boolean test for whether the supplied name is a supported constant (exp,
	 * pi, nan or infinity)
	 * @param name the name to test
	 * @return <code>true</code> if is a constant
	 */
	public static boolean isConstant(String name) {
		return getConstant(name) != null;
	}

	public static ASTNumberType getFunctionTypeForName(String fName) {
		return ENUMNAME_2_ENUM.get(fName);
	}

	/**
	 * Represents truth for boolean MathML statements. This has a default value
	 * of '1'
	 * @return An {@link ASTNumber} representing TRUE.
	 */
	public static final ASTNumber TRUE() {
		RealNumber num = new RealNumber(1d);
		num.setType(ASTNumberType.TRUE);
		return num;
	}

	/**
	 * Represents falsity for boolean MathML statements, and has a default value
	 * of '0'
	 * @return An {@link ASTNumber} representing FALSE.
	 */
	public static final ASTNumber FALSE() {
		RealNumber num = new RealNumber(0d);
		num.setType(ASTNumberType.FALSE);
		return num;
	}

	/**
	 * Represents e in MathML statements
	 * @return An {@link ASTNumber} representing E.
	 */
	public static final ASTNumber E() {
		RealNumber num = new RealNumber(Math.E);
		num.setType(ASTNumberType.E);
		return num;
	}

	/**
	 * Represents pi in MathML statements
	 * @return An {@link ASTNumber} representing PI.
	 */
	public static final ASTNumber PI() {
		RealNumber num = new RealNumber(Math.PI);
		num.setType(ASTNumberType.PI);
		return num;
	}

	/**
	 * Represents NaN in MathML statements
	 * @return An {@link ASTNumber} representing NaN.
	 */
	public static final ASTNumber NaN() {
		RealNumber num = new RealNumber(Double.NaN);
		num.setType(ASTNumberType.NaN);
		return num;
	}

	/**
	 * Represents infinity in MathML
	 * @return An {@link ASTNumber} representing Infinity.
	 */
	public static final ASTNumber INFINITY() {
		RealNumber num = new RealNumber(Double.POSITIVE_INFINITY);
		num.setType(ASTNumberType.INFINITY);
		return num;
	}

	/**
	 * Null number when AST cannot be evaluated e.g., due to incorrect
	 * construction of AST.
	 */
	public static final ASTNumber AST_NULL_NUMBER = new RealNumber(Double.NaN);

	/**
	 * Creates an {@link ASTNumber} for a <code>double</code> of type
	 * {@link RealNumber}.
	 * @param n a double
	 * @return A {@link RealNumber}
	 */
	public static ASTNumber createNumber(double n) {
		return new RealNumber(n);
	}

	/**
	 * Creates an ASTNumber of type integer.
	 * @param n A <code>Integer</code>.
	 * @return An {@link ASTNumber}.
	 */
	public static ASTNumber createNumber(int n) {
		return new RealNumber(n);
	}

	/**
	 * Creates an ASTNumber of type integer.
	 * @param n A <code>long</code>.
	 * @return An {@link ASTNumber}.
	 */
	public static ASTNumber createNumber(long n) {
		return new RealNumber(n);
	}

	/**
	 * Creates an {@link ENotationNumber}
	 * 
	 * @param mantissa
	 *            A double
	 * @param exponent
	 *            An int
	 * @return An ASTNumber where isEnotationNumber()== true
	 */
	public static ENotationNumber createNumber(double mantissa, int exponent) {
		return new ENotationNumber(mantissa, exponent);
	}

	/**
	 * Creates a rational number. It is up to the client to test for the
	 * denominator being 0.
	 * 
	 * @param numerator
	 *            a <code>integer</code>
	 * @param denominator
	 *            a <code>integer</code>
	 * @throws IllegalArgumentException
	 *             if <code>denominator</code> is zero.
	 * @return A {@link RationalNumber}
	 */
	public static RationalNumber createNumber(int numerator, int denominator) {
		if (denominator == 0) {
			throw new IllegalArgumentException("denominator is zero");
		}
		return new RationalNumber(numerator, denominator);
	}

	public abstract double getValue();

	boolean doAccept(ASTVisitor v) {
		return v.visit(this);
	}

	ASTNumber(ASTNumberType numberType) {
		super(numberType);
	}

	@Override
	ASTNumber doEvaluate(IEvaluationContext con) {
		return this;
	}

	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 0;
	}

	/*
	 * Returns true, as this is the base class of all numbers (i.e., Cn elements
	 * in MathML) (non-Javadoc)
	 * 
	 * @see model.ASTNode#isNumber()
	 */
	public boolean isNumber() {
		return true;
	}

	/**
	 * Boolean test for whether number is rational (i.e., has numerator or
	 * denominator).
	 * 
	 * @return <code>true</code> if is rational
	 */
	public boolean isRational() {
		return false;
	}

	/**
	 * Boolean test for whether value is integer. This base class returns
	 * <code>false</code>, subclasses can override.
	 * 
	 * @return <code>true</code> if this number is an integer
	 */
	public boolean isInteger() {
		return false;
	}

	/**
	 * Returns numerator, but only if isRational()==true. Default is to throw an
	 * unsupported operation exception: subclasses can override
	 * 
	 * @return the numerator
	 * @throws UnsupportedOperationException
	 *             if isRational()!=true
	 * 
	 */
	public int getNumerator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns denominator, but only if isRational()==true. Default is to throw
	 * an unsupported operation exception: subclasses can override
	 * 
	 * @return the denominator
	 * @throws UnsupportedOperationException
	 *             if isRational()!=true
	 * 
	 */
	public int getDenominator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get exponent of Enotation numbers, for numbers where isENotation() ==true
	 *
	 * @return the exponent of the number.
	 * @throws UnsupportedOperationException
	 *             if isEnotation()!=true
	 */
	public int getExponent() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the mantissa of Enotation numbers, for numbers where isENotation()
	 * ==true
	 * 
	 * @return the mantissa of the number.
	 * @throws UnsupportedOperationException
	 *             if isEnotation()!=true
	 */
	public double getMantissa() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Boolean test as to whether this number holds details in ENotation
	 * 
	 * @return <code>false</code> by default.
	 */
	public boolean isENotation() {
		return false;
	}

	public boolean isTruth() {
		return getType().equals(ASTNumberType.TRUE);
	}

	public boolean isFalse() {
		return getType().equals(ASTNumberType.FALSE);
	}

	public boolean isNaN() {
		return getType().equals(ASTNumberType.NaN);
	}

	public boolean isPI() {
		return getType().equals(ASTNumberType.PI);
	}

	public boolean isInfinity() {
		return getType().equals(ASTNumberType.INFINITY);
	}

	public boolean isE() {
		return getType().equals(ASTNumberType.E);
	}

}
