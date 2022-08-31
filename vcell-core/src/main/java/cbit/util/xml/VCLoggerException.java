package cbit.util.xml;

public class VCLoggerException extends Exception {
    public VCLoggerException(){
        super();
    }

    public VCLoggerException(Throwable cause){
        super(cause);
    }

    public VCLoggerException(String message){
        super(message);
    }

    public VCLoggerException(String message, Throwable cause){
        super(message, cause);
    }

    public VCLoggerException(String message, Throwable cause, boolean enableSupression, boolean writableStackTrace){
        super(message, cause, enableSupression, writableStackTrace);
    }
}
