package cbit.vcell.export.server.datacontainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class ExternalResultDataContainer implements FileStyleDataContainer {
    private final static Logger lg = LogManager.getLogger(ExternalResultDataContainer.class);
    private final ResultDataContainerID id;
    private final long size;
    private final File dataFile;


    ExternalResultDataContainer(File externalFile) throws IOException {
        this.dataFile = externalFile;
        this.id = new ResultDataContainerID(this.hashCode());
        this.size = this.dataFile.length();
    }

    public boolean isDataInFile(){
        return this.dataFile != null;
    }

    public File getDataFile() {
        return this.dataFile;
    }

    @Override
    public void append(String csq) throws IOException{
        throw new RuntimeException("FileDataContainer can't append externally supplied files");
    }

    @Override
    public void append(ResultDataContainer container) throws IOException{
        throw new RuntimeException("FileDataContainer can't append externally supplied files");
    }

    @Override
    public void append(byte[] bytesToAppend) throws IOException{
        throw new RuntimeException("FileDataContainer can't append externally supplied files");
    }

    public byte[] getDataBytes(){
        throw new RuntimeException("getDataBytes() not allowed for FileBacked containers");
    }

    @Override
    public long getDataSize(){
        return this.size;
    }

    @Override
    public ResultDataContainerID getId(){
        return this.id;
    }
}
