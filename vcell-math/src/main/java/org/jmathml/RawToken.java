package org.jmathml;

class RawToken {

	RawToken(char rawToken) {
		super();
		this.rawToken = rawToken;
	}

	private char rawToken;
	private boolean isMatched;

	boolean isMatched() {
		return isMatched;
	}

	void setMatched(boolean isMatched) {
		this.isMatched = isMatched;
	}

	char getRawToken() {
		return rawToken;
	}

}
