package org.vcell.restq.errors.handlers;

import cbit.vcell.clientdb.ServerRejectedSaveException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.errors.APIException;

public abstract class GenericAPIExceptionHandler {

    /**
     * Should be inherited by all Exception classes
     */
    @Produces(MediaType.APPLICATION_JSON)
    public Response genericExceptionHandler(APIException e, Logger logger){
        return genericExceptionHandler(e, e.getHttpCode(), logger);
    }

    /**
     * Should only be used by RuntimeWebExceptionHandler
     */
    @Produces(MediaType.APPLICATION_JSON)
    public static Response genericExceptionHandler(Throwable e, int httpCode, Logger logger){
        logger.error(e);
        try{
            return Response.status(httpCode)
                    .entity(new ObjectMapper().writeValueAsString(VCellHTTPError.fromException(e)))
                    .build();
        } catch (JsonProcessingException jsonProcessingException) {
            logger.error(jsonProcessingException);
            return Response.status(500)
                    .entity("Unknown error while trying to return another error")
                    .build();
        }
    }

    record VCellHTTPError(
            String exceptionType,
            String message
    ){
        public static VCellHTTPError fromException(Throwable e){
            String errorType = e.getClass().getSimpleName();
            if (e.getCause() != null){
                errorType = e.getCause().getClass().getSimpleName();
            }
            Throwable currentCause = e;
            String message = e.getMessage();
            while (currentCause != null){
                if (currentCause.getClass().equals(ServerRejectedSaveException.class)){
                    errorType = ServerRejectedSaveException.class.getSimpleName();
                    message = currentCause.getMessage();
                    break;
                }
                currentCause = currentCause.getCause();
            }
            return new VCellHTTPError(errorType, message);
        }
    }
}
