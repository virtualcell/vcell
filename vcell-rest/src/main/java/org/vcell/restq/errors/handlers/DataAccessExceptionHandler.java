package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.vcell.restq.errors.exceptions.DataAccessWebException;

@Provider
public class DataAccessExceptionHandler extends GenericAPIExceptionHandler implements ExceptionMapper<DataAccessWebException>{
    private final static Logger logger = LogManager.getLogger(DataAccessExceptionHandler.class);


    @Override
    @APIResponse(responseCode = DataAccessWebException.HTTP_CODE + "", description = DataAccessWebException.DEFAULT_MESSAGE)
    public Response toResponse(DataAccessWebException e) {
        return genericExceptionHandler(e, logger);
    }
}
