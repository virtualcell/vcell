package cbit.vcell.export.server;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.DataFormatException;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import GIFUtils.GIFFormatException;
import GIFUtils.GIFImage;
import GIFUtils.GIFOutputStream;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.task.ClientTaskStatusSupport;
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
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.gui.DisplayPreferences;
import cbit.vcell.solvers.CartesianMesh;
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

/**
 * This method was created in VisualAge.
 */
public ExportOutput[] makeMediaData(
		OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs,ClientTaskStatusSupport clientTaskStatusSupport)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

			return makeMedia(exportServiceImpl,outputContext,jobRequest.getJobID(),user,dataServerImpl,exportSpecs,clientTaskStatusSupport);
}

private static ExportOutput[] makeMedia(ExportServiceImpl exportServiceImpl,
		OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		ExportSpecs exportSpecs,ClientTaskStatusSupport clientTaskStatusSupport)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	boolean bOverLay = false;
	if (exportSpecs.getFormatSpecificSpecs() instanceof MovieSpecs){
		bOverLay = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getOverlayMode();
	}
	
	int sliceIndicator = (exportSpecs.getGeometrySpecs().getModeID() == ExportConstants.GEOMETRY_FULL?FULL_MODE_ALL_SLICES:exportSpecs.getGeometrySpecs().getSliceNumber());
	int imageScale = 0;
	boolean bHideMmebraneOutline = false;
	int meshMode = 0;
	int mirroringType = 0;
	int membraneScale = 0;
	double duration = 1.0;
	DisplayPreferences[] displayPreferences = null;
	if(exportSpecs.getFormatSpecificSpecs() instanceof ImageSpecs){
		imageScale = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling();
		bHideMmebraneOutline = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline();
		meshMode = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode();
		mirroringType = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType();
		membraneScale = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling();
		displayPreferences = ((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences();
		duration = (double)((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration()/1000.0;//convert from milliseconds to seconds
	}else if (exportSpecs.getFormatSpecificSpecs() instanceof MovieSpecs){
		imageScale = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling();
		bHideMmebraneOutline = ((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline();
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
	String[] varNames = exportSpecs.getVariableSpecs().getVariableNames();

	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	int startSlice = (sliceIndicator==FULL_MODE_ALL_SLICES?0:sliceIndicator);
	int sliceCount = getSliceCount(sliceIndicator,exportSpecs.getGeometrySpecs().getAxis(), dataServerImpl.getMesh(user, vcdID));
	double progressIncr = 1.0/(sliceCount*(endTimeIndex - beginTimeIndex + 1)*varNames.length);
	double progress = 0.0;
	MovieHolder movieHolder = new MovieHolder();
for (int sliceNumber = startSlice; sliceNumber < startSlice+sliceCount; sliceNumber++) {

	PDEOffscreenRenderer offScreenRenderer = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
	offScreenRenderer.setNormalAxis(exportSpecs.getGeometrySpecs().getAxis());
	offScreenRenderer.setSlice(sliceNumber);
	offScreenRenderer.setHideMembraneOutline(bHideMmebraneOutline);
	Dimension imageDimension = offScreenRenderer.getImageDimension(meshMode,imageScale);
	int originalWidth = (int)imageDimension.getWidth();
	int originalHeight = (int)imageDimension.getHeight();
	
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
		exportServiceImpl.fireExportProgress(jobID, vcdID, "MEDIA", progress);
		progress+= progressIncr;
		MirrorInfo currentSliceTimeMirrorInfo =
			renderAndMirrorSliceTimePixels(offScreenRenderer, varNames[varNameIndex0], allTimes[timeIndex0],
				displayPreferences[varNameIndex0],imageScale, membraneScale,
				meshMode, originalWidth, originalHeight, mirroringType);
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
				createMedia(exportOutputV, vcdID, dataID, exportSpecs,true,bEndslice,bBegintime,bEndTime, bSingleTimePoint, varNames,displayPreferences,movieHolder, overLayPixels, currentSliceTimeMirrorInfo.getMirrorWidth(), currentSliceTimeMirrorInfo.getMirrorHeight()*varNames.length);
				varNameIndex0 = 0;
				timeIndex0++;
				if(timeIndex0>endTimeIndex){
					break;
				}
			}
		}else{
			String dataID = createDataID(exportSpecs, sliceNumber, varNames[varNameIndex0], timeIndex0);
			boolean bEndVars = varNameIndex0 == varNames.length-1;
			createMedia(exportOutputV, vcdID, dataID, exportSpecs,bEndVars,bEndslice,bBegintime, bEndTime, bSingleTimePoint, new String[] {varNames[varNameIndex0]},new DisplayPreferences[] {displayPreferences[varNameIndex0]},movieHolder, currentSliceTimeMirrorInfo.getPixels(), currentSliceTimeMirrorInfo.getMirrorWidth(), currentSliceTimeMirrorInfo.getMirrorHeight());
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
	return exportOutputV.toArray(new ExportOutput[0]);
}

	private static int getSliceCount(int sliceIndicator,int normalAxis,CartesianMesh mesh){
		if (sliceIndicator!=FULL_MODE_ALL_SLICES){
			return 1;
		}
		switch (normalAxis){
			case Coordinate.X_AXIS:{
				// YZ plane
				return mesh.getSizeX();
			}
			case Coordinate.Y_AXIS:{
				// ZX plane
				return mesh.getSizeY();
			}
			case Coordinate.Z_AXIS:{
				// XY plane
				return mesh.getSizeZ();

			}
			default:{
				throw new IllegalArgumentException("unexpected normal axis "+normalAxis);
			}
		}
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
private static MirrorInfo renderAndMirrorSliceTimePixels(
		PDEOffscreenRenderer offScreenRenderer,String varName,double timePoint,DisplayPreferences displayPreference,
		int imageScale,int membraneScaling,int meshMode,
		int originalWidth,int originalHeight,int mirroringType) throws Exception{
	offScreenRenderer.setVarAndTimeAndDisplay(varName,timePoint, displayPreference);
	int[] pixels = offScreenRenderer.getPixelsRGB(imageScale,membraneScaling,meshMode);
	pixels = ExportUtils.extendMirrorPixels(pixels,originalWidth,originalHeight, mirroringType);
	
	int mirrorWidth = originalWidth;
	int mirrorHeight = originalHeight;
	if ((mirroringType == MIRROR_LEFT) || (mirroringType == MIRROR_RIGHT)){
		mirrorWidth = 2 * originalWidth;
	}
	if ((mirroringType == MIRROR_TOP) || (mirroringType == MIRROR_BOTTOM)){
		mirrorHeight = 2 * originalHeight;
	}

	return new MirrorInfo(pixels, mirrorHeight, mirrorWidth);

}
private static String create4DigitNumber(int number){
	return (number<1000?"0":"")+(number<100?"0":"")+(number<10?"0":"") + number;
}
private static String create3DigitNumber(int number){
	return (number<100?"0":"")+(number<10?"0":"") + number;
}
private static String createDataID(ExportSpecs exportSpecs,int sliceNumber,String varName,int timeIndex){
	int sliceNormalAxis = exportSpecs.getGeometrySpecs().getAxis();
	if(exportSpecs.getFormat() == ExportConstants.FORMAT_ANIMATED_GIF){
		return "_" + Coordinate.getNormalAxisPlaneName(sliceNormalAxis) + "_" + create3DigitNumber(sliceNumber) + "_" + varName + "_animated";
	}else if (exportSpecs.getFormat() == ExportConstants.FORMAT_QUICKTIME){
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
		MovieHolder movieHolder,int[] pixels,int mirrorWidth,int mirrorHeight) throws Exception{
	
	boolean isGrayScale = true;
	for (int i = 0; i < displayPreferencesArr.length; i++) {
		if(!displayPreferencesArr[i].isGrayScale()){
			isGrayScale = false;
			break;
		}
	}
	FormatSpecificSpecs formatSpecificSpecs = exportSpecs.getFormatSpecificSpecs();
	if(exportSpecs.getFormat() == ExportConstants.FORMAT_GIF && 
		formatSpecificSpecs instanceof ImageSpecs &&
		((ImageSpecs)formatSpecificSpecs).getFormat() == ExportConstants.GIF){
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		GIFOutputStream gifOut = new GIFOutputStream(bytesOut);
		GIFImage gifImage = new GIFUtils.GIFImage(pixels,mirrorWidth);
		gifImage.write(gifOut);
		gifOut.close();
		byte[] data = bytesOut.toByteArray();
		exportOutputV.add(new ExportOutput(true, ".gif", vcdID.getID(), dataID, data));
	}else if(exportSpecs.getFormat() == ExportConstants.FORMAT_JPEG && 
			formatSpecificSpecs instanceof ImageSpecs &&
			((ImageSpecs)formatSpecificSpecs).getFormat() == ExportConstants.JPEG){
		VideoMediaSample jpegEncodedVideoMediaSample = 
			FormatSpecificSpecs.getVideoMediaSample(mirrorWidth, mirrorHeight, 1, isGrayScale,FormatSpecificSpecs.CODEC_JPEG, ((ImageSpecs)formatSpecificSpecs).getcompressionQuality(), pixels);
		exportOutputV.add(new ExportOutput(true, ".jpg", vcdID.getID(), dataID, jpegEncodedVideoMediaSample.getDataBytes()));
	}else if(exportSpecs.getFormat() == ExportConstants.FORMAT_ANIMATED_GIF && 
			formatSpecificSpecs instanceof ImageSpecs &&
			((ImageSpecs)formatSpecificSpecs).getFormat() == ExportConstants.ANIMATED_GIF){
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
			exportOutputV.add(new ExportOutput(true, ".gif", vcdID.getID(), dataID, data));
		}

	}else if(exportSpecs.getFormat() == ExportConstants.FORMAT_QUICKTIME && 
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
		movieHolder.getVarNameVideoMediaChunkHash().get(VIDEOMEDIACHUNKID).add(new VideoMediaChunk(videoMediaSample));
		if(bEndTime && !bQTVR){
			String simID = exportSpecs.getVCDataIdentifier().getID();
			double[] allTimes = exportSpecs.getTimeSpecs().getAllTimes();
			int beginTimeIndex = exportSpecs.getTimeSpecs().getBeginTimeIndex();
			int endTimeIndex = exportSpecs.getTimeSpecs().getEndTimeIndex();
			VideoMediaChunk[] videoMediaChunkArr = movieHolder.getVarNameVideoMediaChunkHash().get(VIDEOMEDIACHUNKID).toArray(new VideoMediaChunk[0]);
			MediaTrack videoTrack = new MediaTrack(videoMediaChunkArr);
			MediaMovie newMovie = new MediaMovie(videoTrack, videoTrack.getDuration(), TIMESCALE);
			newMovie.addUserDataEntry(new UserDataEntry("cpy", "©" + (new GregorianCalendar()).get(Calendar.YEAR) + ", UCHC"));
			newMovie.addUserDataEntry(new UserDataEntry("des", "Dataset name: " + simID));
			newMovie.addUserDataEntry(new UserDataEntry("cmt", "Time range: " + allTimes[beginTimeIndex] + " - " + allTimes[endTimeIndex]));
			for (int i = 0; varNameArr != null && i < varNameArr.length; i++) {
				newMovie.addUserDataEntry(new UserDataEntry("v"+(i<10?"0":"")+i,
					"Variable name: " + varNameArr[i] +
					"\nmin: " + (displayPreferencesArr==null || displayPreferencesArr[i].getScaleSettings()==null?"default":displayPreferencesArr[i].getScaleSettings().getMin()) +
					"\nmax: " + (displayPreferencesArr==null || displayPreferencesArr[i].getScaleSettings()==null?"default":displayPreferencesArr[i].getScaleSettings().getMax())
					));				
			}
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			DataOutputStream movieOutput = new DataOutputStream(bytesOut);
			MediaMethods.writeMovie(movieOutput, newMovie);
			movieOutput.close();
			byte[] finalMovieBytes = bytesOut.toByteArray();
			exportOutputV.add(new ExportOutput(true, ".mov", simID, dataID, finalMovieBytes));
			movieHolder.getVarNameVideoMediaChunkHash().clear();
			movieHolder.getVarNameDataIDHash().clear();
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
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream movieOutputStream = new DataOutputStream(baos);
				writeQTVRWorker(movieOutputStream, videoMediaChunkArr, numTimes,videoMediaChunkArr.length/numTimes, mirrorWidth, mirrorHeight);
				movieOutputStream.close();
				byte[] finalMovieBytes = baos.toByteArray();
				exportOutputV.add(new ExportOutput(true, ".mov", simID, storedDataID, finalMovieBytes));					
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