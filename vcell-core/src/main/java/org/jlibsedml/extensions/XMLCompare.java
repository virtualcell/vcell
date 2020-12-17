package org.jlibsedml.extensions;

import java.util.List;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class XMLCompare {

    public boolean equals(Element e1, Element e2) {
        if (!e1.getName().equals(e2.getName())) {
            return false;
        }
        if (!e1.getNamespace().equals(e2.getNamespace())) {
            return false;
        }
        if (!e1.getTextNormalize().equals(e2.getTextNormalize())) {
            return false;
        }
        List<Attribute> e1Atts = e1.getAttributes();
        List<Attribute> e2Atts = e2.getAttributes();
        for (Attribute e1a : e1Atts) {
            boolean found = false;
            for (Attribute e2a : e2Atts) {
                if (equals(e1a, e2a)) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        for (Attribute e2a : e2Atts) {
            boolean found = false;
            for (Attribute e1a : e1Atts) {
                if (equals(e1a, e2a)) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;

    }

    /**
     * To be equal, two attributes must have the same NS, name and value
     * @param a1
     * @param a2
     * @return <code>true</code> if attributes are equal, <code>false</code> otherwise
     */
    public boolean equals(Attribute a1, Attribute a2) {
        return a1.getName().equals(a2.getName())
                && a1.getValue().equals(a2.getValue())
                && a1.getNamespace().equals(a2.getNamespace());
    }

    /**
     * Given two elements, will test whether they or any sub-elements differ
     * from each other in terms of element name or attribute content.
     * 
     * @param differences
     *            A <code>Set</code> into which will be put any elements from
     *            changedElement that differ in terms of name, attributes or
     *            text value from their equivalent referenceElement.
     * @param changedElement
     * @param referenceElement
     */
    public void compareElements(Set<Element> differences,
            Element changedElement, Element referenceElement) {
        if (!equals(referenceElement, changedElement)) {
            differences.add(changedElement);
            Element par = changedElement;
            while ((par = par.getParentElement()) != null) {
                differences.add(par);
            }
        }
        List<Element> changedKids = changedElement
                .getContent(new ElementFilter());
        List<Element> refKids = referenceElement
                .getContent(new ElementFilter());
            
            for (int i = 0; i < changedKids.size(); i++) {
                Element chKid = changedKids.get(i);
                if(i < refKids.size());
                Element refKid = refKids.get(i);
                compareElements(differences, chKid, refKid);
            }

            
        }
        
        

    

    private boolean changedHasAdded(List<Element> changedKids,
            List<Element> refKids) {
       return changedKids.size() > refKids.size();
           
    }

    private boolean equalSize(List<Element> changedKids, List<Element> refKids) {
        return changedKids.size() == refKids.size();
    }

}
