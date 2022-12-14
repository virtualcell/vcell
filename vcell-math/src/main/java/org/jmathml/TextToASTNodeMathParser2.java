package org.jmathml;

import org.jmathml.ASTFunction.ASTFunctionType;
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
 * E.g., <br>
 * 
 * <pre>
 * myfunction(a, b)
 * </pre>
 * 
 * can be turned into an ASTNode, but will not be evaluable.
 * 
 *
 */
public class TextToASTNodeMathParser2 {

	/**
	 * Parses a C -style math string into an ASTNode
	 * 
	 * @param mathString A <code>String</code> of math
	 * @param toAdd the node to add
	 * @return An {@link ASTNode}. This will be an {@link ASTRootNode} with the
	 *         math nodes as children. E.g., the string
	 * 
	 *         <pre>
	 * 2 * (4 / 5)
	 * </pre>
	 * 
	 *         will return the structure:
	 * 
	 *         <pre>
	 *  ASTRootNode
	 *       ASTimes
	 *            ASTCn(2)
	 *            ASTDivide
	 *                 ASTCn(4)
	 *                 ASTCn(5)
	 * </pre>
	 * @throws RuntimeException
	 *             if mathString cannot be parsed due to incorrect syntax.
	 */
	public ASTNode parseString(String mathString, ASTNode toAdd) {

		// get rid of all whitespace
		String mathToParse = mathString.replaceAll(" ", "");

		Tokenizer tokeniser = new Tokenizer();
		TokenStream tokens = tokeniser.tokenize(mathToParse)
				.trimExtraneousParentheses();
		return parseTokens(tokens, toAdd);
	}

	ASTNode parseTokens(TokenStream tokens, ASTNode toAdd) {
		// iterate over chars, find lowest precedence operator
		int tokIndx = 0;
		Token currLowestPrecedenceToken = null;
		int currLowestPrecedencTokenIndex = -1; // index of any operator in curr
		// string
		int currLowestPrecedence = 10; // higher than any other
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
				int precedence = getPrecedenceForToken(tok, tokIt);
				if (precedence <= currLowestPrecedence) {
					currLowestPrecedenceToken = tok;
					currLowestPrecedence = precedence;
					currLowestPrecedencTokenIndex = tokIndx;
				}
			}
			tokIndx++;
		}
		ASTNode rc = null;

		if (currLowestPrecedencTokenIndex != -1) {
			rc = createNodeForLowestPrecedence(currLowestPrecedenceToken);
			if (rc != null)
				toAdd.addChildNode(rc);
			else
				rc = toAdd;
			if (currLowestPrecedenceToken.isOperator()
					&& !currLowestPrecedenceToken.isUnaryMinus()) {
				TokenStream left = tokens.subList(0,
						currLowestPrecedencTokenIndex)
						.trimExtraneousParentheses();
				TokenStream right = tokens.subList(
						currLowestPrecedencTokenIndex + 1, tokens.size())
						.trimExtraneousParentheses();
				parseTokens(left, rc);
				parseTokens(right, rc);
			} else if (currLowestPrecedenceToken.isUnary()) {
				TokenStream child = tokens.subList(
						currLowestPrecedencTokenIndex + 1, tokens.size())
						.trimExtraneousParentheses();
				parseTokens(child, rc);
			}

			else if (currLowestPrecedenceToken.isNumber()) {
				return rc;
			} else if (currLowestPrecedenceToken.isIdentifier()) {
				return rc;
			}

		}

		return rc;
	}

	private ASTNode createNodeForLowestPrecedence(Token token) {
		String tokenStr = token.getString();
		if (tokenStr.equals("+"))
			return new ASTPlus();
		else if (tokenStr.equals("-")) {
			return new ASTMinus();
		} else if (tokenStr.equals("*"))
			return new ASTTimes();
		else if (token.isConstant()) {
			ASTNumber num = ASTNumber.getConstant(tokenStr);

			return num;
		} else if (token.isFunction()) {
			ASTNode fn = ASTFunction.createFunctionNode(ASTFunction
					.getFunctionTypeForName(tokenStr));
			if (fn.getType().equals(ASTFunctionType.MISCELLANEOUS)) {
				ASTSymbol sym = SymbolRegistry.getInstance().createSymbolFor(
						token.getString());
				if (sym != null) {
					fn = sym;
				}
			}
			return fn;
		} else if (token.isNumber()) {
			ASTNumber cn = null;
			if (token.isInteger()) {
				cn = ASTNumber.createNumber(Integer.parseInt(tokenStr));
			} else {
				cn = ASTNumber.createNumber(Double.parseDouble(tokenStr));
			}

			return cn;
		} else if (token.isIdentifier()) {
			ASTCi ci = new ASTCi(tokenStr);

			return ci;
		}

		else if (tokenStr.equals("/"))

			return new ASTDivide();
		else if (tokenStr.equals("^"))
			return new ASTPower();
		else if (tokenStr.equals(">"))
			return new ASTRelational(ASTRelationalType.GT);
		else if (tokenStr.equals("<"))
			return new ASTRelational(ASTRelationalType.LT);
		else if (tokenStr.equals("<="))
			return new ASTRelational(ASTRelationalType.LEQ);
		else if (tokenStr.equals(">="))
			return new ASTRelational(ASTRelationalType.GEQ);
		else if (tokenStr.equals("=="))
			return new ASTRelational(ASTRelationalType.EQ);
		else if (tokenStr.equals("!="))
			return new ASTRelational(ASTRelationalType.NEQ);
		else if (tokenStr.equals("||"))
			return new ASTLogical(ASTLogicalType.OR);
		else if (tokenStr.equals("&&"))
			return new ASTLogical(ASTLogicalType.AND);
		else if (tokenStr.equals("!"))
			return new ASTLogical(ASTLogicalType.NOT);
		else if (tokenStr.equals(","))
			return null;
		else
			return new ASTCi("");

	}

	private int getPrecedenceForToken(Token tok, TokenIterator tokIt) {
		String test = tok.getString();

		if (test.equals("+") || test.equals("-")) {
			if (test.equals("-")) {

				if (tokIt.previous() == null || tokIt.previous().isOperator()
						|| tokIt.previous().isLPar()) {
					tok.setIsUnaryMinus(true);
					return 7;
				} else {
					return 4;
				}
			} else {
				return 4;
			}
		}

		else if (tok.isRelationalOperator()) {
			return 3;
		} else if (tok.isLogicalOperator()) {
			return 2;
		} else if (test.equals("*") || test.equals("/")) {
			return 5;
		} else if (test.equals("^")) {
			return 6;
		} else if (test.equals(",")) {
			return 1;
		} else if (tok.isIdentifier()) {
			if (tokIt.peek() != null && tokIt.peek().isLPar()) {
				tok.setIsFunction(true);
				return 8;
			} else {
				return 9;
			}

		} else if (tok.isNumber()) {

			return 9;

		} else
			return 12;
	}

}
