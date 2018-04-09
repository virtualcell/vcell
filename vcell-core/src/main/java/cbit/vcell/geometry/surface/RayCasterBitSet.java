/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Tree;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactory;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.parser.ExpressionException;

public class RayCasterBitSet {
	private static Logger lg = LogManager.getLogger(RayCasterBitSet.class);
	
	public static Geometry createGeometryFromSTL(GeometryThumbnailImageFactory geometryThumbnailImageFactory, File stlFile, int numSamples) throws ImageException, PropertyVetoException, GeometryException, ExpressionException, IOException{
		SurfaceCollection surfaceCollection = StlReader.readStl(stlFile);
		
		Node[] nodes = surfaceCollection.getNodes();
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (Node node : nodes){
			double nx = node.getX();
			double ny = node.getY();
			double nz = node.getZ();
			minX = Math.min(minX,nx);
			maxX = Math.max(maxX,nx);
			minY = Math.min(minY,ny);
			maxY = Math.max(maxY,ny);
			minZ = Math.min(minZ,nz);
			maxZ = Math.max(maxZ,nz);
		}
		Extent extent = new Extent((maxX-minX)*1.4, (maxY-minY)*1.4, (maxZ-minZ)*1.4);
		Origin origin = new Origin(minX - 0.2*extent.getX(),minY - 0.2*extent.getY(),minZ - 0.2*extent.getZ());
		
		ISize sampleSize = GeometrySpec.calulateResetSamplingSize(3, extent, numSamples);
		
		Geometry geometry = createGeometry(geometryThumbnailImageFactory, surfaceCollection, origin, extent, sampleSize);
		
		return geometry;
	}

	public static VolumeSamplesBitSet volumeSampleSurface(SurfaceCollection surfaceCollection, ISize sampleSize, Origin origin, Extent extent, boolean bCellCentered) throws ImageException {
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		double samplesX[] = new double[numX];
		double samplesY[] = new double[numY];
		double samplesZ[] = new double[numZ];
		long t1 = 0, t2 = 0, t3 = 0, t4 = 0; //debug
		
		sampleXYZCoordinates(sampleSize, origin, extent, samplesX, samplesY, samplesZ, bCellCentered);

		if (lg.isDebugEnabled()) {
			t1 = System.currentTimeMillis();
		}

		if (surfaceCollection.getSurfaceCount()>32){
			throw new RuntimeException("Number of masks exceeds max allowed. The image contains too many distinct geometric objects. " +
					"Alternately, sampling resolution is too coarse and results in a single object becoming separated in multiple fragments. " +
					"Try using a finer mesh.");
			}
		setSurfaceMasks(surfaceCollection);
		
		RayCastResults rayCastResults = rayCastXYZ(surfaceCollection, samplesX, samplesY, samplesZ);

		if (lg.isDebugEnabled()) {
			t2 = System.currentTimeMillis();
		}

		VolumeSamplesBitSet volumeSamples = new VolumeSamplesBitSet(numX*numY*numZ);
		
		//
		// volume sample in Z direction (index = i + j*numX)
		//
		{
		int xyIndex = 0;
		int numXY = numX*numY;
		HitList[] hitListsZ = rayCastResults.getHitListsXY();
		for (int j = 0; j < numY; j++){
			for (int i = 0; i < numX; i++){
				hitListsZ[xyIndex].sampleRegionIDs(samplesZ, volumeSamples, xyIndex, numXY);
				xyIndex++;
			}
		}
		}
		//
		// volume sample in Y direction (index = i + k*numX)
		//
		{
		int xzIndex = 0;
		int numXY = numX*numY;
		HitList[] hitListsY = rayCastResults.getHitListsXZ();
		for (int k = 0; k < numZ; k++){
			for (int i = 0; i < numX; i++){
				hitListsY[xzIndex].sampleRegionIDs(samplesY, volumeSamples, i+k*numXY, numX);
				xzIndex++;
			}
		}
		}
		//
		// volume sample in X direction (index = j + k*numY)
		//
		{
		int yzIndex = 0;
		int numXY = numX*numY;
		HitList[] hitListsX = rayCastResults.getHitListsYZ();
		for (int k = 0; k < numZ; k++){
			for (int j = 0; j < numY; j++){
				hitListsX[yzIndex].sampleRegionIDs(samplesX, volumeSamples, j*numX+k*numXY, 1);
				yzIndex++;
			}
		}
		}
		if (lg.isDebugEnabled()) {
			t3 = System.currentTimeMillis();
		}
		
		int count = 0;
		while (volumeSamples.hasZeros() && count < 100){
			//
			// go through "empty" hitlists and set zero elements to nonzero (until no more zero left)
			//
			{
			int xyIndex = 0;
			int numXY = numX*numY;
			HitList[] hitListsZ = rayCastResults.getHitListsXY();
			for (int j = 0; j < numY; j++){
				for (int i = 0; i < numX; i++){
					if(hitListsZ[xyIndex].isEmpty())
					{
						volumeSamples.fillEmpty(numZ, xyIndex, numXY);
					}
					xyIndex++;
				}
			}
			}
			//
			// volume sample in Y direction (index = i + k*numX)
			//
			{
			int xzIndex = 0;
			int numXY = numX*numY;
			HitList[] hitListsY = rayCastResults.getHitListsXZ();
			for (int k = 0; k < numZ; k++){
				for (int i = 0; i < numX; i++){
					if(hitListsY[xzIndex].isEmpty())
					{
						volumeSamples.fillEmpty(numY, i+k*numXY, numX);
					}
					xzIndex++;
				}
			}
			}
			//
			// volume sample in X direction (index = j + k*numY)
			//
			{
			int yzIndex = 0;
			int numXY = numX*numY;
			HitList[] hitListsX = rayCastResults.getHitListsYZ();
			for (int k = 0; k < numZ; k++){
				for (int j = 0; j < numY; j++){
					if(hitListsX[yzIndex].isEmpty())
					{
						volumeSamples.fillEmpty(numX, j*numX+k*numXY, 1);
					}
					yzIndex++;
				}
			}
			}
			count++;
		}		
		if (lg.isDebugEnabled()) {
			t4 = System.currentTimeMillis();
			lg.debug("\n\n\nray trace triangles from 3 orthogonal directions ("+(t2-t1)+"ms), volume sample hit lists ("+(t3-t2)+"ms), "+count+" passes resolving zeros ("+(t4-t3)+"ms)\n\n\n");
		}
		return volumeSamples;
	}

	private static void setSurfaceMasks(SurfaceCollection surfaceCollection) {
		long mask = 1;
		for (int i=0;i<surfaceCollection.getSurfaceCount();i++){
			surfaceCollection.getSurfaces(i).setInteriorMask(mask);
			mask = mask<<1;
			surfaceCollection.getSurfaces(i).setExteriorMask(mask);
			mask = mask<<1;
		}
	}

	private static void sampleXYZCoordinates(ISize sampleSize, Origin origin, Extent extent, double[] samplesX, double[] samplesY, double[] samplesZ, boolean bCellCentered) {
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		double ox = origin.getX();
		double oy = origin.getY();
		double oz = origin.getZ();
		if (bCellCentered){
			for (int i=0;i<numX;i++){
				samplesX[i] = ox + ((i+0.5)*extent.getX()/(numX));
			}
			for (int i=0;i<numY;i++){
				samplesY[i] = oy + ((i+0.5)*extent.getY()/(numY));
			}
			for (int i=0;i<numZ;i++){
				samplesZ[i] = oz + ((i+0.5)*extent.getZ()/(numZ));
			}
		}else{
			for (int i=0;i<numX;i++){
				samplesX[i] = ox + (i*extent.getX()/(numX-1));
			}
			for (int i=0;i<numY;i++){
				samplesY[i] = oy + (i*extent.getY()/(numY-1));
			}
			for (int i=0;i<numZ;i++){
				samplesZ[i] = oz + (i*extent.getZ()/(numZ-1));
			}
			//
			// instead of sampling right on the "edge" of the bounding box formed by origin/extent ... come in a little to avoid incorrect sampling.
			//
			double epsilonX = 1e-8 * extent.getX()/(numX);
			samplesX[0] 				+= epsilonX;
			samplesX[samplesX.length-1] -= epsilonX;
			
			double epsilonY = 1e-8 * extent.getY()/(numY);
			samplesY[0] 				+= epsilonY;
			samplesY[samplesY.length-1] -= epsilonY;
			
			double epsilonZ = 1e-8 * extent.getZ()/(numZ);
			samplesZ[0] 				+= epsilonZ;
			samplesZ[samplesZ.length-1] -= epsilonZ;
		}
	}

	public static VCImage sampleGeometry(Geometry geometry, ISize sampleSize, boolean bCellCentered) throws ImageException, PropertyVetoException, GeometryException, ExpressionException{
//		int numX = sampleSize.getX();
//		int numY = sampleSize.getY();
//		int numZ = sampleSize.getZ();
//		
//		Origin origin = geometry.getOrigin();
//		Extent extent = geometry.getExtent();
//		
//		VolumeSamplesBitSet volumeSamples = volumeSampleSurface(geometry.getGeometrySurfaceDescription().getSurfaceCollection(), sampleSize, origin, extent, bCellCentered);
//				
//		// for each mask bit, find union of masks which contain that bit ... iterate until no change.
//		HashSet<Long> uniqueMasks = new HashSet<Long>();
//		HashMap<Long,BitSet> incidentSurfaceBitSets = volumeSamples.getIncidentSurfaceBitSets();
//		for (Long mask : incidentSurfaceBitSets.keySet()){
//			if (!uniqueMasks.contains(mask)){
//				uniqueMasks.add(mask);
//System.out.println("+++++++++++++++++++++++++++++++++++++ unique mask : "+Long.toBinaryString(mask));
//			}
//		}
//		ArrayList<Long> consensusMaskArray = new ArrayList<Long>(uniqueMasks);
//		for (Long l : consensusMaskArray) {
//			System.out.println("++++++++++++++++++++++++++++++++ final mask "+Long.toBinaryString(l));
//		}
//		HashSet<Long> consensusSet = new HashSet<Long>(consensusMaskArray);
//		byte[] pixels = new byte[numX*numY*numZ];
//		setSurfaceMasks(geometry.getGeometrySurfaceDescription().getSurfaceCollection());
//		for (long mask : consensusSet){
//			// for this consensus mask, find the subvolume that is associated
//			SubVolume subvolume = getSubvolume(geometry, mask);
//			if (subvolume == null){
//				throw new RuntimeException("could not reconcile volume samples with original geometry");
//			}
//			byte pixelValue = (byte)subvolume.getHandle();
//			for (int i=0;i<incidentSurfaceMasks.length;i++){
//				if ((incidentSurfaceMasks[i] & mask) != 0){
//					pixels[i] = (byte)pixelValue;
//				}
//			}
//		}
//		
//		
//		VCImageUncompressed vcImage = new VCImageUncompressed(null,pixels,extent,numX,numY,numZ);		
//		return vcImage;
		return null;
	}

	private static SubVolume getSubvolume(Geometry geometry, long mask){
		SurfaceCollection surfaceCollection = geometry.getGeometrySurfaceDescription().getSurfaceCollection();
		for (int i=0;i<surfaceCollection.getSurfaceCount();i++){
			Surface surface = surfaceCollection.getSurfaces(i);
			if ((surface.getInteriorMask() & mask) != 0L){
				for (GeometricRegion region : geometry.getGeometrySurfaceDescription().getGeometricRegions()){
					if (region instanceof VolumeGeometricRegion){
						VolumeGeometricRegion volRegion = (VolumeGeometricRegion)region;
						if (volRegion.getRegionID() == surface.getInteriorRegionIndex()){
							return volRegion.getSubVolume();
						}
					}
				}
			}
			if ((surface.getExteriorMask() & mask) != 0L){
				for (GeometricRegion region : geometry.getGeometrySurfaceDescription().getGeometricRegions()){
					if (region instanceof VolumeGeometricRegion){
						VolumeGeometricRegion volRegion = (VolumeGeometricRegion)region;
						if (volRegion.getRegionID() == surface.getExteriorRegionIndex()){
							return volRegion.getSubVolume();
						}
					}
				}
			}
		}
		return null;
	}
	

	
	
	//
	// compute ray intersection in X-Z plane with ray cast from y = -Infinity to +Infinity
	// hitList is indexed as (i+numX*k)
	//
	public static RayCastResults rayCastXYZ(SurfaceCollection surfaceCollection, double[] samplesX, double[] samplesY, double[] samplesZ){
		int numX = samplesX.length;
		int numY = samplesY.length;
		int numZ = samplesZ.length;
		
		long t1 = System.currentTimeMillis();

		HitList hitListsXY[] = new HitList[numX*numY];
		for (int j = 0; j < numY; j++){
			for (int i = 0; i < numX; i++){
				hitListsXY[i + numX*j] = new HitList(); // samplesX[i], samplesY[j]
			}
		}

		HitList hitListsXZ[] = new HitList[numX*numZ];
		for (int k = 0; k < numZ; k++){
			for (int i = 0; i < numX; i++){
				hitListsXZ[i + numX*k] = new HitList(); // samplesX[i], samplesZ[k]
			}
		}

		HitList hitListsYZ[] = new HitList[numY*numZ];
		for (int k = 0; k < numZ; k++){
			for (int j = 0; j < numY; j++){
				hitListsYZ[j + numY*k] = new HitList(); // samplesY[j], samplesZ[k]
			}
		}

		//
		// here we expect either triangles or nonplanar quads which will be broken up into two triangles.
		// then process using barycentric-like coordinates.
		//
		int hitCount=0;
		cbit.vcell.geometry.surface.Triangle triangles[] = new cbit.vcell.geometry.surface.Triangle[2];
		for (int j = 0; j < surfaceCollection.getSurfaceCount(); j++){
			Surface surface = surfaceCollection.getSurfaces(j);
			for (int k = 0; k < surface.getPolygonCount(); k++){
				Polygon polygon = surface.getPolygons(k);
				Node[] nodes = polygon.getNodes();
				double p0x = nodes[0].getX();
				double p0y = nodes[0].getY();
				double p0z = nodes[0].getZ();
				double p1x = nodes[1].getX();
				double p1y = nodes[1].getY();
				double p1z = nodes[1].getZ();
				double p2x = nodes[2].getX();
				double p2y = nodes[2].getY();
				double p2z = nodes[2].getZ();
				double minX = Math.min(p0x, Math.min(p1x, p2x));
				double maxX = Math.max(p0x, Math.max(p1x, p2x));
				double minY = Math.min(p0y, Math.min(p1y, p2y));
				double maxY = Math.max(p0y, Math.max(p1y, p2y));
				double minZ = Math.min(p0z, Math.min(p1z, p2z));
				double maxZ = Math.max(p0z, Math.max(p1z, p2z));
				if (polygon.getNodeCount()==4){
					double p3x = nodes[3].getX();
					double p3y = nodes[3].getY();
					double p3z = nodes[3].getZ();
					minX = Math.min(minX,p3x);
					maxX = Math.max(maxX,p3x);
					minY = Math.min(minY,p3y);
					maxY = Math.max(maxY,p3y);
					minZ = Math.min(minZ,p3z);
					maxZ = Math.max(maxZ,p3z);
				}
				
				// precompute ray indices that are within the bounding box of the quad.
				int startI = numX;
				int endI = -1;
				for (int ii=0;ii<numX;ii++){
					double sampleX = samplesX[ii];
					if (sampleX >= minX && sampleX <= maxX){
						startI = Math.min(startI, ii);
						endI = Math.max(endI,  ii);
					}
				}
				int startJ = numY;
				int endJ = -1;
				for (int jj=0;jj<numY;jj++){
					double sampleY = samplesY[jj];
					if (sampleY >= minY && sampleY <= maxY){
						startJ = Math.min(startJ, jj);
						endJ = Math.max(endJ,  jj);
					}
				}
				int startK = numZ;
				int endK = -1;
				for (int kk=0;kk<numZ;kk++){
					double sampleZ = samplesZ[kk];
					if (sampleZ >= minZ && sampleZ <= maxZ){
						startK = Math.min(startK, kk);
						endK = Math.max(endK,  kk);
					}
				}
				//
				// convert to quads to triangles if necessary
				//
				int numTriangles;
				if (polygon.getNodeCount()==3){
					numTriangles = 1;
					triangles[0] = new cbit.vcell.geometry.surface.Triangle(polygon.getNodes(0), polygon.getNodes(1), polygon.getNodes(2));
				}else if (polygon.getNodeCount()==4){
					numTriangles = 2;
					triangles[0] = new cbit.vcell.geometry.surface.Triangle(polygon.getNodes(0), polygon.getNodes(1), polygon.getNodes(2));
					triangles[1] = new cbit.vcell.geometry.surface.Triangle(polygon.getNodes(0), polygon.getNodes(2), polygon.getNodes(3));
				}else{
					throw new RuntimeException("polygons with "+polygon.getNodeCount()+" edges are supported");
				}
				for (int triIndex = 0; triIndex < numTriangles; triIndex++){
					cbit.vcell.geometry.surface.Triangle triangle = triangles[triIndex];
					double ax = triangle.getNodes()[0].getX();
					double ay = triangle.getNodes()[0].getY();
					double az = triangle.getNodes()[0].getZ();
					double bx = triangle.getNodes()[1].getX();
					double by = triangle.getNodes()[1].getY();
					double bz = triangle.getNodes()[1].getZ();
					double cx = triangle.getNodes()[2].getX();
					double cy = triangle.getNodes()[2].getY();
					double cz = triangle.getNodes()[2].getZ();
					double v1x = bx-ax;
					double v1y = by-ay;
					double v1z = bz-az;
					double v0x = cx-ax;
					double v0y = cy-ay;
					double v0z = cz-az;
					
					double nx = v1y*v0z - v1z*v0y;
					double ny = -(v1x*v0z - v1z*v0x);
					double nz = v1x*v0y - v1y*v0x;
					double normalLength = Math.sqrt(nx*nx+ny*ny+nz*nz);
					nx = nx/normalLength;
					ny = ny/normalLength;
					nz = nz/normalLength;
					
					// SAMPLE IN XY
					{
					double dot11_xy = v1x*v1x + v1y*v1y;
					double dot01_xy = v0x*v1x + v0y*v1y;
					double dot00_xy = v0x*v0x + v0y*v0y;
					// get normal in y direction (A,B,C) ... y component of (B-A)X(C-A)
					//double nz = v1x*v0y - v1y*v0x;
					//boolean entering = (nz < 0);
					double invDenom = 1.0 / (dot00_xy*dot11_xy - dot01_xy*dot01_xy);
					for (int rayIndexI = startI; rayIndexI <= endI; rayIndexI++){
						for (int rayIndexJ = startJ; rayIndexJ <= endJ; rayIndexJ++){
							HitList hitList = hitListsXY[rayIndexI+rayIndexJ*numX];
							double vpx = samplesX[rayIndexI]-ax;
							double vpy = samplesY[rayIndexJ]-ay;
							double dot0p_xy = v0x*vpx + v0y*vpy;
							double dot1p_xy = v1x*vpx + v1y*vpy;
							double u = (dot11_xy * dot0p_xy - dot01_xy * dot1p_xy) * invDenom;
							double v = (dot00_xy * dot1p_xy - dot01_xy * dot0p_xy) * invDenom;
							if ((u > 0) && (v > 0) && (u + v < 1)){
								double v2z = az + (u*v0z) + (v*v1z);
								double centroidZ = (az+bz+cz)/3;
								hitList.addHitEvent(new HitEvent(surface,polygon,nz,v2z,centroidZ,"no message"));
								hitCount++;
							}
						}
					}
					}
					// SAMPLE IN XZ
					{
					double dot11_xz = v1x*v1x + v1z*v1z;
					double dot01_xz = v0x*v1x + v0z*v1z;
					double dot00_xz = v0x*v0x + v0z*v0z;
					// get normal in y direction (A,B,C) ... y component of (B-A)X(C-A)
					//double ny = -(v1x*v0z - v1z*v0x);
					//boolean entering = (ny < 0);
					double invDenom = 1.0 / (dot00_xz*dot11_xz - dot01_xz*dot01_xz);
					for (int rayIndexI = startI; rayIndexI <= endI; rayIndexI++){
						for (int rayIndexK = startK; rayIndexK <= endK; rayIndexK++){
							HitList hitList = hitListsXZ[rayIndexI+rayIndexK*numX];
							double vpx = samplesX[rayIndexI]-ax;
							double vpz = samplesZ[rayIndexK]-az;
							double dot0p_xz = v0x*vpx + v0z*vpz;
							double dot1p_xz = v1x*vpx + v1z*vpz;
							double u = (dot11_xz * dot0p_xz - dot01_xz * dot1p_xz) * invDenom;
							double v = (dot00_xz * dot1p_xz - dot01_xz * dot0p_xz) * invDenom;
							if ((u > 0) && (v > 0) && (u + v < 1)){
								double v2y = ay + (u*v0y) + (v*v1y);                    
								double centroidY = (ay+by+cy)/3;
								hitList.addHitEvent(new HitEvent(surface,polygon,ny,v2y,centroidY,"no message"));
								hitCount++;
							}
						}
					}
					}
					// SAMPLE IN YZ
					{
					double dot11_yz = v1y*v1y + v1z*v1z;
					double dot01_yz = v0y*v1y + v0z*v1z;
					double dot00_yz = v0y*v0y + v0z*v0z;
					// get normal in y direction (A,B,C) ... y component of (B-A)X(C-A)
					//double nx = v1y*v0z - v1z*v0y;
					//boolean entering = (nx < 0);
					double invDenom = 1.0 / (dot00_yz*dot11_yz - dot01_yz*dot01_yz);
					for (int rayIndexJ = startJ; rayIndexJ <= endJ; rayIndexJ++){
						for (int rayIndexK = startK; rayIndexK <= endK; rayIndexK++){
							HitList hitList = hitListsYZ[rayIndexJ+rayIndexK*numY];
							double vpy = samplesY[rayIndexJ]-ay;
							double vpz = samplesZ[rayIndexK]-az;
							double dot0p_yz = v0y*vpy + v0z*vpz;
							double dot1p_yz = v1y*vpy + v1z*vpz;
							double u = (dot11_yz * dot0p_yz - dot01_yz * dot1p_yz) * invDenom;
							double v = (dot00_yz * dot1p_yz - dot01_yz * dot0p_yz) * invDenom;
							if ((u > 0) && (v > 0) && (u + v < 1)){
								double v2x = ax + (u*v0x) + (v*v1x);                    
								double centroidX = (ax+bx+cx)/3;
								hitList.addHitEvent(new HitEvent(surface,polygon,nx,v2x,centroidX,"no message"));
								hitCount++;
							}
						}
					}
					}
				}
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println("*********************** ray cast XYZ ("+hitCount+" hits), "+(t2-t1)+"ms *********************");
		return new RayCastResults(hitListsXY, hitListsXZ, hitListsYZ, numX, numY, numZ);
	}
	public static Geometry createGeometry(GeometryThumbnailImageFactory geometryThumbnailImageFactory, SurfaceCollection surfaceCollection, Origin origin, Extent extent, ISize sampleSize) throws ImageException, PropertyVetoException, GeometryException, ExpressionException{
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		
		VolumeSamplesBitSet volumeSamples = volumeSampleSurface(surfaceCollection, sampleSize, origin, extent, false);
		//	
		// create a graph with:
		//    a node for each surface face (mask)
		//    an edge for each pair of surface faces share by a volume node.
		//
		Graph connectivityGraph = new Graph();
		ArrayList<Long> masks = new ArrayList<Long>(volumeSamples.getIncidentSurfaceBitSets().keySet());
		for (int i=0;i<masks.size();i++){
			cbit.util.graph.Node node = new cbit.util.graph.Node(masks.get(i).toString(),volumeSamples.getIncidentSurfaceBitSets().get(masks.get(i)));
			connectivityGraph.addNode(node);
		}
		for (int i=0;i<masks.size();i++){
			long mask1 = masks.get(i);
			cbit.util.graph.Node node1 = connectivityGraph.getNode(Long.toString(mask1));
			BitSet bitSet1 = volumeSamples.getIncidentSurfaceBitSets().get(mask1);
			for (int j=i+1;j<masks.size();j++){
				long mask2 = masks.get(j);
				cbit.util.graph.Node node2 = connectivityGraph.getNode(Long.toString(mask2));
				BitSet bitSet2 = volumeSamples.getIncidentSurfaceBitSets().get(mask2);
				if (bitSet1.intersects(bitSet2)){
					BitSet bitSetIntersection = new BitSet();
					bitSetIntersection.or(bitSet1);
					bitSetIntersection.and(bitSet2);
					Edge edge = new Edge(node1,node2,bitSetIntersection);
					connectivityGraph.addEdge(edge);
				}
			}
		}
		
		//
		// create a spanning forest (collection of isolated spanning trees)
		// each tree represents a mutually exclusive set of pixels.
		//
		// if any trees are found to contain both sides of a membrane, 
		//   then the least expensive cutset is identified.
		//
		for (int i=0; i<surfaceCollection.getSurfaceCount();i++){
			Surface surface = surfaceCollection.getSurfaces(i);
			
			Tree[] spanningForest = connectivityGraph.getSpanningForest();
			for (Tree tree : spanningForest){
				cbit.util.graph.Node insideNode = tree.getNode(Long.toString(surface.getInteriorMask()));
				cbit.util.graph.Node outsideNode = tree.getNode(Long.toString(surface.getExteriorMask()));
				if (insideNode!=null && outsideNode!=null){
					System.out.println("++++++++++++++++++++++++++++++++ separating "+Long.toBinaryString(Long.parseLong(insideNode.getName()))+" with "+Long.toBinaryString(Long.parseLong(outsideNode.getName())));
					//
					// remove sub-region covered by more loosely connected (insideNode or outsideNode) from this ensemble region
					// this could be improved by considering a minimal cut-set (too hard for today).
					//
					int connectionCardinalityInsideNode = 0;
					Edge[] insideNodeEdges = connectivityGraph.getAdjacentEdges(insideNode);
					for (Edge insideEdge : insideNodeEdges){
						connectionCardinalityInsideNode += ((BitSet)insideEdge.getData()).cardinality();
					}
					int connectionCardinalityOutsideNode = 0;
					Edge[] outsideNodeEdges = connectivityGraph.getAdjacentEdges(outsideNode);
					for (Edge outsideEdge : outsideNodeEdges){
						connectionCardinalityOutsideNode += ((BitSet)outsideEdge.getData()).cardinality();
					}
					if (connectionCardinalityInsideNode < connectionCardinalityOutsideNode){
						// remove inside Node (clear its bits from all others in this tree)
						for (cbit.util.graph.Node node : tree.getNodes()){
							if (node != insideNode){
								BitSet otherBitSet = (BitSet)node.getData();
								otherBitSet.andNot((BitSet)insideNode.getData());
							}
						}
						for (Edge edge : insideNodeEdges){
							connectivityGraph.remove(edge);
						}
					}else{
						// remove outside Node
						// remove inside Node (clear its bits from all others in this tree)
						for (cbit.util.graph.Node node : tree.getNodes()){
							if (node != outsideNode){
								BitSet otherBitSet = (BitSet)node.getData();
								otherBitSet.andNot((BitSet)outsideNode.getData());
							}
						}
						for (Edge edge : outsideNodeEdges){
							connectivityGraph.remove(edge);
						}
					}
				}
			}
		}
		
		Tree[] spanningForest = connectivityGraph.getSpanningForest();
		BitSet[] regionBitSets = new BitSet[spanningForest.length];
		long[] regionMasks = new long[spanningForest.length];
		for (int i=0;i<spanningForest.length;i++){
			Tree tree = spanningForest[i];
			regionBitSets[i] = new BitSet();
			for (cbit.util.graph.Node node : tree.getNodes()){
				regionBitSets[i].or((BitSet)node.getData());
				regionMasks[i] = regionMasks[i] | Long.valueOf(node.getName());
			}
			System.out.println("+++++++++++++++++++++++++++++++++++++ final region mask : "+Long.toBinaryString(regionMasks[i])+", region size = "+regionBitSets[i].cardinality());
		}
		
		byte[] pixels = new byte[numX*numY*numZ];
		byte pixelValue = 1;
		for (int r=0;r<regionMasks.length;r++){
			BitSet regionBitSet = regionBitSets[r];
			for (int i=0;i<pixels.length;i++){
				if (regionBitSet.get(i)){
					pixels[i] = pixelValue;
				}
			}
			pixelValue++;
		}
		
		
		VCImageUncompressed vcImage = new VCImageUncompressed(null,pixels,extent,numX,numY,numZ);
		Geometry geometry = new Geometry("newGeometry",vcImage);
		geometry.getGeometrySpec().setExtent(extent);
		geometry.getGeometrySpec().setOrigin(origin);
		geometry.precomputeAll(geometryThumbnailImageFactory, true, true);
		
		return geometry;
	}
	


}
