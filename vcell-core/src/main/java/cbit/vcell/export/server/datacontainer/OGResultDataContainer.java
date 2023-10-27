package cbit.vcell.export.server.datacontainer;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.FileUtils;

import java.io.*;

public class OGResultDataContainer {
    private final static Logger lg = LogManager.getLogger(OGResultDataContainer.class);
    private final AggregateDataSize usedMemory;
    File tempDataFile = null;
    byte[] dataBytes = null;
    public boolean bNoAppend = false;


    OGResultDataContainer(AggregateDataSize usedMemory) throws IOException {
        this.usedMemory = usedMemory;
        if (this.usedMemory.isMaxedOut()) this.createTempFile();
    }

    OGResultDataContainer(AggregateDataSize usedMemory, byte[] dataBytes) throws IOException {
        this(usedMemory);
        if (isDataInFile()){
            if (dataBytes != null && dataBytes.length > 0){
                FileUtils.writeByteArrayToFile(dataBytes, this.tempDataFile);
            }
        } else {
            this.dataBytes = dataBytes;
        }
    }

    private void createTempFile() throws IOException{
        this.tempDataFile = File.createTempFile("TempFile", ".tmp");
        this.tempDataFile.deleteOnExit();
    }
    private void transition() throws IOException {
        //first copy data to file if not there already
        if (!this.usedMemory.isMaxedOut() || this.isDataInFile()) return;
        this.createTempFile();
        if (this.dataBytes == null) return;
        try (BufferedOutputStream bos = getBufferedOutputStream()){
            bos.write(this.dataBytes, 0, this.dataBytes.length);
            bos.flush();
        }
        this.dataBytes = null;  //no longer needed
    }

    void append(CharSequence csq) throws IOException	{
        if (this.bNoAppend) throw new RuntimeException("FileDataContainer can't append externally supplied files");

        this.transition();
        byte[] csqBytes = csq.toString().getBytes();
        if (this.isDataInFile()){
            try(BufferedOutputStream bos = getBufferedOutputStream()){
                bos.write(csqBytes);
                bos.flush();
            }
        } else {
            if (this.dataBytes == null){
                this.dataBytes = csqBytes;
            } else {
                byte[] newDataBytes = new byte[dataBytes.length + csqBytes.length];
                System.arraycopy(this.dataBytes, 0, newDataBytes, 0, this.dataBytes.length);
                System.arraycopy(csqBytes, 0, newDataBytes, dataBytes.length, csqBytes.length);
                this.dataBytes = newDataBytes;
            }
        }
    }

    void appendArrayContainer(byte[] bytesToAppend) throws  IOException{
        if (this.bNoAppend){
            throw new RuntimeException("FileDataContainer can't append externally supplied files");
        }
        this.transition();
        if (this.isDataInFile()){
            try(BufferedOutputStream bos = getBufferedOutputStream()){
                bos.write(bytesToAppend, 0, bytesToAppend.length);
                bos.flush();
            }
        } else {
            if (this.dataBytes == null){
                dataBytes = bytesToAppend;
                return;
            }
            byte[] newDataBytes = new byte[dataBytes.length+bytesToAppend.length];
            System.arraycopy(dataBytes,0,newDataBytes,0,dataBytes.length);
            System.arraycopy(bytesToAppend,0,newDataBytes,dataBytes.length,bytesToAppend.length);
            dataBytes=newDataBytes;
        }
    }


    private void appendFileContainer(File fileToAppend) throws IOException{
        if (bNoAppend){
            throw new RuntimeException("FileDataContainer can't append externally supplied files");
        }
        this.transition();
        if (isDataInFile()) {
            try(
                    FileInputStream fis = new FileInputStream(fileToAppend);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    BufferedOutputStream bos = getBufferedOutputStream()
            ){
                IOUtils.copy(bis, bos);
            }
        } else {
            byte[] bytesToAppend = FileUtils.readByteArrayFromFile(fileToAppend);
            if (dataBytes == null){
                dataBytes = bytesToAppend;
                return;
            }
            byte[] newDataBytes = new byte[dataBytes.length + bytesToAppend.length];
            System.arraycopy(dataBytes,0, newDataBytes,0, dataBytes.length);
            System.arraycopy(bytesToAppend,0, newDataBytes, dataBytes.length, bytesToAppend.length);
            dataBytes = newDataBytes;
        }
    }

    void append(OGResultDataContainer container) throws IOException {
        if (bNoAppend){
            throw new RuntimeException("FileDataContainer can't append externally supplied files");
        }
        this.transition();
        if (this.isDataInFile()){
            if(container.isDataInFile()){
                this.appendFileContainer(container.getDataFile());
            }else{
                try(
                        ByteArrayInputStream byis = new ByteArrayInputStream(container.getDataBytes());
                        BufferedInputStream bis = new BufferedInputStream(byis);
                        BufferedOutputStream bos = getBufferedOutputStream()
                ){
                    IOUtils.copy(bis, bos);
                }
            }
        } else {
            if (container.isDataInFile()){
                this.appendFileContainer(container.getDataFile());
            } else {
                this.appendArrayContainer(container.getDataBytes());
            }
        }
    }

    public boolean isDataInFile(){
        return this.tempDataFile != null;
    }

    public File getDataFile() throws IOException{
        if(!isDataInFile()) throw new RuntimeException("No Datafile");
        return this.tempDataFile;
    }

    public byte[] getDataBytes(){
        if (this.tempDataFile != null) throw new RuntimeException("getDataBytes() not allowed for FileBacked containers");
        return this.dataBytes;
    }

    public long size(){
        if (!this.isDataInFile()){
            return this.dataBytes != null ? this.dataBytes.length : 0;
        } else {
            return this.tempDataFile.length();
        }
    }

    private BufferedOutputStream getBufferedOutputStream() throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(this.tempDataFile, true));
    }

    void deleteTempFile() throws IOException {
        if (!isDataInFile() || !this.getDataFile().exists() || getDataFile().delete()) return;
        String msg = String.format("Failed to delete ExportOutput TempFile '%s'",
                getDataFile().getAbsolutePath());
        Exception e = new Exception(msg);
        lg.error(e.getMessage(), e);
    }
}
