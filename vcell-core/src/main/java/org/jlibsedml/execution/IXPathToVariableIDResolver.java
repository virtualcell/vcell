package org.jlibsedml.execution;

/**
 * Defines methods to extract element identifiers from an XPath expression.
 * @author radams
 *
 */
public interface IXPathToVariableIDResolver {
	
	/**
	 * Should return an identifier given an xpath, or <code>null</code> if no identifier is present in XPath.
	 * @param xpath
	 * @return A <code>String</code> or <code>null</code>.
	 */
	String getIdFromXPathIdentifer(String xpath);
}
