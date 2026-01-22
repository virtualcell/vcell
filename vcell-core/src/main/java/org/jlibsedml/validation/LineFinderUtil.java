package org.jlibsedml.validation;

import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.located.LocatedElement;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;

class LineFinderUtil {
    private Document doc;

    Document getDoc() {
        return this.doc;
    }

    void setDoc(Document doc) {
        this.doc = doc;
    }

    int getLineForElement(String elName, SId elID, Document doc) {
        for (Element element : doc.getDescendants(new ElementFilter())){
            if (!(element instanceof LocatedElement locatedElement)) continue;
            if (locatedElement.getName().equals(elName)
                    && locatedElement.getAttribute(SedMLTags.MODEL_ATTR_ID) != null
                    && locatedElement.getAttributeValue(SedMLTags.MODEL_ATTR_ID).equals(elID.string())) {
                return locatedElement.getLine();
            }
        }
        return 0;
    }

    int getLineForMathElement(String elName, String elID, Document doc) {
        for (Element element : doc.getDescendants(new ElementFilter())){
            if (!(element instanceof LocatedElement locatedElement)) continue;
            if (locatedElement.getName().equals(elName)
                    && locatedElement.getAttribute(SedMLTags.MODEL_ATTR_ID) != null
                    && locatedElement.getAttribute(SedMLTags.MODEL_ATTR_ID).getValue().equals(elID)) {
                return locatedElement.getLine();
            }
        }
        return 0;
    }

}
