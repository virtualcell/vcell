package org.jlibsedml.extensions;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

/**
 * E.g., for a given xml: <br/>
 * <code>
 *  &lt;a &gt; &lt;b name='c'/&gt; &lt;/a &gt;
 * </code>
 * <br/>
 * for attribute 'c' will return the XPath /a:a/a:b/[name='c']. <br/>
 * If an element has no attributes, selecting the element will select it.
 *  In this case, it is assumed that it is a unique element, and no further
 *   XPath predicate is added.
 * @author radams
 *
 */
public class ElementIdentifiedBySelectedAttributePolicy implements
		ISelectionChangedPolicy {
    public ElementIdentifiedBySelectedAttributePolicy(Document doc) {
		super();
		this.doc = doc;
	}

	private Document doc;
	public String getDescription() {
		return "Choose an attribute. The XPath will identify the attribute's enclosing element" +
				" by this attribute value";
	}

	/**
	 * Gets String for XPath or <code>null</code> if XML could not be parsed.
	 */
	public String getXPathForAttribute(Attribute attribute) {
		
		
				return new XMLUtils().getXPathForElementIdentifiedByAttribute(
						attribute.getParent(), doc, attribute);
			
			
		
		
	}

	/**
	 * Returns null
	 */
	public String getXPathForElement(Element selected) {
		if(selected.getAttributes().isEmpty()){
			return new XMLUtils().getXPathFor(selected, doc);
		} else {
			return null;
		}
	}

}
