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
import java.util.Arrays;
import java.util.HashSet;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactory;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.render.Vect3d;

public class RayCaster {
	
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

	public static VolumeSamples volumeSampleSurface(SurfaceCollection surfaceCollection, ISize sampleSize, Origin origin, Extent extent, boolean bCellCentered) throws ImageException {
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		double samplesX[] = new double[numX];
		double samplesY[] = new double[numY];
		double samplesZ[] = new double[numZ];
		
		sampleXYZCoordinates(sampleSize, origin, extent, samplesX, samplesY, samplesZ, bCellCentered);

		long t1 = System.currentTimeMillis();

		if (surfaceCollection.getSurfaceCount()>32){
			throw new RuntimeException("current mask-based approach cannot handle more than 32 masks");
		}
		setSurfaceMasks(surfaceCollection);
		
		RayCastResults rayCastResults = rayCastXYZ(surfaceCollection, samplesX, samplesY, samplesZ);

		long t2 = System.currentTimeMillis();
		
		long unionOfMasks = 0L;
		for(int i=0; i<surfaceCollection.getSurfaceCount(); i++)
		{
			Surface surf = surfaceCollection.getSurfaces(i);
			unionOfMasks |= surf.getInteriorMask();
			unionOfMasks |= surf.getExteriorMask();
		}

		VolumeSamples volumeSamples = null;
		if((unionOfMasks & 0x000000FFL) == unionOfMasks )
		{
			volumeSamples = new VolumeSamplesByte(numX*numY*numZ);
		}
		else if((unionOfMasks & 0x0000FFFFL) == unionOfMasks )
		{
			volumeSamples = new VolumeSamplesShort(numX*numY*numZ);
		}
		else if((unionOfMasks & 0xFFFFFFFFL) == unionOfMasks )
		{
			volumeSamples = new VolumeSamplesInt(numX*numY*numZ);
		}
		else
		{
			volumeSamples = new VolumeSamplesLong(numX*numY*numZ);
		}
		
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
		long t3 = System.currentTimeMillis();
//		{
//			System.out.println("\n\nBEFORE RECONCILIATION");
//			int index = 0;
//			for (int k=0;k<numZ;k++){
//				for (int j=0;j<numY;j++){
//					for (int i=0;i<numX;i++){
//						System.out.print(volumeSamples.getIncidentSurfaceMask()[index++]+" ");
//					}
//					System.out.println();
//				}
//				System.out.println();
//			}
//		}
		
		int count = 0;
		while (volumeSamples.hasZeros() && count < 100 && surfaceCollection.getSurfaceCount() > 0){
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
//			{
//				System.out.println("\n\nAFTER RECONCILIATION ... LOOP COUNT "+count);
//				int index = 0;
//				for (int k=0;k<numZ;k++){
//					for (int j=0;j<numY;j++){
//						for (int i=0;i<numX;i++){
//							System.out.print(volumeSamples.getIncidentSurfaceMask()[index++]+" ");
//						}
//						System.out.println();
//					}
//					System.out.println();
//				}
//			}
		}		
		long t4 = System.currentTimeMillis();
		
		System.out.println("\n\n\nray trace triangles from 3 orthogonal directions ("+(t2-t1)+"ms), volume sample hit lists ("+(t3-t2)+"ms), "+count+" passes resolving zeros ("+(t4-t3)+"ms)\n\n\n");
		return volumeSamples;
	}

	
	
	private static void setSurfaceMasks(SurfaceCollection surfaceCollection) {
		long mask = 1;
		for (int i=0;i<surfaceCollection.getSurfaceCount();i++){
			if (surfaceCollection.getSurfaces(i).getInteriorRegionIndex() >= surfaceCollection.getSurfaces(i).getExteriorRegionIndex()){
				throw new RuntimeException("interior region index should less than exterior region index");
			}
			surfaceCollection.getSurfaces(i).setInteriorMask(mask);
			mask = mask<<1;
			surfaceCollection.getSurfaces(i).setExteriorMask(mask);
			mask = mask<<1;
		}
	}

	public static void sampleXYZCoordinates(ISize sampleSize, Origin origin, Extent extent, double[] samplesX, double[] samplesY, double[] samplesZ, boolean bCellCentered) {
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
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		
		Origin origin = geometry.getOrigin();
		Extent extent = geometry.getExtent();
		
		SurfaceCollection surfaceCollection = geometry.getGeometrySurfaceDescription().getSurfaceCollection();
		int surfaceCount = surfaceCollection.getSurfaceCount();
		//
		// handles the case where there is nothing to sample.
		//
		if (surfaceCount==0 && geometry.getGeometrySpec().getNumSubVolumes()==1){
			byte[] pixels = new byte[numX*numY*numZ];
			SubVolume subVolume = geometry.getGeometrySpec().getSubVolumes()[0];
			Arrays.fill(pixels,(byte)subVolume.getHandle());
			VCImageUncompressed vcImage = new VCImageUncompressed(null,pixels,extent,numX,numY,numZ);
			return vcImage;
		}		

		VolumeSamples volumeSamples = volumeSampleSurface(surfaceCollection, sampleSize, origin, extent, bCellCentered);
		// for each mask bit, find union of masks which contain that bit ... iterate until no change.
		HashSet<Long> uniqueMasks = volumeSamples.getUniqueMasks();

		ArrayList<Long> consensusMaskArray = new ArrayList<Long>(uniqueMasks);
//		boolean bChanged = true;
//		while (bChanged){
//			bChanged = false;
//			for (int i=0;i<consensusMaskArray.size();i++){
//				for (int j=i+1;j<consensusMaskArray.size();j++){
//					if ((((long)consensusMaskArray.get(i)) & ((long)consensusMaskArray.get(j))) != 0L && (((long)consensusMaskArray.get(i))!=((long)consensusMaskArray.get(j)))){
//						long merged = consensusMaskArray.get(i) | consensusMaskArray.get(j);
//						if ((((merged<<1) & merged)==0L) &&  (((merged>>1) & merged)==0L)){
//System.out.println("++++++++++++++++++++++++++++++++ merged "+Long.toBinaryString(consensusMaskArray.get(i))+" with "+Long.toBinaryString(consensusMaskArray.get(j))+" to get "+Long.toBinaryString(merged));
//							consensusMaskArray.set(i, merged);
//							consensusMaskArray.set(j, merged);
//							bChanged = true;
//						}
//					}
//				}
//			}
//		}
		for (Long l : consensusMaskArray) {
			System.out.println("++++++++++++++++++++++++++++++++ final mask "+Long.toBinaryString(l));
		}
		HashSet<Long> consensusSet = new HashSet<Long>(consensusMaskArray);
		byte[] pixels = new byte[numX*numY*numZ];
		setSurfaceMasks(geometry.getGeometrySurfaceDescription().getSurfaceCollection());
		for (long mask : consensusSet){
			// for this consensus mask, find the subvolume that is associated
			SubVolume subvolume = getSubvolume(geometry, mask);
			if (subvolume == null){
				throw new RuntimeException("could not reconcile volume samples with original geometry");
			}
			byte pixelValue = (byte)subvolume.getHandle();
			
			for (int i=0;i<volumeSamples.getNumXYZ();i++){
				if ((volumeSamples.getMask(i) & mask) != 0){
					pixels[i] = (byte)pixelValue;
				}
			}
		}
		
		
		VCImageUncompressed vcImage = new VCImageUncompressed(null,pixels,extent,numX,numY,numZ);		
		return vcImage;
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
		if (geometry.getGeometrySpec().getNumSubVolumes()==1 && mask==0){
			return geometry.getGeometrySpec().getSubVolumes()[0];
		}
		return null;
	}
	

	public static boolean connectsAcrossSurface(long mask){
		//
		// each side of surface N has mask bits of 1<<(2*N) for inside and 1<<(2*N+1) for outside
		// 0x55555555 & mask gives you only the lower bits in each 2-bit chunk (e.g. only inside)
		// then shift the low bits to line up with the high bits (e.g. the outside) and see if mask spans inside/outside ... error
		//
		return ((((0x55555555 & mask)<<1) & mask)!=0L);
	}
	
	//
	// compute ray intersection in X-Z plane with ray cast from y = -Infinity to +Infinity
	// hitList is indexed as (i+numX*k)
	//
	public static RayCastResults rayCastXYZ(SurfaceCollection surfaceCollection, double[] samplesX, double[] samplesY, double[] samplesZ){
		int numX = samplesX.length;
		int numY = samplesY.length;
		int numZ = samplesZ.length;
		
		double epsilon = 1e-8; // roundoff factor.
		
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
				int numRaysX = Math.max(0,endJ-startJ+1)*Math.max(0,endK-startK+1);
				int numRaysY = Math.max(0,endI-startI+1)*Math.max(0,endK-startK+1);
				int numRaysZ = Math.max(0,endI-startI+1)*Math.max(0,endJ-startJ+1);
				Vect3d unitNormal = new Vect3d();
				polygon.getUnitNormal(unitNormal);
//				System.out.println("\n\nquad("+k+"), numRaysX = " + numRaysX+ " numRaysY = " + numRaysY + " numRaysZ = " + numRaysZ + ", unitNormal=("+unitNormal.toString()+")");
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
					// get normal in z direction (A,B,C) ... z component of (B-A)X(C-A)
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
							if ((u > -epsilon) && (v > -epsilon) && (u + v < 1+epsilon)){
								double v2z = az + (u*v0z) + (v*v1z);                    
								hitList.addHitEvent(new HitEvent(surface,polygon,nz,v2z));
//System.out.println("hit xy @ x="+samplesX[rayIndexI]+", y="+samplesY[rayIndexJ]+" ... ray hit at z="+v2z);
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
					boolean entering = (ny < 0);
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
							if ((u > -epsilon) && (v > -epsilon) && (u + v < 1+epsilon)){
								double v2y = ay + (u*v0y) + (v*v1y);                    
								hitList.addHitEvent(new HitEvent(surface,polygon,ny,v2y));
//System.out.println("hit xz @ x="+samplesX[rayIndexI]+", z="+samplesZ[rayIndexK]+" ... ray hit at y="+v2y);
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
					boolean entering = (nx < 0);
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
							if ((u > -epsilon) && (v > -epsilon) && (u + v < 1+epsilon)){
								double v2x = ax + (u*v0x) + (v*v1x);                    
								hitList.addHitEvent(new HitEvent(surface,polygon,nx,v2x));
//System.out.println("hit yz @ y="+samplesY[rayIndexJ]+", z="+samplesZ[rayIndexK]+" ... ray hit at x="+v2x);
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
		
		VolumeSamples volumeSamples = volumeSampleSurface(surfaceCollection, sampleSize, origin, extent, false);
		// for each mask bit, find union of masks which contain that bit ... iterate until no change.
		HashSet<Long> uniqueMasks = volumeSamples.getUniqueMasks();

		ArrayList<Long> consensusMaskArray = new ArrayList<Long>(uniqueMasks);
		boolean bChanged = true;
		while (bChanged){
			bChanged = false;
			for (int i=0;i<consensusMaskArray.size();i++){
				for (int j=i+1;j<consensusMaskArray.size();j++){
					if ((((long)consensusMaskArray.get(i)) & ((long)consensusMaskArray.get(j))) != 0L && (((long)consensusMaskArray.get(i))!=((long)consensusMaskArray.get(j)))){
						long merged = consensusMaskArray.get(i) | consensusMaskArray.get(j);
						if ((((merged<<1) & merged)==0L) &&  (((merged>>1) & merged)==0L)){
System.out.println("++++++++++++++++++++++++++++++++ merged "+Long.toBinaryString(consensusMaskArray.get(i))+" with "+Long.toBinaryString(consensusMaskArray.get(j))+" to get "+Long.toBinaryString(merged));
							consensusMaskArray.set(i, merged);
							consensusMaskArray.set(j, merged);
							bChanged = true;
						}
					}
				}
			}
		}
		HashSet<Long> consensusSet = new HashSet<Long>(consensusMaskArray);
		int[] pixels = new int[numX*numY*numZ];
		int pixelValue = 1;
		for (long mask : consensusSet){
System.out.println("++++++++++++++++++ +++++++++++++++ ++++++++++++++++++ consensus mask: "+Long.toBinaryString(mask));
// TODO have to merge masks ... only if doesn't couple both sides of membrane.  
// TODO generate octree ... 2^N sampling apriori ..store in tree with info of where surfaces go for smoldyn? 
// TODO then Smoldyn chooses num volume samples (in each of x,y,z) NumSamples_i = 2^(N-k) for (k>=2) ... then 2^(3k) subsamples for volume fraction computation .. info (e.g. included panels) summarized in subtree roots.
//    use connectsAcrossSurface() to avoid short-circuiting a membrane.
			for (int i=0;i<volumeSamples.getNumXYZ();i++){
				if ((volumeSamples.getMask(i) & mask) != 0){
				//if (incidentSurfaceMasks[i] == mask){
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
	
	public static Geometry resampleGeometry(GeometryThumbnailImageFactory geometryThumbnailImageFactory, Geometry origGeometry, ISize sampleSize) throws ImageException, PropertyVetoException, GeometryException, ExpressionException {
		GeometrySpec origGeometrySpec = origGeometry.getGeometrySpec();
		VCImage origSubvolumeImage = origGeometry.getGeometrySpec().getSampledImage().getCurrentValue();
		if (origSubvolumeImage==null){	
			throw new GeometryException("original geometry does not have a sampled image");
		}
		
		
		VCImage resampledSubvolumeImage = RayCaster.sampleGeometry(origGeometry, sampleSize, false);
		
		//
		// Check if resampling failed: 
		//    if not the same number of pixelClasses (between original geometry and resampled)
		//    if the subvolume handles are the same
		//
		boolean bSameSubvolumes = true;
		if (origSubvolumeImage.getNumPixelClasses() != resampledSubvolumeImage.getNumPixelClasses()){
			bSameSubvolumes = false;
		}
		//
		// check if the subvolume handles are the same for both subvolume sampled images (from original geometry and resampled image).
		//
		for (VCPixelClass origPixelClass : origSubvolumeImage.getPixelClasses()){
			VCPixelClass resampledPixelClass = resampledSubvolumeImage.getPixelClassFromPixelValue(origPixelClass.getPixel());
			if (resampledPixelClass==null){
				bSameSubvolumes = false;
				break;
			}
		}
		
		//
		// if subvolume not the same, compose a nice message and throw an exception
		//
		if (!bSameSubvolumes){
			StringBuffer message = new StringBuffer();
			message.append("\n\nexisting geometry:\n");
			for (SubVolume oldSubvolume : origGeometrySpec.getSubVolumes()){
				long count = origSubvolumeImage.countPixelsByValue((byte)oldSubvolume.getHandle());
				message.append("subvolume('"+oldSubvolume.getName()+"',handle="+oldSubvolume.getHandle()+",numPixels="+count+" of "+origSubvolumeImage.getNumXYZ()+"\n");
			}
			message.append("\n\nnew resampled handle VCImage:\n");
			for (VCPixelClass newPixelClass : resampledSubvolumeImage.getPixelClasses()){
				long count = resampledSubvolumeImage.countPixelsByValue((byte)newPixelClass.getPixel());
				message.append("pixelClass('"+newPixelClass.getPixelClassName()+"',pixelValue="+newPixelClass.getPixel()+",numPixels="+count+" of "+resampledSubvolumeImage.getNumXYZ()+")\n");
			}
			throw new GeometryException("original Geometry had "+origSubvolumeImage.getNumPixelClasses()+" subvolumes, resampled Geometry found "+resampledSubvolumeImage.getNumPixelClasses()+" subvolumes "+message.toString());
		}

		//
		// Create new VCImage that will form the basis for a new image-based geometry.
		//
		VCImage newVCImage = null;
		if (origGeometrySpec.getImage()!=null){
			//
			// was an image-based geometry - try to make new VCImage similar to the original (same pixelClass names and pixel values).
			// the goal is to make identical geometries if the sample size is same as the original image size.
			//
			//  create a new VCImage with same image pixel values (not subvolume handles) and pixel class names as original image.
			//
			byte[] newVCImagePixels = new byte[sampleSize.getXYZ()];
			byte[] resampledSubvolumePixels = resampledSubvolumeImage.getPixels();
			for (int i=0;i<sampleSize.getXYZ();i++){
				int subvolumeHandle = resampledSubvolumePixels[i];
				ImageSubVolume imageSubvolume = (ImageSubVolume)origGeometrySpec.getSubVolume(subvolumeHandle);
				newVCImagePixels[i] = (byte)imageSubvolume.getPixelValue();
			}
			newVCImage = new VCImageUncompressed(null,newVCImagePixels,origGeometry.getExtent(),sampleSize.getX(),sampleSize.getY(),sampleSize.getZ());
			newVCImage.setName(origGeometrySpec.getImage().getName());
			ArrayList<VCPixelClass> newPixelClasses = new ArrayList<VCPixelClass>();
			for (VCPixelClass origPixelClass : origGeometrySpec.getImage().getPixelClasses()){
				SubVolume origSubvolume = origGeometrySpec.getImageSubVolumeFromPixelValue(origPixelClass.getPixel());
				newPixelClasses.add(new VCPixelClass(null,  origSubvolume.getName(), origPixelClass.getPixel()));
			}
			newVCImage.setPixelClasses(newPixelClasses.toArray(new VCPixelClass[newPixelClasses.size()]));
		}else{
			//
			// was an analytic geometry - create a new image-based geometry
			//
			//   create a new VCImage with image pixel values and pixelClass names equal to corresponding subvolumes from original geometry
			//   make new subvolume names equal to old subvolume names.
			//
			byte[] newVCImageSubvolumePixels = resampledSubvolumeImage.getPixels().clone();
			newVCImage = new VCImageUncompressed(null,newVCImageSubvolumePixels,origGeometry.getExtent(),sampleSize.getX(),sampleSize.getY(),sampleSize.getZ());
			ArrayList<VCPixelClass> newPixelClasses = new ArrayList<VCPixelClass>();
			for (SubVolume origSubvolume : origGeometrySpec.getSubVolumes()){
				newPixelClasses.add(new VCPixelClass(null,  origSubvolume.getName(), origSubvolume.getHandle()));  // note: newVCImage already has subvolume handle pixels. 
			}
			newVCImage.setPixelClasses(newPixelClasses.toArray(new VCPixelClass[newPixelClasses.size()]));
		}
		
		//
		// construct the new geometry with the sampled VCImage.
		//
		Geometry newGeometry = new Geometry(origGeometry.getName(), newVCImage);
		newGeometry.getGeometrySpec().setExtent(origGeometry.getExtent());
		newGeometry.getGeometrySpec().setOrigin(origGeometry.getOrigin());
		newGeometry.setDescription(origGeometry.getDescription());
		newGeometry.getGeometrySurfaceDescription().setFilterCutoffFrequency(origGeometry.getGeometrySurfaceDescription().getFilterCutoffFrequency());
		
		newGeometry.precomputeAll(geometryThumbnailImageFactory, true,true);
		
		return newGeometry;
	}
	
}
