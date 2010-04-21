package org.vcell.sybil.models.dublincore;

import org.vcell.sybil.models.AnnotationQualifier;
import org.vcell.sybil.rdf.RDFBox.PropertyWrapper;

import com.hp.hpl.jena.rdf.model.Property;

public class DublinCoreQualifier extends PropertyWrapper implements AnnotationQualifier {

	public static class DateQualifier extends DublinCoreQualifier {
		public DateQualifier(Property property) { super(property); }
	}
	
	public DublinCoreQualifier(Property property) { super(property); }

	
	
}
