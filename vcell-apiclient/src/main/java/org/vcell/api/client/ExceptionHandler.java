package org.vcell.api.client;

import org.vcell.restclient.CustomObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.model.VCellHTTPError;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;

public class ExceptionHandler {
    
    protected static Exception getProperException(ApiException e){
        int httpCode = e.getCode();
        String message = e.getResponseBody() == null ? e.getMessage() : e.getResponseBody();
        String originalExceptionClassName = e.getClass().getSimpleName();

        // Wrapped in JSON dictionary, must be custom error object
        if (e.getResponseBody() != null && e.getResponseBody().startsWith("{") && e.getResponseBody().endsWith("}")){
            try {
                VCellHTTPError error = new CustomObjectMapper().readValue(e.getResponseBody(), VCellHTTPError.class);
                message = error.getMessage();
                originalExceptionClassName = error.getExceptionType();
                if (originalExceptionClassName == null){
                    originalExceptionClassName = "Unknown Exception";
                }
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        switch (httpCode){
            case 500:
                if (originalExceptionClassName.equals(DataAccessException.class.getSimpleName())){
                    return new DataAccessException(message, e);
                } else return new RuntimeException(message, e);
            case 401, 403:
                return new PermissionException(message);
            case 404:
                return new ObjectNotFoundException(message, e);
            default:
                return new RuntimeException(message, e);
        }
    }

    /**
     * Throws data access or runtime exception from ApiException
     */
    public static void onlyDataAccessException(ApiException e) throws DataAccessException, ObjectNotFoundException, PermissionException {
        Exception exception = getProperException(e);
        if (exception instanceof ObjectNotFoundException onf) {
            throw onf;
        } if (exception instanceof DataAccessException de){
            throw de;
        } else if (exception instanceof PermissionException pe){
            throw pe;
        } else if (exception instanceof RuntimeException re) {
            throw re;
        } else {
            throw new RuntimeException("Expected data access or runtime exception, but instead got: " + exception.getClass().getSimpleName(), e);
        }
    }

}
