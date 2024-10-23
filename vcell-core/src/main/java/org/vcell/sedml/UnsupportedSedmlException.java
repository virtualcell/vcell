package org.vcell.sedml;

public class UnsupportedSedmlException extends Exception {
    public UnsupportedSedmlException(String message) {
        super(message);
    }
    public UnsupportedSedmlException(String message, Throwable cause) {super(message, cause);}
    public UnsupportedSedmlException(Throwable cause) {super(cause);}
}
