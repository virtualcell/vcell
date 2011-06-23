package org.vcell.sybil.models;

import org.openrdf.model.URI;

public interface AnnotationQualifier {
	public URI property();
	public String getLocalName();
	public String getNameSpace();
	public String getDescription();

}
