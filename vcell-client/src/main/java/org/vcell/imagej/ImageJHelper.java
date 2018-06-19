package org.vcell.imagej;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
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
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataJobID;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.gui.DialogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.server.ClientServerManager;
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
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Model.Owner;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.util.ColumnDescription;

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
	private enum ApiEnum {hello,getinfo,getdata,gettimeseries};
	private static String GETINFO_PARMS = IJListParams.type.name()+"={"+IJDocType.bm.name()+","+IJDocType.mm.name()+","+IJDocType.quick.name()+"}"+"&"+IJListParams.open.name()+"={true,false}";
	private static String GETDATA_PARMS = IJGetDataParams.cachekey.name()+"=int"+"&"+IJGetDataParams.varname.name()+"=string"+"&"+IJGetDataParams.timepoint.name()+"="+"double";
	private static String GETTIMESERIES_PARMS =
			IJGetTimeSeriesParams.varnames.name()+"=int"+"&"+
			IJGetTimeSeriesParams.indices.name()+"=int"+"&"+
			IJGetTimeSeriesParams.starttime.name()+"=int"+"&"+
			IJGetTimeSeriesParams.steptime.name()+"=int"+"&"+
			IJGetTimeSeriesParams.endtime.name()+"=int"+"&"+
			IJGetTimeSeriesParams.bspacestats.name()+"=int"+"&"+
			IJGetTimeSeriesParams.btimestats.name()+"=int"+"&"+
			IJGetTimeSeriesParams.jobid.name()+"=int"+"&"+
			IJGetTimeSeriesParams.cacheky.name()+"=int";

	public static class ApiInfoHandler extends AbstractHandler
	{
		private HashMap<ApiEnum, VCCommand[]> apiParams = new HashMap<>();
		public ApiInfoHandler() {
			apiParams.put(ApiEnum.hello, new VCCommand[] {
					/*"type={biom,math}","type=sims&modelname=xxx"*/
					new VCCommand(ApiEnum.hello.name(), "simple reply if server is listening running")
					});
			apiParams.put(ApiEnum.getinfo, new VCCommand[] {
					/*"type={biom,math}","type=sims&modelname=xxx"*/
					new VCCommand(ApiEnum.getinfo.name()+"?"+GETINFO_PARMS, "List of Bio/Math models information")
					});
			apiParams.put(ApiEnum.getdata, new VCCommand[] {
					/*"type={biom,math}","type=sims&modelname=xxx"*/
					new VCCommand(ApiEnum.getdata.name()+"?"+GETDATA_PARMS, "Get sim data")
					});
			apiParams.put(ApiEnum.gettimeseries, new VCCommand[] {
					/*"type={biom,math}","type=sims&modelname=xxx"*/
					new VCCommand(ApiEnum.gettimeseries.name()+"?"+GETTIMESERIES_PARMS, "Get point,line,kymograph,ROI. Retrieve data at multiple times")
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
//	    	if ("POST".equalsIgnoreCase(request.getMethod())) 
//	    	{
//	    		System.out.println(" \n\n Headers");
//
//	    	    Enumeration headerNames = request.getHeaderNames();
//	    	    while(headerNames.hasMoreElements()) {
//	    	        String headerName = (String)headerNames.nextElement();
//	    	        System.out.println(headerName + " = " + request.getHeader(headerName));
//	    	    }
//
//	    	    System.out.println("\n\nParameters");
//
//	    	    Enumeration params = request.getParameterNames();
//	    	    while(params.hasMoreElements()){
//	    	        String paramName = (String)params.nextElement();
//	    	        System.out.println(paramName + " = " + request.getParameter(paramName));
//	    	    }
//
//	    	    System.out.println("\n\n Row data");
//	    	   String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//	    	   System.out.println(test);
//	    	}
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
				response.getWriter().close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e);
			}
	        
//	        try {
//				if(VCellClientTest.getVCellClient() != null){
////					TreeMap<String, double[]> mapObsTpData = new TreeMap<>();
//					double[] times = null;
//					BioModelInfo[] bioModelInfos = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModelInfos();
//					for (int i = 0; i < bioModelInfos.length; i++) {
//						if(bioModelInfos[i].getVersion().getName().equals("EGFR Full_Model_Compart_v3")) {
//							System.out.println(bioModelInfos[i]);
//							BioModel bioModel = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModel(bioModelInfos[i]);
//							SimulationContext[] simulationContexts = bioModel.getSimulationContexts();
//							for (int j = 0; j < simulationContexts.length; j++) {
//								if(simulationContexts[j].getName().toLowerCase().contains("nfsim")) {
//									TreeMap<String, double[]> mapObsTpData = new TreeMap<>();
//									SimulationContext simulationContext = simulationContexts[j];
//									Simulation[] sims = simulationContext.getSimulations();
//									for (int k = 0; k < sims.length; k++) {
//										VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(sims[k].getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), 0);
//										ODESimData odeSimData = VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().getODEData(vcSimulationDataIdentifier);
//										for (int l = 0; l < odeSimData.getRowCount(); l++) {
//											ColumnDescription columnDescription = odeSimData.getColumnDescriptions(l);
////											System.out.println(columnDescription.getName()+"\n-----");
//											double[] rowData = odeSimData.extractColumn(l);
//											if(times == null && columnDescription.getName().toLowerCase().equals("t")) {
//												times = rowData;
//												System.out.print("'Model:App:Variable'");
//												for (int m = 0;m < times.length; m++) {
//													System.out.print((m!= 0?",":"")+(times[m]));
//												}
//												System.out.println();
//											}
//											String keyName = simulationContexts[j].getName()+":"+columnDescription.getName();
//											double[] accumData = mapObsTpData.get(keyName);
//											if(accumData == null) {
//												accumData = rowData;
//												mapObsTpData.put(keyName, accumData);
//											}
//											if(accumData.length != rowData.length) {
//												throw new Exception("Rows are different lengths");
//											}
//											for (int m = 0; m < rowData.length; m++) {
//												accumData[m]+= rowData[m];
//											}
////											System.out.println();
//										}
////										DataIdentifier[] dataIdentifiers = VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().getDataIdentifiers(null, vcSimulationDataIdentifier);
////										for (int l = 0; l < dataIdentifiers.length; l++) {
////											if(dataIdentifiers[l].getName().toLowerCase().endsWith("_count")){
////												VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().getODEData(dataIdentifiers[l]);
////											}
////										}
//									}
//									for(String s:mapObsTpData.keySet()) {
//										System.out.print("'"+/*bioModelInfos[i].getVersion().getName()+":"+simulationContexts[j].getName()+":"+*/s+"'");
//										double[] accumVals = mapObsTpData.get(s);
//										for (int k = 0; k < accumVals.length; k++) {
//											System.out.print((k!= 0?",":"")+(accumVals[k]/sims.length));
//										}
//										System.out.println();
//									}
//									System.out.println();
//								}
//							}
//						}
//					}
////	        	VCellClientTest.getVCellClient().getClientServerManager().getVCDataManager().
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    }

	}
	
//	public static class TimeSeriesJobSpec{
//	private String[] variableNames;
//	private int[] indices;
//	private double startTime;
//	private int step;
//	private double endTime;
//	private boolean calcSpaceStats = false;//Calc stats over space for each timepoint
//	private boolean calcTimeStats = false;
//	private int jobid;
//	private int cachekey;
//}

//	public static String getRawContent(URL url) throws Exception{
//		HttpURLConnection con = (HttpURLConnection)url.openConnection();
//		int responseCode = con.getResponseCode();
//		if(responseCode == HttpURLConnection.HTTP_OK) {
//			return streamToString(con.getInputStream());
//		}else {
//			throw new Exception("Expecting OK but got "+responseCode+" "+streamToString(con.getErrorStream()));
//		}
//	}
	public static String streamToString(InputStream stream) throws Exception{
		try(InputStream instrm = stream){				
		    StringBuilder textBuilder = new StringBuilder();
			Reader reader = new BufferedReader(new InputStreamReader(instrm, Charset.forName(StandardCharsets.UTF_8.name())));
			int c = 0;
			while ((c = reader.read()) != -1) {
			    textBuilder.append((char) c);
			}
			return textBuilder.toString();
		}
		
	}

	@XmlRootElement
	public static class IJTimeSeriesJobResults{
		@XmlElement
		private String[] variableNames;
		@XmlElement
		private int[] indices;//all variable share same indices
		@XmlElement
		private double[] times;//all vars share times
		@XmlElement
		private double[][][] data;//[varname][indices][times];
		public IJTimeSeriesJobResults() {
			
		}
		public IJTimeSeriesJobResults(String[] variableNames, int[] indices, double[] times, double[][][] data) {
			super();
			this.variableNames = variableNames;
			this.indices = indices;
			this.times = times;
			this.data = data;
		}
	}
	
	@XmlRootElement
	public static class IJTimeSeriesResults{
		@XmlElement
		private double[][] results;
		public IJTimeSeriesResults() {
			
		}
		public IJTimeSeriesResults(double[][] results) {
			super();
			this.results = results;
		}
	}
	
	@XmlRootElement
	public static class IJTimeSeriesJobSpec{
		@XmlElement
		private String[] variableNames;
		@XmlElement
		private int[] indices;
		@XmlAttribute
		private double startTime;
		@XmlAttribute
		private int step;
		@XmlAttribute
		private double endTime;
		@XmlAttribute
		private boolean calcSpaceStats;//Calc stats over space for each timepoint
		@XmlAttribute
		private boolean calcTimeStats;
		@XmlAttribute
		private int jobid;
		@XmlAttribute
		private int cachekey;
		public IJTimeSeriesJobSpec() {
			
		}
//		public IJTimeSeriesJobSpec(String[] variableNames, int[] indices, double startTime, int step, double endTime,
//				boolean calcSpaceStats, boolean calcTimeStats, int jobid, int cachekey) {
//			super();
//			this.variableNames = variableNames;
//			this.indices = indices;
//			this.startTime = startTime;
//			this.step = step;
//			this.endTime = endTime;
//			this.calcSpaceStats = calcSpaceStats;
//			this.calcTimeStats = calcTimeStats;
//			this.jobid = jobid;
//			this.cachekey = cachekey;
//		}
		
	}

	private static IJDataResponder getIJDataResponderFromCache(int cacheKey,Integer jobid) throws Exception{
		IJDataResponder ijDataResponder = null;
    	if(ijModelInfoCache != null) {
    		outerloop:
    		for(IJModelInfo ijModelInfo:ijModelInfoCache) {
    			if(ijModelInfo.contexts != null) {
    				for(IJContextInfo ijContextInfo:ijModelInfo.contexts) {
    					if(ijContextInfo.ijSimId != null) {
    						for(IJSimInfo ijSimInfo:ijContextInfo.ijSimId) {
    							if(ijSimInfo.cacheKey == cacheKey) {
    								if(ijSimInfo.quickrunKey != null) {
//    									simKey = new KeyValue(ijSimInfo.quickrunKey);
    									ijDataResponder = IJDataResponder.create(new KeyValue(ijSimInfo.quickrunKey), ResourceUtil.getLocalRootDir(), jobid);
    								}else if(VCellClientTest.getVCellClient() != null) {
    									ijDataResponder = IJDataResponder.create(VCellClientTest.getVCellClient(), new KeyValue(ijModelInfo.modelkey) , ijContextInfo.name, ijSimInfo.name, jobid);
//    									if(ijContextInfo.name == null) {//mathmodel
//    										KeyValue simKey = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getMathModel(new KeyValue(ijModelInfo.modelkey)).getSimulation(ijSimInfo.name).getSimulationVersion().getVersionKey();
//    									}else {
//    										KeyValue simKey = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModel(new KeyValue(ijModelInfo.modelkey)).getSimulationContext(ijContextInfo.name).getSimulation(ijSimInfo.name).getSimulationVersion().getVersionKey();
//    									}
    								}
    								break outerloop;
    							}
    						}
    					}
    				}
    			}
    		}
    	}
		return ijDataResponder;
	}
	
	private static enum IJGetTimeSeriesParams {varnames,indices,starttime,steptime,endtime,bspacestats,btimestats,jobid,cacheky}
	public static class ApiTimeSeriesHandler extends AbstractHandler{

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
	    	try {
//				List<NameValuePair> params = getParamsFromRequest(request);
				if ("POST".equalsIgnoreCase(request.getMethod())) 
				{
//					System.out.println(" \n\n Headers");
//				    Enumeration headerNames = request.getHeaderNames();
//				    while(headerNames.hasMoreElements()) {
//				        String headerName = (String)headerNames.nextElement();
//				        System.out.println(headerName + " = " + request.getHeader(headerName));
//				    }
//
//				    System.out.println("\n\nParameters");
//				    Enumeration params2 = request.getParameterNames();
//				    while(params2.hasMoreElements()){
//				        String paramName = (String)params2.nextElement();
//				        System.out.println(paramName + " = " + request.getParameter(paramName));
//				    }

//				    System.out.println("\n\n Row data");
				   String test = streamToString(request.getInputStream());
//				   System.out.println(test);
				    JAXBContext jContext = JAXBContext.newInstance(IJTimeSeriesJobSpec.class);
				    //creating the unmarshall object
				    Unmarshaller unmarshallerObj = jContext.createUnmarshaller();
				    //calling the unmarshall method
				    IJTimeSeriesJobSpec ijTimeSeriesJobSpec=(IJTimeSeriesJobSpec) unmarshallerObj.unmarshal(new ByteArrayInputStream(test.getBytes()));
				    IJDataResponder ijDataResponder = getIJDataResponderFromCache(ijTimeSeriesJobSpec.cachekey, ijTimeSeriesJobSpec.jobid);//IJDataResponder.create(getSimKeyFromCache(ijTimeSeriesJobSpec.cachekey), ResourceUtil.getLocalSimDir("temp"), ijTimeSeriesJobSpec.jobid);
					// Declare response encoding and types
					response.setContentType("text/html; charset=utf-8");

					// Declare response status code
					response.setStatus(HttpServletResponse.SC_OK);

					// Inform jetty that this request has now been handled
					baseRequest.setHandled(true);
					
					response.setContentType("text/xml; charset=utf-8");
//					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().print(createXML(ijDataResponder.getIJTimeSeriesData(ijTimeSeriesJobSpec)));
				}else {
					throw new Exception("POST expected");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ServletException(e);
			}


	        
////	    	public class TimeSeriesJobSpec implements java.io.Serializable{
////	    		
////	    		private String[] variableNames;
////	    		private int[][] indices;// int[varNameIndex][varValIndex]
////	    		private double startTime;
////	    		private int step;
////	    		private double endTime;
////	    		private boolean calcSpaceStats = false;//Calc stats over space for each timepoint
////	    		private boolean calcTimeStats = false;
////	    		private BitSet[] roi;
////	    		private VCDataJobID vcDataJobID;
////	    		private int[][] crossingMembraneIndices;//int[varNameIndex][MembraneIndex] non-null for volume data only
//
//	    	String[] variableNames = null;
//	    	int[][] indices = null;
//	    	Double starttime = null;
//	    	Integer step = null;
//	    	Double endtime = null;
//	    	Boolean calcSpaceStats = null;
//	    	Boolean calcTimeStats = null;
//	    	BitSet[] roi = null;
//	    	VCDataJobID vcDataJobID = null;
//	    	int[][] crossingMembraneIndices = null;
//	    	for(NameValuePair nameValuePair:params) {
//	    		if(nameValuePair.getName().equals(IJGetTimeSeriesParams.varnames.name())) {
//	    			StringTokenizer st = new StringTokenizer(str, ",");
//	    		}else if(nameValuePair.getName().equals(IJGetTimeSeriesParams.indices.name())) {
//	    			bOpen = Boolean.parseBoolean(nameValuePair.getValue());
//	    		}
////	    		else if(nameValuePair.getName().equals(IJListParams.cachekey.name())) {
////	    			cacheKey = Long.valueOf(nameValuePair.getValue());
////	    		}
//	    	}
	    	
//	    	if(ijDocType == null && bOpen == null/* && cacheKey == null*/) {
//	    		response.setContentType("text/html; charset=utf-8");
//	    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//	    		response.getWriter().println("<h1>Expecting '"+GETINFO_PARMS+"' in request</h1>");
//	    	}else {
//	    		
//	    	}
			
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
		@XmlAttribute()
		private String modelkey;
		@XmlElement(name="context")
		ArrayList<IJContextInfo> contexts;
		public IJModelInfo() {
			
		}
		public IJModelInfo(String name, Date date, IJDocType type, Boolean open,String user,KeyValue modelkey,ArrayList<IJContextInfo> contexts) {
			super();
			this.name = name;
			this.date = (date != null?date.toString():null);
			this.type = (type != null?type.name():null);
			this.open = open;
			this.user = user;
			this.modelkey = (modelkey != null?modelkey.toString():null);
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
		if(VCellClientTest.getVCellClient() == null) {
			return;
		}
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
		    				modelInfos.add(new IJModelInfo(documentWindowManager.getVCDocument().getName(), null, IJDocType.bm, true,documentWindowManager.getUser().getName(),null, ijContextInfos));
		    			}else if(documentWindowManager instanceof MathModelWindowManager) {
		    				MathModel mathModel = ((MathModelWindowManager)documentWindowManager).getMathModel();
		    				addSimToIJContextInfo(ijContextInfos, null,mathModel.getMathDescription().getMathType(),mathModel.getGeometry().getDimension(),mathModel.getGeometry().getName(),((MathModelWindowManager)documentWindowManager).getSimulationWorkspace().getSimulations());
		    				modelInfos.add(new IJModelInfo(documentWindowManager.getVCDocument().getName(), null, IJDocType.mm, true,documentWindowManager.getUser().getName(),null, ijContextInfos));
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
	private static HashMap<String, VCDocument> modelKeyMapToVCDocument = new HashMap<>();
	
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
								KeyValue parentModelKey = null;
								IJDocType docType = IJDocType.quick;
								boolean bOpenOnDesktop = false;
								if(VCellClientTest.getVCellClient() != null) {
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
						    					parentModelKey = documentWindowManager.getVCDocument().getVersion().getVersionKey();
						    					docType = (documentWindowManager.getVCDocument().getDocumentType() ==VCDocumentType.BIOMODEL_DOC?IJDocType.bm:IJDocType.mm);
						    					bOpenOnDesktop = true;
						    				}
						    			}
						    		}
								}
					    		if(bOpen == null || bOpen.booleanValue() == bOpenOnDesktop){
									ArrayList<IJContextInfo> contInfos = new ArrayList<>();
									ArrayList<IJSimInfo> ijsimfos= new ArrayList<>();
									ijsimfos.add(new IJSimInfo(quickrunKey, parentSimName));
									contInfos.add(new IJContextInfo(parentContextName, parentMathType, parentGeomDim, parentGeomName, ijsimfos));
									modelInfos.add(new IJModelInfo(parentModelName, parentDate, docType, (docType != IJDocType.quick?true:false), parentUser,parentModelKey,contInfos));
					    		}
							}
						}
			        }
		        	
		        }
		        if(VCellClientTest.getVCellClient() != null) {
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
				        	modelInfos.add(new IJModelInfo(bioModelInfo.getVersion().getName(), bioModelInfo.getVersion().getDate(), IJDocType.bm, openKeys.contains(bioModelInfo.getVersion().getVersionKey()),bioModelInfo.getVersion().getOwner().getName(),bioModelInfo.getVersion().getVersionKey(), ijContextInfos));
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
				        	modelInfos.add(new IJModelInfo(mathModelInfo.getVersion().getName(), mathModelInfo.getVersion().getDate(), IJDocType.mm, openKeys.contains(mathModelInfo.getVersion().getVersionKey()), mathModelInfo.getVersion().getOwner().getName(),mathModelInfo.getVersion().getVersionKey(),ijContextInfos));
				        }
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
		@XmlAttribute
		private String varname;
		@XmlAttribute
		private Double timepoint;
		@XmlElement
		private BasicStackDimensions stackInfo;
		@XmlInlineBinaryData
		private byte[] data;//double[] converted to byte[]
		public IJData() {
			
		}
		public IJData(BasicStackDimensions stackInfo, byte[] data,String varname,Double timepoint) {
			super();
			this.stackInfo = stackInfo;
			this.data = data;
			this.varname = varname;
			this.timepoint = timepoint;
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
		private Boolean bFunction;
		public IJVarInfo() {
			
		}
		public IJVarInfo(String name, String displayName, VariableType variableType, Domain domain, Boolean bFunction) {
			this.name = name;
			this.displayName = displayName;
			this.variableType = (variableType != null?variableType.getTypeName():null);
			this.domain = (domain != null?domain.getName():null);
			this.bFunction = bFunction;
		}
		
	}
	
	@XmlRootElement
	private static class IJVarInfos {
		@XmlAttribute
		private String simname;
		@XmlAttribute
		private Long cachekey;
		@XmlAttribute
		private Integer scancount;
		@XmlElement
		private String times;
		@XmlElement
		private ArrayList<IJVarInfo> ijVarInfo;
		public IJVarInfos() {
			
		}
		public IJVarInfos(ArrayList<IJVarInfo> ijVarInfos,String simname,Long cachekey,double[] times,Integer scancount) {
			super();
			this.simname = simname;
			this.cachekey = cachekey;
			this.ijVarInfo = ijVarInfos;
			if(times != null && times.length > 0) {
				StringBuffer sb =new StringBuffer();
				for(int i=0;i<times.length;i++) {
					sb.append((i>0?",":"")+i+"='"+times[i]+"'");
				}
				this.times=sb.toString();
			}
			this.scancount = scancount;
		}
		
	}
	
//	@XmlRootElement
//	private static class IJSimJobIDInfo {
//		@XmlAttribute
//		private String simname;
//		@XmlAttribute
//		private int scancount;
//		@XmlAttribute
//		private String modelkey;
//		@XmlAttribute
//		private String simcontextkey;
//		@XmlAttribute
//		private String simkey;
//		public IJSimJobIDInfo() {
//			
//		}
//		public IJSimJobIDInfo(String simname, int scancount, KeyValue modelkey, KeyValue simcontextkey, KeyValue simkey) {
//			super();
//			this.simname = simname;
//			this.scancount = scancount;
//			this.modelkey = modelkey.toString();
//			this.simcontextkey = simcontextkey.toString();
//			this.simkey = simkey.toString();
//		}
//		
//	}
	
	private static byte[] convertDoubleToBytes(double[] doubles) {
		byte[] byteDoubles = new byte[doubles.length*Double.BYTES];
		ByteBuffer bb = ByteBuffer.wrap(byteDoubles);
		for(int i=0;i<doubles.length;i++) {
			bb.putDouble(doubles[i]);
		}
		return byteDoubles;
	}
	
	private static class IJDataResponder{
		private VCDataManager vcDataManager;
		private DataSetControllerImpl dataSetControllerImpl;
		private OutputContext outputContext;
		private VCSimulationDataIdentifier vcSimulationDataIdentifier;
		private Integer jobCount;
		private User user;
		private IJDataResponder(OutputContext outputContext,VCSimulationDataIdentifier vcSimulationDataIdentifier,Integer jobcount,User user) {
			this.vcSimulationDataIdentifier = vcSimulationDataIdentifier;
			this.outputContext = outputContext;
			this.jobCount = jobcount;
			this.user = user;
		}
		private IJDataResponder(VCDataManager vcDataManager,OutputContext outputContext,VCSimulationDataIdentifier vcSimulationDataIdentifier,Integer jobcount,User user) {//saved to database
			this(outputContext,  vcSimulationDataIdentifier,jobcount,user);
			this.vcDataManager = vcDataManager;
		}
		private IJDataResponder(DataSetControllerImpl dataSetControllerImpl,OutputContext outputContext,VCSimulationDataIdentifier vcSimulationDataIdentifier,Integer jobCount,User user) {//quickrun
			this(outputContext, vcSimulationDataIdentifier,jobCount,user);
			this.dataSetControllerImpl = dataSetControllerImpl;
		}
		public static IJDataResponder create(VCellClient vCellClient,KeyValue modelKey,String simContextName,String simName,Integer jobIndex) throws Exception{
			SimulationOwner simOwner = null;
			VCDocument vcDocument = modelKeyMapToVCDocument.get(modelKey.toString());
			if(vcDocument == null) {
				vcDocument = (simContextName != null?vCellClient.getRequestManager().getDocumentManager().getBioModel(modelKey):vCellClient.getRequestManager().getDocumentManager().getMathModel(modelKey));
				modelKeyMapToVCDocument.clear();
				modelKeyMapToVCDocument.put(modelKey.toString(), vcDocument);
			}
			simOwner = (vcDocument instanceof BioModel?((BioModel)vcDocument).getSimulationContext(simContextName):((MathModel)vcDocument));
			Simulation[] simulations = simOwner.getSimulations();
			for(Simulation sim:simulations) {
				if(sim.getName().equals(simName)) {
					VCSimulationIdentifier vcSimulationIdentifier = sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
					VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, (jobIndex!=null?jobIndex:0));
					OutputContext outputContext = new OutputContext(simOwner.getOutputFunctionContext().getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
					VCDataManager vcDataManager = new VCDataManager(vCellClient.getClientServerManager());
					return new IJDataResponder(vcDataManager, outputContext, vcSimulationDataIdentifier,sim.getScanCount(),sim.getSimulationVersion().getOwner());
				}
			}
			throw new Exception("IJREsponder: simulation name '"+simName+"' not found");
		}
		public static IJDataResponder create(KeyValue quickrunKey,File simDataDir,Integer jobIndex) throws Exception{
			VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(quickrunKey, User.tempUser);
			VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, (jobIndex!=null?jobIndex:0));
//			SimulationData simulationData = new SimulationData(vcSimulationDataIdentifier, simDataDir, simDataDir, null);
			DataSetControllerImpl dsci = new DataSetControllerImpl(null, simDataDir,null);
			return new IJDataResponder(dsci, null, vcSimulationDataIdentifier,1,User.tempUser);
		}
		public IJData getIJData(String varname,Double timepoint) throws Exception{
			CartesianMesh mesh = (dataSetControllerImpl != null?dataSetControllerImpl.getMesh(vcSimulationDataIdentifier):vcDataManager.getMesh(vcSimulationDataIdentifier));
			BasicStackDimensions basicStackDimensions = new BasicStackDimensions(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ(), 1, 1);
			SimDataBlock simDataBlock = (dataSetControllerImpl != null?dataSetControllerImpl.getSimDataBlock(outputContext, vcSimulationDataIdentifier, varname,timepoint):vcDataManager.getSimDataBlock(outputContext, vcSimulationDataIdentifier, varname, timepoint));
			return new IJData(basicStackDimensions, convertDoubleToBytes(simDataBlock.getData()),varname,timepoint);
		}
		public IJData getOdeIJData(String varName) throws Exception{
			ODESolverResultSet odeSolverResultSet = null;
			if(vcDataManager != null) {
				ODESimData odeSimData = vcDataManager.getODEData(vcSimulationDataIdentifier);
				odeSolverResultSet = odeSimData;
			}else {
				ODEDataBlock odeDataBlock = dataSetControllerImpl.getODEDataBlock(vcSimulationDataIdentifier);
				odeSolverResultSet = odeDataBlock.getODESimData();
			}
			double[] doubles = null;
			double[] times = null;
			for (int i = 0; i < odeSolverResultSet.getRowCount(); i++) {
				ColumnDescription columnDescription = odeSolverResultSet.getColumnDescriptions(i);
				if(columnDescription.getName().equals(varName)) {
					doubles = odeSolverResultSet.extractColumn(i);
					if (times != null) {
						break;
					}
				}else if(columnDescription.getName().equals(ReservedVariable.TIME.getName())) {
					times = odeSolverResultSet.extractColumn(i);
					if (doubles != null) {
						break;
					}
				}
			}
			BasicStackDimensions basicStackDimensions = new BasicStackDimensions(1, 1, 1, 1, doubles.length);
			IJData ijData = new IJData(basicStackDimensions, convertDoubleToBytes(doubles),varName,null);
			return ijData;
		}
		public IJTimeSeriesJobResults getIJTimeSeriesData(IJTimeSeriesJobSpec ijTimeSeriesJobSpec) throws Exception{
			int[][] copiedIndices = new int[ijTimeSeriesJobSpec.variableNames.length][];
			for (int i = 0; i < copiedIndices.length; i++) {
				copiedIndices[i] = ijTimeSeriesJobSpec.indices;
			}
			TimeSeriesJobSpec timeSeriesJobSpec = new TimeSeriesJobSpec(ijTimeSeriesJobSpec.variableNames, copiedIndices,null,
						ijTimeSeriesJobSpec.startTime, ijTimeSeriesJobSpec.step, ijTimeSeriesJobSpec.endTime, /*ijTimeSeriesJobSpec.calcTimeStats, ijTimeSeriesJobSpec.calcSpaceStats,*/ new VCDataJobID(vcSimulationDataIdentifier.getJobIndex(), user, false));
			TSJobResultsNoStats timeSeriesJobResults = (TSJobResultsNoStats)(dataSetControllerImpl != null?dataSetControllerImpl.getTimeSeriesValues(outputContext, vcSimulationDataIdentifier, timeSeriesJobSpec):vcDataManager.getTimeSeriesValues(outputContext, vcSimulationDataIdentifier, timeSeriesJobSpec));
			double[][][] newData = new double[timeSeriesJobResults.getVariableNames().length][][];
			for (int i = 0; i < newData.length; i++) {
				newData[i] = timeSeriesJobResults.getTimesAndValuesForVariable(timeSeriesJobResults.getVariableNames()[i]);
			}
			return new IJTimeSeriesJobResults(timeSeriesJobResults.getVariableNames(), timeSeriesJobResults.getIndices()[0], timeSeriesJobResults.getTimes(), newData);
		}
		public IJVarInfos getIJVarInfos(String simName,Long cachekey,Integer scancount) throws Exception{
			DataIdentifier[] dataIdentifiers = (dataSetControllerImpl != null?dataSetControllerImpl.getDataIdentifiers(outputContext, vcSimulationDataIdentifier):vcDataManager.getDataIdentifiers(outputContext, vcSimulationDataIdentifier));
			ArrayList<IJVarInfo> ijVarInfos = new ArrayList<>();
			for(DataIdentifier did:dataIdentifiers) {
				ijVarInfos.add(new IJVarInfo(did.getName(), did.getDisplayName(), did.getVariableType(), did.getDomain(), did.isFunction()));
			}
			return new IJVarInfos(ijVarInfos,simName,cachekey,(dataSetControllerImpl != null?dataSetControllerImpl.getDataSetTimes(vcSimulationDataIdentifier):vcDataManager.getDataSetTimes(vcSimulationDataIdentifier)),scancount);
		}
		public void respondData(HttpServletResponse response,String varname,Double timepoint,boolean bSpatial) throws Exception{
			if(bSpatial) {
				respond(response, createXML(getIJData(varname,timepoint)));
			}else {
				respond(response, createXML(getOdeIJData(varname)));
			}
		}
		public void respondIdentifiers(HttpServletResponse response,String simName,Long cachekey) throws Exception{
    		respond(response, createXML(getIJVarInfos(simName, cachekey, jobCount)));
		}
		private void respond(HttpServletResponse response,String xml) throws IOException{
    		response.setContentType("text/xml; charset=utf-8");
    		response.setStatus(HttpServletResponse.SC_OK);
    		response.getWriter().write(xml);
		}
	}
	
	private static enum IJGetDataParams {cachekey,varname,timepoint,jobid}
	public static class ApiGetDataHandler extends AbstractHandler{
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
	    	System.out.println(target+"\n"+baseRequest.getQueryString());
	    	List<NameValuePair> params = getParamsFromRequest(request);
	    	Long cacheKey = null;
	    	String varname = null;
	    	Double timepoint = null;
	    	Integer jobid = null;
	    	for(NameValuePair nameValuePair:params) {
	    		if(nameValuePair.getName().equals(IJGetDataParams.cachekey.name())) {
	    			cacheKey = Long.valueOf(nameValuePair.getValue());
	    		}else if(nameValuePair.getName().equals(IJGetDataParams.varname.name())) {
	    			varname = nameValuePair.getValue();
	    		}else if(nameValuePair.getName().equals(IJGetDataParams.timepoint.name())) {
	    			timepoint = Double.parseDouble(nameValuePair.getValue());
	    		}else if(nameValuePair.getName().equals(IJGetDataParams.jobid.name())) {
	    			jobid = Integer.parseInt(nameValuePair.getValue());
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
	        									try {
	        										IJDataResponder ijDataResponder = null;
			        								if(ijSimInfo.quickrunKey != null) {//quickrun sim
				        							        ArrayList<File> dirs = new ArrayList<>();
				        							        dirs.add(ResourceUtil.getLocalRootDir());
				        							        fileloop:
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
//				        													User user = new User("quick", new KeyValue("0"));
//				        													VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(quickrunKey), null);
//				        													VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, (jobid!=null?jobid:0));//quickrun always job 0
//				        													SimulationData simulationData = new SimulationData(vcSimulationDataIdentifier, file.getParentFile(), file.getParentFile(), null);
				        													ijDataResponder = IJDataResponder.create(new KeyValue(quickrunKey),file.getParentFile().getParentFile(), jobid);//new IJDataResponder(simulationData, null, vcSimulationDataIdentifier);
//				        													if(varname!=null && timepoint != null) {
//				        														ijDataResponder.respondData(response,varname, timepoint);
//				        													}else {
//				        														ijDataResponder.respondIdentifiers(response, ijSimInfo.name, cacheKey);
//				        													}
//				    			        						    		bFound = true;
//				    			        						    		break outerloop;
				        													break fileloop;
				        												}	
				        											}
				        							        	}
				        							        }
				        								}else {//saved sim
//				        									RequestManager requestManager = VCellClientTest.getVCellClient().getRequestManager();
				        									IJDocType ijDocType = IJDocType.valueOf(ijModelInfo.type);
				        									if(ijDocType != IJDocType.bm && ijDocType != IJDocType.mm) {
				        							    		response.setContentType("text/html; charset=utf-8");
				        							    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				        							    		response.getWriter().println("<h1>\"Assumed IJ request for saved bio/math model, not expecting IJDocType='\"+ijDocType.name()+\"'\"</h1>");
				        							    		baseRequest.setHandled(true);
				        							    		return;
//				        										throw new ServletException("Assumed IJ request for saved bio/math model, not expecting IJDocType='"+ijDocType.name()+"'");
				        									}
				        									ijDataResponder = IJDataResponder.create(VCellClientTest.getVCellClient(), new KeyValue(ijModelInfo.modelkey), ijContextInfo.name/*null for mathmodel*/, ijSimInfo.name, jobid);
//				        									if(ijDocType == IJDocType.bm) {
////				        										BioModel biomodel = requestManager.getDocumentManager().getBioModel(new KeyValue(ijModelInfo.modelkey));
////				        										SimulationContext simulationContext = biomodel.getSimulationContext(ijContextInfo.name);
////				        										Simulation simulation = simulationContext.getSimulation(ijSimInfo.name);
////				        										VCSimulationIdentifier vcSimulationIdentifier = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
////				        										VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, (jobid==null?0:jobid));
////				        										OutputContext outputContext = new OutputContext(simulationContext.getOutputFunctionContext().getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
////				        										VCDataManager vcDataManager = new VCDataManager(VCellClientTest.getVCellClient().getClientServerManager());
//																ijDataResponder = IJDataResponder.create(VCellClientTest.getVCellClient(), new KeyValue(ijModelInfo.modelkey), ijContextInfo.name, ijSimInfo.name, jobid);
//																//new IJDataResponder(vcDataManager, outputContext,/* varname, timepoint,*/vcSimulationDataIdentifier);
////				        										if(varname!=null && timepoint != null){
////				        											ijDataResponder.respondData(response,varname, timepoint);
////				        										}else {
////				        											ijDataResponder.respondIdentifiers(response, ijSimInfo.name, cacheKey);
////				        										}
////	    			        						    		bFound = true;
////	    			        						    		break outerloop;
//				        									}else if(ijDocType == IJDocType.mm) {
//																ijDataResponder = IJDataResponder.create(VCellClientTest.getVCellClient(), new KeyValue(ijModelInfo.modelkey), ijContextInfo.name, ijSimInfo.name, jobid);				        										
//				        									}else {
//				        										throw new ServletException("Assumed IJ request for saved bio/math model, not expecting IJDocType='"+ijDocType.name()+"'");
//				        									}
				        								}
			        								if(ijDataResponder != null) {
		        										if(varname!=null/* && timepoint != null*/){
		        											ijDataResponder.respondData(response,varname, timepoint,(ijContextInfo.gomeDim > 0));
		        										}else {
		        											ijDataResponder.respondIdentifiers(response, ijSimInfo.name, cacheKey);
		        										}
		        										bFound = true;
		        										break outerloop;			        									
			        								}
	        									}catch(Exception e) {
	        										throw new ServletException(e);
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

	public static class ApiHelloHandler extends AbstractHandler{

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			// TODO Auto-generated method stub
    		response.setContentType("text/html; charset=utf-8");
    		response.setStatus(HttpServletResponse.SC_OK);
    		response.getWriter().write("VCellApi");
    		baseRequest.setHandled(true);

		}
		
	}
    public static void startService(Integer onlyThisPort) throws Exception
    {

        Server server = null;
        for(int i=8000;i<=8100;i++) {
            ContextHandler contextHello = new ContextHandler("/"+ApiEnum.hello.name()+"/");
            contextHello.setHandler(new ApiHelloHandler());

            ContextHandler contextRoot = new ContextHandler("/");
            contextRoot.setHandler(new ApiInfoHandler());
            
            ContextHandler context = new ContextHandler("/"+ApiEnum.getinfo.name()+"/");
            context.setHandler(new ApiListHandler());
            
            ContextHandler contextData = new ContextHandler("/"+ApiEnum.getdata.name()+"/");
            contextData.setHandler(new ApiGetDataHandler());

            ContextHandler contextTimeSeries = new ContextHandler("/"+ApiEnum.gettimeseries.name()+"/");
            contextTimeSeries.setHandler(new ApiTimeSeriesHandler());

            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[] { contextHello,contextRoot,context,contextData ,contextTimeSeries});

            try {
				server = new Server((onlyThisPort == null?i:onlyThisPort));
				server.setHandler(contexts);
				server.start();
		        System.out.println(server.getURI());
				break;
			} catch (BindException e) {
				if(onlyThisPort != null) {
					e.printStackTrace();
					throw e;
				}
				System.out.println("VCellIJ service: port "+i+" in use, continuing search...");
			}
        }
    }
//	@Override
//	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
//			throws IOException, ServletException {
//		// TODO Auto-generated method stub
//		
//	}

}
