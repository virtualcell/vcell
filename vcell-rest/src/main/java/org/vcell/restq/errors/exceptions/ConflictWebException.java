package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class ConflictWebException extends APIException {
    public final static int HTTP_CODE = 409;
    public final static String DEFAULT_MESSAGE = "Conflict with server state.";

    public ConflictWebException(String message, Exception cause) {
        super(HTTP_CODE, message, cause);
    }

    public ConflictWebException(String message){
        super(HTTP_CODE, message, null);
    }
}
