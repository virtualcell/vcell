package cbit.vcell.export.server;

import cbit.vcell.solver.SimulationInfo;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solvers.CartesianMesh;
import java.util.*;
import cbit.vcell.geometry.Curve;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import cbit.vcell.geometry.gui.CurveRenderer;

import cbit.vcell.simdata.*;
import cbit.image.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.simdata.gui.*;
import java.awt.*;

import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.Range;
import org.vcell.util.VCDataIdentifier;
import org.vcell.util.document.User;

import cbit.vcell.server.*;
/**
 * Insert the type's description here.
 * Creation date: (3/1/2001 11:37:38 PM)
 * @author: Ion Moraru
 */
public class PDEOffscreenRenderer {
	private ServerPDEDataContext serverPDEDataContext = null;
	private DisplayAdapterService displayAdapterService = new DisplayAdapterService();
	private int slice = 0;
	private int normalAxis = 0;
	private java.util.Hashtable membranesAndValues = null;
	private boolean bHideMembraneOutline = true;
	boolean bNeedsDefaultScaling;

	
private void setDefaultScaling(){
	double[] values = getServerPDEDataContext().getDataValues();
	double min=values[0];
	double max=min;
	for (int i = 0; i < values.length; i++) {
		if(values[i] < min){min = values[i];}
		if(values[i] > max){max = values[i];}
	}
	getDisplayAdapterService().setActiveScaleRange(new Range(min,max));
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 4:49:39 PM)
 */
private int[] getCurveColors(java.util.Hashtable curvesAndMembraneIndexes, Curve curve,MeshDisplayAdapter meshDisplayAdapter) {
	//
	int[] membraneIndexes = (int[]) curvesAndMembraneIndexes.get(curve);
	double[] membraneValues = meshDisplayAdapter.getDataValuesForMembraneIndexes(membraneIndexes,getServerPDEDataContext().getDataValues(),getServerPDEDataContext().getDataIdentifier().getVariableType());
	if(membraneValues != null){
		if(bNeedsDefaultScaling){setDefaultScaling();}
		int[] valueColors = new int[membraneValues.length];
		for (int i = 0; i < membraneValues.length; i += 1) {
			valueColors[i] = getDisplayAdapterService().getColorFromValue(membraneValues[i]);
		}
		return valueColors;
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:37:01 AM)
 * @return cbit.image.DisplayAdapterService
 */
private cbit.image.DisplayAdapterService getDisplayAdapterService() {
	return displayAdapterService;
}
public Dimension getImageDimension(int meshMode,int imageScale) {
	ImagePaneModel imagePaneModel = new ImagePaneModel();
	imagePaneModel.setMode(meshMode);
	imagePaneModel.setZoom(imageScale);
	Dimension dim = getImageDimensionUnscaled();
	dim.width = imagePaneModel.getScaledLength(dim.width);
	dim.height = imagePaneModel.getScaledLength(dim.height);
	return dim;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:03:46 AM)
 * @return java.awt.Dimension
 */
private Dimension getImageDimensionUnscaled() {
	CartesianMesh mesh = getServerPDEDataContext().getCartesianMesh();
	switch (getNormalAxis()){
		case Coordinate.X_AXIS:{
			//
			// YZ plane
			//
			return new Dimension(mesh.getSizeY(),mesh.getSizeZ());
		}
		case Coordinate.Y_AXIS:{
			//
			// ZX plane
			//
			return new Dimension(mesh.getSizeZ(),mesh.getSizeX());
		}
		case Coordinate.Z_AXIS:{
			//
			// XY plane
			//
			return new Dimension(mesh.getSizeX(),mesh.getSizeY());

		}
		default:{
			throw new IllegalArgumentException("unexpected normal axis "+getNormalAxis());
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:47:07 AM)
 * @return int
 */
private int getNormalAxis() {
	return normalAxis;
}

private BufferedImage getScaledRGBVolume(CartesianMesh mesh,int meshMode,int imageScale,boolean bBackground){
	int width = (int)getImageDimensionUnscaled().getWidth();
	int height = (int)getImageDimensionUnscaled().getHeight();
	double[] volumeData = new double[width*height];
	if(!bBackground){
		double values[] = getServerPDEDataContext().getDataValues();
		int dataIndices[] = getServerPDEDataContext().getCartesianMesh().getVolumeSliceIndices(getNormalAxis(),getSlice());
		for (int i = 0; i < dataIndices.length; i++) {
			volumeData[i] = values[dataIndices[i]];
		}
		if(bNeedsDefaultScaling){setDefaultScaling();}
	}
	
	SourceDataInfo sourceDataInfo =
		new SourceDataInfo(SourceDataInfo.RAW_VALUE_TYPE,
				volumeData,
				mesh.getExtent(), mesh.getOrigin(),
				new Range(0,1),
				0, width, 1,
				height, width,
				1, 0);
	ImagePaneModel imagePaneModel = new ImagePaneModel();
	imagePaneModel.setSourceData(sourceDataInfo);
	imagePaneModel.setBackgroundColor(Color.black);
	imagePaneModel.setDisplayAdapterService(getDisplayAdapterService());
	imagePaneModel.setMode(meshMode);
	imagePaneModel.setZoom(imageScale);
	imagePaneModel.setViewport(new Rectangle(imagePaneModel.getScaledLength(width),imagePaneModel.getScaledLength(height)));
	imagePaneModel.updateViewPortImage();
	if(bBackground){
		int[] internalBuffer = ((DataBufferInt)(imagePaneModel.getViewPortImage().getRaster().getDataBuffer())).getData();
		Arrays.fill(internalBuffer, getDisplayAdapterService().getSpecialColors()[DisplayAdapterService.NULL_COLOR_OFFSET]);
	}
	return imagePaneModel.getViewPortImage();
	
//	int width = (int)getImageDimension().getWidth();
//	int height = (int)getImageDimension().getHeight();
//
//	if(bBackground){
//		BufferedImage bufferedImage = new BufferedImage(width*imageScale,height*imageScale,BufferedImage.TYPE_INT_ARGB);
////		bufferedImage.setRGB(0,0,width,height,rgbArray,0,width);
//		return bufferedImage;
//	}
//	double data[] = getServerPDEDataContext().getDataValues();
//	int dataIndices[] = getServerPDEDataContext().getCartesianMesh().getVolumeSliceIndices(getNormalAxis(),getSlice());
//	int rgbArray[] = new int[dataIndices.length*imageScale*imageScale];
//	int index = 0;
//	int scaledWidth = width*imageScale;
//	for(int y=0;y<height;y++){
//		int scaledIndex = y*imageScale*scaledWidth;
//		for(int x=0;x<width;x++){
//			int pixelColor = getDisplayAdapterService().getColorFromValue(data[dataIndices[index]]);
//			for(int subY=0;subY<imageScale;subY++){
//				int subScaledIndex = scaledIndex + subY*scaledWidth + x*imageScale;
//				for(int subX=0;subX<imageScale;subX++){
//					rgbArray[subScaledIndex+subX] = pixelColor;
//				}
//			}
//			index++;
//			scaledIndex+= imageScale;
//		}
//	}
//	BufferedImage bufferedImage = new BufferedImage(width*imageScale,height*imageScale,BufferedImage.TYPE_INT_ARGB);
//	bufferedImage.setRGB(0,0,width,height,rgbArray,0,width);
//	return bufferedImage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 11:54:44 PM)
 * @return int[]
 */
public int[] getPixelsRGB(int imageScale,int membrScale,int meshMode) {
	if (getServerPDEDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME)) {
//		int width = (int)getImageDimension().getWidth();
//		int height = (int)getImageDimension().getHeight();
//		BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
//		double data[] = getServerPDEDataContext().getDataValues();
//		int startX = 0;
//		int startY = 0;
//		int offset = 0;
		CartesianMesh mesh = getServerPDEDataContext().getCartesianMesh();
		MeshDisplayAdapter meshDisplayAdapter = new MeshDisplayAdapter(mesh);
//		int dataIndices[] = mesh.getVolumeSliceIndices(getNormalAxis(),getSlice());
//		int rgbArray[] = new int[dataIndices.length];
//		for (int i=0;i<rgbArray.length;i++){
//			rgbArray[i] = getDisplayAdapterService().getColorFromValue(data[dataIndices[i]]);
//		}
//		bufferedImage.setRGB(startX,startY,width,height,rgbArray,offset,width);
		BufferedImage bufferedImage = getScaledRGBVolume(mesh,meshMode,imageScale,false);
		//
		// apply curve renderer
		//
		if (!bHideMembraneOutline){
			cbit.vcell.geometry.gui.CurveRenderer curveRenderer =
				new cbit.vcell.geometry.gui.CurveRenderer(getDisplayAdapterService());
			curveRenderer.setNormalAxis(getNormalAxis());

			org.vcell.util.Origin origin = mesh.getOrigin();
			org.vcell.util.Extent extent = mesh.getExtent();
			curveRenderer.setWorldOrigin(new org.vcell.util.Coordinate(origin.getX(),origin.getY(),origin.getZ()));
//			double pixelScaleX = extent.getX()/mesh.getSizeX();
//			double pixelScaleY = extent.getY()/mesh.getSizeY();
//			double pixelScaleZ = extent.getZ()/mesh.getSizeZ();
			Coordinate pixeldelta = getPixelDelta(extent, mesh, meshMode,imageScale);
			curveRenderer.setWorldDelta(new org.vcell.util.Coordinate(pixeldelta.getX(),pixeldelta.getY(),pixeldelta.getZ()));
			//int numMembraneElements = getServerPDEDataContext().getCartesianMesh().getDataLength(VariableType.MEMBRANE);
			//double membraneValues[] = new double[numMembraneElements];
			//membraneValues = null;
			//Hashtable curveHash = meshDisplayAdapter.getCurvesFromMembranes(getNormalAxis(),getSlice(),membraneValues);
			Hashtable curvesAndMembraneIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(getNormalAxis(),getSlice());
			if (curvesAndMembraneIndexes!=null){
				Curve curves[] = (Curve[])org.vcell.util.BeanUtils.getArray(curvesAndMembraneIndexes.keys(),Curve.class);
				for (int i=0;curves!=null && i<curves.length;i++){
					curveRenderer.addCurve(curves[i]);
					curveRenderer.renderPropertySegmentColors(curves[i],getCurveColors(curvesAndMembraneIndexes,curves[i],meshDisplayAdapter));
					curveRenderer.renderPropertyLineWidthMultiplier(curves[i],membrScale);
				}
				Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
				curveRenderer.setAntialias(false);//must be false or could get more than 256 colors
				curveRenderer.draw(g);
			}
		}
		return ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
//		return bufferedImage.getRGB(startX,startY,width,height,rgbArray,offset,width);
	} else if (getServerPDEDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE)) {
//		int width = (int)getImageDimension().getWidth();
//		int height = (int)getImageDimension().getHeight();
//		BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
//		double membraneData[] = getServerPDEDataContext().getDataValues();
//		int startX = 0;
//		int startY = 0;
//		int offset = 0;
		CartesianMesh mesh = getServerPDEDataContext().getCartesianMesh();
		MeshDisplayAdapter meshDisplayAdapter = new MeshDisplayAdapter(mesh);
//		int dataIndices[] = mesh.getVolumeSliceIndices(getNormalAxis(),getSlice());
//		int rgbArray[] = new int[dataIndices.length];
//		for (int i=0;i<rgbArray.length;i++){
//			rgbArray[i] = Color.black.getRGB();
//		}
//		bufferedImage.setRGB(startX,startY,width,height,rgbArray,offset,width);
		BufferedImage bufferedImage = getScaledRGBVolume(mesh,meshMode,imageScale,true);
		//
		// apply curve renderer
		//
		cbit.vcell.geometry.gui.CurveRenderer curveRenderer =
			new cbit.vcell.geometry.gui.CurveRenderer(getDisplayAdapterService());
		curveRenderer.setNormalAxis(getNormalAxis());

		org.vcell.util.Origin origin = mesh.getOrigin();
		org.vcell.util.Extent extent = mesh.getExtent();
		curveRenderer.setWorldOrigin(new org.vcell.util.Coordinate(origin.getX(),origin.getY(),origin.getZ()));
		//following caluclations assume "mesh mode" from ImagePaneModel.getScaledLength(...)
//		double pixelScaleX = extent.getX()/imageScale * 2 * (mesh.getSizeX() - 1);
//		double pixelScaleY = extent.getY()/imageScale * 2 * (mesh.getSizeY() - 1);
//		double pixelScaleZ = extent.getZ()/imageScale * 2 * (mesh.getSizeZ() - 1);
		Coordinate pixeldelta = getPixelDelta(extent, mesh,meshMode, imageScale);
		curveRenderer.setWorldDelta(new org.vcell.util.Coordinate(pixeldelta.getX(),pixeldelta.getY(),pixeldelta.getZ()));
		//int numMembraneElements = getServerPDEDataContext().getCartesianMesh().getDataLength(VariableType.MEMBRANE);
		//Hashtable curveHash = meshDisplayAdapter.getCurvesFromMembranes(getNormalAxis(),getSlice(),membraneData);
		Hashtable curvesAndMembraneIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(getNormalAxis(),getSlice());
		if (curvesAndMembraneIndexes!=null){
			Curve curves[] = (Curve[])org.vcell.util.BeanUtils.getArray(curvesAndMembraneIndexes.keys(),Curve.class);
			for (int i=0;curves!=null && i<curves.length;i++){
				curveRenderer.addCurve(curves[i]);
				curveRenderer.renderPropertySegmentColors(curves[i],getCurveColors(curvesAndMembraneIndexes,curves[i],meshDisplayAdapter));
				curveRenderer.renderPropertyLineWidthMultiplier(curves[i],membrScale);
			}
			Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
			curveRenderer.setAntialias(false);
			curveRenderer.draw(g);
		}
		return ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
//		return bufferedImage.getRGB(startX,startY,width,height,rgbArray,offset,width);
	} else {
		throw new RuntimeException("unsupported VariableType "+getServerPDEDataContext().getDataIdentifier().getVariableType());
	}
}

private Coordinate getPixelDelta(Extent extent,CartesianMesh mesh,int meshMode,int imageScale){
	ImagePaneModel imagePaneModel = new ImagePaneModel();
	imagePaneModel.setMode(meshMode);
	imagePaneModel.setZoom(imageScale);
	double pixelScaleX = extent.getX()/imagePaneModel.getScaledLength(mesh.getSizeX());
	double pixelScaleY = extent.getY()/imagePaneModel.getScaledLength(mesh.getSizeY());
	double pixelScaleZ = extent.getZ()/imagePaneModel.getScaledLength(mesh.getSizeZ());
	return new Coordinate(pixelScaleX,pixelScaleY,pixelScaleZ);

}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:37:01 AM)
 * @return cbit.vcell.simdata.ServerPDEDataContext
 */
private cbit.vcell.simdata.ServerPDEDataContext getServerPDEDataContext() {
	return serverPDEDataContext;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:47:07 AM)
 * @return int
 */
private int getSlice() {
	return slice;
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 11:59:11 PM)
 * @param displayPreferences cbit.vcell.simdata.gui.DisplayPreferences
 */
public void setDisplayPreferences(DisplayPreferences displayPreferences) {
	bNeedsDefaultScaling = displayPreferences.scaleIsDefault();
	getDisplayAdapterService().setActiveScaleRange(displayPreferences.getScaleSettings());
	getDisplayAdapterService().setActiveColorModelID(displayPreferences.getColorMode());
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/01 11:16:37 AM)
 * @return boolean
 * @param hideMembraneOutline boolean
 */
public void setHideMembraneOutline(boolean hideMembraneOutline) {
	bHideMembraneOutline = hideMembraneOutline;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:47:07 AM)
 * @param newNormalAxis int
 */
public void setNormalAxis(int newNormalAxis) {
	normalAxis = newNormalAxis;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:37:01 AM)
 * @param newServerPDEDataContext cbit.vcell.simdata.ServerPDEDataContext
 */
private void setServerPDEDataContext(cbit.vcell.simdata.ServerPDEDataContext newServerPDEDataContext) {
	serverPDEDataContext = newServerPDEDataContext;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:47:07 AM)
 * @param newSlice int
 */
public void setSlice(int newSlice) {
	slice = newSlice;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:19:20 AM)
 * @param timepoint double
 */
public void setTimepoint(double timepoint) throws Exception {
	getServerPDEDataContext().setTimePoint(timepoint);
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 11:57:27 PM)
 * @param variableName java.lang.String
 */
public void setVariable(String variableName) throws Exception {
	getServerPDEDataContext().setVariableName(variableName);
}

/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:08:22 AM)
 * @param dataSetController cbit.vcell.server.DataSetController
 * @param simulationIdentifier java.lang.String
 */
public PDEOffscreenRenderer(User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID) throws Exception {
	setServerPDEDataContext(new ServerPDEDataContext(user, dataServerImpl, vcdID));
	getDisplayAdapterService().addColorModelForValues(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.createGraySpecialColors(), "Gray");
	getDisplayAdapterService().addColorModelForValues(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.createBlueRedSpecialColors(), "BlueRed");
}
}
