package org.jlibsedml;

import javax.xml.xpath.XPathExpressionException;

/**
 * Library independent exception to throw when an error occurs evaluating XPath.
 * @author radams
 *
 */
public class XPathEvaluationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XPathEvaluationException(String string, XPathExpressionException e) {
		super (string, e);
	}

}
