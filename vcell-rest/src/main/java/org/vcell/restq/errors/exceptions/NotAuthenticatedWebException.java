package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class NotAuthenticatedWebException extends APIException {
    public final static int HTTP_CODE = 401;
    public final static String DEFAULT_MESSAGE = "Not Authorized";

    public NotAuthenticatedWebException(String message, Exception e) {
        super(HTTP_CODE, message, e);
    }

    public NotAuthenticatedWebException(String message){
        super(HTTP_CODE, message, null);
    }
}
