package org.vcell.cli.run.hdf5;

public class MissingDataException extends Exception {
    public MissingDataException(){
        super();
    }

    public MissingDataException(String message){
        super(message);
    }
}
