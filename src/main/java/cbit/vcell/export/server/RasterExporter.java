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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.mapping.chombo.ChomboVtkFileWriter;
import org.vcell.vis.mapping.vcell.CartesianMeshVtkFileWriter;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import cbit.image.ImagePlaneManager;
import cbit.image.SourceDataInfo;
import cbit.vcell.export.AVS_UCD_Exporter;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.nrrd.NrrdInfo.NRRDAxisNames;
import cbit.vcell.export.nrrd.NrrdWriter;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.RegionImage.MembraneElementIdentifier;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo.PostProcessDataType;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.MembraneElement;
import cbit.vcell.solvers.MeshDisplayAdapter;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2004 12:53:34 PM)
 * @author: Ion Moraru
 */
public class RasterExporter implements ExportConstants {
	private ExportServiceImpl exportServiceImpl = null;

	private static final int VTK_HEXAHEDRON = 12;
	private static final int VTK_QUAD = 9;
	private static final int VTK_LINE = 3;

/**
 * Insert the method's description here.
 * Creation date: (4/27/2004 1:18:37 PM)
 * @param exportServiceImpl cbit.vcell.export.server.ExportServiceImpl
 */
public RasterExporter(ExportServiceImpl exportServiceImpl) {
	this.exportServiceImpl = exportServiceImpl;
}


private static class NRRDHelper {
	private ISize isize;
	private Coordinate spacing;
	private boolean bPostProcessInfo = false;
	public NRRDHelper(String exportVarName,ISize isize,Extent extent,DataProcessingOutputInfo dataProcessingOutputInfo) throws DataAccessException{
		if(dataProcessingOutputInfo != null){
			for(int varIndex=0;varIndex<dataProcessingOutputInfo.getVariableNames().length;varIndex++){
				if(dataProcessingOutputInfo.getVariableNames()[varIndex].equals(exportVarName)){
					if(!dataProcessingOutputInfo.getPostProcessDataType(exportVarName).equals(PostProcessDataType.image)){
						throw new DataAccessException("Export nrrd only implemented for 'image' post-process data");
					}
					this.bPostProcessInfo = true;
					this.isize = new ISize(dataProcessingOutputInfo.getVariableISize(exportVarName).getX(),
										dataProcessingOutputInfo.getVariableISize(exportVarName).getY(),
										dataProcessingOutputInfo.getVariableISize(exportVarName).getZ());
					this.spacing = new Coordinate(dataProcessingOutputInfo.getVariableExtent(exportVarName).getX()/isize.getX(),
											dataProcessingOutputInfo.getVariableExtent(exportVarName).getY()/isize.getY(),
											dataProcessingOutputInfo.getVariableExtent(exportVarName).getZ()/isize.getZ());
					break;
				}
			}
		}
		if(!bPostProcessInfo){
			this.isize = isize;
			this.spacing = new Coordinate(extent.getX() / isize.getX(),
					extent.getY() / isize.getY(),
					extent.getZ() / isize.getZ());
		}
	}
	
	public boolean isSizeEqual(NRRDHelper obj) {
		return obj instanceof NRRDHelper &&
				((NRRDHelper)obj).isize.compareEqual(isize) &&
				((NRRDHelper)obj).spacing.equals(spacing);
//				&& ((NRRDHelper)obj).bPostProcessInfo == bPostProcessInfo;
	}

	public ISize getIsize() {
		return isize;
	}
	public Coordinate getSpacing() {
		return spacing;
	}
	public boolean isPostProcessInfo(){
		return bPostProcessInfo;
	}
	public static int calculateNumTimes(TimeSpecs timeSpecs){
		return timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1;
	}
	public NrrdInfo createSingleFullNrrdInfo(FileDataContainerManager fileDataContainerManager,VCDataIdentifier vcdID,VariableSpecs variableSpecs,RasterSpecs rasterSpecs,TimeSpecs timeSpecs) throws IOException{
		String simID = vcdID.getID();
		int NUM_TIMES = calculateNumTimes(timeSpecs);
		NrrdInfo nrrdInfo = NrrdInfo.createBasicNrrdInfo(
				5,
				new int[] {getIsize().getX(),getIsize().getY(),getIsize().getZ(),NUM_TIMES, variableSpecs.getVariableNames().length},
				"double",
				"raw",
				NrrdInfo.createXYZTVMap()
			);
			nrrdInfo.setSpacings(new double[] {getSpacing().getX(),getSpacing().getY(),getSpacing().getZ(),
				Double.NaN,	// timepoints can have irregular intervals
				Double.NaN  // not meaningful for variables
			});

		nrrdInfo.setCanonicalFileNamePrefix(simID + "_" + NUM_TIMES + "times_" + variableSpecs.getVariableNames().length + "vars");
		
		StringBuffer sb = new StringBuffer();
		sb.append(nrrdInfo.getDimensionDescription()+" VCData("+simID+") Vars(");
		for(int i=0;i<variableSpecs.getVariableNames().length;i++){
			sb.append(variableSpecs.getVariableNames()[i]+(i!=(variableSpecs.getVariableNames().length-1)?",":""));
		}
		sb.append(") Times(");
		for(int i=timeSpecs.getBeginTimeIndex();i<= timeSpecs.getEndTimeIndex();i++){
			sb.append(timeSpecs.getAllTimes()[i]+(i!=timeSpecs.getEndTimeIndex()?",":""));
		}
		sb.append(")");
		nrrdInfo.setMainComment(sb.toString());

		nrrdInfo.setCenters(new String[] {"cell", "cell", "cell", "cell", "???"});

		nrrdInfo.setSeparateHeader(rasterSpecs.isSeparateHeader());
		// make datafile and update info
		nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
		return nrrdInfo;
	}
	public NrrdInfo createTimeFullNrrdInfo(FileDataContainerManager fileDataContainerManager,VCDataIdentifier vcdID,VariableSpecs variableSpecs,double time,RasterSpecs rasterSpecs) throws IOException{
		String simID = vcdID.getID();
		NrrdInfo nrrdInfo = NrrdInfo.createBasicNrrdInfo(
			4,
			new int[] {getIsize().getX(),getIsize().getY(), getIsize().getZ(),variableSpecs.getVariableNames().length},
			"double",
			"raw",
			NrrdInfo.createXYZVMap()
		);
		nrrdInfo.setCanonicalFileNamePrefix(simID + "_" + formatTime(time) + "time_" + variableSpecs.getVariableNames().length + "vars");

		nrrdInfo.setContent(nrrdInfo.getDimensionDescription()+" VCData from " + simID);
		nrrdInfo.setCenters(new String[] {"cell", "cell", "cell","???"});
		nrrdInfo.setSpacings(new double[] {getSpacing().getX(),getSpacing().getY(),getSpacing().getZ(),
			Double.NaN  // not meaningful for variables
		});
		nrrdInfo.setSeparateHeader(rasterSpecs.isSeparateHeader());
		// make datafile and update info						
		nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
		return nrrdInfo;
	}
	public NrrdInfo createVariableFullNrrdInfo(FileDataContainerManager fileDataContainerManager,VCDataIdentifier vcdID,String variableName,TimeSpecs timeSpecs,RasterSpecs rasterSpecs) throws IOException{
		String simID = vcdID.getID();
		int NUM_TIMES = calculateNumTimes(timeSpecs);
		NrrdInfo nrrdInfo = NrrdInfo.createBasicNrrdInfo(
			4,
			new int[] {getIsize().getX(),	getIsize().getY(),getIsize().getZ(), NUM_TIMES},
			"double",
			"raw",
			NrrdInfo.createXYZTMap()
		);
		nrrdInfo.setCanonicalFileNamePrefix(simID + "_" + NUM_TIMES + "times_" + variableName + "vars");

		nrrdInfo.setContent(nrrdInfo.getDimensionDescription()+" VCData from " + simID);
		nrrdInfo.setCenters(new String[] {"cell", "cell", "cell", "cell"});
		nrrdInfo.setSpacings(new double[] {getSpacing().getX(),getSpacing().getY(),getSpacing().getZ(),
			Double.NaN,	// timepoints can have irregular intervals
		});
		nrrdInfo.setSeparateHeader(rasterSpecs.isSeparateHeader());
		// make datafile and update info
		nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
		return nrrdInfo;

	}
	public static NRRDHelper getSizeCheckedNrrdHelper(VariableSpecs variableSpecs,ISize isize,Extent extent,DataProcessingOutputInfo dataProcessingOutputInfo) throws DataAccessException{
		NRRDHelper lastNRRDHelper = null;
		for(int nameIndex=0;nameIndex<variableSpecs.getVariableNames().length;nameIndex++){
			NRRDHelper currentNRDHelper = new NRRDHelper(variableSpecs.getVariableNames()[nameIndex], isize,extent, dataProcessingOutputInfo);
			if(lastNRRDHelper != null && !lastNRRDHelper.isSizeEqual(currentNRDHelper)){
				throw new DataAccessException("NRRD export error: all variables must have the same x,y,z size"+(lastNRRDHelper.isPostProcessInfo() != currentNRDHelper.isPostProcessInfo()?" (mixed postprocess)":""));
			}
			lastNRRDHelper = currentNRDHelper;
		}
		return lastNRRDHelper;
	}
	public static void appendDoubleData(NrrdInfo nrrdInfo,FileDataContainerManager fileDataContainerManager,double[] data,String varName) throws DataAccessException,IOException{
		int bufferSize = 8*nrrdInfo.getSizes()[0]*nrrdInfo.getSizes()[1]*nrrdInfo.getSizes()[2];
		if(data.length != (bufferSize/8)){//doubles are 8 bytes
			throw new DataAccessException("NRRD export Var: "+varName+" size != calculated size");
		}
		byte[] output = new byte[bufferSize];
		ByteBuffer byteBuffer = ByteBuffer.wrap(output);
		for (int k = 0; k < data.length; k++){
			byteBuffer.putDouble(data[k]);
		}
		fileDataContainerManager.append(nrrdInfo.getDataFileID(), output);

	}
}


private NRRDHelper createSliceNrrdHelper(CartesianMesh mesh,DataProcessingOutputInfo dataProcessingOutputInfo,VCDataIdentifier vcdID,
		VariableSpecs variableSpecs, TimeSpecs timeSpecs2, GeometrySpecs geometrySpecs, RasterSpecs rasterSpecs,
		FileDataContainerManager fileDataContainerManager) throws IOException,DataAccessException{
	ISize planeISize = Coordinate.convertAxisFromStandardXYZToNormal(mesh.getISize(), geometrySpecs.getAxis());
	planeISize = new ISize(planeISize.getX(),planeISize.getY(),1);
	Extent planeExtent = Coordinate.convertAxisFromStandardXYZToNormal(mesh.getExtent(), geometrySpecs.getAxis());
	planeExtent = new Extent(planeExtent.getX(),planeExtent.getY(),1);
	return NRRDHelper.getSizeCheckedNrrdHelper(variableSpecs,planeISize,planeExtent,dataProcessingOutputInfo);
}
private ImagePlaneManager createSlicer(CartesianMesh mesh,double[] data,GeometrySpecs originalGeometrySpecs){
	SourceDataInfo sdi = new SourceDataInfo(SourceDataInfo.RAW_VALUE_TYPE, data, mesh.getExtent(), mesh.getOrigin(),
			null, 0, mesh.getSizeX(), 1, mesh.getSizeY(), mesh.getSizeX(), mesh.getSizeZ(), mesh.getSizeX()*mesh.getSizeY());
	ImagePlaneManager imagePlaneManager = new ImagePlaneManager();
	imagePlaneManager.setSourceDataInfo(sdi);
	imagePlaneManager.setNormalAxis(originalGeometrySpecs.getAxis());
	imagePlaneManager.setSlice(originalGeometrySpecs.getSliceNumber());
	return imagePlaneManager;
}
private void appendSlice(String varName,double[] unslicedData,NrrdInfo sliceNrrdInfo,CartesianMesh mesh,GeometrySpecs geometrySpecs,
	FileDataContainerManager fileDataContainerManager) throws IOException,DataAccessException{
	//Setup manager to extract plane
	double[] sliceData = new double[sliceNrrdInfo.getAxisSize(NRRDAxisNames.X)*sliceNrrdInfo.getAxisSize(NRRDAxisNames.Y)];
	ImagePlaneManager imagePlaneManager = createSlicer(mesh, unslicedData, geometrySpecs);
	//copy plane to double[]
	int index = 0;
	for(int y=0;y<sliceNrrdInfo.getAxisSize(NRRDAxisNames.Y);y++){
		for(int x=0;x<sliceNrrdInfo.getAxisSize(NRRDAxisNames.X);x++){
			sliceData[index] = imagePlaneManager.getImagePlaneData().getDataAsTypeRaw(x, y, 0);
			index++;
		}
	}
	NRRDHelper.appendDoubleData(sliceNrrdInfo, fileDataContainerManager,sliceData,varName);
}
private NrrdInfo[] exportPDEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, VariableSpecs variableSpecs,
	TimeSpecs timeSpecs2, GeometrySpecs geometrySpecs, RasterSpecs rasterSpecs, FileDataContainerManager fileDataContainerManager) throws RemoteException, DataAccessException, IOException {
	
	CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);
	DataProcessingOutputInfo dataProcessingOutputInfo = null;
	//check if any of export variables are PostProcess and if so try to get PostProcessOutputInfo
	exportServiceImpl.fireExportProgress(jobID, vcdID, "Check PostProcess",0.0);
	DataIdentifier[] dataIdentifiers = dataServerImpl.getDataIdentifiers(outputContext, user, vcdID);
	for (int i = 0; i < dataIdentifiers.length; i++) {
		for(int j=0;j<variableSpecs.getVariableNames().length;j++){
			if(variableSpecs.getVariableNames()[j].equals(dataIdentifiers[i].getName()) && VariableType.POSTPROCESSING.equals(dataIdentifiers[i].getVariableType())){
				try{//we need PostProcessOutputInfo
					exportServiceImpl.fireExportProgress(jobID, vcdID, "Read PostProcess",0.0);
					dataProcessingOutputInfo = (DataProcessingOutputInfo)dataServerImpl.doDataOperation(user,new DataOperation.DataProcessingOutputInfoOP(vcdID,false,outputContext));
					break;
				}catch(Exception e){
					throw new DataAccessException("Export variable '"+variableSpecs.getVariableNames()[j] + "' is PostProcessing type.  Error reading PostProcessing data: "+e.getClass().getName()+" "+e.getMessage());
				}
			}
		}
	}

	long lastUpdateTime = 0;
	switch (rasterSpecs.getFormat()) {
		case NRRD_SINGLE: {
			switch (geometrySpecs.getModeID()) {
				case GEOMETRY_FULL:{
					NrrdInfo nrrdInfo =
						NRRDHelper.getSizeCheckedNrrdHelper(variableSpecs, mesh.getISize(),mesh.getExtent(), dataProcessingOutputInfo).
							createSingleFullNrrdInfo(fileDataContainerManager, vcdID, variableSpecs, rasterSpecs, timeSpecs2);
					int progressIndex = 1;
					int progressEnd = variableSpecs.getVariableNames().length*(timeSpecs2.getEndTimeIndex()-timeSpecs2.getBeginTimeIndex()+1);
					for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
						for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
							lastUpdateTime = fireThrottledProgress(exportServiceImpl, lastUpdateTime, "NRRD-snglfull",jobID, vcdID, progressIndex,progressEnd);
							progressIndex++;
							double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
							NRRDHelper.appendDoubleData(nrrdInfo, fileDataContainerManager, data, variableSpecs.getVariableNames()[i]);
						}
					}
					nrrdInfo = NrrdWriter.writeNRRD(nrrdInfo,fileDataContainerManager);
					return new NrrdInfo[] {nrrdInfo};
				}
				case GEOMETRY_SLICE: {
					NrrdInfo sliceNrrdInfo =
						createSliceNrrdHelper(mesh, dataProcessingOutputInfo, vcdID, variableSpecs,
							timeSpecs2, geometrySpecs, rasterSpecs, fileDataContainerManager).createSingleFullNrrdInfo(fileDataContainerManager, vcdID, variableSpecs, rasterSpecs, timeSpecs2);
					int progressIndex = 1;
					int progressEnd = variableSpecs.getVariableNames().length*(timeSpecs2.getEndTimeIndex()-timeSpecs2.getBeginTimeIndex()+1);
					for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
						for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
							lastUpdateTime = fireThrottledProgress(exportServiceImpl, lastUpdateTime, "NRRD-snglslice",jobID, vcdID, progressIndex,progressEnd);
							progressIndex++;
							double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
							appendSlice(variableSpecs.getVariableNames()[i], data, sliceNrrdInfo, mesh, geometrySpecs, fileDataContainerManager);
						}
					}
					sliceNrrdInfo = NrrdWriter.writeNRRD(sliceNrrdInfo,fileDataContainerManager);
					return new NrrdInfo[] {sliceNrrdInfo};
				}
				default: {
					throw new DataAccessException("NRRD export from slice not yet supported");
				}
			}			
		}
		case NRRD_BY_TIME: {
			switch (geometrySpecs.getModeID()) {
				case GEOMETRY_FULL: {
					NRRDHelper nrrdHelper = NRRDHelper.getSizeCheckedNrrdHelper(variableSpecs, mesh.getISize(),mesh.getExtent(), dataProcessingOutputInfo);
					Vector<NrrdInfo> nrrdinfoV = new Vector<NrrdInfo>();
					int progressIndex = 1;
					int progressEnd = (timeSpecs2.getEndTimeIndex()-timeSpecs2.getBeginTimeIndex()+1);
					for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
						lastUpdateTime = fireThrottledProgress(exportServiceImpl, lastUpdateTime, "NRRD-timefull",jobID, vcdID, progressIndex,progressEnd);
						progressIndex++;
						NrrdInfo nrrdInfo = nrrdHelper.createTimeFullNrrdInfo(fileDataContainerManager, vcdID, variableSpecs, timeSpecs2.getAllTimes()[j], rasterSpecs);
						nrrdinfoV.add(nrrdInfo);
						for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
							double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
							NRRDHelper.appendDoubleData(nrrdInfo, fileDataContainerManager, data, variableSpecs.getVariableNames()[i]);
						}
						NrrdWriter.writeNRRD(nrrdInfo,fileDataContainerManager);
					}
					if(nrrdinfoV.size() > 0){
						NrrdInfo[] nrrdinfoArr = new NrrdInfo[nrrdinfoV.size()];
						nrrdinfoV.copyInto(nrrdinfoArr);
						return nrrdinfoArr;
					}
					return null;
				}
				case GEOMETRY_SLICE: {
					Vector<NrrdInfo> nrrdinfoV = new Vector<NrrdInfo>();
					int progressIndex = 1;
					int progressEnd = (timeSpecs2.getEndTimeIndex()-timeSpecs2.getBeginTimeIndex()+1);
					for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
						lastUpdateTime = fireThrottledProgress(exportServiceImpl, lastUpdateTime, "NRRD-timeslice",jobID, vcdID, progressIndex,progressEnd);
						progressIndex++;
						NrrdInfo sliceNrrdInfo =
							createSliceNrrdHelper(mesh, dataProcessingOutputInfo, vcdID, variableSpecs, timeSpecs2, geometrySpecs, rasterSpecs, fileDataContainerManager).
								createTimeFullNrrdInfo(fileDataContainerManager, vcdID, variableSpecs, timeSpecs2.getAllTimes()[j], rasterSpecs);
						nrrdinfoV.add(sliceNrrdInfo);
						for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
							double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
							appendSlice(variableSpecs.getVariableNames()[i], data, sliceNrrdInfo, mesh, geometrySpecs, fileDataContainerManager);
						}
						NrrdWriter.writeNRRD(sliceNrrdInfo,fileDataContainerManager);
					}
					if(nrrdinfoV.size() > 0){
						NrrdInfo[] nrrdinfoArr = new NrrdInfo[nrrdinfoV.size()];
						nrrdinfoV.copyInto(nrrdinfoArr);
						return nrrdinfoArr;
					}
					return null;
				}
				default: {
					throw new DataAccessException("NRRD export from slice not yet supported");
				}
			}			
		}
		case NRRD_BY_VARIABLE : {
			switch (geometrySpecs.getModeID()) {
				case GEOMETRY_FULL: {
					Vector<NrrdInfo> nrrdinfoV = new Vector<NrrdInfo>();
					int progressIndex = 1;
					int progressEnd = variableSpecs.getVariableNames().length*(timeSpecs2.getEndTimeIndex()-timeSpecs2.getBeginTimeIndex()+1);
					for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
						NRRDHelper nrrdhelHelper = new NRRDHelper(variableSpecs.getVariableNames()[i], mesh.getISize(),mesh.getExtent(), dataProcessingOutputInfo);
						NrrdInfo nrrdInfo = nrrdhelHelper.createVariableFullNrrdInfo(fileDataContainerManager, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2, rasterSpecs);
						nrrdinfoV.add(nrrdInfo);		
						for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
							lastUpdateTime = fireThrottledProgress(exportServiceImpl, lastUpdateTime, "NRRD-varsfull",jobID, vcdID, progressIndex,progressEnd);
							progressIndex++;
							double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
							NRRDHelper.appendDoubleData(nrrdInfo, fileDataContainerManager, data, variableSpecs.getVariableNames()[i]);
						}
						nrrdInfo = NrrdWriter.writeNRRD(nrrdInfo,fileDataContainerManager);
					}
					if(nrrdinfoV.size() > 0){
						NrrdInfo[] nrrdinfoArr = new NrrdInfo[nrrdinfoV.size()];
						nrrdinfoV.copyInto(nrrdinfoArr);
						return nrrdinfoArr;
					}
					return null;
				}
				case GEOMETRY_SLICE: {
					Vector<NrrdInfo> nrrdinfoV = new Vector<NrrdInfo>();
					int progressIndex = 1;
					int progressEnd = variableSpecs.getVariableNames().length*(timeSpecs2.getEndTimeIndex()-timeSpecs2.getBeginTimeIndex()+1);
					for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
						NrrdInfo sliceNrrdInfo =
								createSliceNrrdHelper(mesh, dataProcessingOutputInfo, vcdID, variableSpecs, timeSpecs2, geometrySpecs, rasterSpecs, fileDataContainerManager).
									createVariableFullNrrdInfo(fileDataContainerManager, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2, rasterSpecs);
						nrrdinfoV.add(sliceNrrdInfo);		
						for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
							lastUpdateTime = fireThrottledProgress(exportServiceImpl, lastUpdateTime, "NRRD-varsfull",jobID, vcdID, progressIndex,progressEnd);
							progressIndex++;
							double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
							appendSlice(variableSpecs.getVariableNames()[i], data, sliceNrrdInfo, mesh, geometrySpecs, fileDataContainerManager);
						}
						NrrdWriter.writeNRRD(sliceNrrdInfo,fileDataContainerManager);
					}
					if(nrrdinfoV.size() > 0){
						NrrdInfo[] nrrdinfoArr = new NrrdInfo[nrrdinfoV.size()];
						nrrdinfoV.copyInto(nrrdinfoArr);
						return nrrdinfoArr;
					}
					return null;
				}
				default: {
					throw new DataAccessException("NRRD export from slice not yet supported");
				}
			}			
		}
		default: {
			throw new DataAccessException("Multiple NRRD file export not yet supported");
		}
	}											
}

	private static long fireThrottledProgress(ExportServiceImpl exportServiceImpl,long lastUpdateTime,String message,long jobID,VCDataIdentifier vcdID,int progressIndex,int endIndex){
		if(lastUpdateTime == 0 || (System.currentTimeMillis() - lastUpdateTime)>5000){
			lastUpdateTime = System.currentTimeMillis();
			exportServiceImpl.fireExportProgress(jobID, vcdID, message,(double)(progressIndex)/(double)(endIndex+1));
		}
		return lastUpdateTime;
	}

/**
 * This method was created in VisualAge.
 */
public NrrdInfo[] makeRasterData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager) 
						throws RemoteException, DataAccessException, IOException {
	return exportPDEData(
			outputContext,
		jobRequest.getJobID(),
		user,
		dataServerImpl,
		exportSpecs.getVCDataIdentifier(),
		exportSpecs.getVariableSpecs(),
		exportSpecs.getTimeSpecs(),
		exportSpecs.getGeometrySpecs(),
		(RasterSpecs)exportSpecs.getFormatSpecificSpecs(),
		fileDataContainerManager
	);
}

public ExportOutput[] makePLYWithTexData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl,
		ExportSpecs exportSpecs,FileDataContainerManager fileDataContainerManager) throws Exception{

	String simID = exportSpecs.getVCDataIdentifier().getID();
	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user,vcdID );
	TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
	VariableSpecs variableSpecs = exportSpecs.getVariableSpecs();
	Vector<ExportOutput> exportOutV = new Vector<ExportOutput>();
	RegionImage regionImage = MeshDisplayAdapter.generateRegionImage(mesh, null);
	PLYSpecs plySpecs = (PLYSpecs)exportSpecs.getFormatSpecificSpecs();
	DisplayPreferences[] displayPreferences = plySpecs.getDisplayPreferences();
	
//	BitSet bInDomain = null;
//	String variableName = variableSpecs.getVariableNames()[0];
//	Domain varDomain = null;
//	DataIdentifier[] dataIdentifiers = dataServerImpl.getDataIdentifiers(outputContext, user, vcdID);
//	for (int i = 0; i < dataIdentifiers.length; i++) {
//		//dataIdentifier.getDomain();
//		if(dataIdentifiers[i].getName().equals(variableName)){
//			varDomain = dataIdentifiers[i].getDomain();
//			break;
//		}
//	}
	
//	MembraneElement membraneElement = mesh.getMembraneElements()[faceIndex];
//	String memSubdomainName = (varDomain==null?null:mesh.getMembraneSubdomainNamefromMemIndex(faceIndex));
//	boolean bInDomain = (varDomain==null?true:varDomain.getName().equals(memSubdomainName));
	
	DisplayAdapterService das = new DisplayAdapterService();
	
	StringWriter stringWriter = new StringWriter();
	PolyTexHelper imgResults = writeStanfordPolygonTex(regionImage, stringWriter/*,bInDomain*/,mesh);
	//mesh
	ExportOutput exportOut = new ExportOutput(true,".ply",simID.toString(),"_memb",fileDataContainerManager);
	fileDataContainerManager.append(exportOut.getFileDataContainerID(), stringWriter.toString());
	exportOutV.add(exportOut);
	//special neighbor annotation image
	ExportOutput textImagetOut0 = new ExportOutput(true,".png",simID.toString(),"_membAnnot",fileDataContainerManager);
	fileDataContainerManager.append(textImagetOut0.getFileDataContainerID(), imgResults.specialNeighborImage);
	exportOutV.add(textImagetOut0);
	for (int varNameIndex = 0; varNameIndex < variableSpecs.getVariableNames().length; varNameIndex++) {
		if(das.fetchColorModel(displayPreferences[varNameIndex].getColorMode()) == null){
			if(displayPreferences[varNameIndex].getColorMode().equals(DisplayAdapterService.GRAY)){
				das.addColorModelForValues(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.createBlueRedSpecialColors(),displayPreferences[varNameIndex].getColorMode());
			}else{
				das.addColorModelForValues(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.createGraySpecialColors(),displayPreferences[varNameIndex].getColorMode());
//				das.setActiveColorModelID("Contrast");
			}
		}

//		BitSet domainValid = (displayPreferences[varNameIndex]==null?null:(displayPreferences[varNameIndex].getDomainValid()==null?null:displayPreferences[varNameIndex].getDomainValid()));
		ExportSpecs.setupDisplayAdapterService(displayPreferences[varNameIndex],das,displayPreferences[varNameIndex].getScaleSettings());
		for (int j = timeSpecs.getBeginTimeIndex(); j <= timeSpecs.getEndTimeIndex(); j++){
			BufferedImage image = createTextureImage(imgResults.imageSideSize);
			int[] imgBuffer = ((DataBufferInt)(image.getRaster().getDataBuffer())).getData();
			Arrays.fill(imgBuffer, Integer.MAX_VALUE);
			SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[varNameIndex], timeSpecs.getAllTimes()[j]);
//			Range valueDomain = null;
//			if(domainValid != null){
//				valueDomain = BeanUtils.calculateValueDomain(simDataBlock.getData(), domainValid);
//			}
			
			// min-max for color range
//			double min = simDataBlock.getData()[0];
//			double max = min;
//			for (int i = 0; i < simDataBlock.getData().length; i++) {
//				String memSubdomainName = (varDomain==null?null:mesh.getMembraneSubdomainNamefromMemIndex(i));
//				if (varDomain== null || varDomain.getName().equals(memSubdomainName)){
//					min = Math.min(min, simDataBlock.getData()[i]);
//					max = Math.max(max, simDataBlock.getData()[i]);
//				}
//			}
//			min  -9e-4;
//			max  5e-5;
			
//			das.setActiveScaleRange(new org.vcell.util.Range(min, max));
//			das.setValueDomain(new org.vcell.util.Range(min,max));

			for (int k = 0; k < imgResults.dataIndexes.length; k++) {
				if(imgBuffer[k] != Integer.MAX_VALUE){
					System.out.println("texture pixel used twice");
				}
				double avgVal = 0;
				for (int l = 0; l < imgResults.dataIndexes[k].length; l++) {
					avgVal+= simDataBlock.getData()[imgResults.dataIndexes[k][l]];
				}
				imgBuffer[k] = das.getColorFromValue(avgVal/imgResults.dataIndexes[k].length);
			}
			byte[] pngImage = flipPNG(image);
			ExportOutput textImagetOut = new ExportOutput(true,".png",simID.toString(),"_memb_"+variableSpecs.getVariableNames()[varNameIndex]+"_"+j,fileDataContainerManager);
			fileDataContainerManager.append(textImagetOut.getFileDataContainerID(), pngImage);
			exportOutV.add(textImagetOut);
		}
	}
	ExportOutput[] exportOutputArr = exportOutV.toArray(new ExportOutput[0]);
	return exportOutputArr;
}

private static BufferedImage createTextureImage(int imageSideLength){
	int[] texImage = new int[imageSideLength*imageSideLength];
	int[] bitMasks = new int[]{0xFF0000, 0xFF00, 0xFF, 0xFF000000};
	SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(
	        DataBuffer.TYPE_INT, imageSideLength, imageSideLength, bitMasks);
	DataBufferInt db = new DataBufferInt(texImage, texImage.length);
	WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());
	BufferedImage image = new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
	return image;
}
private static byte[] flipPNG(BufferedImage image) throws IOException{
	//flip image vertical
	AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
	tx.translate(0, -image.getHeight(null));
	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	//write out
	image = op.filter(image, null);
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ImageIO.write(image, "png", baos);
	return baos.toByteArray();
}
public static class PolyTexHelper {
	public int[][] dataIndexes;
	public byte[] specialNeighborImage;
	public int imageSideSize;
	public PolyTexHelper(int[][] dataIndexes, byte[] specialNeighborImage,int imageSideSize) {
		super();
		this.dataIndexes = dataIndexes;
		this.specialNeighborImage = specialNeighborImage;
		this.imageSideSize = imageSideSize;
	}
}
public static PolyTexHelper writeStanfordPolygonTex(RegionImage regionImage,Writer writer,/*BitSet bInDomain,*/CartesianMesh mesh) throws IOException,MathException{
	int totalPolygons = regionImage.getSurfacecollection().getTotalPolygonCount();
	int totalVerts = totalPolygons*4;
	final int ALL_NEIGHBOR_QUAD_BOX_SIDE = 3;
	int imgSideSize = (int) Math.ceil(Math.sqrt((totalPolygons)));//2D image texture
	int texelXSize = (imgSideSize*ALL_NEIGHBOR_QUAD_BOX_SIDE);
	int[][] texIndexToFaceIndexPtrs = new int[texelXSize*texelXSize][0];
//	Arrays.fill(texImage0, Integer.MAX_VALUE);
	
	final int squareSize = 40;
	int fontXSize = texelXSize*squareSize;
	int fontYSize = texelXSize*squareSize;
	BufferedImage image0 = createTextureImage(fontXSize);
//	int[] texFontImage = new int[fontXSize*fontYSize];
//	Arrays.fill(texFontImage, 0xFF000000);
//	int[] bitMasks0 = new int[]{0xFF0000, 0xFF00, 0xFF, 0xFF000000};
//	SinglePixelPackedSampleModel sm0 = new SinglePixelPackedSampleModel(
//	        DataBuffer.TYPE_INT, texelXSize*squareSize, texelXSize*squareSize, bitMasks0);
//	DataBufferInt db0 = new DataBufferInt(texFontImage, texFontImage.length);
//	WritableRaster wr0 = Raster.createWritableRaster(sm0, db0, new Point());
//	BufferedImage image0 = new BufferedImage(ColorModel.getRGBdefault(), wr0, false, null);
	Graphics2D g2d = image0.createGraphics();
	g2d.setColor(Color.white);
	Font bigF = Font.decode("Dialog-plain-10");
	g2d.setFont(bigF);

	float texelSize = 1.0f/texelXSize;
	writer.write("ply\n");
	writer.write("format ascii 1.0\n");
//	writer.write("comment "+geometrySurfaceDescription.getGeometry().getName()+"\n");
	writer.write("comment textureImage size is "+imgSideSize+"x"+imgSideSize+"\n");
	writer.write("element vertex "+totalVerts+"\n");
	writer.write("property float x\n");
	writer.write("property float y\n");
	writer.write("property float z\n");
	writer.write("property float s\n");//must be 's' for blender (u texture coord)
	writer.write("property float t\n");//must be 't' for blender (v texture coord)
	writer.write("element face "+totalPolygons+"\n");
	writer.write("property list uchar int vertex_index\n");
	writer.write("end_header\n");
	StringBuffer sbVert = new StringBuffer();
	StringBuffer sbFace = new StringBuffer();
	int faceIndex = 0;
	int vertIndex = 0;
	for (int currSurf = 0; currSurf < regionImage.getSurfacecollection().getSurfaceCount(); currSurf++) {
		for (int currFace = 0; currFace < regionImage.getSurfacecollection().getSurfaces(currSurf).getPolygonCount(); currFace++) {
//			if(bInDomain != null && !bInDomain.get(faceIndex)){
//				continue;
//			}
			g2d.setColor(Color.white);
			Quadrilateral quad = (Quadrilateral)regionImage.getSurfacecollection().getSurfaces(currSurf).getPolygons(currFace);
			NAHelper naHelper0 = calcNAHelper(quad);
			Point2D p2dm = Coordinate.get2DProjection(naHelper0.middle, naHelper0.normalAxis);
			
			int x = faceIndex%imgSideSize;
			int y = faceIndex/imgSideSize;
			int boxX = (x*ALL_NEIGHBOR_QUAD_BOX_SIDE)+1;
			int boxY = (y*ALL_NEIGHBOR_QUAD_BOX_SIDE)+1;
			int fontX = boxX*squareSize;
			int fontY = boxY*squareSize;

			texIndexToFaceIndexPtrs[boxY*texelXSize+boxX] = new int[] {faceIndex};//das.getColorFromValue(data[faceIndex]);//face color
			g2d.drawString(currSurf+"-"+currFace, fontX, fontY);				
			
			float imgXTexelCenter = (boxX+.5f)*texelSize;
			float imgYTexelCenter = (boxY+.5f)*texelSize;
			ArrayList<RegionImage.MembraneEdgeNeighbor> neighbors = regionImage.getMembraneEdgeNeighbors()[currSurf][currFace];
			sbFace.append(quad.getNodeCount()+"");
			for (int vrt = 0; vrt < quad.getNodeCount(); vrt++) {
				g2d.setColor(Color.white);
				RegionImage.MembraneEdgeNeighbor membraneEdgeNeighbor = neighbors.get(vrt);
				MembraneElementIdentifier neighb = membraneEdgeNeighbor.getMembraneElementIdentifier();
				if(neighb != null){
					Quadrilateral quadNeighbor = (Quadrilateral)regionImage.getSurfacecollection().getSurfaces(neighb.surfaceIndex).getPolygons(neighb.nonMasterPolygonIndex);
					NAHelper naHelper1 = calcNAHelper(quadNeighbor);
					Point2D p2dmNeighb = Coordinate.get2DProjection(naHelper1.middle, naHelper0.normalAxis);
					double xdiff = p2dmNeighb.getX()-p2dm.getX();
					double ydiff = p2dmNeighb.getY()-p2dm.getY();
					int dxn = (int)Math.signum((Math.abs(xdiff)<1e-9?0:xdiff));
					int dyn = (int)Math.signum((Math.abs(ydiff)<1e-9?0:ydiff));
					texIndexToFaceIndexPtrs[(boxY+dyn)*texelXSize+(boxX+dxn)] = new int[] {membraneEdgeNeighbor.getMasterPolygonIndex()};//das.getColorFromValue(data[membraneEdgeNeighbor.getMasterPolygonIndex()]);
					g2d.drawString(neighb.nonMasterPolygonIndex+"", fontX+(dxn*squareSize), fontY+(dyn*squareSize));
					
					RegionImage.MembraneEdgeNeighbor membraneEdgeNeighbor2 = neighbors.get((vrt+1==quad.getNodeCount()?0:vrt+1));
					MembraneElementIdentifier neighb2 = membraneEdgeNeighbor2.getMembraneElementIdentifier();
					if(neighb2 != null){
						CommonNeighb commonNeighb = calculateCommonNeighbor(mesh,regionImage,mesh.getMembraneElements()[membraneEdgeNeighbor.getMasterPolygonIndex()], mesh.getMembraneElements()[membraneEdgeNeighbor2.getMasterPolygonIndex()], faceIndex);
						if(commonNeighb != null){
							if(commonNeighb.neighborneighbos.size() == 0){
								commonNeighb.neighborneighbos.add(membraneEdgeNeighbor.getMasterPolygonIndex());
								commonNeighb.neighborneighbos.add(membraneEdgeNeighbor2.getMasterPolygonIndex());
							}
							Coordinate commonCoord = new Coordinate(commonNeighb.node.getX(),commonNeighb.node.getY(),commonNeighb.node.getZ());//mesh.getMembraneElements()[commonNeighb].getCentroid();
							Point2D p2dmcommon = Coordinate.get2DProjection(commonCoord, naHelper0.normalAxis);
							double xdiff1 = p2dmcommon.getX()-p2dm.getX();
							double ydiff1 = p2dmcommon.getY()-p2dm.getY();
							int dxc = (int)Math.signum((Math.abs(xdiff1)<1e-9?0:xdiff1));
							int dyc = (int)Math.signum((Math.abs(ydiff1)<1e-9?0:ydiff1));
							g2d.setColor(Color.green);
							int xloc = fontX+(dxc*squareSize);
							int yloc = fontY+(dyc*squareSize);
							double avgVal = 0;
							int texIndex = (boxY+dyc)*texelXSize+boxX+dxc;
							texIndexToFaceIndexPtrs[texIndex] = new int[commonNeighb.neighborneighbos.size()];
							for (int k = 0; k < commonNeighb.neighborneighbos.size(); k++) {
								g2d.drawString(regionImage.getQuadIndexToSurfAndFace().get(commonNeighb.neighborneighbos.get(k)).getFace()+"", xloc, yloc+(k*11));
//								avgVal+= data[commonNeighb.neighborneighbos.get(k)];
								texIndexToFaceIndexPtrs[texIndex][k] = commonNeighb.neighborneighbos.get(k);//das.getColorFromValue(avgVal/commonNeighb.neighborneighbos.size());
							}
//							if(texImage0[texIndex] != Integer.MAX_VALUE){
//								System.out.println("texture pixel used twice");
//							}
						}
					}
				}else{
					System.out.println("neighbor null");
				}

				Point2D p2dv = Coordinate.get2DProjection(new Coordinate(quad.getNodes(vrt).getX(),quad.getNodes(vrt).getY(),quad.getNodes(vrt).getZ()), naHelper0.normalAxis);
				double xdiff3 = p2dv.getX()-p2dm.getX();
				double ydiff3 = p2dv.getY()-p2dm.getY();
				int dx = (int)Math.signum((Math.abs(xdiff3)<1e-9?0:xdiff3));
				int dy = (int)Math.signum((Math.abs(ydiff3)<1e-9?0:ydiff3));
				float u = (float)imgXTexelCenter+((float)dx*texelSize/2.0f);
				float v = (float)imgYTexelCenter+((float)dy*texelSize/2.0f);
				Node vertex = quad.getNodes()[vrt];
				sbVert.append((float)vertex.getX()+" "+(float)vertex.getY()+" "+(float)vertex.getZ()+" "+u+" "+v+"\n");
				sbFace.append(" ");
				sbFace.append(vertIndex+"");
				vertIndex++;
			}
			sbFace.append("\n");
			faceIndex++;
		}
	}
	if(faceIndex != totalPolygons || vertIndex != totalVerts){
		throw new IOException("Final faces and verts count don't match");
	}
	writer.write(sbVert.toString());
	writer.write(sbFace.toString());

	//flip image vertical
//	AffineTransform tx0 = AffineTransform.getScaleInstance(1, -1);
//	tx0.translate(0, -image0.getHeight(null));
//	AffineTransformOp op0 = new AffineTransformOp(tx0, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//	ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	baos = new ByteArrayOutputStream();
//	ImageIO.write(op0.filter(image0, null), "png", baos);
	return new PolyTexHelper(texIndexToFaceIndexPtrs,flipPNG(image0),texelXSize);
}
private static class NAHelper{
	public Coordinate middle;
	public int normalAxis;
	public NAHelper(Coordinate middle, int normalAxis) {
		super();
		this.middle = middle;
		this.normalAxis = normalAxis;
	}
}
private static NAHelper calcNAHelper(Quadrilateral quad){
	double cx=0,cy=0,cz = 0;
	boolean nx=true, ny = true,nz=true;
	double ulp = Math.ulp(quad.getNodes(0).getX())*10;
	for (int vrt = 0; vrt < quad.getNodeCount(); vrt++) {
		cx+= quad.getNodes(vrt).getX();
		cy+= quad.getNodes(vrt).getY();
		cz+= quad.getNodes(vrt).getZ();
		double xd = Math.abs(quad.getNodes(0).getX()-quad.getNodes(vrt).getX());
		double yd = Math.abs(quad.getNodes(0).getY()-quad.getNodes(vrt).getY());
		double zd = Math.abs(quad.getNodes(0).getZ()-quad.getNodes(vrt).getZ());
		nx = nx && (xd<ulp);
		ny = ny && (yd<ulp);
		nz = nz && (zd<ulp);
	}
	Coordinate cm = new Coordinate(cx/quad.getNodeCount(), cy/quad.getNodeCount(), cz/quad.getNodeCount());
	int normalAxis = (nx?Coordinate.X_AXIS:(ny?Coordinate.Y_AXIS:Coordinate.Z_AXIS));
	return new NAHelper(cm, normalAxis);
}

private static class CommonNeighb {
	public Node node;
	public ArrayList<Integer> neighborneighbos;
	public CommonNeighb(Node node, ArrayList<Integer> neighborneighbos) {
		super();
		this.node = node;
		this.neighborneighbos = neighborneighbos;
	}
	
}
private static CommonNeighb calculateCommonNeighbor(CartesianMesh mesh,RegionImage regionImage,MembraneElement membraneElementN0,MembraneElement membraneElementN1,int parentMembrIndex){
	//find common node
	Quadrilateral quadp = (Quadrilateral)regionImage.getSurfacecollection().getSurfaces(regionImage.getQuadIndexToSurfAndFace().get(parentMembrIndex).getSurf()).getPolygons(regionImage.getQuadIndexToSurfAndFace().get(parentMembrIndex).getFace());
	Quadrilateral quad0 = (Quadrilateral)regionImage.getSurfacecollection().getSurfaces(regionImage.getQuadIndexToSurfAndFace().get(membraneElementN0.getMembraneIndex()).getSurf()).getPolygons(regionImage.getQuadIndexToSurfAndFace().get(membraneElementN0.getMembraneIndex()).getFace());
	Quadrilateral quad1 = (Quadrilateral)regionImage.getSurfacecollection().getSurfaces(regionImage.getQuadIndexToSurfAndFace().get(membraneElementN1.getMembraneIndex()).getSurf()).getPolygons(regionImage.getQuadIndexToSurfAndFace().get(membraneElementN1.getMembraneIndex()).getFace());
	List<Node> nodesp = Arrays.asList(quadp.getNodes());
	List<Node> nodes0 = Arrays.asList(quad0.getNodes());
	List<Node> nodes1 = Arrays.asList(quad1.getNodes());
	List<Node> intersectN = new ArrayList<>(nodes0);
	intersectN.retainAll(nodes1);
	intersectN.retainAll(nodesp);
	if(intersectN.size() != 1){
		System.out.println("Couldn't find parent node");
		return null;
	}
	ArrayList<Integer> commonNeighbors = new ArrayList<>();
	findNeighborNeighborsWithNode(regionImage, membraneElementN0, intersectN.get(0), parentMembrIndex, commonNeighbors);
	findNeighborNeighborsWithNode(regionImage, membraneElementN1, intersectN.get(0), parentMembrIndex, commonNeighbors);

	return new CommonNeighb(intersectN.get(0), commonNeighbors);
}

public static void findNeighborNeighborsWithNode(RegionImage regionImage,MembraneElement membraneElement,Node theNode,int excludeParent,ArrayList<Integer> container){
	int[] neighborNeighbors = membraneElement.getMembraneNeighborIndexes();
	for (int i = 0; i < neighborNeighbors.length; i++) {
		Quadrilateral quad =
			(Quadrilateral)regionImage.getSurfacecollection().getSurfaces(regionImage.getQuadIndexToSurfAndFace().get(neighborNeighbors[i]).getSurf()).getPolygons(regionImage.getQuadIndexToSurfAndFace().get(neighborNeighbors[i]).getFace());
		for (int j = 0; j < quad.getNodeCount(); j++) {
			if(quad.getNodes(j) == theNode && neighborNeighbors[i] != excludeParent){
				container.add(neighborNeighbors[i]);
			}
		}
	}
}

public ExportOutput[] makeUCDData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs,FileDataContainerManager fileDataContainerManager)
						throws Exception{
	
	String simID = exportSpecs.getVCDataIdentifier().getID();
	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	VariableSpecs variableSpecs = exportSpecs.getVariableSpecs();
	TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user,vcdID );
	CartesianMesh.UCDInfo ucdInfo = mesh.getUCDInfo();
	CartesianMesh.UCDInfo ucdInfoReduced = ucdInfo.removeNonMembraneGridNodes();
	
	Vector<ExportOutput> exportOutV = new Vector<ExportOutput>();
//	MeshDisplayAdapter meshDisplayAdapter = new MeshDisplayAdapter(mesh);
//	SurfaceCollection surfaceCollection = meshDisplayAdapter.generateMeshRegionSurfaces().getSurfaceCollection();
//	for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
		for (int j = timeSpecs.getBeginTimeIndex(); j <= timeSpecs.getEndTimeIndex(); j++){
			exportServiceImpl.fireExportProgress(
					jobRequest.getJobID(), vcdID, "UCD",
					(double)(j-timeSpecs.getBeginTimeIndex())/(double)(timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1));
			
//			String fileID = simID + "_Full_" + formatTime(timeSpecs.getAllTimes()[j]) + "time_" + variableSpecs.getVariableNames().length + "vars";

//			File datafile = new File(tempDir, fileID + "_data.ucd");
//			FileWriter fileWriter = new FileWriter(datafile);
			
			Vector<double[]> volumeDataV = new Vector<double[]>();
			Vector<String> volumeDataNameV = new Vector<String>();
			Vector<String> volumeDataUnitV = new Vector<String>();
			Vector<double[]> membraneDataV = new Vector<double[]>();
			Vector<String> membraneDataNameV = new Vector<String>();
			Vector<String> membraneDataUnitV = new Vector<String>();
			for (int k = 0; k < variableSpecs.getVariableNames().length; k++) {
				SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[k], timeSpecs.getAllTimes()[j]);
				if(simDataBlock.getVariableType().equals(VariableType.VOLUME)){
					volumeDataNameV.add(variableSpecs.getVariableNames()[k]);
					volumeDataUnitV.add("unknown");
					volumeDataV.add(simDataBlock.getData());
				}else{
					membraneDataNameV.add(variableSpecs.getVariableNames()[k]);
					membraneDataUnitV.add("unknown");
					membraneDataV.add(simDataBlock.getData());
				}
				
			}

			if(volumeDataV.size() > 0){
				StringWriter stringWriter = new StringWriter();
				AVS_UCD_Exporter.writeUCDVolGeomAndData(
						ucdInfo,
					volumeDataNameV.toArray(new String[0]),
					volumeDataUnitV.toArray(new String[0]),
					volumeDataV.toArray(new double[0][]),
					stringWriter);
				ExportOutput exportOut = new ExportOutput(true,".ucd",simID.toString(),"_vol_"+j,fileDataContainerManager);
				fileDataContainerManager.append(exportOut.getFileDataContainerID(), stringWriter.toString());
				exportOutV.add(exportOut);				
			}
			if(membraneDataV.size() > 0){
				StringWriter stringWriter = new StringWriter();
				AVS_UCD_Exporter.writeUCDMembGeomAndData(
						ucdInfoReduced/*ucdInfo*/,
					membraneDataNameV.toArray(new String[0]),
					membraneDataUnitV.toArray(new String[0]),
					membraneDataV.toArray(new double[0][]),
					stringWriter);
				ExportOutput exportOut = new ExportOutput(true,".ucd",simID.toString(),"_memb_"+j,fileDataContainerManager);
				fileDataContainerManager.append(exportOut.getFileDataContainerID(), stringWriter.toString());
				exportOutV.add(exportOut);				
			}

			
//			AVS_UCD_Exporter.writeUCD(mesh,
//					(volumeDataNameV.size() == 0?null:volumeDataNameV.toArray(new String[0])),
//					(volumeDataUnitV.size() == 0?null:volumeDataUnitV.toArray(new String[0])),
//					(volumeDataV.size() == 0?null:volumeDataV.toArray(new double[0][])),
//					
//					(membraneDataNameV.size() == 0?null:membraneDataNameV.toArray(new String[0])),
//					(membraneDataUnitV.size() == 0?null:membraneDataUnitV.toArray(new String[0])),
//					(membraneDataV.size() == 0?null:membraneDataV.toArray(new double[0][])),
//					stringWriter);
//			ExportOutput exportOut = new ExportOutput(true,".ucd",simID.toString(),fileID,stringWriter.toString().getBytes());
//			exportOutV.add(exportOut);
		}
//	}

	ExportOutput[] exportOutputArr = exportOutV.toArray(new ExportOutput[0]);
	return exportOutputArr;
	
	
//	String fileID = simID + "_Full_" + NUM_TIMES + "times_" + variableSpecs.getVariableNames().length + "vars";
//	File datafile = new File(tempDir, fileID + "_data.nrrd");
//	nrrdInfo.setDatafile(datafile.getName());
//	DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(datafile)));
//	try {
//		for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
//			for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
//				double[] data = dataServerImpl.getSimDataBlock(user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
//				for (int k = 0; k < data.length; k++){
//					out.writeDouble(data[k]);
//				}
//			}
//		}
//	} catch (IOException exc) {
//		throw new DataAccessException(exc.toString());
//	} finally {
//		out.close();
//	}

}

public ExportOutput[] makeVTKImageData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl,
		ExportSpecs exportSpecs,FileDataContainerManager fileDataContainerManager) throws Exception{
	
	String simID = exportSpecs.getVCDataIdentifier().getID();
	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	VariableSpecs variableSpecs = exportSpecs.getVariableSpecs();
	TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user,vcdID );
	
	Vector<ExportOutput> exportOutV = new Vector<ExportOutput>();
	for (int j = timeSpecs.getBeginTimeIndex(); j <= timeSpecs.getEndTimeIndex(); j++){
		exportServiceImpl.fireExportProgress(
				jobRequest.getJobID(), vcdID, "VTKIMG",
				(double)(j-timeSpecs.getBeginTimeIndex())/(double)(timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1));

		StringBuffer sb = new StringBuffer();
		sb.append("# vtk DataFile Version 2.0"+"\n");
		sb.append("Simulation "+vcdID.toString()+"\n");
		sb.append("ASCII"+"\n");
		sb.append("DATASET STRUCTURED_POINTS"+"\n");
		sb.append("DIMENSIONS "+
			mesh.getSizeX()+" "+mesh.getSizeY()+" "+mesh.getSizeZ()+"\n");
		sb.append("SPACING "+
			mesh.getExtent().getX()+" "+mesh.getExtent().getY()+" "+mesh.getExtent().getZ()+"\n");
		sb.append("ORIGIN "+
				mesh.getOrigin().getX()+" "+mesh.getOrigin().getY()+" "+mesh.getOrigin().getZ()+"\n");
		sb.append("POINT_DATA "+mesh.getNumVolumeElements()+"\n");
		
		//write volume region ids
		sb.append("SCALARS "+"regionID"+" double 1"+"\n");
		sb.append("LOOKUP_TABLE default"+"\n");
		int yzSize = mesh.getSizeY()*mesh.getSizeZ();
		int index = 0;
		for (int yz = 0; yz < yzSize; yz++) {
			for (int x = 0; x < mesh.getSizeX(); x++) {
				sb.append((x!=0?" ":"")+mesh.getVolumeRegionIndex(index));
				index++;
			}
			sb.append("\n");
		}
		sb.append("\n");
		
		for (int k = 0; k < variableSpecs.getVariableNames().length; k++) {
			SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[k], timeSpecs.getAllTimes()[j]);
			if(simDataBlock.getVariableType().equals(VariableType.VOLUME)){
				sb.append("SCALARS "+variableSpecs.getVariableNames()[k]+" double 1"+"\n");
				sb.append("LOOKUP_TABLE default"+"\n");
				double[] volumeData = simDataBlock.getData();
				index = 0;
				for (int yz = 0; yz < yzSize; yz++) {
					for (int x = 0; x < mesh.getSizeX(); x++) {
						sb.append((x!=0?" ":"")+volumeData[index]);
						index++;
					}
					sb.append("\n");
				}
				ExportOutput exportOut = new ExportOutput(true,".vtk",simID.toString(),"_vol_"+j,fileDataContainerManager);
				fileDataContainerManager.append(exportOut.getFileDataContainerID(), sb.toString());
				exportOutV.add(exportOut);				

			}else{
				throw new RuntimeException("VTK Image format only for volume data");
			}	
		}	
	}
	
	ExportOutput[] exportOutputArr = exportOutV.toArray(new ExportOutput[0]);
	return exportOutputArr;

}

public ExportOutput[] makeVTKUnstructuredData0(OutputContext outputContext,JobRequest jobRequest, User user,
		DataServerImpl dataServerImpl, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager) throws Exception{
	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	boolean bChombo = dataServerImpl.isChombo(user, vcdID);
	final File tmpDir = PropertyLoader.getSystemTemporaryDirectory();
	if (bChombo){
		return makeVTKUnstructuredData_Chombo(outputContext, jobRequest, user, dataServerImpl, exportSpecs, tmpDir, fileDataContainerManager);
	}else{
		return makeVTKUnstructuredData_VCell(outputContext, jobRequest, user, dataServerImpl, exportSpecs, tmpDir, fileDataContainerManager);
	}
}


public ExportOutput[] makeVTKUnstructuredData_Chombo(OutputContext outputContext,final JobRequest jobRequest, User user,
		DataServerImpl dataServerImpl, ExportSpecs exportSpecs, File tmpDir, FileDataContainerManager fileDataContainerManager) throws Exception{
	
	String simID = exportSpecs.getVCDataIdentifier().getID();
	final VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	VariableSpecs variableSpecs = exportSpecs.getVariableSpecs();
	TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
	
	ChomboFiles chomboFiles = dataServerImpl.getChomboFiles(user, vcdID);
	ChomboVtkFileWriter chomboVTKFileWriter = new ChomboVtkFileWriter();
	File[] vtkFiles = chomboVTKFileWriter.writeVtuExportFiles(chomboFiles, tmpDir, new ChomboVtkFileWriter.ProgressListener() {
		public void progress(double percentDone) {
			exportServiceImpl.fireExportProgress(jobRequest.getJobID(), vcdID, "VTKUNSTR", percentDone);
		}
	});
	
	Vector<ExportOutput> exportOutV = new Vector<ExportOutput>();
	for (File file : vtkFiles){
		String dataID = file.getName().replace(simID.toString(), "");
		ExportOutput exportOut = new ExportOutput(true,".vtu",simID.toString(),dataID,fileDataContainerManager);
		fileDataContainerManager.manageExistingTempFile(exportOut.getFileDataContainerID(), file);
		exportOutV.add(exportOut);				
	}

	ExportOutput[] exportOutputArr = exportOutV.toArray(new ExportOutput[0]);
	return exportOutputArr;
}


public ExportOutput[] makeVTKUnstructuredData_VCell(OutputContext outputContext,final JobRequest jobRequest, User user,
		DataServerImpl dataServerImpl, ExportSpecs exportSpecs, File tmpDir, FileDataContainerManager fileDataContainerManager) throws Exception{
	
	String simID = exportSpecs.getVCDataIdentifier().getID();
	final VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	VariableSpecs variableSpecs = exportSpecs.getVariableSpecs();
	TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
	
	VCellSimFiles vcellFiles = dataServerImpl.getVCellSimFiles(user, vcdID);
	CartesianMeshVtkFileWriter cartesianMeshVtkFileWriter = new CartesianMeshVtkFileWriter();
	File[] vtkFiles = cartesianMeshVtkFileWriter.writeVtuExportFiles(vcellFiles, tmpDir, new CartesianMeshVtkFileWriter.ProgressListener() {
		public void progress(double percentDone) {
			exportServiceImpl.fireExportProgress(jobRequest.getJobID(), vcdID, "VTKUNSTR", percentDone);
		}
	});
	
	Vector<ExportOutput> exportOutV = new Vector<ExportOutput>();
	for (File file : vtkFiles){
		String dataID = file.getName().replace(simID.toString(), "");
		ExportOutput exportOut = new ExportOutput(true,".vtu",simID.toString(),dataID,fileDataContainerManager);
		fileDataContainerManager.manageExistingTempFile(exportOut.getFileDataContainerID(), file);
		exportOutV.add(exportOut);				
	}

	ExportOutput[] exportOutputArr = exportOutV.toArray(new ExportOutput[0]);
	return exportOutputArr;
}

public ExportOutput[] makeVTKUnstructuredData(OutputContext outputContext,JobRequest jobRequest, User user,
			DataServerImpl dataServerImpl, ExportSpecs exportSpecs,FileDataContainerManager fileDataContainerManager) throws Exception{

	String simID = exportSpecs.getVCDataIdentifier().getID();
	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	VariableSpecs variableSpecs = exportSpecs.getVariableSpecs();
	TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user,vcdID );
	CartesianMesh.UCDInfo ucdInfo = mesh.getUCDInfo();
	CartesianMesh.UCDInfo ucdInfoReduced = ucdInfo.removeNonMembraneGridNodes();
	
	Vector<ExportOutput> exportOutV = new Vector<ExportOutput>();
	//MeshDisplayAdapter meshDisplayAdapter = new MeshDisplayAdapter(mesh);
	//SurfaceCollection surfaceCollection = meshDisplayAdapter.generateMeshRegionSurfaces().getSurfaceCollection();
	//for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
	for (int j = timeSpecs.getBeginTimeIndex(); j <= timeSpecs.getEndTimeIndex(); j++){
	exportServiceImpl.fireExportProgress(
	jobRequest.getJobID(), vcdID, "VTKUNSTR",
	(double)(j-timeSpecs.getBeginTimeIndex())/(double)(timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1));
	
	//String fileID = simID + "_Full_" + formatTime(timeSpecs.getAllTimes()[j]) + "time_" + variableSpecs.getVariableNames().length + "vars";
	
	//File datafile = new File(tempDir, fileID + "_data.ucd");
	//FileWriter fileWriter = new FileWriter(datafile);
	
	Vector<double[]> volumeDataV = new Vector<double[]>();
	Vector<String> volumeDataNameV = new Vector<String>();
	Vector<String> volumeDataUnitV = new Vector<String>();
	Vector<double[]> membraneDataV = new Vector<double[]>();
	Vector<String> membraneDataNameV = new Vector<String>();
	Vector<String> membraneDataUnitV = new Vector<String>();
	for (int k = 0; k < variableSpecs.getVariableNames().length; k++) {
	SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[k], timeSpecs.getAllTimes()[j]);
	if(simDataBlock.getVariableType().equals(VariableType.VOLUME)){
	volumeDataNameV.add(variableSpecs.getVariableNames()[k]);
	volumeDataUnitV.add("unknown");
	volumeDataV.add(simDataBlock.getData());
	}else{
	membraneDataNameV.add(variableSpecs.getVariableNames()[k]);
	membraneDataUnitV.add("unknown");
	membraneDataV.add(simDataBlock.getData());
	}
	
	}


	if(volumeDataV.size() > 0){
		int[] regionIDs = new int[mesh.getNumVolumeElements()];
		for (int i = 0; i < regionIDs.length; i++) {
			regionIDs[i] = mesh.getVolumeRegionIndex(i);
		}
		StringWriter stringWriter = new StringWriter();
		writeVTKUnstructuredHeader(ucdInfo,vcdID,stringWriter);
		int CELL_INFO_COUNT = 1 + 4 + (ucdInfo.getNumVolumeNodesZ()>1?4:0);
		stringWriter.write("CELLS "+ucdInfo.getNumVolumeCells()+" "+(ucdInfo.getNumVolumeCells()*CELL_INFO_COUNT)+"\n");
		stringWriter.write(ucdInfo.getVolumeCellsString(true));
		writeVTKCellTypes(ucdInfo,ucdInfo.getNumVolumeCells(),(ucdInfo.getNumVolumeNodesZ()>1?VTK_HEXAHEDRON:VTK_QUAD),stringWriter);
		writeVTKCellData(volumeDataV.toArray(new double[0][]), regionIDs,volumeDataNameV.toArray(new String[0]), stringWriter);
//	AVS_UCD_Exporter.writeUCDVolume(
//	ucdInfo,
//	volumeDataNameV.toArray(new String[0]),
//	volumeDataUnitV.toArray(new String[0]),
//	volumeDataV.toArray(new double[0][]),
//	stringWriter);
		ExportOutput exportOut = new ExportOutput(true,".vtk",simID.toString(),"vol_"+j,fileDataContainerManager);
		fileDataContainerManager.append(exportOut.getFileDataContainerID(), stringWriter.toString());
		exportOutV.add(exportOut);				
	}
	if(membraneDataV.size() > 0){
		int[] regionIDs = new int[mesh.getNumMembraneElements()];
		for (int i = 0; i < regionIDs.length; i++) {
			regionIDs[i] = mesh.getMembraneRegionIndex(i);
		}
		StringWriter stringWriter = new StringWriter();
		writeVTKUnstructuredHeader(ucdInfoReduced/*ucdInfo*/,vcdID,stringWriter);
		int MEMBR_INFO_COUNT = 1 + 2 + (ucdInfo.getNumVolumeNodesZ()>1?2:0);
		stringWriter.write("CELLS "+ucdInfoReduced/*ucdInfo*/.getNumMembraneCells()+" "+(ucdInfoReduced/*ucdInfo*/.getNumMembraneCells()*MEMBR_INFO_COUNT)+"\n");
		stringWriter.write(ucdInfoReduced/*ucdInfo*/.getMembraneCellsString(0,true));
		writeVTKCellTypes(ucdInfoReduced/*ucdInfo*/,ucdInfoReduced/*ucdInfo*/.getNumMembraneCells(),(ucdInfo.getNumVolumeNodesZ()>1?VTK_QUAD:VTK_LINE),stringWriter);
		writeVTKCellData(membraneDataV.toArray(new double[0][]), regionIDs,membraneDataNameV.toArray(new String[0]), stringWriter);

//	AVS_UCD_Exporter.writeUCDMembrane(
//	ucdInfo,
//	membraneDataNameV.toArray(new String[0]),
//	membraneDataUnitV.toArray(new String[0]),
//	membraneDataV.toArray(new double[0][]),
//	stringWriter);
		ExportOutput exportOut = new ExportOutput(true,".vtk",simID.toString(),"memb_"+j,fileDataContainerManager);
		fileDataContainerManager.append(exportOut.getFileDataContainerID(), stringWriter.toString());
		exportOutV.add(exportOut);				
	}
	
	
	//AVS_UCD_Exporter.writeUCD(mesh,
	//(volumeDataNameV.size() == 0?null:volumeDataNameV.toArray(new String[0])),
	//(volumeDataUnitV.size() == 0?null:volumeDataUnitV.toArray(new String[0])),
	//(volumeDataV.size() == 0?null:volumeDataV.toArray(new double[0][])),
	//
	//(membraneDataNameV.size() == 0?null:membraneDataNameV.toArray(new String[0])),
	//(membraneDataUnitV.size() == 0?null:membraneDataUnitV.toArray(new String[0])),
	//(membraneDataV.size() == 0?null:membraneDataV.toArray(new double[0][])),
	//stringWriter);
	//ExportOutput exportOut = new ExportOutput(true,".ucd",simID.toString(),fileID,stringWriter.toString().getBytes());
	//exportOutV.add(exportOut);
	}
	//}
	
	ExportOutput[] exportOutputArr = exportOutV.toArray(new ExportOutput[0]);
	return exportOutputArr;
	
	
	//String fileID = simID + "_Full_" + NUM_TIMES + "times_" + variableSpecs.getVariableNames().length + "vars";
	//File datafile = new File(tempDir, fileID + "_data.nrrd");
	//nrrdInfo.setDatafile(datafile.getName());
	//DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(datafile)));
	//try {
	//for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
	//for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
	//double[] data = dataServerImpl.getSimDataBlock(user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
	//for (int k = 0; k < data.length; k++){
	//out.writeDouble(data[k]);
	//}
	//}
	//}
	//} catch (IOException exc) {
	//throw new DataAccessException(exc.toString());
	//} finally {
	//out.close();
	//}

}

private void addRegionID(){
	
}

private void writeVTKCellData(double[][] cellData,int[] regionIDs,String[] dataNames,StringWriter stringWriter){
	stringWriter.write("CELL_DATA "+cellData[0].length+"\n");
	
	stringWriter.write("SCALARS "+"regionID"+" int 1"+"\n");
	stringWriter.write("LOOKUP_TABLE default"+"\n");
	for (int j = 0; j < regionIDs.length; j++) {
		stringWriter.write(regionIDs[j]+"\n");
	}

	for (int i = 0; i < dataNames.length; i++) {
		stringWriter.write("SCALARS "+dataNames[i]+" double 1"+"\n");
		stringWriter.write("LOOKUP_TABLE default"+"\n");
		for (int j = 0; j < cellData[i].length; j++) {
			stringWriter.write(cellData[i][j]+"\n");
		}
	}
}
private void writeVTKCellTypes(CartesianMesh.UCDInfo ucdInfo, int cellCount, int cellType,StringWriter stringWriter){
	stringWriter.write("CELL_TYPES "+cellCount+"\n");
	for (int i = 0; i < cellCount; i++) {
		if(i!= 0 && (i%32 == 0)){
			stringWriter.write("\n");
		}
		stringWriter.write(cellType+" ");
	}
	stringWriter.write("\n");
}
private void writeVTKUnstructuredHeader(CartesianMesh.UCDInfo ucdInfo,VCDataIdentifier vcdID,StringWriter stringWriter){
	stringWriter.write("# vtk DataFile Version 2.0"+"\n");
	stringWriter.write("Simulation "+vcdID.toString()+"\n");
	stringWriter.write("ASCII"+"\n");
	stringWriter.write("DATASET UNSTRUCTURED_GRID"+"\n");
	stringWriter.write("POINTS "+ucdInfo.getNumPointsXYZ()+" double"+"\n");
	stringWriter.write(ucdInfo.getMeshGridNodesString(true));

}
private static String formatTime(double timePoint){
	StringBuffer timeSB = new StringBuffer(timePoint+"");
	int dotIndex = timeSB.toString().indexOf(".");
	if(dotIndex != -1){
		timeSB.replace(dotIndex,dotIndex+1,"_");
		if(dotIndex == 0){
		timeSB.insert(0,"0");
		}
		if(dotIndex == timeSB.length()-1){
		timeSB.append("0");
		}
	}else{
		timeSB.append("_0");
	}

	return timeSB.toString();
}
}
