package org.vcell.util.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLog extends Log {
    protected File logFile;
    protected FileWriter fileWriter;
    protected StringBuffer sBuff;

    public FileLog(String fileName){
        this.sBuff = new StringBuffer();
        this.logFile = new File(fileName);
        this.fileWriter = null;

        if (!logFile.exists() || logFile.isDirectory()) 
            throw new RuntimeException("File \"" + fileName + "\" is invalid.");
        
        try {
            this.fileWriter = new FileWriter(logFile, false);
        } catch (IOException e){
            throw new RuntimeException("File \"" + fileName + "\"  could not be created or opened.", e);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.write(sBuff.toString());
        this.fileWriter.close();
    }

    @Override
    protected void write(String m) throws IOException {
        this.fileWriter.write(m);
    }    
}
