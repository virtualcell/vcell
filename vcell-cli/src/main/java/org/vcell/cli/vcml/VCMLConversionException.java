package org.vcell.cli.vcml;

public class VCMLConversionException extends Exception {
    public VCMLConversionException(String message){
        super(message);
    }

    public VCMLConversionException(String message, Throwable cause){
        super(message, cause);
    }

    public VCMLConversionException(Throwable cause){
        super(cause);
    }

    public VCMLConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
