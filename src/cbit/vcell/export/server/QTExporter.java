package cbit.vcell.export.server;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.DataFormatException;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.export.gloworm.atoms.UserDataEntry;
import cbit.vcell.export.gloworm.quicktime.MediaMethods;
import cbit.vcell.export.gloworm.quicktime.MediaMovie;
import cbit.vcell.export.gloworm.quicktime.MediaTrack;
import cbit.vcell.export.gloworm.quicktime.VideoMediaChunk;
import cbit.vcell.export.gloworm.quicktime.VideoMediaSample;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.gui.DisplayPreferences;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2004 1:09:30 PM)
 * @author: Ion Moraru
 */
public class QTExporter implements ExportConstants {
	
	private static final int FULL_MODE_ALL_SLICES = -1;
	
	private ExportServiceImpl exportServiceImpl = null;
	
	private ExportOutput[] makeSimpleMovies(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID,
			String[] varNames, int beginTimeIndex, int endTimeIndex, int axis, int sliceNumber,
			DisplayPreferences[] displayPreferences, int mirroringType, double duration, boolean hideMembraneOutline,
			int imageScale,int membraneScale,int meshMode,int compressionType,float compressionQuality) throws DataAccessException, RemoteException, DataFormatException, IOException, Exception 
	{
		return makeSimpleMovies(outputContext, jobID, user, dataServerImpl, vcdID, varNames, beginTimeIndex, endTimeIndex, axis, sliceNumber,
				displayPreferences, mirroringType, duration, hideMembraneOutline, imageScale, membraneScale, meshMode, compressionType,compressionQuality,null);
	}
	
	private ExportOutput[] makeSimpleMovies(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID,
			String[] varNames, int beginTimeIndex, int endTimeIndex, int axis, int sliceIndicator,
			DisplayPreferences[] displayPreferences, int mirroringType, double duration, boolean hideMembraneOutline,
			int imageScale,int membraneScale,int meshMode, int compressionType, float compressionQuality,ClientTaskStatusSupport clientTaskStatusSupport)
							throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
	
		if(sliceIndicator==FULL_MODE_ALL_SLICES && varNames.length != 1){
			throw new IllegalArgumentException("All slices simple movies can only have 1 variable");
		}
		String simID = vcdID.getID();
		VideoMediaSample videoMediaSample;
		int sampleDuration = 0;
		int timeScale = 1000;
		int bitsPerPixel = 32;
		boolean isGrayscale = false;
	
		double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
		double interval = allTimes[endTimeIndex] - allTimes[beginTimeIndex];
		PDEOffscreenRenderer off = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
		off.setNormalAxis(axis);
		
		int startSlice = (sliceIndicator==FULL_MODE_ALL_SLICES?0:sliceIndicator);
		int sliceCount = getSliceCount(sliceIndicator,axis, dataServerImpl.getMesh(user, vcdID));
		ExportOutput[] output = new ExportOutput[(sliceIndicator==FULL_MODE_ALL_SLICES?sliceCount:varNames.length)];
	for (int sliceNumber = startSlice; sliceNumber < startSlice+sliceCount; sliceNumber++) {
		byte[] finalMovieBytes = null;
		String finalMovieID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + sliceNumber + "_" + varNames[0];
		
		off.setSlice(sliceNumber);
		off.setHideMembraneOutline(hideMembraneOutline);
		
		Dimension imageDimension = off.getImageDimension(meshMode,imageScale);
		int originalWidth = (int)imageDimension.getWidth();
		int originalHeight = (int)imageDimension.getHeight();
		int mirrorWidth = originalWidth; int mirrorHeight = originalHeight;
		if ((mirroringType == MIRROR_LEFT) || (mirroringType == MIRROR_RIGHT)) mirrorWidth = 2 * originalWidth;
		if ((mirroringType == MIRROR_TOP) || (mirroringType == MIRROR_BOTTOM)) mirrorHeight = 2 * originalHeight;
		
		String dataType = ".mov";
		double progress = 0.0;
	
		for (int varNameIndex=0;varNameIndex<varNames.length;varNameIndex++) {
			VideoMediaChunk[] chunks = new VideoMediaChunk[endTimeIndex - beginTimeIndex + 1];
			for (int i=beginTimeIndex;i<=endTimeIndex;i++) {
				progress = (double)(varNameIndex * chunks.length + i) / (varNames.length * chunks.length);
				exportServiceImpl.fireExportProgress(jobID, vcdID, "MOV", progress);
				off.setVarAndTimeAndDisplay(varNames[varNameIndex], allTimes[i], displayPreferences[varNameIndex]);
				int[] pixels = off.getPixelsRGB(imageScale,membraneScale,meshMode);
				pixels = ExportUtils.extendMirrorPixels(pixels, originalWidth, originalHeight, mirroringType);
				ByteArrayOutputStream sampleBytes = new ByteArrayOutputStream();
				DataOutputStream sampleData = new DataOutputStream(sampleBytes);
				for (int j=0;j<pixels.length;j++) sampleData.writeInt(pixels[j]);
				sampleData.close();
				byte[] bytes = sampleBytes.toByteArray();
				if (i == endTimeIndex){
					//Keep the last non-zero sample duration
					//sampleDuration = 0
				}else{
					sampleDuration = (int)Math.ceil((allTimes[i + 1] - allTimes[i]) / interval * duration);
				}
							
				videoMediaSample =
					FormatSpecificSpecs.getVideoMediaSample(mirrorWidth, mirrorHeight, sampleDuration, bitsPerPixel, isGrayscale, compressionType,compressionQuality, bytes);

				chunks[i - beginTimeIndex] = new VideoMediaChunk(videoMediaSample);
				if(clientTaskStatusSupport != null)
				{
					clientTaskStatusSupport.setProgress((i*100/(endTimeIndex - beginTimeIndex)));
					if(clientTaskStatusSupport.isInterrupted())
					{
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
			}
			
			MediaTrack videoTrack = new MediaTrack(chunks);
			MediaMovie newMovie = new MediaMovie(videoTrack, videoTrack.getDuration(), timeScale);
			newMovie.addUserDataEntry(new UserDataEntry("cpy", "©" + (new GregorianCalendar()).get(Calendar.YEAR) + ", UCHC"));
			newMovie.addUserDataEntry(new UserDataEntry("des", "Dataset name: " + simID));
			newMovie.addUserDataEntry(new UserDataEntry("cmt", "Time range: " + allTimes[beginTimeIndex] + " - " + allTimes[endTimeIndex]));
			newMovie.addUserDataEntry(new UserDataEntry("v00",
				"Variable name: " + varNames[varNameIndex] +
				"\nmin: " + (displayPreferences[varNameIndex].getScaleSettings()==null?"default":displayPreferences[varNameIndex].getScaleSettings().getMin()) +
				"\nmax: " + (displayPreferences[varNameIndex].getScaleSettings()==null?"default":displayPreferences[varNameIndex].getScaleSettings().getMax())
				));
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			DataOutputStream movieOutput = new DataOutputStream(bytesOut);
			MediaMethods.writeMovie(movieOutput, newMovie);
			movieOutput.close();
			finalMovieBytes = bytesOut.toByteArray();
			if(sliceIndicator != FULL_MODE_ALL_SLICES){
				finalMovieID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + create3DigitNumber(sliceNumber) + "_" + varNames[varNameIndex];
				output[varNameIndex] = new ExportOutput(true, dataType, simID, finalMovieID, finalMovieBytes);
			}
		}
		if(sliceIndicator == FULL_MODE_ALL_SLICES){
			output[sliceNumber] = new ExportOutput(true, dataType, simID, finalMovieID, finalMovieBytes);
		}
	}

		return output;
		
	}
	
	private String create3DigitNumber(int sliceNumber){
		return (sliceNumber<100?"0":"")+(sliceNumber<10?"0":"") + sliceNumber;
	}
	private int getSliceCount(int sliceIndicator,int normalAxis,CartesianMesh mesh){
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
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/27/2004 1:18:37 PM)
	 * @param exportServiceImpl cbit.vcell.export.server.ExportServiceImpl
	 */
	public QTExporter(ExportServiceImpl exportServiceImpl) {
		this.exportServiceImpl = exportServiceImpl;
	}
	
	public ExportOutput[] makeMovieData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs)
							throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
		return makeMovieData(outputContext, jobRequest, user, dataServerImpl, exportSpecs, null);
	}
	
	
	public ExportOutput[] makeMovieData(
			OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs, ClientTaskStatusSupport clientTaskStatusSupport)
							throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
		ExportOutput[] rawOutput;
		if (((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getOverlayMode())
			rawOutput = makeOverlayMovie(
					outputContext,
				jobRequest.getJobID(),
				user,
				dataServerImpl,
				exportSpecs.getVCDataIdentifier(),
				exportSpecs.getVariableSpecs().getVariableNames(),
				exportSpecs.getTimeSpecs().getBeginTimeIndex(),
				exportSpecs.getTimeSpecs().getEndTimeIndex(),
				exportSpecs.getGeometrySpecs().getAxis(),
				(exportSpecs.getGeometrySpecs().getModeID() == ExportConstants.GEOMETRY_FULL?FULL_MODE_ALL_SLICES:exportSpecs.getGeometrySpecs().getSliceNumber()),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getCompressionType(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getcompressionQuality(),
				clientTaskStatusSupport
				);
		else
			rawOutput = makeSimpleMovies(
					outputContext,
				jobRequest.getJobID(),
				user,
				dataServerImpl,
				exportSpecs.getVCDataIdentifier(),
				exportSpecs.getVariableSpecs().getVariableNames(),
				exportSpecs.getTimeSpecs().getBeginTimeIndex(),
				exportSpecs.getTimeSpecs().getEndTimeIndex(),
				exportSpecs.getGeometrySpecs().getAxis(),
				(exportSpecs.getGeometrySpecs().getModeID() == ExportConstants.GEOMETRY_FULL?FULL_MODE_ALL_SLICES:exportSpecs.getGeometrySpecs().getSliceNumber()),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getCompressionType(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getcompressionQuality(),
				clientTaskStatusSupport
				);
		switch (((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getEncodingFormat()) {
			case RAW_RGB:
				return rawOutput;
			default:
				return new ExportOutput[] {new ExportOutput(false, null, null, null, null)};
		}
	}
	
	private ExportOutput[] makeOverlayMovie(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String[] varNames,
			int beginTimeIndex, int endTimeIndex, int axis, int sliceNumber, DisplayPreferences[] displayPreferences,
			int mirroringType, double duration, boolean hideMembraneOutline,
			int imageScale,int membraneScale,int meshMode,int compressionType,float compressionQuality) throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
		return makeOverlayMovie(outputContext, jobID, user, dataServerImpl, vcdID, varNames, beginTimeIndex, endTimeIndex, axis,
				sliceNumber, displayPreferences, mirroringType, duration, hideMembraneOutline, imageScale, membraneScale, meshMode,compressionType,compressionQuality, null);
	}
	
	private ExportOutput[] makeOverlayMovie(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String[] varNames,
			int beginTimeIndex, int endTimeIndex, int axis, int sliceIndicator, DisplayPreferences[] displayPreferences,
			int mirroringType, double duration, boolean hideMembraneOutline,
			int imageScale,int membraneScale,int meshMode, int compressionType,float compressionQuality,ClientTaskStatusSupport clientTaskStatusSupport)
							throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
	

		int startSlice = (sliceIndicator==FULL_MODE_ALL_SLICES?0:sliceIndicator);
		int sliceCount = getSliceCount(sliceIndicator,axis, dataServerImpl.getMesh(user, vcdID));
		ExportOutput[] output = new ExportOutput[(sliceIndicator==FULL_MODE_ALL_SLICES?sliceCount:1)];
		double progressIncr = 1.0/(sliceCount*(endTimeIndex - beginTimeIndex + 1)*varNames.length);
		double progress = 0.0;
	for (int sliceNumber = startSlice; sliceNumber < startSlice+sliceCount; sliceNumber++) {
		String simID = vcdID.getID();
		VideoMediaSample videoMediaSample;
		int sampleDuration = 0;
		int timeScale = 1000;
		int bitsPerPixel = 32;
		boolean isGrayscale = false;
		
		double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
		double interval = allTimes[endTimeIndex] - allTimes[beginTimeIndex];
		PDEOffscreenRenderer off = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
		off.setNormalAxis(axis);
		off.setSlice(sliceNumber);
		off.setHideMembraneOutline(hideMembraneOutline);
	
		Dimension imageDimension = off.getImageDimension(meshMode,imageScale);
		int originalWidth = (int)imageDimension.getWidth();
		int originalHeight = (int)imageDimension.getHeight();
		int mirrorWidth = originalWidth; int mirrorHeight = originalHeight;
		if ((mirroringType == MIRROR_LEFT) || (mirroringType == MIRROR_RIGHT)) mirrorWidth = 2 * originalWidth;
		if ((mirroringType == MIRROR_TOP) || (mirroringType == MIRROR_BOTTOM)) mirrorHeight = 2 * originalHeight;
	
		String dataType = ".mov";
		
		VideoMediaChunk[] chunks = new VideoMediaChunk[endTimeIndex - beginTimeIndex + 1];	
		for (int i=beginTimeIndex;i<=endTimeIndex;i++) {
			ByteArrayOutputStream sampleBytes = new ByteArrayOutputStream();
			DataOutputStream sampleData = new DataOutputStream(sampleBytes);
			for (int k=0;k<varNames.length;k++) {	
				exportServiceImpl.fireExportProgress(jobID, vcdID, "MOV", progress);
				progress+= progressIncr;
				off.setVarAndTimeAndDisplay(varNames[k], allTimes[i], displayPreferences[k]);
				int[] pixels = off.getPixelsRGB(imageScale,membraneScale,meshMode);
				pixels = ExportUtils.extendMirrorPixels(pixels, originalWidth, originalHeight, mirroringType);
				for (int j=0;j<pixels.length;j++) sampleData.writeInt(pixels[j]);
			}
			sampleData.close();
			byte[] bytes = sampleBytes.toByteArray();
			if (i == endTimeIndex){
				//Keep the last non-zero duration
				//sampleDuration = 0;
			}else{
				sampleDuration = (int)Math.ceil((allTimes[i + 1] - allTimes[i]) / interval * duration);
			}
			
			videoMediaSample =
				FormatSpecificSpecs.getVideoMediaSample(mirrorWidth, mirrorHeight*varNames.length, sampleDuration, bitsPerPixel, isGrayscale, compressionType,compressionQuality, bytes);

			chunks[i - beginTimeIndex] = new VideoMediaChunk(videoMediaSample);
			if(clientTaskStatusSupport != null)
			{
				clientTaskStatusSupport.setProgress((i*100/(endTimeIndex - beginTimeIndex)));
				if(clientTaskStatusSupport.isInterrupted())
				{
					throw UserCancelException.CANCEL_GENERIC;
				}
			}
		}
		
		MediaTrack videoTrack = new MediaTrack(chunks);
		MediaMovie newMovie = new MediaMovie(videoTrack, videoTrack.getDuration(), timeScale);
		newMovie.addUserDataEntry(new UserDataEntry("cpy", "©" + (new GregorianCalendar()).get(Calendar.YEAR) + ", UCHC"));
		newMovie.addUserDataEntry(new UserDataEntry("des", "Dataset name: " + simID));
		newMovie.addUserDataEntry(new UserDataEntry("cmt", "Time range: " + allTimes[beginTimeIndex] + " - " + allTimes[endTimeIndex]));
		for (int k=0;k<varNames.length;k++) {
			String entryType;
			if (k < 10) entryType = "v0" + k;
			else entryType = "v" + k;
			newMovie.addUserDataEntry(new UserDataEntry(entryType,
				"Variable name: " + varNames[k] +
				"\nmin: " + (displayPreferences[k].getScaleSettings()==null?"default":displayPreferences[k].getScaleSettings().getMin()) +
				"\nmax: " + (displayPreferences[k].getScaleSettings()==null?"default":displayPreferences[k].getScaleSettings().getMax())
				));
		}
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStream movieOutput = new DataOutputStream(bytesOut);
		MediaMethods.writeMovie(movieOutput, newMovie);
		movieOutput.close();
		byte[] sliceMovieData = bytesOut.toByteArray();
		String sliceMovieID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + create3DigitNumber(sliceNumber) + "_" + varNames.length + "_overlay";
		output[sliceNumber-startSlice] = new ExportOutput(true, dataType, simID, sliceMovieID, sliceMovieData);
	}
		return output;
	
	}
}