package org.vcell.vis.io;

import java.io.File;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

import org.vcell.vis.chombo.ChomboBoundaries;
import org.vcell.vis.chombo.ChomboBoundaries.BorderCellInfo;
import org.vcell.vis.chombo.ChomboBoundaries.MeshMetrics;
import org.vcell.vis.chombo.ChomboBoundaries.SurfaceTriangle;
import org.vcell.vis.chombo.ChomboBox;
import org.vcell.vis.chombo.ChomboDataset;
import org.vcell.vis.chombo.ChomboLevel;
import org.vcell.vis.chombo.ChomboLevelData;
import org.vcell.vis.chombo.ChomboMembraneVarData;
import org.vcell.vis.chombo.ChomboMesh;
import org.vcell.vis.chombo.ChomboMeshData;
import org.vcell.vis.core.Face;
import org.vcell.vis.core.Vect3D;
import org.vcell.vis.io.ChomboFiles.ChomboFileEntry;
import org.vcell.vis.mapping.chombo.ChomboVtkFileWriter;

import cbit.vcell.solvers.CartesianMeshChombo;
import cbit.vcell.solvers.CartesianMeshChombo.FeaturePhaseVol;

public class ChomboFileReader {
	//private static final String METRICS_DATASET = "metrics";
	private static final String MESH_ATTR_DIMENSION = "dimension";
	private static final String MESH_ATTR_ORIGIN = "origin";
	private static final String MESH_ATTR_EXTENT = "extent";
	
	private static ChomboMeshData readMesh(String meshFileName, String vol0FileName) throws Exception{
		
		ChomboMesh chomboMesh = new ChomboMesh();
		
		if(H5.H5open() < 0){
			throw new Exception("H5.H5open() failed");
		}
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		if(fileFormat == null){
			throw new Exception("FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5) failed, returned null.");
		}
		FileFormat meshFile = fileFormat.createInstance(new File(meshFileName).getAbsolutePath(), FileFormat.READ);
		try {
			meshFile.open();
			
			DefaultMutableTreeNode meshRootNode = (DefaultMutableTreeNode)meshFile.getRootNode();
			Group meshRootGroup = (Group)meshRootNode.getUserObject();
			Group meshGroup = Hdf5Reader.getChildGroup(meshRootGroup,"mesh");
			
			chomboMesh.setDimension(Hdf5Reader.getIntAttribute(meshGroup,MESH_ATTR_DIMENSION));		
			chomboMesh.setExtent(Hdf5Reader.getVect3DAttribute(meshGroup,MESH_ATTR_EXTENT,1.0));
			chomboMesh.setOrigin(Hdf5Reader.getVect3DAttribute(meshGroup,MESH_ATTR_ORIGIN,0.0));
			
			// it's very wasteful here, but what can I do?
			CartesianMeshChombo cartesianMeshChombo = CartesianMeshChombo.readMeshFile(new File(meshFileName));
			for (FeaturePhaseVol fpv : cartesianMeshChombo.getFeaturePhaseVols())
			{
				chomboMesh.addFeaturePhase(fpv.feature, fpv.iphase);
			}
			
			//Hdf5Reader.DataColumn[] metricsColumns = Hdf5Reader.getDataTable(meshGroup,METRICS_DATASET);
			if (chomboMesh.getDimension()==2){
				Hdf5Reader.DataColumn[] segmentColumns = Hdf5Reader.getDataTable(meshGroup,"segments");
				Hdf5Reader.DataColumn[] verticesColumns = Hdf5Reader.getDataTable(meshGroup,"vertices");
				ChomboBoundaries boundaries = chomboMesh.getBoundaries();
				int numVertices = verticesColumns[0].getNumRows();
				int numSegments = segmentColumns[0].getNumRows();
				for (int i=0;i<numVertices;i++){
					double x = verticesColumns[0].getValue(i);
					double y = verticesColumns[1].getValue(i);
					double z = 0.0;
					boundaries.addPoint(new ChomboBoundaries.Point(x,y,z));
				}
				for (int i=0;i<numSegments;i++){
					int v1 = (int)segmentColumns[1].getValue(i);
					int v2 = (int)segmentColumns[2].getValue(i);
					int chomboIndex = i; // THIS COULD BE WRONG - is the chomboIndex one-to-one with the line segments? ... if not should be in the HDF5 file.
					boundaries.addSegment(new ChomboBoundaries.Segment(chomboIndex, v1, v2));
				}
			}else if (chomboMesh.getDimension()==3){
				Hdf5Reader.DataColumn[] surfaceTriangleColumns = Hdf5Reader.getDataTable(meshGroup,"surface triangles");
				ChomboBoundaries boundaries = chomboMesh.getBoundaries();
				int numTriangles = surfaceTriangleColumns[0].getNumRows();
				for (int row=0;row<numTriangles;row++){
					int index = (int)surfaceTriangleColumns[0].getValue(row);
					int faceNumber = (int)surfaceTriangleColumns[1].getValue(row);
					int neighborIndex = (int)surfaceTriangleColumns[2].getValue(row); // not used currently
					double x0 = surfaceTriangleColumns[3].getValue(row);
					double y0 = surfaceTriangleColumns[4].getValue(row);
					double z0 = surfaceTriangleColumns[5].getValue(row);
					int p0_index = boundaries.getOrCreatePoint(x0,y0,z0);
					double x1 = surfaceTriangleColumns[6].getValue(row);
					double y1 = surfaceTriangleColumns[7].getValue(row);
					double z1 = surfaceTriangleColumns[8].getValue(row);
					int p1_index = boundaries.getOrCreatePoint(x1,y1,z1);
					double x2 = surfaceTriangleColumns[9].getValue(row);
					double y2 = surfaceTriangleColumns[10].getValue(row);
					double z2 = surfaceTriangleColumns[11].getValue(row);
					int p2_index = boundaries.getOrCreatePoint(x2,y2,z2);
					Face face = Face.fromInteger(faceNumber);
					SurfaceTriangle surfaceTriangle = new SurfaceTriangle(index, face, p0_index,p1_index,p2_index);
					boundaries.addSurfaceTriangle(surfaceTriangle);
				}
				Hdf5Reader.DataColumn[] metricsColumns = Hdf5Reader.getDataTable(meshGroup,"membrane elements");
				MeshMetrics meshMetrics = boundaries.getMeshMetrics();
				int numMeshMetrics = metricsColumns[0].getNumRows();
				for (int row=0;row<numMeshMetrics;row++){
					int index = (int)metricsColumns[0].getValue(row);
					int level = (int)metricsColumns[1].getValue(row);
					int i = (int)metricsColumns[2].getValue(row); // not used currently
					int j = (int)metricsColumns[3].getValue(row); // not used currently
					int k = (int)metricsColumns[4].getValue(row); // not used currently
					double x = metricsColumns[5].getValue(row);
					double y = metricsColumns[6].getValue(row);
					double z = metricsColumns[7].getValue(row);
					Vect3D center = new Vect3D(x,y,z);
					double normalX = metricsColumns[8].getValue(row);
					double normalY = metricsColumns[9].getValue(row);
					double normalZ = metricsColumns[10].getValue(row);
					Vect3D normal = new Vect3D(normalX,normalY,normalZ);
					double volumeFraction = metricsColumns[11].getValue(row);
					double areaFraction = metricsColumns[12].getValue(row);
					int membraneId = (int)metricsColumns[13].getValue(row);
					int cornerPhaseMask = (int)metricsColumns[14].getValue(row);
					BorderCellInfo borderCellInfo = new BorderCellInfo(index,level,i,j,k,center,normal,volumeFraction,areaFraction,membraneId,cornerPhaseMask);
					meshMetrics.addBorderCellInfo(borderCellInfo);
				}
			}else{
				throw new Exception("failed to read chombo file, unexpected mesh dimension "+chomboMesh.getDimension());
			}
		}finally{
			meshFile.close();
		}
		FileFormat vol0File = fileFormat.createInstance(new File(vol0FileName).getAbsolutePath(), FileFormat.READ);
		try {
			vol0File.open();
			
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)vol0File.getRootNode();
			Group rootGroup = (Group)rootNode.getUserObject();
			
			Group level0Group = Hdf5Reader.getChildGroup(rootGroup, "level_0");
			double time = Hdf5Reader.getDoubleAttribute(level0Group, "time");
			ChomboMeshData chomboMeshData = new ChomboMeshData(chomboMesh, time);

			int numComponents = Hdf5Reader.getIntAttribute(rootGroup, "num_components");
			int numLevels = Hdf5Reader.getIntAttribute(rootGroup, "num_levels");
			int fractionComponentIndex = -1;
			for (int i=0;i<numComponents;i++){
				String compName = Hdf5Reader.getStringAttribute(rootGroup, "component_"+i);
				chomboMeshData.addComponentName(compName);
				if (compName.equals("fraction-0")){
					fractionComponentIndex = i;
					chomboMeshData.setFraction0ComponentIndex(fractionComponentIndex);
				}
			}
			for (int i=0;i<numLevels;i++){
				Group levelGroup = Hdf5Reader.getChildGroup(rootGroup, "level_"+i);
				int refinement = 2;
				if (i==0){
					refinement = 1;
				}
				ChomboLevel chomboLevel = new ChomboLevel(chomboMesh, i, refinement);
				Hdf5Reader.DataColumn[] boxColumns = Hdf5Reader.getDataTable(levelGroup, "boxes");
				int[] lo_i, lo_j, lo_k, hi_i, hi_j, hi_k;
				if (chomboMesh.getDimension()==2){
					lo_i = ((Hdf5Reader.IntColumn)boxColumns[0]).data;
					lo_j = ((Hdf5Reader.IntColumn)boxColumns[1]).data;
					hi_i = ((Hdf5Reader.IntColumn)boxColumns[2]).data;
					hi_j = ((Hdf5Reader.IntColumn)boxColumns[3]).data;
					lo_k = new int[boxColumns[0].getNumRows()];
					hi_k = new int[boxColumns[0].getNumRows()];
				}else{
					lo_i = ((Hdf5Reader.IntColumn)boxColumns[0]).data;
					lo_j = ((Hdf5Reader.IntColumn)boxColumns[1]).data;
					lo_k = ((Hdf5Reader.IntColumn)boxColumns[2]).data;
					hi_i = ((Hdf5Reader.IntColumn)boxColumns[3]).data;
					hi_j = ((Hdf5Reader.IntColumn)boxColumns[4]).data;
					hi_k = ((Hdf5Reader.IntColumn)boxColumns[5]).data;
				}				
				for (int b=0; b<boxColumns[0].getNumRows(); b++){
					ChomboBox chomboBox = new ChomboBox(chomboLevel,lo_i[b],hi_i[b],lo_j[b],hi_j[b],lo_k[b],hi_k[b],chomboMesh.getDimension());
					chomboLevel.addBox(chomboBox);
				}
				chomboMesh.addLevel(chomboLevel);
				//
				// read the variables
				//
				Hdf5Reader.DataColumn[] data = Hdf5Reader.getDataTable(levelGroup,"data:datatype=0");
				Hdf5Reader.DataColumn[] offsets = Hdf5Reader.getDataTable(levelGroup,"data:offsets=0");
				ChomboLevelData chomboLevelData = new ChomboLevelData(i,fractionComponentIndex,((Hdf5Reader.DoubleColumn)data[0]).data,((Hdf5Reader.LongColumn)offsets[0]).data);
				chomboMeshData.addLevelData(chomboLevelData);
			}
			
			readMembraneVarData(chomboMeshData, rootGroup);

			return chomboMeshData;
		}finally{
			vol0File.close();
		}
	}
	
	public static ChomboDataset readDataset(ChomboFiles chomboFiles, int timeIndex) throws Exception{
		String meshFilename = chomboFiles.getMeshFile().getPath();
		ChomboDataset chomboDataset = new ChomboDataset();
		int domainOrdinal = 0;
		for (ChomboFileEntry cfe : chomboFiles.getEntries(timeIndex))
		{
			File domainFile = cfe.getFile();
			ChomboMeshData chomboMeshData = readMesh(meshFilename, domainFile.getPath());
			ChomboMesh chomboMesh = chomboMeshData.getMesh();
			ChomboDataset.ChomboCombinedVolumeMembraneDomain chomboCombinedVolumeMembraneDomain = new ChomboDataset.ChomboCombinedVolumeMembraneDomain(cfe,chomboMesh,chomboMeshData,domainOrdinal);
			chomboDataset.addDomain(chomboCombinedVolumeMembraneDomain);
			domainOrdinal++;
		}
		return chomboDataset;
	}	
	
	public static void main(String[] args){
		try {
			ChomboMeshData meshdata = readMesh("C:\\Developer\\eclipse\\workspace\\pyVCell\\ChomboUtils\\SimData\\SimID_85232385_0_.mesh.hdf5","C:\\Developer\\eclipse\\workspace\\pyVCell\\ChomboUtils\\SimData\\SimID_85232385_0_000075.feature_EC.vol0.hdf5");
			System.out.println("read mesh of dimension "+meshdata.getMesh().getDimension());
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	//
	// Membrane data are stored as a UCHC (or vcell) extension to the normal Chombo Data.
	// ChomboMembraneVarData was formally called VCellSolution by Fei.
	//
	private static void readMembraneVarData(ChomboMeshData chomboMeshData, Group rootGroup)
	{
			// I added solution and extrapolated_volumes group to hold all the solutions from vcell 
		String[] groups = new String[]{"solution"/*, "extrapolated_volumes"*/};
		for (String group: groups)
		{
			try
			{
				Group vcellGroup = Hdf5Reader.getChildGroup(rootGroup, group);
				if (vcellGroup != null)
				{
					List<HObject> children = vcellGroup.getMemberList();
					for (HObject c: children)
					{
						if (c instanceof Dataset)
						{
							Dataset dataset = (Dataset)c;
							String name = dataset.getName();
							List<Attribute> solAttrList = dataset.getMetadata();
							String domain = null;
							for (Attribute attr : solAttrList)
							{
								String attrName = attr.getName();
								if (attrName.equals("domain")) {
									Object obj = attr.getValue();
									domain = ((String[])obj)[0];
									break;
								}
							}
							ChomboMembraneVarData vcellSolution = new ChomboMembraneVarData(name, domain, (double[]) dataset.read());
							chomboMeshData.addMembraneVarData(vcellSolution);
						}
					}
				}
			}
			catch (Exception ex)
			{
				// it is ok if there is no vcell group
			}
		}
	}
}
