package cbit.vcell.export.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class FileDataContainerManager {
	ArrayList<FileDataContainer> fileDataContainers = new ArrayList<FileDataContainer>();
	public class FileDataContainerID{
		private int id;
		public FileDataContainerID (int id){
			this.id = id;
		}
	}
	public FileDataContainerID getNewFileDataContainerID() throws IOException{
		return getNewFileDataContainerID(null);
	}
	private FileDataContainerID getNewFileDataContainerID(byte[] initData) throws IOException{
		if(initData == null){
			fileDataContainers.add(new FileDataContainer());
		}else{
			fileDataContainers.add(new FileDataContainer(initData));
		}
		return new FileDataContainerID(fileDataContainers.size()-1);
	}
	public FileDataContainer getFileDataContainer(FileDataContainerID fileDataContainerID){
		return fileDataContainers.get(fileDataContainerID.id);
	}
	public void closeAllAndDelete(){
		for(FileDataContainer fileDataContainer:fileDataContainers){
			fileDataContainer.closeAndDelete();
		}
	}
	public void append(FileDataContainerID appendToThis,FileDataContainerID appendThis) throws IOException{
		getFileDataContainer(appendToThis).append(getFileDataContainer(appendThis));
	}
	public void append(FileDataContainerID appendToThis,String appendThis) throws IOException{
		getFileDataContainer(appendToThis).append(appendThis);
	}
	public void append(FileDataContainerID appendToThis,byte[] appendThis) throws IOException{
		getFileDataContainer(appendToThis).append(getFileDataContainer(getNewFileDataContainerID(appendThis)));
	}
	public File getFile(FileDataContainerID fileDataContainerID){
		return getFileDataContainer(fileDataContainerID).getDataFile();
	}
}
