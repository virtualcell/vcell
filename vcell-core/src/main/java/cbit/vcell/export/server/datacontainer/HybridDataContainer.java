package cbit.vcell.export.server.datacontainer;

import java.io.IOException;

public class HybridDataContainer implements ResultDataContainer {
    @Override
    public void append(CharSequence csq) throws IOException {

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
    public long getDataSize() {
        return 0;
    }
}
