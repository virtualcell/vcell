package org.jmathml;

/**
 * Visitor for traversing AST nodes for conversion to C-style strings as
 * documented in libSBML.
 * 
 * @author radams
 *
 */
public class ASTToFormulaVisitor extends ASTVisitor {

	StringBuffer sb = new StringBuffer();

	/**
	 * Before visiting, this method will return the empty string. After
	 * visiting, this method will return a C-style syntax String of the
	 * expression.
	 * 
	 * @return A <code>String </code>of the expression.
	 */
	public String getString() {
		return sb.toString();
	}

	public boolean visit(ASTOperator node) {
		if (node.getParentNode().isOperator()) {
			sb.append(" " + node.getParentNode().getString() + " ");
		}
		return true;
	}

	public boolean visit(ASTNumber node) {
		if (node.getIndex() == 0) {
			sb.append(node.getString());
		} else {
			sb.append(" " + node.getParentNode().getString() + " ").append(
					node.getString());
		}
		return true;
	}

	public boolean visit(ASTCi astIdentifiable) {
		sb.append(astIdentifiable.getString());
		return true;
	}

	public boolean visit(ASTSymbol symbol) {
		sb.append(symbol.getString());
		return true;
	}

	public boolean visit(ASTRootNode node) {
		printEnterNode(node);
		return true;
	}

	public boolean visit(ASTFunction node) {
		sb.append(node.getString() + "(");
		return true;
	}

	public boolean endVisit(ASTFunction node) {
		sb.append(")");
		return true;
	}

	void printEnterNode(ASTNode node) {
		System.out.println(node.getString() + " ");
	}

	void printLeaveNode(ASTNode node) {
		System.out.println("Leaving " + node.getName());
	}

	public void clear() {
		sb = new StringBuffer();
	}

}
