package cbit.vcell.export.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.commons.compress.utils.IOUtils;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;

public class FileDataContainerManager {
	ArrayList<FileDataContainer> fileDataContainers = new ArrayList<FileDataContainer>();
	private long aggregateDataSize = 0;
	private static long AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT= PropertyLoader.getLongProperty(PropertyLoader.exportMaxInMemoryLimit, 1024*1024*100); 
	
	public class FileDataContainerID{
		private final int id;
		public FileDataContainerID (int id){
			this.id = id;
		}
	}
	
	private class FileDataContainer {

		File tempDataFile = null;
		byte[] dataBytes = null;
		private int maxMemoryArraySize=1024*1024*200;
		private boolean bDataInFile = false;
		public boolean bNoAppend = false;


		FileDataContainer(boolean bFileBacked) throws IOException{
			tempDataFile = File.createTempFile("TempFile", ".tmp");
			tempDataFile.deleteOnExit();
			if (bFileBacked){
				maxMemoryArraySize=0;
				bDataInFile=true;
			}
		}


		FileDataContainer(byte[] dataBytes, boolean bFileBacked) throws IOException{
			this(bFileBacked);
			if (dataBytes.length > maxMemoryArraySize){
				bDataInFile = true;
				FileUtils.writeByteArrayToFile(dataBytes, tempDataFile);
			} else {
				this.dataBytes = dataBytes;
			}
		}

		FileDataContainer(FileDataContainer container, boolean bFileBacked) throws IOException{
			this(bFileBacked);
			this.append(container, bFileBacked);
		}

		void append(CharSequence csq, boolean bFileBacked) throws IOException	{
			if (bNoAppend){
				throw new RuntimeException("FileDataContainer can't append externally supplied files"); 
			}
			int dataBytesSize=0;
			if (dataBytes!=null) {
				dataBytesSize=dataBytes.length;
			}
			if (bDataInFile || (dataBytesSize + csq.length() > maxMemoryArraySize)){
				//first copy data to file if not there already
				if (!bDataInFile){
					try(
							BufferedOutputStream bos = getBufferedOutputStream(true)
						){
							if (dataBytes!=null) {
								getBufferedOutputStream(true).write(dataBytes, 0, dataBytes.length);  
								bos.flush();
							}
						}
					bDataInFile = true;
					dataBytes = null;  //no longer needed or not needed in the first place
				}	
				try(BufferedOutputStream bos = getBufferedOutputStream(true)){
					bos.write(csq.toString().getBytes());
					bos.flush();
				} 
				
			} else {
				if (dataBytes==null){
					dataBytes = csq.toString().getBytes();
				} else {
					byte[] csqBytes=csq.toString().getBytes();
					byte[] newDataBytes = new byte[dataBytes.length+csqBytes.length];
					System.arraycopy(dataBytes,0,newDataBytes,0,dataBytes.length);
					System.arraycopy(csqBytes,0,newDataBytes,dataBytes.length,csqBytes.length);
					dataBytes=newDataBytes;
				}
			}
			
		}


		private void appendArrayContainer(byte[] bytesToAppend) throws  IOException{
			if (bNoAppend){
				throw new RuntimeException("FileDataContainer can't append externally supplied files"); 
			}
			if (bDataInFile){
				try(
						BufferedOutputStream bos = getBufferedOutputStream(true)
					){
		   				getBufferedOutputStream(true).write(bytesToAppend, 0, bytesToAppend.length);                 
						bos.flush();

					}
			} else {
				if (dataBytes==null){
					dataBytes=bytesToAppend;
					return;
				}
				if (dataBytes.length + bytesToAppend.length < maxMemoryArraySize){
					byte[] newDataBytes = new byte[dataBytes.length+bytesToAppend.length];
					System.arraycopy(dataBytes,0,newDataBytes,0,dataBytes.length);
					System.arraycopy(bytesToAppend,0,newDataBytes,dataBytes.length,bytesToAppend.length);
					dataBytes=newDataBytes;
				} else {
					try(
							BufferedOutputStream bos = getBufferedOutputStream(true)
						){
							bos.write(dataBytes, 0, dataBytes.length);  
			   				getBufferedOutputStream(true).write(bytesToAppend, 0, bytesToAppend.length);                 
							bos.flush();
							bDataInFile = true;
							dataBytes = null; //no longer needed

						}
				}
				
			}
		}


		private void appendFileContainer(File fileToAppend) throws IOException{
			if (bNoAppend){
				throw new RuntimeException("FileDataContainer can't append externally supplied files"); 
			}
			if (bDataInFile) {
			try(
					FileInputStream fis = new FileInputStream(fileToAppend);
					BufferedInputStream bis = new BufferedInputStream(fis);
					BufferedOutputStream bos = getBufferedOutputStream(true)
				){
				
				IOUtils.copy(bis, bos);
					// Copy the contents of the file to the output stream
//					byte[] buffer = new byte[(int)Math.min(1048576/*2^20*/,fileToAppend.length())];
//					int count = 0;                 
//					while ((count = bis.read(buffer)) >= 0) {    
//						bos.write(buffer, 0, count);
//					}                 
					bos.flush();
					fis.close();
				}
			} else {
				try(
						BufferedOutputStream bos = getBufferedOutputStream(true)
					){
		   				if (dataBytes!=null){
		   					bos.write(dataBytes, 0, dataBytes.length);                 
		   					bos.flush();
		   				}
						bDataInFile = true;
						dataBytes = null; //no longer needed
					}
			}
		}

		void append(FileDataContainer container, boolean bFileBacked) throws IOException {
			if (bNoAppend){
				throw new RuntimeException("FileDataContainer can't append externally supplied files"); 
			}
			File appendThisDataFile = null;
			byte[] appendThisData = null;
			if (bFileBacked){
				maxMemoryArraySize = 0;
			}

			if (container.isDataInFile()){
				appendThisDataFile = container.getDataFile();
				appendFileContainer(appendThisDataFile);
			} else {
				appendThisData = container.getDataBytes();
				appendArrayContainer(appendThisData);
			}
		}

		public boolean isDataInFile(){
			return bDataInFile;
		}

		public File getDataFile() throws IOException{
			if (!isDataInFile() && dataBytes!=null){
				try(
						BufferedOutputStream bos = getBufferedOutputStream(true)
					){
						bos.write(dataBytes, 0, dataBytes.length);                
						bos.flush();
						dataBytes = null; //no longer needed

					}
			}
			bDataInFile=true;
			return tempDataFile;
		}

		public byte[] getDataBytes(){
			return dataBytes;
		}

		public long size(){
			if (!bDataInFile){
				if (dataBytes==null){
					return 0;
				} else {
					return (long)dataBytes.length;
				} 
			} else {
				if (tempDataFile==null){
					return 0;
				} else {
					return tempDataFile.length();
				}
			}
		}

		private BufferedOutputStream getBufferedOutputStream(boolean bAppend) throws FileNotFoundException{
			return new BufferedOutputStream(new FileOutputStream(tempDataFile, bAppend));

		}

		void deleteTempFile() throws IOException {
			if(getDataFile() != null && getDataFile().exists()){
				if (!getDataFile().delete()){	
				new Exception("Failed to delete ExportOutput Tempfile '"+getDataFile().getAbsolutePath()+"'").printStackTrace();
				}
			}
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
	
	FileDataContainerManager(long memLimitOverride){
		this.AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT = memLimitOverride;
	}
	
	public FileDataContainerManager() {
	}

	public FileDataContainerID getNewFileDataContainerID() throws IOException{
		return getNewFileDataContainerID(null);
	}
	private FileDataContainerID getNewFileDataContainerID(byte[] initData) throws IOException{
		if(initData == null){
			fileDataContainers.add(new FileDataContainer(aggregateDataSize > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT));
		}else{
			aggregateDataSize = aggregateDataSize + initData.length;
			fileDataContainers.add(new FileDataContainer(initData, aggregateDataSize > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT));
		}
		return new FileDataContainerID(fileDataContainers.size()-1);
	}
	private FileDataContainer getFileDataContainer(FileDataContainerID fileDataContainerID){
		return fileDataContainers.get(fileDataContainerID.id);
	}
	public void closeAllAndDelete() throws IOException{
		for(FileDataContainer fileDataContainer:fileDataContainers){
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
			byte[] data = getDataBytes(fileDataContainerID);
			for (byte aByte : data){
				ps.write((int)aByte);
				totalSize++;
			}
			ps.flush();
		}
		ps.println("\nTotal Size = "+Long.toString(totalSize));
	}
	
	public void writeAndFlush(FileDataContainerID fileDataContainerID, OutputStream outputStream) throws IOException{
//		FileInputStream fis = new FileInputStream(data.getDataFile());
//		ReadableByteChannel source = Channels.newChannel(fis);
//	    WritableByteChannel target = Channels.newChannel(outputStream);
	//
//	    ByteBuffer buffer = ByteBuffer.allocate(16 * 4096);
//	    while (source.read(buffer) != -1) {
//	        buffer.flip(); // Prepare the buffer to be drained
//	        while (buffer.hasRemaining()) {
//	            target.write(buffer);
//	        }
//	        buffer.clear(); // Empty buffer to get ready for filling
//	    }
	//
//	    source.close();
//		fis.close();
		if (isDataInFile(fileDataContainerID)){
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try{
				File dataFile = getFileDataContainer(fileDataContainerID).getDataFile();
				fis = new FileInputStream(dataFile);
				bis = new BufferedInputStream(fis);
				// Copy the contents of the file to the output stream
				byte[] buffer = new byte[(int)Math.min(1048576/*2^20*/,dataFile.length())];
				int count = 0;                 
				while ((count = bis.read(buffer)) >= 0) {    
					outputStream.write(buffer, 0, count);
				}                 
				outputStream.flush();
			}finally{
				if(bis != null){try{bis.close();}catch(Exception e){e.printStackTrace();}}
				if(fis != null){try{fis.close();}catch(Exception e){e.printStackTrace();}}
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
		aggregateDataSize = aggregateDataSize + getFileDataContainer(appendThis).size();
		getFileDataContainer(appendToThis).append(getFileDataContainer(appendThis), aggregateDataSize > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT);
	}
	public void append(FileDataContainerID appendToThis,String appendThis) throws IOException{
		aggregateDataSize = aggregateDataSize + appendThis.length();
		getFileDataContainer(appendToThis).append(appendThis, aggregateDataSize > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT);
	}
	public void append(FileDataContainerID appendToThis,byte[] appendThis) throws IOException{
		aggregateDataSize = aggregateDataSize + getFileDataContainer(getNewFileDataContainerID(appendThis)).size();
		getFileDataContainer(appendToThis).append(getFileDataContainer(getNewFileDataContainerID(appendThis)),aggregateDataSize > AGGREGATE_DATA_SIZE_IN_MEMORY_LIMIT);
	}
	
	private boolean isDataInFile(FileDataContainerID fileDataContainerID){
		return getFileDataContainer(fileDataContainerID).isDataInFile();
	}
	
//	public File getFile(FileDataContainerID fileDataContainerID) throws IOException{
//		return getFileDataContainer(fileDataContainerID).getDataFile();
//	}
	
	private byte[] getDataBytes(FileDataContainerID fileDataContainerID){
		return getFileDataContainer(fileDataContainerID).getDataBytes();
	}

	public void manageExistingTempFile(FileDataContainerID fileDataContainerID, File incomingFile) {
		if (getFileDataContainer(fileDataContainerID).tempDataFile.length()>0 || getFileDataContainer(fileDataContainerID).dataBytes!=null){
			throw new RuntimeException("FileDataContainer already has data");
		}
		if (!incomingFile.exists()){
			throw new RuntimeException("");
		}
		getFileDataContainer(fileDataContainerID).bNoAppend = true;
		getFileDataContainer(fileDataContainerID).bDataInFile = true;
		getFileDataContainer(fileDataContainerID).tempDataFile = incomingFile;
	}
}
