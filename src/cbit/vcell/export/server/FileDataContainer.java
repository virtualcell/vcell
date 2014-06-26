 package cbit.vcell.export.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.vcell.util.FileUtils;

public class FileDataContainer {

File tempDataFile = null;


FileDataContainer() throws IOException{
	tempDataFile = File.createTempFile("TempFile", ".tmp");
	tempDataFile.deleteOnExit();
}


FileDataContainer(byte[] dataBytes) throws IOException{
	this();
	FileUtils.writeByteArrayToFile(dataBytes, tempDataFile);
}

FileDataContainer(FileDataContainer container) throws IOException{
	this();
	this.append(container);
}

void append(CharSequence csq) throws IOException	{
	try(BufferedOutputStream bos = getBufferedOutputStream(true)){
		bos.write(csq.toString().getBytes());
		bos.flush();
		bos.close();
	}
}

void append(FileDataContainer container) throws IOException {
	File appendThisDataFile = container.getDataFile();
	try(
		FileInputStream fis = new FileInputStream(appendThisDataFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedOutputStream bos = getBufferedOutputStream(true)
	){
		// Copy the contents of the file to the output stream
		byte[] buffer = new byte[(int)Math.min(1048576/*2^20*/,appendThisDataFile.length())];
		int count = 0;                 
		while ((count = bis.read(buffer)) >= 0) {    
			getBufferedOutputStream(true).write(buffer, 0, count);
		}                 
		bos.flush();
		bos.close();
		bis.close();
		fis.close();
	}
}

File getDataFile(){
	return tempDataFile;
}

private BufferedOutputStream getBufferedOutputStream(boolean bAppend) throws FileNotFoundException{
	return new BufferedOutputStream(new FileOutputStream(tempDataFile, bAppend));

}

void deleteTempFile() {
	if(getDataFile() != null && !getDataFile().delete()){
		new Exception("Failed to delete ExportOutput Tempfile '"+getDataFile().getAbsolutePath()+"'").printStackTrace();
	}
}
}
