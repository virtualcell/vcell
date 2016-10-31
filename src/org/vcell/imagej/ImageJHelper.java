package org.vcell.imagej;

import java.awt.Component;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipInputStream;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.UserCancelException;

import cbit.vcell.client.data.SimulationModelInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.nrrd.NrrdWriter;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.simdata.PDEDataContext;

public class ImageJHelper {
	public static final String USER_ABORT = "userAbort";
	public enum ExternalCommunicator 
	{
	  IMAGEJ(5000),
	  BLENDER(5001);

	  private final int port; 
	  private ExternalCommunicator(final int port) { this.port = port; }
	  public int getPort() { return this.port;}
	}	
	public static class ImageJConnection {
		public ServerSocket serverSocket;
		public Socket socket;
		public DataInputStream dis;
		public DataOutputStream dos;
		public ExternalCommunicator externalCommunicator;
		public ImageJConnection(ExternalCommunicator externalCommunicator) throws Exception{
			serverSocket = new ServerSocket(externalCommunicator.getPort());
			this.externalCommunicator = externalCommunicator;
		}
		public void openConnection(ImageJHelper.VCellImageJCommands command,String descr) throws Exception{
			socket = serverSocket.accept();
			serverSocket.close();
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(command.name());
			dos.writeUTF(descr);
			String startMessage = null;
			switch(externalCommunicator){
				case IMAGEJ:
					startMessage = dis.readUTF();
					break;
				case BLENDER:
					startMessage = dis.readLine();
					break;
				default:
					throw new IllegalArgumentException("Unexpected external program "+externalCommunicator.name());
			}
			if(startMessage == null){
				throw new Exception(externalCommunicator.name()+" unexpectedly stopped communicating");
			}
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
	
	private static class BasicStackDimensions {
		public int xsize;
		public int ysize;
		public int zsize;
		public int csize;
		public int tsize;
		public BasicStackDimensions(int xsize, int ysize, int zsize, int csize, int tsize){
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
			return new int[] {xsize,ysize,zsize,tsize,csize};
		}
	}
	private static class HyperStackHelper extends BasicStackDimensions{
		public String dataClass;
		public boolean hasOverlays;
		public Extent extent;
		public boolean isMask;
		public double[] timePoints;
		public String[] channelDescriptions;
		public HyperStackHelper(BasicStackDimensions basicStackDimensions,Extent extent,boolean hasOverlays,String dataClass,boolean isMask,double[] timePoints,String[] channelDescriptions) {
			super(basicStackDimensions.xsize,basicStackDimensions.ysize,basicStackDimensions.zsize,basicStackDimensions.csize,basicStackDimensions.tsize);
			this.extent = extent;
			this.dataClass = dataClass;
			this.hasOverlays = hasOverlays;
			this.isMask = isMask;
			this.timePoints = timePoints;
			this.channelDescriptions = channelDescriptions;
		}
		public void writeInfo(DataOutputStream dos) throws Exception{
			dos.writeUTF(dataClass);
			for (int i = 0; i < getSizesInVCellPluginOrder().length; i++) {
				dos.writeInt(getSizesInVCellPluginOrder()[i]);
			}
			dos.writeDouble(extent.getX());
			dos.writeDouble(extent.getY());
			dos.writeDouble(extent.getZ());
			dos.writeBoolean(hasOverlays);
			dos.writeBoolean(isMask);
			//timepoints
			dos.writeInt((timePoints==null?0:timePoints.length));
			if(timePoints != null){
				for (int i = 0; i < timePoints.length; i++) {
					dos.writeDouble(timePoints[i]);
				}
			}
			//channel descriptions
			dos.writeInt((channelDescriptions==null?0:channelDescriptions.length));
			if(channelDescriptions != null){
				for (int i = 0; i < channelDescriptions.length; i++) {
					dos.writeUTF(channelDescriptions[i]);
				}
			}
		}
	}
	public static enum VCellImageJCommands {vcellWantImage,vcellWantInfo,vcellSendImage,vcellSendInfo,vcellSendDomains,vcellWantSurface};

	private static enum doneFlags {working,cancelled,finished};

	public static File vcellWantSurface(ClientTaskStatusSupport clientTaskStatusSupport,String description) throws Exception{
		return doCancellableConnection(ExternalCommunicator.BLENDER, VCellImageJCommands.vcellWantSurface, clientTaskStatusSupport, description);
	}
	private static File vcellWantSurface0(ClientTaskStatusSupport clientTaskStatusSupport,String description,ImageJConnection imageJConnection) throws Exception{
		String sizeStr = imageJConnection.dis.readLine();
		int fileSize = Integer.parseInt(sizeStr);
		byte[] bytes = new byte[fileSize];
		int numread = 0;
		while(numread >= 0 && numread != bytes.length){
			numread+= imageJConnection.dis.read(bytes, numread, bytes.length-numread);
		}
		File newFile = File.createTempFile("vcellBlener", ".stl");
		FileUtils.writeByteArrayToFile(bytes, newFile);
		return newFile;
	}
	
	private static void startCancelThread(ClientTaskStatusSupport clientTaskStatusSupport,ImageJConnection[] imageJConnectionArr,doneFlags[] bDone){
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
						try{Thread.sleep(250);}catch(Exception e){e.printStackTrace();}
					}
				}
			}).start();
		}
	}
	private static File doCancellableConnection(ExternalCommunicator externalCommunicator,VCellImageJCommands command,ClientTaskStatusSupport clientTaskStatusSupport,String description) throws Exception{
		ImageJConnection[] imageJConnectionArr = new ImageJConnection[1];
		doneFlags[] bDone = new doneFlags[] {doneFlags.working};
		try{
			imageJConnectionArr[0] = new ImageJConnection(externalCommunicator);
			startCancelThread(clientTaskStatusSupport, imageJConnectionArr, bDone);
			imageJConnectionArr[0].openConnection(command,description);
			switch(command){
				case vcellWantSurface:
					return vcellWantSurface0(clientTaskStatusSupport, description, imageJConnectionArr[0]);
//					break;
				case vcellWantImage:
					return vcellWantImage0(clientTaskStatusSupport, description,imageJConnectionArr[0]);
//					break;
				default:
					throw new IllegalArgumentException("Unexpected command "+command.name());	
			}
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
	public static File vcellWantImage(ClientTaskStatusSupport clientTaskStatusSupport,String description) throws Exception{
		return doCancellableConnection(ExternalCommunicator.IMAGEJ, VCellImageJCommands.vcellWantImage, clientTaskStatusSupport, description);
	}
	private static File vcellWantImage0(ClientTaskStatusSupport clientTaskStatusSupport,String description,ImageJConnection imageJConnection) throws Exception{
		if(clientTaskStatusSupport != null){
			clientTaskStatusSupport.setMessage("Waiting for ImageJ to send image...");
		}
		//Create nrrd file from socket input
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
	}

	private static String extract(DataInputStream dis) throws IOException{
		String line = dis.readLine();
		StringTokenizer st = new StringTokenizer(line, ":");
		st.nextToken();
		return st.nextToken().trim();
	}
	private static BasicStackDimensions extractArr(DataInputStream dis) throws IOException{
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
		return new BasicStackDimensions((result.length>=1?result[0]:1), (result.length>=2?result[1]:1), (result.length>=3?result[2]:1), (result.length>=5?result[4]:1), (result.length>=4?result[3]:1));
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

	public static void sendVolumeDomain(Component requester,PDEDataContext pdeDataContext,ISize iSize,ClientTaskStatusSupport clientTaskStatusSupport,ListenAndCancel listenAndCancel,String description,SimulationModelInfo simulationModelInfo) throws Exception{
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
	    	imageJConnection[0] = new ImageJConnection(ExternalCommunicator.IMAGEJ);
			imageJConnection[0].openConnection(VCellImageJCommands.vcellSendDomains,description);
			Hashtable<Integer, BitSet> subVolMapMask = new Hashtable<>();
			for (int i = 0; i < pdeDataContext.getCartesianMesh().getNumVolumeElements(); i++) {
				int subvolume = pdeDataContext.getCartesianMesh().getSubVolumeFromVolumeIndex(i);
				if(subvolume > 255){
					throw new Exception("Error ImageJHelper.sendVolumeDomain(...) subvolume > 255 not implemented");
				}
				if(!subVolMapMask.containsKey(subvolume)){
					subVolMapMask.put(subvolume, new BitSet(pdeDataContext.getCartesianMesh().getNumVolumeElements()));
				}
				subVolMapMask.get(subvolume).set(i);
			}
			ArrayList<String> channelDescriptions = new ArrayList<>();
			for(Integer subvolID:subVolMapMask.keySet()){
				channelDescriptions.add(simulationModelInfo.getVolumeNameGeometry(subvolID)+":"+simulationModelInfo.getVolumeNamePhysiology(subvolID));
			}
			sendData0(imageJConnection[0], new HyperStackHelper(new BasicStackDimensions(iSize.getX(), iSize.getY(), iSize.getZ(), subVolMapMask.size(), 1),pdeDataContext.getCartesianMesh().getExtent(),false,Byte.class.getSimpleName(),true,null,channelDescriptions.toArray(new String[0])), subVolMapMask,description);
		}catch(Exception e){
			if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
				//ignore, we were cancelled
			}
		}finally{
			try{if(imageJConnection != null){imageJConnection[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
		}

	}
	public static void vcellSendNRRD(final Component requester,ZipInputStream nrrdFileFormatData,ClientTaskStatusSupport clientTaskStatusSupport,ImageJConnection imageJConnection,String description,double[] timePoints,String[] channelDescriptions) throws Exception{
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
		BasicStackDimensions basicStackDimensions = extractArr(dis);
		
		//read other text header elements until exhausted
		String unused = "";
		while((unused = dis.readLine()).length() != 0){
			System.out.println(unused);
		}
		try{
	    	if(clientTaskStatusSupport != null){
	    		clientTaskStatusSupport.setMessage("Sending data to ImageJ...");
	    	}
			imageJConnection.openConnection(VCellImageJCommands.vcellSendImage,description);
			double[] data = new double[basicStackDimensions.getTotalSize()];
			for (int i = 0; i < data.length; i++) {
				data[i] = dis.readDouble();
			}
			sendImageDataAsFloats(imageJConnection, new HyperStackHelper(basicStackDimensions, new Extent(1,1,1), false, Float.class.getSimpleName(), false,timePoints,channelDescriptions), data,description);
		}
		finally{
			try{if(imageJConnection != null){imageJConnection.closeConnection();}}catch(Exception e){e.printStackTrace();}
		}

	}
	private static void sendMembraneOutline(ImageJConnection imageJConnection,Hashtable<SampledCurve, int[]>[] membraneTables) throws Exception{
		if(membraneTables != null){
			imageJConnection.dos.writeInt(membraneTables.length);//num slices
			for(Hashtable<SampledCurve, int[]> membraneTable:membraneTables){
				//System.out.println();
				if(membraneTable != null){//some slices have no membrane outline
					imageJConnection.dos.writeInt(membraneTable.size());//num polygons on slice
					Enumeration<SampledCurve> sliceMembranes = membraneTable.keys();
					while(sliceMembranes.hasMoreElements()){
						SampledCurve sampledCurve = sliceMembranes.nextElement();
						Vector<Coordinate> polygonPoints = sampledCurve.getControlPointsVector();
						//System.out.println(polygonPoints.size());
						imageJConnection.dos.writeInt(polygonPoints.size());//num points for polygon
						imageJConnection.dos.writeInt((sampledCurve.isClosed()?1:0));//isClosed
						for(Coordinate coord:polygonPoints){
							imageJConnection.dos.writeDouble(coord.getX());
							imageJConnection.dos.writeDouble(coord.getY());
							imageJConnection.dos.writeDouble(coord.getZ());
						}
					}
				}else{
					imageJConnection.dos.writeInt(0);
				}
			}
		}else{
			imageJConnection.dos.writeInt(0);
		}
	}
	private static void sendImageDataAsFloats(ImageJConnection imageJConnection, HyperStackHelper hyperStackHelper,double[] data,String title) throws Exception{
		sendData0(imageJConnection, hyperStackHelper, data, title);
	}

	private static void sendData0(ImageJConnection imageJConnection, HyperStackHelper hyperStackHelper,Object dataObj,String title) throws Exception{
		//
		//This method expects data to be in "x,y,z,t,c" order
		//
		//See cbit.vcell.export.server.RasterExporter.exportPDEData(...), NRRD_SINGLE, GEOMETRY_FULL
		//See also RasterExporter.NRRDHelper.createSingleFullNrrdInfo(...)
		//
		imageJConnection.dos.writeUTF(title);
		hyperStackHelper.writeInfo(imageJConnection.dos);
		if(dataObj instanceof double[]){//convert to floats for imagej
			final int buffersize = 100000;
			byte[] bytes = new byte[Float.BYTES*buffersize];
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
			int dataLen = hyperStackHelper.getTotalSize();
			for (int i = 0; i < dataLen; i++) {
				float val = (float)(((double[])dataObj)[i]);
				if(val == 0 && ((double[])dataObj)[i] != 0){
					val = Float.MIN_VALUE;
				}
				byteBuffer.putFloat(val);
				if((i+1)%buffersize == 0){
					imageJConnection.dos.write(bytes, 0, buffersize*Float.BYTES);
					byteBuffer.rewind();
				}
//				imageJConnection.dos.writeFloat(val);
			}
			if(dataLen%buffersize != 0){
				imageJConnection.dos.write(bytes, 0, (dataLen%buffersize)*Float.BYTES);
			}
		}else if(dataObj instanceof byte[]){
			imageJConnection.dos.write((byte[])dataObj);
		}else if(dataObj instanceof Hashtable && hyperStackHelper.isMask){
			int channelSize = hyperStackHelper.xsize*hyperStackHelper.ysize*hyperStackHelper.zsize;
			Enumeration<Integer> subVolIDs = ((Hashtable<Integer, BitSet>)dataObj).keys();
			while(subVolIDs.hasMoreElements()){
				Integer subvolID = subVolIDs.nextElement();
				BitSet bitset = ((Hashtable<Integer,BitSet>)dataObj).get(subvolID);
				System.out.println(bitset.cardinality());
				byte[] data = new byte[channelSize];
				Arrays.fill(data, (byte)0);
				for (int i = 0; i < channelSize; i++) {
					if(bitset.get(i)){
						data[i]|= 0xFF;
					}
				}
				imageJConnection.dos.write(data);
			}
		}else{
			throw new IllegalArgumentException("Unexpected data type="+dataObj.getClass().getName());
		}
	}
	public static void vcellSendImage(final Component requester,final PDEDataContext pdeDataContext,Hashtable<SampledCurve, int[]>[] membraneTables,String description,double[] timePoints,String[] channelDescriptions) throws Exception{//xyz, 1 time, 1 var
		final ImageJConnection[] imageJConnectionArr = new ImageJConnection[1];
		AsynchClientTask sendImageTask = new AsynchClientTask("Send image to ImageJ...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				try{					
					ImageJConnection imageJConnection = new ImageJConnection(ExternalCommunicator.IMAGEJ);
					imageJConnectionArr[0] = imageJConnection;
					imageJConnection.openConnection(VCellImageJCommands.vcellSendImage,description);
					//send size of the standard 5 dimensions in this order (width, height, nChannels, nSlices, nFrames)
					ISize xyzSize = pdeDataContext.getCartesianMesh().getISize();
					Extent extent = pdeDataContext.getCartesianMesh().getExtent();
					sendImageDataAsFloats(imageJConnection, new HyperStackHelper(new BasicStackDimensions(xyzSize.getX(), xyzSize.getY(), xyzSize.getZ(), 1, 1),extent,true,Float.class.getSimpleName(),false,timePoints,channelDescriptions), pdeDataContext.getDataValues(),"'"+pdeDataContext.getVariableName()+"'"+pdeDataContext.getTimePoint());
					sendMembraneOutline(imageJConnection, membraneTables);
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
