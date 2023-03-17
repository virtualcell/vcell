package org.vcell.util.recording;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class TextFileRecord extends Record {
    protected File logFile;
    protected StringBuffer sBuff;

    public TextFileRecord(String filePath){
        this.sBuff = new StringBuffer();
        this.logFile = new File(filePath);
        try {
            if (logFile.exists() && !logFile.isDirectory()){
                this.write("Test Write");
                Files.delete(logFile.toPath());
            }   
        } catch (IOException e){
            throw new RuntimeException("File \"" + filePath + "\"  could not be created, writen to, or opened.", e);
        }
    }

    public void print(String message) throws IOException {
        this.buffer(message);
        this.flush();
    }

    public void buffer(String message){
        this.sBuff.append(message);
    }

    public void flush() throws IOException {
        this.write(this.sBuff.toString());
    }

    public String getPath(){
        return this.logFile.getAbsolutePath();
    }

    public String getName(){
        return this.logFile.getName();
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        if (this.sBuff.toString().equals("") && logFile.exists() && !logFile.isDirectory()) 
            Files.delete(this.logFile.toPath()); // Throws an exception on failure
    }

    @Override
    protected void write(String m) throws IOException {
        if (logFile.isDirectory() || m.equals("")) 
            return;

        Writer fileWriter = new FileWriter(logFile, false);
        fileWriter.write(m);
        fileWriter.close();
    }   
}
