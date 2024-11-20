package org.vcell.cli.run.hdf5;

import java.io.Serial;

public class BiosimulationsHdfWriterException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public BiosimulationsHdfWriterException(String message) {
        super(message);
    }

    public BiosimulationsHdfWriterException(String message, Exception e) {
        super(message, e);
    }
}
