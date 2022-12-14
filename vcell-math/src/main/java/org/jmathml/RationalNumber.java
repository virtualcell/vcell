package org.jmathml;

/**
 * Class representing a MathML &lt;cn&gt; element of type "rational".
 */
public class RationalNumber extends ASTNumber {
	private int num, dom;

	/**
	 * It is up to the client to check that the denominator is not zero.
	 * 
	 * @param numerator
	 * @param denominator
	 */
	RationalNumber(int numerator, int denominator) {
		super(ASTNumberType.NUMBER);
		this.num = numerator;
		this.dom = denominator;
		setName(getString());
	}

	@Override
	public String getString() {
		return "(" + num + "/" + dom + ")";
	}

	public int getNumerator() {
		return num;
	}

	public int getDenominator() {
		return dom;
	}

	@Override
	public double getValue() {
		return (double) num / (double) dom;
	}

	public boolean isRational() {
		return true;
	}

}
