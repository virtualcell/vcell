package org.vcell.imagej;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.test.VCellClientTest;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.nrrd.NrrdWriter;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Model.Owner;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;

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
		private BufferedInputStream bis;
		public ImageJConnection(ExternalCommunicator externalCommunicator) throws Exception{
			serverSocket = new ServerSocket(externalCommunicator.getPort());
			this.externalCommunicator = externalCommunicator;
		}
		public void openConnection(ImageJHelper.VCellImageJCommands command,String descr) throws Exception{
			socket = serverSocket.accept();
			serverSocket.close();
			bis = new BufferedInputStream(socket.getInputStream());
			dis = new DataInputStream(bis);
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(command.name());
			dos.writeUTF(descr);
			String startMessage = null;
			switch(externalCommunicator){
				case IMAGEJ:
					startMessage = dis.readUTF();
					break;
				case BLENDER:
					startMessage = readLine();
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
			try{bis.close();}catch(Exception e){errors.add(e);}
			try{dos.close();}catch(Exception e){errors.add(e);}
			try{socket.close();}catch(Exception e){errors.add(e);}
			try{serverSocket.close();}catch(Exception e){errors.add(e);}
			if(errors.size() > 0){
				return errors.toArray(new Exception[0]);
			}
			return null;
		}
		public String readLine() throws IOException{
			return ImageJHelper.readLine(bis);
		}
	}
	
	private static class BasicStackDimensions {
		@XmlAttribute
		public int xsize;
		@XmlAttribute
		public int ysize;
		@XmlAttribute
		public int zsize;
		@XmlAttribute
		public int csize;
		@XmlAttribute
		public int tsize;
		public BasicStackDimensions() {
			
		}
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
		public int[] domainSubvolumeIDs;//this will be non-null if this is a domain mask
		public double[] timePoints;
		public String[] channelDescriptions;
		public int[] channelSubvolIDs;
		public int[] colormap;
		public HyperStackHelper(BasicStackDimensions basicStackDimensions,Extent extent,boolean hasOverlays,String dataClass,int[] domainSubvolumeIDs,int[] channelSubvolIDs,double[] timePoints,String[] channelDescriptions,int[] colormap) {
			super(basicStackDimensions.xsize,basicStackDimensions.ysize,basicStackDimensions.zsize,basicStackDimensions.csize,basicStackDimensions.tsize);
			this.extent = extent;
			this.dataClass = dataClass;
			this.hasOverlays = hasOverlays;
			this.domainSubvolumeIDs = domainSubvolumeIDs;
			this.channelSubvolIDs = channelSubvolIDs;
			this.timePoints = timePoints;
			this.channelDescriptions = channelDescriptions;
			this.colormap = colormap;
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
			//write colormap
			dos.writeInt((colormap != null?colormap.length:0));
			if(colormap != null){
				for (int i = 0; i < colormap.length; i++) {
					dos.writeInt(colormap[i]);//argb
				}
			}
			//write subvolumeIDs
			dos.writeInt((domainSubvolumeIDs==null?0:domainSubvolumeIDs.length));
			if(domainSubvolumeIDs != null){
				for (int j = 0; j < domainSubvolumeIDs.length; j++) {
					dos.writeInt(domainSubvolumeIDs[j]);
				}
			}
			//write channel subvolumeIDs
			dos.writeInt((channelSubvolIDs==null?0:channelSubvolIDs.length));
			if(channelSubvolIDs != null){
				for (int j = 0; j < channelSubvolIDs.length; j++) {
					dos.writeInt(channelSubvolIDs[j]);
				}
			}
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
		String sizeStr = imageJConnection.readLine();
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

	private static String extract(String line) throws IOException{
		StringTokenizer st = new StringTokenizer(line, ":");
		st.nextToken();
		return st.nextToken().trim();
	}
	private static BasicStackDimensions extractArr(String line) throws IOException{
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

	public static void sendVolumeDomain0(ImageJConnection imageJConnection,CartesianMesh mesh,SimulationModelInfo simulationModelInfo,String description) throws Exception{
		Hashtable<Integer, BitSet> subVolMapMask = new Hashtable<>();
		for (int i = 0; i < mesh.getNumVolumeElements(); i++) {
			int subvolume = mesh.getSubVolumeFromVolumeIndex(i);
			if(subvolume > 255){
				throw new Exception("Error ImageJHelper.sendVolumeDomain(...) subvolume > 255 not implemented");
			}
			if(!subVolMapMask.containsKey(subvolume)){
				subVolMapMask.put(subvolume, new BitSet(mesh.getNumVolumeElements()));
			}
			subVolMapMask.get(subvolume).set(i);
		}
		ArrayList<String> channelDescriptions = new ArrayList<>();
		if(simulationModelInfo != null){
			for(Integer subvolID:subVolMapMask.keySet()){
				channelDescriptions.add(simulationModelInfo.getVolumeNameGeometry(subvolID)+":"+simulationModelInfo.getVolumeNamePhysiology(subvolID));
			}
		}
		int[] subvolumeIDs = new int[subVolMapMask.size()];
		Enumeration<Integer> subvolid = subVolMapMask.keys();
		int cnt = 0;
		while(subvolid.hasMoreElements()){
			subvolumeIDs[cnt] = subvolid.nextElement();
			cnt++;
		}
		sendData0(imageJConnection, new HyperStackHelper(new BasicStackDimensions(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ(), subVolMapMask.size(), 1),mesh.getExtent(),false,Byte.class.getSimpleName(),subvolumeIDs,null,null,(channelDescriptions.size()==0?null:channelDescriptions.toArray(new String[0])),null), subVolMapMask,description);

	}
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
			sendVolumeDomain0(imageJConnection[0], pdeDataContext.getCartesianMesh(), simulationModelInfo, description);
		}catch(Exception e){
			if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
				//ignore, we were cancelled
			}
		}finally{
			try{if(imageJConnection != null){imageJConnection[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
		}

	}
	public static String readLine(BufferedInputStream bis) throws IOException{
		StringBuffer sb = new StringBuffer();
		int nextChar = 0;
		while((nextChar = bis.read()) != -1){
			if(nextChar == '\n'){
				break;
			}else if(nextChar == '\r'){
				bis.mark(1);
				nextChar = bis.read();
				if(nextChar == -1 || nextChar != '\n'){
					bis.reset();
					break;
				}
			}
			sb.append((char)nextChar);
		}
		return sb.toString();
	}
	public static void vcellSendNRRD(final Component requester,BufferedInputStream bis,ClientTaskStatusSupport clientTaskStatusSupport,ImageJConnection imageJConnection,String description,double[] timePoints,String[] channelDescriptions) throws Exception{
    	if(clientTaskStatusSupport != null){
    		clientTaskStatusSupport.setMessage("reading format... ");
    	}
		//read nrrd file format (See NRRDWriter.writeNRRD(...) for header format)
		DataInputStream dis = new DataInputStream(bis);
		readLine(bis);//magic nrrd
		readLine(bis);//endian
		readLine(bis);//comment
		String type = extract(readLine(bis));//"double"
		Integer.parseInt(extract(readLine(bis)));//integer (dimension)
		extract(readLine(bis));//"raw" (encoding)
		BasicStackDimensions basicStackDimensions = extractArr(readLine(bis));
		
		//read other text header elements until exhausted
		String unused = "";
		while((unused = readLine(bis)).length() != 0){
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
			sendData0(imageJConnection, new HyperStackHelper(basicStackDimensions, new Extent(1,1,1), false, Float.class.getSimpleName(), null,null,timePoints,channelDescriptions,null), data,description);
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

	private static void sendData0(ImageJConnection imageJConnection, HyperStackHelper hyperStackHelper,Object dataObj,String title) throws Exception{
		//
		//This method expects data to be in "x,y,z,t,c" order
		//
		//See cbit.vcell.export.server.RasterExporter.exportPDEData(...), NRRD_SINGLE, GEOMETRY_FULL
		//See also RasterExporter.NRRDHelper.createSingleFullNrrdInfo(...)
		//
		imageJConnection.dos.writeUTF(title);
		hyperStackHelper.writeInfo(imageJConnection.dos);
		if(dataObj instanceof double[]){//convert to floats for imagej (GRAY32 image type)
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
			}
			if(dataLen%buffersize != 0){
				imageJConnection.dos.write(bytes, 0, (dataLen%buffersize)*Float.BYTES);
			}
		}else if(dataObj instanceof byte[]){//send 8bit bytes as is to ImageJ (grayscale or colormap images)
			imageJConnection.dos.write((byte[])dataObj);
		}else if(dataObj instanceof Hashtable && hyperStackHelper.domainSubvolumeIDs != null){//convert to bytes (0 or 255) for ImageJ binary processing
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
	public static void vcellSendImage(final Component requester,final PDEDataContext pdeDataContext,SubVolume subvolume,Hashtable<SampledCurve, int[]>[] membraneTables,String description,double[] timePoints,String[] channelDescriptions,int[] colormap) throws Exception{//xyz, 1 time, 1 var
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
					BasicStackDimensions basicStackDimensions = new BasicStackDimensions(xyzSize.getX(), xyzSize.getY(), xyzSize.getZ(), 1, 1);
					sendData0(imageJConnection, new HyperStackHelper(basicStackDimensions,extent,true,Float.class.getSimpleName(),null,new int[] {subvolume.getHandle()},timePoints,channelDescriptions,colormap), pdeDataContext.getDataValues(),"'"+pdeDataContext.getVariableName()+"'"+pdeDataContext.getTimePoint());
					sendVolumeDomain0(imageJConnection, pdeDataContext.getCartesianMesh(), null, description);
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
	
	
	
	private static String createXML(Object theClass) throws Exception{
//		vcListXML.setCommandInfo(result);
		JAXBContext context = JAXBContext.newInstance(theClass.getClass());
		Marshaller m = context.createMarshaller();
		// for pretty-print XML in JAXB
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		// Write to list to a writer
		m.marshal(theClass, writer);
		String str = writer.toString();
//		System.out.println(str);
		// write the content to a physical file
//		new FileWriter("jaxbTest.xml").write(result);
		return str;
	}
//	@XmlRootElement()
	private static class VCCommand{
		@XmlElement
		private String command;
		@XmlElement
		private String description;
		public VCCommand() {
			
		}
		public VCCommand(String command, String description) {
			super();
			this.command = command;
			this.description = description;
		}		
	}
	
	@XmlRootElement()
	private static class VCCommandList{
		@XmlElement()
		private ArrayList<VCCommand> commandInfo;
		public VCCommandList() {
			
		}
		public VCCommandList(ArrayList<VCCommand> commandInfo) {
			super();
			this.commandInfo = commandInfo;
		}
	}
	private enum ApiEnum {getinfo,getdata};
	private static String GETINFO_PARMS = IJListParams.type.name()+"={"+IJDocType.bm.name()+","+IJDocType.mm.name()+","+IJDocType.quick.name()+"}"+"&"+IJListParams.open.name()+"={true,false}";
	private static String GETDATA_PARMS = IJGetDataParams.cachekey.name()+"=key";
	public static class ApiInfoHandler extends AbstractHandler
	{
		private HashMap<ApiEnum, VCCommand[]> apiParams = new HashMap<>();
		public ApiInfoHandler() {
			apiParams.put(ApiEnum.getinfo, new VCCommand[] {
					/*"type={biom,math}","type=sims&modelname=xxx"*/
					new VCCommand(ApiEnum.getinfo.name()+"?"+GETINFO_PARMS, "List of Bio/Math models information")
					});
			apiParams.put(ApiEnum.getdata, new VCCommand[] {
					/*"type={biom,math}","type=sims&modelname=xxx"*/
					new VCCommand(ApiEnum.getdata.name()+"?"+GETDATA_PARMS, "Get sim data")
					});
		}
	    @Override
	    public void handle( String target,
	                        Request baseRequest,
	                        HttpServletRequest request,
	                        HttpServletResponse response ) throws IOException,
	                                                      ServletException
	    {
	    	
	    	String baseUri = "http:"+baseRequest.getHttpURI().toString();
	    	System.out.println(target+"\n"+baseUri);
	        // Declare response encoding and types
	        response.setContentType("text/html; charset=utf-8");

	        // Declare response status code
	        response.setStatus(HttpServletResponse.SC_OK);

	        ArrayList<VCCommand> result = new ArrayList<>();
	        for(ApiEnum apiEnum:ApiEnum.values()) {
		        
		        VCCommand[] commands = apiParams.get(apiEnum);
	        	for(VCCommand commandInfo:commands) {
	        		String res = baseUri+commandInfo.command;
//		        	response.getWriter().println(res);
		        	result.add(new VCCommand(res, commandInfo.description));
	        	}

//		        if(params != null) {
//		        	for(VCCommand s:params) {
//		        		String res = baseUri+apiEnum.name()+"?"+s.command;
//			        	response.getWriter().println(res);
//			        	result.add(new VCCommand(res, "blah"));
//		        	}
//		        }else {
//		        	response.getWriter().println(baseUri+apiEnum.name());
//		        	result.add(new VCCommand(baseUri+apiEnum.name(), "blah"));
//		        }
		        

	        }
	        // Write back response
//	        response.getWriter().println("<h1>Hello World</h1>");

	        // Inform jetty that this request has now been handled
	        baseRequest.setHandled(true);
	        
			VCCommandList vcListXML = new VCCommandList(result);
	        try {
				response.getWriter().println(createXML(vcListXML));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e);
			}
	    }

	}
	
	private static class IJSimInfo {
//		@XmlAttribute()
		private String quickrunKey;
		@XmlAttribute()
		private String name;
		@XmlAttribute()
		private long cacheKey;
		public IJSimInfo() {
			
		}
		public IJSimInfo(String quickrunKey, String name) {
			super();
			this.quickrunKey = quickrunKey;
			this.name = name;
			this.cacheKey = ijCacheCounter++;
		}
		
	}
	private static class IJContextInfo{
		@XmlAttribute()
		private String name;
		@XmlAttribute()
		private String mathType;
		@XmlAttribute()
		private Integer gomeDim;
		@XmlAttribute()
		private String geomName;
		@XmlElement(name="simInfo")
		private ArrayList<IJSimInfo> ijSimId;
		public IJContextInfo() {
			
		}
		public IJContextInfo(String name, MathType mathType, Integer gomeDim, String geomName, ArrayList<IJSimInfo> ijSimId) {
			super();
			this.name = name;
			this.mathType = (mathType != null?mathType.name():null);
			this.gomeDim = gomeDim;
			this.geomName = geomName;
			this.ijSimId = ijSimId;
		}
		
	}
	private static enum IJDocType {
		bm(VCDocument.VCDocumentType.BIOMODEL_DOC),
		mm(VCDocument.VCDocumentType.MATHMODEL_DOC),
		quick(null);
		
		VCDocument.VCDocumentType vcDocType;
		private IJDocType(VCDocument.VCDocumentType vcDocType) {
			this.vcDocType = vcDocType;
		}
		public VCDocument.VCDocumentType getVCDocType() {
			return vcDocType;
		}
		
	}
	private static class IJModelInfo {
		@XmlAttribute()
		private String name;
		@XmlAttribute()
		private String date;
		@XmlAttribute()
		private String type;
		@XmlAttribute()
		private Boolean open;
		@XmlAttribute()
		private String user;
		@XmlElement(name="context")
		ArrayList<IJContextInfo> contexts;
		public IJModelInfo() {
			
		}
		public IJModelInfo(String name, Date date, IJDocType type, Boolean open,String user,ArrayList<IJContextInfo> contexts) {
			super();
			this.name = name;
			this.date = (date != null?date.toString():null);
			this.type = (type != null?type.name():null);
			this.open = open;
			this.user = user;
			this.contexts = contexts;
		}
		
	}
	@XmlRootElement()
	private static class IJModelInfos {
		@XmlElement()
		private ArrayList<IJModelInfo> modelInfo;
		public IJModelInfos() {
			
		}
		public IJModelInfos(ArrayList<IJModelInfo> modelInfo) {
			super();
			this.modelInfo = modelInfo;
		}
		
	}

	private static void addSimToIJContextInfo(ArrayList<IJContextInfo>ijContextInfos,String contextName,MathType mathType,int geomDim,String geomName,Simulation[] sims) {
		ArrayList<IJSimInfo> ijSimInfos = new ArrayList<>();
		for(Simulation sim:sims) {
			ijSimInfos.add(new IJSimInfo(null, sim.getName()));
		}
		ijContextInfos.add(new IJContextInfo(contextName,mathType,geomDim,geomName, ijSimInfos));	
	}
	private static void populateDesktopIJModelInfos(IJDocType docType,ArrayList<KeyValue> openKeys,ArrayList<IJModelInfo> modelInfos) {
		Collection<TopLevelWindowManager> windowManagers = VCellClientTest.getVCellClient().getMdiManager().getWindowManagers();
		for(TopLevelWindowManager topLevelWindowManager:windowManagers) {
	    	if(topLevelWindowManager instanceof DocumentWindowManager) {
	    		DocumentWindowManager documentWindowManager = (DocumentWindowManager)topLevelWindowManager;
	    		VCDocument.VCDocumentType currDocType = documentWindowManager.getVCDocument().getDocumentType();
	    		if(currDocType == docType.getVCDocType()) {
		    		if(documentWindowManager.getVCDocument().getVersion() != null && documentWindowManager.getVCDocument().getVersion().getVersionKey() != null) {
		    			openKeys.add(documentWindowManager.getVCDocument().getVersion().getVersionKey());
		    		}else {//open but never saved
		    			ArrayList<IJContextInfo> ijContextInfos = new ArrayList<>();
		    			if(documentWindowManager instanceof BioModelWindowManager) {
		    				for(SimulationContext simulationContext:((BioModelWindowManager)documentWindowManager).getBioModel().getSimulationContexts()) {
			    				addSimToIJContextInfo(ijContextInfos, simulationContext.getName(), simulationContext.getMathType(),simulationContext.getGeometryContext().getGeometry().getDimension(),simulationContext.getGeometry().getName(),simulationContext.getSimulations());
		    				}
		    				modelInfos.add(new IJModelInfo(documentWindowManager.getVCDocument().getName(), null, IJDocType.bm, true,documentWindowManager.getUser().getName(), ijContextInfos));
		    			}else if(documentWindowManager instanceof MathModelWindowManager) {
		    				MathModel mathModel = ((MathModelWindowManager)documentWindowManager).getMathModel();
		    				addSimToIJContextInfo(ijContextInfos, null,mathModel.getMathDescription().getMathType(),mathModel.getGeometry().getDimension(),mathModel.getGeometry().getName(),((MathModelWindowManager)documentWindowManager).getSimulationWorkspace().getSimulations());
		    				modelInfos.add(new IJModelInfo(documentWindowManager.getVCDocument().getName(), null, IJDocType.mm, true,documentWindowManager.getUser().getName(), ijContextInfos));
		    			}
		    		}
	    		}
	    	}
		}
	}
	private static List<NameValuePair> getParamsFromRequest(HttpServletRequest request) throws ServletException{
    	List<NameValuePair> params = null;
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
        	queryString = requestURL.toString();
        } else {
        	queryString = requestURL.append('?').append(queryString).toString();
        }
    	try {
			return URLEncodedUtils.parse(new URI(queryString), "UTF-8");
		} catch (URISyntaxException e) {
			throw new ServletException(e);
		}

	}

	private static long ijCacheCounter = 0;
	private static ArrayList<IJModelInfo> ijModelInfoCache;
	
	private static enum IJListParams {type,open}
	public static class ApiListHandler extends AbstractHandler{

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
	    	System.out.println(target+"\n"+baseRequest.getQueryString());
	    	List<NameValuePair> params = getParamsFromRequest(request);
	    	IJDocType ijDocType = null;
	    	Boolean bOpen = null;
//	    	Long cacheKey = null;
	    	for(NameValuePair nameValuePair:params) {
	    		if(nameValuePair.getName().equals(IJListParams.type.name())) {
	    			ijDocType = IJDocType.valueOf(nameValuePair.getValue());
	    		}else if(nameValuePair.getName().equals(IJListParams.open.name())) {
	    			bOpen = Boolean.parseBoolean(nameValuePair.getValue());
	    		}
//	    		else if(nameValuePair.getName().equals(IJListParams.cachekey.name())) {
//	    			cacheKey = Long.valueOf(nameValuePair.getValue());
//	    		}
	    	}
	    	if(ijDocType == null && bOpen == null/* && cacheKey == null*/) {
	    		response.setContentType("text/html; charset=utf-8");
	    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		response.getWriter().println("<h1>Expecting '"+GETINFO_PARMS+"' in request</h1>");
	    	}else {
		        // Declare response encoding and types
		        response.setContentType("text/xml; charset=utf-8");
	
		        // Declare response status code
		        response.setStatus(HttpServletResponse.SC_OK);
		        
		        ArrayList<KeyValue> openKeys = new ArrayList<>();		     
		        ArrayList<IJModelInfo> modelInfos = new ArrayList<>();
		        if(bOpen == null || bOpen) {
		        	populateDesktopIJModelInfos(ijDocType,openKeys, modelInfos);
		        }
		        
		        if(ijDocType == null || ijDocType == IJDocType.quick) {
			        ArrayList<File> dirs = new ArrayList<>();
			        dirs.add(ResourceUtil.getLocalRootDir());
			        while(dirs.size() != 0) {
			        	File dir = dirs.remove(0);
			        	File[] files = dir.listFiles();
			        	for (File file:files) {
			        		if(file.isHidden()) {
			        			continue;
			        		}
							if(file.isDirectory()) {
								dirs.add(file);
							}else if (file.getName().startsWith("SimID_") && file.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)) {
								StringTokenizer st = new StringTokenizer(file.getName(), "_");
								st.nextToken();
								String quickrunKey = st.nextToken();
								String parentSimName = null;
								String parentContextName = null;
								Integer parentGeomDim = null;
								MathType parentMathType = null;
								String parentGeomName = null;
								String parentModelName = null;
								Date parentDate = null;
								String parentUser = null;
								IJDocType docType = IJDocType.quick;
					    		Collection<TopLevelWindowManager> windowManagers = VCellClientTest.getVCellClient().getMdiManager().getWindowManagers();
					    		for(TopLevelWindowManager topLevelWindowManager:windowManagers) {
					    			if(topLevelWindowManager.getComponent() != null && topLevelWindowManager instanceof DocumentWindowManager) {//can be null for databseWinManger or FieldDataWindowMangr if they are not showing
					    				DocumentWindowManager documentWindowManager = (DocumentWindowManager)topLevelWindowManager;
					    				ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(topLevelWindowManager.getComponent());
					    				SimulationWindow simulationWindow = childWindowManager.getTempSimWindow(quickrunKey);
					    				if(simulationWindow != null) {
					    					parentSimName = simulationWindow.getSimulation().getName();
					    					parentContextName = simulationWindow.getSimOwner().getName();
					    					parentGeomDim = simulationWindow.getSimOwner().getGeometry().getDimension();
					    					parentMathType = simulationWindow.getSimOwner().getMathDescription().getMathType();
					    					parentGeomName = simulationWindow.getSimOwner().getGeometry().getName();
					    					parentModelName = documentWindowManager.getVCDocument().getName();
					    					parentDate = documentWindowManager.getVCDocument().getVersion().getDate();
					    					parentUser = documentWindowManager.getVCDocument().getVersion().getOwner().getName();
					    					docType = (documentWindowManager.getVCDocument().getDocumentType() ==VCDocumentType.BIOMODEL_DOC?IJDocType.bm:IJDocType.mm);
					    				}
					    			}
					    		}
					    		if(bOpen == null || (bOpen && docType != IJDocType.quick)){
									ArrayList<IJContextInfo> contInfos = new ArrayList<>();
									ArrayList<IJSimInfo> ijsimfos= new ArrayList<>();
									ijsimfos.add(new IJSimInfo(quickrunKey, parentSimName));
									contInfos.add(new IJContextInfo(parentContextName, parentMathType, parentGeomDim, parentGeomName, ijsimfos));
									modelInfos.add(new IJModelInfo(parentModelName, parentDate, docType, (docType != IJDocType.quick?true:false), parentUser,contInfos));
					    		}
							}
						}
			        }
		        	
		        }
		        RequestManager requestManager = VCellClientTest.getVCellClient().getRequestManager();
		        if(ijDocType == null || ijDocType == IJDocType.bm) {
			        BioModelInfo[] bioModelInfos = requestManager.getDocumentManager().getBioModelInfos();
			        for(BioModelInfo bioModelInfo:bioModelInfos) {
			        	if(bOpen && !openKeys.contains(bioModelInfo.getVersion().getVersionKey())) {
			        		continue;
			        	}
				        ArrayList<IJContextInfo> ijContextInfos = new ArrayList<>();
			        	BioModelChildSummary bioModelChildSummary = bioModelInfo.getBioModelChildSummary();
						if(bioModelChildSummary.getSimulationContextNames() != null) {
			        		for(int i = 0; i<bioModelInfo.getBioModelChildSummary().getSimulationContextNames().length;i++) {
			        			String bioModelContextName = bioModelInfo.getBioModelChildSummary().getSimulationContextNames()[i];
			        			if(bioModelContextName != null) {
			        				ArrayList<IJSimInfo> ijSimInfos = new ArrayList<>();
			        				for(String simName:bioModelInfo.getBioModelChildSummary().getSimulationNames(bioModelContextName)) {
			        					ijSimInfos.add(new IJSimInfo(null,simName));
			        				}
		        					IJContextInfo ijContextInfo = new IJContextInfo(bioModelContextName,bioModelInfo.getBioModelChildSummary().getAppTypes()[i],bioModelInfo.getBioModelChildSummary().getGeometryDimensions()[i],bioModelInfo.getBioModelChildSummary().getGeometryNames()[i],ijSimInfos);
		        					ijContextInfos.add(ijContextInfo);
			        			}
			        		}
			        	}
			        	modelInfos.add(new IJModelInfo(bioModelInfo.getVersion().getName(), bioModelInfo.getVersion().getDate(), IJDocType.bm, openKeys.contains(bioModelInfo.getVersion().getVersionKey()),bioModelInfo.getVersion().getOwner().getName(), ijContextInfos));
			        }
		        }
		        
		        if(ijDocType == null || ijDocType == IJDocType.mm) {
			        MathModelInfo[] mathModelInfos = requestManager.getDocumentManager().getMathModelInfos();
			        for(MathModelInfo mathModelInfo:mathModelInfos) {
			        	if(bOpen && !openKeys.contains(mathModelInfo.getVersion().getVersionKey())) {
			        		continue;
			        	}
	    				ArrayList<IJSimInfo> ijSimInfos = new ArrayList<>();
	    				for(String simName:mathModelInfo.getMathModelChildSummary().getSimulationNames()) {
	    					ijSimInfos.add(new IJSimInfo(null,simName));
	    				}
	
				        ArrayList<IJContextInfo> ijContextInfos = new ArrayList<>();
						IJContextInfo ijContextInfo = new IJContextInfo(null,mathModelInfo.getMathModelChildSummary().getModelType(),mathModelInfo.getMathModelChildSummary().getGeometryDimension(),mathModelInfo.getMathModelChildSummary().getGeometryName(),ijSimInfos);
						ijContextInfos.add(ijContextInfo);
			        	modelInfos.add(new IJModelInfo(mathModelInfo.getVersion().getName(), mathModelInfo.getVersion().getDate(), IJDocType.mm, openKeys.contains(mathModelInfo.getVersion().getVersionKey()), mathModelInfo.getVersion().getOwner().getName(),ijContextInfos));
			        }
		        }
		        // Write back response
//		        response.getWriter().println("<h1>getInfo requested</h1>");
		        try {
					response.getWriter().println(createXML(new IJModelInfos(modelInfos)));
			        ijModelInfoCache = modelInfos;
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
	    	}
	        // Inform jetty that this request has now been handled
	        baseRequest.setHandled(true);
	        
			
		}
		
	}
	
//	@XmlRootElement()
//	private static class IJData {
//		@XmlAttribute()
//		private int x;
//		@XmlAttribute()
//		private int y;
//		@XmlAttribute()
//		private int z;
//		@XmlAttribute()
//		private int c;
//		@XmlAttribute()
//		private int t;
//		@XmlElement()
//		private double[] data;
//		public IJData() {
//			
//		}
//		public IJData(int x, int y, int z, int c, int t, double[] data) {
//			super();
//			this.x = x;
//			this.y = y;
//			this.z = z;
//			this.c = c;
//			this.t = t;
//			this.data = data;
//		}
//	}
	@XmlRootElement
	private static class IJData {
		@XmlElement
		private BasicStackDimensions stackInfo;
		@XmlInlineBinaryData
		private byte[] data;
		public IJData() {
			
		}
		public IJData(BasicStackDimensions stackInfo, byte[] data) {
			super();
			this.stackInfo = stackInfo;
			this.data = data;
		}
		
	}
	
	private static class IJVarInfo{
		@XmlAttribute
		private String name;
		@XmlAttribute
		private String displayName;
		@XmlAttribute
		private String variableType;
		@XmlAttribute
		private String domain;
		@XmlAttribute
		private boolean bFunction;
		public IJVarInfo() {
			
		}
		public IJVarInfo(String name, String displayName, String variableType, String domain, boolean bFunction) {
			this.name = name;
			this.displayName = displayName;
			this.variableType = variableType;
			this.domain = domain;
			this.bFunction = bFunction;
		}
		
	}
	private static enum IJGetDataParams {cachekey,varname,timepoint}
	public static class ApiGetDataHandler extends AbstractHandler{
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
	    	System.out.println(target+"\n"+baseRequest.getQueryString());
	    	List<NameValuePair> params = getParamsFromRequest(request);
	    	Long cacheKey = null;
	    	String varname = null;
	    	Double timepoint = null;
	    	for(NameValuePair nameValuePair:params) {
	    		if(nameValuePair.getName().equals(IJGetDataParams.cachekey.name())) {
	    			cacheKey = Long.valueOf(nameValuePair.getValue());
	    		}else if(nameValuePair.getName().equals(IJGetDataParams.varname.name())) {
	    			varname = nameValuePair.getValue();
	    		}else if(nameValuePair.getName().equals(IJGetDataParams.timepoint.name())) {
	    			timepoint = Double.parseDouble(nameValuePair.getValue());
	    		}

	    	}
	    	if(cacheKey == null) {
	    		response.setContentType("text/html; charset=utf-8");
	    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		response.getWriter().println("<h1>Expecting '"+GETINFO_PARMS+"' in request</h1>");
	    	}else {
//		        // Declare response encoding and types
//		        response.setContentType("text/xml; charset=utf-8");
//	
//		        // Declare response status code
//		        response.setStatus(HttpServletResponse.SC_OK);
		        
		        if(cacheKey != null) {
			        boolean bFound = false;
		        	if(ijModelInfoCache != null) {
		        		outerloop:
		        		for(IJModelInfo ijModelInfo:ijModelInfoCache) {
		        			if(ijModelInfo.contexts != null) {
		        				for(IJContextInfo ijContextInfo:ijModelInfo.contexts) {
		        					if(ijContextInfo.ijSimId != null) {
		        						for(IJSimInfo ijSimInfo:ijContextInfo.ijSimId) {
		        							if(ijSimInfo.cacheKey == cacheKey) {
		        								if(ijSimInfo.quickrunKey != null) {//quickrun sim
		        									try {
			        							        ArrayList<File> dirs = new ArrayList<>();
			        							        dirs.add(ResourceUtil.getLocalRootDir());
			        							        while(dirs.size() != 0) {
			        							        	File dir = dirs.remove(0);
			        							        	File[] files = dir.listFiles();
			        							        	for (File file:files) {
			        							        		if(file.isHidden()) {
			        							        			continue;
			        							        		}
			        											if(file.isDirectory()) {
			        												dirs.add(file);
			        											}else if (file.getName().startsWith("SimID_") && file.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)) {
			        												StringTokenizer st = new StringTokenizer(file.getName(), "_");
			        												st.nextToken();
			        												String quickrunKey = st.nextToken();
			        												if(quickrunKey.equals(ijSimInfo.quickrunKey)) {
			        													User user = new User("quick", new KeyValue("0"));
			        													VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(quickrunKey), user);
			        													VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);//quickrun always job 0
			        													SimulationData simData = new SimulationData(vcSimulationDataIdentifier, file.getParentFile(), file.getParentFile(), null);
			        													CartesianMesh mesh = simData.getMesh();
			        													BasicStackDimensions basicStackDimensions = new BasicStackDimensions(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ(), 1, simData.getDataTimes().length);
			        													if(varname!=null) {
			        														SimDataBlock simDataBlock = simData.getSimDataBlock(null, varname, timepoint);
				        													int numDoubles = basicStackDimensions.getTotalSize();
				        													byte[] byteDoubles = new byte[numDoubles*Double.BYTES];
				        													ByteBuffer bb = ByteBuffer.wrap(byteDoubles);
				        													for(int i=0;i<numDoubles;i++) {
				        														bb.putDouble(simDataBlock.getData()[i]);
				        													}
				        													IJData ijData = new IJData(basicStackDimensions, byteDoubles);
				    			        						    		response.setContentType("text/html; charset=utf-8");
				    			        						    		response.setStatus(HttpServletResponse.SC_OK);
				    			        						    		response.getWriter().write(createXML(ijData));
			        													}else {
				        													DataIdentifier[] dataIdentifiers = simData.getVarAndFunctionDataIdentifiers(null);
				        													ArrayList<String> varNames = new ArrayList<>();
				        													if(dataIdentifiers != null) {
				        														for(DataIdentifier did:dataIdentifiers) {
				        															System.out.println(did.toString());
				        														}
				        													}
			        														
			        													}
//			    			        						    		response.getWriter().println("<h1>mesh="+mesh.getSizeX()+","+mesh.getSizeY()+","+mesh.getSizeZ()+"\n"+
//			    			        						    				"numTimes="+simData.getDataTimes().length+
//			    			        						    				"</h1>");
			    			        						    		bFound = true;
			    			        						    		break outerloop;
			        												}	
			        											}
			        							        	}
			        							        }
		        									}catch(Exception e) {
		        										throw new ServletException(e);
		        									}
		        								}else {//saved sim
		        									RequestManager requestManager = VCellClientTest.getVCellClient().getRequestManager();
		        								}
		        							}
		        						}
		        					}
		        				}
		        			}
		        		}
				        if(!bFound) {
				    		response.setContentType("text/html; charset=utf-8");
				    		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				    		response.getWriter().println("<h1>Key="+cacheKey+" not found</h1>");
				        }
		        	}else {
		        		throw new ServletException("No vcell ijcache to lookup cacheKey="+cacheKey);
		        	}
		        }
	    	}
	        // Inform jetty that this request has now been handled
	        baseRequest.setHandled(true);

			
		}
		
	}

    public static void startService() throws Exception
    {
        Server server = new Server(8080);
        
        ContextHandler contextRoot = new ContextHandler("/");
        contextRoot.setHandler(new ApiInfoHandler());
        
        ContextHandler context = new ContextHandler("/"+ApiEnum.getinfo.name()+"/");
        context.setHandler(new ApiListHandler());
        
        ContextHandler contextData = new ContextHandler("/"+ApiEnum.getdata.name()+"/");
        contextData.setHandler(new ApiGetDataHandler());

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { contextRoot,context,contextData });

        server.setHandler(contexts);

//        server.setHandler(new HelloWorld());
        System.out.println(server.getURI());
        server.start();
//        server.join();
    }

    public static void exerciseService() {
    	try {
//    	HttpClient httpClient = new HttpClient();
//    	HostConfiguration hostConfiguration = new HostConfiguration();
//    	hostConfiguration.setHost("localhost",8080);
//    	HttpMethod method = new GetMethod("/list?type=biom");
//			int var = httpClient.executeMethod(hostConfiguration, method);
//			System.out.println("result="+var);
//			
//			method = new GetMethod("/");
//			var = httpClient.executeMethod(hostConfiguration, method);
//			System.out.println("result="+var);

//			URL url = new URL("http://localhost:8080/");
//			URL url = new URL("http://localhost:8080/"+ApiEnum.getinfo.name()+"?open=false&type=math");
    		System.out.println(getApiResponse("http://localhost:8080/"));
//    		System.out.println(getApiResponse("http://localhost:8080/"+ApiEnum.getinfo.name()+"?"/*+"open=true"+"&"*/+IJListParams.type.name()+"="+IJDocType.quick.name()));
//    		System.out.println(getApiResponse("http://localhost:8080/"+ApiEnum.getdata.name()+"?"/*+"open=true"+"&"*/+IJListParams.cachekey.name()+"=0"));
//    		String simData = getApiResponse("http://localhost:8080/"+ApiEnum.getdata.name()+"?"/*+"open=true"+"&"*/+IJListParams.cachekey.name()+"=0");
    		JAXBContext jaxbContext = JAXBContext.newInstance(IJData.class);

    		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    		IJData ijData = (IJData) jaxbUnmarshaller.unmarshal(new URL("http://localhost:8080/"+ApiEnum.getdata.name()+"?"/*+"open=true"+"&"*/+IJGetDataParams.cachekey.name()+"=0"));
    		System.out.println(ijData);
    		
//			URLConnection con = url.openConnection();
//			InputStream in = con.getInputStream();
//			String encoding = con.getContentEncoding();
//			encoding = encoding == null ? "UTF-8" : encoding;
//			String body = IOUtils.toString(in, encoding);
//			System.out.println(body);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static String getApiResponse(String url) throws Exception{
        String responseBody = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()){
            HttpGet httpget = new HttpGet(url);

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        HttpEntity entity = response.getEntity();
                        String res = entity != null ? EntityUtils.toString(entity) : null;
                        throw new ClientProtocolException("Unexpected response status: " + res);
                    }
                }

            };
            responseBody = httpclient.execute(httpget, responseHandler);
//            System.out.println("----------------------------------------");
//            System.out.println(responseBody);
        	return responseBody;
        }
    }

//	public static class ApiGetDataHandler extends AbstractHandler{
//
//		@Override
//		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
//				throws IOException, ServletException {
//			VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModel(bioModelInfo)
//			
//		}
//		
//	}

}
