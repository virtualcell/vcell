package org.vcell.rest.common;

import org.restlet.resource.Get;

public interface BiomodelVCMLResource {

	/**
	 * Returns the list of BioModels accessible to this user
	 */

	@Get("application/vcml+xml")
	public String get_xml();
	
}
