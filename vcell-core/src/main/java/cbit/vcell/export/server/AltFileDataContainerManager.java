package cbit.vcell.export.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.FileUtils;

import cbit.vcell.resource.PropertyLoader;

public class AltFileDataContainerManager {
	private final static Logger lg = LogManager.getLogger(AltFileDataContainerManager.class);

	List<FileDataContainer> fileDataContainers = new ArrayList<>();
	private long aggregateDataSize = 0;
	private static long AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT =
			PropertyLoader.getLongProperty(PropertyLoader.exportMaxInMemoryLimit, 1024*1024*100);
	
	public class FileDataContainerID {
		private final int id;
		public FileDataContainerID (int id){
			this.id = id;
		}
	}
	
	private class FileDataContainer {
		File tempDataFile = null;
		byte[] dataBytes = null;
		public boolean bNoAppend = false;


		FileDataContainer() throws IOException {
			if (isLimited()) createTempFile();
		}

		FileDataContainer(byte[] dataBytes) throws IOException{
			this();
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
			if(!isLimited() || this.isDataInFile()) return;
			this.createTempFile();
			if(this.dataBytes == null) return;
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

		private void appendArrayContainer(byte[] bytesToAppend) throws  IOException{
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

		void append(FileDataContainer container) throws IOException {
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

	
	@SuppressWarnings("static-access")   
	/**
	 * 
	 * @param memLimitOverride
	 * 
	 * This constructor is meant for testing and has package access only
	 * 
	 */
	
	public AltFileDataContainerManager(long memLimitOverride){
		this.AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT = memLimitOverride;
	}
	
	public AltFileDataContainerManager() {}

	public FileDataContainerID getNewFileDataContainerID() throws IOException {
		return getNewFileDataContainerID(null);
	}
	public FileDataContainerID getNewFileDataContainerID(byte[] initData) throws IOException{
		if(initData == null){
			this.fileDataContainers.add(new FileDataContainer());
		}else{
			this.fileDataContainers.add(new FileDataContainer(initData));
		}
		return new FileDataContainerID(fileDataContainers.size()-1);
	}
	private FileDataContainer getFileDataContainer(FileDataContainerID fileDataContainerID){
		return this.fileDataContainers.get(fileDataContainerID.id);
	}
	public void closeAllAndDelete() throws IOException{
		for(FileDataContainer fileDataContainer:fileDataContainers){
//			System.out.println("deleting: "+fileDataContainer.getDataFile().getAbsolutePath());
			fileDataContainer.deleteTempFile();
		}
	}
	
	public void printBytes(FileDataContainerID fileDataContainerID, PrintStream ps) throws FileNotFoundException, IOException{
		long totalSize = 0;
		if (isDataInFile(fileDataContainerID)){
			ps.println("Data in the file is:\n");
			FileInputStream fis = new FileInputStream(getFileDataContainer(fileDataContainerID).getDataFile());
			int aByte;
			while ((aByte = fis.read()) != -1) {
				ps.write(aByte);
				totalSize++;
			}
			ps.flush();
			ps.println("\nTotal Size = "+Long.toString(totalSize));
			fis.close();	
		}else{
			ps.println("Data in memory is:\n");
			byte[] data = getFileDataContainer(fileDataContainerID).getDataBytes();
			for (byte aByte : data){
				ps.write((int)aByte);
				totalSize++;
			}
			ps.flush();
		}
		ps.println("\nTotal Size = "+Long.toString(totalSize));
	}
	
	public void writeAndFlush(FileDataContainerID fileDataContainerID, OutputStream outputStream) throws IOException{
		if (isDataInFile(fileDataContainerID)){
			try(
					FileInputStream fis = new FileInputStream(getFileDataContainer(fileDataContainerID).getDataFile());
					BufferedInputStream bis = new BufferedInputStream(fis);
				){
				IOUtils.copy(bis, outputStream); //fis is not buffered inside of IOUtils
			}
		} else{
			/*
			 * Data is in memory
			 */
			outputStream.write(getFileDataContainer(fileDataContainerID).getDataBytes(), 0, getFileDataContainer(fileDataContainerID).getDataBytes().length);
			outputStream.flush();
		}

	}
	
	public void append(FileDataContainerID appendToThis,FileDataContainerID appendThis) throws IOException{
		updateAggregate(getFileDataContainer(appendThis).size());
		getFileDataContainer(appendToThis).append(getFileDataContainer(appendThis));
	}
	public void append(FileDataContainerID appendToThis,String appendThis) throws IOException{
		updateAggregate(appendThis.length());
		getFileDataContainer(appendToThis).append(appendThis);
	}
	public void append(FileDataContainerID appendToThis,byte[] appendThis) throws IOException{
		updateAggregate(appendThis.length);
		getFileDataContainer(appendToThis).appendArrayContainer(appendThis);
	}
	
	private void updateAggregate(long increase){
//		if((aggregateDataSize > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT) != ((aggregateDataSize+increase) > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT)){
//			System.out.println("-----Limit reached");
//		}
		this.aggregateDataSize = aggregateDataSize + increase;
	}
	private boolean isDataInFile(FileDataContainerID fileDataContainerID){
		return getFileDataContainer(fileDataContainerID).isDataInFile();
	}

	public void manageExistingTempFile(FileDataContainerID fileDataContainerID, File incomingFile) {
		if ((isDataInFile(fileDataContainerID) && getFileDataContainer(fileDataContainerID).tempDataFile.length()>0) ||
			(!isDataInFile(fileDataContainerID) &&getFileDataContainer(fileDataContainerID).dataBytes!=null)){
			throw new RuntimeException("FileDataContainer already has data");
		}
		if (!incomingFile.exists()){
			throw new RuntimeException("");
		}
		getFileDataContainer(fileDataContainerID).bNoAppend = true;
		getFileDataContainer(fileDataContainerID).tempDataFile = incomingFile;
	}
	public boolean isLimited(){
		return this.aggregateDataSize > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT;
	}

//	//
//	//Code coverage test, scrapbook2.default.TestFileDataContainer
//	//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		try{
//			ArrayList<Byte> byteArr = new ArrayList<>();
//			FileDataContainerManager fileDataContainerManager = new FileDataContainerManager(1000);
//			FileDataContainerID fileDataContainerIDAll = fileDataContainerManager.getNewFileDataContainerID();
//			FileDataContainerID databytesFiledataContainerID = null;
//			byte[] dataBytes = null;
//			FileDataContainerID limitedFiledataContainerID = null;
//			String mngStr = "this is a managed entry\n";
//			for(int i=0;i<100;i++){
//				FileDataContainerID fileDataContainerID = null;
//				if(i==0){
//					fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();
//					File managedFile = File.createTempFile("imanage", "test");
//					fileDataContainerManager.manageExistingTempFile(fileDataContainerID, managedFile);
//					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(managedFile));
//					bos.write(mngStr.getBytes());
//					bos.close();
//					byte[] strBytes = mngStr.getBytes();
//					for(int j=0;j<strBytes.length;j++){
//						byteArr.add(strBytes[j]);
//					}
//					for(int j=0;j<strBytes.length;j++){
//						byteArr.add(strBytes[j]);
//					}
//					fileDataContainerManager.append(fileDataContainerIDAll, fileDataContainerID);
//				}else{
//					String str = "fileDataContainerID "+i+"\n";
//					byte[] strBytes = str.getBytes();
//					if(databytesFiledataContainerID == null){
//						fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID(strBytes);
//						databytesFiledataContainerID = fileDataContainerID;
//						dataBytes = strBytes;
//					}else if(limitedFiledataContainerID == null && fileDataContainerManager.isLimited()){
//						fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID(strBytes);
//						limitedFiledataContainerID = fileDataContainerID;
//					}else{
//						fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();
//						fileDataContainerManager.append(fileDataContainerID,str);
//					}
//					fileDataContainerManager.append(fileDataContainerID,str);
//					for(int j=0;j<strBytes.length;j++){
//						byteArr.add(strBytes[j]);
//					}
//					for(int j=0;j<strBytes.length;j++){
//						byteArr.add(strBytes[j]);
//					}
//				}
//				fileDataContainerManager.append(fileDataContainerIDAll, fileDataContainerID);
//				
//			}
//			fileDataContainerManager.append(fileDataContainerIDAll, databytesFiledataContainerID);
//			for(int i=0;i<dataBytes.length;i++){
//				byteArr.add(dataBytes[i]);
//			}
//			for(int i=0;i<dataBytes.length;i++){
//				byteArr.add(dataBytes[i]);
//			}
//			
//			byte[] bytes = new byte[256];
//			for(int i=0;i<256;i++){
//				bytes[i] = (byte)(i&0x000000ff);
//				byteArr.add(bytes[i]);
//			}
//			fileDataContainerManager.append(fileDataContainerIDAll, bytes);
//
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			fileDataContainerManager.writeAndFlush(fileDataContainerIDAll, bos);
//			byte[] allBytes = bos.toByteArray();
//			bos.close();
//
//			//check managed for code coverage
//			ByteArrayOutputStream byos = new ByteArrayOutputStream();
//			fileDataContainerManager.writeAndFlush(databytesFiledataContainerID, byos);
//			byte[] managedbytes = byos.toByteArray();
//			byos.close();
//			for(int i=0;i<managedbytes.length;i++){
//				if(managedbytes[i] != allBytes[i+mngStr.length()*2]){
//					throw new Exception("databytes don't match at "+i);
//				}
//			}
//
//			fileDataContainerManager.closeAllAndDelete();
//			
//			System.out.println("RAW:\n"+(new String(allBytes)));
//			System.out.println("\nHEX:\n"+Hex.toString(allBytes));
//			if(allBytes.length != byteArr.size()){
//				throw new Exception("sizes don't match allBytes="+allBytes.length+" byteArr="+byteArr.size());
//			}
//			byte[] b = new byte[1];
//			for(int i=0;i<byteArr.size();i++){
//				b[0] = byteArr.get(i);
//				if(b[0] != allBytes[i]){
//					throw new Exception("bytes don't match at "+i);
//				}
//				System.out.print(Hex.toString(b));
//			}
//			
//			
//		}catch(Exception e){
//			lg.error(e);
//		}
//	}

}
