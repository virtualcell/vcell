package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.vcell.restq.errors.exceptions.NotAuthenticatedException;

@Provider
public class UnauthorizedExceptionHandler extends GenericAPIExceptionHandler implements ExceptionMapper<NotAuthenticatedException>{
    private final static Logger logger = LogManager.getLogger(UnauthorizedExceptionHandler.class);

    
    @Override
    @APIResponse(responseCode = NotAuthenticatedException.HTTP_CODE + "", description = NotAuthenticatedException.DEFAULT_MESSAGE)
    public Response toResponse(NotAuthenticatedException e) {
        return genericExceptionHandler(e, logger);
    }
}
