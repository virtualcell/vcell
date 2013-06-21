package cbit.vcell.export.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.vcell.util.FileUtils;

public class FileBackedDataContainer {

File tempDataFile = null;
PrintWriter printWriter =  null;
BufferedReader bufferedReader = null;
	

FileBackedDataContainer() throws IOException{
	tempDataFile = File.createTempFile("TempFile", ".tmp");
	//System.out.println("Creating tempfile "+tempDataFile.getAbsolutePath());
	tempDataFile.deleteOnExit();
	
}

//FileBackedDataContainer(String str) throws IOException{
//	this();
//	this.append(str);
//}
	
FileBackedDataContainer(byte[] dataBytes) throws IOException{
	this();
	FileUtils.writeByteArrayToFile(dataBytes, tempDataFile);
}

FileBackedDataContainer(FileBackedDataContainer container) throws IOException{
	this();
	this.append(container);
}

void append(CharSequence csq) throws FileNotFoundException	{
	this.getPrintWriter().append(csq);
	this.getPrintWriter().flush();
}

void append(FileBackedDataContainer container) throws IOException {
	
    FileInputStream fileInputStream = new FileInputStream(container.getDataFile());
    DataInputStream dataInputStream = new DataInputStream(
                    fileInputStream);
    BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(dataInputStream));
    String fileData = "";
    while ((fileData = bufferedReader.readLine()) != null) {
            this.getPrintWriter().println(fileData);
    }
    bufferedReader.close();	
    this.getPrintWriter().flush();
}


File getDataFile(){
	return tempDataFile;
}

private PrintWriter getPrintWriter() throws FileNotFoundException{
	if (printWriter==null){
		printWriter = new PrintWriter(tempDataFile);
	} 
	return printWriter;
}

BufferedReader getBufferedReader() throws FileNotFoundException{
	if (bufferedReader==null){
		bufferedReader = new BufferedReader(new FileReader(tempDataFile));
	}
	return bufferedReader;
}

//String getDataAsString() throws IOException{
//	if (tempDataFile!=null){
//		return FileUtils.readFileToString(tempDataFile);
//	} else {
//		return null;
//	}
//}


public String toString(){
	throw new NullPointerException("HERE!");

	}

void close() {
	printWriter.flush();
	printWriter.close();
	printWriter = null;
}

void cleanup(){
	if (printWriter!=null){
		printWriter.close();
		
	}
	if (tempDataFile!=null){
		tempDataFile.delete();
	}
}

//protected void finalize() throws Throwable{
//	try {
//		cleanup();
//	} finally {
//		super.finalize();
//	}
//}

}
