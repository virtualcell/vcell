package org.vcell.vis.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Coordinate;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.vis.core.Vect3D;
import org.vcell.vis.vcell.CartesianMesh;
import org.vcell.vis.vcell.ContourElement;
import org.vcell.vis.vcell.MembraneElement;
import org.vcell.vis.vcell.MembraneMeshMetrics;
import org.vcell.vis.vcell.MeshRegionInfo;
import org.vcell.vis.vcell.SubdomainInfo;

import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFormatException;
import cbit.vcell.math.VCML;

public class CartesianMeshFileReader {

	public final static String VERSION_1_0 = "1.0";		// origin, extent, sizeXYZ, 
	// membrane (membraneIndex, insideVolumeIndex, outsideVolumeIndex)
	
	public final static String VERSION_1_1 = "1.1";		// added: membrane connectivity 8/30/2000
	public final static String VERSION_1_2 = "1.2";		// added: Regions 07/02/2001

	public CartesianMesh readFromFiles(VCellSimFiles vcellSimFiles) throws IOException, MathException {	
		//
		// read meshFile and parse into 'mesh' object
		//
		BufferedReader meshReader = null;
		BufferedReader meshMetricsReader = null;
		try{
		meshReader = new BufferedReader(new FileReader(vcellSimFiles.cartesianMeshFile));
		CommentStringTokenizer meshST = new CommentStringTokenizer(meshReader);

		CommentStringTokenizer membraneMeshMetricsST = null;
		if(vcellSimFiles.meshMetricsFile != null){
			meshMetricsReader = new BufferedReader(new FileReader(vcellSimFiles.meshMetricsFile));
			membraneMeshMetricsST = new CommentStringTokenizer(meshMetricsReader);
		}

		MembraneMeshMetrics membraneMeshMetrics = null;
		SubdomainInfo subdomainInfo = null;
		if(membraneMeshMetricsST != null){
			membraneMeshMetrics = readMembraneMeshMetrics(membraneMeshMetricsST);
		}
		if (vcellSimFiles.subdomainFile != null) {
			subdomainInfo = SubdomainInfo.read(vcellSimFiles.subdomainFile);
		}
		CartesianMesh mesh = readCartesianMesh(meshST,membraneMeshMetrics,subdomainInfo);
		return mesh;
		}finally{
			if(meshReader != null){try{meshReader.close();}catch(Exception e){e.printStackTrace();}}
			if(meshMetricsReader != null){try{meshMetricsReader.close();}catch(Exception e){e.printStackTrace();}}
		}
	} 

	private MembraneMeshMetrics readMembraneMeshMetrics(CommentStringTokenizer tokens) throws MathException{
	
		MembraneMeshMetrics membraneMeshMetrics = new MembraneMeshMetrics();
		
		String token = null;
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.MembraneElements)){
			token = tokens.nextToken();
		}else{
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.CartesianMesh);
		}
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}
		
		token = tokens.nextToken();
		int numMembraneElements = 0;
		try{
			numMembraneElements = Integer.valueOf(token).intValue();
		}catch(NumberFormatException e){
			throw new MathFormatException("unexpected token "+token+" expecting MembraneElement count");
		}
		
		short[] regionIndex = new short[numMembraneElements];
		float[] areas = new float[numMembraneElements];
		float[][] normals = new float[numMembraneElements][3];
		float[][] centroids = new float[numMembraneElements][3];
	
		if(
			!(token = tokens.nextToken()).equals("Index") ||
			!(token = tokens.nextToken()).equals("RegionIndex") ||
			!(token = tokens.nextToken()).equals("X") ||
			!(token = tokens.nextToken()).equals("Y") ||
			!(token = tokens.nextToken()).equals("Z") ||
			!(token = tokens.nextToken()).equals("Area") ||
			!(token = tokens.nextToken()).equals("Nx") ||
			!(token = tokens.nextToken()).equals("Ny") ||
			!(token = tokens.nextToken()).equals("Nz")
		){
			throw new MathFormatException("unexpected MeshMetrics column description = "+token);
		}
		int counter = 0;
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}			
			if(counter >= numMembraneElements){
				throw new MathFormatException("Error parsing MembraneMeshMetrics values index="+counter+".  Expecting only "+numMembraneElements+" MembraneElements");
			}
			try {
				int index = Integer.valueOf(token).intValue();
				if(index != counter){
					throw new MathFormatException("unexpected token "+token+" expecting "+counter);
				}
				regionIndex[counter] = Short.parseShort(tokens.nextToken());
				//centroids
				centroids[counter][0] = Float.parseFloat(tokens.nextToken());
				centroids[counter][1] = Float.parseFloat(tokens.nextToken());
				centroids[counter][2] = Float.parseFloat(tokens.nextToken());
				//area
				areas[counter] = Float.parseFloat(tokens.nextToken());
				//normals
				normals[counter][0] = Float.parseFloat(tokens.nextToken());
				normals[counter][1] = Float.parseFloat(tokens.nextToken());
				normals[counter][2] = Float.parseFloat(tokens.nextToken());
				
			}catch (NumberFormatException e){
				throw new MathFormatException("Error parsing MembraneMeshMetrics values index="+counter+"  "+e.getMessage());
			}
			counter+= 1;
		}
	
		membraneMeshMetrics.regionIndexes = regionIndex;
		membraneMeshMetrics.areas = areas;
		membraneMeshMetrics.normals = normals;
		membraneMeshMetrics.centroids = centroids;
		
		return membraneMeshMetrics;	
	}

	private CartesianMesh readCartesianMesh(CommentStringTokenizer tokens, final MembraneMeshMetrics membraneMeshMetrics, final SubdomainInfo subdomainInfo) throws MathException {
		//
		// clear previous contents
		//
		MembraneElement[] membraneElements = null;
		String version = null;
		MeshRegionInfo meshRegionInfo = null;
		ISize size = null;
		Vect3D extent = null;
		Vect3D origin = null;
		ContourElement[] contourElements = null;

		//
		// read new stuff
		//
		String token = null;
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.Version)){
			//
			// read version number 
			//
			token = tokens.nextToken(); 
			version = token;
			token = tokens.nextToken();
		}
		if (token.equalsIgnoreCase(VCML.CartesianMesh)){
			token = tokens.nextToken();
		}else{
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.CartesianMesh);
		}
		//
		// only Version 1.1 and later supports membrane connectivity  (as of 8/30/2000)
		//
		boolean bConnectivity = false;
		if (version.equals(VERSION_1_1) || version.equals(VERSION_1_2)){
			bConnectivity = true;
		}
		//
		// only Version 1.2 and later supports Regions
		//
		boolean bRegions = false;
		if (version.equals(VERSION_1_2)){
			bRegions = true;
			meshRegionInfo = new MeshRegionInfo();
		}
		
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}			
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}			
			if (token.equalsIgnoreCase(VCML.Size)){
				int sx, sy, sz;
				try {
					token = tokens.nextToken();
					sx = Integer.valueOf(token).intValue();
					token = tokens.nextToken();
					sy = Integer.valueOf(token).intValue();
					token = tokens.nextToken();
					sz = Integer.valueOf(token).intValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("expected:  "+VCML.Size+" # # #");
				}
				size = new ISize(sx,sy,sz);
				continue;
			}			
			if (token.equalsIgnoreCase(VCML.Extent)){
				double ex, ey, ez;
				try {
					token = tokens.nextToken();
					ex = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					ey = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					ez = Double.valueOf(token).doubleValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("expected:  "+VCML.Extent+" # # #");
				}
				extent = new Vect3D(ex,ey,ez);
				continue;
			}			
			if (token.equalsIgnoreCase(VCML.Origin)){
				double ox, oy, oz;
				try {
					token = tokens.nextToken();
					ox = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					oy = Double.valueOf(token).doubleValue();
					token = tokens.nextToken();
					oz = Double.valueOf(token).doubleValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("expected:  "+VCML.Origin+" # # #");
				}
				origin = new Vect3D(ox,oy,oz);
				continue;
			}
			//
			//
			//
			if (token.equalsIgnoreCase(VCML.VolumeRegionsMapSubvolume)){
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)){
					throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numVolumeRegions = 0;
				try {
					numVolumeRegions = Integer.valueOf(token).intValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("unexpected token "+token+" expecting the VolumeRegionsMapSubvolume list length");
				}
				int checkCount = 0;
				while (tokens.hasMoreTokens()){
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)){
						break;
					}
					try{
						int volRegionID = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int subvolumeID = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						double volume = Double.valueOf(token).doubleValue();
						
						String subdomainName = null;
						if (subdomainInfo != null) {
							subdomainName = subdomainInfo.getCompartmentSubdomainName(subvolumeID);
						}
						meshRegionInfo.mapVolumeRegionToSubvolume(volRegionID,subvolumeID,volume, subdomainName);
					}catch (NumberFormatException e){
						throw new MathFormatException("expected:  # # #");
					}
					checkCount+= 1;
				}
				if(checkCount != numVolumeRegions){
					throw new MathFormatException("CartesianMesh.read->VolumeRegionsMapSubvolume: read "+checkCount+" VolRegions but was expecting "+numVolumeRegions);
				}
				continue;
			}	
			if (token.equalsIgnoreCase(VCML.MembraneRegionsMapVolumeRegion)){
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)){
					throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numMembraneRegions = 0;
				try {
					numMembraneRegions = Integer.valueOf(token).intValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("unexpected token "+token+" expecting the MembraneRegionsMapVolumeRegion list length");
				}
				int checkCount = 0;
				while (tokens.hasMoreTokens()){
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)){
						break;
					}
					try{
						int memRegionID = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int volRegionIn = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int volRegionOut = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						double surface = Double.valueOf(token).doubleValue();
						meshRegionInfo.mapMembraneRegionToVolumeRegion(memRegionID,volRegionIn,volRegionOut,surface);
					}catch (NumberFormatException e){
						throw new MathFormatException("expected:  # # #");
					}
					checkCount+= 1;
				}
				if(checkCount != numMembraneRegions){
					throw new MathFormatException("CartesianMesh.read->MembraneRegionsMapVolumeRegion: read "+checkCount+" MembraneRegions but was expecting "+numMembraneRegions);
				}
				continue;
			}	
			if (token.equalsIgnoreCase(VCML.VolumeElementsMapVolumeRegion)){
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)){
					throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numVolumeElements = 0;
				try {
					numVolumeElements = Integer.valueOf(token).intValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("unexpected token "+token+" expecting the VolumeElementsMapVolumeRegion list length");
				}
				token  = tokens.nextToken();
				boolean bCompressed = token.equalsIgnoreCase("Compressed");
				if(!bCompressed){
					if(!token.equalsIgnoreCase("UnCompressed")){
						throw new MathFormatException("unexpected token "+token+" expecting Compress or UnCompress");
					}
				}
				byte[] volumeElementMap = new byte[numVolumeElements];
				int checkCount = 0;
				if(bCompressed){
					//Get HEX encoded bytes of the compressed VolumeElements-RegionID Map
					StringBuffer hexOfCompressed = new StringBuffer();
					while (tokens.hasMoreTokens()){
						token = tokens.nextToken();
						if (token.equalsIgnoreCase(VCML.EndBlock)){
							break;
						}
						hexOfCompressed.append(token);
					}
					//Un-HEX the compressed data
					byte[] compressedData = Hex.toBytes(hexOfCompressed.toString());
					try{
						meshRegionInfo.setCompressedVolumeElementMapVolumeRegion(compressedData, numVolumeElements);
					}catch(IOException e){
						throw new MathFormatException("CartesianMesh.read->VolumeElementsMapVolumeRegion "+e.toString());
					}
					checkCount = meshRegionInfo.getUncompressedVolumeElementMapVolumeRegionLength();
				}else{
					while (tokens.hasMoreTokens()){
						token = tokens.nextToken();
						if (token.equalsIgnoreCase(VCML.EndBlock)){
							break;
						}
						try{
							int volumeRegionID = Integer.valueOf(token).intValue();
							volumeElementMap[checkCount] = (byte)volumeRegionID;
						}catch (NumberFormatException e){
							throw new MathFormatException("expected:  # # #");
						}
						checkCount+= 1;
					}
				}
				if(checkCount != numVolumeElements && checkCount != 2 * numVolumeElements){
					throw new MathFormatException("CartesianMesh.read->VolumeElementsMapVolumeRegion: read "+checkCount+" VolumeElements but was expecting "+numVolumeElements);
				}
				continue;
			}
			//
			//
			//	
			HashMap<Integer, Integer> volumeRegionMapSubvolume = getVolumeRegionMapSubvolume(meshRegionInfo);
			if (token.equalsIgnoreCase(VCML.MembraneElements)){
				//
				// read '{'
				//
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)){
					throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numMemElements = 0;
				try {
					numMemElements = Integer.valueOf(token).intValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("unexpected token "+token+" expecting the membraneElement list length");
				}
				//
				// read list of the following format:
				//
				//		memIndex insideVolIndex outsideVolIndex
				//
				membraneElements = new MembraneElement[numMemElements];
				int index = 0;
				int[] membraneElementMapMembraneRegion = null;
				if(bRegions){
					membraneElementMapMembraneRegion = new int[numMemElements];
					meshRegionInfo.mapMembraneElementsToMembraneRegions(membraneElementMapMembraneRegion);
				}
				//
				// loop until read a "}"
				//
				while (tokens.hasMoreTokens()){
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)){
						break;
					}
					int memIndex = -1;
					int insideIndex = -1;
					int outsideIndex = -1;
					try {
						//
						// read first three tokens of a membrane element
						//
						//     membraneIndex   insideIndex    outsideIndex
						//
						memIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						insideIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						outsideIndex = Integer.valueOf(token).intValue();

						if (subdomainInfo != null) {
							int insideRegionIndex = meshRegionInfo.getVolumeElementMapVolumeRegion(insideIndex);
							int outsideRegionIndex = meshRegionInfo.getVolumeElementMapVolumeRegion(outsideIndex);
							int insideSubVolumeHandle = volumeRegionMapSubvolume.get(insideRegionIndex);
							int outsideSubVolumeHandle = volumeRegionMapSubvolume.get(outsideRegionIndex);
							int realInsideSubVolumeHandle = subdomainInfo.getInside(insideSubVolumeHandle, outsideSubVolumeHandle);
							
							if (realInsideSubVolumeHandle != insideSubVolumeHandle) {
								int temp = insideIndex;
								insideIndex = outsideIndex;
								outsideIndex = temp;
							}
						}
					}catch (NumberFormatException e){
						throw new MathFormatException("expected:  # # #");
					}
					
					MembraneElement me = null;
					//
					// grab connectivity if enabled (additional four tokens)
					//
					//     memNeighbor1  memNeighbor2  memNeighbor3  memNeighbor4
					//
					//	where memNeighborX = -1 for missing connections.   
					//
					if (bConnectivity){
						try {
							token = tokens.nextToken();
							int neighbor1 = Integer.valueOf(token).intValue();
							token = tokens.nextToken();
							int neighbor2 = Integer.valueOf(token).intValue();
							token = tokens.nextToken();
							int neighbor3 = Integer.valueOf(token).intValue();
							token = tokens.nextToken();
							int neighbor4 = Integer.valueOf(token).intValue();
							//
							if(bRegions){
								token = tokens.nextToken();
								int regionID = Integer.valueOf(token).intValue();
								membraneElementMapMembraneRegion[memIndex] = regionID;
							}
							if(membraneMeshMetrics == null){
								me = new MembraneElement(	memIndex,insideIndex,outsideIndex,
														neighbor1,neighbor2,neighbor3,neighbor4,MembraneElement.AREA_UNDEFINED,0,0,0,0,0,0);
							}else{
								me = new MembraneElement(	memIndex,insideIndex,outsideIndex,
														neighbor1,neighbor2,neighbor3,neighbor4,
														membraneMeshMetrics.areas[memIndex],
														membraneMeshMetrics.normals[memIndex][0],
														membraneMeshMetrics.normals[memIndex][1],
														membraneMeshMetrics.normals[memIndex][2],
														membraneMeshMetrics.centroids[memIndex][0],
														membraneMeshMetrics.centroids[memIndex][1],
														membraneMeshMetrics.centroids[memIndex][2]);
							}
						}catch (NumberFormatException e){
							throw new MathFormatException("expected:  # # # # # # #");
						}
					}else{
						me = new MembraneElement(memIndex,insideIndex,outsideIndex);
					}
					
					membraneElements[index] = me;
					index++;
				}
				continue;
			}			
			if (token.equalsIgnoreCase(VCML.ContourElements)){
				//
				// read '{'
				//
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)){
					throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
				}
				token = tokens.nextToken();
				int numContourElements = 0;
				try {
					numContourElements = Integer.valueOf(token).intValue();
				}catch (NumberFormatException e){
					throw new MathFormatException("unexpected token "+token+" expecting the contourElement list length");
				}
				//
				// read list of the following format:
				//
				//		contourIndex volumeIndex beginCoord endCoord prevIndex nextIndex
				//
				contourElements = new ContourElement[numContourElements];
				int index = 0;
				//
				// loop until read a "}"
				//
				while (tokens.hasMoreTokens()){
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)){
						break;
					}
					ContourElement ce = null;
					try {
						//
						// read first two tokens of a contour element
						//
						//     contourIndex volumeIndex
						//
						int contourIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int volumeIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();

						//
						// read beginCoord endCoord
						//
						double beginX = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double beginY = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double beginZ = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();

						double endX = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double endY = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();
						double endZ = Double.valueOf(token).doubleValue();
						token = tokens.nextToken();

						Coordinate begin = new Coordinate(beginX,beginY,beginZ);
						Coordinate end = new Coordinate(endX,endY,endZ);

						//
						// read last two tokens of a contour element
						//
						//     prevContourIndex nextContourIndex
						//
						int prevContourIndex = Integer.valueOf(token).intValue();
						token = tokens.nextToken();
						int nextContourIndex = Integer.valueOf(token).intValue();

						ce = new ContourElement(contourIndex, volumeIndex, begin, end, prevContourIndex, nextContourIndex);

					}catch (NumberFormatException e){
						throw new MathFormatException("expected:  %d %d   %f %f %f    %f %f %f   %d %d");
					}
					
					contourElements[index] = ce;
					
					index++;
				}
				continue;
			}
			throw new MathFormatException("unexpected identifier "+token);
		}
		int dimension = getGeometryDimension(size);
		switch (dimension) {
			case 1 : {
				if (extent.y != 1 || extent.z != 1) {
					System.out.println("Extent "+extent.toString()+" for a 1-D mesh truncated to 1 for y and z");
					extent = new Vect3D(extent.x, 1.0, 1.0);
				}
				break;
			}
			case 2 : {
				if (extent.z != 1) {
					System.out.println("Extent "+extent.toString()+" for a 2-D mesh truncated to 1 for z");
					extent = new Vect3D(extent.x, extent.y, 1.0);
				}
				break;			
			}
		}
		CartesianMesh mesh = new CartesianMesh(version,subdomainInfo,membraneElements,contourElements,meshRegionInfo,size,extent,origin,dimension);
		return mesh;
	}
	
	private int getGeometryDimension(ISize size) {
		// Get dimension of geometry using mesh variables to use appropriate resampling algorithm.
		int dimension = 0;
		
		if ( (size.getX() > 1) && (size.getY() > 1) && (size.getZ() > 1) ) {
			dimension = 3;
		} else if ( ( (size.getX() > 1) && (size.getY() > 1) && (size.getZ() == 1) ) ||
					( (size.getX() > 1) && (size.getY() == 1) && (size.getZ() > 1) ) ||
					( (size.getX() == 1) && (size.getY() > 1) && (size.getZ() > 1) )  ) {
			dimension = 2;
		} else if ( ( (size.getX() > 1) && (size.getY() == 1) && (size.getZ() == 1) ) ||
					( (size.getX() == 1) && (size.getY() > 1) && (size.getZ() == 1) ) ||
					( (size.getX() == 1) && (size.getY() == 1) && (size.getZ() > 1) )  ) {
			dimension = 1;
		}
					
		return dimension;
	}

	private HashMap<Integer, Integer> getVolumeRegionMapSubvolume(MeshRegionInfo meshRegionInfo){
		Vector<MeshRegionInfo.VolumeRegionMapSubvolume> volRegionMapSubVolV = meshRegionInfo.getVolumeRegionMapSubvolume();
		HashMap<Integer, Integer> volregMapSubVHashMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < volRegionMapSubVolV.size(); i++) {
			volregMapSubVHashMap.put(volRegionMapSubVolV.elementAt(i).volumeRegionID, volRegionMapSubVolV.elementAt(i).subvolumeID);
		}
		return volregMapSubVHashMap;
	}

}
