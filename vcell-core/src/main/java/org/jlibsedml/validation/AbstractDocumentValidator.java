package org.jlibsedml.validation;

import org.jdom2.Document;
import org.jlibsedml.IIdentifiable;

/**
 * Provides support for getting line number of identifiable elements by linking
 * an element to a line in the XML document.
 * 
 * @author radams
 *
 */
abstract class AbstractDocumentValidator implements ISedMLValidator {
    private final Document doc;

    AbstractDocumentValidator(Document doc) {
        super();
        this.doc = doc;
    }

    Document getDoc() {
        return this.doc;
    }

    int getLineNumberOfError(String elementKind, IIdentifiable identifiable) {
        return new LineFinderUtil().getLineForElement(elementKind,
                identifiable.getId(), this.getDoc());
    }
}
