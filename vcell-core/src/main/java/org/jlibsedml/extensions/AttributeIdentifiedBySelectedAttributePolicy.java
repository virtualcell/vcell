package org.jlibsedml.extensions;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

/**
 * E.g., for a given xml: <br/>
 * <code>
 *  &lt;a &gt; &lt;b name='c'/&gt; &lt;/a &gt;
 * </code>
 * <br/>
 * for attribute 'c' will return the XPath /a:a/a:b/[name='c']
 * @author radams
 *
 */
public class AttributeIdentifiedBySelectedAttributePolicy implements
		ISelectionChangedPolicy {
	private final static Logger lg = LogManager.getLogger(AttributeIdentifiedBySelectedAttributePolicy.class);
  
	public AttributeIdentifiedBySelectedAttributePolicy(Document doc,Attribute attIdentifier) {
		super();
		this.doc = doc;
		this.attIdentifier = attIdentifier;
	}

	private Document doc;
	private Attribute attIdentifier;
	public String getDescription() {
		return "Choose an attribute. The XPath will identify this attribute, and identify its " +
				" enclosing element by a second attribute";
	}

	/**
	 * Gets String for XPath or <code>null</code> if XML could not be parsed.
	 */
	public String getXPathForAttribute(Attribute att) {
		
			try {
				return new XMLUtils().getXPathForAttributeIdentifiedByAttribute(att.getParent(),
						att, doc, attIdentifier);
			} catch (JDOMException | IOException e) {
				lg.error(e);
			}
			return null;
		
		
	}

	/**
	 * Returns null
	 */
	public String getXPathForElement(Element selected) {
		// TODO Auto-generated method stub
		return null;
	}

}
