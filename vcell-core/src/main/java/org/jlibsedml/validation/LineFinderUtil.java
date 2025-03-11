package org.jlibsedml.validation;

import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.filter.ElementFilter;
import org.jdom2.located.LocatedElement;
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
			LocatedElement e = (LocatedElement) iter.next();
			if (e.getName().equals(elName)
					&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID) != null
					&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID).getValue().equals(elID)) {
				return e.getLine();
			}

		}
		return 0;
	}
	 
	 int getLineForMathElement( String elName, String elID, Document doc)
	 {
for (Iterator iter = doc.getDescendants(new ElementFilter()); iter
		.hasNext();) {
	LocatedElement e = (LocatedElement) iter.next();
	if (e.getName().equals(elName)
			&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID) != null
			&& e.getAttribute(SEDMLTags.MODEL_ATTR_ID).getValue().equals(elID)) {
		return e.getLine();
	}

}
return 0;
}

}
