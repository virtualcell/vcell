package cbit.vcell.export.server.datacontainer;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDataContainerManager {
    private final static Logger lg = LogManager.getLogger(ResultDataContainerManager.class);
    private final AggregateDataSize aggregateDataSize;
    int containersCreated;
    List<ResultDataContainer> resultDataContainers;
    HybridDataContainerCollection dataContainers;

    public ResultDataContainerManager() {
        this.containersCreated = 0;
        this.aggregateDataSize = new AggregateDataSize();
        this.dataContainers = new HybridDataContainerCollection(this.aggregateDataSize);
    }

    public ResultDataContainerID containerizeData() throws IOException {
        ResultDataContainer dataContainer = new InMemoryDataContainer();
        this.dataContainers.add(dataContainer);
        return dataContainer.getId();
    }
    public ResultDataContainerID containerizeData(byte[] initData) throws IOException {
        ResultDataContainer dataContainer = new InMemoryDataContainer(initData);
        this.dataContainers.add(dataContainer);
        return dataContainer.getId();
    }

    private ResultDataContainer getFileDataContainer(ResultDataContainerID resultDataContainerID){
        return this.resultDataContainers.get(resultDataContainerID.id());
    }

    public void closeAllAndDeleteFiles() {
        this.dataContainers.clear();
    }

    public void printBytes(ResultDataContainerID resultDataContainerID, PrintStream ps) throws IOException {
        String containerTypeString;
        ResultDataContainer dataContainer = this.getFileDataContainer(resultDataContainerID);
        if (dataContainer instanceof FileDataContainer) containerTypeString = "the file";
        else if (dataContainer instanceof InMemoryDataContainer) containerTypeString = "memory";
        else containerTypeString = "an unknown format";

        ps.printf("Data in %s is:%n%n", containerTypeString);
        ps.write(dataContainer.getDataBytes());
        ps.flush();
        ps.println("\nTotal Size = " + dataContainer.getDataSize());
    }

    public void writeAndFlush(ResultDataContainerID resultDataContainerID, OutputStream outputStream) throws IOException {
        ResultDataContainer dataContainer = this.getFileDataContainer(resultDataContainerID);
        outputStream.write(dataContainer.getDataBytes());
        outputStream.flush();
    }

    public void append(ResultDataContainerID appendToThis, ResultDataContainerID appendThis) throws IOException{
        this.aggregateDataSize.increaseDataAmount(this.getFileDataContainer(appendThis).getDataSize());
        this.getFileDataContainer(appendToThis).append(getFileDataContainer(appendThis));
    }
    public void append(ResultDataContainerID appendToThis, String appendThis) throws IOException{
        this.aggregateDataSize.increaseDataAmount(appendThis.length());
        this.getFileDataContainer(appendToThis).append(appendThis);
    }
    public void append(ResultDataContainerID appendToThis, byte[] appendThis) throws IOException{
        this.aggregateDataSize.increaseDataAmount(appendThis.length);
        this.getFileDataContainer(appendToThis).append(appendThis);
    }


    private boolean isDataInFile(ResultDataContainerID resultDataContainerID){
        return this.getFileDataContainer(resultDataContainerID) instanceof FileDataContainer;
    }

    public void manageExistingTempFile(ResultDataContainerID resultDataContainerID, File incomingFile) {
        ResultDataContainer dataContainer = this.getFileDataContainer(resultDataContainerID);
        if (dataContainer instanceof FileDataContainer fileDataContainer
                && (fileDataContainer.getDataBytes() != null || fileDataContainer.getDataSize() > 0))
            throw new RuntimeException("FileDataContainer already has data");
        if (!incomingFile.exists()){
            throw new RuntimeException("");
        }

        /*
        if ((isDataInFile(resultDataContainerID) && getFileDataContainer(resultDataContainerID).tempDataFile.length()>0) ||
                (!isDataInFile(resultDataContainerID) &&getFileDataContainer(resultDataContainerID).dataBytes!=null)){
            throw new RuntimeException("FileDataContainer already has data");
        }
        if (!incomingFile.exists()){
            throw new RuntimeException("");
        }
        getFileDataContainer(resultDataContainerID).bNoAppend = true;
        getFileDataContainer(resultDataContainerID).tempDataFile = incomingFile;*/
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
