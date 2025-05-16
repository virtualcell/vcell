package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class NotAuthenticatedException extends APIException {
    public final static int HTTP_CODE = 401;
    public final static String DEFAULT_MESSAGE = "Not Authorized";

    public NotAuthenticatedException(String message, Exception e) {
        super(HTTP_CODE, message, e);
    }

    public NotAuthenticatedException(String message){
        super(HTTP_CODE, message, null);
    }
}
