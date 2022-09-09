package org.vcell.util.recording;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileRecord extends Record {
    protected File logFile;
    protected FileWriter fileWriter;
    protected StringBuffer sBuff;

    public FileRecord(String fileName){
        this.sBuff = new StringBuffer();
        this.logFile = new File(fileName);
        this.fileWriter = null;
        try {
        if ((logFile.exists() || logFile.createNewFile()) && !logFile.isDirectory()){
            logFile.delete();
            this.fileWriter = new FileWriter(logFile, false);
        }
            
        } catch (IOException e){
            throw new RuntimeException("File \"" + fileName + "\"  could not be created or opened.", e);
        }
    }

    // Expose the underlying write method.
    public void print(String message) throws IOException {
        this.sBuff.append(message);
    }

    public void flush() throws IOException {
        this.write(this.sBuff.toString());
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        this.fileWriter.close();
        if (this.sBuff.toString().equals("")) 
            Files.delete(this.logFile.toPath()); // Throws an exception on failure
    }

    @Override
    protected void write(String m) throws IOException {
        if (!m.equals(""))
            this.fileWriter.write(m);
    }   
}
