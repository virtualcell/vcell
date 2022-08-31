package org.jlibsedml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A collection of utility methods for applying XPath transformations to XML
 * based models.
 * 
 * @author radams
 * 
 */
class ModelTransformationUtils {

	/**
	 * Will delete all elements matching the supplied XPath expression
	 * 
	 * @param originalModel
	 *            A String of the XML of the model to be altered
	 * @param xPathToElementToDelete
	 *            A valid XPath String defining the elements to be removed
	 * @param xpath
	 *            A {@link XPath}
	 * @return A <code>String</code> of the modified model.
	 * @throws XMLException
	 *             if errors occur reading or writing XML
	 * @throws XPathEvaluationException
	 *             if errors occur applying the XPath expression.
	 */
	static public void deleteXMLElement(final Document doc,
			String xPathToElementToDelete,  XPath xpath)
			throws XMLException, XPathEvaluationException {
		Object result = applyXpath(xPathToElementToDelete, xpath, doc );
		NodeList nodes = (NodeList) result;
		removeChild(nodes);
	}

	private static void removeChild(NodeList nodes) {

		for (int i = 0; i < nodes.getLength(); i++) {
			nodes.item(i).getParentNode().removeChild(nodes.item(i));
		}
	}

	static public void changeXMLElement(final Document doc, NewXML newXML,
			String xPathToRemove,  XPath xpath)
			throws XMLException, XPathEvaluationException {
		Object result = applyXpath(xPathToRemove, xpath, doc);
		NodeList nodes = (NodeList) result;

		try {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node parent = nodes.item(i).getParentNode();
				removeChild(nodes);
				for (org.jdom.Element el : newXML.getXml()) {
				    el.setNamespace(Namespace.NO_NAMESPACE);
					String elAsString = new XMLOutputter().outputString(el);
					Node imported = doc.importNode(
							createElementFromString(elAsString), true);

					parent.appendChild(imported);
				}
			}

		} catch (Exception e) {
			throw new XMLException("Couldn't generate new XML model", e);
		}

	}

	// returned object is a nodeList
	private static Object applyXpath(String xPathToElementToDelete,
			XPath xpath, Document doc)
			throws XPathEvaluationException {
		
		XPathExpression expr;
		Object result;
		try {
			expr = xpath.compile(xPathToElementToDelete);

			result = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new XPathEvaluationException("Could not evaluate XPath", e);
		}
		return result;
	}

	/**
	 * Inserts a block of XML in the model.
	 * 
	 * @param model
	 *            A String of the XML Representation of the model
	 * @param xmlToAdd
	 *            The XML to insert
	 * @param xPathToParentElement
	 *            An XPath expression to the parent elements to insert the XML
	 *
	 * @return A <code>String</code> of the model XML with the added XML element
	 * @throws XMLException
	 *             if errors occur reading or writing XML
	 * @throws XPathEvaluationException
	 *             if errors occur applying the XPath expression.
	 */
	static public void addXMLelement(final Document doc, String xmlToAdd,
			String xPathTpoParentElement,  XPath xpath)
			throws XMLException, XPathEvaluationException {

		Object result = applyXpath(xPathTpoParentElement, xpath, doc);
		NodeList nodes = (NodeList) result;
		try {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node imported = doc.importNode(
						createElementFromString(xmlToAdd), true);

				nodes.item(i).appendChild(imported);
			}

		} catch (Exception e) {
			throw new XMLException("Couldn't generate new XML model", e);
		}
	}

	private static Node createElementFromString(String toAdd)
			throws SAXException, IOException, ParserConfigurationException {
		Element node = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(
						new ByteArrayInputStream(toAdd.getBytes("UTF-8")))
				.getDocumentElement();
		

		return node;

	}

	static public String exportChangedXMLAsString(Document doc)
			throws TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		String xmlString;
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// initialize StreamResult with File object to save to file
		StreamResult result2 = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result2);

		xmlString = result2.getWriter().toString();
		return xmlString;
	}

	static public void applyAttributeChange(Document doc, XPath xpath, Change change)
			throws XPathExpressionException {
		ChangeAttribute chAtrr = (ChangeAttribute) change;
		XPathExpression expr;

		expr = xpath.compile(change.getTargetXPath().getTargetAsString());

		final Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			nodes.item(i).setNodeValue(chAtrr.getNewValue());
		}
	}

	static public Document getXMLDocumentFromModelString(final String originalModel)
			throws ParserConfigurationException, UnsupportedEncodingException,
			IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Writer out = new OutputStreamWriter(bos, "UTF8");
		out.write(originalModel);
		out.close();
		bos.close();

		Document doc = builder
				.parse(new ByteArrayInputStream(bos.toByteArray()));
		return doc;
	}

}
