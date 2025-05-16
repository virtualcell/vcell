package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.NotFoundException;

@Provider
public class NotFoundExceptionHandler extends GenericAPIExceptionHandler implements ExceptionMapper<NotFoundException>{
    private final static Logger logger = LogManager.getLogger(NotFoundExceptionHandler.class);


    @Override
    @APIResponse(responseCode = NotFoundException.HTTP_CODE + "", description = NotFoundException.DEFAULT_MESSAGE)
    public Response toResponse(NotFoundException e) {
        return genericExceptionHandler(e, logger);
    }
}
