package cbit.vcell.export.server.datacontainer;

import java.io.File;
import java.io.IOException;

public class FileDataContainer implements ResultDataContainer {

    @Override
    public void append(String csq) throws IOException {

    }

    @Override
    public void append(ResultDataContainer container) throws IOException {

    }

    @Override
    public void append(byte[] bytesToAppend) throws IOException {

    }

    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }

    @Override
    public int getDataSize() {
        return 0;
    }
}
