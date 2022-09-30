package org.jmathml;

class RealNumber extends ASTNumber {
	Double real = 0d;
	long intVal = 0;
	final int UNKNOWN = 1;
	final int INTEGER = 2;
	final int DOUBLE = 1;
	int type = UNKNOWN;

	RealNumber(Double value) {
		super(ASTNumberType.NUMBER);
		this.real = value;
		type = DOUBLE;
		setName(getString());
	}

	RealNumber(int n) {
		super(ASTNumberType.NUMBER);
		this.intVal = n;
		type = INTEGER;
		setName(getString());
	}

	RealNumber(long n) {
		super(ASTNumberType.NUMBER);
		this.intVal = n;
		type = INTEGER;
		setName(getString());
	}

	public long getIntValue() {
		return intVal;
	}

	@Override
	public String getString() {
		if (type == INTEGER) {
			return Long.toString(intVal);
		} else if (type == DOUBLE) {
			if (isInfinity())
				return "infinity";
			else if (real == Double.NEGATIVE_INFINITY)
				return "-infinity";
			else if (isNaN())
				return "notanumber";
			else if (isE()) {
				return "exponentiale";
			} else if (isPI()) {
				return "pi";
			} else
				return Double.toString(real);
		}
		return Double.toString(Double.NaN);
	}

	@Override
	public double getValue() {
		return type == INTEGER ? intVal : real;
	}

	public boolean isInteger() {
		return type == INTEGER ? true : false;
	}

}
