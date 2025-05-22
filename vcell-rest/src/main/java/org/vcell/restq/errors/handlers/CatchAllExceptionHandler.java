package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class CatchAllExceptionHandler implements ExceptionMapper<Throwable> {
    private final static Logger logger = LogManager.getLogger(CatchAllExceptionHandler.class);

    @Override
    public Response toResponse(Throwable e) {
        return GenericAPIExceptionHandler.genericExceptionHandler(e, 500, logger);
    }
}
