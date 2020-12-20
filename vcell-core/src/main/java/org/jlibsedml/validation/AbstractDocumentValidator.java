package org.jlibsedml.validation;

import org.jdom.Document;
import org.jlibsedml.IIdentifiable;

/**
 * Provides support for getting line number of identifiable elements by linking
 * an element to a line in the XML document.
 * 
 * @author radams
 *
 */
abstract class AbstractDocumentValidator implements ISedMLValidator {

    AbstractDocumentValidator(Document doc) {
        super();
        this.doc = doc;
    }

    private Document doc;

    Document getDoc() {
        return doc;
    }

    int getLineNumberOfError(String elementKind, IIdentifiable identifiable) {
        int line = new LineFinderUtil().getLineForElement(elementKind,
                identifiable.getId(), getDoc());
        return line;
    }
}
