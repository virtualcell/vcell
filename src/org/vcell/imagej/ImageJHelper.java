package org.vcell.imagej;

import java.awt.Component;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.zip.ZipInputStream;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ISize;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.UserCancelException;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.nrrd.NrrdWriter;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.simdata.PDEDataContext;

public class ImageJHelper {
	public static final String USER_ABORT = "userAbort";
	public static class ImageJConnection {
		public ServerSocket serverSocket;
		public Socket socket;
		public DataInputStream dis;
		public DataOutputStream dos;
		public ImageJConnection() throws Exception{
			serverSocket = new ServerSocket(5000);
		}
		public void openConnection(ImageJHelper.commands command,String descr) throws Exception{
			socket = serverSocket.accept();
			serverSocket.close();
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(command.name());
			dos.writeUTF(descr);
			String startMessage = dis.readUTF();
			if(startMessage.equals(USER_ABORT)){
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
		public Exception[] closeConnection(){
			ArrayList<Exception> errors = new ArrayList<>();
			try{dis.close();}catch(Exception e){errors.add(e);}
			try{dos.close();}catch(Exception e){errors.add(e);}
			try{socket.close();}catch(Exception e){errors.add(e);}
			try{serverSocket.close();}catch(Exception e){errors.add(e);}
			if(errors.size() > 0){
				return errors.toArray(new Exception[0]);
			}
			return null;
		}
	}
	
	private static class HyperStackHelper {
		public int xsize;
		public int ysize;
		public int zsize;
		public int csize;
		public int tsize;
		public HyperStackHelper(int xsize, int ysize, int zsize, int csize, int tsize) {
			super();
			this.xsize = xsize;
			this.ysize = ysize;
			this.zsize = zsize;
			this.csize = csize;
			this.tsize = tsize;
		}
		public int getTotalSize(){
			return xsize*ysize*zsize*csize*tsize;
		}
		public int[] getSizesInVCellPluginOrder(){
			//return sizes in "x,y,z,t,c" order
			return new int[] {zsize,ysize,xsize,csize,tsize};
		}
	}
	public static enum commands {vcellWantImage,vcellWantInfo,vcellSendImage,vcellSendInfo,vcellSendDomains};

	private static enum doneFlags {working,cancelled,finished};

	public static File vcellWantImage(ClientTaskStatusSupport clientTaskStatusSupport,String description) throws Exception{
		
		final ImageJConnection[] imageJConnectionArr = new ImageJConnection[1];
		final doneFlags[] bDone = new doneFlags[] {doneFlags.working};

		if(clientTaskStatusSupport != null){
			clientTaskStatusSupport.setMessage("Waiting for ImageJ to send image...");
		}
		//Create nrrd file from socket input
		try{
			ImageJConnection imageJConnection = new ImageJConnection();
			imageJConnectionArr[0] = imageJConnection;
			//check in separate thread for possible cancel while this task is blocked waiting for serversocket contact with ImageJ
			if(clientTaskStatusSupport != null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						while(bDone[0] == doneFlags.working){
							if(clientTaskStatusSupport.isInterrupted()){
								bDone[0] = doneFlags.cancelled;
								try{if(imageJConnectionArr[0] != null){imageJConnectionArr[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
								return;
							}
							try{Thread.sleep(100);}catch(Exception e){e.printStackTrace();}
						}
					}
				}).start();
			}
			//contact ImageJ
			imageJConnection.openConnection(commands.vcellWantImage,description);
			imageJConnection.dis.readInt();//integer (dimensions)
			//get size of the standard 5 dimensions in this order (width, height, nChannels, nSlices, nFrames)
			int xsize = imageJConnection.dis.readInt();
			int ysize = imageJConnection.dis.readInt();
			imageJConnection.dis.readInt();
			imageJConnection.dis.readInt();
			imageJConnection.dis.readInt();
			//read data
			int slices = imageJConnection.dis.readInt();
			byte[] data = new byte[slices*xsize*ysize*Double.BYTES];
			ByteBuffer byteBuffer = ByteBuffer.wrap(data);
			for (int i = 0; i < slices; i++) {
				if(clientTaskStatusSupport != null){
					clientTaskStatusSupport.setMessage("Reading Fiji/ImageJ slice "+(i+1)+" of "+slices+"...");
					if(clientTaskStatusSupport.isInterrupted()){
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				String arraytype = imageJConnection.dis.readUTF();
				int arrLength = imageJConnection.dis.readInt();
				if(arraytype.equals(byte[].class.getName())){//byte array
					byte[] bytes = new byte[arrLength];
					int numread = 0;
					while(numread != bytes.length){
						numread+= imageJConnection.dis.read(bytes, numread, bytes.length-numread);
					}
					for (int j = 0; j < bytes.length; j++) {
						byteBuffer.putDouble((double)Byte.toUnsignedInt(bytes[j]));
					}
					System.out.println("bytesRead="+numread);
				}else if(arraytype.equals(short[].class.getName())){// short array
					short[] shorts = new short[arrLength];
					for (int j = 0; j < shorts.length; j++) {
						shorts[j] = imageJConnection.dis.readShort();
						byteBuffer.putDouble((double)Short.toUnsignedInt(shorts[j]));
					}
					System.out.println("shortsRead="+shorts.length);
				}
			}

			if(clientTaskStatusSupport != null){
				clientTaskStatusSupport.setMessage("Converting slices to file...");
				if(clientTaskStatusSupport.isInterrupted()){
					throw UserCancelException.CANCEL_GENERIC;
				}
			}
			NrrdInfo nrrdInfo = NrrdInfo.createBasicNrrdInfo(5, new int[] {xsize,ysize,slices,1,1}, "double", "raw", NrrdInfo.createXYZTVMap());
			FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();
			nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
			fileDataContainerManager.append(nrrdInfo.getDataFileID(), byteBuffer.array());
			NrrdWriter.writeNRRD(nrrdInfo, fileDataContainerManager);
			File tempFile = File.createTempFile("fijinrrd", ".nrrd");
			fileDataContainerManager.writeAndFlush(nrrdInfo.getHeaderFileID(), new FileOutputStream(tempFile));
			if(clientTaskStatusSupport != null){
				clientTaskStatusSupport.setMessage("Finished ImageJ data conversion...");
				if(clientTaskStatusSupport.isInterrupted()){
					throw UserCancelException.CANCEL_GENERIC;
				}
			}
			return tempFile;
		}catch(Exception e){
			if(bDone[0] == doneFlags.cancelled){
				throw UserCancelException.CANCEL_GENERIC;
			}
			throw e;
		}finally{
			bDone[0] = doneFlags.finished;
			try{if(imageJConnectionArr[0] != null){imageJConnectionArr[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
		}
	}

	private static String extract(DataInputStream dis) throws IOException{
		String line = dis.readLine();
		StringTokenizer st = new StringTokenizer(line, ":");
		st.nextToken();
		return st.nextToken().trim();
	}
	private static HyperStackHelper extractArr(DataInputStream dis) throws IOException{
		String line = dis.readLine();
		StringTokenizer st = new StringTokenizer(line, ":");
		st.nextToken();
		String arr = st.nextToken();
		StringTokenizer st2 = new StringTokenizer(arr, " ");
		ArrayList<Integer> resultArr = new ArrayList<>();
		while(st2.hasMoreTokens()){
			resultArr.add(Integer.parseInt(st2.nextToken().trim()));
		}
		int[] result = new int[resultArr.size()];//x,y,z,t,v see RasterExporter.createSingleFullNrrdInfo(...)
		for (int i = 0; i < result.length; i++) {
			result[i] = resultArr.get(i);
		}
		return new HyperStackHelper((result.length>=1?result[0]:1), (result.length>=2?result[1]:1), (result.length>=3?result[2]:1), (result.length>=5?result[4]:1), (result.length>=4?result[3]:1));
	}
	public static class ListenAndCancel implements ProgressDialogListener {
		private Runnable cancelMethod;
		public void setCancelMethod(Runnable runnable) {
			cancelMethod = runnable;
		}
		@Override
		public void cancelButton_actionPerformed(EventObject newEvent) {
			if(cancelMethod != null){
				cancelMethod.run();
			}
		}
	};

	public static void sendVolumeDomain(Component requester,PDEDataContext pdeDataContext,ISize iSize,ClientTaskStatusSupport clientTaskStatusSupport,ListenAndCancel listenAndCancel,String description) throws Exception{
		ImageJConnection[] imageJConnection = new ImageJConnection[1];
		try{
			if(listenAndCancel != null){
				listenAndCancel.setCancelMethod(new Runnable() {
					@Override
					public void run() {
						if(imageJConnection[0] != null){
							imageJConnection[0].closeConnection();
						}
					}
				});
			}
	    	if(clientTaskStatusSupport != null){
	    		clientTaskStatusSupport.setMessage("Sending Domain data to ImageJ...");
	    	}
//	    	new Thread(new Runnable() {
//				@Override
//				public void run() {
//				}
//			}).start();
	    	imageJConnection[0] = new ImageJConnection();
			imageJConnection[0].openConnection(commands.vcellSendDomains,description);
			byte[] data = new byte[pdeDataContext.getCartesianMesh().getNumVolumeElements()];
			for (int i = 0; i < data.length; i++) {
				int subvolume = pdeDataContext.getCartesianMesh().getSubVolumeFromVolumeIndex(i);
				if(subvolume > 255){
					throw new Exception("Error ImageJHelper.sendVolumeDomain(...) subvolume > 255 not implemented");
				}
				data[i] = (byte)subvolume;
			}
			sendGrayscaleData(imageJConnection[0], new HyperStackHelper(iSize.getX(), iSize.getY(), iSize.getZ(), 1, 1), data,description);
		}catch(Exception e){
			if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
				//ignore, we were cancelled
			}
		}finally{
			try{if(imageJConnection != null){imageJConnection[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
		}

	}
	public static void vcellSendNRRD(final Component requester,ZipInputStream nrrdFileFormatData,ClientTaskStatusSupport clientTaskStatusSupport,ImageJConnection imageJConnection,String description) throws Exception{
    	if(clientTaskStatusSupport != null){
    		clientTaskStatusSupport.setMessage("reading format... ");
    	}
		//read nrrd file format (See NRRDWriter.writeNRRD(...) for header format)
		DataInputStream dis = new DataInputStream(nrrdFileFormatData);
		dis.readLine();//magic nrrd
		dis.readLine();//endian
		dis.readLine();//comment
		String type = extract(dis);//"double"
		Integer.parseInt(extract(dis));//integer (dimension)
		extract(dis);//"raw" (encoding)
		HyperStackHelper hyperStackHelper = extractArr(dis);//integers (x,y,z,t,v)
		
		//read other text header elements until exhausted
		String unused = "";
		while((unused = dis.readLine()).length() != 0){
			System.out.println(unused);
		}
		try{
	    	if(clientTaskStatusSupport != null){
	    		clientTaskStatusSupport.setMessage("Sending data to ImageJ...");
	    	}
			imageJConnection.openConnection(commands.vcellSendImage,description);
			double[] data = new double[hyperStackHelper.getTotalSize()];
			for (int i = 0; i < data.length; i++) {
				data[i] = dis.readDouble();
			}
			sendImageDataAsFloats(imageJConnection, hyperStackHelper, data,description);
		}
		finally{
			try{if(imageJConnection != null){imageJConnection.closeConnection();}}catch(Exception e){e.printStackTrace();}
		}

	}
	private static void sendImageDataAsFloats(ImageJConnection imageJConnection, HyperStackHelper hyperStackHelper,double[] data,String title) throws Exception{
		sendData0(imageJConnection, hyperStackHelper, data, title);
	}
	private static void sendGrayscaleData(ImageJConnection imageJConnection, HyperStackHelper hyperStackHelper,byte[] data,String title) throws Exception{
		sendData0(imageJConnection, hyperStackHelper, data, title);
	}
	private static void sendData0(ImageJConnection imageJConnection, HyperStackHelper hyperStackHelper,Object arrObj,String title) throws Exception{
		int dataLen = Array.getLength(arrObj);
		if(dataLen != hyperStackHelper.getTotalSize()){
			throw new Exception("data length "+dataLen+" not match hyperstack size "+hyperStackHelper.getTotalSize());
		}
		//
		//This method expects data to be in "x,y,z,t,c" order
		//
		//See cbit.vcell.export.server.RasterExporter.exportPDEData(...), NRRD_SINGLE, GEOMETRY_FULL
		//See also RasterExporter.NRRDHelper.createSingleFullNrrdInfo(...)
		//
		imageJConnection.dos.writeUTF(title);
		for (int i = 0; i < hyperStackHelper.getSizesInVCellPluginOrder().length; i++) {
			imageJConnection.dos.writeInt(hyperStackHelper.getSizesInVCellPluginOrder()[i]);
		}
		byte[] bytes = (arrObj instanceof double[]?new byte[dataLen*Float.BYTES]:(byte[]) arrObj);
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		byteBuffer.rewind();
		if(arrObj instanceof double[]){//convert to floats for imagej
			for (int i = 0; i < dataLen; i++) {
				byteBuffer.putFloat((float)(((double[])arrObj)[i]));
			}
		}//else{just send bytes as is to ImageJ}
		imageJConnection.dos.write(bytes);
	}
	public static void vcellSendImage(final Component requester,final PDEDataContext pdeDataContext,String description) throws Exception{//xyz, 1 time, 1 var
		final ImageJConnection[] imageJConnectionArr = new ImageJConnection[1];
		AsynchClientTask sendImageTask = new AsynchClientTask("Send image to ImageJ...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				try{					
					ImageJConnection imageJConnection = new ImageJConnection();
					imageJConnectionArr[0] = imageJConnection;
					imageJConnection.openConnection(commands.vcellSendImage,description);
					//send size of the standard 5 dimensions in this order (width, height, nChannels, nSlices, nFrames)
					ISize xyzSize = pdeDataContext.getCartesianMesh().getISize();
					sendImageDataAsFloats(imageJConnection, new HyperStackHelper(xyzSize.getX(), xyzSize.getY(), xyzSize.getZ(), 1, 1), pdeDataContext.getDataValues(),"'"+pdeDataContext.getVariableName()+"'"+pdeDataContext.getTimePoint());
				}catch(Exception e){
					if(e instanceof UserCancelException){
						throw e;
					}
					e.printStackTrace();
					hashTable.put("imagejerror",e);
				}finally{
					try{if(imageJConnectionArr[0] != null){imageJConnectionArr[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
				}
			
			}			
		};
//		AsynchClientTask cancelImageTask = new AsynchClientTask("Finishing...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//			@Override
//			public void run(Hashtable<String, Object> hashTable) throws Exception {
//				if(hashTable.get("imagejerror") != null && (getClientTaskStatusSupport() == null || !getClientTaskStatusSupport().isInterrupted())){
//					throw((Exception)hashTable.get("imagejerror"));
//				}
//			}
//			
//		};
		ClientTaskDispatcher.dispatch(requester, new Hashtable<>(), new AsynchClientTask[] {sendImageTask}, false, true, new ProgressDialogListener() {
			@Override
			public void cancelButton_actionPerformed(EventObject newEvent) {
				if(imageJConnectionArr[0] != null){
					imageJConnectionArr[0].closeConnection();
				}
			}
		});
	}
}
