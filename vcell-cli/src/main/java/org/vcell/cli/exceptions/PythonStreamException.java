package org.vcell.cli.exceptions;

//import java.io.ObjectStreamException;

public class PythonStreamException extends Exception { // at some point want to extend to IOException (we did that before, but it hid the exception)

    public PythonStreamException(String message, Throwable cause){
        super(message, cause);
    }

    public PythonStreamException(String message){
        super(message);
    }

    public PythonStreamException(){
        super();
    }
}
