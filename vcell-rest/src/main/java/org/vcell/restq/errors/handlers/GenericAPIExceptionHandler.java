package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.errors.APIException;

public abstract class GenericAPIExceptionHandler {

    public Response genericExceptionHandler(APIException e, Logger logger){
        logger.error(e);
        return Response.status(e.getHttpCode())
                .entity(e.getMessage())
                .build();
    }
}
