package org.vcell.sybil.models.dublincore;

import org.openrdf.model.URI;
import org.vcell.sybil.models.AnnotationQualifier;
import org.vcell.sybil.rdf.RDFBox.PropertyWrapper;
import org.vcell.sybil.rdf.schemas.ProtegeDC;

public class DublinCoreQualifier extends PropertyWrapper implements AnnotationQualifier {

	public static DublinCoreQualifier.DateQualifier created = 
		new DublinCoreQualifier.DateQualifier(ProtegeDC.created, "creation date");

	public static class DateQualifier extends DublinCoreQualifier {
		public DateQualifier(URI property, String description) {
			super(property,description);
		}
	}
	
	private String description = null;
	
	public DublinCoreQualifier(URI property, String description) {
		super(property);
		this.description = description;
	}

	public String getLocalName(){
		return property().getLocalName();
	}

	public String getNameSpace(){
		return property().getNamespace();
	}
	
	public String getDescription(){
		return description;
	}

	
}
