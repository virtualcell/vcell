package org.jlibsedml;

import java.util.Collections;
import java.util.List;

import org.jdom.Element;

/**
 * Encapsulates a NewXML element in the SED-ML specification. This object can contain one or more XML elements.
 *  For example,
 *  *<pre>
   &lt;addXML target="/sbml:sbml/sbml:model/sbml:listOfParameters">
 *  &lt;newXML>
 *    &lt;parameter metaid="metaid_0000010"id="V_mT"value="0.7"/>
 *    &lt;parameter metaid="metaid_0000011"id="V_mT2"value="0.71"/>
*    &lt;/newXML>
*  &lt;/addXML&gt;
*</pre>
will produce a NewXML object with a <code>List</code> of two {@link Element}s, all of which will be added as children of the
 the target element.
 * @author radams
 *
 */
public final class NewXML  {
	
	public NewXML( List<Element> xml) {
		super();
		this.xml = xml;
	}

	/**
	 * Getter for the model XML fragments to be inserted into the model XML.
	 * @return An unmodifiable 
	 */
	public  List<Element> getXml() {
		return Collections.unmodifiableList(xml);
	}
	/**
	 * Gets the number of top-level XML elements contained in this object.
	 * @return an integer, >= 0
	 */
	public int numElements() {
		return xml.size();
	}

	private final List<Element> xml;
	
	

}
