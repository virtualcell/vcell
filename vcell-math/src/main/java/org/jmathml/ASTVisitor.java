package org.jmathml;

/**
 * Abstract class for visiting AST nodes for MathML based AST.
 * 
 * @author radams
 *
 */
public abstract class ASTVisitor {
	public boolean visit(ASTOperator node) {
		return true;
	}

	public boolean endVisit(ASTOperator node) {
		return true;
	}

	public boolean visit(ASTRootNode node) {
		return true;
	}

	public boolean endVisit(ASTRootNode node) {
		return true;
	}

	public boolean visit(ASTNumber node) {
		return true;
	}

	public boolean endVisit(ASTNumber node) {
		return true;
	}

	public boolean visit(ASTFunction node) {
		return true;
	}

	public boolean endVisit(ASTFunction node) {
		return true;
	}

	public boolean visit(ASTRelational node) {
		return true;
	}

	public boolean endVisit(ASTRelational node) {
		return true;
	}

	public boolean visit(ASTPiecewise node) {
		return true;
	}

	public boolean endVisit(ASTPiecewise node) {
		return true;
	}

	/**
	 * Clears state of visitor for reuse. Subclasses can override. This
	 * implementation does nothing.
	 */
	public void clear() {
	}

	public boolean visit(ASTCi astIdentifiable) {
		return true;
	}

	public boolean visit(ASTLogical astLogical) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean endVisit(ASTLogical astLogical) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean endVisit(ASTSymbol symbol) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean visit(ASTSymbol astSymbol) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean visit(ASTPiece astPiece) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean endVisit(ASTPiece astPiece) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean visit(ASTOtherwise astOtherwise) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean endVisit(ASTOtherwise astOtherwise) {
		// TODO Auto-generated method stub
		return true;
	}

}
