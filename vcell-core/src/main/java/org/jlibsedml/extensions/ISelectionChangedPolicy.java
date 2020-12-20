package org.jlibsedml.extensions;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * Interface for policy to adopt when an element or attribute is selected.
 * @author radams
 *
 */
public interface ISelectionChangedPolicy {
	
	
	/**
	 * Can return <code>null</code> if no XPath can be generated with this policy.
	 * @param selected
	 * @return the XPath string
	 */
	public String getXPathForAttribute (Attribute selected);
	
	/**
	 * Can return <code>null</code> if no XPath can be generated with this policy.
	 * @param selected
	 * @return  the XPath string
	 */
	public String getXPathForElement (Element selected);
	
	/**
	 * Gets description on how selection works
	 * @return  the XPath string
	 */
	public String getDescription();

}
