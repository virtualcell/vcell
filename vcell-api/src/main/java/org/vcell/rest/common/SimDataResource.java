package org.vcell.rest.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Collection resource containing user accounts.
 */
public interface SimDataResource {

    @Get("json")
    public SimDataRepresentation get_json();
    
    @Get("html")
    public Representation get_html();

}
