package org.vcell.sybil.models;

import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.rdf.model.Property;

public interface AnnotationQualifier {
	public Property property();
	public URI getURI() throws URISyntaxException;
	public String getLocalName();
	public String getNameSpace();
	public String getDescription();

}
