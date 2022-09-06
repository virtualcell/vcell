package org.vcell.util.logging;

import java.io.Closeable;

public abstract class Log implements Closeable {
    protected abstract void write(String m);
    public abstract void close();
}
