package org.vcell.util.recording;

import java.io.Closeable;
import java.io.IOException;

public abstract class Record implements Closeable {
    protected abstract void write(String m) throws IOException;
}
