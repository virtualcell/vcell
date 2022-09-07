package org.vcell.util.logging;

import java.io.IOException;

public class StdErrLog extends StdStreamLog {
    protected StringBuffer sBuff;

    @Override
    public void close() throws IOException {
        // No special closing required!
    }

    @Override
    protected void write(String m) {
        System.err.println("Error: " + m);
    }
    
}
