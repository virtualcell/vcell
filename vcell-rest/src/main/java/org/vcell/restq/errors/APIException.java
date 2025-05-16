package org.vcell.restq.errors;

public class APIException extends Exception{
    private final int httpCode;
    private final String message;

    public APIException(int httpCode, String message, Exception cause){
        super(message, cause);
        this.httpCode = httpCode;
        this.message = message;
    }

    public int getHttpCode(){
        return httpCode;
    }
}
