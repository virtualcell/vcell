/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.Executable;
import org.vcell.util.ExecutableStatus;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.Range;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import GIFUtils.GIFFormatException;
import GIFUtils.GIFImage;
import GIFUtils.GIFOutputStream;
import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import cbit.image.ImagePaneModel;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.gloworm.atoms.UserDataEntry;
import cbit.vcell.export.gloworm.quicktime.MediaMethods;
import cbit.vcell.export.gloworm.quicktime.MediaMovie;
import cbit.vcell.export.gloworm.quicktime.MediaTrack;
import cbit.vcell.export.gloworm.quicktime.ObjectMediaChunk;
import cbit.vcell.export.gloworm.quicktime.VRMediaChunk;
import cbit.vcell.export.gloworm.quicktime.VRMediaMovie;
import cbit.vcell.export.gloworm.quicktime.VRWorld;
import cbit.vcell.export.gloworm.quicktime.VideoMediaChunk;
import cbit.vcell.export.gloworm.quicktime.VideoMediaSample;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.util.AmplistorUtils;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2004 1:28:34 PM)
 * @author: Ion Moraru
 */
public class IMGExporter implements ExportConstants {
	
	private static final int FULL_MODE_ALL_SLICES = -1;

	private ExportServiceImpl exportServiceImpl = null;
/**
 * Insert the method's description here.
 * Creation date: (4/27/2004 1:18:37 PM)
 * @param exportServiceImpl cbit.vcell.export.server.ExportServiceImpl
 */
public IMGExporter(ExportServiceImpl exportServiceImpl) {
	this.exportServiceImpl = exportServiceImpl;
}

public static void main(String [] args) throws Exception{
	if(args.length < 7){
		System.out.println("Usage: IMGExporter username userkey simulationkey userdatadir beginTimeIndex endTimeIndex {varName1 varName2 ...}");
		System.exit(0);
	}
	String userName = args[0];
	String userKey = args[1];
	String SimulationKey = args[2];
	String primaryDirStr = args[3];
	int beginTimeIndex = Integer.valueOf(args[4]);
	int endTimeIndex = Integer.valueOf(args[5]);
	String[] varNames = new String[args.length-6];
	if(args.length > 6){
		for (int i = 6; i < args.length; i++) {
			varNames[i-6] = args[i];
		}
	}
	PropertyLoader.loadProperties();
	
	User user = new User(userName, new KeyValue(userKey));
	VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(new KeyValue(SimulationKey), user);
	VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID, 0);
	
	StdoutSessionLog sessionLog = new StdoutSessionLog(user.getName());
	class PrintingExportServiceImpl extends ExportServiceImpl {
		public PrintingExportServiceImpl(SessionLog sessionLog){
			super(sessionLog);
		}
		@Override
		protected void fireExportEvent(ExportEvent event) {
			super.fireExportEvent(event);
			System.out.println("Event type="+event.getEventTypeID()+" JobID="+event.getJobID()+" progress="+event.getProgress());
		}
	}
	ExportServiceImpl exportServiceImpl = new PrintingExportServiceImpl(sessionLog);
	Cachetable cachetable = new Cachetable(10*Cachetable.minute);
	File primaryDir = new File(primaryDirStr);
	DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(sessionLog,cachetable,primaryDir,null);
	DataServerImpl dataServerImpl = new DataServerImpl(sessionLog, dataSetControllerImpl, exportServiceImpl);
	double[] allTimes = dataSetControllerImpl.getDataSetTimes(vcdID);
	TimeSpecs timeSpecs = new TimeSpecs(beginTimeIndex, endTimeIndex, allTimes, ExportConstants.TIME_RANGE);
	VariableSpecs variableSpecs = new VariableSpecs(varNames, ExportConstants.VARIABLE_MULTI);
	GeometrySpecs geometrySpecs = new GeometrySpecs(null, 0, 0, ExportConstants.GEOMETRY_SLICE);
	DisplayPreferences displayPreferences =
		new DisplayPreferences(DisplayAdapterService.BLUERED, new Range(0,1), DisplayAdapterService.createBlueRedSpecialColors());
	MovieSpecs movieSpecs = new MovieSpecs(
		1000.0, false, new DisplayPreferences[] {displayPreferences}, ExportFormat.FORMAT_JPEG, 0, 1, 1, 1,///
		ImagePaneModel.MESH_MODE, FormatSpecificSpecs.CODEC_JPEG, 1.0f, false,FormatSpecificSpecs.PARTICLE_SELECT);
	ExportSpecs exportSpecs = new ExportSpecs(vcdID, ExportFormat.QUICKTIME, variableSpecs, timeSpecs, geometrySpecs, movieSpecs,"IMGExporterTest",null);
	exportServiceImpl.makeRemoteFile(null, user, dataServerImpl, exportSpecs);
}

private static class ParticleInfo{
	public File imageFrameDir;
	public ParticleInfo(File imageFrameDir){
		this.imageFrameDir = imageFrameDir;
	}
	public File getImageFrameDir(){
		return imageFrameDir;
	}
	public Dimension getImageFrameSize(VCDataIdentifier vcDataID){
		File[] imageFrameDirList = imageFrameDir.listFiles();
		for (int i = 0; i < imageFrameDirList.length; i++) {
			if(imageFrameDirList[i].getName().startsWith(vcDataID.getID()) &&
				imageFrameDirList[i].getName().endsWith(".jpeg")){
				ImageIcon imgIcon = new ImageIcon(imageFrameDirList[i].getAbsolutePath());
				return new Dimension(imgIcon.getIconWidth(), imgIcon.getIconHeight());
			}
		}
		return null;
	}
}
/**
 * This method was created in VisualAge.
 */
public ExportOutput[] makeMediaData(
		OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs,ClientTaskStatusSupport clientTaskStatusSupport,FileDataContainerManager fileDataContainerManager)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	int particleMode = FormatSpecificSpecs.PARTICLE_NONE;
	if(exportSpecs.getFormatSpecificSpecs() instanceof ImageSpecs){
		particleMode = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getParticleMode();
	}else if (exportSpecs.getFormatSpecificSpecs() instanceof MovieSpecs){
		particleMode = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getParticleMode();
	}
	ExportOutput[] mediaArr = null;
	if(particleMode == FormatSpecificSpecs.PARTICLE_NONE){
		mediaArr = makeMedia(exportServiceImpl,outputContext,jobRequest.getJobID(),user,dataServerImpl,exportSpecs,clientTaskStatusSupport,null/*particleInfo*/,fileDataContainerManager);
	}else if(particleMode == FormatSpecificSpecs.PARTICLE_SELECT){
	//	ParticleInfo particleInfo = checkParticles_unused(exportSpecs,user,dataServerImpl,jobRequest.getJobID());
		try{
			File[] smoldynparticleCoordinateFiles = getParticleFiles(exportSpecs, user, dataServerImpl);
			if(smoldynparticleCoordinateFiles != null && smoldynparticleCoordinateFiles.length > 0){
				ArrayList<ExportOutput> expOutputs = new ArrayList<>();
//				expOutputs.addAll(Arrays.asList(mediaArr));
				for (int i = 0; i < smoldynparticleCoordinateFiles.length; i++) {
					ExportOutput pointExportOutput = new ExportOutput(true, ""/*".smoldynOutput"*/, smoldynparticleCoordinateFiles[i].getName()/*exportSpecs.getVCDataIdentifier().getID()*/, ""/*"_points"*/, fileDataContainerManager);
					byte[] particleCoordsText = FileUtils.readByteArrayFromFile(smoldynparticleCoordinateFiles[i]);
					fileDataContainerManager.append(pointExportOutput.getFileDataContainerID(), particleCoordsText);
					expOutputs.add(pointExportOutput);
				}
				mediaArr = expOutputs.toArray(new ExportOutput[0]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}else{
		throw new IllegalArgumentException("Unknown FormatSpecificSpecs.PARTICLE flag="+particleMode);
	}
	return mediaArr;
}

private File[] getParticleFiles(ExportSpecs exportSpecs,User user,DataServerImpl dataServerImpl) throws Exception{
//	int particleMode = FormatSpecificSpecs.PARTICLE_NONE;
//	if(exportSpecs.getFormatSpecificSpecs() instanceof ImageSpecs){
//		particleMode = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getParticleMode();
//	}else if (exportSpecs.getFormatSpecificSpecs() instanceof MovieSpecs){
//		particleMode = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getParticleMode();
//	}
//	if(particleMode == FormatSpecificSpecs.PARTICLE_NONE){
//		return null;
//	}
	
	final VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	SimulationData.SimDataAmplistorInfo simDataAmplistorInfo = AmplistorUtils.getSimDataAmplistorInfoFromPropertyLoader();
	SimulationData simData = new SimulationData(vcdID,
			new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),vcdID.getOwner().getName()),
			new File(PropertyLoader.getProperty(PropertyLoader.primarySimDataDirProperty,null),vcdID.getOwner().getName()),
			simDataAmplistorInfo);
		
	File logFile = simData.getLogFile();
	if(!logFile.exists()){
		throw new Exception("ImgExport particle, Couldn't find Log file "+logFile.getAbsolutePath());
	}
	ArrayList<File> smoldynPointFiles = new ArrayList<>();
	int timeIndex = 1;//smoldyn always begins at timeindex 1
	while(true){
		File smoldynOutFile = simData.getSmoldynOutputFile(timeIndex);
		if(!smoldynOutFile.exists()){//get smoldynOutput files
			break;
		}else{
			smoldynPointFiles.add(smoldynOutFile);
		}
		timeIndex++;
	}
	if(smoldynPointFiles.size() == 0){
		return null;
	}
	return smoldynPointFiles.toArray(new File[0]);
}

private ParticleInfo checkParticles_unused(final ExportSpecs exportSpecs,User user,DataServerImpl dataServerImpl,final long jobID) throws Exception{
	int particleMode = FormatSpecificSpecs.PARTICLE_NONE;
	if(exportSpecs.getFormatSpecificSpecs() instanceof ImageSpecs){
		particleMode = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getParticleMode();
	}else if (exportSpecs.getFormatSpecificSpecs() instanceof MovieSpecs){
		particleMode = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getParticleMode();
	}
	if(particleMode == FormatSpecificSpecs.PARTICLE_NONE){
		return null;
	}
	
	final VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	CartesianMesh cartesianMesh = dataServerImpl.getMesh(user, vcdID);
	int dimension = cartesianMesh.getGeometryDimension();
	
	String[] variableNames = exportSpecs.getVariableSpecs().getVariableNames();
	
	File visitExeLocation =
		new File(PropertyLoader.getRequiredProperty(PropertyLoader.visitSmoldynVisitExecutableProperty));
	File visitSmoldynScriptLocation =
		new File(PropertyLoader.getRequiredProperty(PropertyLoader.visitSmoldynScriptPathProperty));
	final File visitSmoldynScriptTempDir = PropertyLoader.getSystemTemporaryDirectory();

	//-----Get all data (from archive if necessary)
	SimulationData.SimDataAmplistorInfo simDataAmplistorInfo = AmplistorUtils.getSimDataAmplistorInfoFromPropertyLoader();
	SimulationData simData = new SimulationData(vcdID,
			new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),vcdID.getOwner().getName()),
			new File(PropertyLoader.getProperty(PropertyLoader.primarySimDataDirProperty,null),vcdID.getOwner().getName()),
			simDataAmplistorInfo);
		
	File logFile = simData.getLogFile();
	if(!logFile.exists()){
		throw new Exception("ImgExport particle, Couldn't find Log file "+logFile.getAbsolutePath());
	}
	simData.getMesh();//gets mesh and meshmetrics files from archive if necessary
	simData.getSubdomainFile();
	simData.getFunctionsFile(false);
	int timeIndex = 1;//smoldyn always begins at timeindex 1
	while(true){
		if(!simData.getSmoldynOutputFile(timeIndex).exists()){//get smoldynOutput files
			break;
		}
		timeIndex++;
	}
	//-----
	
	File visitDataPathFragment = new File(logFile.getParent(),vcdID.getID()+"_");
	System.out.println(visitExeLocation.getAbsolutePath());
	System.out.println(visitSmoldynScriptLocation.getAbsolutePath());
	System.out.println(visitSmoldynScriptTempDir.getAbsolutePath());
	System.out.println(visitDataPathFragment.getAbsolutePath());
	
//if(true){return new ParticleInfo(visitSmoldynScriptTempDir);}
	if(exportSpecs.getTimeSpecs().getAllTimes().length == 1){
		throw new IllegalArgumentException("Time zero not valid for smoldyn particle data");
	}
	int beginIndexTime = exportSpecs.getTimeSpecs().getBeginTimeIndex();
	int endIndexTime = exportSpecs.getTimeSpecs().getEndTimeIndex();
	
	System.out.println("beginIndexTime="+beginIndexTime+" endIndexTime="+endIndexTime);
	
	ArrayList<String> args = new ArrayList<String>();
	args.add(visitExeLocation.getAbsolutePath());//location of visit
	args.add("-nowin");
	args.add("-cli");
	args.add("-s");
	
	args.add(visitSmoldynScriptLocation.getAbsolutePath());//location of the script
	args.add(visitDataPathFragment.getAbsolutePath()); //location of the SimID 
	args.add(visitSmoldynScriptTempDir.getAbsolutePath());  // where frames are dumped 
	args.add(dimension+""); //dimension
	args.add(beginIndexTime+"");
	args.add(endIndexTime+"");
	args.add(4+"");//particle sphere size
	args.add(FormatSpecificSpecs.SMOLDYN_DEFAULT_FRAME_SIZE.width+"");//frame size X
	args.add(FormatSpecificSpecs.SMOLDYN_DEFAULT_FRAME_SIZE.height+"");//frame size Y
	args.add(variableNames.length+""); // 0 = show all the particles.  >0 == show n different particles, to be listed below
	for (int i = 0; i < variableNames.length; i++) {
		args.add(variableNames[i]);
	}
	
	final String tempFilePrefix = vcdID.getID()+"_p3d";
	
	//Monitor progress of smoldyn script
	exportServiceImpl.fireExportProgress(jobID, vcdID, "MEDIA", 0.0);
	final boolean[] finishedFlag = new boolean[] {false};
	final int numTimePoints = exportSpecs.getTimeSpecs().getEndTimeIndex()-exportSpecs.getTimeSpecs().getBeginTimeIndex()+1;
	Thread progressThread = new Thread(new Runnable() {
		public void run() {
			System.out.println("smoldyn progress monitor started");
			while(!finishedFlag[0]){
				int count = 0;
				File[] tempFiles = visitSmoldynScriptTempDir.listFiles();
				for (int i = 0; i < tempFiles.length; i++) {
					if(tempFiles[i].getName().startsWith(tempFilePrefix) ||
						(tempFiles[i].getName().startsWith(vcdID.getID()) &&
								tempFiles[i].getName().endsWith(".jpeg"))){
						count+= 1;
					}
				}
				double progress = .5 * (double)count / (double)(2*numTimePoints);
				exportServiceImpl.fireExportProgress(jobID, vcdID, "MEDIA", progress);
				//System.out.println("All files read="+tempFiles.length+" Files counted="+count+" progress="+progress);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;//This shouldn't happen but if so just quit
				}
			}
			System.out.println("smoldyn progress monitor finished");
		}
	});
	progressThread.start();
	
	
	try{
		final long TIME_OUT = 1200000; //20 minutes
		Executable executable = new Executable(args.toArray(new String[0]),TIME_OUT);
		executable.start();//blocking, internal monitoring, return after timeout
		if(!executable.getStatus().equals(ExecutableStatus.COMPLETE)){
			System.out.println(executable.getStderrString());
			System.out.println(executable.getStdoutString());
			throw new Exception("Particle data exporter did not complete normally. "+(executable.getStatus()==null?"":executable.getStatus().toString()));
		}
	}finally{
		finishedFlag[0] = true;
		//Remove temp files created by smoldyn script
		File[] tempFiles = visitSmoldynScriptTempDir.listFiles();
		for (int i = 0; i < tempFiles.length; i++) {
			if(tempFiles[i].getName().startsWith(tempFilePrefix)){
				tempFiles[i].delete();
			}
		}
	}
	return new ParticleInfo(visitSmoldynScriptTempDir);
}

private static ExportOutput[] makeMedia(ExportServiceImpl exportServiceImpl,
		OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		ExportSpecs exportSpecs,ClientTaskStatusSupport clientTaskStatusSupport,ParticleInfo particleInfo,FileDataContainerManager fileDataContainerManager)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	boolean bOverLay = false;
	int sliceIndicator = 0;
	if(particleInfo == null){
		sliceIndicator = (exportSpecs.getGeometrySpecs().getModeID() == ExportConstants.GEOMETRY_FULL?FULL_MODE_ALL_SLICES:exportSpecs.getGeometrySpecs().getSliceNumber());
	}
	int imageScale = 0;
	int meshMode = 0;
	int mirroringType = 0;
	int membraneScale = 0;
	double duration = 1.0;
	DisplayPreferences[] displayPreferences = null;
	int volVarMembrOutlineThickness = 1;
	if(exportSpecs.getFormatSpecificSpecs() instanceof ImageSpecs){
		bOverLay = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getOverlayMode();
		imageScale = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling();
		volVarMembrOutlineThickness = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getVolVarMembrOutlineThickness();
		meshMode = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode();
		mirroringType = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType();
		membraneScale = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling();
		displayPreferences = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences();
		duration = (double)((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration()/1000.0;//convert from milliseconds to seconds
	}else if (exportSpecs.getFormatSpecificSpecs() instanceof MovieSpecs){
		bOverLay = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getOverlayMode();
		imageScale = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling();
		volVarMembrOutlineThickness = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getVolVarMembrOutlineThickness();
		meshMode = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode();
		mirroringType = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType();
		membraneScale = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling();
		displayPreferences = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences();
		duration = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration()/1000.0;//convert from milliseconds to seconds
	}else{
		throw new DataFormatException("Unknown FormatSpecificSpec "+exportSpecs.getFormatSpecificSpecs().getClass().getName());
	}

	Vector<ExportOutput> exportOutputV = new Vector<ExportOutput>();
	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	
	int beginTimeIndex = exportSpecs.getTimeSpecs().getBeginTimeIndex();
	int endTimeIndex = exportSpecs.getTimeSpecs().getEndTimeIndex();
	boolean bSingleTimePoint = beginTimeIndex==endTimeIndex;
	String[] varNames = (particleInfo == null?exportSpecs.getVariableSpecs().getVariableNames():new String[] {"smoldynParticleDummy"});

	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	int startSlice = (sliceIndicator==FULL_MODE_ALL_SLICES?0:sliceIndicator);
	int sliceCount = FormatSpecificSpecs.getSliceCount(sliceIndicator==FULL_MODE_ALL_SLICES,exportSpecs.getGeometrySpecs().getAxis(), dataServerImpl.getMesh(user, vcdID));
	double progressIncr = 1.0/(sliceCount*(endTimeIndex - beginTimeIndex + 1)*varNames.length);
	double progress = 0.0;
	MovieHolder movieHolder = new MovieHolder();
	Dimension imageDimension = FormatSpecificSpecs.getImageDimension(meshMode,imageScale,dataServerImpl.getMesh(user, vcdID),exportSpecs.getGeometrySpecs().getAxis());
	int originalWidth = (int)imageDimension.getWidth();
	int originalHeight = (int)imageDimension.getHeight();
	ExportRenderInfo exportRenderInfo = null;
try{
for (int sliceNumber = startSlice; sliceNumber < startSlice+sliceCount; sliceNumber++) {
	if(particleInfo == null){
		PDEOffscreenRenderer offScreenRenderer = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
		offScreenRenderer.setNormalAxis(exportSpecs.getGeometrySpecs().getAxis());
		offScreenRenderer.setSlice(sliceNumber);
		exportRenderInfo = new ExportRenderInfo(offScreenRenderer);
	}else{
		exportRenderInfo = new ExportRenderInfo(particleInfo, allTimes, vcdID,beginTimeIndex);
		Dimension particleImageSize = particleInfo.getImageFrameSize(vcdID);
		originalWidth = particleImageSize.width;
		originalHeight = particleImageSize.height;
	}
	
	int varNameIndex0 = 0;
	int timeIndex0 = beginTimeIndex;
	int[] overLayPixels = null;
	movieHolder.setSampleDurationSeconds(duration);//set default time if only 1 timepoint
	boolean bEndslice = sliceNumber == (startSlice+sliceCount-1);
	while(true){
		if(clientTaskStatusSupport != null){
			clientTaskStatusSupport.setProgress((int)(progress*100));
			if(clientTaskStatusSupport.isInterrupted()){
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
		exportServiceImpl.fireExportProgress(jobID, vcdID, "MEDIA", (particleInfo==null?progress:.5+(progress/2.0)));
		progress+= progressIncr;
		MirrorInfo currentSliceTimeMirrorInfo =
			renderAndMirrorSliceTimePixels(exportRenderInfo, varNames[varNameIndex0], allTimes[timeIndex0],
				displayPreferences[varNameIndex0],imageScale, membraneScale,
				meshMode, volVarMembrOutlineThickness,originalWidth, originalHeight, mirroringType);
		if(bOverLay){
			if(varNames.length == 1){
				overLayPixels = currentSliceTimeMirrorInfo.getPixels();
			}else{
				//Overlay append in Y-direction
				if(overLayPixels == null){
					overLayPixels = new int[currentSliceTimeMirrorInfo.getPixels().length*varNames.length];
				}
				int appendIndex = currentSliceTimeMirrorInfo.getPixels().length*varNameIndex0;
				System.arraycopy(currentSliceTimeMirrorInfo.getPixels(), 0, overLayPixels, appendIndex, currentSliceTimeMirrorInfo.getPixels().length);
			}
		}
		if (timeIndex0 != endTimeIndex){
			//calculate duration for each timepoint
			movieHolder.setSampleDurationSeconds((allTimes[timeIndex0 + 1] - allTimes[timeIndex0]) / (allTimes[endTimeIndex+(endTimeIndex==allTimes.length-1?0:1)] - allTimes[beginTimeIndex]) * duration);
		}else{
			//when last or only 1 timepoint, use last duration set
			movieHolder.setSampleDurationSeconds(movieHolder.getSampleDurationSeconds());
		}
		//Index var and time properly
		boolean bBegintime = timeIndex0==beginTimeIndex;
		boolean bEndTime = timeIndex0==endTimeIndex;
		if(bOverLay){
			varNameIndex0++;
			if(varNameIndex0==varNames.length){
				String dataID = createDataID(exportSpecs, sliceNumber, "overlay", timeIndex0);
				createMedia(exportOutputV, vcdID, dataID, exportSpecs,true,bEndslice,bBegintime,bEndTime, bSingleTimePoint, varNames,displayPreferences,movieHolder, overLayPixels, currentSliceTimeMirrorInfo.getMirrorWidth(), currentSliceTimeMirrorInfo.getMirrorHeight()*varNames.length,fileDataContainerManager);
				varNameIndex0 = 0;
				timeIndex0++;
				if(timeIndex0>endTimeIndex){
					break;
				}
			}
		}else{
			String dataID = createDataID(exportSpecs, sliceNumber, varNames[varNameIndex0], timeIndex0);
			boolean bEndVars = varNameIndex0 == varNames.length-1;
			createMedia(exportOutputV, vcdID, dataID, exportSpecs,bEndVars,bEndslice,bBegintime, bEndTime, bSingleTimePoint, new String[] {varNames[varNameIndex0]},new DisplayPreferences[] {displayPreferences[varNameIndex0]},movieHolder, currentSliceTimeMirrorInfo.getPixels(), currentSliceTimeMirrorInfo.getMirrorWidth(), currentSliceTimeMirrorInfo.getMirrorHeight(),fileDataContainerManager);
			timeIndex0++;
			if(timeIndex0>endTimeIndex){
				timeIndex0 = beginTimeIndex;
				varNameIndex0++;
				if(varNameIndex0 == varNames.length){
					break;
				}
			}
		}
	}
	}
}finally{
	if(exportRenderInfo != null){
		exportRenderInfo.cleanup();
	}
}
	return exportOutputV.toArray(new ExportOutput[0]);
}
	private static class MirrorInfo {
		private int[] pixels;
		private int mirrorHeight;
		private int mirrorWidth;
		public MirrorInfo(int[] pixels,int mirrorHeight,int mirrorWidth){
			this.pixels = pixels;
			this.mirrorWidth = mirrorWidth;
			this.mirrorHeight = mirrorHeight;
		}
		public int[] getPixels() {
			return pixels;
		}
		public int getMirrorHeight() {
			return mirrorHeight;
		}
		public int getMirrorWidth() {
			return mirrorWidth;
		}
	}
	
	private static class ExportRenderInfo {
		private PDEOffscreenRenderer pdeOffscreenRenderer;
		private ParticleInfo particleInfo;
		private double[] allTimes;
		private String varName;
		private double timePoint;
		private DisplayPreferences displayPreferences;
		private VCDataIdentifier vcDataID;
		private File[] imageFrames;
		private int beginIndexTime;
		
		public ExportRenderInfo(PDEOffscreenRenderer pdeOffscreenRenderer){
			this.pdeOffscreenRenderer = pdeOffscreenRenderer;
		}
		public ExportRenderInfo(ParticleInfo particleInfo,double[] allTimes,final VCDataIdentifier vcDataID,int beginIndexTime){
			this.particleInfo = particleInfo;
			this.allTimes = allTimes;
			this.vcDataID = vcDataID;
			this.beginIndexTime = beginIndexTime;
			TreeSet<File> imageFramesTreeSet = new TreeSet<File>(new Comparator<File>() {
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			
			File[] imageFrameDirList = particleInfo.getImageFrameDir().listFiles();
			for (int i = 0; i < imageFrameDirList.length; i++) {
				if(imageFrameDirList[i].getName().startsWith(vcDataID.getID()) &&
					imageFrameDirList[i].getName().endsWith(".jpeg")){
					imageFramesTreeSet.add(imageFrameDirList[i]);
				}
			}
			imageFrames = imageFramesTreeSet.toArray(new File[0]);
		}
		public void cleanup(){
			if(imageFrames == null){
				return;
			}
			//Remove files created by Visit
			for (int i = 0; i < imageFrames.length; i++) {
				imageFrames[i].delete();
			}
		}
		public void setVarAndTimeAndDisplay(String varName,double timePoint, DisplayPreferences displayPreference) throws DataAccessException{
			this.varName = varName;
			this.timePoint = timePoint;
			this.displayPreferences = displayPreferences;
			if(pdeOffscreenRenderer != null){
				pdeOffscreenRenderer.setVarAndTimeAndDisplay(varName,timePoint, displayPreference);
			}
		}
		public int[] getPixelsRGB(int imageScale,int membraneScaling,int meshMode,int volVarMembrOutlineThickness) throws Exception{
			if(pdeOffscreenRenderer != null){
				return pdeOffscreenRenderer.getPixelsRGB(imageScale,membraneScaling,meshMode,volVarMembrOutlineThickness);
			}else{
				int timeIndex = -1;
				for (int i = 0; i < allTimes.length; i++) {
					if(allTimes[i] == timePoint){
						timeIndex = i;
						break;
					}
				}
				if(timeIndex == -1){
					throw new DataAccessException("TimePoint "+timePoint+" not found");
				}
//				System.out.println("Found index "+timeIndex+" for time "+timePoint);
//				System.out.println("Reading "+imageFrames[timeIndex].getAbsolutePath());
				
				
				BufferedImage bufferedImage = ImageIO.read(imageFrames[timeIndex-beginIndexTime]);
				int[] pixels = new int[bufferedImage.getWidth()*bufferedImage.getHeight()];
				int index = 0;
				for (int y = 0; y < bufferedImage.getWidth(); y++) {
					for (int x = 0; x < bufferedImage.getHeight(); x++) {
						pixels[index] = bufferedImage.getRGB(x,y);
						index++;
					}
				}
				
//				ImageIcon imgIcon = new ImageIcon(imageFrames[timeIndex].getAbsolutePath());//provides observer
//				BufferedImage bufferedImage = new BufferedImage(imgIcon.getIconWidth(), imgIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
//				boolean bDrawStatus =
//					bufferedImage.getGraphics().drawImage(imgIcon.getImage(), imgIcon.getIconWidth(), imgIcon.getIconHeight(), imgIcon.getImageObserver());
//				System.out.println("bDrawStatus ImageIcon->BufferedImage="+bDrawStatus);
//				try{
//					String tempName = "temp_"+timeIndex+".jpg";
//					File tempFile = new File(imageFrames[timeIndex].getParentFile(),tempName);
//					ImageIO.write(bufferedImage, "jpg", tempFile);
//					System.out.println("Wrote temp jpg "+tempFile.getAbsolutePath());
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				int[] pixels = ((DataBufferInt)(bufferedImage.getRaster().getDataBuffer())).getData();
//				boolean bAllZero = true;
//				for (int i = 0; i < pixels.length; i++) {
//					if(pixels[i] != 0){
//						bAllZero = false;
//					}
//				}
//				System.out.println("Had all zeros="+bAllZero);
				return pixels;
			}
		}
	};
	
private static MirrorInfo renderAndMirrorSliceTimePixels(
		ExportRenderInfo exportRenderInfo,String varName,double timePoint,DisplayPreferences displayPreference,
		int imageScale,int membraneScaling,int meshMode,int volVarMembrOutlineThickness,
		int originalWidth,int originalHeight,int mirroringType) throws Exception{
	exportRenderInfo.setVarAndTimeAndDisplay(varName,timePoint, displayPreference);
	int[] pixels = exportRenderInfo.getPixelsRGB(imageScale,membraneScaling,meshMode,volVarMembrOutlineThickness);
	pixels = ExportUtils.extendMirrorPixels(pixels,originalWidth,originalHeight, mirroringType);
	
	Dimension mirrorDim = FormatSpecificSpecs.getMirrorDimension(mirroringType, originalWidth, originalHeight);

	return new MirrorInfo(pixels, mirrorDim.height, mirrorDim.width);

}
private static String create4DigitNumber(int number){
	return (number<1000?"0":"")+(number<100?"0":"")+(number<10?"0":"") + number;
}
private static String create3DigitNumber(int number){
	return (number<100?"0":"")+(number<10?"0":"") + number;
}
private static String createDataID(ExportSpecs exportSpecs,int sliceNumber,String varName,int timeIndex){
	int sliceNormalAxis = exportSpecs.getGeometrySpecs().getAxis();
	if(exportSpecs.getFormat() == ExportFormat.ANIMATED_GIF){
		return "_" + Coordinate.getNormalAxisPlaneName(sliceNormalAxis) + "_" + create3DigitNumber(sliceNumber) + "_" + varName + "_animated";
	}else if (exportSpecs.getFormat() == ExportFormat.QUICKTIME){
		return "_" + Coordinate.getNormalAxisPlaneName(sliceNormalAxis) + "_" + create3DigitNumber(sliceNumber) + "_" + varName;
	}else{
		return "_" + Coordinate.getNormalAxisPlaneName(sliceNormalAxis) + "_" + create3DigitNumber(sliceNumber) + "_" + varName +"_" + create4DigitNumber(timeIndex);		
	}

}
private static class MovieHolder{
	private Hashtable<String, Vector<VideoMediaChunk>> varNameVideoMediaChunkHash = new Hashtable<String, Vector<VideoMediaChunk>>();
	private Hashtable<String, String> varNameDataIDHash = new Hashtable<String, String>();
	private GIFImage gifImage;
	private double sampleDurationSeconds;
	public void setSampleDurationSeconds(double sampleDurationSeconds){
		this.sampleDurationSeconds = sampleDurationSeconds;
	}
	public double getSampleDurationSeconds(){
		return sampleDurationSeconds;
	}
	public void setGifImage(GIFImage gifImage){
		this.gifImage = gifImage;
	}
	public GIFImage getGifImage(){
		return gifImage;
	}
	public Hashtable<String, Vector<VideoMediaChunk>> getVarNameVideoMediaChunkHash(){
		return varNameVideoMediaChunkHash;
	}
	public Hashtable<String, String> getVarNameDataIDHash(){
		return varNameDataIDHash;
	}
 }

private static void createMedia(Vector<ExportOutput> exportOutputV,VCDataIdentifier vcdID,String dataID,
		ExportSpecs exportSpecs,boolean bEndVars,boolean bEndSlice,
		boolean bBeginTime,boolean bEndTime,boolean bSingleTimePoint,String[] varNameArr,DisplayPreferences[] displayPreferencesArr,
		MovieHolder movieHolder,int[] pixels,int mirrorWidth,int mirrorHeight,FileDataContainerManager fileDataContainerManager) throws Exception{
	
	boolean isGrayScale = true;
	for (int i = 0; i < displayPreferencesArr.length; i++) {
		if(!displayPreferencesArr[i].isGrayScale()){
			isGrayScale = false;
			break;
		}
	}
	FormatSpecificSpecs formatSpecificSpecs = exportSpecs.getFormatSpecificSpecs();
	if(exportSpecs.getFormat() == ExportFormat.GIF && 
		formatSpecificSpecs instanceof ImageSpecs/* && ((ImageSpecs)formatSpecificSpecs).getFormat() == ExportConstants.GIF*/){
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		GIFOutputStream gifOut = new GIFOutputStream(bytesOut);
		GIFImage gifImage = new GIFUtils.GIFImage(pixels,mirrorWidth);
		gifImage.write(gifOut);
		gifOut.close();
		byte[] data = bytesOut.toByteArray();
		ExportOutput exportOutput = new ExportOutput(true, ".gif", vcdID.getID(), dataID, fileDataContainerManager);
		fileDataContainerManager.append(exportOutput.getFileDataContainerID(), data);
		exportOutputV.add(exportOutput);
	}else if(exportSpecs.getFormat() == ExportFormat.FORMAT_JPEG && 
			formatSpecificSpecs instanceof ImageSpecs/* && ((ImageSpecs)formatSpecificSpecs).getFormat() == ExportConstants.JPEG*/){
		VideoMediaSample jpegEncodedVideoMediaSample = 
			FormatSpecificSpecs.getVideoMediaSample(mirrorWidth, mirrorHeight, 1, isGrayScale,FormatSpecificSpecs.CODEC_JPEG, ((ImageSpecs)formatSpecificSpecs).getcompressionQuality(), pixels);
		ExportOutput exportOutput = new ExportOutput(true, ".jpg", vcdID.getID(), dataID, fileDataContainerManager);
		fileDataContainerManager.append(exportOutput.getFileDataContainerID(), jpegEncodedVideoMediaSample.getDataBytes());
		exportOutputV.add(exportOutput);
	}else if(exportSpecs.getFormat() == ExportFormat.ANIMATED_GIF && 
			formatSpecificSpecs instanceof ImageSpecs/* && ((ImageSpecs)formatSpecificSpecs).getFormat() == ExportConstants.ANIMATED_GIF*/){
		int imageDuration = (int)Math.ceil((movieHolder.getSampleDurationSeconds()*100));//1/100's of a second
		if (bEndTime && (((ImageSpecs)formatSpecificSpecs).getLoopingMode() != 0 || bSingleTimePoint)) {
			imageDuration = 0;
		}
		if (bBeginTime) {
			movieHolder.setGifImage(new GIFUtils.GIFImage(pixels, mirrorWidth));
			movieHolder.getGifImage().setDelay(imageDuration);
		} else {
			movieHolder.getGifImage().addImage(pixels, mirrorWidth, true);
			movieHolder.getGifImage().setDelay(movieHolder.getGifImage().countImages()-1, imageDuration);
		}
		if(bEndTime){
			movieHolder.getGifImage().setIterationCount(((ImageSpecs)formatSpecificSpecs).getLoopingMode());
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			GIFOutputStream gifOut = new GIFOutputStream(bytesOut);
			movieHolder.getGifImage().write(gifOut);
			gifOut.close();
			byte[] data = bytesOut.toByteArray();
			ExportOutput exportOutput = new ExportOutput(true, ".gif", vcdID.getID(), dataID, fileDataContainerManager);
			fileDataContainerManager.append(exportOutput.getFileDataContainerID(), data);
			exportOutputV.add(exportOutput);
		}

	}else if(exportSpecs.getFormat() == ExportFormat.QUICKTIME && 
			formatSpecificSpecs instanceof MovieSpecs){
		String VIDEOMEDIACHUNKID = (varNameArr.length==1?varNameArr[0]:"OVERLAY");
		final int TIMESCALE = 1000;//number of units per second in movie
		boolean bQTVR = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).isQTVR();
		int sampleDuration = (bQTVR?TIMESCALE:(int)(TIMESCALE*movieHolder.getSampleDurationSeconds()));
		VideoMediaSample videoMediaSample =
			FormatSpecificSpecs.getVideoMediaSample(mirrorWidth, mirrorHeight, sampleDuration, isGrayScale, FormatSpecificSpecs.CODEC_JPEG,((MovieSpecs)formatSpecificSpecs).getcompressionQuality(), pixels);
		if (bBeginTime && (!bQTVR || movieHolder.getVarNameVideoMediaChunkHash().get(VIDEOMEDIACHUNKID) == null)) {
			movieHolder.getVarNameVideoMediaChunkHash().put(VIDEOMEDIACHUNKID,new Vector<VideoMediaChunk>());
			movieHolder.getVarNameDataIDHash().put(VIDEOMEDIACHUNKID, dataID);
		}
		movieHolder.getVarNameVideoMediaChunkHash().get(VIDEOMEDIACHUNKID).add(new VideoMediaChunk(videoMediaSample,fileDataContainerManager));
		if(bEndTime && !bQTVR){
			String simID = exportSpecs.getVCDataIdentifier().getID();
			double[] allTimes = exportSpecs.getTimeSpecs().getAllTimes();
			int beginTimeIndex = exportSpecs.getTimeSpecs().getBeginTimeIndex();
			int endTimeIndex = exportSpecs.getTimeSpecs().getEndTimeIndex();
			VideoMediaChunk[] videoMediaChunkArr = movieHolder.getVarNameVideoMediaChunkHash().get(VIDEOMEDIACHUNKID).toArray(new VideoMediaChunk[0]);
			MediaTrack videoTrack = new MediaTrack(videoMediaChunkArr);
			MediaMovie newMovie = new MediaMovie(videoTrack, videoTrack.getDuration(), TIMESCALE);
			newMovie.addUserDataEntry(new UserDataEntry("cpy", "�" + (new GregorianCalendar()).get(Calendar.YEAR) + ", UCHC"));
			newMovie.addUserDataEntry(new UserDataEntry("des", "Dataset name: " + simID));
			newMovie.addUserDataEntry(new UserDataEntry("cmt", "Time range: " + allTimes[beginTimeIndex] + " - " + allTimes[endTimeIndex]));
			for (int i = 0; varNameArr != null && i < varNameArr.length; i++) {
				newMovie.addUserDataEntry(new UserDataEntry("v"+(i<10?"0":"")+i,
					"Variable name: " + varNameArr[i] +
					"\nmin: " + (displayPreferencesArr==null || displayPreferencesArr[i].getScaleSettings()==null?"default":displayPreferencesArr[i].getScaleSettings().getMin()) +
					"\nmax: " + (displayPreferencesArr==null || displayPreferencesArr[i].getScaleSettings()==null?"default":displayPreferencesArr[i].getScaleSettings().getMax())
					));				
			}		
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			DataOutputStream movieos = null;
			try{
				ExportOutput exportOutput = new ExportOutput(true, ".mov", simID, dataID,fileDataContainerManager);
				File tempMovieFile = File.createTempFile("Movie", "temp");
				fileDataContainerManager.manageExistingTempFile(exportOutput.getFileDataContainerID(), tempMovieFile);
				fos = new FileOutputStream(tempMovieFile);
				bos = new BufferedOutputStream(fos);
				movieos = new DataOutputStream(bos);
				MediaMethods.writeMovie(movieos, newMovie);
				movieos.close();
				exportOutputV.add(exportOutput);
				movieHolder.getVarNameVideoMediaChunkHash().clear();
				movieHolder.getVarNameDataIDHash().clear();
			}finally{
				if(movieos != null){try{movieos.close();}catch(Exception e){e.printStackTrace();}}
				if(bos != null){try{bos.close();}catch(Exception e){e.printStackTrace();}}
				if(fos != null){try{fos.close();}catch(Exception e){e.printStackTrace();}}
			}
		}else if(bEndVars && bEndTime && bEndSlice && bQTVR){
			String simID = exportSpecs.getVCDataIdentifier().getID();
			Enumeration<String> allStoredVarNamesEnum = movieHolder.getVarNameVideoMediaChunkHash().keys();
			while(allStoredVarNamesEnum.hasMoreElements()){
				String varName = allStoredVarNamesEnum.nextElement();
				String storedDataID = movieHolder.getVarNameDataIDHash().get(varName);
				VideoMediaChunk[] videoMediaChunkArr = movieHolder.getVarNameVideoMediaChunkHash().get(varName).toArray(new VideoMediaChunk[0]);
				int beginTimeIndex = exportSpecs.getTimeSpecs().getBeginTimeIndex();
				int endTimeIndex = exportSpecs.getTimeSpecs().getEndTimeIndex();
				int numTimes = endTimeIndex-beginTimeIndex+1;
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				DataOutputStream movieos = null;
				try{
					ExportOutput exportOutput = new ExportOutput(true, ".mov", simID, storedDataID,fileDataContainerManager);				
					File tempMovieFile = File.createTempFile("Movie", "temp");
					fileDataContainerManager.manageExistingTempFile(exportOutput.getFileDataContainerID(), tempMovieFile);
					fos = new FileOutputStream(tempMovieFile);
					bos = new BufferedOutputStream(fos);
					movieos = new DataOutputStream(bos);
					writeQTVRWorker(movieos, videoMediaChunkArr, numTimes,videoMediaChunkArr.length/numTimes, mirrorWidth, mirrorHeight);
					movieos.close();
					exportOutputV.add(exportOutput);
				}finally{
					if(movieos != null){try{movieos.close();}catch(Exception e){e.printStackTrace();}}
					if(bos != null){try{bos.close();}catch(Exception e){e.printStackTrace();}}
					if(fos != null){try{fos.close();}catch(Exception e){e.printStackTrace();}}
				}
			}
			movieHolder.getVarNameVideoMediaChunkHash().clear();
			movieHolder.getVarNameDataIDHash().clear();
		}
	}

}

public static void writeQTVRWorker(DataOutputStream dataOutputStream,VideoMediaChunk[] videoMediaChunks,int numTimePoints,int numslices,int width,int height) throws java.io.IOException, java.util.zip.DataFormatException {
	/* make the single node VR World and required chunks */
	if(numTimePoints*numslices != videoMediaChunks.length){
		throw new DataFormatException("NumTimePoints x Numslices != VideoMediaChunk length.");
	}
	VRWorld singleObjVRWorld = VRWorld.createSingleObjectVRWorld(videoMediaChunks[0].getDuration(), numTimePoints, numslices, (float)(width/2), (float)(height/2));
	singleObjVRWorld.getVRObjectSampleAtom(0).setControlSettings(singleObjVRWorld.getVRObjectSampleAtom(0).getControlSettings() | (Integer.parseInt("00001000",2))); // reverse pan controls (set bit 3)
	VRMediaChunk vrChunk = new VRMediaChunk(singleObjVRWorld);
	ObjectMediaChunk objChunk = new ObjectMediaChunk(singleObjVRWorld);
	/* assemble tracks and write the rest of the file */
	MediaTrack qtvrTrack = new MediaTrack(vrChunk);
	MediaTrack objectTrack = new MediaTrack(objChunk);
	MediaTrack imageTrack = new MediaTrack(videoMediaChunks);
	qtvrTrack.setWidth(imageTrack.getWidth());
	qtvrTrack.setHeight(imageTrack.getHeight());
	objectTrack.setWidth(imageTrack.getWidth());
	objectTrack.setHeight(imageTrack.getHeight());
	VRMediaMovie vrMovie = VRMediaMovie.createVRMediaMovie(qtvrTrack, objectTrack, imageTrack, null, imageTrack.getDuration(),videoMediaChunks[0].getDuration()/*DEFAULT_DURATION * numslices * numTimePoints, AtomConstants.defaultTimeScale*/);
	MediaMethods.writeMovie(dataOutputStream, vrMovie);
}
}
