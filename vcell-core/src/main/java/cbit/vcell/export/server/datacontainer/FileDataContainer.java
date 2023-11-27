package cbit.vcell.export.server.datacontainer;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;

public class FileDataContainer implements FileStyleDataContainer {
    private final ResultDataContainerID id;
    private long size = 0;
    private final File dataFile;

    public FileDataContainer() throws IOException {
        this.dataFile = Files.createTempFile("vcellFDC_", "").toFile();
        this.id = new ResultDataContainerID(this.hashCode());
    }

    public boolean deleteFile(){
        return this.dataFile.delete();
    }

    public File getDataFile() {
        return this.dataFile;
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
        try (FileOutputStream writer = new FileOutputStream(this.dataFile, true)){
            writer.write(bytesToAppend);
        }
        this.size += bytesToAppend.length; // done after in case of exception
    }

    @Override
    public byte[] getDataBytes() {
        throw new RuntimeException("getDataBytes() not allowed for FileBacked containers");
    }

    @Override
    public long getDataSize() {
        return this.size;
    }

    @Override
    public ResultDataContainerID getId() {
        return this.id;
    }
}
