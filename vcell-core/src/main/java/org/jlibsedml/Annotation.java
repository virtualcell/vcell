package org.jlibsedml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Encapsulates an annotation element which can be used for software tools to 
 *   annotate a SED-ML document.
 * The content of an annotation need  not be human-readable.<br/>
 * For human readable content, the 'Notes' elements should be used.<br/>
 * Elements added to this Annotation should be in their own XML namespace. 
 *
 */
public final class Annotation {
	
	private List<Element> elements = new ArrayList<Element>();
	

	/**
	 * Accepts an  annotation element and adds it as the first XML element in the <code>List</code>
	 *  of XML elements.
	 * @param argAnnotElement  a non-null {@link Element}
	 */
	public Annotation(Element argAnnotElement) {
		if(SEDMLElementFactory.getInstance().isStrictCreation())
			Assert.checkNoNullArgs(argAnnotElement);
		elements.add(0, argAnnotElement);
	}
	

	
	/**
	 * Retrieves the first annotation element for this object.
	 * @return a non-null {@link Element}
	 * @deprecated Use get getAnnotationElementList() from now on.
	 */
	public Element getAnnotationElement() {
		
		    return elements.get(0);
		
	}
	
	/**
     * Retrieves the first annotation element for this object.
     * @return a non-null unmodifiable <code>List</code>{@link Element}.
     */
    public List<Element> getAnnotationElementsList() {
            return Collections.unmodifiableList(elements);
    }
    
    /**
     * Will remove this element based on its namespace. In section 2.3.3.3 of the L1V1 specification,
     *  it is recommended that an <code>Annotation</code> only has one top-level element in a particular
     *   namespace.
     * @param el The <code>Element</code> to remove.
     * @return <code>true</code> if an element in the same namespace as this was removed.
     */
    public boolean removeElement (Element el){
        Namespace ns = el.getNamespace();
        for (Element child: elements) {
            if (child.getNamespace().equals(el.getNamespace())){
                return elements.remove(child);
            }
        }
        return false;
    }


}
