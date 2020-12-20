package org.jlibsedml.validation;

import java.util.Iterator;

import org.jdom.Document;
import org.jdom.contrib.input.LineNumberElement;
import org.jdom.filter.ElementFilter;
import org.jlibsedml.SEDMLTags;

class LineFinderUtil {
	private Document doc;

	Document getDoc() {
		return doc;
	}

	void setDoc(Document doc) {
		this.doc = doc;
	}

	 int getLineForElement( String elName, String elID, Document doc)
			 {
		for (Iterator iter = doc.getDescendants(new ElementFilter()); iter
				.hasNext();) {
			LineNumberElement e = (LineNumberElement) iter.next();
			if (e.getName().equals(elName)
					&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID) != null
					&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID).getValue().equals(elID)) {
				return e.getStartLine();
			}

		}
		return 0;
	}
	 
	 int getLineForMathElement( String elName, String elID, Document doc)
	 {
for (Iterator iter = doc.getDescendants(new ElementFilter()); iter
		.hasNext();) {
	LineNumberElement e = (LineNumberElement) iter.next();
	if (e.getName().equals(elName)
			&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID) != null
			&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID).getValue().equals(elID)) {
		return e.getStartLine();
	}

}
return 0;
}

}
