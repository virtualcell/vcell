package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class BadRequestWebException extends APIException {
    public final static int HTTP_CODE = 400;
    public final static String DEFAULT_MESSAGE = "Bad Request.";

    public BadRequestWebException(String message, Exception cause) {
        super(HTTP_CODE, message, cause);
    }

    public BadRequestWebException(String reasonShownToUser, String realReason){
        super(HTTP_CODE, reasonShownToUser, new Exception(realReason));
    }

    public BadRequestWebException(String message){
        super(HTTP_CODE, message, null);
    }
}
