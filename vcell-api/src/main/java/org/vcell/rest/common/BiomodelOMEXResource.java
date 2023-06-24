package org.vcell.rest.common;

import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.resource.Get;

public interface BiomodelOMEXResource {

	public static final String APPLICATION_OMEX_ZIP = "application/omex+zip";
	public static final MediaType OMEX_MEDIATYPE = MediaType.register(BiomodelOMEXResource.APPLICATION_OMEX_ZIP, "COMBINE omex+zip archive");

	/**
	 * Returns the list of BioModels accessible to this user
	 */

	@Get(APPLICATION_OMEX_ZIP)
	public ByteArrayRepresentation get_omex();
	
}
