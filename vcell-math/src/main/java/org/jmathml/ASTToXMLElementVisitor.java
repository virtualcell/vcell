package org.jmathml;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jmathml.ASTFunction.ASTFunctionType;
import org.jmathml.ASTFunction.ASTLog;
import org.jmathml.ASTFunction.ASTRoot;

/**
 * <p>
 * Class for converting ASTNodes of math elements into MathML elements. An
 * ASTNode passed into one of the methods will be wrapped in a
 * <code>&lt;math&gt;</code> element.
 * </p>
 * 
 * <p>
 * This class is stateful. For separate invocations, either use a new object or
 * call clear() before using again.
 * </p>
 * 
 * Usage:
 * 
 * <pre>
 * ASTToXMLElementVisitor visitor = new ASTToXMLElementVisitor();
 * // use a created ASTNode
 * plusNode.accept(visitor);
 * // now get XML
 * Element element = visitor.getElement();
 * // clear for reuse
 * visitor.clear();
 * </pre>
 * 
 * @author radams
 *
 */
public class ASTToXMLElementVisitor extends ASTVisitor {
	private Element xml = new Element("math");
	private Element currEl = xml;

	static Namespace MATHML_NS = Namespace.getNamespace("math",
			"http://www.w3.org/1998/Math/MathML");

	/**
	 * Returns the generated Element. If this method is called before visit(),
	 * this will return an empty &lt;math&gt; element.
	 * 
	 * @return An Element constructed from the ASTNode.
	 */
	public Element getElement() {
		return xml;
	}

	public boolean visit(ASTOperator node) {
		Element operator = new Element("apply");
		operator.setNamespace(MATHML_NS);
		Element operatorEl = new Element(node.getName());
		operatorEl.setNamespace(MATHML_NS);
		operator.addContent(operatorEl);
		currEl.addContent(operator);
		currEl = operator;

		return true;
	}

	public boolean endVisit(ASTOperator node) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTRootNode node) {
		xml.setNamespace(MATHML_NS);
		return true;
	}

	public boolean endVisit(ASTRootNode node) {
		return true;
	}

	public boolean visit(ASTNumber node) {
		if (parentIsLogToOtherBase(node) || parentIsRootToOtherDegree(node)) {
			return true;
		}
		Element cn;
		cn = new Element("cn");
		if (node.isInteger()) {
			cn.setAttribute("type", "integer");
			cn.setText(node.getName());
		} else if (node.isRational()) {
			cn.setAttribute("type", "rational");
			cn.setText(Integer.toString(node.getNumerator()));
			Element sep = new Element("sep");
			cn.addContent(sep);
			cn.addContent(Integer.toString(node.getDenominator()));
		} else if (node.isENotation()) {
			cn.setAttribute("type", "e-notation");
			cn.setText(Double.toString(node.getMantissa()));
			Element sep = new Element("sep");
			cn.addContent(sep);
			cn.addContent(Integer.toString(node.getExponent()));
		} else if (node.isE()) {
			cn = new Element("exponentiale");
		} else if (node.isPI()) {
			cn = new Element("pi");
		} else if (node.isTruth()) {
			cn = new Element("true");
		} else if (node.isFalse()) {
			cn = new Element("false");
		} else if (node.isNaN()) {
			cn = new Element("notanumber");
		} else if (node.isInfinity()) {
			cn = new Element("infinity");
		} else {
			cn.setAttribute("type", "real");
			cn.setText(node.getName());
		}
		cn.setNamespace(MATHML_NS);
		currEl.addContent(cn);
		return true;
	}

	private boolean parentIsRootToOtherDegree(ASTNumber node) {
		return node.getParentNode() != null
				&& node.getParentNode().getType().equals(ASTFunctionType.ROOT)
				&& !node.getParentNode().isSqrt() && node.getIndex() == 0;
	}

	private boolean parentIsLogToOtherBase(ASTNumber node) {
		return node.getParentNode() != null
				&& node.getParentNode().getType().equals(ASTFunctionType.LOG)
				&& !node.getParentNode().isLog10() && node.getIndex() == 0;
	}

	public boolean endVisit(ASTNumber node) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTFunction node) {
		Element app = new Element("apply");
		app.setNamespace(MATHML_NS);
		Element func = new Element(node.getName());
		func.setNamespace(MATHML_NS);
		app.addContent(func);
		if (node.getType().equals(ASTFunctionType.LOG)) {
			createXMLForLogbase(node, app);
		} else if (node.getType().equals(ASTFunctionType.ROOT)) {
			createXMLForRootDegree(node, app);
		}
		currEl.addContent(app);
		currEl = app;
		return true;
	}

	private void createXMLForRootDegree(ASTFunction node, Element app) {
		ASTRoot root = (ASTRoot) node;
		if (!root.isSqrt()) {

			Element degreeEl = new Element("degree");

			degreeEl.setNamespace(MATHML_NS);
			Element cn = new Element("cn");
			cn.setNamespace(MATHML_NS);
			cn.setText(node.firstChild().getString());
			degreeEl.addContent(cn);
			app.addContent(degreeEl);
		}

	}

	private void createXMLForLogbase(ASTFunction node, Element app) {
		ASTLog log = (ASTLog) node;
		if (!log.isLog10()) {

			Element logbase = new Element("logbase");
			logbase.setNamespace(MATHML_NS);
			Element cn = new Element("cn");
			cn.setNamespace(MATHML_NS);
			cn.setText(node.firstChild().getString());
			logbase.addContent(cn);
			app.addContent(logbase);
		}
	}

	public boolean endVisit(ASTFunction node) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	/**
	 * Restores xml to a new empty math element.
	 */
	public void clear() {
		xml = new Element("math");
		currEl = xml;

	}

	public boolean visit(ASTCi astIdentifiable) {
		Element ci = new Element("ci");
		ci.setNamespace(MATHML_NS);
		ci.setText(astIdentifiable.getString());
		currEl.addContent(ci);
		return true;
	}

	public boolean endVisit(ASTCi astIdentifiable) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTRelational rel) {
		Element app = new Element("apply");
		app.setNamespace(MATHML_NS);
		Element func = new Element(rel.getName());
		func.setNamespace(MATHML_NS);
		app.addContent(func);
		currEl.addContent(app);
		currEl = app;
		return true;

	}

	public boolean endVisit(ASTRelational rel) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTLogical log) {
		Element app = new Element("apply");
		app.setNamespace(MATHML_NS);
		Element func = new Element(log.getName());
		func.setNamespace(MATHML_NS);
		app.addContent(func);
		currEl.addContent(app);
		currEl = app;
		return true;

	}

	public boolean endVisit(ASTLogical log) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTSymbol astSymbol) {
		if (astSymbol.isSymbolFunction()) {
			Element app = new Element("apply");
			app.setNamespace(MATHML_NS);
			Element el = createCsymbolXML(astSymbol);
			app.addContent(el);
			currEl.addContent(app);
			currEl = app;
			return true;
		} else {
			Element el = createCsymbolXML(astSymbol);
			currEl.addContent(el);

			return true;
		}

	}

	Element createCsymbolXML(ASTSymbol astSymbol) {
		Element el = new Element("csymbol");
		el.setNamespace(MATHML_NS);
		if (astSymbol.getDefinitionURL() != null) {
			el.setAttribute("definitionURL", astSymbol.getDefinitionURL());
		}
		if (astSymbol.getEncoding() != null) {
			el.setAttribute("encoding", astSymbol.getEncoding());
		}
		el.setText(astSymbol.getName());
		return el;
	}

	public boolean endVisit(ASTSymbol astSymbol) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTOtherwise other) {
		Element el = new Element("otherwise");
		el.setNamespace(MATHML_NS);
		currEl.addContent(el);
		currEl = el;
		return true;
	}

	public boolean endVisit(ASTOtherwise other) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTPiecewise node) {
		Element el = new Element("piecewise");
		el.setNamespace(MATHML_NS);
		currEl.addContent(el);
		currEl = el;
		return true;
	}

	public boolean endVisit(ASTPiecewise node) {
		currEl = (Element) currEl.getParent();
		return true;
	}

	public boolean visit(ASTPiece astPiece) {
		Element el = new Element("piece");
		el.setNamespace(MATHML_NS);
		currEl.addContent(el);
		currEl = el;
		return true;
	}

	public boolean endVisit(ASTPiece astPiece) {
		currEl = (Element) currEl.getParent();
		return true;
	}

}
