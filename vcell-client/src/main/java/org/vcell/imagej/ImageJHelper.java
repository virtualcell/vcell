package org.vcell.imagej;

import java.awt.Component;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
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
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
import org.jdom.Namespace;
import org.sbml.jsbml.ext.spatial.SampledVolume;
import org.vcell.imagej.ImageJHelper.ApiSolverHandler.IJGeom;
import org.vcell.sbml.SBMLUtils;
import org.vcell.util.BeanUtils;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.Range;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TSJobResultsSpaceStats;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vmicro.workflow.data.ExternalDataInfo;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cbit.image.ImageException;
import cbit.image.ThumbnailImage;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.TranslationLogger;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.desktop.biomodel.MathematicsPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.test.VCellClientTest;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.nrrd.NrrdWriter;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactory;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Model.Owner;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataInfoProvider;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.ServerPDEDataContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.simdata.gui.PDEDataContextPanel.RecodeDataForDomainInfo;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TempSimulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.util.ColumnDescription;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlHelper;

public class ImageJHelper {
//	public static final String USER_ABORT = "userAbort";
//	public enum ExternalCommunicator 
//	{
//	  IMAGEJ(5000),
//	  BLENDER(5001);
//
//	  private final int port; 
//	  private ExternalCommunicator(final int port) { this.port = port; }
//	  public int getPort() { return this.port;}
//	}	
//	public static class ImageJConnection {
//		public ServerSocket serverSocket;
//		public Socket socket;
//		public DataInputStream dis;
//		public DataOutputStream dos;
//		public ExternalCommunicator externalCommunicator;
//		private BufferedInputStream bis;
//		public ImageJConnection(ExternalCommunicator externalCommunicator) throws Exception{
//			serverSocket = new ServerSocket(externalCommunicator.getPort());
//			this.externalCommunicator = externalCommunicator;
//		}
//		public void openConnection(ImageJHelper.VCellImageJCommands command,String descr) throws Exception{
//			socket = serverSocket.accept();
//			serverSocket.close();
//			bis = new BufferedInputStream(socket.getInputStream());
//			dis = new DataInputStream(bis);
//			dos = new DataOutputStream(socket.getOutputStream());
//			dos.writeUTF(command.name());
//			dos.writeUTF(descr);
//			String startMessage = null;
//			switch(externalCommunicator){
//				case IMAGEJ:
//					startMessage = dis.readUTF();
//					break;
//				case BLENDER:
//					startMessage = readLine();
//					break;
//				default:
//					throw new IllegalArgumentException("Unexpected external program "+externalCommunicator.name());
//			}
//			if(startMessage == null){
//				throw new Exception(externalCommunicator.name()+" unexpectedly stopped communicating");
//			}
//			if(startMessage.equals(USER_ABORT)){
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//		}
//		public Exception[] closeConnection(){
//			ArrayList<Exception> errors = new ArrayList<>();
//			try{dis.close();}catch(Exception e){errors.add(e);}
//			try{bis.close();}catch(Exception e){errors.add(e);}
//			try{dos.close();}catch(Exception e){errors.add(e);}
//			try{socket.close();}catch(Exception e){errors.add(e);}
//			try{serverSocket.close();}catch(Exception e){errors.add(e);}
//			if(errors.size() > 0){
//				return errors.toArray(new Exception[0]);
//			}
//			return null;
//		}
//		public String readLine() throws IOException{
//			return ImageJHelper.readLine(bis);
//		}
//	}
//	
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
//	private static class HyperStackHelper extends BasicStackDimensions{
//		public String dataClass;
//		public boolean hasOverlays;
//		public Extent extent;
//		public int[] domainSubvolumeIDs;//this will be non-null if this is a domain mask
//		public double[] timePoints;
//		public String[] channelDescriptions;
//		public int[] channelSubvolIDs;
//		public int[] colormap;
//		public HyperStackHelper(BasicStackDimensions basicStackDimensions,Extent extent,boolean hasOverlays,String dataClass,int[] domainSubvolumeIDs,int[] channelSubvolIDs,double[] timePoints,String[] channelDescriptions,int[] colormap) {
//			super(basicStackDimensions.xsize,basicStackDimensions.ysize,basicStackDimensions.zsize,basicStackDimensions.csize,basicStackDimensions.tsize);
//			this.extent = extent;
//			this.dataClass = dataClass;
//			this.hasOverlays = hasOverlays;
//			this.domainSubvolumeIDs = domainSubvolumeIDs;
//			this.channelSubvolIDs = channelSubvolIDs;
//			this.timePoints = timePoints;
//			this.channelDescriptions = channelDescriptions;
//			this.colormap = colormap;
//		}
//		public void writeInfo(DataOutputStream dos) throws Exception{
//			dos.writeUTF(dataClass);
//			for (int i = 0; i < getSizesInVCellPluginOrder().length; i++) {
//				dos.writeInt(getSizesInVCellPluginOrder()[i]);
//			}
//			dos.writeDouble(extent.getX());
//			dos.writeDouble(extent.getY());
//			dos.writeDouble(extent.getZ());
//			dos.writeBoolean(hasOverlays);
//			//write colormap
//			dos.writeInt((colormap != null?colormap.length:0));
//			if(colormap != null){
//				for (int i = 0; i < colormap.length; i++) {
//					dos.writeInt(colormap[i]);//argb
//				}
//			}
//			//write subvolumeIDs
//			dos.writeInt((domainSubvolumeIDs==null?0:domainSubvolumeIDs.length));
//			if(domainSubvolumeIDs != null){
//				for (int j = 0; j < domainSubvolumeIDs.length; j++) {
//					dos.writeInt(domainSubvolumeIDs[j]);
//				}
//			}
//			//write channel subvolumeIDs
//			dos.writeInt((channelSubvolIDs==null?0:channelSubvolIDs.length));
//			if(channelSubvolIDs != null){
//				for (int j = 0; j < channelSubvolIDs.length; j++) {
//					dos.writeInt(channelSubvolIDs[j]);
//				}
//			}
//			//timepoints
//			dos.writeInt((timePoints==null?0:timePoints.length));
//			if(timePoints != null){
//				for (int i = 0; i < timePoints.length; i++) {
//					dos.writeDouble(timePoints[i]);
//				}
//			}
//			//channel descriptions
//			dos.writeInt((channelDescriptions==null?0:channelDescriptions.length));
//			if(channelDescriptions != null){
//				for (int i = 0; i < channelDescriptions.length; i++) {
//					dos.writeUTF(channelDescriptions[i]);
//				}
//			}
//		}
//	}
//	public static enum VCellImageJCommands {vcellWantImage,vcellWantInfo,vcellSendImage,vcellSendInfo,vcellSendDomains,vcellWantSurface};
//
//	private static enum doneFlags {working,cancelled,finished};
//
//	public static File vcellWantSurface(ClientTaskStatusSupport clientTaskStatusSupport,String description) throws Exception{
//		return doCancellableConnection(ExternalCommunicator.BLENDER, VCellImageJCommands.vcellWantSurface, clientTaskStatusSupport, description);
//	}
//	private static File vcellWantSurface0(ClientTaskStatusSupport clientTaskStatusSupport,String description,ImageJConnection imageJConnection) throws Exception{
//		String sizeStr = imageJConnection.readLine();
//		int fileSize = Integer.parseInt(sizeStr);
//		byte[] bytes = new byte[fileSize];
//		int numread = 0;
//		while(numread >= 0 && numread != bytes.length){
//			numread+= imageJConnection.dis.read(bytes, numread, bytes.length-numread);
//		}
//		File newFile = File.createTempFile("vcellBlener", ".stl");
//		FileUtils.writeByteArrayToFile(bytes, newFile);
//		return newFile;
//	}
//	
//	private static void startCancelThread(ClientTaskStatusSupport clientTaskStatusSupport,ImageJConnection[] imageJConnectionArr,doneFlags[] bDone){
//		//check in separate thread for possible cancel while this task is blocked waiting for serversocket contact with ImageJ
//		if(clientTaskStatusSupport != null){
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					while(bDone[0] == doneFlags.working){
//						if(clientTaskStatusSupport.isInterrupted()){
//							bDone[0] = doneFlags.cancelled;
//							try{if(imageJConnectionArr[0] != null){imageJConnectionArr[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
//							return;
//						}
//						try{Thread.sleep(250);}catch(Exception e){e.printStackTrace();}
//					}
//				}
//			}).start();
//		}
//	}
//	private static File doCancellableConnection(ExternalCommunicator externalCommunicator,VCellImageJCommands command,ClientTaskStatusSupport clientTaskStatusSupport,String description) throws Exception{
//		ImageJConnection[] imageJConnectionArr = new ImageJConnection[1];
//		doneFlags[] bDone = new doneFlags[] {doneFlags.working};
//		try{
//			imageJConnectionArr[0] = new ImageJConnection(externalCommunicator);
//			startCancelThread(clientTaskStatusSupport, imageJConnectionArr, bDone);
//			imageJConnectionArr[0].openConnection(command,description);
//			switch(command){
//				case vcellWantSurface:
//					return vcellWantSurface0(clientTaskStatusSupport, description, imageJConnectionArr[0]);
////					break;
//				case vcellWantImage:
//					return vcellWantImage0(clientTaskStatusSupport, description,imageJConnectionArr[0]);
////					break;
//				default:
//					throw new IllegalArgumentException("Unexpected command "+command.name());	
//			}
//		}catch(Exception e){
//			if(bDone[0] == doneFlags.cancelled){
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//			throw e;
//		}finally{
//			bDone[0] = doneFlags.finished;
//			try{if(imageJConnectionArr[0] != null){imageJConnectionArr[0].closeConnection();}}catch(Exception e){e.printStackTrace();}	
//		}
//	}
//	public static File vcellWantImage(ClientTaskStatusSupport clientTaskStatusSupport,String description) throws Exception{
//		return doCancellableConnection(ExternalCommunicator.IMAGEJ, VCellImageJCommands.vcellWantImage, clientTaskStatusSupport, description);
//	}
//	private static File vcellWantImage0(ClientTaskStatusSupport clientTaskStatusSupport,String description,ImageJConnection imageJConnection) throws Exception{
//		if(clientTaskStatusSupport != null){
//			clientTaskStatusSupport.setMessage("Waiting for ImageJ to send image...");
//		}
//		//Create nrrd file from socket input
//		imageJConnection.dis.readInt();//integer (dimensions)
//		//get size of the standard 5 dimensions in this order (width, height, nChannels, nSlices, nFrames)
//		int xsize = imageJConnection.dis.readInt();
//		int ysize = imageJConnection.dis.readInt();
//		imageJConnection.dis.readInt();
//		imageJConnection.dis.readInt();
//		imageJConnection.dis.readInt();
//		//read data
//		int slices = imageJConnection.dis.readInt();
//		byte[] data = new byte[slices*xsize*ysize*Double.BYTES];
//		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//		for (int i = 0; i < slices; i++) {
//			if(clientTaskStatusSupport != null){
//				clientTaskStatusSupport.setMessage("Reading Fiji/ImageJ slice "+(i+1)+" of "+slices+"...");
//				if(clientTaskStatusSupport.isInterrupted()){
//					throw UserCancelException.CANCEL_GENERIC;
//				}
//			}
//			String arraytype = imageJConnection.dis.readUTF();
//			int arrLength = imageJConnection.dis.readInt();
//			if(arraytype.equals(byte[].class.getName())){//byte array
//				byte[] bytes = new byte[arrLength];
//				int numread = 0;
//				while(numread != bytes.length){
//					numread+= imageJConnection.dis.read(bytes, numread, bytes.length-numread);
//				}
//				for (int j = 0; j < bytes.length; j++) {
//					byteBuffer.putDouble((double)Byte.toUnsignedInt(bytes[j]));
//				}
//				System.out.println("bytesRead="+numread);
//			}else if(arraytype.equals(short[].class.getName())){// short array
//				short[] shorts = new short[arrLength];
//				for (int j = 0; j < shorts.length; j++) {
//					shorts[j] = imageJConnection.dis.readShort();
//					byteBuffer.putDouble((double)Short.toUnsignedInt(shorts[j]));
//				}
//				System.out.println("shortsRead="+shorts.length);
//			}
//		}
//
//		if(clientTaskStatusSupport != null){
//			clientTaskStatusSupport.setMessage("Converting slices to file...");
//			if(clientTaskStatusSupport.isInterrupted()){
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//		}
//		NrrdInfo nrrdInfo = NrrdInfo.createBasicNrrdInfo(5, new int[] {xsize,ysize,slices,1,1}, "double", "raw", NrrdInfo.createXYZTVMap());
//		FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();
//		nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
//		fileDataContainerManager.append(nrrdInfo.getDataFileID(), byteBuffer.array());
//		NrrdWriter.writeNRRD(nrrdInfo, fileDataContainerManager);
//		File tempFile = File.createTempFile("fijinrrd", ".nrrd");
//		fileDataContainerManager.writeAndFlush(nrrdInfo.getHeaderFileID(), new FileOutputStream(tempFile));
//		if(clientTaskStatusSupport != null){
//			clientTaskStatusSupport.setMessage("Finished ImageJ data conversion...");
//			if(clientTaskStatusSupport.isInterrupted()){
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//		}
//		return tempFile;
//	}
//
//	private static String extract(String line) throws IOException{
//		StringTokenizer st = new StringTokenizer(line, ":");
//		st.nextToken();
//		return st.nextToken().trim();
//	}
//	private static BasicStackDimensions extractArr(String line) throws IOException{
//		StringTokenizer st = new StringTokenizer(line, ":");
//		st.nextToken();
//		String arr = st.nextToken();
//		StringTokenizer st2 = new StringTokenizer(arr, " ");
//		ArrayList<Integer> resultArr = new ArrayList<>();
//		while(st2.hasMoreTokens()){
//			resultArr.add(Integer.parseInt(st2.nextToken().trim()));
//		}
//		int[] result = new int[resultArr.size()];//x,y,z,t,v see RasterExporter.createSingleFullNrrdInfo(...)
//		for (int i = 0; i < result.length; i++) {
//			result[i] = resultArr.get(i);
//		}
//		return new BasicStackDimensions((result.length>=1?result[0]:1), (result.length>=2?result[1]:1), (result.length>=3?result[2]:1), (result.length>=5?result[4]:1), (result.length>=4?result[3]:1));
//	}
//	public static class ListenAndCancel implements ProgressDialogListener {
//		private Runnable cancelMethod;
//		public void setCancelMethod(Runnable runnable) {
//			cancelMethod = runnable;
//		}
//		@Override
//		public void cancelButton_actionPerformed(EventObject newEvent) {
//			if(cancelMethod != null){
//				cancelMethod.run();
//			}
//		}
//	};
//
//	public static void sendVolumeDomain0(ImageJConnection imageJConnection,CartesianMesh mesh,SimulationModelInfo simulationModelInfo,String description) throws Exception{
//		Hashtable<Integer, BitSet> subVolMapMask = new Hashtable<>();
//		for (int i = 0; i < mesh.getNumVolumeElements(); i++) {
//			int subvolume = mesh.getSubVolumeFromVolumeIndex(i);
//			if(subvolume > 255){
//				throw new Exception("Error ImageJHelper.sendVolumeDomain(...) subvolume > 255 not implemented");
//			}
//			if(!subVolMapMask.containsKey(subvolume)){
//				subVolMapMask.put(subvolume, new BitSet(mesh.getNumVolumeElements()));
//			}
//			subVolMapMask.get(subvolume).set(i);
//		}
//		ArrayList<String> channelDescriptions = new ArrayList<>();
//		if(simulationModelInfo != null){
//			for(Integer subvolID:subVolMapMask.keySet()){
//				channelDescriptions.add(simulationModelInfo.getVolumeNameGeometry(subvolID)+":"+simulationModelInfo.getVolumeNamePhysiology(subvolID));
//			}
//		}
//		int[] subvolumeIDs = new int[subVolMapMask.size()];
//		Enumeration<Integer> subvolid = subVolMapMask.keys();
//		int cnt = 0;
//		while(subvolid.hasMoreElements()){
//			subvolumeIDs[cnt] = subvolid.nextElement();
//			cnt++;
//		}
//		sendData0(imageJConnection, new HyperStackHelper(new BasicStackDimensions(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ(), subVolMapMask.size(), 1),mesh.getExtent(),false,Byte.class.getSimpleName(),subvolumeIDs,null,null,(channelDescriptions.size()==0?null:channelDescriptions.toArray(new String[0])),null), subVolMapMask,description);
//
//	}
//	public static void sendVolumeDomain(Component requester,PDEDataContext pdeDataContext,ISize iSize,ClientTaskStatusSupport clientTaskStatusSupport,ListenAndCancel listenAndCancel,String description,SimulationModelInfo simulationModelInfo) throws Exception{
//		ImageJConnection[] imageJConnection = new ImageJConnection[1];
//		try{
//			if(listenAndCancel != null){
//				listenAndCancel.setCancelMethod(new Runnable() {
//					@Override
//					public void run() {
//						if(imageJConnection[0] != null){
//							imageJConnection[0].closeConnection();
//						}
//					}
//				});
//			}
//	    	if(clientTaskStatusSupport != null){
//	    		clientTaskStatusSupport.setMessage("Sending Domain data to ImageJ...");
//	    	}
//	    	imageJConnection[0] = new ImageJConnection(ExternalCommunicator.IMAGEJ);
//			imageJConnection[0].openConnection(VCellImageJCommands.vcellSendDomains,description);
//			sendVolumeDomain0(imageJConnection[0], pdeDataContext.getCartesianMesh(), simulationModelInfo, description);
//		}catch(Exception e){
//			if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
//				//ignore, we were cancelled
//			}
//		}finally{
//			try{if(imageJConnection != null){imageJConnection[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
//		}
//
//	}
//	public static String readLine(BufferedInputStream bis) throws IOException{
//		StringBuffer sb = new StringBuffer();
//		int nextChar = 0;
//		while((nextChar = bis.read()) != -1){
//			if(nextChar == '\n'){
//				break;
//			}else if(nextChar == '\r'){
//				bis.mark(1);
//				nextChar = bis.read();
//				if(nextChar == -1 || nextChar != '\n'){
//					bis.reset();
//					break;
//				}
//			}
//			sb.append((char)nextChar);
//		}
//		return sb.toString();
//	}
//	public static void vcellSendNRRD(final Component requester,BufferedInputStream bis,ClientTaskStatusSupport clientTaskStatusSupport,ImageJConnection imageJConnection,String description,double[] timePoints,String[] channelDescriptions) throws Exception{
//    	if(clientTaskStatusSupport != null){
//    		clientTaskStatusSupport.setMessage("reading format... ");
//    	}
//		//read nrrd file format (See NRRDWriter.writeNRRD(...) for header format)
//		DataInputStream dis = new DataInputStream(bis);
//		readLine(bis);//magic nrrd
//		readLine(bis);//endian
//		readLine(bis);//comment
//		String type = extract(readLine(bis));//"double"
//		Integer.parseInt(extract(readLine(bis)));//integer (dimension)
//		extract(readLine(bis));//"raw" (encoding)
//		BasicStackDimensions basicStackDimensions = extractArr(readLine(bis));
//		
//		//read other text header elements until exhausted
//		String unused = "";
//		while((unused = readLine(bis)).length() != 0){
//			System.out.println(unused);
//		}
//		try{
//	    	if(clientTaskStatusSupport != null){
//	    		clientTaskStatusSupport.setMessage("Sending data to ImageJ...");
//	    	}
//			imageJConnection.openConnection(VCellImageJCommands.vcellSendImage,description);
//			double[] data = new double[basicStackDimensions.getTotalSize()];
//			for (int i = 0; i < data.length; i++) {
//				data[i] = dis.readDouble();
//			}
//			sendData0(imageJConnection, new HyperStackHelper(basicStackDimensions, new Extent(1,1,1), false, Float.class.getSimpleName(), null,null,timePoints,channelDescriptions,null), data,description);
//		}
//		finally{
//			try{if(imageJConnection != null){imageJConnection.closeConnection();}}catch(Exception e){e.printStackTrace();}
//		}
//
//	}
//	private static void sendMembraneOutline(ImageJConnection imageJConnection,Hashtable<SampledCurve, int[]>[] membraneTables) throws Exception{
//		if(membraneTables != null){
//			imageJConnection.dos.writeInt(membraneTables.length);//num slices
//			for(Hashtable<SampledCurve, int[]> membraneTable:membraneTables){
//				//System.out.println();
//				if(membraneTable != null){//some slices have no membrane outline
//					imageJConnection.dos.writeInt(membraneTable.size());//num polygons on slice
//					Enumeration<SampledCurve> sliceMembranes = membraneTable.keys();
//					while(sliceMembranes.hasMoreElements()){
//						SampledCurve sampledCurve = sliceMembranes.nextElement();
//						Vector<Coordinate> polygonPoints = sampledCurve.getControlPointsVector();
//						//System.out.println(polygonPoints.size());
//						imageJConnection.dos.writeInt(polygonPoints.size());//num points for polygon
//						imageJConnection.dos.writeInt((sampledCurve.isClosed()?1:0));//isClosed
//						for(Coordinate coord:polygonPoints){
//							imageJConnection.dos.writeDouble(coord.getX());
//							imageJConnection.dos.writeDouble(coord.getY());
//							imageJConnection.dos.writeDouble(coord.getZ());
//						}
//					}
//				}else{
//					imageJConnection.dos.writeInt(0);
//				}
//			}
//		}else{
//			imageJConnection.dos.writeInt(0);
//		}
//	}
//
//	private static void sendData0(ImageJConnection imageJConnection, HyperStackHelper hyperStackHelper,Object dataObj,String title) throws Exception{
//		//
//		//This method expects data to be in "x,y,z,t,c" order
//		//
//		//See cbit.vcell.export.server.RasterExporter.exportPDEData(...), NRRD_SINGLE, GEOMETRY_FULL
//		//See also RasterExporter.NRRDHelper.createSingleFullNrrdInfo(...)
//		//
//		imageJConnection.dos.writeUTF(title);
//		hyperStackHelper.writeInfo(imageJConnection.dos);
//		if(dataObj instanceof double[]){//convert to floats for imagej (GRAY32 image type)
//			final int buffersize = 100000;
//			byte[] bytes = new byte[Float.BYTES*buffersize];
//			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
//			int dataLen = hyperStackHelper.getTotalSize();
//			for (int i = 0; i < dataLen; i++) {
//				float val = (float)(((double[])dataObj)[i]);
//				if(val == 0 && ((double[])dataObj)[i] != 0){
//					val = Float.MIN_VALUE;
//				}
//				byteBuffer.putFloat(val);
//				if((i+1)%buffersize == 0){
//					imageJConnection.dos.write(bytes, 0, buffersize*Float.BYTES);
//					byteBuffer.rewind();
//				}
//			}
//			if(dataLen%buffersize != 0){
//				imageJConnection.dos.write(bytes, 0, (dataLen%buffersize)*Float.BYTES);
//			}
//		}else if(dataObj instanceof byte[]){//send 8bit bytes as is to ImageJ (grayscale or colormap images)
//			imageJConnection.dos.write((byte[])dataObj);
//		}else if(dataObj instanceof Hashtable && hyperStackHelper.domainSubvolumeIDs != null){//convert to bytes (0 or 255) for ImageJ binary processing
//			int channelSize = hyperStackHelper.xsize*hyperStackHelper.ysize*hyperStackHelper.zsize;
//			Enumeration<Integer> subVolIDs = ((Hashtable<Integer, BitSet>)dataObj).keys();
//			while(subVolIDs.hasMoreElements()){
//				Integer subvolID = subVolIDs.nextElement();
//				BitSet bitset = ((Hashtable<Integer,BitSet>)dataObj).get(subvolID);
//				System.out.println(bitset.cardinality());
//				byte[] data = new byte[channelSize];
//				Arrays.fill(data, (byte)0);
//				for (int i = 0; i < channelSize; i++) {
//					if(bitset.get(i)){
//						data[i]|= 0xFF;
//					}
//				}
//				imageJConnection.dos.write(data);
//			}
//		}else{
//			throw new IllegalArgumentException("Unexpected data type="+dataObj.getClass().getName());
//		}
//	}
//	public static void vcellSendImage(final Component requester,final PDEDataContext pdeDataContext,SubVolume subvolume,Hashtable<SampledCurve, int[]>[] membraneTables,String description,double[] timePoints,String[] channelDescriptions,int[] colormap) throws Exception{//xyz, 1 time, 1 var
//		final ImageJConnection[] imageJConnectionArr = new ImageJConnection[1];
//		AsynchClientTask sendImageTask = new AsynchClientTask("Send image to ImageJ...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//			@Override
//			public void run(Hashtable<String, Object> hashTable) throws Exception {
//				try{					
//					ImageJConnection imageJConnection = new ImageJConnection(ExternalCommunicator.IMAGEJ);
//					imageJConnectionArr[0] = imageJConnection;
//					imageJConnection.openConnection(VCellImageJCommands.vcellSendImage,description);
//					//send size of the standard 5 dimensions in this order (width, height, nChannels, nSlices, nFrames)
//					ISize xyzSize = pdeDataContext.getCartesianMesh().getISize();
//					Extent extent = pdeDataContext.getCartesianMesh().getExtent();
//					BasicStackDimensions basicStackDimensions = new BasicStackDimensions(xyzSize.getX(), xyzSize.getY(), xyzSize.getZ(), 1, 1);
//					sendData0(imageJConnection, new HyperStackHelper(basicStackDimensions,extent,true,Float.class.getSimpleName(),null,new int[] {subvolume.getHandle()},timePoints,channelDescriptions,colormap), pdeDataContext.getDataValues(),"'"+pdeDataContext.getVariableName()+"'"+pdeDataContext.getTimePoint());
//					sendVolumeDomain0(imageJConnection, pdeDataContext.getCartesianMesh(), null, description);
//					sendMembraneOutline(imageJConnection, membraneTables);
//				}catch(Exception e){
//					if(e instanceof UserCancelException){
//						throw e;
//					}
//					e.printStackTrace();
//					hashTable.put("imagejerror",e);
//				}finally{
//					try{if(imageJConnectionArr[0] != null){imageJConnectionArr[0].closeConnection();}}catch(Exception e){e.printStackTrace();}
//				}
//			
//			}			
//		};
//		ClientTaskDispatcher.dispatch(requester, new Hashtable<>(), new AsynchClientTask[] {sendImageTask}, false, true, new ProgressDialogListener() {
//			@Override
//			public void cancelButton_actionPerformed(EventObject newEvent) {
//				if(imageJConnectionArr[0] != null){
//					imageJConnectionArr[0].closeConnection();
//				}
//			}
//		});
//	}
	
	private static JAXBContext jaxbContext = null;
	static {
		try {
			jaxbContext = JAXBContext.newInstance(new Class[] {VCCommandList.class,IJModelInfos.class,IJSolverStatus.class,IJTimeSeriesJobResults.class,IJDataList.class,IJVarInfos.class,IJFieldData.class,IJGeom.class});
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	private static String createXML(Object theClass) throws Exception{
		Marshaller m = jaxbContext.createMarshaller();
		// for pretty-print XML in JAXB
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		m.marshal(theClass, writer);
		String str = writer.toString();
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
	public static enum ApiEnum {hello,getinfo,getdata,gettimeseries,solver};
	private static String GETINFO_PARMS = IJListParams.type.name()+"={"+IJDocType.bm.name()+","+IJDocType.mm.name()+","+IJDocType.quick.name()+"}"+"&"+IJListParams.open.name()+"={true,false}";
	private static String GETDATA_PARMS = IJGetDataParams.cachekey.name()+"=int"+"&"+IJGetDataParams.varname.name()+"=string"+"&"+IJGetDataParams.timeindex.name()+"="+"double";
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
			apiParams.put(ApiEnum.solver, new VCCommand[] {
					/*"type={biom,math}","type=sims&modelname=xxx"*/
					new VCCommand(ApiEnum.solver.name()+"?"+"TBI", "Start solver")
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
		@XmlElement
		private double[][] min;
		@XmlElement
		private double[][] max;
		@XmlElement
		private double[][] unweightedMean;
		@XmlElement
		private double[][] weightedMean = null;
		@XmlElement
		private double[] totalSpace = null;
		@XmlElement
		private double[][] unweightedSum;
		@XmlElement
		private double[][] weightedSum;

		public IJTimeSeriesJobResults() {
			
		}
		public IJTimeSeriesJobResults(String[] variableNames, int[] indices, double[] times, double[][][] data) {
			super();
			this.variableNames = variableNames;
			this.indices = indices;
			this.times = times;
			this.data = data;
		}
		public IJTimeSeriesJobResults(String[] variableNames, int[] indices, double[] times, double[][] min,
				double[][] max, double[][] unweightedMean, double[][] weightedMean, double[] totalSpace,
				double[][] unweightedSum, double[][] weightedSum) {
			super();
			this.variableNames = variableNames;
			this.indices = indices;
			this.times = times;
			this.min = min;
			this.max = max;
			this.unweightedMean = unweightedMean;
			this.weightedMean = weightedMean;
			this.totalSpace = totalSpace;
			this.unweightedSum = unweightedSum;
			this.weightedSum = weightedSum;
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
    								if(!ijSimInfo.isDB) {
    									KeyValue simKey = new KeyValue(ijSimInfo.simId.substring("SimId_".length()));
    									ijDataResponder = IJDataResponder.create(simKey, ResourceUtil.getLocalRootDir(), jobid);
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
		@XmlAttribute()
		private boolean isOpen;
		@XmlAttribute()
		private boolean isDB;
		@XmlAttribute()
		private String simId;
		@XmlAttribute()
		private String name;
		@XmlAttribute()
		private long cacheKey;
		public IJSimInfo() {
		}
		public IJSimInfo(boolean isOpen, boolean isDB, String simId, String name) {
			super();
			this.isOpen = isOpen;
			this.isDB = isDB;
			this.simId = simId;
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
		private Integer geomDim;
		@XmlAttribute()
		private String geomName;
		@XmlElement(name="simInfo")
		private ArrayList<IJSimInfo> ijSimId;
		public IJContextInfo() {
			
		}
		public IJContextInfo(String name, MathType mathType, Integer geomDim, String geomName, ArrayList<IJSimInfo> ijSimId) {
			super();
			this.name = name;
			this.mathType = (mathType != null?mathType.name():null);
			this.geomDim = geomDim;
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
		private Long date;
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
			this.date = (date != null?date.getTime():null);
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
			ijSimInfos.add(new IJSimInfo(true, false, sim.getSimulationID(), sim.getName()));
		}
		ijContextInfos.add(new IJContextInfo(contextName,mathType,geomDim,geomName, ijSimInfos));	
	}
	private static void populateDesktopIJModelInfos(IJDocType docType,ArrayList<KeyValue> openVCDocumentVersionKeys,ArrayList<IJModelInfo> modelInfos) {
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
		    			openVCDocumentVersionKeys.add(documentWindowManager.getVCDocument().getVersion().getVersionKey());
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
	
	private static SimulationWorkspaceModelInfo getOpenSimulationWorkspaceModelInfo(VCSimulationIdentifier quickrunKey) {
		if(VCellClientTest.getVCellClient() == null) {
			return null;
		}
//		ArrayList<SimulationWorkspaceModelInfo> result = new ArrayList<>();
		Collection<TopLevelWindowManager> windowManagers = VCellClientTest.getVCellClient().getMdiManager().getWindowManagers();
		for(TopLevelWindowManager topLevelWindowManager:windowManagers) {
	    	if(topLevelWindowManager instanceof DocumentWindowManager) {
	    		DocumentWindowManager documentWindowManager = (DocumentWindowManager)topLevelWindowManager;
	    		VCDocument.VCDocumentType currDocType = documentWindowManager.getVCDocument().getDocumentType();
	    		if(currDocType == VCDocumentType.BIOMODEL_DOC || currDocType == VCDocumentType.MATHMODEL_DOC) {
	    			if(documentWindowManager instanceof BioModelWindowManager) {
	    				SimulationWindow simWindow = ((BioModelWindowManager)documentWindowManager).haveSimulationWindow(quickrunKey);
	    				if(simWindow != null) {
	    					return new SimulationWorkspaceModelInfo(simWindow.getSimOwner(), simWindow.getSimulation().getName());
	    				}
//	    				System.out.println(((BioModelWindowManager)documentWindowManager).haveSimulationWindow(quickrunKey));
//	    				for(SimulationContext simulationContext:((BioModelWindowManager)documentWindowManager).getBioModel().getSimulationContexts()) {
//	    					Simulation[] sims = simulationContext.getSimulations();
//	    					for (int i = 0; i < sims.length; i++) {
//								if(quickrunKey.equals(sims[i].getKey())) {
//									result.add(new SimulationWorkspaceModelInfo(simulationContext, sims[i].getName()));
//								}
//							}
//	    				}
	    			}else if(documentWindowManager instanceof MathModelWindowManager) {
	    				SimulationWindow simWindow = ((MathModelWindowManager)documentWindowManager).haveSimulationWindow(quickrunKey);
	    				if(simWindow != null) {
	    					return new SimulationWorkspaceModelInfo(simWindow.getSimOwner(), simWindow.getSimulation().getName());
	    				}

//	    				MathModel mathModel = ((MathModelWindowManager)documentWindowManager).getMathModel();
//    					Simulation[] sims = mathModel.getSimulations();
//    					for (int i = 0; i < sims.length; i++) {
//							if(quickrunKey.equals(sims[i].getKey())) {
//								result.add(new SimulationWorkspaceModelInfo(mathModel, sims[i].getName()));
//							}
//						}
	    			}
	    		}
	    	}
		}
//		if(result.size() > 0) {
//			return result.toArray(new SimulationWorkspaceModelInfo[0]);
//		}
		return null;
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
	private static HashMap<String, ODESolverResultSet> SimDataIdMapToOdeResultSet = new HashMap<>();
	private static enum IJListParams {type,open}
	public static class ApiListHandler extends AbstractHandler{

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
	    	System.out.println(target+"\n"+baseRequest.getQueryString());
	    	List<NameValuePair> params = getParamsFromRequest(request);
	    	IJDocType ijDocType = null;
	    	Boolean bOpenOnly = null;
//	    	Long cacheKey = null;
	    	for(NameValuePair nameValuePair:params) {
	    		if(nameValuePair.getName().equals(IJListParams.type.name())) {
	    			ijDocType = IJDocType.valueOf(nameValuePair.getValue());
	    		}else if(nameValuePair.getName().equals(IJListParams.open.name())) {
	    			bOpenOnly = Boolean.parseBoolean(nameValuePair.getValue());
	    		}
//	    		else if(nameValuePair.getName().equals(IJListParams.cachekey.name())) {
//	    			cacheKey = Long.valueOf(nameValuePair.getValue());
//	    		}
	    	}
	    	if(ijDocType == null && bOpenOnly == null/* && cacheKey == null*/) {
	    		response.setContentType("text/html; charset=utf-8");
	    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		response.getWriter().println("<h1>Expecting '"+GETINFO_PARMS+"' in request</h1>");
	    	}else {
		        // Declare response encoding and types
		        response.setContentType("text/xml; charset=utf-8");
	
		        // Declare response status code
		        response.setStatus(HttpServletResponse.SC_OK);
		        
		        ArrayList<KeyValue> openVCDocumentVersionKeys = new ArrayList<>();		     
		        ArrayList<IJModelInfo> modelInfos = new ArrayList<>();
	        	populateDesktopIJModelInfos(ijDocType,openVCDocumentVersionKeys, modelInfos);
		        
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
								Integer parentGeomDim = null;
								try {
									String simRoot = file.getName().substring(0, file.getName().length()-SimDataConstants.LOGFILE_EXTENSION.length());
									File meshFile = new File(file.getParentFile(),simRoot+SimDataConstants.MESHFILE_EXTENSION);
									if(meshFile.exists()) {//try to get geometry dimension even if there is no open vcell data window
										File meshmetricsFile = new File(file.getParentFile(),simRoot+SimDataConstants.MESHMETRICSFILE_EXTENSION);
										File subdomainFile = new File(file.getParentFile(),simRoot+SimDataConstants.SUBDOMAINS_FILE_SUFFIX);
										CartesianMesh mesh = CartesianMesh.readFromFiles(meshFile, meshmetricsFile, subdomainFile);
										parentGeomDim = mesh.getGeometryDimension();
									}
								} catch (Exception e) {
									e.printStackTrace();
									//continue, downstream might not need dim
								}
								StringTokenizer st = new StringTokenizer(file.getName(), "_");
								st.nextToken();
								String quickrunKey = st.nextToken();
								//See if a quickrun sim has an open data viewer that we can get context info from
								String parentSimName = null;
								String parentContextName = null;
								MathType parentMathType = null;
								String parentGeomName = null;
								String parentModelName = null;
								Date parentDate = null;
								String parentUser = null;
								KeyValue parentModelKey = null;
								IJDocType docType = IJDocType.quick;
								boolean bOpenOnDesktop = false;
								//Match a local file SimID with an open model if possible
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
						    					int tempdim = simulationWindow.getSimOwner().getGeometry().getDimension();
						    					if(parentGeomDim != null && parentGeomDim.intValue() != tempdim) {
						    						throw new ServletException("Geometry dimesnion from mesh="+parentGeomDim+" does not equal display window model dimension="+tempdim);
						    					}
						    					parentGeomDim = tempdim;
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
					    		if(bOpenOnly == null || bOpenOnly.booleanValue() == false || (bOpenOnly.booleanValue() == true && bOpenOnDesktop == true)){
									ArrayList<IJContextInfo> contInfos = new ArrayList<>();
									ArrayList<IJSimInfo> ijsimfos= new ArrayList<>();
									ijsimfos.add(new IJSimInfo(bOpenOnDesktop, false,Simulation.createSimulationID(new KeyValue(quickrunKey)), parentSimName));
//									ijsimfos.add(new IJSimInfo(quickrunKey, parentSimName));
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
				        	if((bOpenOnly != null && bOpenOnly.booleanValue()) && !openVCDocumentVersionKeys.contains(bioModelInfo.getVersion().getVersionKey())) {
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
				        					ijSimInfos.add(new IJSimInfo(false, true, null, simName));
//				        					ijSimInfos.add(new IJSimInfo(null,simName));
				        				}
			        					IJContextInfo ijContextInfo = new IJContextInfo(bioModelContextName,bioModelInfo.getBioModelChildSummary().getAppTypes()[i],bioModelInfo.getBioModelChildSummary().getGeometryDimensions()[i],bioModelInfo.getBioModelChildSummary().getGeometryNames()[i],ijSimInfos);
			        					ijContextInfos.add(ijContextInfo);
				        			}
				        		}
				        	}
				        	modelInfos.add(new IJModelInfo(bioModelInfo.getVersion().getName(), bioModelInfo.getVersion().getDate(), IJDocType.bm, openVCDocumentVersionKeys.contains(bioModelInfo.getVersion().getVersionKey()),bioModelInfo.getVersion().getOwner().getName(),bioModelInfo.getVersion().getVersionKey(), ijContextInfos));
				        }
			        }
			        
			        if(ijDocType == null || ijDocType == IJDocType.mm) {
				        MathModelInfo[] mathModelInfos = requestManager.getDocumentManager().getMathModelInfos();
				        for(MathModelInfo mathModelInfo:mathModelInfos) {
				        	if((bOpenOnly != null && bOpenOnly.booleanValue()) && !openVCDocumentVersionKeys.contains(mathModelInfo.getVersion().getVersionKey())) {
				        		continue;
				        	}
		    				ArrayList<IJSimInfo> ijSimInfos = new ArrayList<>();
		    				for(String simName:mathModelInfo.getMathModelChildSummary().getSimulationNames()) {
		    					ijSimInfos.add(new IJSimInfo(false, true, null, simName));
//		    					ijSimInfos.add(new IJSimInfo(null,simName));
		    				}
		
					        ArrayList<IJContextInfo> ijContextInfos = new ArrayList<>();
							IJContextInfo ijContextInfo = new IJContextInfo(null,mathModelInfo.getMathModelChildSummary().getModelType(),mathModelInfo.getMathModelChildSummary().getGeometryDimension(),mathModelInfo.getMathModelChildSummary().getGeometryName(),ijSimInfos);
							ijContextInfos.add(ijContextInfo);
				        	modelInfos.add(new IJModelInfo(mathModelInfo.getVersion().getName(), mathModelInfo.getVersion().getDate(), IJDocType.mm, openVCDocumentVersionKeys.contains(mathModelInfo.getVersion().getVersionKey()), mathModelInfo.getVersion().getOwner().getName(),mathModelInfo.getVersion().getVersionKey(),ijContextInfos));
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
	private static class IJData {
		@XmlAttribute
		private String varname;
		@XmlAttribute
		private Double timepoint;
		@XmlAttribute
		private Integer jobindex;
		@XmlElement
		private BasicStackDimensions stackInfo;
		@XmlInlineBinaryData
		private byte[] data;//double[] converted to byte[]
		public IJData() {
			
		}
		public IJData(BasicStackDimensions stackInfo, byte[] data,String varname,Double timepoint,Integer jobindex) {
			super();
			this.stackInfo = stackInfo;
			this.data = data;
			this.varname = varname;
			this.timepoint = timepoint;
			this.jobindex = jobindex;
		}
		
	}
	
	@XmlRootElement
	private static class IJDataList {
		@XmlElement
		IJData[] ijData;
		public IJDataList() {
			
		}
		public IJDataList(IJData[] ijData) {
			super();
			this.ijData = ijData;
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
		private PDEDataContext pdeDataContext;
		private SimulationModelInfo simulationModelInfo;
//		private ClientPDEDataContext clientPDEDataContext;
//		private ServerPDEDataContext serverPDEDataContext;
		private IJDataResponder(OutputContext outputContext,VCSimulationDataIdentifier vcSimulationDataIdentifier,Integer jobcount,User user) {
			this.vcSimulationDataIdentifier = vcSimulationDataIdentifier;
			this.outputContext = outputContext;
			this.jobCount = jobcount;
			this.user = user;
		}
		private IJDataResponder(VCDataManager vcDataManager,OutputContext outputContext,VCSimulationDataIdentifier vcSimulationDataIdentifier,Integer jobcount,User user,SimulationModelInfo simulationModelInfo) {//saved to database
			this(outputContext,  vcSimulationDataIdentifier,jobcount,user);
			this.vcDataManager = vcDataManager;
			this.pdeDataContext = new ClientPDEDataContext(new PDEDataManager(outputContext, vcDataManager, vcSimulationDataIdentifier));
			this.simulationModelInfo = simulationModelInfo;
		}
		private IJDataResponder(DataSetControllerImpl dataSetControllerImpl,OutputContext outputContext,VCSimulationDataIdentifier vcSimulationDataIdentifier,Integer jobCount,User user,SimulationModelInfo simulationModelInfo) throws Exception {//quickrun
			this(outputContext, vcSimulationDataIdentifier,jobCount,user);
			this.dataSetControllerImpl = dataSetControllerImpl;
			this.pdeDataContext = new ServerPDEDataContext(outputContext, user, new DataServerImpl(dataSetControllerImpl, null), vcSimulationDataIdentifier);
//	        ArrayList<IJModelInfo> modelInfos = new ArrayList<>();
//	        ArrayList<KeyValue> openVCDocumentVersionKeys = new ArrayList<>();		     
//        	populateDesktopIJModelInfos(IJDocType.quick,openVCDocumentVersionKeys, modelInfos);
//        	modelInfos.get(0).contexts.get(0).ijSimId.get(0).
			this.simulationModelInfo = simulationModelInfo;
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
					SimulationModelInfo simulationModelInfo0 = new SimulationWorkspaceModelInfo(simOwner, simName);
					return new IJDataResponder(vcDataManager, outputContext, vcSimulationDataIdentifier,sim.getScanCount(),sim.getSimulationVersion().getOwner(),simulationModelInfo0);
				}
			}
			throw new Exception("IJREsponder: simulation name '"+simName+"' not found");
		}
		public static IJDataResponder create(KeyValue quickrunKey,File simDataDir,int jobIndex) throws Exception{
			VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(quickrunKey, User.tempUser);
			VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);
//			SimulationData simulationData = new SimulationData(vcSimulationDataIdentifier, simDataDir, simDataDir, null);
			DataSetControllerImpl dsci = new DataSetControllerImpl(null, simDataDir,null);
			final SimulationWorkspaceModelInfo openSimulationWorkspaceModelInfo = getOpenSimulationWorkspaceModelInfo(vcSimulationIdentifier);
			return new IJDataResponder(dsci, null, vcSimulationDataIdentifier,1,User.tempUser,openSimulationWorkspaceModelInfo);
		}
		public ArrayList<IJData> getIJData(String varname,int[] timeIndexes) throws Exception{
			ArrayList<IJData> ijDatas = new ArrayList<>();
			double[] times = (dataSetControllerImpl != null?dataSetControllerImpl.getDataSetTimes(vcSimulationDataIdentifier):vcDataManager.getDataSetTimes(vcSimulationDataIdentifier));
			CartesianMesh mesh = pdeDataContext.getCartesianMesh();
			BasicStackDimensions basicStackDimensions = new BasicStackDimensions(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ(), 1, 1);
			if(timeIndexes != null) {
				for(int timeIndex:timeIndexes) {
					double timePoint = times[timeIndex];
					SimDataBlock simDataBlock = (dataSetControllerImpl != null?dataSetControllerImpl.getSimDataBlock(outputContext, vcSimulationDataIdentifier, varname,timePoint):vcDataManager.getSimDataBlock(outputContext, vcSimulationDataIdentifier, varname, timePoint));
					ijDatas.add(new IJData(basicStackDimensions, convertDoubleToBytes(simDataBlock.getData()),varname,timePoint,vcSimulationDataIdentifier.getJobIndex()));				
				}
			}else {
				ijDatas.add(new IJData(basicStackDimensions, null,varname,-1.0,vcSimulationDataIdentifier.getJobIndex()));	
			}
			return ijDatas;
		}
//		public DataIdentifier[] getDataIdentifiers() throws FileNotFoundException, DataAccessException, IOException {
//			return pdeDataContext.getDataIdentifiers();
////			return (dataSetControllerImpl != null?dataSetControllerImpl.getDataIdentifiers(outputContext, vcSimulationDataIdentifier):vcDataManager.getDataIdentifiers(outputContext, vcSimulationDataIdentifier));
//		}
//		public CartesianMesh getMesh() throws DataAccessException, IOException, MathException {
//			return pdeDataContext.getCartesianMesh();
////			return (dataSetControllerImpl != null?dataSetControllerImpl.getMesh(vcSimulationDataIdentifier):vcDataManager.getMesh(vcSimulationDataIdentifier));
//		}
		public IJData getOdeIJData(String varName) throws Exception{
			ODESolverResultSet odeSolverResultSet = SimDataIdMapToOdeResultSet.get(vcSimulationDataIdentifier.toString());
			if(odeSolverResultSet == null) {
				if(vcDataManager != null) {
					ODESimData odeSimData = vcDataManager.getODEData(vcSimulationDataIdentifier);
					odeSolverResultSet = odeSimData;
				}else {
					ODEDataBlock odeDataBlock = dataSetControllerImpl.getODEDataBlock(vcSimulationDataIdentifier);
					odeSolverResultSet = odeDataBlock.getODESimData();
				}
				SimDataIdMapToOdeResultSet.clear();
				SimDataIdMapToOdeResultSet.put(vcSimulationDataIdentifier.toString(), odeSolverResultSet);
			}
			double[] doubles = odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn(varName));
//			double[] times = odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn(ReservedVariable.TIME.getName()));
//			for (int i = 0; i < odeSolverResultSet.getRowCount(); i++) {
//				ColumnDescription columnDescription = odeSolverResultSet.getColumnDescriptions(i);
//				if(columnDescription.getName().equals(varName)) {
//					doubles = odeSolverResultSet.extractColumn(i);
//					if (times != null) {
//						break;
//					}
//				}else if(columnDescription.getName().equals(ReservedVariable.TIME.getName())) {
//					times = odeSolverResultSet.extractColumn(i);
//					if (doubles != null) {
//						break;
//					}
//				}
//			}
			BasicStackDimensions basicStackDimensions = new BasicStackDimensions(1, 1, 1, 1, doubles.length);
			IJData ijData = new IJData(basicStackDimensions, convertDoubleToBytes(doubles),varName,null,vcSimulationDataIdentifier.getJobIndex());
			return ijData;
		}
		public IJTimeSeriesJobResults getIJTimeSeriesData(IJTimeSeriesJobSpec ijTimeSeriesJobSpec) throws Exception{
			TimeSeriesJobSpec timeSeriesJobSpec = null;
			BitSet[] domainMaskArr = new BitSet[ijTimeSeriesJobSpec.variableNames.length];
			for (int i = 0; i < domainMaskArr.length; i++) {
				if(i==0) {
					pdeDataContext.setVariableName(ijTimeSeriesJobSpec.variableNames[i]);
					domainMaskArr[i] = getDomainMask(pdeDataContext.getDataIdentifier(),simulationModelInfo);
					System.out.println(domainMaskArr[i].cardinality());
					if(ijTimeSeriesJobSpec.indices == null) {
						ijTimeSeriesJobSpec.indices = new int[domainMaskArr[i].cardinality()];
						int cnt = 0;
						for (int j = domainMaskArr[i].nextSetBit(0); j >= 0; j = domainMaskArr[i].nextSetBit(j+1)) {
							ijTimeSeriesJobSpec.indices[cnt++] = j;
							if (i == Integer.MAX_VALUE) {
								break; // or (i+1) would overflow
							}
						}
					}else {
						domainMaskArr[i].clear();
						for (int j = 0; j < ijTimeSeriesJobSpec.indices.length; j++) {
							domainMaskArr[i].set(ijTimeSeriesJobSpec.indices[j]);
						}
					}
				}else {
					domainMaskArr[i] = domainMaskArr[0];
				}
			}

			int[][] copiedIndices = new int[ijTimeSeriesJobSpec.variableNames.length][];
			for (int i = 0; i < ijTimeSeriesJobSpec.variableNames.length; i++) {
				copiedIndices[i] = ijTimeSeriesJobSpec.indices;
			}

			if(!(ijTimeSeriesJobSpec.calcTimeStats || ijTimeSeriesJobSpec.calcSpaceStats)) {
				timeSeriesJobSpec = new TimeSeriesJobSpec(ijTimeSeriesJobSpec.variableNames, copiedIndices,null,
					ijTimeSeriesJobSpec.startTime, ijTimeSeriesJobSpec.step, ijTimeSeriesJobSpec.endTime, new VCDataJobID(vcSimulationDataIdentifier.getJobIndex(), user, false));
			}else if(!ijTimeSeriesJobSpec.calcTimeStats){
				timeSeriesJobSpec = new TimeSeriesJobSpec(ijTimeSeriesJobSpec.variableNames, domainMaskArr,
					ijTimeSeriesJobSpec.startTime, ijTimeSeriesJobSpec.step, ijTimeSeriesJobSpec.endTime, ijTimeSeriesJobSpec.calcSpaceStats, ijTimeSeriesJobSpec.calcTimeStats, new VCDataJobID(vcSimulationDataIdentifier.getJobIndex(), user, false));				
			}else {
				throw new IllegalArgumentException("calc time states not yet implemented");
			}
			
			
			TimeSeriesJobResults timeSeriesJobResults = (dataSetControllerImpl != null?dataSetControllerImpl.getTimeSeriesValues(outputContext, vcSimulationDataIdentifier, timeSeriesJobSpec):vcDataManager.getTimeSeriesValues(outputContext, vcSimulationDataIdentifier, timeSeriesJobSpec));
			if(timeSeriesJobResults instanceof TSJobResultsNoStats) {
				double[][][] newData = new double[timeSeriesJobResults.getVariableNames().length][][];
				for (int i = 0; i < newData.length; i++) {
					newData[i] = ((TSJobResultsNoStats)timeSeriesJobResults).getTimesAndValuesForVariable(timeSeriesJobResults.getVariableNames()[i]);
				}
				return new IJTimeSeriesJobResults(timeSeriesJobResults.getVariableNames(), timeSeriesJobResults.getIndices()[0], timeSeriesJobResults.getTimes(), newData);
			}else if(timeSeriesJobResults instanceof TSJobResultsSpaceStats) {
				TSJobResultsSpaceStats tsjrst = ((TSJobResultsSpaceStats) timeSeriesJobResults);
				return new IJTimeSeriesJobResults(timeSeriesJobResults.getVariableNames(), timeSeriesJobResults.getIndices()[0], timeSeriesJobResults.getTimes(),
					tsjrst.getMinimums(),tsjrst.getMaximums(), tsjrst.getUnweightedMean(), tsjrst.getWeightedMean(), tsjrst.getTotalSpace(), tsjrst.getUnweightedSum(), tsjrst.getWeightedSum());
			}
			throw new Exception("Unexpected Results type= "+timeSeriesJobResults.getClass().getName());
		}
		public IJVarInfos getIJVarInfos(String simName,Long cachekey,Integer scancount) throws Exception{
			DataIdentifier[] dataIdentifiers = (dataSetControllerImpl != null?dataSetControllerImpl.getDataIdentifiers(outputContext, vcSimulationDataIdentifier):vcDataManager.getDataIdentifiers(outputContext, vcSimulationDataIdentifier));
			ArrayList<IJVarInfo> ijVarInfos = new ArrayList<>();
			for(DataIdentifier did:dataIdentifiers) {
				ijVarInfos.add(new IJVarInfo(did.getName(), did.getDisplayName(), did.getVariableType(), did.getDomain(), did.isFunction()));
			}
			return new IJVarInfos(ijVarInfos,simName,cachekey,(dataSetControllerImpl != null?dataSetControllerImpl.getDataSetTimes(vcSimulationDataIdentifier):vcDataManager.getDataSetTimes(vcSimulationDataIdentifier)),scancount);
		}
		public void respondData(HttpServletResponse response,String[] varNames,int[] timeIndexes,boolean bSpatial) throws Exception{
			ArrayList<IJData> ijDatas = new ArrayList<>();
			for (int i = 0; i < varNames.length; i++) {
				if(bSpatial) {
					ijDatas.addAll(getIJData(varNames[i],timeIndexes));
				}else {
					ijDatas.add(getOdeIJData(varNames[i]));
				}				
			}
			respond(response, createXML(new IJDataList(ijDatas.toArray(new IJData[0]))));
		}
		public void respondIdentifiers(HttpServletResponse response,String simName,Long cachekey) throws Exception{
    		respond(response, createXML(getIJVarInfos(simName, cachekey, jobCount)));
		}
		private void respond(HttpServletResponse response,String xml) throws IOException{
    		response.setContentType("text/xml; charset=utf-8");
//    		response.setContentLength(xml.length());
    		response.setStatus(HttpServletResponse.SC_OK);
    		response.getWriter().write(xml);    	
		}
		public BitSet getDomainMask(DataIdentifier di,SimulationModelInfo simulationModelInfo) {
//			SimulationModelInfo simulationModelInfo = new SimulationWorkspaceModelInfo(simOwner, argSimulationName);
			final CartesianMesh cartesianMesh = pdeDataContext.getCartesianMesh();
			double[] originalData = new double[cartesianMesh.getISize().getXYZ()];
			Arrays.fill(originalData, 1.0);
			DataInfoProvider dip = new DataInfoProvider(pdeDataContext, simulationModelInfo);
			final RecodeDataForDomainInfo recodeDataForDomain0 = recodeDataForDomain0(di, dip, originalData, cartesianMesh);
			BitSet domainBitSet = new BitSet(originalData.length);
			for (int i = 0; i < originalData.length; i++) {
				if(recodeDataForDomain0.getRecodedDataForDomain()[i] == 1.0) {
					domainBitSet.set(i);
				}
			}
			return domainBitSet;
		}
		
		private static RecodeDataForDomainInfo recodeDataForDomain0(DataIdentifier di,DataInfoProvider dip,double[] originalData,CartesianMesh mesh) {
			Domain varDomain = di.getDomain();
			double[] tempRecodedData = null;
			Range dataRange = null;
			VariableType vt = di.getVariableType();
			boolean bRecoding = dip != null && varDomain != null;	
			Double notInDomainValue = null;
//			if (getPdeDataContext().getDataValues() != originalData ||
//				recodeDataForDomainInfo == null ||
//				((getDataInfoProvider() == null) != bDataInfoProviderNull) ||
//				!Compare.isEqualOrNull(functionStatisticsRange, lastFunctionStatisticsRange)) {
//				lastFunctionStatisticsRange = functionStatisticsRange;
//				bDataInfoProviderNull = (dip== null);
//				originalData = getPdeDataContext().getDataValues();
				tempRecodedData = originalData;
				
				double illegalNumber = Double.POSITIVE_INFINITY;
				if (bRecoding) {
					tempRecodedData = new double[originalData.length];
					System.arraycopy(originalData, 0, tempRecodedData, 0, tempRecodedData.length);
					for(int i = 0; i < originalData.length; i++){
						if(!Double.isNaN(originalData[i])){
							illegalNumber = Math.min(illegalNumber, originalData[i]);
						}
					}
				}
				illegalNumber-=1;//
				notInDomainValue = new Double(illegalNumber);

				final CartesianMesh cartesianMesh = mesh;
				double minCurrTime = Double.POSITIVE_INFINITY;
				double maxCurrTime = Double.NEGATIVE_INFINITY;
				for (int i = 0; i < tempRecodedData.length; i ++) {
					if (bRecoding) {
						if(!isInDomain(cartesianMesh, varDomain, dip, i, vt)){
							tempRecodedData[i] = illegalNumber;
						}
					}
					if(!Double.isNaN(tempRecodedData[i]) && tempRecodedData[i] != illegalNumber){
						minCurrTime = Math.min(minCurrTime, tempRecodedData[i]);
						maxCurrTime = Math.max(maxCurrTime, tempRecodedData[i]);
					}
				}
//				if(!getdisplayAdapterService1().getAllTimes() || functionStatisticsRange == null){
//					dataRange = new Range(minCurrTime,maxCurrTime);
//				}else if(functionStatisticsRange != null){
//					dataRange = functionStatisticsRange;
//				}else{
//					throw new RuntimeException("Unexpected state for range calculation");
//				}
//			}else{
//				dataRange = recodeDataForDomainInfo.getRecodedDataRange();
//				tempRecodedData = recodeDataForDomainInfo.getRecodedDataForDomain();
//			}
			if (bRecoding) {
				return new RecodeDataForDomainInfo(true, tempRecodedData, dataRange,notInDomainValue);
			}else{
				return new RecodeDataForDomainInfo(false, tempRecodedData, dataRange,notInDomainValue);		
			}
		}

		public static boolean isInDomain(CartesianMesh cartesianMesh,Domain varDomain,DataInfoProvider dataInfoProvider,int i,VariableType vt){
			if(!cartesianMesh.hasSubvolumeInfo()){
				return true;
			}
			if (vt.equals(VariableType.VOLUME) && !(cartesianMesh.isChomboMesh())) {
				int subvol = cartesianMesh.getSubVolumeFromVolumeIndex(i);
				if (varDomain != null &&
					dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol) != null &&
					!dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol).equals(varDomain.getName())) {
					return false;
				}
			} else if (vt.equals(VariableType.VOLUME_REGION)) {
				int subvol = cartesianMesh.getVolumeRegionMapSubvolume().get(i);
				if (varDomain != null &&
					dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol) != null &&
					!dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol).equals(varDomain.getName())) {
					return false;
				}
			} else if (vt.equals(VariableType.MEMBRANE) && !(cartesianMesh.isChomboMesh())) {
				int insideVolumeIndex = cartesianMesh.getMembraneElements()[i].getInsideVolumeIndex();
				int subvol1 =  cartesianMesh.getSubVolumeFromVolumeIndex(insideVolumeIndex);
				int outsideVolumeIndex = cartesianMesh.getMembraneElements()[i].getOutsideVolumeIndex();
				int subvol2 =  cartesianMesh.getSubVolumeFromVolumeIndex(outsideVolumeIndex);
				if (varDomain != null &&
					dataInfoProvider.getSimulationModelInfo().getMembraneName(subvol1, subvol2, true) != null &&
					!dataInfoProvider.getSimulationModelInfo().getMembraneName(subvol1, subvol2, true).equals(varDomain.getName())) {
					return false;
				}
			} else if (vt.equals(VariableType.MEMBRANE_REGION)) {
				int[] subvols = cartesianMesh.getMembraneRegionMapSubvolumesInOut().get(i);
				if (varDomain != null &&
					dataInfoProvider.getSimulationModelInfo().getMembraneName(subvols[0], subvols[1], true) != null &&
					!dataInfoProvider.getSimulationModelInfo().getMembraneName(subvols[0], subvols[1], true).equals(varDomain.getName())) {
					return false;
				}
			}
			return true;
		}

	}
	
	
	
	
//	private static RecodeDataForDomainInfo recodeDataForDomain0(DataIdentifier di,DataInfoProvider dip,double[] originalData,CartesianMesh mesh) {
//		Domain varDomain = di.getDomain();
//		double[] tempRecodedData = null;
//		Range dataRange = null;
//		VariableType vt = di.getVariableType();
//		boolean bRecoding = dip != null && varDomain != null;	
//		Double notInDomainValue = null;
////		if (getPdeDataContext().getDataValues() != originalData ||
////			recodeDataForDomainInfo == null ||
////			((getDataInfoProvider() == null) != bDataInfoProviderNull) ||
////			!Compare.isEqualOrNull(functionStatisticsRange, lastFunctionStatisticsRange)) {
////			lastFunctionStatisticsRange = functionStatisticsRange;
////			bDataInfoProviderNull = (dip== null);
////			originalData = getPdeDataContext().getDataValues();
//			tempRecodedData = originalData;
//			
//			double illegalNumber = Double.POSITIVE_INFINITY;
//			if (bRecoding) {
//				tempRecodedData = new double[originalData.length];
//				System.arraycopy(originalData, 0, tempRecodedData, 0, tempRecodedData.length);
//				for(int i = 0; i < originalData.length; i++){
//					if(!Double.isNaN(originalData[i])){
//						illegalNumber = Math.min(illegalNumber, originalData[i]);
//					}
//				}
//			}
//			illegalNumber-=1;//
//			notInDomainValue = new Double(illegalNumber);
//
//			final CartesianMesh cartesianMesh = mesh;
//			double minCurrTime = Double.POSITIVE_INFINITY;
//			double maxCurrTime = Double.NEGATIVE_INFINITY;
//			for (int i = 0; i < tempRecodedData.length; i ++) {
//				if (bRecoding) {
//					if(!isInDomain(cartesianMesh, varDomain, dip, i, vt)){
//						tempRecodedData[i] = illegalNumber;
//					}
//				}
//				if(!Double.isNaN(tempRecodedData[i]) && tempRecodedData[i] != illegalNumber){
//					minCurrTime = Math.min(minCurrTime, tempRecodedData[i]);
//					maxCurrTime = Math.max(maxCurrTime, tempRecodedData[i]);
//				}
//			}
////			if(!getdisplayAdapterService1().getAllTimes() || functionStatisticsRange == null){
////				dataRange = new Range(minCurrTime,maxCurrTime);
////			}else if(functionStatisticsRange != null){
////				dataRange = functionStatisticsRange;
////			}else{
////				throw new RuntimeException("Unexpected state for range calculation");
////			}
////		}else{
////			dataRange = recodeDataForDomainInfo.getRecodedDataRange();
////			tempRecodedData = recodeDataForDomainInfo.getRecodedDataForDomain();
////		}
//		if (bRecoding) {
//			return new RecodeDataForDomainInfo(true, tempRecodedData, dataRange,notInDomainValue);
//		}else{
//			return new RecodeDataForDomainInfo(false, tempRecodedData, dataRange,notInDomainValue);		
//		}
//	}
//
//	public static boolean isInDomain(CartesianMesh cartesianMesh,Domain varDomain,DataInfoProvider dataInfoProvider,int i,VariableType vt){
//		if(!cartesianMesh.hasSubvolumeInfo()){
//			return true;
//		}
//		if (vt.equals(VariableType.VOLUME) && !(cartesianMesh.isChomboMesh())) {
//			int subvol = cartesianMesh.getSubVolumeFromVolumeIndex(i);
//			if (varDomain != null &&
//				dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol) != null &&
//				!dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol).equals(varDomain.getName())) {
//				return false;
//			}
//		} else if (vt.equals(VariableType.VOLUME_REGION)) {
//			int subvol = cartesianMesh.getVolumeRegionMapSubvolume().get(i);
//			if (varDomain != null &&
//				dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol) != null &&
//				!dataInfoProvider.getSimulationModelInfo().getVolumeNameGeometry(subvol).equals(varDomain.getName())) {
//				return false;
//			}
//		} else if (vt.equals(VariableType.MEMBRANE) && !(cartesianMesh.isChomboMesh())) {
//			int insideVolumeIndex = cartesianMesh.getMembraneElements()[i].getInsideVolumeIndex();
//			int subvol1 =  cartesianMesh.getSubVolumeFromVolumeIndex(insideVolumeIndex);
//			int outsideVolumeIndex = cartesianMesh.getMembraneElements()[i].getOutsideVolumeIndex();
//			int subvol2 =  cartesianMesh.getSubVolumeFromVolumeIndex(outsideVolumeIndex);
//			if (varDomain != null &&
//				dataInfoProvider.getSimulationModelInfo().getMembraneName(subvol1, subvol2, true) != null &&
//				!dataInfoProvider.getSimulationModelInfo().getMembraneName(subvol1, subvol2, true).equals(varDomain.getName())) {
//				return false;
//			}
//		} else if (vt.equals(VariableType.MEMBRANE_REGION)) {
//			int[] subvols = cartesianMesh.getMembraneRegionMapSubvolumesInOut().get(i);
//			if (varDomain != null &&
//				dataInfoProvider.getSimulationModelInfo().getMembraneName(subvols[0], subvols[1], true) != null &&
//				!dataInfoProvider.getSimulationModelInfo().getMembraneName(subvols[0], subvols[1], true).equals(varDomain.getName())) {
//				return false;
//			}
//		}
//		return true;
//	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static enum IJGetDataParams {cachekey,varname,timeindex,jobindex}
	public static class ApiGetDataHandler extends AbstractHandler{
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
	    	System.out.println(target+"\n"+baseRequest.getQueryString());
//	    	List<NameValuePair> params = getParamsFromRequest(request);
	    	Long cacheKey = null;
	    	String[] decodedVarNames = null;
	    	int[] timeIndexes = null;
	    	int jobIndex = 0;//jobIndex 0 by default
	    	Map<String, String[]> parameterMap = request.getParameterMap();
	    	for(String param:parameterMap.keySet()) {
	    		if(param.equals(IJGetDataParams.cachekey.name())) {
	    			cacheKey = Long.valueOf(parameterMap.get(param)[0]);
	    		}else if(param.equals(IJGetDataParams.varname.name())) {
	    			decodedVarNames = parameterMap.get(param);//Values are already URLDecoded at this point
	    		}else if(param.equals(IJGetDataParams.timeindex.name())) {
	    			String[] timeIndexeStr = parameterMap.get(param);
	    			timeIndexes = new int[timeIndexeStr.length];
	    			for (int i = 0; i < timeIndexeStr.length; i++) {
						timeIndexes[i] = Integer.parseInt(timeIndexeStr[i]);
					}
	    		}else if(param.equals(IJGetDataParams.jobindex.name())) {
	    			jobIndex = Integer.valueOf(parameterMap.get(param)[0]);
	    		}

	    	}
	    	if(cacheKey == null) {
	    		response.setContentType("text/html; charset=utf-8");
	    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		response.getWriter().println("<h1>Expecting '"+GETINFO_PARMS+"' in request</h1>");
	    	}else {		        
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
			        								if(!ijSimInfo.isDB) {//quickrun/local sim
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
				        												if(Simulation.createSimulationID(new KeyValue(quickrunKey)).equals(ijSimInfo.simId)) {
				        													ijDataResponder = IJDataResponder.create(new KeyValue(quickrunKey),file.getParentFile().getParentFile(), jobIndex);
				        													break fileloop;
				        												}	
				        											}
				        							        	}
				        							        }
				        								}else {//saved sim
				        									IJDocType ijDocType = IJDocType.valueOf(ijModelInfo.type);
				        									if(ijDocType != IJDocType.bm && ijDocType != IJDocType.mm) {
				        							    		response.setContentType("text/html; charset=utf-8");
				        							    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				        							    		response.getWriter().println("<h1>\"Assumed IJ request for saved bio/math model, not expecting IJDocType='"+ijDocType.name()+"'\"</h1>");
				        							    		baseRequest.setHandled(true);
				        							    		return;
				        									}
				        									ijDataResponder = IJDataResponder.create(VCellClientTest.getVCellClient(), new KeyValue(ijModelInfo.modelkey), ijContextInfo.name/*null for mathmodel*/, ijSimInfo.name, jobIndex);
				        								}
			        								if(ijDataResponder != null) {
		        										if(decodedVarNames!=null/* && timepoint != null*/){
		        											ijDataResponder.respondData(response,decodedVarNames, timeIndexes,(ijContextInfo.geomDim > 0));
		        										}else {
		        											ijDataResponder.respondIdentifiers(response, ijSimInfo.name, cacheKey);
		        										}
		        										bFound = true;
		        										break outerloop;			        									
			        								}
	        									}catch(Exception e) {
	        										throw new ServletException("model='"+ijModelInfo.name+"'"+"context='"+ijContextInfo.name+"'"+"sim='"+ijSimInfo.name+"': "+e);
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
    		response.setContentType("text/plain; charset=utf-8");
    		response.setStatus(HttpServletResponse.SC_OK);
    		response.getWriter().write("VCellApi");
    		baseRequest.setHandled(true);

		}
		
	}

	@XmlRootElement
	public static class IJFieldData {
		@XmlAttribute
		public String varName;
		@XmlAttribute
		public int xsize;
		@XmlAttribute
		public int ysize;
		@XmlAttribute
		public int zsize;
		@XmlElement
		double[] data;
		public IJFieldData() {		
		}
		public IJFieldData(String varName,int xsize, int ysize, int zsize, double[] data) {
			super();
			this.varName = varName;
			this.xsize = xsize;
			this.ysize = ysize;
			this.zsize = zsize;
			this.data = data;
		}
	}

	@XmlRootElement
	private static class IJSolverStatus  {
		@XmlAttribute
		String simJobId;
		@XmlAttribute
		int statusCode;
		@XmlAttribute
		String statusName;
		@XmlAttribute
		String statusDetail;
		@XmlAttribute
		String statusMessage;
		public IJSolverStatus() {	
		}
		public IJSolverStatus(SolverStatus solverStatus,SimulationJob simJob) {
			super();
			this.simJobId = simJob.getSimulationJobID();
			this.statusCode = solverStatus.getStatus();
			this.statusName = solverStatus.toString().substring(0, solverStatus.toString().indexOf(':'));
			this.statusDetail = (solverStatus.getSimulationMessage()==null || solverStatus.getSimulationMessage().getDetailedState() ==null?null:solverStatus.getSimulationMessage().getDetailedState().name());
			this.statusMessage = (solverStatus.getSimulationMessage()==null?null:solverStatus.getSimulationMessage().getDisplayMessage());
		}
	}
	private static HashMap<String, Solver> solverCache = new HashMap<>();
	public static class ApiSolverHandler extends AbstractHandler{
//		private static final String R_DIFFUSION = "rDiffusion";
//		private static final String KR_BINDING = "krBinding";
//		private static final String KF_BINDING = "kfBinding";

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
	    	try {
	    		
    			ArrayList<AsynchClientTask> mathUpdateTasks = null;
	    		Simulation newsim = null;
	    		boolean[] bResponseHolder = new boolean[] {false};
	    		if(target.startsWith("/status")) {
	    			String[] vcSimIds = request.getParameterValues("vcSimId");
	    			if(vcSimIds == null || vcSimIds.length != 1) {
	    				generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "Expecting 1 'vcdid' to identify sim for status");
	    			}else {
	    				Solver solver = solverCache.get(vcSimIds[0]);
	    				if(solver != null) {
	    					generalResponse(bResponseHolder,baseRequest,response,HttpServletResponse.SC_OK,TYPE_TEXT_XML_UTF8,createXML(new IJSolverStatus(solver.getSolverStatus(),solver.getSimulationJob())));
		    				if(solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_ABORTED || solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_STOPPED || solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_FINISHED) {
		    					solverCache.remove(vcSimIds[0]);
		    				}
	    				}else {
	    					generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "No solver cached for status request="+request.toString());
	    				}
	    			}
	    		}else if(target.startsWith("/bycachekey")) {//vcDocument = (simContextName != null?vCellClient.getRequestManager().getDocumentManager().getBioModel(modelKey):vCellClient.getRequestManager().getDocumentManager().getMathModel(modelKey));
	    			if(VCellClientTest.getVCellClient() == null) {
	    				generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "Must be logged in to start solver");
	    			}else {
		    			String[] cachekeys = request.getParameterValues("cachekey");
		    			if(cachekeys == null || cachekeys.length != 1) {
		    				generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "Expecting 1 'cachekey' to identify sim to run");
		    				return;
		    			}
		    			Long cachekey = null;
		    			try {
							cachekey = Long.parseLong(cachekeys[0]);
						} catch (Exception e) {
							generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "Start simulation, trouble parsing 'cachekey': "+e.getMessage());
							return;
						}

    			    	if(ijModelInfoCache != null) {
    			    		modelInfoCacheLoop:
    			    		for(IJModelInfo ijModelInfo:ijModelInfoCache) {
    			    			if(ijModelInfo.contexts != null) {
    			    				for(IJContextInfo ijContextInfo:ijModelInfo.contexts) {
    			    					if(ijContextInfo.ijSimId != null) {
    			    						for(IJSimInfo ijSimInfo:ijContextInfo.ijSimId) {
    			    							if(ijSimInfo.cacheKey == cachekey) {
			    									if(ijContextInfo.name == null) {//mathmodel
			    										MathModel mathModel = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getMathModel(new KeyValue(ijModelInfo.modelkey));
														newsim = mathModel.getSimulation(ijSimInfo.name);
			    									}else {
			    										BioModel bioModel = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModel(new KeyValue(ijModelInfo.modelkey));
														newsim = bioModel.getSimulationContext(ijContextInfo.name).getSimulation(ijSimInfo.name);
			    									}
    			    								break modelInfoCacheLoop;
    			    							}
    			    						}
    			    					}
    			    				}
    			    			}
    			    		}
    			    	}
	    				if(newsim == null) {
	    					generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "Start simulation, couldn't find simulation for 'cachekey'= "+cachekey);
	    					return;
	    				}
	    				
//		    			BioModelInfo[] bioModelInfos = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModelInfos();
//		    			getout: //label this loop to jump out
//		    			for (BioModelInfo bioModelInfo : bioModelInfos) {
//							if(bioModelInfo.getVersion().getOwner().getName().equals("frm") && bioModelInfo.getVersion().getName().equals("Tutorial_FRAP")) {
//								BioModel frapBioModel = VCellClientTest.getVCellClient().getRequestManager().getDocumentManager().getBioModel(bioModelInfo);
//								Simulation[] simulations = frapBioModel.getSimulations();
//								for (Simulation simulation : simulations) {
//									if(simulation.getName().equals("FRAP")) {
//										newsim = simulation;
		    			
						Simulation clonedSimulation = (Simulation)BeanUtils.cloneSerializable(newsim);
						clonedSimulation.refreshDependencies();
						//Set any simulation parameter overrides
						MathOverrides clonedMathOverrides = clonedSimulation.getMathOverrides();
						String[] allConstantNames = clonedMathOverrides.getAllConstantNames();
						Map<String, String[]> parameterMap = request.getParameterMap();
						boolean bChanges = false;
						for(String param:parameterMap.keySet()) {
							for(String constantName:allConstantNames) {
								if(constantName.equals(param)) {
									Constant oldConstant = clonedMathOverrides.getConstant(constantName);
									Constant newConstant = new Constant(oldConstant.getName(), new Expression(parameterMap.get(param)[0]));
									clonedMathOverrides.putConstant(newConstant);
									bChanges = true;
								}
							}
						}
						if(bChanges) {
							clonedSimulation.refreshDependencies();
							newsim.setMathOverrides(new MathOverrides(newsim, clonedSimulation.getMathOverrides()));
						}
						
						//Set a new geometry if requested
						if("POST".equals(request.getMethod())){//Geometry being sent
							IJGeom ijGeom = (IJGeom)jaxbContext.createUnmarshaller().unmarshal(request.getInputStream());
							changeGeometry(newsim,ijGeom);											
						}
										
						//Set new initial conditions
						SimulationContext simulationContext = (SimulationContext)newsim.getSimulationOwner();
//										String[] laserCoverageExp = request.getParameterMap().get("laserCoverage");
//										if(laserCoverageExp != null) {
											SpeciesContextSpec[] speciesContextSpecs = simulationContext.getReactionContext().getSpeciesContextSpecs();
											for (SpeciesContextSpec speciesContextSpec : speciesContextSpecs) {
												System.out.println(speciesContextSpec);
												if(parameterMap.containsKey(speciesContextSpec.getSpeciesContext().getName())) {
//												if(speciesContextSpec.getSpeciesContext().getSpecies().getCommonName().equals("Laser") && speciesContextSpec.getSpeciesContext().getStructure().getName().equals("Nuc")) {
													System.out.println(speciesContextSpec.getInitialConcentrationParameter());
													System.out.println(speciesContextSpec.getInitialConditionParameter());
													System.out.println(speciesContextSpec.getInitialCountParameter());
							    					Expression expr = new Expression(parameterMap.get(speciesContextSpec.getSpeciesContext().getName())[0]);
//							    					SimulationSymbolTable sst = new SimulationSymbolTable(newsim, 0);
//							    					expr.bindExpression(sst);
													speciesContextSpec.getInitialConcentrationParameter().setExpression(expr);
													speciesContextSpec.getInitialConditionParameter().setExpression(expr);
//													break;
//													speciesContextSpec.refreshDependencies();
												}
											}
//											ClientTaskDispatcher.dispatchColl(null, new Hashtable<String, Object>(), ClientRequestManager.updateMath(null, simulationContext, true,NetworkGenerationRequirements.ComputeFullStandardTimeout), false);
//											Thread.sleep(10000);
//										}
										
										//Change simulation endtime
										String[] newSimEndTime = request.getParameterMap().get("newSimEndTime");
										if(newSimEndTime != null) {
											ClientTaskManager.changeEndTime(null, newsim.getSolverTaskDescription(), Double.parseDouble(newSimEndTime[0]));
										}
										
										//Update everything
						mathUpdateTasks = (ArrayList<AsynchClientTask>)ClientRequestManager.updateMath(null, simulationContext, false,NetworkGenerationRequirements.ComputeFullStandardTimeout);
//										break getout;
//									}
//								}
//							}
//						}
						if(newsim == null) {
							generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,TYPE_TEXT_PLAIN_UTF8, "Couldn't find simulation for 'frap' solver");
						}
//	    				if("POST".equals(request.getMethod())){//FieldData being sent
//	    					IJFieldData ijFieldData = (IJFieldData)jaxbContext.createUnmarshaller().unmarshal(request.getInputStream());
//	    					Constant overrideThis = newsim.getMathOverrides().getConstant(ijFieldData.varName);
//	    					if(overrideThis == null) {
//	    						generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "Couldn't find fieldData override varname="+ijFieldData.varName);
//	    					}else {
//		    					ExternalDataInfo externalDataInfo = LocalWorkspace.createNewExternalDataInfo(ResourceUtil.getLocalSimDir(User.tempUser.getName()),"frapBinding_"+System.currentTimeMillis());
//		    					DataSetControllerImpl dsci = new DataSetControllerImpl(null,ResourceUtil.getLocalRootDir(),null);
//		    					Geometry geom = ((SimulationContext)newsim.getSimulationOwner()).getGeometry();
//		    					FieldDataFileOperationResults fdfor = LocalWorkspace.saveExternalData(geom.getExtent(),geom.getOrigin(),
//		    						new ISize(ijFieldData.xsize, ijFieldData.ysize, ijFieldData.zsize),ijFieldData.data ,User.tempUser,
//		    						"prebleach", externalDataInfo.getExternalDataIdentifier(), dsci);
//		    					Expression expr = new Expression("vcField('"+externalDataInfo.getExternalDataIdentifier().getName()+"','prebleach',0.0,'Volume')");
//		    					SimulationSymbolTable sst = new SimulationSymbolTable(newsim, 0);
//		    					expr.bindExpression(sst);
//		    					Constant newFieldDataConstant = new Constant(overrideThis.getName(), expr);
////		    					private static final String[] ARGUMENT_NAMES = new String[]{"DatasetName","VariableName","Time","VariableType"};
//		    					newsim.getMathOverrides().putConstant(newFieldDataConstant);
//	    					}
//	    				}

	    			}
	    		}else if(target.startsWith("/sbml")) {
					String test = streamToString(request.getInputStream());
					ExternalDocInfo externalDocInfo = new ExternalDocInfo(test);
					XMLSource xmlSource = externalDocInfo.createXMLSource();
					org.jdom.Element rootElement = xmlSource.getXmlDoc().getRootElement();
					String xmlType = rootElement.getName();
					if (xmlType.equals(XMLTags.SbmlRootNodeTag)) {
						Namespace namespace = rootElement.getNamespace(XMLTags.SBML_SPATIAL_NS_PREFIX);
						boolean bIsSpatial = (namespace==null) ? false : true;
						TranslationLogger translationLogger = new TranslationLogger((VCellClientTest.getVCellClient() == null?null:VCellClientTest.getVCellClient().getMdiManager().getFocusedWindowManager()));
						VCDocument vcDoc = XmlHelper.importSBML(translationLogger, xmlSource, bIsSpatial);
				    	if(vcDoc instanceof BioModel){
					    	SimulationContext simContext = ((BioModel)vcDoc).getSimulationContext(0);
							MathMappingCallback callback = new MathMappingCallbackTaskAdapter(null);
							newsim = simContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX,callback,NetworkGenerationRequirements.AllowTruncatedStandardTimeout);
				    	}else if(vcDoc instanceof MathModel){
				    		newsim = ((MathModel)vcDoc).addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX);
				    	}else{
				    		generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "'solver' handler does not understand target '"+target+"' that results in "+vcDoc.getClass().getName());
				    	}
					}else {
						generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "'solver' handler does not understand target '"+target+"',"+"xmlType="+xmlType+" is expected to be "+XMLTags.SbmlRootNodeTag);
					}
	    		}else {
	    			generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_BAD_REQUEST,TYPE_TEXT_PLAIN_UTF8, "'solver' handler does not understand target '"+target+"'");
	    		}
//				List<NameValuePair> params = getParamsFromRequest(request);
	    		//if ("POST".equalsIgnoreCase(request.getMethod())) {
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

	    		final Simulation finalSim = newsim;
	    		AsynchClientTask startSolverTask = new AsynchClientTask("start solver...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,false) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(finalSim != null && !bResponseHolder[0]) {	
					    	SolverDescription solverDescription = finalSim.getSolverTaskDescription().getSolverDescription();
					    	if (solverDescription.equals(SolverDescription.FiniteVolumeStandalone)) {
					    		throw new IllegalArgumentException("Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step) solver not allowed for quick run of simulations.");
					    	}
					    	SolverUtilities.prepareSolverExecutable(solverDescription);	
					    	// create solver from SolverFactory
					    	SimulationTask simTask = new SimulationTask(new SimulationJob(finalSim, 0, null),0);
//					    	VCSimulationDataIdentifier vcSimulationDataIdentifier = simTask.getSimulationJob().getVCDataIdentifier();
					    	final File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());
					    	File[] files = localSimDataDir.listFiles();
					    	for (int i = 0; i < files.length; i++) {
								if(files[i].getName().startsWith(finalSim.getSimulationID())) {
									files[i].delete();
								}
							}
BioModel bioModel = ((SimulationContext)finalSim.getSimulationOwner()).getBioModel();
bioModel.clearVersion();
File outputFile = File.createTempFile("laser", ".xml");//new File("C:/temp/laser.xml");
outputFile.delete();
FileUtils.writeByteArrayToFile(XmlHelper.bioModelToXML(bioModel).getBytes(), outputFile);
					    	Thread.sleep(1000);
					    	Solver solver = SolverFactory.createSolver(localSimDataDir, simTask, false);						
							solver.startSolver();
					    	SolverStatus solverStatus =  solver.getSolverStatus();
					    	generalResponse(bResponseHolder,baseRequest,response,HttpServletResponse.SC_OK,TYPE_TEXT_XML_UTF8,createXML(new IJSolverStatus(solverStatus,solver.getSimulationJob())));
					    	solverCache.clear();
					    	solverCache.put(solver.getSimulationJob().getSimulationJobID(), solver);
						}
						
						if(!bResponseHolder[0]) {
							generalResponse(bResponseHolder,baseRequest,response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,TYPE_TEXT_PLAIN_UTF8, request.toString()+" was not handled");										
						}
						
					}
				};
				if(mathUpdateTasks != null) {
					Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
					mathUpdateTasks.add(startSolverTask);
					for (AsynchClientTask asynchClientTask : mathUpdateTasks) {
						asynchClientTask.run(hashTable);
					}
//					ClientTaskDispatcher.dispatchColl(null, new Hashtable<String, Object>(), mathUpdateTasks, false);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ServletException(e);
			}			
		}

		@XmlRootElement
		public static class IJGeom {
			@XmlElement
			String[] subvolumeNames;
			@XmlElement
			int[] subvolumePixelValue;
			@XmlAttribute
			public int xsize;
			@XmlAttribute
			public int ysize;
			@XmlAttribute
			public int zsize;
			@XmlElement
			double[] originXYZ;
			@XmlElement
			double[] extentXYZ;
			@XmlElement
			byte[] geom;
			public IJGeom() {
				
			}
//			public IJGeom(String[] subvolumeNames, int[] subvolumePixelValue, int xsize, int ysize, int zsize,double[] originXYZ, double[] extentXYZ, byte[] geom) throws Exception{
//				super();
//				if(originXYZ == null || extentXYZ == null || originXYZ.length != 3 || extentXYZ.length != 3) {
//					throw new Exception("origin and extent array size must be 3");
//				}
//				if(subvolumeNames == null || subvolumePixelValue == null || (subvolumeNames.length != subvolumePixelValue.length)) {
//					throw new Exception("subvolNames and pixelvalues arrays must be non-null and same length");
//				}
//				if(geom == null || geom.length != (xsize*ysize*zsize)) {
//					throw new Exception("x*y*z="+(xsize*ysize*zsize)+" not the same as geom length="+geom.length);
//				}
//
//				this.subvolumeNames = subvolumeNames;
//				this.subvolumePixelValue = subvolumePixelValue;
//				this.xsize = xsize;
//				this.ysize = ysize;
//				this.zsize = zsize;
//				this.originXYZ = originXYZ;
//				this.extentXYZ = extentXYZ;
//				this.geom = geom;
//			}
			public Extent getExtent() {
				return new Extent(extentXYZ[0],extentXYZ[1],extentXYZ[2]);
			}
			public Origin getOrigin() {
				return new Origin(originXYZ[0],originXYZ[1],originXYZ[2]);
			}
			public VCPixelClass[] getPixelVCPixelClasses() {
				ArrayList<VCPixelClass> pixelClassList = new ArrayList<>();
				for (int i = 0; i < subvolumeNames.length; i++) {
					pixelClassList.add(new VCPixelClass(null, subvolumeNames[i], subvolumePixelValue[i]));
				}
				return pixelClassList.toArray(new VCPixelClass[0]);
			}
			public ISize getISize() {
				return new ISize(xsize, ysize, zsize);
			}
		}

		public void changeGeometry(Simulation newsim,IJGeom ijGeom) throws Exception {
//			ISize simMeshSize = newsim.getMeshSpecification().getSamplingSize();
//			byte[] subvolumes = new byte[simMeshSize.getXYZ()];
//			for (int y = 0; y < simMeshSize.getY(); y++) {
//				if(y>10 && y < 20) {
//					for (int x = 0; x < simMeshSize.getX(); x++) {
//						if(x>10 && x <20) {
//							subvolumes[y*simMeshSize.getX() + x] = (byte)0x01;
//						}
//					}
//				}
//			}
			
			SimulationContext simulationContext = (SimulationContext)newsim.getSimulationOwner();
			StructureMapping[] origStructureMappings = simulationContext.getGeometryContext().getStructureMappings();
			VCImageUncompressed aVCImage = new VCImageUncompressed(null, ijGeom.geom,ijGeom.getExtent()/*newsim.getMeshSpecification().getGeometry().getExtent()*/ , ijGeom.xsize,ijGeom.ysize,ijGeom.zsize);
			aVCImage.setPixelClasses(ijGeom.getPixelVCPixelClasses());
//			SubVolume[] subVolumes4 = simulationContext.getGeometry().getGeometrySpec().getSubVolumes();
//			SubVolume[] subVolumes3 = subVolumes4;
//			SubVolume[] subVolumes2 = subVolumes3;
//			aVCImage.setPixelClasses(new VCPixelClass[] {
//					new VCPixelClass(subVolumes2[1].getKey(), subVolumes2[1].getName(), subVolumes2[1].getHandle()),
//					new VCPixelClass(subVolumes2[0].getKey(), subVolumes2[0].getName(), subVolumes2[0].getHandle())
//					});
			Geometry overrideGeom = new Geometry("overrideGeom", aVCImage);
			overrideGeom.getGeometrySpec().setOrigin(ijGeom.getOrigin()/*newsim.getMathDescription().getGeometry().getGeometrySpec().getOrigin()*/);
			
//										VCPixelClass[] vcpixelClasses = new VCPixelClass[numSampledVols];
//										ImageSubVolume vcImageSubVols[] = new ImageSubVolume[numSampledVols];
//										// get pixel classes for geometry
//										int idx = 0;
//										for (SampledVolume sVol: sampledVolumes) {
//											// from subVolume, get pixelClass?
//											final String name =  sVol.getDomainType();
//											final int pixelValue = SBMLUtils.ignoreZeroFraction( sVol.getSampledValue() );
//											VCPixelClass pc = new VCPixelClass(null, name,  pixelValue); 
//											vcpixelClasses[idx] = pc; 
//											// Create the new Image SubVolume - use index of this for
//											// loop as 'handle' for ImageSubVol?
//											ImageSubVolume isv = new ImageSubVolume(null, pc, idx);
//											isv.setName(name);
//											vcImageSubVols[idx++] = isv;
//										}
//										vcGeometry.getGeometrySpec().setSubVolumes(vcImageSubVols);

			
			
//			overrideGeom.precomputeAll(new GeometryThumbnailImageFactoryAWT());
//			overrideGeom.getGeometrySpec().setImage(aVCImage);//setSubVolumes(simulationContext.getGeometry().getGeometrySpec().getSubVolumes());
			overrideGeom.getGeometrySurfaceDescription().updateAll();
			simulationContext.setGeometry(overrideGeom);
			newsim.getMathDescription().setGeometry(overrideGeom);
			newsim.getMeshSpecification().setGeometry(overrideGeom);
			newsim.getMeshSpecification().setSamplingSize(ijGeom.getISize());
			
			//Create Structure mappings using names of subvolumes matching
			System.out.println("-----subvolumes");
			SubVolume[] svArr = simulationContext.getGeometry().getGeometrySpec().getSubVolumes();
			for (SubVolume subVolume : svArr) {
				System.out.println(subVolume);
			}
			System.out.println("-----structuremappings");
			StructureMapping[] newStructureMappings = simulationContext.getGeometryContext().getStructureMappings();
			for (StructureMapping structureMapping : newStructureMappings) {
				if(structureMapping.getGeometryClass() == null) {
					for (SubVolume subvolume: svArr) {
						if(subvolume.getName().toLowerCase().equals(structureMapping.getStructure().getName().toLowerCase())) {
//							structureMapping.setGeometryClass(subvolume);
							simulationContext.getGeometryContext().assignStructure(structureMapping.getStructure(), subvolume);
							break;
						}
					}
				}
				System.out.println(structureMapping);
			}
			
//			ArrayList<StructureMapping> structureMappings = new ArrayList<>();
//			structureMappings.add(new FeatureMapping(feature, simulationContext, argModelUnitSystem))
//			simulationContext.getGeometryContext().setStructureMappings(structureMappings.toArray(new StructureMapping[0]));
			
			simulationContext.getGeometryContext().refreshStructureMappings();
			
			
//			overrideGeom.refreshDependencies();
//			newsim.getMathDescription().refreshDependencies();
//			simulationContext.refreshDependencies();
		}
	}

	private static final String TYPE_TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";
	private static final String TYPE_TEXT_XML_UTF8 = "text/xml; charset=utf-8";
	private static void generalResponse(boolean[] bResponseHolder,Request request, HttpServletResponse response,int httpResponseCode,String contentType,String content) throws Exception{
		response.setContentType(contentType);
		response.setStatus(httpResponseCode);
		response.getWriter().write(content);
		request.setHandled(true);
		if(bResponseHolder != null) {
			bResponseHolder[0] = true;
		}
	}
	
	public static URI getServiceURI() {
		return imageJServer.getURI();
	}
	public static boolean serviceExists() {
		return imageJServer != null;
	}
	public static void stopService() throws Exception{
		if(imageJServer != null) {
			imageJServer.stop();
			imageJServer = null;
		}
	}
	private static Server imageJServer;
	private static final int IJSERVER_BEGIN_PORT_RANGE = 50000;
	private static final int IJSERVER_END_PORT_RANGE = 50100;
    public static void startService(Integer onlyThisPort) throws Exception{
    	if(ImageJHelper.serviceExists()) {
    		throw new Exception("ImageJServer already exists, only 1 allowed");
    	}
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

            ContextHandler contextSolver = new ContextHandler("/"+ApiEnum.solver.name()+"/");
            contextSolver.setHandler(new ApiSolverHandler());

            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[] { contextHello,contextRoot,context,contextData ,contextTimeSeries,contextSolver});
        for(int ijServerPort=IJSERVER_BEGIN_PORT_RANGE;ijServerPort<=IJSERVER_END_PORT_RANGE;ijServerPort++) {
            imageJServer = null;
            try {
            	imageJServer = new Server((onlyThisPort == null?ijServerPort:onlyThisPort));
            	imageJServer.setHandler(contexts);
            	imageJServer.start();
		        System.out.println(imageJServer.getURI());
				break;
			} catch (BindException e) {
				ImageJHelper.stopService();
				if(onlyThisPort != null) {
					e.printStackTrace();
					throw e;
				}
				System.out.println("VCellIJ service: port "+ijServerPort+" in use, continuing search...");
			}
        }
    }
}
