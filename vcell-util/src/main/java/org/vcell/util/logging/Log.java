package org.vcell.util.logging;

import java.io.Closeable;
import java.io.IOException;

public abstract class Log implements Closeable {
    protected abstract void write(String m) throws IOException;
}
