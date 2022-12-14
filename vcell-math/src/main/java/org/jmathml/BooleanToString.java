package org.jmathml;

import java.util.ArrayList;
import java.util.List;

import org.jmathml.ASTLogical.ASTLogicalType;
import org.jmathml.ASTRelational.ASTRelationalType;

class BooleanToString {

	/**
	 * Takes a logical or relational node of arbitrary complexity and returns a
	 * C-style representation
	 * 
	 * @param logicalOrRelational
	 *            An {@link ASTNode} of type {@link ASTRelationalType} or
	 *            {@link ASTLogicalType}
	 * @return A <code>String </code> representation
	 * @throws IllegalArgumentException
	 *             if node is incorrect type.
	 */
	public String getString(ASTNode logicalOrRelationalNode) {
		if (logicalOrRelationalNode == null
				|| !logicalOrRelationalNode.isLogical()
				&& !logicalOrRelationalNode.isRelational()) {
			throw new IllegalArgumentException();
		}
		// handle emptyNode
		if (logicalOrRelationalNode.getNumChildren() == 0) {
			return "";
		}
		List<ASTNode> bottomlogEqs = new ArrayList<ASTNode>();
		populateBottomLogicalNodes(logicalOrRelationalNode, bottomlogEqs);
		popList(bottomlogEqs);
		String sb = (String) logicalOrRelationalNode.getUserData();
		return sb;

	}

	private void populateBottomLogicalNodes(ASTNode root,
			List<ASTNode> bottomlogEqs) {
		boolean hasAnyLogicalChildren = false;
		for (ASTNode node : root.getChildren()) {
			if (node.isLogical()) {
				hasAnyLogicalChildren = true;
				populateBottomLogicalNodes(node, bottomlogEqs);
			}
		}
		if (!hasAnyLogicalChildren) {
			bottomlogEqs.add(root);
		}

	}

	private void popList(List<ASTNode> bottomlogEqs) {

		List<ASTNode> parents = new ArrayList<ASTNode>();
		for (ASTNode bottom : bottomlogEqs) {
			StringBuffer sb = new StringBuffer();

			if (bottom.isRelational()) {
				createInequality(sb, bottom);

				bottom.setUserData(sb.toString());
			} else {
				sb.append("(");
				for (ASTNode ineq : bottom.getChildren()) {
					if (ineq.getUserData() != null) {
						sb.append(ineq.getUserData());
						if (ineq != bottom.getRightChild()) {
							sb.append(" " + bottom.getString() + " "); // ineq
																		// operator
						}
					} else {
						createInequality(sb, ineq);
						if (ineq != bottom.getRightChild()) {
							sb.append(" " + bottom.getString() + " "); // logical
																		// operator
						}
					}
				}

				sb.append(")");
			}
			bottom.setUserData(sb.toString());
			if (bottom.getParentNode() != null
					&& !parents.contains(bottom.getParentNode())) {
				parents.add(bottom.getParentNode());
			}
		}
		// guard for top of tree, to prevent infinite recursion.
		if (!parents.isEmpty()) {
			popList(parents);
		}

	}

	private void createInequality(StringBuffer sb, ASTNode ineq) {
		// this section is for populating inequliity
		for (ASTNode math : ineq.getChildren()) { // 2 nodes f
			sb.append(new FormulaFormatter().formulaToString(math));
			if (math != ineq.getRightChild()) {
				sb.append(" " + ineq.getString() + " "); // ineq operator
			}
		}
	}

}
