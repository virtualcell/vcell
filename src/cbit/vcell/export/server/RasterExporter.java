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
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.chombo.ChomboDataset;
import org.vcell.vis.io.ChomboFileReader;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.mapping.CartesianMeshVtkFileWriter;
import org.vcell.vis.mapping.ChomboMeshMapping;
import org.vcell.vis.mapping.ChomboVtkFileWriter;
import org.vcell.vis.vismesh.VisDataset;
import org.vcell.vis.vismesh.VisDataset.VisDomain;
import org.vcell.vis.vtk.VtkGridUtils;

import vtk.vtkUnstructuredGrid;
import cbit.vcell.export.AVS_UCD_Exporter;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.nrrd.NrrdWriter;
import cbit.vcell.math.VariableType;
import cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard.FileSummaryDescriptor;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo.PostProcessDataType;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.DataSetControllerImpl.ProgressListener;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solvers.CartesianMesh;
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


/**
 * This method was created in VisualAge.
 */
private NrrdInfo[] exportPDEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs2, GeometrySpecs geometrySpecs, RasterSpecs rasterSpecs, FileDataContainerManager fileDataContainerManager) 
						throws RemoteException, DataAccessException, IOException {

	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);
	DataProcessingOutputInfo dataProcessingOutputInfo = null;
	try {
		dataProcessingOutputInfo = (DataProcessingOutputInfo)dataServerImpl.doDataOperation(user,new DataOperation.DataProcessingOutputInfoOP(vcdID,false,outputContext));
	} catch (Exception e) {
		e.printStackTrace();
//		throw new Exception("Data Processing Output Error - '"+e.getMessage()+"'  (Note: Data Processing Output is generated automatically when running VCell 5.2 or later simulations)");
	}

	String simID = vcdID.getID();
	int NUM_TIMES = timeSpecs2.getEndTimeIndex()-timeSpecs2.getBeginTimeIndex()+1;
	switch (rasterSpecs.getFormat()) {
		case NRRD_SINGLE: {
			// single info, specifying 5D
			switch (geometrySpecs.getModeID()) {
				case GEOMETRY_FULL: {
					boolean bPostProcess = false;
					NrrdInfo nrrdInfo = null;
					if(dataProcessingOutputInfo != null){
						for(int nameIndex=0;nameIndex<variableSpecs.getVariableNames().length;nameIndex++){
							String myVarName = variableSpecs.getVariableNames()[nameIndex];
							boolean bFound = false;
							for(int varIndex=0;varIndex<dataProcessingOutputInfo.getVariableNames().length;varIndex++){
								if(dataProcessingOutputInfo.getVariableNames()[varIndex].equals(myVarName)){
									bFound = true;
									if(!dataProcessingOutputInfo.getPostProcessDataType(myVarName).equals(PostProcessDataType.image)){
										throw new DataAccessException("Export nrrd only implemented for 'image' post-process data");
									}
									if(bPostProcess){
										if(dataProcessingOutputInfo.getVariableISize(myVarName).getX() != nrrdInfo.getSizes()[0] ||
											dataProcessingOutputInfo.getVariableISize(myVarName).getY() != nrrdInfo.getSizes()[1] ||
											dataProcessingOutputInfo.getVariableISize(myVarName).getZ() != nrrdInfo.getSizes()[2]){
											throw new DataAccessException("Export nrrd post-process must all have same x,y,z size, try exporting individually");
										}
										continue;
									}
									bPostProcess = true;
									nrrdInfo = NrrdInfo.createBasicNrrdInfo(
											5,
											new int[] {dataProcessingOutputInfo.getVariableISize(myVarName).getX(),
													dataProcessingOutputInfo.getVariableISize(myVarName).getY(),
													dataProcessingOutputInfo.getVariableISize(myVarName).getZ(),
													NUM_TIMES, variableSpecs.getVariableNames().length},
											"double",
											"raw"
										);
									nrrdInfo.setSpacings(new double[] {
										dataProcessingOutputInfo.getVariableExtent(myVarName).getX()/dataProcessingOutputInfo.getVariableISize(myVarName).getX(),
										dataProcessingOutputInfo.getVariableExtent(myVarName).getY()/dataProcessingOutputInfo.getVariableISize(myVarName).getY(),
										dataProcessingOutputInfo.getVariableExtent(myVarName).getZ()/dataProcessingOutputInfo.getVariableISize(myVarName).getZ(),
										Double.NaN,	// timepoints can have irregular intervals
										Double.NaN  // not meaningful for variables
									});
									nrrdInfo.setCanonicalFileNamePrefix(simID + "_" + NUM_TIMES + "times_" + "postprocvars");
									nrrdInfo.setContent("5D(x,y,z,t,var) PostProcess VCData from " + simID);
								}
							}
							if(bPostProcess && !bFound){
								throw new DataAccessException("Mixed post-process and regular data not allowed");
							}
						}
						
					}
					if(!bPostProcess){
						// create the info object
						nrrdInfo = NrrdInfo.createBasicNrrdInfo(
							5,
							new int[] {mesh.getSizeX(),	mesh.getSizeY(), mesh.getSizeZ(),NUM_TIMES, variableSpecs.getVariableNames().length},
							"double",
							"raw"
						);
						nrrdInfo.setSpacings(new double[] {
							mesh.getExtent().getX() / mesh.getSizeX(),
							mesh.getExtent().getY() / mesh.getSizeY(),
							mesh.getExtent().getZ() / mesh.getSizeZ(),
							Double.NaN,	// timepoints can have irregular intervals
							Double.NaN  // not meaningful for variables
						});
						nrrdInfo.setCanonicalFileNamePrefix(simID + "_" + NUM_TIMES + "times_" + variableSpecs.getVariableNames().length + "vars");
						nrrdInfo.setContent("5D(x,y,z,t,var) VCData from " + simID);
					}
					
					nrrdInfo.setCenters(new String[] {"cell", "cell", "cell", "cell", "???"});

					nrrdInfo.setSeparateHeader(rasterSpecs.isSeparateHeader());
					// make datafile and update info
					nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
					//DataOutputStream out = null;
				
					//out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileDataContainerManager.getFile(nrrdInfo.getDataFileID()))));
					int bufferSize = 8*nrrdInfo.getSizes()[0]*nrrdInfo.getSizes()[1]*nrrdInfo.getSizes()[2];
					for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
						for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
							double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
							if(data.length != (bufferSize/8)){//doubles are 8 bytes
								System.out.println(data.length+" "+(bufferSize/8));
								throw new DataAccessException("Var: "+variableSpecs.getVariableNames()[i]+" size != calculated size");
							}
							byte[] output = new byte[bufferSize];
							ByteBuffer byteBuffer = ByteBuffer.wrap(output);
							for (int k = 0; k < data.length; k++){
								byteBuffer.putDouble(data[k]);
							}
							fileDataContainerManager.append(nrrdInfo.getDataFileID(), output);
						}
					}
				
				// write out final output
				nrrdInfo = NrrdWriter.writeNRRD(nrrdInfo,fileDataContainerManager);
				return new NrrdInfo[] {nrrdInfo};
				}
				default: {
					throw new DataAccessException("NRRD export from slice not yet supported");
				}
			}			
		}
		case NRRD_BY_TIME: {
			switch (geometrySpecs.getModeID()) {
				case GEOMETRY_FULL: {
					Vector<NrrdInfo> nrrdinfoV = new Vector();
					for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
						// create the info object
						NrrdInfo nrrdInfo = NrrdInfo.createBasicNrrdInfo(
							4,
							new int[] {mesh.getSizeX(),	mesh.getSizeY(), mesh.getSizeZ(),variableSpecs.getVariableNames().length},
							"double",
							"raw"
						);
						nrrdInfo.setCanonicalFileNamePrefix(simID + "_" + formatTime(timeSpecs2.getAllTimes()[j]) + "time_" + variableSpecs.getVariableNames().length + "vars");
						nrrdinfoV.add(nrrdInfo);
						nrrdInfo.setContent("4D(x,y,z,var) VCData from " + simID);
						nrrdInfo.setCenters(new String[] {"cell", "cell", "cell","???"});
						nrrdInfo.setSpacings(new double[] {
							mesh.getExtent().getX() / mesh.getSizeX(),
							mesh.getExtent().getY() / mesh.getSizeY(),
							mesh.getExtent().getZ() / mesh.getSizeZ(),
							Double.NaN  // not meaningful for variables
						});
						nrrdInfo.setSeparateHeader(rasterSpecs.isSeparateHeader());
						// make datafile and update info						
						nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
		
						try {
							//out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileDataContainerManager.getFile(nrrdInfo.getDataFileID()))));
							for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
								//for (int j = 0; j < timeSpecs.getAllTimes().length; j++){
									double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
									for (int k = 0; k < data.length; k++){
										fileDataContainerManager.append(nrrdInfo.getDataFileID(), String.valueOf(data[k]));
									}
								//}
							}
						} catch (IOException exc) {
							throw new DataAccessException(exc.toString());
						} 
						// write out final output
						nrrdInfo = NrrdWriter.writeNRRD(nrrdInfo,fileDataContainerManager);
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
					Vector nrrdinfoV = new Vector();
					for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
						// create the info object
						NrrdInfo nrrdInfo = NrrdInfo.createBasicNrrdInfo(
							4,
							new int[] {mesh.getSizeX(),	mesh.getSizeY(), mesh.getSizeZ(), NUM_TIMES},
							"double",
							"raw"
						);
						nrrdInfo.setCanonicalFileNamePrefix(simID + "_" + NUM_TIMES + "times_" + variableSpecs.getVariableNames()[i] + "vars");
						nrrdinfoV.add(nrrdInfo);
						nrrdInfo.setContent("4D(x,y,z,t) VCData from " + simID);
						nrrdInfo.setCenters(new String[] {"cell", "cell", "cell", "cell"});
						nrrdInfo.setSpacings(new double[] {
							mesh.getExtent().getX() / mesh.getSizeX(),
							mesh.getExtent().getY() / mesh.getSizeY(),
							mesh.getExtent().getZ() / mesh.getSizeZ(),
							Double.NaN,	// timepoints can have irregular intervals
						});
						nrrdInfo.setSeparateHeader(rasterSpecs.isSeparateHeader());
						// make datafile and update info
						nrrdInfo.setDataFileID(fileDataContainerManager.getNewFileDataContainerID());
					
						try {
							//out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileDataContainerManager.getFile(nrrdInfo.getDataFileID()))));
							//for (int i = 0; i < variableSpecs.getVariableNames().length; i++){
								for (int j = timeSpecs2.getBeginTimeIndex(); j <= timeSpecs2.getEndTimeIndex(); j++){
									double[] data = dataServerImpl.getSimDataBlock(outputContext,user, vcdID, variableSpecs.getVariableNames()[i], timeSpecs2.getAllTimes()[j]).getData();
									for (int k = 0; k < data.length; k++){
										fileDataContainerManager.append(nrrdInfo.getDataFileID(),String.valueOf(data[k]));
									}
								}
							//}
						} catch (IOException exc) {
							throw new DataAccessException(exc.toString());
						}
						// write out final output
						nrrdInfo = NrrdWriter.writeNRRD(nrrdInfo,fileDataContainerManager);
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
	File[] vtkFiles = chomboVTKFileWriter.writeFiles(chomboFiles, tmpDir, new ChomboVtkFileWriter.ProgressListener() {
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
	File[] vtkFiles = cartesianMeshVtkFileWriter.writeFiles(vcellFiles, tmpDir, new CartesianMeshVtkFileWriter.ProgressListener() {
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
private String formatTime(double timePoint){
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
