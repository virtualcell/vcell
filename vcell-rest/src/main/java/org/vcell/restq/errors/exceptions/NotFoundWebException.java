package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class NotFoundWebException extends APIException {
    public final static int HTTP_CODE = 404;
    public final static String DEFAULT_MESSAGE = "Not found";

    public NotFoundWebException(String message, Exception e){
        super(HTTP_CODE, message, e);
    }

    public NotFoundWebException(String message){super(HTTP_CODE, message, null);}

}
