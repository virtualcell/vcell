package org.vcell.util.logging;

import java.io.IOException;

public abstract class StdStreamLog extends Log {
    public void writeToStream(String m) throws IOException {
        this.write(m);
    }
}
