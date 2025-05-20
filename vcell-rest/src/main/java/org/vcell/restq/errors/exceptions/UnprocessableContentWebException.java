package org.vcell.restq.errors.exceptions;

import org.vcell.restq.errors.APIException;

public class UnprocessableContentWebException extends APIException {
    public final static int HTTP_CODE = 422;
    public final static String DEFAULT_MESSAGE = "Unprocessable content submitted";

    public UnprocessableContentWebException(String message, Exception e){
        super(HTTP_CODE, message, e);
    }

    public UnprocessableContentWebException(String message){
        super(HTTP_CODE, message, null);
    }

}
