package cbit.vcell.export.server.datacontainer;

import java.io.IOException;
import java.io.File;

public interface ResultDataContainer {
    void append(String csq) throws IOException;

    void append(ResultDataContainer container) throws IOException;

    void append(byte[] bytesToAppend) throws IOException;

    byte[] getDataBytes();

    int getDataSize();
}
