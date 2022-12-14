package org.jmathml;

import org.jmathml.ASTNode.ASTType;

/**
 * Converts an AST to a string representation of a formula. Currently handles
 * functions, arithmetic and numerical construct, and simple inequalities.
 * <p>
 * The strings generated are not automatically in correct C syntax, but are
 * generated based on MathML terminology. For example, the natural logarithm,
 * ln, is translated into the string 'ln', not 'log' as it is in C.
 */
public class FormulaFormatter {

	/**
	 * See http://sourceforge.net/apps/mediawiki/jmathml/index.php?title=Import_of_C-style_Math
	 * @param node A non-null node to extract the formula for
	 * @return The formula as a String
	 * @throws IllegalArgumentException
	 *             if <code>node</code> is null.
	 */
	public String formulaToString(final ASTNode node) {
		StringBuffer sb = new StringBuffer();

		if (node == null) {
			throw new IllegalArgumentException();
		}
		FormulaFormatter_visit(null, node, sb);
		return sb.toString();

	}

	/**
	 * @return true (non-zero) if the given ASTNode is to be formatted as a
	 *         function.
	 */
	boolean FormulaFormatter_isFunction(final ASTNode node) {
		return node.isFunction() || node.isLogical()
				|| (node.isSymbol() && ((ASTSymbol) node).isSymbolFunction());
	}

	/**
	 * @return true (non-zero) if the given child ASTNode should be grouped
	 *         (with parenthesis), false (0) otherwise.
	 * 
	 *         A node should be group if it is not an argument to a function and
	 *         either:
	 * 
	 *         - The parent node has higher precedence than the child, or
	 * 
	 *         - If parent node has equal precedence with the child and the
	 *         child is to the right. In this case, operator associativity and
	 *         right-most AST derivation enforce the grouping.
	 */
	boolean FormulaFormatter_isGrouped(final ASTNode parent, final ASTNode child) {
		int pp, cp;
		ASTTypeI pt, ct;
		boolean group = false;

		if (parent != null) {
			if (!FormulaFormatter_isFunction(parent)) {
				pp = parent.getPrecedence();
				cp = child.getPrecedence();

				if (pp > cp) {
					group = true;
				} else if (pp == cp) {
					/**
					 * Group only if i) child is to the right and ii) both
					 * parent and child are either not the same, or if they are
					 * the same, they should be non-associative operators (i.e.
					 * AST_MINUS or AST_DIVIDE). That is, do not group a parent
					 * and right child that are either both AST_PLUS or both
					 * AST_TIMES operators.
					 */
					if (parent.getRightChild().equals(child)) {
						pt = parent.getType();
						ct = child.getType();

						group = ((pt != ct) || (pt == ASTType.MINUS || pt == ASTType.DIVIDE));
					}
				}
			}
		}

		return group;
	}

	/**
	 * Formats the given ASTNode as a token and appends the result to the given
	 * StringBuffer.
	 */
	void FormulaFormatter_format(StringBuffer sb, final ASTNode node) {
		if (node.isOperator()) {
			FormulaFormatter_formatOperator(sb, node);
		} else if (node.isRelational()) {
			FormulaFormatter_formatRelation(sb, node);
		} else if (node.isFunction()) {
			FormulaFormatter_formatFunction(sb, node);
		} else if (node.isNumber()) {
			sb.append(node.getString());
		} else {
			if (node.isNumber() && node.getAsASTNumber().isInfinity()) {
				sb.append("infinity");
			} else {
				sb.append(node.getString());
			}
		}
	}

	private void FormulaFormatter_formatRelation(StringBuffer sb, ASTNode node) {
		if (!node.isRelational()) {
			throw new IllegalArgumentException();
		}
		sb.append(" ");
		sb.append(node.getString());
		sb.append(" ");
	}

	/**
	 * Formats the given ASTNode as a function name and appends the result to
	 * the given StringBuffer.
	 * 
	 * @throw
	 */
	void FormulaFormatter_formatFunction(StringBuffer sb, final ASTNode node) {
		if (!node.isFunction()) {
			throw new IllegalArgumentException();
		}
		sb.append(node.getString());
	}

	/**
	 * Formats the given ASTNode as an operator and appends the result to the
	 * given StringBuffer.
	 * @throws IllegalArgumentException
	 *             if node is not an operator (if node.getOperator() == false)
	 */
	void FormulaFormatter_formatOperator(StringBuffer sb, final ASTNode node) {

		if (!node.isOperator()) {
			throw new IllegalArgumentException();
		}
		ASTOperator op = node.getNodeAsOperator();
		if (!op.isPower())
			sb.append(" ");
		sb.append(node.getString());
		if (!op.isPower())
			sb.append(" ");
	}

	/**
	 * Visits the given ASTNode node. This function is really just a dispatcher
	 * to either SBML_formulaToString_visitFunction() or
	 * SBML_formulaToString_visitOther().
	 */
	void FormulaFormatter_visit(final ASTNode parent, final ASTNode node,
			StringBuffer sb) {

		if (FormulaFormatter_isFunction(node)) {
			FormulaFormatter_visitFunction(parent, node, sb);
		} else if (FormulaFormatter_isFunction(node)) {
			FormulaFormatter_visitFunction(parent, node, sb);
		} else if (node.getType().equals(ASTType.UMINUS)) {
			FormulaFormatter_visitUMinus(parent, node, sb);
		} else {
			FormulaFormatter_visitOther(parent, node, sb);
		}
	}

	// if (node.isLog10()) {
	// FormulaFormatter_visitLog10(parent, node, sb);
	// } else
	// else if (node.isFunction() && ((ASTFunction) node).isSqrt()) {
	// FormulaFormatter_visitSqrt(parent, node, sb);
	// }

	/**
	 * Visits the given ASTNode as a function. For this node only the traversal
	 * is preorder.
	 */
	void FormulaFormatter_visitFunction(final ASTNode parent,
			final ASTNode node, StringBuffer sb) {
		int numChildren = node.getNumChildren();
		int n;
		FormulaFormatter_format(sb, node);
		sb.append("(");
		if (numChildren > 0) {
			FormulaFormatter_visit(node, node.firstChild(), sb);
		}
		for (n = 1; n < numChildren; n++) {
			sb.append(", ");
			FormulaFormatter_visit(node, node.getChildAtIndex(n), sb);
		}
		sb.append(")");
	}

	/**
	 * Visits the given ASTNode as the function "log(10, x)" and in doing so,
	 * formats it as "log(x)" (where x is any subexpression).
	 */
	void FormulaFormatter_visitLog10(final ASTNode parent, final ASTNode node,
			StringBuffer sb) {
		sb.append("log10(");
		if (node.getNumChildren() == 1)
			FormulaFormatter_visit(node, node.firstChild(), sb);
		else {
			FormulaFormatter_visit(node, node.getChildAtIndex(1), sb);
		}
		sb.append(")");
	}

	/**
	 * Visits the given ASTNode as the function "root(2, x)" and in doing so,
	 * formats it as "root(x)" (where x is any subexpression).
	 */
	void FormulaFormatter_visitSqrt(final ASTNode parent, final ASTNode node,
			StringBuffer sb) {
		sb.append("root(");
		FormulaFormatter_visit(node, node.getChildAtIndex(0), sb);
		sb.append(")");
	}

	/**
	 * Visits the given ASTNode as a unary minus. For this node only the
	 * traversal is preorder.
	 */
	void FormulaFormatter_visitUMinus(final ASTNode parent, final ASTNode node,
			StringBuffer sb) {
		sb.append("-");
		FormulaFormatter_visit(node, node.firstChild(), sb);
	}

	/**
	 * Visits the given ASTNode and continues the inorder traversal.
	 */
	void FormulaFormatter_visitOther(final ASTNode parent, final ASTNode node,
			StringBuffer sb) {
		int numChildren = node.getNumChildren();
		boolean group = FormulaFormatter_isGrouped(parent, node);

		if (group) {
			sb.append("(");
		}

		if (numChildren > 0) {
			FormulaFormatter_visit(node, node.firstChild(), sb);
		}

		FormulaFormatter_format(sb, node);

		if (numChildren > 1) {
			for (int i = 1; i < numChildren; i++) {
				FormulaFormatter_visit(node, node.getChildAtIndex(i), sb);
				if (node.isOperator() && i < numChildren - 1) {
					sb.append(" " + node.getString() + " ");
				}
			}
		}
		if (group) {
			sb.append(")");
		}
	}
}
