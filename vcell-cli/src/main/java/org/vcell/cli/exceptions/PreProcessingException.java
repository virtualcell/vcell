package org.vcell.cli.exceptions;

public class PreProcessingException extends RuntimeException {
    public PreProcessingException(String message) {
        super(message);
    }

    public PreProcessingException(Throwable cause) {
        super(cause);
    }

    public PreProcessingException(String message, Throwable cause){
        super(message, cause);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
