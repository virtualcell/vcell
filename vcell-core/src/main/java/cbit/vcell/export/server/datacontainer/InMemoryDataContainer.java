package cbit.vcell.export.server.datacontainer;

import java.io.IOException;
import java.util.Arrays;

public class InMemoryDataContainer implements ResultDataContainer {
    private byte[] bytes;
    @Override
    public void append(String str) throws IOException {
        this.append(str.getBytes());
    }

    @Override
    public void append(ResultDataContainer container) throws IOException {
        this.append(container.getDataBytes());
    }

    @Override
    public void append(byte[] bytesToAppend) throws IOException {
        int totalLength = this.bytes.length + bytesToAppend.length;
        byte[] fullBytes = Arrays.copyOf(this.bytes, totalLength);
        for (int i = this.bytes.length, j = 0; i < totalLength; i++, j++)
            fullBytes[i] = bytesToAppend[j];
        this.bytes = fullBytes;
    }

    @Override
    public byte[] getDataBytes() {
        return this.bytes;
    }

    @Override
    public int getDataSize() {
        return this.bytes.length;
    }
}
