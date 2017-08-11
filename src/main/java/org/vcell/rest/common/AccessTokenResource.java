package org.vcell.rest.common;

import org.restlet.resource.Get;

/**
 * Collection resource containing user accounts.
 */
public interface AccessTokenResource {

    @Get("json")
    public AccessTokenRepresentation get_json();
    
}
