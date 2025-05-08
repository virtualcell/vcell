package org.vcell.restq.config;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class WebExceptionErrorHandler implements ExceptionMapper<WebApplicationException> {

    private final static Logger logger = LogManager.getLogger(WebExceptionErrorHandler.class);

    public final static int DATA_ACCESS_EXCEPTION_HTTP_CODE = 500;
    public final static int UNPROCESSABLE_HTTP_CODE = 422;

    @Override
    public Response toResponse(WebApplicationException e) {
        logger.error(e);
        return Response.status(e.getResponse().getStatus())
                .entity(e.getMessage())
                .build();
    }
}
