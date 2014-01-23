package cbit.vcell.export.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.vcell.util.FileUtils;

public class FileDataContainer {

File tempDataFile = null;
BufferedOutputStream bos;
	

FileDataContainer() throws IOException{
	tempDataFile = File.createTempFile("TempFile", ".tmp");
	//System.out.println("Creating tempfile "+tempDataFile.getAbsolutePath());
	tempDataFile.deleteOnExit();
}

//FileBackedDataContainer(String str) throws IOException{
//	this();
//	this.append(str);
//}
	
FileDataContainer(byte[] dataBytes) throws IOException{
	this();
	FileUtils.writeByteArrayToFile(dataBytes, tempDataFile);
}

FileDataContainer(FileDataContainer container) throws IOException{
	this();
	this.append(container);
}

void append(CharSequence csq) throws IOException	{
	getBufferedOutputStream().write(csq.toString().getBytes());
	getBufferedOutputStream().flush();
}

void append(FileDataContainer container) throws IOException {
	
	FileInputStream fis = null;
	BufferedInputStream bis = null;
	try{
		File appendThisDataFile = container.getDataFile();
		fis = new FileInputStream(appendThisDataFile);
		bis = new BufferedInputStream(fis);
		// Copy the contents of the file to the output stream
		byte[] buffer = new byte[(int)Math.min(1048576/*2^20*/,appendThisDataFile.length())];
		int count = 0;                 
		while ((count = bis.read(buffer)) >= 0) {    
			getBufferedOutputStream().write(buffer, 0, count);
		}                 
		getBufferedOutputStream().flush();
	}finally{
		if(bis != null){try{bis.close();}catch(Exception e){e.printStackTrace();}}
		if(fis != null){try{fis.close();}catch(Exception e){e.printStackTrace();}}
	}

//    FileInputStream fileInputStream = new FileInputStream(container.getDataFile());
//    DataInputStream dataInputStream = new DataInputStream(
//                    fileInputStream);
//    BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(dataInputStream));
//    String fileData = "";
//    while ((fileData = bufferedReader.readLine()) != null) {
//            this.getPrintWriter().println(fileData);
//    }
//    bufferedReader.close();	
//    this.getPrintWriter().flush();
}


File getDataFile(){
	return tempDataFile;
}

private BufferedOutputStream getBufferedOutputStream() throws FileNotFoundException{
	if (bos==null){
		bos = new BufferedOutputStream(new FileOutputStream(tempDataFile));
	} 
	return bos;
}

void closeAndDelete() {
	if(bos != null){
		try{
			try{bos.flush();}catch(Exception e){e.printStackTrace();}
			try{bos.close();}catch(Exception e){e.printStackTrace();}
			bos = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	if(getDataFile() != null && !getDataFile().delete()){
		new Exception("Failed to delete ExportOutput Tempfile '"+getDataFile().getAbsolutePath()+"'").printStackTrace();
	}
}
}
