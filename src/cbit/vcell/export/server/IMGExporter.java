package cbit.vcell.export.server;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import GIFUtils.GIFFormatException;
import GIFUtils.GIFOutputStream;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.simdata.DataServerImpl;
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


//private ExportOutput[] makeAnimatedGIFs(
//		OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
//		VCDataIdentifier vcdID, String[] varNames,
//		int beginTimeIndex, int endTimeIndex, int axis, int sliceNumber,
//		DisplayPreferences[] displayPreferences, int mirroringType, double duration, int loopingMode, boolean hideMembraneOutline,
//		int imageScale,int membraneScale,int meshMode)
//						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {
//
//	String simID = vcdID.getID();
//	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
//	double interval = allTimes[endTimeIndex] - allTimes[beginTimeIndex];
//	PDEOffscreenRenderer off = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
//	off.setNormalAxis(axis);
//	off.setSlice(sliceNumber);
//	off.setHideMembraneOutline(hideMembraneOutline);
//
//	Dimension imageDimension = off.getImageDimension(meshMode,imageScale);
//	int originalWidth = (int)imageDimension.getWidth();
//	int originalHeight = (int)imageDimension.getHeight();
//	int mirrorWidth = originalWidth;
//	if ((mirroringType == MIRROR_LEFT) || (mirroringType == MIRROR_RIGHT)) mirrorWidth = 2 * originalWidth;
//
//	ExportOutput[] output = new ExportOutput[varNames.length];
//	String dataType = ".gif";
//	double progress = 0.0;
//	
//	for (int k=0;k<varNames.length;k++) {
//		String dataID = "_" + Coordinate.getNormalAxisPlaneName(axis) + "_" + sliceNumber + "_" + varNames[k] + "_animated";
//		GIFUtils.GIFImage gifImage = null;
//		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
//		GIFOutputStream gifOut = new GIFOutputStream(bytesOut);
//		for (int i=beginTimeIndex;i<=endTimeIndex;i++) {
//			progress = (double)(k * (endTimeIndex - beginTimeIndex + 1) + i) / (varNames.length * (endTimeIndex - beginTimeIndex + 1));
//			exportServiceImpl.fireExportProgress(jobID, vcdID, "GIF", progress);
//			off.setVarAndTimeAndDisplay(varNames[k], allTimes[i], displayPreferences[k]);
//			int[] pixels = off.getPixelsRGB(imageScale,membraneScale,meshMode);
//			pixels = ExportUtils.extendMirrorPixels(pixels, originalWidth, originalHeight, mirroringType);
//			int imageDuration = 0;
//			if (i == endTimeIndex) {
//				if (loopingMode != 0 || (beginTimeIndex == endTimeIndex)){
//					imageDuration = 0;
//				}else{
//					imageDuration = (int)Math.ceil((allTimes[i] - allTimes[i - 1]) / interval * duration / 10); // 100th of second
//				}
//			} else {
//				imageDuration = (int)Math.ceil((allTimes[i + 1] - allTimes[i]) / interval * duration / 10); // 100th of second
//			}
//			if (i == beginTimeIndex) {
//				gifImage = new GIFUtils.GIFImage(pixels, mirrorWidth);
//				gifImage.setDelay(imageDuration);
//			} else {
//				gifImage.addImage(pixels, mirrorWidth, true);
//				gifImage.setDelay(i - beginTimeIndex, imageDuration);
//			}
//		}
//		gifImage.setIterationCount(loopingMode);
//		gifImage.write(gifOut);
//		gifOut.close();
//		byte[] data = bytesOut.toByteArray();
//		output[k] = new ExportOutput(true, dataType, simID, dataID, data);
//	}
//
//	return output;
//}

/**
 * This method was created in VisualAge.
 */
public ExportOutput[] makeImageData(
		OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	switch (((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getFormat()) {
		case GIF:
		case JPEG:
		case ANIMATED_GIF:
			return makeSingleImages(exportServiceImpl,outputContext,jobRequest.getJobID(),user,dataServerImpl,exportSpecs);
		default:
			throw new Exception("Unknown format type="+((ImageSpecs)exportSpecs.getFormatSpecificSpecs()).getFormat());
	}
}

private static ExportOutput[] makeSingleImages(ExportServiceImpl exportServiceImpl,
		OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		ExportSpecs exportSpecs)
						throws RemoteException, IOException, GIFFormatException, DataAccessException, Exception {

	ImageSpecs imageSpecs = (ImageSpecs)exportSpecs.getFormatSpecificSpecs();
	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	int sliceNumber = exportSpecs.getGeometrySpecs().getSliceNumber();
	int sliceNormalAxis = exportSpecs.getGeometrySpecs().getAxis();
	int imageScale = imageSpecs.getImageScaling();
	
	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	PDEOffscreenRenderer offScreenRenderer = new PDEOffscreenRenderer(outputContext,user, dataServerImpl, vcdID);
	offScreenRenderer.setNormalAxis(sliceNormalAxis);
	offScreenRenderer.setSlice(sliceNumber);
	offScreenRenderer.setHideMembraneOutline(imageSpecs.isHideMembraneOutline());

	Dimension imageDimension = offScreenRenderer.getImageDimension(imageSpecs.getMeshMode(),imageScale);
	int originalWidth = (int)imageDimension.getWidth();
	int originalHeight = (int)imageDimension.getHeight();
	int mirrorWidth = originalWidth;
	int mirrorHeight = originalHeight;
	if ((imageSpecs.getMirroringType() == MIRROR_LEFT) || (imageSpecs.getMirroringType() == MIRROR_RIGHT)){
		mirrorWidth = 2 * originalWidth;
	}
	if ((imageSpecs.getMirroringType() == MIRROR_TOP) || (imageSpecs.getMirroringType() == MIRROR_BOTTOM)){
		mirrorHeight = 2 * originalHeight;
	}

	int beginTimeIndex = exportSpecs.getTimeSpecs().getBeginTimeIndex();
	int endTimeIndex = exportSpecs.getTimeSpecs().getEndTimeIndex();
	String[] varNames = exportSpecs.getVariableSpecs().getVariableNames();
	ExportOutput[] exportOutputArr = null;
	if(imageSpecs.getFormat() == ExportConstants.ANIMATED_GIF){
		exportOutputArr = new ExportOutput[varNames.length];
	}else{
		exportOutputArr = new ExportOutput[varNames.length * (endTimeIndex - beginTimeIndex + 1)];
	}
	double progress = 0.0;
	double progressIncr = 1.0/(varNames.length*(endTimeIndex - beginTimeIndex + 1));
	
	//*****GIF and AnimatedGif only section
	GIFUtils.GIFImage gifImage = null;
	ByteArrayOutputStream bytesOut = null;
	GIFOutputStream gifOut = null;
	double interval = allTimes[endTimeIndex] - allTimes[beginTimeIndex];
	double duration = imageSpecs.getDuration();
	//*****End GIF and AnimatedGif only section
	
	for (int varNameIndex=0;varNameIndex<varNames.length;varNameIndex++) {
		
		if(imageSpecs.getFormat() == ExportConstants.ANIMATED_GIF){
			bytesOut = new ByteArrayOutputStream();
			gifOut = new GIFOutputStream(bytesOut);
		}
		
		for (int timeIndex=beginTimeIndex;timeIndex<=endTimeIndex;timeIndex++) {
			exportServiceImpl.fireExportProgress(jobID, vcdID, (imageSpecs.getFormat() == ExportConstants.GIF?"GIF":"JPG"), progress);
			progress+= progressIncr;
			String timeIndexS = (timeIndex<1000?"0":"")+(timeIndex<100?"0":"")+(timeIndex<10?"0":"")+timeIndex;
			String dataID = "_" + Coordinate.getNormalAxisPlaneName(sliceNormalAxis) + "_" + sliceNumber + "_" + varNames[varNameIndex] +"_" + timeIndexS;
			offScreenRenderer.setVarAndTimeAndDisplay(varNames[varNameIndex], allTimes[timeIndex], imageSpecs.getDisplayPreferences()[varNameIndex]);
			int[] pixels = offScreenRenderer.getPixelsRGB(imageScale,imageSpecs.getMembraneScaling(),imageSpecs.getMeshMode());
			pixels = ExportUtils.extendMirrorPixels(pixels,originalWidth,originalHeight, imageSpecs.getMirroringType());
			int exportOutputIndex = varNameIndex * (endTimeIndex - beginTimeIndex + 1) + timeIndex - beginTimeIndex;
			if(exportSpecs.getFormat() == ExportConstants.FORMAT_GIF && imageSpecs.getFormat() == ExportConstants.GIF){
				bytesOut = new ByteArrayOutputStream();
				gifOut = new GIFOutputStream(bytesOut);
				gifImage = new GIFUtils.GIFImage(pixels,mirrorWidth);
				gifImage.write(gifOut);
				gifOut.close();
				byte[] data = bytesOut.toByteArray();
				exportOutputArr[exportOutputIndex] =
					new ExportOutput(true, ".gif", vcdID.getID(), dataID, data);
			}else if(exportSpecs.getFormat() == ExportConstants.FORMAT_JPEG && imageSpecs.getFormat() == ExportConstants.JPEG){
				BufferedImage bufferedImage = new BufferedImage(mirrorWidth, mirrorHeight, BufferedImage.TYPE_INT_RGB);
				int[] bufferData = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
				System.arraycopy(pixels, 0, bufferData, 0, pixels.length);
				byte[] data =
					FormatSpecificSpecs.encodeJPEG(bufferedImage, imageSpecs.getcompressionQuality(), mirrorWidth, mirrorHeight, 1, 32, false).getDataBytes();
				exportOutputArr[exportOutputIndex] =
					new ExportOutput(true, ".jpg", vcdID.getID(), dataID, data);
			}else if(exportSpecs.getFormat() == ExportConstants.FORMAT_ANIMATED_GIF && imageSpecs.getFormat() == ExportConstants.ANIMATED_GIF){
				int imageDuration = 0;
				if (timeIndex == endTimeIndex) {
					if (imageSpecs.getLoopingMode() != 0 || (beginTimeIndex == endTimeIndex)){
						imageDuration = 0;
					}else{
						imageDuration = (int)Math.ceil((allTimes[timeIndex] - allTimes[timeIndex - 1]) / interval * duration / 10); // 100th of second
					}
				} else {
					imageDuration = (int)Math.ceil((allTimes[timeIndex + 1] - allTimes[timeIndex]) / interval * duration / 10); // 100th of second
				}
				if (timeIndex == beginTimeIndex) {
					gifImage = new GIFUtils.GIFImage(pixels, mirrorWidth);
					gifImage.setDelay(imageDuration);
				} else {
					gifImage.addImage(pixels, mirrorWidth, true);
					gifImage.setDelay(timeIndex - beginTimeIndex, imageDuration);
				}
			}
		}
		if(imageSpecs.getFormat() == ExportConstants.ANIMATED_GIF){
			String dataID = "_" + Coordinate.getNormalAxisPlaneName(sliceNormalAxis) + "_" + sliceNumber + "_" + varNames[varNameIndex] + "_animated";
			gifImage.setIterationCount(imageSpecs.getLoopingMode());
			gifImage.write(gifOut);
			gifOut.close();
			byte[] data = bytesOut.toByteArray();
			exportOutputArr[varNameIndex] = new ExportOutput(true, ".gif", vcdID.getID(), dataID, data);
		}
	}
	return exportOutputArr;
}

}