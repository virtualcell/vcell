package org.jmathml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TokenStream implements Iterable<Token> {

	private List<Token> tokens;

	TokenStream(List<Token> tokens) {
		super();
		this.tokens = tokens;
	}

	TokenStream subList(int st, int end) {
		List<Token> rc = new ArrayList<Token>();
		for (int i = st; i < end; i++) {
			rc.add(tokens.get(i));
		}
		return new TokenStream(rc);
	}

	public int size() {
		return tokens.size();
	}

	public Token get(int indx) {
		return tokens.get(indx);
	}

	public TokenIterator iterator() {
		return new TokenIterator();
	}

	public boolean startsWith(Token t) {
		return tokens.indexOf(t) == 0;
	}

	public boolean endsWith(Token t) {
		return tokens.lastIndexOf(t) == tokens.size() - 1;
	}

	String join() {
		StringBuffer sb = new StringBuffer();
		for (Token t : tokens) {
			sb.append(t.getString());
		}
		return sb.toString();
	}

	public TokenStream trimExtraneousParentheses() {
		while (startsWith(new Token("(")) && endsWith(new Token(")"))
				&& allOneElement()) {
			TokenStream ts = new TokenStream(tokens.subList(1,
					tokens.size() - 1));
			return ts.trimExtraneousParentheses();
		}
		return this;
	}

	private boolean allOneElement() {
		TokenIterator it = iterator();
		int parenDepth = 0;
		while (it.hasNext()) {
			Token t = it.next();
			if (t.getString().equals("(")) {
				parenDepth++;
			}
			if (t.getString().equals(")")) {
				parenDepth--;
			}
			if (parenDepth == 0 && it.peek() != null) {
				return false;
			}
		}
		return true;
	}

	class TokenIterator implements Iterator<Token> {
		private int currIndx = -1;

		/*
		 * Gets previous token or null if current token is first token
		 */
		Token previous() {
			if (currIndx <= 0) {
				return null;
			} else
				return tokens.get(currIndx - 1);
		}

		public boolean hasNext() {
			return currIndx < tokens.size() - 1;
		}

		/*
		 * Gets next token without advancing the pointer or null there is no
		 * next token
		 */
		Token peek() {
			if (hasNext()) {
				return tokens.get(currIndx + 1);
			} else
				return null;
		}

		public Token next() {
			return tokens.get(++currIndx);
		}

		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

}
