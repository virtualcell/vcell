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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Hashtable;

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.Range;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import cbit.image.ImagePaneModel;
import cbit.image.SourceDataInfo;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.ServerPDEDataContext;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.MeshDisplayAdapter;

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
	private BitSet domainValid;


	
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 4:49:39 PM)
 */
private int[] getCurveColors(Hashtable<SampledCurve, int[]> curvesAndMembraneIndexes, Curve curve,MeshDisplayAdapter meshDisplayAdapter) {

	int[] membraneIndexes = (int[]) curvesAndMembraneIndexes.get(curve);
	double[] membraneValues = meshDisplayAdapter.getDataValuesForMembraneIndexes(membraneIndexes,getServerPDEDataContext().getDataValues(),getServerPDEDataContext().getDataIdentifier().getVariableType());
	int notInDomainColor = getDisplayAdapterService().getSpecialColors()[DisplayAdapterService.NOT_IN_DOMAIN_COLOR_OFFSET];
	if(membraneValues != null){
		int[] valueColors = new int[membraneValues.length];
		for (int i = 0; i < membraneValues.length; i += 1) {
			int valueColor = getDisplayAdapterService().getColorFromValue(membraneValues[i]);
			valueColors[i] = (domainValid!=null?(domainValid.get(membraneIndexes[i])?valueColor:notInDomainColor):valueColor);
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

/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:47:07 AM)
 * @return int
 */
private int getNormalAxis() {
	return normalAxis;
}

private BufferedImage getScaledRGBVolume(CartesianMesh mesh,int meshMode,int imageScale,boolean bBackground){
	Dimension dim = FormatSpecificSpecs.getMeshDimensionUnscaled(getNormalAxis(),getServerPDEDataContext().getCartesianMesh());
	int width = (int)dim.getWidth();
	int height = (int)dim.getHeight();
	double[] volumeData = new double[width*height];
	if(!bBackground){
		//if(bNeedsDefaultScaling){setDefaultScaling();}
		double notInDomainValue = getDisplayAdapterService().getValueDomain().getMin()-1.0;
		double values[] = getServerPDEDataContext().getDataValues();
		int dataIndices[] = getServerPDEDataContext().getCartesianMesh().getVolumeSliceIndices(getNormalAxis(),getSlice());
		for (int i = 0; i < dataIndices.length; i++) {
			double value = values[dataIndices[i]];
			if (domainValid!=null){
				if (domainValid.get(dataIndices[i]) || domainValid.isEmpty()){
					volumeData[i] = value;
				}else{
					volumeData[i] = notInDomainValue;
				}
			}else{
				volumeData[i] = value;
			}
		}
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
//	getDisplayAdapterService().setActiveScaleRange(new Range(0,1));
	imagePaneModel.setMode(meshMode);
	imagePaneModel.setZoom(imageScale);
	imagePaneModel.setViewport(new Rectangle(imagePaneModel.getScaledLength(width),imagePaneModel.getScaledLength(height)));
	imagePaneModel.updateViewPortImage();
	if(bBackground){
		int[] internalBuffer = ((DataBufferInt)(imagePaneModel.getViewPortImage().getRaster().getDataBuffer())).getData();
		Arrays.fill(internalBuffer, getDisplayAdapterService().getSpecialColors()[DisplayAdapterService.NULL_COLOR_OFFSET]);
	}
	
	BufferedImage bufferedImage = imagePaneModel.getViewPortImage();
	imagePaneModel.setDisplayAdapterService(null);
	return bufferedImage;
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 11:54:44 PM)
 * @return int[]
 */
public int[] getPixelsRGB(int imageScale,int membrScale,int meshMode,int volVarMembrOutlineThickness) {
	if (getServerPDEDataContext().getDataIdentifier().getVariableType().equals(VariableType.VOLUME) ||
		getServerPDEDataContext().getDataIdentifier().getVariableType().equals(VariableType.POSTPROCESSING)) {
		CartesianMesh mesh = getServerPDEDataContext().getCartesianMesh();
		MeshDisplayAdapter meshDisplayAdapter = new MeshDisplayAdapter(mesh);
		BufferedImage bufferedImage = getScaledRGBVolume(mesh,meshMode,imageScale,false);
		//
		// apply curve renderer
		//
		if (!getServerPDEDataContext().getDataIdentifier().getVariableType().equals(VariableType.POSTPROCESSING) && volVarMembrOutlineThickness > 0){
			cbit.vcell.geometry.CurveRenderer curveRenderer =
				new cbit.vcell.geometry.CurveRenderer(getDisplayAdapterService());
			curveRenderer.setNormalAxis(getNormalAxis());

			org.vcell.util.Origin origin = mesh.getOrigin();
			org.vcell.util.Extent extent = mesh.getExtent();
			curveRenderer.setWorldOrigin(new org.vcell.util.Coordinate(origin.getX(),origin.getY(),origin.getZ()));
			Coordinate pixeldelta = getPixelDelta(extent, mesh, meshMode,imageScale);
			curveRenderer.setWorldDelta(new org.vcell.util.Coordinate(pixeldelta.getX(),pixeldelta.getY(),pixeldelta.getZ()));
			Hashtable<SampledCurve, int[]> curvesAndMembraneIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(getNormalAxis(),getSlice());
			if (curvesAndMembraneIndexes!=null){
				Curve curves[] = (Curve[])org.vcell.util.BeanUtils.getArray(curvesAndMembraneIndexes.keys(),Curve.class);
				for (int i=0;curves!=null && i<curves.length;i++){
					curveRenderer.addCurve(curves[i]);
					curveRenderer.renderPropertySegmentColors(curves[i],null/*getCurveColors(curvesAndMembraneIndexes,curves[i],meshDisplayAdapter)*/);
					curveRenderer.renderPropertyLineWidthMultiplier(curves[i],volVarMembrOutlineThickness);
				}
				Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
				curveRenderer.setAntialias(false);//must be false or could get more than 256 colors
				curveRenderer.draw(g);
			}
		}
		return ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
	} else if (getServerPDEDataContext().getDataIdentifier().getVariableType().equals(VariableType.MEMBRANE)) {
		CartesianMesh mesh = getServerPDEDataContext().getCartesianMesh();
		MeshDisplayAdapter meshDisplayAdapter = new MeshDisplayAdapter(mesh);
		BufferedImage bufferedImage = getScaledRGBVolume(mesh,meshMode,imageScale,true);
		//
		// apply curve renderer
		//
		cbit.vcell.geometry.CurveRenderer curveRenderer =
			new cbit.vcell.geometry.CurveRenderer(getDisplayAdapterService());
		curveRenderer.setNormalAxis(getNormalAxis());

		org.vcell.util.Origin origin = mesh.getOrigin();
		org.vcell.util.Extent extent = mesh.getExtent();
		curveRenderer.setWorldOrigin(new org.vcell.util.Coordinate(origin.getX(),origin.getY(),origin.getZ()));
		Coordinate pixeldelta = getPixelDelta(extent, mesh,meshMode, imageScale);
		curveRenderer.setWorldDelta(new org.vcell.util.Coordinate(pixeldelta.getX(),pixeldelta.getY(),pixeldelta.getZ()));
		Hashtable<SampledCurve, int[]> curvesAndMembraneIndexes = meshDisplayAdapter.getCurvesAndMembraneIndexes(getNormalAxis(),getSlice());
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

public void setVarAndTimeAndDisplay(String varName,double timepoint,DisplayPreferences displayPreferences) throws DataAccessException{
	getServerPDEDataContext().setVariableName(varName);
	getServerPDEDataContext().setTimePoint(timepoint);
	domainValid = (displayPreferences==null?null:(displayPreferences.getDomainValid()==null?null:displayPreferences.getDomainValid()));
	Range valueDomain = BeanUtils.calculateValueDomain(getServerPDEDataContext().getDataValues(),domainValid);
	ExportSpecs.setupDisplayAdapterService(displayPreferences,getDisplayAdapterService(),valueDomain);
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 1:08:22 AM)
 * @param dataSetController cbit.vcell.server.DataSetController
 * @param simulationIdentifier java.lang.String
 */
public PDEOffscreenRenderer(OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID) throws Exception {
	setServerPDEDataContext(new ServerPDEDataContext(outputContext,user, dataServerImpl, vcdID));
	getDisplayAdapterService().addColorModelForValues(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.createGraySpecialColors(), DisplayAdapterService.GRAY);
	getDisplayAdapterService().addColorModelForValues(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.createBlueRedSpecialColors(), DisplayAdapterService.BLUERED);
}
}
