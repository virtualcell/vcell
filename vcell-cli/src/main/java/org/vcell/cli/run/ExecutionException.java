package org.vcell.cli.run;

public class ExecutionException extends Exception {
    public ExecutionException(String message){
        super(message);
    }

    public ExecutionException(String message, Throwable cause){
        super(message, cause);
    }

    public ExecutionException(Throwable cause){
        super(cause);
    }

    public ExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
