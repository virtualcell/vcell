package cbit.vcell.export.server.datacontainer;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileDataContainer implements ResultDataContainer {
    private int size = 0;
    private final File dataFile;

    public FileDataContainer() throws IOException {
        this.dataFile = Files.createTempFile("vcellFDC", "").toFile();
    }

    @Override
    public void append(@NotNull String csq) throws IOException {
        this.append(csq.getBytes());
    }

    @Override
    public void append(@NotNull ResultDataContainer container) throws IOException {
        this.append(container.getDataBytes());
    }

    @Override
    public void append(byte[] bytesToAppend) throws IOException {
        this.size += bytesToAppend.length;
        try (FileWriter writer = new FileWriter(this.dataFile)){
            writer.append(bytesToAppend);
        }

    }

    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }

    @Override
    public int getDataSize() {
        return this.size;
    }
}
