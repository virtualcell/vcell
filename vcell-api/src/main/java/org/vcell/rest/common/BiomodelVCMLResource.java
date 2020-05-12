package org.vcell.rest.common;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public interface BiomodelVCMLResource {

	public static final String APPLICATION_VCML_XML = "application/vcml+xml";
	public static final MediaType VCDOC_MEDIATYPE = MediaType.register(BiomodelVCMLResource.APPLICATION_VCML_XML, "VCell vcml+xml Document");

	/**
	 * Returns the list of BioModels accessible to this user
	 */

	@Get(APPLICATION_VCML_XML)
	public StringRepresentation get_xml();
	
}
