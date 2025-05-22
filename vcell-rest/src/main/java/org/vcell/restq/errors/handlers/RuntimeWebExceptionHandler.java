package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.errors.exceptions.RuntimeWebException;

@Provider
public class RuntimeWebExceptionHandler implements ExceptionMapper<RuntimeWebException> {
    public static final Logger logger = LogManager.getLogger(RuntimeWebExceptionHandler.class);

    @Override
    public Response toResponse(RuntimeWebException e) {
        return GenericAPIExceptionHandler.genericExceptionHandler(e, 500, logger);
    }
}
