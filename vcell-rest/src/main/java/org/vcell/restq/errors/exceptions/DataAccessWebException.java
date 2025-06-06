package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class DataAccessWebException extends APIException {
    public final static int HTTP_CODE = 500;
    public final static String DEFAULT_MESSAGE = "Data Access Exception";

    public DataAccessWebException(String message, Exception e) {
        super(HTTP_CODE, message, e);
    }

    public DataAccessWebException(String message){
        super(HTTP_CODE, message, null);
    }
}
