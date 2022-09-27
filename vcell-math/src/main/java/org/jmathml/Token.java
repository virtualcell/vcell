package org.jmathml;

/*
 * Represents a single token after parsing
 */
class Token {

	Token(String string) {
		super();

		this.string = string;
	}

	private String string;

	String getString() {
		return string;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return true;
	}

	boolean isArithmeticOperator() {
		return string.equals("+") || string.equals("-") || string.equals("*")
				|| string.equals("/") || string.equals("^");
	}

	boolean isRelationalOperator() {
		return string.equals(">") || string.equals("<") || string.equals(">=")
				|| string.equals("<=") || string.equals("==")
				|| string.equals("!=");
	}

	boolean isLogicalOperator() {
		return string.equals("&&") || string.equals("||") || string.equals("!");
	}

	boolean isOperator() {
		return isArithmeticOperator() || isRelationalOperator()
				|| isLogicalOperator() || isArgSeparator();
	}

	private boolean isArgSeparator() {
		return string.equals(",");
	}

	boolean isLPar() {
		return string.equals("(");
	}

	boolean isRPar() {
		return string.equals(")");
	}

	public boolean isIdentifier() {
		return string.matches(Tokenizer.NAME_STR);
	}

	public boolean isUnary() {
		return isFunction || isUnaryMinus || string.matches("!");
	}

	private boolean isFunction = false;

	void setIsFunction(boolean b) {
		if (isIdentifier())
			isFunction = b;
	}

	boolean isFunction() {
		return isFunction;
	}

	public boolean isNumber() {
		return string.matches(Tokenizer.NUMBER_STR);
	}

	public boolean isConstant() {
		return ASTNumber.isConstant(string);
	}

	private boolean isUnaryMinus = false;

	public void setIsUnaryMinus(boolean b) {
		if (string.equals("-")) {
			isUnaryMinus = b;
		}

	}

	public boolean isUnaryMinus() {
		return isUnaryMinus;
	}

	public boolean isInteger() {
		return string.matches(Tokenizer.INTEGER_STR);
	}

}
