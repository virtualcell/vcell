package cbit.vcell.export.server;
import GIFUtils.*;

import java.awt.Dimension;
import java.io.*;
import java.rmi.*;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.gui.*;
import cbit.vcell.simdata.*;
import cbit.vcell.server.*;
import cbit.image.ImagePaneModel;
import cbit.util.*;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.geometry.*;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2004 1:28:34 PM)
 * @author: Ion Moraru
 */
public class IMGExporter implements ExportConstants {
	private ExportServiceImpl exportServiceImpl = null;
/**
 * Insert the method's description here.
 * Creation date: (4/27/2004 1:18:37 PM)
 * @param exportServiceImpl cbit.vcell.export.server.ExportServiceImpl
 */
public IMGExporter(ExportServiceImpl exportServiceImpl) {
	this.exportServiceImpl = exportServiceImpl;
}


private ExportOutput[] makeAnimatedGIFs(
		OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		VCDataIdentifier vcdID, String[] varNames,
		int beginTimeIndex, int endTimeIndex, int axis, int sliceNumber,
		DisplayPreferences[] displayPreferences, int mirroringType, double duration, int loopingMode, boolean hideMembraneOutline,
		int imageScale,int membraneScale,int meshMode)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	String simID = vcdID.getID();
	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	double interval = allTimes[endTimeIndex] - allTimes[beginTimeIndex];
	PDEOffscreenRenderer off = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
	off.setNormalAxis(axis);
	off.setSlice(sliceNumber);
	off.setHideMembraneOutline(hideMembraneOutline);

	Dimension imageDimension = off.getImageDimension(meshMode,imageScale);
	int originalWidth = (int)imageDimension.getWidth();
	int originalHeight = (int)imageDimension.getHeight();
	int mirrorWidth = originalWidth;
	if ((mirroringType == MIRROR_LEFT) || (mirroringType == MIRROR_RIGHT)) mirrorWidth = 2 * originalWidth;

	ExportOutput[] output = new ExportOutput[varNames.length];
	String dataType = ".gif";
	double progress = 0.0;
	
	for (int k=0;k<varNames.length;k++) {
		off.setVariable(varNames[k]);
		off.setDisplayPreferences(displayPreferences[k]);
		String dataID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + sliceNumber + "_" + varNames[k] + "_animated";
		GIFUtils.GIFImage gifImage = null;
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		GIFOutputStream gifOut = new GIFOutputStream(bytesOut);
		for (int i=beginTimeIndex;i<=endTimeIndex;i++) {
			progress = (double)(k * (endTimeIndex - beginTimeIndex + 1) + i) / (varNames.length * (endTimeIndex - beginTimeIndex + 1));
			exportServiceImpl.fireExportProgress(jobID, vcdID, "GIF", progress);
			off.setTimepoint(allTimes[i]);
			int[] pixels = off.getPixelsRGB(imageScale,membraneScale,meshMode);
			pixels = ExportUtils.extendMirrorPixels(pixels, originalWidth, originalHeight, mirroringType);
			int imageDuration = 0;
			if (i == endTimeIndex) {
				if (loopingMode != 0 || (beginTimeIndex == endTimeIndex)){
					imageDuration = 0;
				}else{
					imageDuration = (int)Math.ceil((allTimes[i] - allTimes[i - 1]) / interval * duration / 10); // 100th of second
				}
			} else {
				imageDuration = (int)Math.ceil((allTimes[i + 1] - allTimes[i]) / interval * duration / 10); // 100th of second
			}
			if (i == beginTimeIndex) {
				gifImage = new GIFUtils.GIFImage(pixels, mirrorWidth);
				gifImage.setDelay(imageDuration);
			} else {
				gifImage.addImage(pixels, mirrorWidth, true);
				gifImage.setDelay(i - beginTimeIndex, imageDuration);
			}
		}
		gifImage.setIterationCount(loopingMode);
		gifImage.write(gifOut);
		gifOut.close();
		byte[] data = bytesOut.toByteArray();
		output[k] = new ExportOutput(true, dataType, simID, dataID, data);
	}

	return output;
}


/**
 * This method was created in VisualAge.
 * @param dataSetController cbit.vcell.server.DataSetController
 * @param vcdID cbit.vcell.solver.VCDataIdentifier
 * @param varNames java.lang.String[]
 * @param times double[]
 * @param displayPreferences DisplayPreferences[]
 * @param slicePlane int
 * @param sliceNumber int
 */
private ExportOutput[] makeGIFs(
		OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		VCDataIdentifier vcdID, String[] varNames,
		int beginTimeIndex, int endTimeIndex,
		int axis, int sliceNumber, DisplayPreferences[] displayPreferences,
		int mirroringType, boolean hideMembraneOutline,
		int imageScale,int membraneScale,int meshMode)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	String simID = vcdID.getID();
	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	PDEOffscreenRenderer off = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
	off.setNormalAxis(axis);
	off.setSlice(sliceNumber);
	off.setHideMembraneOutline(hideMembraneOutline);

	Dimension imageDimension = off.getImageDimension(meshMode,imageScale);
	int originalWidth = (int)imageDimension.getWidth();
	int originalHeight = (int)imageDimension.getHeight();
	int mirrorWidth = originalWidth;
	if ((mirroringType == MIRROR_LEFT) || (mirroringType == MIRROR_RIGHT)) mirrorWidth = 2 * originalWidth;

	ExportOutput[] output = new ExportOutput[varNames.length * (endTimeIndex - beginTimeIndex + 1)];
	String dataType = ".gif";
	double progress = 0.0;
	
	for (int k=0;k<varNames.length;k++) {
		off.setVariable(varNames[k]);
		off.setDisplayPreferences(displayPreferences[k]);
		for (int i=beginTimeIndex;i<=endTimeIndex;i++) {
			progress = (double)(k * (endTimeIndex - beginTimeIndex + 1) + i) / (varNames.length * (endTimeIndex - beginTimeIndex + 1));
			exportServiceImpl.fireExportProgress(jobID, vcdID, "GIF", progress);
			StringBuffer inset = new StringBuffer(Integer.toString(i));
				inset.reverse();
				inset.append("000");
				inset.setLength(4);
				inset.reverse();
			String dataID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + sliceNumber + "_" + varNames[k] +"_" + inset.toString();
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			GIFOutputStream gifOut = new GIFOutputStream(bytesOut);
			off.setTimepoint(allTimes[i]);
			int[] pixels = off.getPixelsRGB(imageScale,membraneScale,meshMode);
			pixels = ExportUtils.extendMirrorPixels(pixels,originalWidth,originalHeight, mirroringType);
			GIFUtils.GIFImage gifImage = new GIFUtils.GIFImage(pixels,mirrorWidth);
			gifImage.write(gifOut);
			gifOut.close();
			byte[] data = bytesOut.toByteArray();
			output[k * (endTimeIndex - beginTimeIndex + 1) + i - beginTimeIndex] = new ExportOutput(true, dataType, simID, dataID, data);
		}
	}

	return output;
}


/**
 * This method was created in VisualAge.
 */
public ExportOutput[] makeImageData(
		OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	switch (((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getFormat()) {
		case GIF:
			return makeGIFs(
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
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode()
				);
		case ANIMATED_GIF:
			return makeAnimatedGIFs(
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
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getDisplayPreferences(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMirroringType(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getDuration(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getLoopingMode(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).isHideMembraneOutline(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getImageScaling(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMembraneScaling(),
				((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getMeshMode()
				);
		case TIFF:
//			return makeTIFFs();
		case JPEG:
//			return makeJPEGs();
		default:
			return new ExportOutput[] {new ExportOutput(false, null, null, null, null)};
	}
}
}