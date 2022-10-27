package org.vcell.cli;

import java.io.IOException;

//import java.io.ObjectStreamException;

public class PythonStreamException extends IOException {

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
