package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class NotFoundException extends APIException {
    public final static int HTTP_CODE = 404;
    public final static String DEFAULT_MESSAGE = "Not found";

    public NotFoundException(String message, Exception e){
        super(HTTP_CODE, message, e);
    }

    public NotFoundException(String message){super(HTTP_CODE, message, null);}

}
