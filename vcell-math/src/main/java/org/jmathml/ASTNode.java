package org.jmathml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmathml.ASTFunction.ASTFunctionType;

/**
 * Base class for all ASTNodes. All MathML elements are linked as an Abstract
 * Syntax Tree For all these elements, equality is based on instance. For
 * example, if an expression makes multiple references to a
 * &lt;ci&gt;x&lt;/ci&gt; element, these should be added to the ASTNode tree as
 * separate objects.
 * <p>
 * ASTNodes can be evaluated as expressions. For literal expressions,
 * 
 * <pre>
 * node.evaluate();
 * </pre>
 * 
 * will evaluate the node.
 
 * For expressions containing variables, these variables need to be resolved at
 * evaluation time by supplying the evaluation function with an
 * {@link IEvaluationContext}.
 * 
 * @author radams
 *
 */
public abstract class ASTNode {
	private List<ASTNode> children = new ArrayList<ASTNode>();
	private ASTNode parentNode;
	private boolean isOperator = false;
	private int level;
	private ASTTypeI type;
	private String name;

	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}

	/*
	 * package scoped - should not be made public
	 */
	void setType(ASTTypeI type) {
		this.type = type;
	}

	private Object userData;

	public enum ASTType implements ASTTypeI {

		PLUS, MINUS, DIVIDE, TIMES, UMINUS, PIECEWISE, PIECE, OTHERWISE, ROOTNODE;

		public String getString() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	ASTNode(ASTTypeI type) {
		this.type = type;
	}

	/**
	 * Gets the specific node type of this node.
	 * 
	 * @return An enum implementing ASTTypeI
	 */
	public ASTTypeI getType() {
		return type;
	}

	/**
	 * Gets the level within the document of this node. The root element is at
	 * level 0, its children at level 1, etc.,
	 * 
	 * @return an <code> int</code> of the level; &gt; 0
	 */
	public final int getLevel() {
		return level;
	}

	void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets the name of this node which may be a descriptor or actual content
	 * depending on the subclass.
	 * 
	 * @return A <code>String</code>.
	 */
	public final String getName() {
		return name;
	}

	void setName(String name) {
		assert (name != null);
		this.name = name;
	}

	/**
	 * Convenience method to return the names of all Ci element names in the
	 * tree descending from this node.<br>
	 * Since ASTCi node equality is based on object identity, this method may
	 * return multiple objects referring to the same variable.
	 * 
	 * @return A possibly empty but non-null set of variable names in this node
	 *         and its descendants.
	 */
	public final Set<ASTCi> getIdentifiers() {
		Set<ASTCi> rc = new HashSet<ASTCi>();
		if (isVariable()) {
			rc.add((ASTCi) this);
		}
		for (ASTNode child : getChildren()) {
			getIdentifiers(rc, child, false);
		}
		return rc;

	}

	/**
	 * Convenience method to return the names of all unique Ci element names in
	 * the tree descending from this node.<br>
	 * 
	 * @return A possibly empty but non-null set of variable names in this node
	 *         and its descendants.
	 */
	public final Set<ASTCi> getUniqueIdentifiers() {
		Set<ASTCi> rc = new HashSet<ASTCi>();
		if (isVariable()) {
			rc.add((ASTCi) this);
		}
		for (ASTNode child : getChildren()) {
			getIdentifiers(rc, child, true);
		}
		return rc;

	}

	// rec
	private void getIdentifiers(Set<ASTCi> rc, ASTNode node, boolean uniqueOnly) {
		if (node.isVariable()) {
			if ((uniqueOnly)) {
				boolean seen = false;
				for (ASTCi ci : rc) {
					if (node.getName().equals(ci.getName())) {
						seen = true;
						break;
					}
				}
				if (!seen) {
					rc.add((ASTCi) node);
				}
			} else {
				rc.add((ASTCi) node);
			}
		}
		for (ASTNode child : node.getChildren()) {
			getIdentifiers(rc, child, uniqueOnly);
		}
	}

	/**
	 * Operation to add a new , non-null child node. This operation does not
	 * check if this is semantically correct, addition may produce the incorrect
	 * number of child nodes for a given type.
	 * 
	 * @param node
	 *            An {@link ASTNode}, not null.
	 * @return <code>true</code> if added, <code>false </code>otherwise.
	 * @throws IllegalArgumentException
	 *             if argument <code>node</code> is null
	 */
	public final boolean addChildNode(ASTNode node) {
		assertNotNull(node);
		boolean added = children.add(node);
		if (added) {
			node.setParent(this);
			node.setLevel(getLevel() + 1);
		}
		return added;
	}

	/**
	 * 
	 * @param index the index
	 * @param node The node to add
	 * @throws IndexOutOfBoundsException - if the index is out of range (index &lt;
	 *         0 || index &gt; size()).
	 */
	public final void addChildNodeAt(int index, ASTNode node) {
		assertNotNull(node);
		children.add(index, node);
		node.setParent(this);
		node.setLevel(getLevel() + 1);
	}

	/**
	 * Operation to remove a non-null child node . This operation does not check
	 * if this is semantically correct, removal may produce the incorrect number
	 * of child nodes for a given type.<br>
	 * The removed node will have a null parent, and its level will be set to 0.
	 * 
	 * @param node
	 *            , not null
	 * @return <code>true</code> if removed, <code>false otherwise</code>
	 * @throws IllegalArgumentException
	 *             if argument <code>node</code> is null
	 */
	public final boolean removeChildNode(ASTNode node) {
		assertNotNull(node);
		boolean removed = children.remove(node);
		if (removed) {
			node.setParent(null);
			node.setLevel(0);
		}
		return removed;
	}

	/**
	 * Getter for parent node.
	 * 
	 * @return The parent <code>ASTNode</code>, or <code>null</code> if not
	 *         parented
	 */
	public final ASTNode getParentNode() {
		return parentNode;
	}

	void setParent(ASTNode node) {
		this.parentNode = node;
	}

	/**
	 * Boolean test for whether this node is an operator
	 * @return <code>true</code> if this node is an operator
	 */
	public final boolean isOperator() {
		return isOperator;
	}

	void setOperator(boolean isOperator) {
		this.isOperator = isOperator;
	}

	/**
	 * Gets a possibly empty but not null <code>List</code> of children.
	 * @return A  List&lt;ASTNode&gt;
	 */
	public final List<ASTNode> getChildren() {
		return children;
	}

	/**
	 * Convenience method to get first child. This is equivalent to:
	 * <p>
	 * <code> node.getChildAtIndex(0);</code>
	 * </p>
	 * @return the first child {@link ASTNode};
	 * @throws IllegalStateException
	 *             if has no child.
	 */
	public final ASTNode firstChild() {
		if (children.isEmpty()) {
			throw new IllegalStateException();
		}
		return children.get(0);
	}

	/**
	 * Gets the child at a given index in an ASTs children list
	 * 
	 * @param index
	 *            , 0 based.
	 * @return The {@link ASTNode} at that position in the child list
	 * @throws IllegalArgumentException
	 *             if index is outside the range of the child list.
	 */
	public final ASTNode getChildAtIndex(int index) {
		if (indexOK(index))
			return children.get(index);
		else
			throw new IllegalArgumentException("index ouside range");
	}

	/**
	 * Gets the child at a given index in an ASTs children list
	 * 
	 * @param index
	 *            , 0 based.
	 * @return The removed {@link ASTNode} at that position in the child list.
	 * @throws IllegalArgumentException
	 *             if index is outside the range of the child list.
	 */
	public final ASTNode removeChildAtIndex(int index) {
		if (indexOK(index))
			return children.remove(index);
		else
			throw new IllegalArgumentException("index outside range");
	}

	/**
	 * This method will replace an old child node with a new child node, if the
	 * old child node is indeed a child of this node. The new node will occupy
	 * the same index as the old one did.
	 * 
	 * @param oldNode
	 *            A non-null node to remove.
	 * @param newNode
	 *            A non-null node to add.
	 * @return <code>true if replacement was successful</code>,
	 *         <code>false</code> if the node to be changed was not found.
	 * @throws IllegalArgumentException
	 *             if either argument is <code>null</code>.
	 */
	public final boolean replaceChild(ASTNode oldNode, ASTNode newNode) {
		assertNotNull(oldNode, newNode);
		int index = children.indexOf(oldNode);
		if (index == -1) {
			return false;
		}
		boolean rc = false;
		rc = removeChildNode(oldNode);
		addChildNodeAt(index, newNode);
		return rc;

	}

	void assertNotNull(Object... args) {
		for (Object o : args) {
			if (o == null) {
				throw new IllegalArgumentException();
			}
		}
		// TODO Auto-generated method stub

	}

	private boolean indexOK(int index) {
		return children.size() > index && index >= 0;
	}

	/**
	 * Gets the index of this node amongst its siblings; 0 based.
	 * 
	 * @return the zero based index of this node amongst its siblings. If it has
	 *         no siblings, index = 0.
	 * @throws IllegalStateException
	 *             if this node has no Parent
	 */
	public final int getIndex() {
		if (getParentNode() == null) {
			throw new IllegalStateException("Node has no parent!");
		}
		return getParentNode().getChildren().indexOf(this);
	}

	/**
	 * Boolean test as to whether node is a leaf (terminating node) in the AST.
	 * 
	 * @return <code>true</code> if this node is a leaf, <code> false</code>
	 *         otherwise
	 */
	public final boolean isLeaf() {
		return children.size() == 0;
	}

	/**
	 * Return <code>true</code> if this node represents a log10 operation
	 * 
	 * @return A <code>boolean</code>
	 */
	public boolean isLog10() {
		return false;
	}

	/**
	 * Return <code>true</code> if this node represents a square root operation
	 * 
	 * @return A <code>boolean</code>
	 */
	public boolean isSqrt() {
		return false;
	}

	/**
	 * Return <code>true</code> if this node represents a &lt;Ci&gt; element.
	 * @return <code>true</code> if this node is a variable
	 */
	public boolean isVariable() {
		return false;
	}

	/**
	 * Return <code>true</code> if this node represents a &lt;csymbol&gt;
	 * element.
	 * @return <code>true</code> if this node is a symbol
	 */
	public boolean isSymbol() {
		return false;
	}

	/**
	 * REturns this node as an {@link ASTOperator}, only if isOperator() == true
	 * 
	 * @return This node, as an {@link ASTOperator}
	 * @throws IllegalStateException
	 *             if not an operator.
	 */
	ASTOperator getNodeAsOperator() {
		if (isOperator()) {
			return (ASTOperator) this;
		} else
			throw new IllegalStateException();
	}

	/**
	 * Visitor pattern for function extensions.
	 * 
	 * @param v
	 *            An {@link ASTVisitor}
	 * @return <code>true</code> if descendants of this node should be
	 *         traversed.
	 */
	public final boolean accept(ASTVisitor v) {
		return doAccept(v);
	}

	abstract boolean doAccept(ASTVisitor v);

	public abstract String getString();

	/**
	 * Evaluates the ASTNode as an expression. If the expression contains
	 * variables, the {@link IEvaluationContext} should resolve these variables
	 * at runtime.<br>
	 * This can be verified by calling canEvaluate(context).<br>
	 *  Subclasses should override the doEvaluate() method to provide an implementation for
	 * a node.
	 * @param context
	 *            A non-null {@link IEvaluationContext}
	 * @return The result of evaluating the expression
	 * @throws IllegalStateException
	 *             if the ASTNode cannot evaluate the expression
	 */
	public final ASTNumber evaluate(IEvaluationContext context) {
		return doEvaluate(context);
	}

	/**
	 * A convenience method, fully equivalent to :
	 * 
	 * <pre>
	 * evaluate(IEvaluationContext.NULL_CONTEXT)
	 * </pre>
	 * 
	 * and should only be used where the node and its descendants contain only
	 * literal numerical values (i.e., no variables requiring resolution).
	 * 
	 * @return The result of evaluating the expression
	 * @throws IllegalStateException
	 *             if the ASTNode cannot evaluate the expression.
	 * @see EvaluationContext
	 */
	public final ASTNumber evaluate() {
		return doEvaluate(IEvaluationContext.NULL_CONTEXT);
	}

	abstract ASTNumber doEvaluate(IEvaluationContext context);

	/**
	 * Boolean test as to whether this AST expression can be evaluated to return
	 * a number. This method recursively explores this node's children. If any
	 * child node cannot be evaluated because: <br>
	 * <ul>
	 * <li>
	 * An incorrect number of children</li>
	 * <li>A variable ( a &lt;ci&gt; term ) cannot be resolved. (I.e., its value
	 * has not been set into its evaluation context. )</li>
	 * <li>This node, or a child node, is an unknown function (type
	 * ASTFunctionType.MISCELLANEOUS).
	 * </ul>
	 * then this method returns <code>false</code>.
	 * 
	 * @param context
	 *            A non-null {@link IEvaluationContext}
	 * @return <code>true</code> if this expression can be evaluated, false
	 *         otherwise.
	 */
	public final boolean canEvaluate(IEvaluationContext context) {
		if ((isVariable() && !context.hasValueFor(getName()))
				|| !hasCorrectNumberChildren() || isFunction()
				&& getType().equals(ASTFunctionType.MISCELLANEOUS)
				&& subclassCanEvaluate(context)) {
			return false;
		} else {
			return canEvaluateChildren(getChildren(), context);

		}
	}

	/**
	 * Subclasses can optionally extend this to provide further tests for
	 * whether a node can be evaluated. By default this method returns
	 * <code>true</code>.
	 * 
	 * @param context
	 *            An {@link IEvaluationContext}
	 * @return A <code>boolean</code>
	 */
	protected boolean subclassCanEvaluate(IEvaluationContext context) {
		return true;
	}

	private boolean canEvaluateChildren(List<ASTNode> children,
			IEvaluationContext context) {
		for (ASTNode node : getChildren()) {
			if (!node.canEvaluate(context)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the number of children of this AST node 
	 * @return an <code>int</code>, &gt;=0
	 */
	public final int getNumChildren() {
		return children.size();
	}

	/**
	 * Gets last (right-hand) child node of this node.
	 * @return the r-h child ASTNode of this node, or <code>null</code> if there
	 *         are no children.
	 */
	public final ASTNode getRightChild() {
		if (isLeaf()) {
			return null;
		} else {
			return children.get(children.size() - 1);
		}
	}

	/**
	 * Predicate returning true or false depending on whether this ASTNode has
	 * the correct number of children for its type.<br>
	 * E.g., for a 'plus' node, number of children should be &gt;= 2 For a 'sin'
	 * node, the correct number of children is 1.
	 * 
	 * @return <code>true</code> if node has correct number of children.
	 * @see <a href="http://www.w3.org/TR/MathML2/chapter4.html"> MathML spec
	 *      </a>
	 */
	public abstract boolean hasCorrectNumberChildren();

	/**
	 * Boolean test for whether this node is a function
	 * 
	 * @return <code>true</code> if this node is a function.
	 */
	public boolean isFunction() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLambda() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Boolean test for whether this node is a logical operator.
	 * 
	 * @return <code>true</code> if this node is a logical operator (and, or,
	 *         xor, not).
	 */
	public boolean isLogical() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Returns if node represents a number: real, rational, integer or
	 * e-notation.
	 * 
	 * @return <code>true</code> if node represents a number.
	 */
	public boolean isNumber() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Boolean test if node represents a relation: ==, !=, &lt;, &lt;=, &gt; or &gt;=.
	 * @return <code>true</code> if node represents a number.
	 */
	public boolean isRelational() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Gets the precedence of this node. This is typically only used for
	 * function and operator nodes
	 * 
	 * @return an integer of the node's precedence in expressions. The default
	 *         is 6.
	 */
	public int getPrecedence() {
		return 6;
	}

	/**
	 * Gets this node as an ASTNumber, if this node's isNumber() returns
	 * <code>true</code>
	 * @return an {@link ASTNumber}
	 */
	public ASTNumber getAsASTNumber() {
		return (ASTNumber) this;
	}

	/**
	 * Gets this node as an ASTFunction, if this node's isFunction() returns
	 * <code>true</code>
	 * @return <code>this</code>
	 */
	public ASTFunction getAsASTFunction() {
		return (ASTFunction) this;
	}
}
