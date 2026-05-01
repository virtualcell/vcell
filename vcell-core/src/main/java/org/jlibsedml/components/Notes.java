package org.jlibsedml.components;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.SedMLTags;

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
public final class Notes implements SedGeneralClass, Cloneable {

    private List<Element> elements;

	/**
	 * @param argNotesElement
	 *            A non-null Element contains XHTM. The children will be detached and have its namespace set
	 *            to "http://www.w3.org/1999/xhtml"
	 */
	public Notes(Element argNotesElement) {
        this.elements = new ArrayList<>();
		if (SedMLElementFactory.getInstance().isStrictCreation()) SedGeneralClass.checkNoNullArgs(argNotesElement);
        // We can NOT use a for-each loop, as we can end up causing a "concurrentAccessViolation" exception
        while (!argNotesElement.getChildren().isEmpty()) {
            Element elementToAdd = argNotesElement.getChildren().get(0).detach();
            this.elements.add(elementToAdd.setNamespace(Namespace.getNamespace(SedMLTags.XHTML_NS)));
        }
	}

    public Notes clone() throws CloneNotSupportedException {
        Notes clone = (Notes) super.clone();
        clone.elements = new ArrayList<>(this.elements);
        return clone;
    }

    public void addNote(Element note) {
        this.elements.add(note);
    }

	/**
	 * Get an unmodifiable list of sub element for this Notes object, will not return null.
	 * 
	 * @return An {@link List} of {@link Element}s
	 */
	public List<Element> getNotesElements() {
		return Collections.unmodifiableList(this.elements);
	}

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.NOTES;
    }
}
