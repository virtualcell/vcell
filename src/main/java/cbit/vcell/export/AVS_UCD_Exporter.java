/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export;

import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.solvers.CartesianMesh;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2004 10:52:10 AM)
 * @author: Jim Schaff
 */
public class AVS_UCD_Exporter {
	

/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 10:54:30 AM)
 * @param geometrySurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
	public static void writeUCDGeometryOnly(GeometrySurfaceDescription geometrySurfaceDescription,java.io.Writer writer)
		throws Exception {

		final String QUAD_TYPE = "quad";
		
//		GeometricRegion regions[] = geometrySurfaceDescription.getGeometricRegions();
		SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
		if(surfaceCollection == null){
			geometrySurfaceDescription.updateAll();
			surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
		}
		Node nodes[] = surfaceCollection.getNodes();
		int numNodes = nodes.length;
		int numCells = 0;
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
			numCells += surfaceCollection.getSurfaces(i).getPolygonCount();
		}
		int numNodeData = 0;
		int numCellData = 0;
		int numModelData = 0;
		writer.write(numNodes+" "+numCells+" "+numNodeData+" "+numCellData+" "+numModelData+"\n");
		for (int i = 0; i < nodes.length; i++){
			writer.write(nodes[i].getGlobalIndex()+" "+nodes[i].getX()+" "+nodes[i].getY()+" "+nodes[i].getZ()+"\n");
		}
		//
		// print the "Cells" (polygons) for each surface (each surface has it's own material id).
		//
		int cellID = 0;
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
			Surface surface = surfaceCollection.getSurfaces(i);
			String materialType = Integer.toString(i); // for material now just give it the index (later need to collect these in terms of closed objects).
			for (int j = 0; j < surface.getPolygonCount(); j++){
				Polygon polygon = surface.getPolygons(j);
				int node0Index = polygon.getNodes(0).getGlobalIndex();
				int node1Index = polygon.getNodes(1).getGlobalIndex();
				int node2Index = polygon.getNodes(2).getGlobalIndex();
				int node3Index = polygon.getNodes(3).getGlobalIndex();
				writer.write(cellID+" "+materialType+" "+QUAD_TYPE+" "+node0Index+" "+node1Index+" "+node2Index+" "+node3Index+"\n");
				cellID++;
			}
		}
	}
	//-----
	public static void writeUCDVolGeomAndData(CartesianMesh.UCDInfo ucdInfo,
			String[] volumeVariableNames,String[] volumeVariableUnits,double[][] volumeData,
			java.io.Writer writer) throws Exception{
		
		if(volumeData != null && volumeData.length == 0){
			volumeData = null;
		}

		if(volumeData != null && volumeData[0].length != ucdInfo.getNumVolumeCells()){
			throw new Exception("Volume data length does not match mesh info.");
		}

		writer.write(ucdInfo.getHeaderString((volumeData != null?volumeData.length:0),0));
		writer.write(ucdInfo.getMeshGridNodesString(false));
		writer.write(ucdInfo.getVolumeCellsString(false));
		ucdInfo.writeCellData(false,
				volumeVariableNames, volumeVariableUnits, volumeData,
				null,null,null, writer);

	}
	
	//-----
	public static void writeUCDMembGeomAndData(CartesianMesh.UCDInfo ucdInfo,
			String[] membraneVariableNames,String[] membraneVariableUnits,double[][] membraneData,
			java.io.Writer writer) throws Exception{
		
		if(membraneData != null && membraneData.length == 0){
			membraneData = null;
		}

		if(membraneData != null && membraneData[0].length != ucdInfo.getNumMembraneCells()){
			throw new Exception("Membrane data length does not match UCD info.");
		}
		
		writer.write(ucdInfo.getHeaderString(0, (membraneData != null?membraneData.length:0)));
		writer.write(ucdInfo.getMeshGridNodesString(false));
		writer.write(ucdInfo.getMembraneCellsString(0,false));
		ucdInfo.writeCellData(false,
				null,null,null,
				membraneVariableNames, membraneVariableUnits, membraneData, writer);


	}
//	public static void writeUCD(CartesianMesh cartesianMesh,
//			String[] volumeVariableNames,String[] volumeVariableUnits,double[][] volumeData,
//			String[] membraneVariableNames,String[] membraneVariableUnits,double[][] membraneData,
//			java.io.Writer writer) throws Exception {
//	
////		if(volumeData != null && volumeData.length == 0){
////			volumeData = null;
////		}
////		if(membraneData != null && membraneData.length == 0){
////			membraneData = null;
////		}
////		if(volumeData != null && volumeData[0].length != cartesianMesh.getNumVolumeElements()){
////			throw new Exception("Volume data length does not match mesh info.");
////		}
////		if(membraneData != null && membraneData[0].length != cartesianMesh.getNumMembraneElements()){
////			throw new Exception("Membrane data length does not match mesh info.");
////		}
//	
//		CartesianMesh.UCDInfo ucdInfo = cartesianMesh.getUCDInfo();
//		
//		writer.write(ucdInfo.getHeaderString((volumeData != null?volumeData.length:0), (membraneData != null?membraneData.length:0))+"\n");
//		writer.write(ucdInfo.getMeshGridNodesString(false));
//		writer.write(ucdInfo.getVolumeCellsString(false));
//		writer.write(ucdInfo.getMembraneCellsString(ucdInfo.getNumVolumeNodesX(),false));
//		ucdInfo.writeCellData(true,
//				volumeVariableNames, volumeVariableUnits, volumeData,
//				membraneVariableNames, membraneVariableUnits, membraneData, writer);
//		
////		//UCD Cell Data (both volume and membrane data)
////		if(numCellData != 0){
////			String cellDataS = numCellData+"";
////			for (int i = 0; i < numCellData; i++) {
////				cellDataS = cellDataS+" 1";
////			}
////			writer.write(cellDataS+"\n");
////			for (int i = 0; volumeVariableNames != null && i < volumeVariableNames.length; i++) {
////				writer.write(volumeVariableNames[i]+","+volumeVariableUnits[i]+"\n");
////			}
////			for (int i = 0; membraneVariableNames != null && i < membraneVariableNames.length; i++) {
////				writer.write(membraneVariableNames[i]+","+membraneVariableUnits[i]+"\n");
////			}
////			
////			String membDataFiller = "";
////			for (int i = 0; membraneData != null && i < membraneData.length; i++) {
////				membDataFiller+= " 0";
////			}
////			String volDataFiller = "";
////			for (int i = 0; volumeData != null && i < volumeData.length; i++) {
////				volDataFiller+= "0 ";
////			}
////
////			for (int i = 0; i < cartesianMesh.getNumVolumeElements(); i++) {
////				writer.write(i+" ");
////				if(volumeData != null){
////					for (int j = 0; j < volumeData.length; j++) {
////						writer.write((j!= 0?" ":"")+volumeData[j][i]);			
////					}
////				}else{
////					writer.write(volDataFiller);
////				}
////				writer.write(membDataFiller+"\n");
////			}
////			
////			for (int i = 0; i < cartesianMesh.getNumMembraneElements(); i++) {
////				writer.write((volCellID+i)+" ");
////				writer.write(volDataFiller);
////				if(membraneData != null){
////					for (int j = 0; j < membraneData.length; j++) {
////						writer.write((j!= 0?" ":"")+membraneData[j][i]);
////					}
////				}else{
////					writer.write(membDataFiller);
////				}
////				writer.write("\n");
////			}
////
////		}
//		
//		
////		MeshDisplayAdapter meshDisplayAdapter = new MeshDisplayAdapter(cartesianMesh);
////		SurfaceCollection surfaceCollection = meshDisplayAdapter.generateMeshRegionSurfaces().getSurfaceCollection();
////		int MembCellID = volCellID;
////		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
////			Surface surface = surfaceCollection.getSurfaces(i);
////			String MembMaterialType = Integer.toString(i+1/*+1 for test purpose only REMOVE!!!!!*/); // for material now just give it the index (later need to collect these in terms of closed objects).
////			for (int j = 0; j < surface.getPolygonCount(); j++){
////				Polygon polygon = surface.getPolygons(j);
////				int node0Index = polygon.getNodes(0).getGlobalIndex();
////				int node1Index = polygon.getNodes(1).getGlobalIndex();
////				int node2Index = polygon.getNodes(2).getGlobalIndex();
////				int node3Index = polygon.getNodes(3).getGlobalIndex();
////				writer.write(MembCellID+" "+MembMaterialType+" "+UCD_QUAD_CELL_TYPE+" "+node0Index+" "+node1Index+" "+node2Index+" "+node3Index+"\n");
////				MembCellID++;
////			}
////		}
//	
//		
//	//	if(membraneData != null){
//	//		writer.write(membraneData.length+" 1"+"\n");
//	//		for (int i = 0; i < membraneVariableNames.length; i++) {
//	//			writer.write(membraneVariableNames[i]+","+membraneVariableUnits[i]+"\n");
//	//		}
//	//		for (int i = 0; i < membraneData[0].length; i++) {
//	//			writer.write((i+1));	
//	//			for (int j = 0; j < membraneData.length; j++) {
//	//				writer.write((j!=0?" ":"")+membraneData[i][j]);
//	//			}
//	//			writer.write("\n");
//	//		}
//	//	}
//	}
}
