package org.vcell.restq.errors.exceptions;

/**
 * Exception for where only a true 500 error code exception makes sense. All exception messages are seen by end users.
 */
public class RuntimeWebException extends RuntimeException{
    public static final int HTTP_CODE = 500;

    public RuntimeWebException(String message, Exception e){
        super(message, e);
    }
}
