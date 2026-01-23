package org.jlibsedml.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Element;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.SedMLElementFactory;

/**
 * Encapsulates an annotation element which can be used for software tools to 
 *   annotate a SED-ML document.
 * The content of an annotation need  not be human-readable.<br/>
 * For human-readable content, the 'Notes' elements should be used.<br/>
 * Elements added to this Annotation should be in their own XML namespace. 
 *
 */
public final class Annotation implements SedGeneralClass, Cloneable{
	
	private List<Element> elements;
	

	/**
	 * Accepts an  annotation element and adds it as the first XML element in the <code>List</code>
	 *  of XML elements.
	 * @param argAnnotElement  a non-null {@link Element}
	 */
	public Annotation(Element argAnnotElement) {
        this.elements = new ArrayList<>();
		if(SedMLElementFactory.getInstance().isStrictCreation()) SedGeneralClass.checkNoNullArgs(argAnnotElement);
        for (Element element : argAnnotElement.getChildren()) {
            this.elements.add(element.detach());
        }
        this.elements.add(0, argAnnotElement);
	}

    public Annotation clone() throws CloneNotSupportedException {
        Annotation clone = (Annotation) super.clone();
        clone.elements = new ArrayList<>(this.elements);
        return clone;
    }



    /**
     * Get an unmodifiable list of sub element for this Annotation object, will not return null.
     *
     * @return An {@link List} of {@link Element}s
     */
	public List<Element> getAnnotationElements() {
		    return Collections.unmodifiableList(this.elements);
	}

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.ANNOTATION;
    }


//    /**
//     * Will remove this element based on its namespace. In section 2.3.3.3 of the L1V1 specification,
//     *  it is recommended that an <code>Annotation</code> only has one top-level element in a particular
//     *   namespace.
//     * @param el The <code>Element</code> to remove.
//     * @return <code>true</code> if an element in the same namespace as this was removed.
//     */
//    public boolean removeElement (Element el){
//        Namespace ns = el.getNamespace();
//        for (Element child: this.elements) {
//            if (child.getNamespace().equals(el.getNamespace())){
//                return this.elements.remove(child);
//            }
//        }
//        return false;
//    }
}
