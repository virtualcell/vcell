package org.vcell.sedml;

public class SEDMLImportException extends Exception {
    public SEDMLImportException(String message){
        super(message);
    }
    public SEDMLImportException(String message, Exception exception){
        super(message, exception);
    }

    public SEDMLImportException(Exception exception){
        super(exception);
    }
}
