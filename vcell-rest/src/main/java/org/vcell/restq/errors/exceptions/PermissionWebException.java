package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class PermissionWebException extends APIException {
    public final static int HTTP_CODE = 403;
    public final static String DEFAULT_MESSAGE = "Not Allowed";

    public PermissionWebException(String message, Exception e) {
        super(HTTP_CODE, message, e);
    }

    public PermissionWebException(String message){
        super(HTTP_CODE, message, null);
    }
}
