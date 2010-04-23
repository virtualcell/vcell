package org.vcell.sybil.models.dublincore;

import java.net.URI;
import java.net.URISyntaxException;

import org.vcell.sybil.models.AnnotationQualifier;
import org.vcell.sybil.rdf.RDFBox.PropertyWrapper;
import org.vcell.sybil.rdf.schemas.ProtegeDC;

import com.hp.hpl.jena.rdf.model.Property;

public class DublinCoreQualifier extends PropertyWrapper implements AnnotationQualifier {

	public static DublinCoreQualifier.DateQualifier created = new DublinCoreQualifier.DateQualifier(ProtegeDC.created, "creation date");

	public static class DateQualifier extends DublinCoreQualifier {
		public DateQualifier(Property property, String description) {
			super(property,description);
		}
	}
	
	private String description = null;
	
	public DublinCoreQualifier(Property property, String description) {
		super(property);
		this.description = description;
	}

	public URI getURI() throws URISyntaxException {
		return new URI(property().getURI());
	}
	
	public String getLocalName(){
		return property().getLocalName();
	}

	public String getNameSpace(){
		return property().getNameSpace();
	}
	
	public String getDescription(){
		return description;
	}

	
}
