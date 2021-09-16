package org.vcell.rest.common;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public interface BiomodelBNGLResource {

	public static final String APPLICATION_BNGL_XML = "application/bngl+xml";
	public static final MediaType VCDOC_MEDIATYPE = MediaType.register(BiomodelBNGLResource.APPLICATION_BNGL_XML, "VCell bngl+xml Document");

	/**
	 * Returns the list of BioModels accessible to this user
	 */

	@Get(APPLICATION_BNGL_XML)
	public StringRepresentation get_xml();
	
}