package org.jmathml;

/**
 * Represents a <code>csymbol</code> MathML element. This can be extended by
 * clients who wish to supply custom symbol functions.
 * 
 * Subclasses should override the following methods:
 * <ul>
 * <li>doEvaluate(IEvaluationContext context)
 * <li>hasCorrectNumberChildren()
 * </ul>
 * and may consider overriding<br>
 * subclassCanEvaluate(IEvaluationContext context) <br>
 * which can provide additional information on whether a symbol can evaluated.
 * 
 * @author radams
 *
 */
public class ASTSymbol extends ASTNode {

	private String encoding, definitionURL, id;
	private boolean isSymbolFunction = true;

	public enum AST_SYMBOL_TYPE implements ASTTypeI {
		AST_SYMBOL;

		public String getString() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	ASTSymbol(ASTTypeI type) {
		super(AST_SYMBOL_TYPE.AST_SYMBOL);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Default constructor for constructing a symbol representing a function.
	 * 
	 * @param id the id
	 */
	public ASTSymbol(String id) {
		super(AST_SYMBOL_TYPE.AST_SYMBOL);
		this.id = id;
		setName(id);
	}

	/**
	 * 
	 * @param id the id
	 * @param isSymbolValue
	 *            A <code>boolean</code> which is <code>true</code> if the
	 *            created symbol will be a value, rather than a function. If
	 *            this argument is <code>false</code>, it is equivalent to <br>
	 * 
	 *            <pre>
	 * public ASTSymbol (String id)
	 * </pre>
	 * @since 2.1
	 * 
	 */
	public ASTSymbol(String id, boolean isSymbolValue) {
		super(AST_SYMBOL_TYPE.AST_SYMBOL);
		this.id = id;
		this.isSymbolFunction = !isSymbolValue;
		setName(id);
	}

	@Override
	boolean doAccept(ASTVisitor v) {
		if (v.visit(this)) {
			for (ASTNode n : getChildren()) {
				if (!n.accept(v)) {
					break;
				}
			}
		}
		return v.endVisit(this);
	}

	public final String getEncoding() {
		return encoding;
	}

	public final void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public final String getDefinitionURL() {
		return definitionURL;
	}

	public final void setDefinitionURL(String definitionURL) {
		this.definitionURL = definitionURL;
	}

	@Override
	public String getString() {
		return getName();
	}

	/**
	 * Can be overridden by clients
	 */
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() == 0;
	}

	@Override
	protected ASTNumber doEvaluate(IEvaluationContext context) {
		return ASTNumber
				.createNumber(context.getValueFor(id).iterator().next());
	}

	public final boolean isSymbol() {
		return true;
	}

	/**
	 * @return <code>true</code> if this symbol represents a function, or
	 *         <code>false</code> if it represents a value to be applied. 
	 * @since 2.1
	 */
	public final boolean isSymbolFunction() {
		return isSymbolFunction;
	}

	/**
	 * Boolean test for whether this symbol represents a value. Only one of
	 * <code>isSymbolValue()</code> and <code>isSymbolFunction</code> can be
	 * true at once.
	 * 
	 * @return <code>true</code> if this symbol represents a value, or
	 *         <code>false</code> if it represents a function to be applied.
	 * @since 2.1
	 */
	public final boolean isSymbolValue() {
		return !isSymbolFunction;
	}

	/**
	 * Subclasses can override if the functionality requires variables that are
	 * vectors.
	 * 
	 * @return <code>true</code> if this is a vector operation.
	 */
	public boolean isVectorOperation() {
		// TODO Auto-generated method stub
		return false;
	}

}
