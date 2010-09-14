package cbit.vcell.export.server;
import java.util.*;

import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.export.quicktime.atoms.*;
import cbit.vcell.export.quicktime.*;
import cbit.vcell.simdata.gui.*;

import java.awt.Dimension;
import java.io.*;
import java.util.zip.*;
import java.rmi.*;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.*;
import cbit.vcell.server.*;
import cbit.vcell.geometry.*;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2004 1:09:30 PM)
 * @author: Ion Moraru
 */
public class QTExporter implements ExportConstants {
	private ExportServiceImpl exportServiceImpl = null;
	
	private ExportOutput[] makeSimpleMovies(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID,
			String[] varNames, int beginTimeIndex, int endTimeIndex, int axis, int sliceNumber,
			DisplayPreferences[] displayPreferences, int mirroringType, double duration, boolean hideMembraneOutline,
			int imageScale,int membraneScale,int meshMode) throws DataAccessException, RemoteException, DataFormatException, IOException, Exception 
	{
		return makeSimpleMovies(outputContext, jobID, user, dataServerImpl, vcdID, varNames, beginTimeIndex, endTimeIndex, axis, sliceNumber,
				displayPreferences, mirroringType, duration, hideMembraneOutline, imageScale, membraneScale, meshMode, null);
	}
	
	private ExportOutput[] makeSimpleMovies(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID,
			String[] varNames, int beginTimeIndex, int endTimeIndex, int axis, int sliceNumber,
			DisplayPreferences[] displayPreferences, int mirroringType, double duration, boolean hideMembraneOutline,
			int imageScale,int membraneScale,int meshMode, ClientTaskStatusSupport clientTaskStatusSupport)
							throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
	
		String simID = vcdID.getID();
		VideoMediaSampleRaw sample;
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
		
		ExportOutput[] output = new ExportOutput[varNames.length];
		String dataType = ".mov";
		double progress = 0.0;
	
		for (int k=0;k<varNames.length;k++) {
			off.setVariable(varNames[k]);
			off.setDisplayPreferences(displayPreferences[k]);
			VideoMediaChunk[] chunks = new VideoMediaChunk[endTimeIndex - beginTimeIndex + 1];
			for (int i=beginTimeIndex;i<=endTimeIndex;i++) {
				progress = (double)(k * chunks.length + i) / (varNames.length * chunks.length);
				exportServiceImpl.fireExportProgress(jobID, vcdID, "MOV", progress);
				off.setTimepoint(allTimes[i]);
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
	//			sample = new VideoMediaSampleRaw(width, height, sampleDuration, bytes, bitsPerPixel, isGrayscale);
				sample = new VideoMediaSampleRaw(mirrorWidth, mirrorHeight, sampleDuration,
						new MediaSample.MediaSampleStream(bytes),
						bytes.length,
						bitsPerPixel, isGrayscale);
				chunks[i - beginTimeIndex] = new VideoMediaChunk(sample);
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
				"Variable name: " + varNames[k] +
				"\nmin: " + displayPreferences[k].getScaleSettings().getMin() +
				"\nmax: " + displayPreferences[k].getScaleSettings().getMax()
				));
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			DataOutputStream movieOutput = new DataOutputStream(bytesOut);
			MediaMethods.writeMovie(movieOutput, newMovie);
			movieOutput.close();
			byte[] data = bytesOut.toByteArray();
			String dataID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + sliceNumber + "_" + varNames[k];
			output[k] = new ExportOutput(true, dataType, simID, dataID, data);
		}
	
		return output;
		
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
				exportSpecs.getGeometrySpecs().getSliceNumber(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode(),
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
				exportSpecs.getGeometrySpecs().getSliceNumber(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling(),
				((MovieSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode(),
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
			int imageScale,int membraneScale,int meshMode) throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
		return makeOverlayMovie(outputContext, jobID, user, dataServerImpl, vcdID, varNames, beginTimeIndex, endTimeIndex, axis,
				sliceNumber, displayPreferences, mirroringType, duration, hideMembraneOutline, imageScale, membraneScale, meshMode, null);
	}
	
	private ExportOutput[] makeOverlayMovie(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String[] varNames,
			int beginTimeIndex, int endTimeIndex, int axis, int sliceNumber, DisplayPreferences[] displayPreferences,
			int mirroringType, double duration, boolean hideMembraneOutline,
			int imageScale,int membraneScale,int meshMode, ClientTaskStatusSupport clientTaskStatusSupport)
							throws DataAccessException, RemoteException, DataFormatException, IOException, Exception {
	
		String simID = vcdID.getID();
		VideoMediaSampleRaw sample;
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
		double progress = 0.0;
		
		VideoMediaChunk[] chunks = new VideoMediaChunk[endTimeIndex - beginTimeIndex + 1];	
		for (int i=beginTimeIndex;i<=endTimeIndex;i++) {
			off.setTimepoint(allTimes[i]);
			ByteArrayOutputStream sampleBytes = new ByteArrayOutputStream();
			DataOutputStream sampleData = new DataOutputStream(sampleBytes);
			for (int k=0;k<varNames.length;k++) {	
				progress = (double)(i * varNames.length + k) / (varNames.length * chunks.length);
				exportServiceImpl.fireExportProgress(jobID, vcdID, "MOV", progress);
				off.setVariable(varNames[k]);
				off.setDisplayPreferences(displayPreferences[k]);
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
	//		sample = new VideoMediaSampleRaw(width, height * varNames.length, sampleDuration, bytes, bitsPerPixel, isGrayscale);
			sample = new VideoMediaSampleRaw(mirrorWidth, mirrorHeight * varNames.length, sampleDuration,
					new MediaSample.MediaSampleStream(bytes),
					bytes.length,
					bitsPerPixel, isGrayscale);
			chunks[i - beginTimeIndex] = new VideoMediaChunk(sample);
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
				"\nmin: " + displayPreferences[k].getScaleSettings().getMin() +
				"\nmax: " + displayPreferences[k].getScaleSettings().getMax()
				));
		}
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStream movieOutput = new DataOutputStream(bytesOut);
		MediaMethods.writeMovie(movieOutput, newMovie);
		movieOutput.close();
		byte[] data = bytesOut.toByteArray();
		String dataID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + sliceNumber + "_" + varNames.length + "overlay";
		ExportOutput[] output = new ExportOutput[] {new ExportOutput(true, dataType, simID, dataID, data)};
		
		return output;
	
	}
}