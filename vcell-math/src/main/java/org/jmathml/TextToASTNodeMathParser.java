package org.jmathml;

import org.jmathml.ASTFunction.ASTFunctionType;
import org.jmathml.ASTFunction.ASTMiscellaneousFunction;
import org.jmathml.ASTLogical.ASTLogicalType;
import org.jmathml.ASTRelational.ASTRelationalType;
import org.jmathml.TokenStream.TokenIterator;

/**
 * Convenience method to parse free text math into an ASTNode. The style of math
 * is a C-style syntax, e.g.,<br>
 * . The parsing supports standard arithmetical operations and function calls.
 * E.g.,
 * 
 * <ul>
 * <li>4 + 2
 * <li>sin(pi / 2) // or any other trig function
 * <li>log(9,81) // = 2
 * <li>root(4,81) //=3
 * <li>exp(t)
 * 
 * </ul>
 *
 * 
 * <p>
 * In addition, the strings <code>pi</code>, <code>NaN</code>,
 * <code>infinity</code> and <code>exponentiale</code> are translated into the
 * appropriate constants.
 * </p>
 * 
 * Functions not supported by this library ( see ASTFunctionType for supported
 * functions ) will be parsed into a function call, which will not be evaluable.
 * E.g., 
 *
 * <pre>
 * myfunction(a, b)
 * </pre>
 * 
 * can be turned into an ASTNode, but will not be evaluable.
 * 
 */
public class TextToASTNodeMathParser {

	/**
	 * @param mathString The math to parse
	 * @param toAdd The node to add
	 * @return the top-level node
	 */
	public ASTNode parseString(String mathString, ASTNode toAdd) {
		mathString = tidyUp(mathString);
		// get rid of all whitespace
		String mathToParse = mathString.replaceAll(" ", "");

		Tokenizer tokeniser = new Tokenizer();
		TokenStream tokens = tokeniser.tokenize(mathToParse);
		return parseTokens(tokens, toAdd);
	}

	ASTNode parseTokens(TokenStream tokens, ASTNode toAdd) {
		// iterate over chars, find lowest precedence operator
		int tokIndx = 0;
		String currLowestPrecedenceToken = " ";
		int currLowestPrecedencTokenIndex = -1; // index of any operator in curr
												// string
		int currLowestPrecedence = 7; // higher than any other
		int parDepth = 0; // parenthesis depth
		TokenIterator tokIt = tokens.iterator();
		while (tokIt.hasNext()) {
			Token tok = tokIt.next();

			// keep track of parentheses,
			if (tok.getString().equals("(")) {
				parDepth++;
			} else if (tok.getString().equals(")")) {
				parDepth--;
			}

			// only parse operators if they're not in brackets
			if (parDepth == 0) {
				int precedence = getPrecedenceForToken(tok);
				if (precedence <= currLowestPrecedence) {
					currLowestPrecedenceToken = tok.getString();
					currLowestPrecedence = precedence;
					currLowestPrecedencTokenIndex = tokIndx;
				}
			}
			tokIndx++;
		}
		ASTNode rc = null;
		boolean isDisposed = false;
		// if we have a lowest precedence operator, split string either side of
		// operator && recurse
		if (currLowestPrecedencTokenIndex != -1) {
			rc = createNodeForLowestPrecedence(currLowestPrecedenceToken);
			if (rc != null)
				toAdd.addChildNode(rc);
			else
				rc = toAdd;
			TokenStream left = tokens.subList(0, currLowestPrecedencTokenIndex);
			TokenStream right = tokens.subList(
					currLowestPrecedencTokenIndex + 1, tokens.size());
			parseTokens(left, rc);
			parseTokens(right, rc);
			isDisposed = true;
		}
		if (isDisposed) {
			return rc; // we've finished with this string
		}
		String mathToParse = tidyUp(join(tokens));
		if (ASTNumber.isConstant(mathToParse)) {
			ASTNumber num = ASTNumber.getConstant(mathToParse);
			toAdd.addChildNode(num);
			return num;
		}
		if (isVarName(join(tokens))) {
			ASTCi ci = new ASTCi(mathToParse.trim());
			toAdd.addChildNode(ci);
			return ci;
		}
		if (isNumber(mathToParse.trim())) {
			ASTNumber cn = ASTNumber.createNumber(Double
					.parseDouble(mathToParse.trim()));
			toAdd.addChildNode(cn);
			return cn;
		}
		if (isFunction(mathToParse.trim())) {
			ASTNode fn = ASTFunction
					.createFunctionNode(ASTFunction
							.getFunctionTypeForName(getCandidateFunctionName(mathToParse)));
			if (fn.getType().equals(ASTFunctionType.MISCELLANEOUS)) {
				fn = new ASTMiscellaneousFunction(
						getCandidateFunctionName(mathToParse));
			}
			toAdd.addChildNode(fn);
			String fnContents = mathToParse.substring(
					mathToParse.indexOf('(') + 1, mathToParse.lastIndexOf(')'));
			return parseString(tidyUp(fnContents), fn);
		}

		if (isGroup(mathToParse.trim())) {
			while (mathToParse.charAt(1) == ('(')
					&& mathToParse.charAt(mathToParse.length() - 2) == (')')) {
				mathToParse = mathToParse
						.substring(1, mathToParse.length() - 1);
			}
			String fnContents = mathToParse.substring(
					mathToParse.lastIndexOf('(') + 1, mathToParse.indexOf(')'));
			return parseString(tidyUp(fnContents), toAdd);
		}

		return rc;
	}

	// remove unecessary surrounding brackets
	private String tidyUp(String right) {
		if (right.startsWith("(") && right.endsWith(")")) {
			return right.substring(right.indexOf('(') + 1,
					right.lastIndexOf(')'));
		}
		return right;
	}

	private boolean isGroup(String trim) {
		return trim.contains("(") && trim.contains(")");
	}

	// for string xxxx(expr), returns xxxx
	private String getCandidateFunctionName(String name) {
		if (!name.contains("(")) {
			return "";
		}
		return name.substring(0, name.indexOf('('));
	}

	private boolean isFunction(String name) {
		if (!name.contains("(")) {
			return false;
		}
		String fname = getCandidateFunctionName(name);
		return isVarName(fname);
	}

	private boolean isVarName(String trim) {
		return trim.matches("[_A-Za-z][_\\w\\d]*");
	}

	private boolean isNumber(String trim) {
		return trim.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

	}

	private ASTNode createNodeForLowestPrecedence(String tok) {
		if (tok.equals("+"))
			return new ASTPlus();
		else if (tok.equals("-")) {
			return new ASTMinus();
		} else if (tok.equals("*"))
			return new ASTTimes();
		else if (tok.equals("/"))

			return new ASTDivide();
		else if (tok.equals("^"))
			return new ASTPower();
		else if (tok.equals(">"))
			return new ASTRelational(ASTRelationalType.GT);
		else if (tok.equals("<"))
			return new ASTRelational(ASTRelationalType.LT);
		else if (tok.equals("<="))
			return new ASTRelational(ASTRelationalType.LEQ);
		else if (tok.equals(">="))
			return new ASTRelational(ASTRelationalType.GEQ);
		else if (tok.equals("=="))
			return new ASTRelational(ASTRelationalType.EQ);
		else if (tok.equals("!="))
			return new ASTRelational(ASTRelationalType.NEQ);
		else if (tok.equals("||"))
			return new ASTLogical(ASTLogicalType.OR);
		else if (tok.equals("&&"))
			return new ASTLogical(ASTLogicalType.AND);
		else if (tok.equals(","))
			return null;
		else
			return new ASTCi("");

	}

	private int getPrecedenceForToken(Token tok) {
		String test = tok.getString();
		if (test.equals("+") || test.equals("-")) {
			return 3;
		} else if (test.equals(">") || test.equals("<") || test.equals("<=")
				|| test.equals(">=") || test.equals("==") || test.equals("!=")) {
			return 2;
		} else if (test.equals("*") || test.equals("/")) {
			return 4;
		} else if (test.equals("^")) {
			return 5;
		} else if (test.equals(",")) {
			return 1;
		} else
			return 12;
	}

	private String join(TokenStream tokens) {
		StringBuffer sb = new StringBuffer();
		for (Token t : tokens) {
			sb.append(t.getString());
		}
		return sb.toString();
	}

}
