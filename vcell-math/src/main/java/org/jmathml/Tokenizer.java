package org.jmathml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Parses an input string into tokens
 */
class Tokenizer {
	final static String NAME_STR = "\\b[_A-Za-z]\\w*\\b"; // use word
															// boundaries: won't
															// match "e10" from
															// "1e10", as that's
															// a number
	final static String NUMBER_STR = "\\b[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?\\b"; // use
																					// word
																					// boundaries:
																					// won't
																					// match
																					// "6"
																					// from
																					// "k6",
																					// as

	final static Pattern NAME = Pattern.compile(NAME_STR);

	final static String INTEGER_STR = "[0-9]+";

	final static Pattern NUMBER = Pattern.compile(NUMBER_STR);
	final static Pattern LPAR = Pattern.compile("\\(");
	final static Pattern RPAR = Pattern.compile("\\)");
	final static Pattern ARG_SEP = Pattern.compile(",");
	final static Pattern PLUS = Pattern.compile("\\+");
	final static Pattern MINUS = Pattern.compile("\\-");
	final static Pattern TIMES = Pattern.compile("\\*");
	final static Pattern DIVIDE = Pattern.compile("/");
	final static Pattern POW = Pattern.compile("\\^");

	final static Pattern EQ = Pattern.compile("==");
	final static Pattern NOT = Pattern.compile("!(?!=)");
	final static Pattern NEQ = Pattern.compile("!=");
	final static Pattern GEQ = Pattern.compile(">=");
	final static Pattern GT = Pattern.compile(">");
	final static Pattern LEQ = Pattern.compile("<=");
	final static Pattern LT = Pattern.compile("<");
	final static Pattern AND = Pattern.compile("&&");
	final static Pattern OR = Pattern.compile("\\|\\|");

	final static Pattern[] patterns = new Pattern[] { NAME, NUMBER, LPAR, RPAR,
			ARG_SEP, PLUS, MINUS, TIMES, DIVIDE, POW, EQ, NOT, NEQ, LEQ, LT,
			GEQ, GT, AND, OR };

	/*
	 * @throws ParseException if there are any unmatched characters
	 */
	TokenStream tokenize(String input) throws ParseException {
		assert (input != null);
		List<RawToken> rawTokens = new ArrayList<RawToken>();
		for (int i = 0; i < input.length(); i++) {
			rawTokens.add(new RawToken(input.charAt(i)));
		}

		List<Token> tokens = new ArrayList<Token>();

		Map<Integer, String> set = new HashMap<Integer, String>();
		for (int i = 0; i < patterns.length; i++) {
			Pattern p = patterns[i];
			Matcher m = p.matcher(input);
			while (m.find()) {
				String matched = m.group();
				// System.out.println("matched:" +matched);

				if (isAlreadyMatched(rawTokens, m)) {
					// System.out.println(m.group() + " already matched");
					continue;
				} else {
					set.put(m.start(), matched);
					for (int j = m.start(); j < m.end(); j++) {
						rawTokens.get(j).setMatched(true);
					}
				}

			}

		}

		List<Integer> keys = new ArrayList<Integer>(set.keySet());
		Collections.sort(keys);
		for (Integer key : keys) {
			// System.out.println("adding " + set.get(key)+ " at " + key);
			Token tok = new Token(set.get(key));
			tokens.add(tok);
		}
		int indx = 0;
		for (RawToken rawtok : rawTokens) {

			if (rawtok.isMatched() == false) {
				throw new ParseException("[" + rawtok.getRawToken()
						+ "] could not be parsed at position: " + indx);
			}
			indx++;
		}

		return new TokenStream(tokens);
	}

	private boolean isAlreadyMatched(List<RawToken> rawTokens, Matcher m) {
		for (int j = m.start(); j < m.end(); j++) {
			if (rawTokens.get(j).isMatched()) {
				return true;
			}
		}
		return false;
	}

}
