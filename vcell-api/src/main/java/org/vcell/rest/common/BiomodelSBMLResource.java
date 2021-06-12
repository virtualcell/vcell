package org.vcell.rest.common;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public interface BiomodelSBMLResource {

	public static final String APPLICATION_SBML_XML = "application/sbml+xml";
	public static final MediaType VCDOC_MEDIATYPE = MediaType.register(BiomodelSBMLResource.APPLICATION_SBML_XML, "VCell sbml+xml Document");

	/**
	 * Returns the list of BioModels accessible to this user
	 */

	@Get(APPLICATION_SBML_XML)
	public StringRepresentation get_xml();
	
}
