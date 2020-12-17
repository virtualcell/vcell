package org.jlibsedml;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Contains notes elements to display human readable information to the user.
 * The content should be XHTML.<br/>
 * Usage is as follows:<br/>
 * Create a new Notes element
 * 
 * <pre>
 * // create some xhtml. E.g.,
 * Element para = new Element(&quot;p&quot;);
 * para.setText(&quot;some comment&quot;);
 * 
 * // create a notes element
 * Notes note = new Notes(para);
 * </pre>
 * 
 * Clients do not have to set the namespace of the XHTML contents, this is performed by this class.
 * 
 */
public final class Notes {

	private Element notesElement = null;

	/**
	 * @param argNotesElement
	 *            A non-null Element. This element will have its namespace set
	 *            to "http://www.w3.org/1999/xhtml"
	 */
	public Notes(Element argNotesElement) {
		if (SEDMLElementFactory.getInstance().isStrictCreation()) {
			Assert.checkNoNullArgs(argNotesElement);
		}
		this.notesElement = argNotesElement;
		notesElement.setNamespace(Namespace.getNamespace(SEDMLTags.XHTML_NS));
	}

	/**
	 * Get the Notes element for this object, will not return null.
	 * 
	 * @return An {@link Element}
	 */
	public Element getNotesElement() {
		return notesElement;
	}
}
