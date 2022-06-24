package org.vcell.cli;

import java.io.ObjectStreamException;

public class PythonStreamException extends ObjectStreamException {
    public PythonStreamException(String message){
        super(message);
    }

    public PythonStreamException(){
        super();
    }
}
