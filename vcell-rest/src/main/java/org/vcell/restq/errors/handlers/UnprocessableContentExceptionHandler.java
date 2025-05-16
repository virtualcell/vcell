package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.UnprocessableContentException;

@Provider
public class UnprocessableContentExceptionHandler extends GenericAPIExceptionHandler implements ExceptionMapper<UnprocessableContentException>{
    private final static Logger logger = LogManager.getLogger(UnprocessableContentExceptionHandler.class);

    
    @Override
    @APIResponse(responseCode = UnprocessableContentException.HTTP_CODE + "", description = UnprocessableContentException.DEFAULT_MESSAGE)
    public Response toResponse(UnprocessableContentException e) {
        return genericExceptionHandler(e, logger);
    }
}
