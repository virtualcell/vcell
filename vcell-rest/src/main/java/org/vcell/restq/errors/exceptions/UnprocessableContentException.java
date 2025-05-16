package org.vcell.restq.errors.exceptions;

import jakarta.ws.rs.ServerErrorException;
import org.vcell.restq.errors.APIException;

public class UnprocessableContentException extends APIException {
    public final static int HTTP_CODE = 422;
    public final static String DEFAULT_MESSAGE = "Unprocessable content submitted";

    public UnprocessableContentException(String message, Exception e){
        super(HTTP_CODE, message, e);
    }

    public UnprocessableContentException(String message){
        super(HTTP_CODE, message, null);
    }

}
