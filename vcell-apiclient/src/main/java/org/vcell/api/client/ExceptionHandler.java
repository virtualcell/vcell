package org.vcell.api.client;

import cbit.vcell.message.CustomObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.model.VCellHTTPError;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;

public class ExceptionHandler {


    protected static Exception getProperException(ApiException e){
        int httpCode = e.getCode();
        String message = e.getResponseBody() == null ? e.getMessage() : e.getResponseBody();
        String originalExceptionClassName = "";

        // Wrapped in JSON dictionary, must be custom error object
        if (e.getResponseBody() != null && e.getResponseBody().startsWith("{") && e.getResponseBody().endsWith("}")){
            try {
                VCellHTTPError error = new CustomObjectMapper().readValue(e.getResponseBody(), VCellHTTPError.class);
                message = error.getMessage();
                originalExceptionClassName = error.getExceptionType();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        switch (httpCode){
            case 500:
                if (originalExceptionClassName.equals(DataAccessException.class.getSimpleName())){
                    return new DataAccessException(message, e);
                } else return new RuntimeException(message, e);
            case 404:
                return new ObjectNotFoundException(message, e);
            default:
                return new RuntimeException(message, e);
        }
    }

    /**
     * Throws data access or runtime exception from ApiException
     */
    public static void onlyDataAccessException(ApiException e) throws DataAccessException{
        Exception exception = getProperException(e);
        if (exception instanceof DataAccessException de){
            throw de;
        } else if (exception instanceof RuntimeException re) {
            throw re;
        } else {
            throw new RuntimeException("Expected data access or runtime exception, but instead got: " + exception.getClass().getSimpleName(), e);
        }
    }

}
