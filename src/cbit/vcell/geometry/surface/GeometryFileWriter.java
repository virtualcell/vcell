package cbit.vcell.geometry.surface;
import java.io.Writer;
import cbit.vcell.geometry.RegionImage;
import cbit.util.ISize;
import java.util.zip.DeflaterOutputStream;
import java.io.ByteArrayOutputStream;
import cbit.vcell.geometry.Geometry;
/**
 * Insert the type's description here.
 * Creation date: (7/19/2004 10:52:10 AM)
 * @author: Jim Schaff
 */
public class GeometryFileWriter {
/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 10:54:30 AM)
 * @param geometrySurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
public static void write(Geometry geometry, ISize newSize, java.io.Writer writer) throws Exception {

	long tm = System.currentTimeMillis();

	// clone geometry to isolate sampling from model (setVolumeSampleSize());
	
	Geometry newGeometry = (Geometry)cbit.util.BeanUtils.cloneSerializable(geometry);
	
	GeometrySurfaceDescription geoSurfaceDesc = geometry.getGeometrySurfaceDescription();
	ISize oldSize = geoSurfaceDesc.getVolumeSampleSize();
	if (oldSize.equals(newSize)) {
		System.out.println("Sample size is the same as old size: " + oldSize);
	} else {
		System.out.println("Old sample size: " + oldSize + ", New Sample size: " + newSize);
		geoSurfaceDesc.setVolumeSampleSize(newSize);
		geoSurfaceDesc.updateAll();
		System.out.println("Geometry updateAll takes " + (System.currentTimeMillis() - tm)/1000.0 + " sec");
	}
	RegionImage regionImage = geoSurfaceDesc.getRegionImage();
	SurfaceCollection surfaceCollection = geoSurfaceDesc.getSurfaceCollection();
	GeometricRegion geometricRegions[] = geoSurfaceDesc.getGeometricRegions();
	
	/*
	long tm = System.currentTimeMillis();

	Geometry newGeometry = new Geometry(geometry);
	
	GeometrySurfaceDescription geoSurfaceDesc = newGeometry.getGeometrySurfaceDescription();	
	geoSurfaceDesc.setVolumeSampleSize(newSize);
	
	
	cbit.vcell.geometry.GeometrySpec geometrySpec = newGeometry.getGeometrySpec();
	
	RegionImage regionImage = GeometrySurfaceUtils.getUpdatedRegionImage(geoSurfaceDesc);

	// make surfaces
	SurfaceGenerator surfaceGenerator = new SurfaceGenerator(new cbit.vcell.server.StdoutSessionLog("suface generator"));
	cbit.util.Extent extent = geometrySpec.getExtent();
	cbit.util.Origin origin = geometrySpec.getOrigin();
	SurfaceCollection surfaceCollection = surfaceGenerator.generateSurface(regionImage, 3, extent, origin);
	//
	// smooth surfaces
	//	
	int numPoints = 0;
	if (surfaceCollection != null) {
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
			numPoints += surfaceCollection.getSurfaces(i).getPolygonCount();
		}
	}

	double lambda = 0.7;
	double L  = 0.0;	
	if (newGeometry.getDimension() == 2) {
		L = numPoints;		
	} else if (newGeometry.getDimension() == 3)  {
		L = Math.sqrt(numPoints);		
	}
	double epsilon = 1/Math.sqrt(L);
	double delta = 2 * Math.PI * Math.PI * lambda * lambda / L;
	
	double mu = - lambda - delta;		
	int N = (int)(4 * lambda * lambda * epsilon / (delta * delta));
	System.out.println("Smoothing parameters: epsilon=" + epsilon + ", delta=" + delta + ", lambda=" + lambda +", mu=" + mu + ", N=" + N);
	
	
	TaubinSmoothing taubinSmoothing = new TaubinSmoothingWrong();
	TaubinSmoothingSpecification taubinSpec = new TaubinSmoothingSpecification(lambda, mu, N, null); //TaubinSmoothingSpecification.getInstance(geoSurfaceDesc.getFilterCutoffFrequency().doubleValue());
	taubinSmoothing.smooth(surfaceCollection,taubinSpec);			

	//
	// parse regionImage into VolumeGeometricRegions and SurfaceCollection into SurfaceGeometricRegions
	//
	GeometricRegion geometricRegions[] = GeometrySurfaceUtils.getUpdatedGeometricRegions(geoSurfaceDesc, regionImage, surfaceCollection);		
	System.out.println("Geometry updateAll takes " + (System.currentTimeMillis() - tm)/1000.0 + " sec");
	*/

	write(writer, newGeometry, newSize, regionImage, surfaceCollection, geometricRegions);
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 10:54:30 AM)
 * @param geometrySurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
private static void write(Writer writer, Geometry geometry, ISize volumeSampleSize, RegionImage regionImage, SurfaceCollection surfaceCollection, GeometricRegion[] geometricRegions) throws Exception {
	//
	// "name" name
	// "dimension" dimension
	// "extent" extentx extenty extentz
	// "origin" originx originy originz
	// "volumeRegions" num
	//    name totalVolume featureHandle
	// "membraneRegions" num
	//    name totalArea volumeRegionIndex1 volumeRegionIndex2
	// "volumeSamples" numX, numY, numZ
	//    uncompressed regionIndexs for each volume element
	//    compressed regionIndexs for each volume element
	// "nodes" num
	//    nodeIndex x y z
	// "cells" num
	//    cellIndex patchIndex node1 node2 node3 node4
	// "celldata"
	//    insideVolumeIndex outsideVolumeIndex area normalx normaly normalz
	//
	//
	//When we are writing volume regions, we sort regions so that ID is equal to index
	//	
	writer.write("name "+geometry.getName()+"\n");
	writer.write("dimension "+geometry.getDimension()+"\n");
	cbit.util.Extent extent = geometry.getExtent();
	cbit.util.Origin origin = geometry.getOrigin();
	switch (geometry.getDimension()) {
		case 1:			
			writer.write("size "+extent.getX()+"\n");
			writer.write("origin "+origin.getX()+"\n");
			break;
		case 2:
			writer.write("size "+extent.getX()+" "+extent.getY()+"\n");
			writer.write("origin "+origin.getX()+" "+origin.getY()+"\n");
			break;
		case 3:
			writer.write("size "+extent.getX()+" "+extent.getY()+" "+extent.getZ()+"\n");
			writer.write("origin "+origin.getX()+" "+origin.getY()+" "+origin.getZ()+"\n");
			break;
	}	

	int numVolumeRegions = 0;
	int numMembraneRegions = 0;	
	java.util.Vector volRegionList = new java.util.Vector();
	if (geometricRegions != null) {	
		for (int i = 0; i < geometricRegions.length; i++){
			if (geometricRegions[i] instanceof VolumeGeometricRegion){
				numVolumeRegions++;
				volRegionList.add(geometricRegions[i]);
			}else if (geometricRegions[i] instanceof SurfaceGeometricRegion){
				numMembraneRegions++;
			}
		}
	}
	
	//
	// get ordered array of volume regions (where "id" == index into array)... fail if impossible
	//
	java.util.Collections.sort(volRegionList,new java.util.Comparator() {
		public int compare(Object obj1, Object obj2){
			VolumeGeometricRegion reg1 = (VolumeGeometricRegion)obj1;
			VolumeGeometricRegion reg2 = (VolumeGeometricRegion)obj2;
			if (reg1.getRegionID()<reg2.getRegionID()){
				return -1;
			}else if (reg1.getRegionID()>reg2.getRegionID()){
				return 1;
			}else{
				return 0;
			}
		}
		public boolean equals(Object obj){
			return this==obj;
		}
	});

	VolumeGeometricRegion volRegions[] = (VolumeGeometricRegion[])cbit.util.BeanUtils.getArray(volRegionList,VolumeGeometricRegion.class);
	
	writer.write("volumeRegions "+numVolumeRegions +"\n");
	for (int i = 0; i < volRegions.length; i++){
		if (volRegions[i].getRegionID() != i) {
			throw new RuntimeException("Region ID != Region Index, they must be the same!");
		}
		writer.write(volRegions[i].getName()+" "+volRegions[i].getSize()+" "+volRegions[i].getSubVolume().getHandle()+"\n");
	}	

	writer.write("membraneRegions "+numMembraneRegions+"\n");
	if (geometricRegions != null) {
		for (int i = 0; i < geometricRegions.length; i++){
			if (geometricRegions[i] instanceof SurfaceGeometricRegion){
				SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion)geometricRegions[i];
				GeometricRegion neighbors[] = surfaceRegion.getAdjacentGeometricRegions();
				VolumeGeometricRegion insideRegion = (VolumeGeometricRegion)neighbors[0];
				VolumeGeometricRegion outsideRegion = (VolumeGeometricRegion)neighbors[1];
				writer.write(surfaceRegion.getName()+" "+surfaceRegion.getSize()+" "+insideRegion.getRegionID()+" "+outsideRegion.getRegionID()+"\n");
			}
		}
	}
	//
	// write volume samples
	//
	switch (geometry.getDimension()) {		
		case 1:
			writer.write("volumeSamples "+volumeSampleSize.getX()+"\n");
			break;
		case 2:
			writer.write("volumeSamples "+volumeSampleSize.getX()+" "+volumeSampleSize.getY()+"\n");
			break;
		case 3:
			writer.write("volumeSamples "+volumeSampleSize.getX()+" "+volumeSampleSize.getY()+" "+volumeSampleSize.getZ()+"\n");
			break;
	}

	byte[] uncompressedRegionIDs = null;
	
	// regionImage
	if (regionImage != null) {
		if (regionImage.getNumRegions()>255){
			throw new RuntimeException("cannot process a geometry with nore than 255 volume regions");
		}
		uncompressedRegionIDs = new byte[regionImage.getNumX()*regionImage.getNumY()*regionImage.getNumZ()];
		for (int i = 0; i < uncompressedRegionIDs.length; i++){
			uncompressedRegionIDs[i] = (byte)regionImage.getRegionInfoFromOffset(i).getRegionIndex();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(bos);
		//DeflaterOutputStream dos = new DeflaterOutputStream(bos,new Deflater(5,false));
		dos.write(uncompressedRegionIDs,0,uncompressedRegionIDs.length);
		dos.close();
		byte[] compressedRegionIDs = bos.toByteArray();
		//writer.write(cbit.util.Hex.toString(uncompressedRegionIDs)+"\n");
		writer.write(cbit.util.Hex.toString(compressedRegionIDs)+"\n");
	} else {
		writer.write("\n");
	}
		
	//
	// write surfaces
	//
	//SurfaceCollection surfaceCollection = geoSurfaceDesc.getSurfaceCollection();
	/*
	Node nodes[] = surfaceCollection.getNodes();
	int numNodes = nodes.length;
	writer.write("nodes "+numNodes+"\n");
	for (int i = 0; i < nodes.length; i++){
		writer.write(nodes[i].getGlobalIndex()+" "+nodes[i].getX()+" "+nodes[i].getY()+" "+nodes[i].getZ()+"\n");
	}
	*/
	//
	// print the "Cells" (polygons) for each surface (each surface has it's own material id).
	//
	int numCells = 0;
	if (surfaceCollection != null) {
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
			numCells += surfaceCollection.getSurfaces(i).getPolygonCount();
		}
	}
	writer.write("cells "+numCells+"\n");
	/*
	int cellID = 0;
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
		Surface surface = surfaceCollection.getSurfaces(i);
		for (int j = 0; j < surface.getPolygonCount(); j++){
			Polygon polygon = surface.getPolygons(j);
			int node0Index = polygon.getNodes(0).getGlobalIndex();
			int node1Index = polygon.getNodes(1).getGlobalIndex();
			int node2Index = polygon.getNodes(2).getGlobalIndex();
			int node3Index = polygon.getNodes(3).getGlobalIndex();
			writer.write(cellID+" "+i+" "+node0Index+" "+node1Index+" "+node2Index+" "+node3Index+"\n");
			cellID++;
		}
	}
	*/
	// "celldata"
	//    insideVolumeIndex outsideVolumeIndex area normalx normaly normalz
	//
	int cellID = 0;
	int dimension = geometry.getDimension();
	
	double correctCoeff = 1;

	if (dimension == 1) {
		correctCoeff = extent.getY() * extent.getZ();
	} else if (dimension == 2) {
		correctCoeff = extent.getZ();
	}	
	if (surfaceCollection != null) {
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
			Surface surface = surfaceCollection.getSurfaces(i);
			int region1Outside = 0;
			int region1Inside = 0;
			for (int j = 0; j < surface.getPolygonCount(); j++){
				Quadrilateral polygon = (Quadrilateral)surface.getPolygons(j);
				Node[] node = polygon.getNodes();

				cbit.vcell.render.Vect3d elementCoord = new cbit.vcell.render.Vect3d();
				int nodesOnBoundary = 0;
				for (int k = 0; k < node.length; k ++) {
					if (!node[k].getMoveX() || (dimension > 1 && !node[k].getMoveY()) || (dimension == 3 && !node[k].getMoveZ())) {
						nodesOnBoundary ++;
					}
				}

				if (nodesOnBoundary == 0) {
					for (int k = 0; k < node.length; k ++) {
						elementCoord.add(new cbit.vcell.render.Vect3d(node[k].getX(), node[k].getY(), node[k].getZ()));
					}
					elementCoord.scale(0.25);
				} else if (nodesOnBoundary == 2) {
					for (int k = 0; k < node.length; k ++) {
						if (!node[k].getMoveX() || !node[k].getMoveY() || !node[k].getMoveZ()) {						
							elementCoord.add(new cbit.vcell.render.Vect3d(node[k].getX(), node[k].getY(), node[k].getZ()));							
						}						
					}
					elementCoord.scale(0.5);
				} else if (nodesOnBoundary == 3) {
					for (int k = 0; k < node.length; k ++) {
						if (!node[k].getMoveX() && !node[k].getMoveY() || !node[k].getMoveY() && !node[k].getMoveZ() || !node[k].getMoveX() && !node[k].getMoveZ()) {						
							elementCoord.set(node[k].getX(), node[k].getY(), node[k].getZ());
						}						
					}
				} else {
					throw new RuntimeException("Unexcepted number of nodes on boundary for a polygon: " + nodesOnBoundary);
				}
	
				cbit.vcell.render.Vect3d unitNormal = new cbit.vcell.render.Vect3d();
				polygon.getUnitNormal(unitNormal);
				
				int volNeighbor1Region = uncompressedRegionIDs[polygon.getVolIndexNeighbor1()];
				int volNeighbor2Region = uncompressedRegionIDs[polygon.getVolIndexNeighbor2()];

				if (surface.getExteriorRegionIndex() == volNeighbor1Region && surface.getInteriorRegionIndex() == volNeighbor2Region) {
					region1Outside ++;
				}
				if (surface.getExteriorRegionIndex() == volNeighbor2Region && surface.getInteriorRegionIndex() == volNeighbor1Region) {
					region1Inside ++;
				}
				
				writer.write(cellID+" "+polygon.getVolIndexNeighbor1()+" "+polygon.getVolIndexNeighbor2()+" "+polygon.getArea()/correctCoeff + " " + elementCoord.getX()+ " " + elementCoord.getY() + " " + elementCoord.getZ() + " " + unitNormal.getX()+ " " + unitNormal.getY() + " " + unitNormal.getZ() + "\n");
				cellID++;
			}
			if (region1Inside != surface.getPolygonCount() && region1Outside != surface.getPolygonCount()) {
				throw new RuntimeException("Volume neighbor regions not consistent: [total, inside, outside]=" + surface.getPolygonCount() + "," + region1Inside + "," + region1Outside + "]");
			}
		}
	}	
}
}