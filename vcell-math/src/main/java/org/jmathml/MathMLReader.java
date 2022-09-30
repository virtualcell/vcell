package org.jmathml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jmathml.ASTFunction.ASTFunctionType;
import org.jmathml.ASTLogical.ASTLogicalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for generating an AST from a MathML representation
 * 
 * @author radams
 *
 */
public class MathMLReader {
	private ASTNode curr, root;
	static Logger log = LoggerFactory.getLogger(MathMLReader.class);

	/**
	 * Static method to convert a <code>String</code> of MathML to a text
	 * representation.
	 * 
	 * @param mathml
	 *            A <code>String</code> of valid MathML
	 * @return A <code>String</code> of C-style math syntax, or an error string
	 *         if it could not be converted.
	 */
	public static String MathMLXMLToText(String mathml) {
		log.info(mathml);
		MathMLReader rdr = new MathMLReader();
		Element rt;
		try {
			rt = rdr.readXML(new StringReader(mathml));
			ASTNode node = rdr.parseMathML(rt);
			return new FormulaFormatter().formulaToString(node);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Could not be formatted";

	}

	/**
	 * Given a <code>String</code> of MathML XML, will return an ASTNode
	 * 
	 * @param mathml
	 *            A string of valid MathML
	 * @return an {@link ASTNode}
	 * @throws IOException
	 *             if parsing or XML formatting errors are found
	 */
	public ASTNode parseMathMLFromString(String mathml) throws IOException {
		Element rt = readXML(new StringReader(mathml));
		return parseMathML(rt);
	}

	/**
	 * Creates a JDOM document from the given XML string (via a Reader)
	 * 
	 * @param reader a {@link Reader}
	 * @return an org.jdom.Element
	 * @throws IOException
	 *             if the JDOM document could not be read.
	 */
	public Element readXML(Reader reader) throws IOException {
		try {
			SAXBuilder builder = new SAXBuilder(false);
			Document sDoc = builder.build(reader);
			Element root = sDoc.getRootElement();
			return root;
		} catch (JDOMException e) {
			e.printStackTrace(System.out);
			throw new IOException(e.getMessage());
		}
	}

	public ASTNode parseMathML(Element mathml) {
		log.debug("Parsing element {}", mathml.getName());
		if (mathml.getName().equals("math")) {
			log.debug("adding math ");
			root = new ASTRootNode();
			curr = root;
		} else if (mathml.getName().equals("plus")) {
			ASTPlus plus = new ASTPlus();
			curr.addChildNode(plus);
			curr = plus;
		} else if (mathml.getName().equals("minus")) {
			ASTMinus minus = new ASTMinus();
			curr.addChildNode(minus);
			curr = minus;
		} else if (mathml.getName().equals("times")) {
			ASTTimes times = new ASTTimes();
			curr.addChildNode(times);
			curr = times;
		} else if (mathml.getName().equals("divide")) {
			ASTDivide divide = new ASTDivide();
			curr.addChildNode(divide);
			curr = divide;
		} else if (mathml.getName().equals("ci")) {
			ASTCi ci = new ASTCi(mathml.getText().trim());
			curr.addChildNode(ci);
		} else if (mathml.getName().equals("cn")) {
			parseNumberElement(mathml);
		} else if (mathml.getName().equals("csymbol")) {
			mathml.getValue();
			ASTSymbol sym = SymbolRegistry.getInstance().createSymbolFor(
					mathml.getAttributeValue("definitionURL"));
			if (sym == null) {
				throw new RuntimeException("Could not create symbol for "
						+ mathml.getAttributeValue("definitionURL"));
			}
			sym.setDefinitionURL(mathml.getAttributeValue("definitionURL"));
			sym.setEncoding(mathml.getAttributeValue("encoding"));
			sym.setName(mathml.getValue());

			curr.addChildNode(sym);
			List<Element> siblingsAndThis = mathml.getParentElement()
					.getChildren();
			Element firstChild = siblingsAndThis.get(0);
			if (firstChild.getName().equals("csymbol")) {
				// is function
				curr = sym;
			}

		} else if (isLogical(mathml)) {
			if (mathml.getName().equals("and")) {
				curr.addChildNode(new ASTLogical(ASTLogicalType.AND));

			} else if (mathml.getName().equals("or")) {
				curr.addChildNode(new ASTLogical(ASTLogicalType.OR));

			} else if (mathml.getName().equals("xor")) {
				curr.addChildNode(new ASTLogical(ASTLogicalType.XOR));

			} else if (mathml.getName().equals("not")) {
				curr.addChildNode(new ASTLogical(ASTLogicalType.NOT));
			}
			curr = curr.getRightChild();
		} else if (isRelational(mathml)) {
			if (mathml.getName().equals("eq")) {
				curr.addChildNode(new ASTRelational(
						ASTRelational.ASTRelationalType.EQ));
			} else if (mathml.getName().equals("neq")) {
				curr.addChildNode(new ASTRelational(
						ASTRelational.ASTRelationalType.NEQ));
			} else if (mathml.getName().equals("gt")) {
				curr.addChildNode(new ASTRelational(
						ASTRelational.ASTRelationalType.GT));
			} else if (mathml.getName().equals("geq")) {
				curr.addChildNode(new ASTRelational(
						ASTRelational.ASTRelationalType.GEQ));
			} else if (mathml.getName().equals("lt")) {
				curr.addChildNode(new ASTRelational(
						ASTRelational.ASTRelationalType.LT));
			} else if (mathml.getName().equals("leq")) {
				curr.addChildNode(new ASTRelational(
						ASTRelational.ASTRelationalType.LEQ));
			}
			curr = curr.getRightChild();
		}
		else if (mathml.getName().equals("logbase")) {
			Element baseValue = (Element) mathml.getChildren().get(0);
			int base = Integer.parseInt(baseValue.getText().trim());
			((ASTFunction.ASTLog) curr).addChildNode(ASTNumber
					.createNumber(base));
		} else if (mathml.getName().equals("piecewise")) {
			log.debug("adding piecewise to {} ", curr.getName());
			curr.addChildNode(new ASTPiecewise());
			curr = curr.getRightChild();
			log.debug("curr is {} " + curr.getName());
		} else if (mathml.getName().equals("piece")) {
			log.debug("adding piece  to {}", curr);
			curr.addChildNode(new ASTPiece());
			curr = curr.getRightChild();
			log.debug("curr is {} " + curr.getName());
		} else if (mathml.getName().equals("otherwise")) {
			log.debug("adding otherwise to {}", curr);
			curr.addChildNode(new ASTOtherwise());
			curr = curr.getRightChild();
			log.debug("curr is {} " + curr.getName());
		
		} else if (isFunction(mathml)) {
			ASTFunctionType type = ASTFunction.getFunctionTypeForName(mathml
					.getName());
			ASTNode toAdd = null;
			if (type.equals(ASTFunctionType.LOG)) {
				List<Element> siblings = ((Element) mathml.getParent())
						.getChildren();
				for (Element siblin : siblings) {
					if (siblin.getName().equals("logbase")) {
						Element baseValue = (Element) siblin.getChildren().get(
								0);
						int base = Integer.parseInt(baseValue.getText().trim());
						toAdd = ASTFunction.createASTLog(base);
					}
				}
			} else if (type.equals(ASTFunctionType.ROOT)) {
				List<Element> siblings = ((Element) mathml.getParent())
						.getChildren();
				for (Element siblin : siblings) {
					if (siblin.getName().equals("degree")) {
						Element baseValue = (Element) siblin.getChildren().get(
								0);
						int degree = Integer.parseInt(baseValue.getText()
								.trim());
						toAdd = ASTFunction.createASTRoot(degree);
					}
				}
			}
			if (toAdd == null) {
				toAdd = ASTFunction.createFunctionNode(type);
			}

			curr.addChildNode(toAdd);
			curr = toAdd;
		}

		List<Element> els = mathml.getChildren();
		log.trace("Iterating over {} children", els.size());
		for (int i = 0; i < mathml.getChildren().size(); i++) {
			String elName = els.get(i).getName();
			if (shouldIgnore(elName)) {
				log.trace("Ignoring", elName);
				continue;
			}
			log.trace("Recursing into {}", elName);
			parseMathML(els.get(i));
			log.trace("Finished into {}", elName);
			if (curr != root
					&& elName.equals("apply")) {
				curr = curr.getParentNode();
				log.trace("Finished apply, curr is now {}", curr.getName());
			} else if (curr != root
					&&(elName.equals("piece") ||
							elName.equals("otherwise")
							||
							elName.equals("piecewise"))) {
				curr = curr.getParentNode();
				log.trace("Finished {}, curr is now {}", els.get(i).getName(), curr.getName());
			}
		}
		
		return curr;
	}

	private boolean shouldIgnore(String name) {
		return name.equals("logbase") || name.equals("degree");
	}

	private boolean isLogical(Element mathml) {
		return mathml.getName().equals("and") || mathml.getName().equals("or")
				|| mathml.getName().equals("not")
				|| mathml.getName().equals("xor");
	}

	private boolean isRelational(Element mathml) {
		return mathml.getName().equals("eq") || mathml.getName().equals("neq")
				|| mathml.getName().equals("geq")
				|| mathml.getName().equals("gt")
				|| mathml.getName().equals("leq")
				|| mathml.getName().equals("lt");
	}

	private boolean isFunction(Element mathml) {
		return ASTFunction.getFunctionTypeForName(mathml.getName()) != null
				&& !ASTFunction.getFunctionTypeForName(mathml.getName())
						.equals(ASTFunctionType.MISCELLANEOUS);
	}

	private void parseNumberElement(final Element mathml) {
		log.trace("Adding cn to {}", curr.getName());
		if (mathml.getAttribute("type") != null
				&& mathml.getAttribute("type").getValue().equals("integer")) {
			ASTNumber val = ASTNumber.createNumber(Integer.parseInt(mathml
					.getText().trim()));
			curr.addChildNode(val);
		} else if (mathml.getAttribute("type") != null
				&& mathml.getAttribute("type").getValue().equals("rational")) {
			if (mathml.getContent().size() == 3) {
				int numerator = Integer.parseInt(((Text) mathml.getContent()
						.get(0)).getText());
				int denom = Integer
						.parseInt(((Text) mathml.getContent().get(2)).getText());
				ASTNumber frac = ASTNumber.createNumber(numerator, denom);
				curr.addChildNode(frac);
			}
		} else if (mathml.getAttribute("type") != null
				&& mathml.getAttribute("type").getValue().equals("e-notation")) {
			if (mathml.getContent().size() == 3) {
				double mantissa = Double.parseDouble(((Text) mathml
						.getContent().get(0)).getText());
				double exp = Double.parseDouble(((Text) mathml.getContent()
						.get(2)).getText());

				ASTNumber frac = ASTNumber.createNumber(mantissa, exp);
				curr.addChildNode(frac);
			}
		} else {
			ASTNumber val = null;
			if (mathml.getText().trim().equalsIgnoreCase("pi")) {
				val = ASTNumber.createNumber(Math.PI);
			} else if (mathml.getText().trim().equalsIgnoreCase("exponentiale")) {
				val = ASTNumber.createNumber(Math.E);
			} else if (mathml.getText().trim().equalsIgnoreCase("notanumber")) {
				val = ASTNumber.createNumber(Double.NaN);

			} else if (mathml.getText().trim().equalsIgnoreCase("infinity")) {
				val = ASTNumber.createNumber(Double.POSITIVE_INFINITY);

			} else {
				val = ASTNumber.createNumber(Double.parseDouble(mathml
						.getText()));
			}
			curr.addChildNode(val);
		}
	}
}
