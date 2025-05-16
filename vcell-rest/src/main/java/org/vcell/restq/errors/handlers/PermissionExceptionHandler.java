package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.vcell.restq.errors.exceptions.PermissionWebException;

@Provider
public class PermissionExceptionHandler extends GenericAPIExceptionHandler implements ExceptionMapper<PermissionWebException>{
    private final static Logger logger = LogManager.getLogger(PermissionExceptionHandler.class);

    
    @Override
    @APIResponse(responseCode = PermissionWebException.HTTP_CODE + "", description = PermissionWebException.DEFAULT_MESSAGE)
    public Response toResponse(PermissionWebException e) {
        return genericExceptionHandler(e, logger);
    }
}
