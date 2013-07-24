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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.render.FastMarchingMethodHA;
import cbit.vcell.render.Vect3d;

public class DistanceMapGenerator {
	
    private static final double MAX_NUMBER = Double.POSITIVE_INFINITY;

	public static SubvolumeSignedDistanceMap[] computeDistanceMaps(Geometry geometry, VCImage subvolumeHandleImage, boolean bCellCentered) throws ImageException{
		return computeDistanceMaps(geometry, subvolumeHandleImage, bCellCentered, false);
	}
	
    public static SubvolumeSignedDistanceMap[] computeDistanceMaps(Geometry geometry, VCImage subvolumeHandleImage, boolean bCellCentered, boolean insideOnly) throws ImageException{
		
		double[] samplesX = new double[subvolumeHandleImage.getNumX()];
		double[] samplesY = new double[subvolumeHandleImage.getNumY()];
		double[] samplesZ = new double[subvolumeHandleImage.getNumZ()];
		ISize sampleSize = new ISize(subvolumeHandleImage.getNumX(),subvolumeHandleImage.getNumY(), subvolumeHandleImage.getNumZ());
		byte[] pixels = subvolumeHandleImage.getPixels();
		boolean[] ignoreMask = new boolean[sampleSize.getXYZ()];
		Origin origin = geometry.getOrigin();
		Extent extent = geometry.getExtent();
		RayCaster.sampleXYZCoordinates(sampleSize, origin, extent, samplesX, samplesY, samplesZ, bCellCentered);
		
		ArrayList<SubvolumeSignedDistanceMap> distanceMaps = new ArrayList<SubvolumeSignedDistanceMap>();
		int count = 0;
		for (SubVolume subVolume : geometry.getGeometrySpec().getSubVolumes()){
			//
			// find surfaces that bound the current SubVolume
			//
			ArrayList<Surface> surfaces = new ArrayList<Surface>();
			for (GeometricRegion geometricRegion : geometry.getGeometrySurfaceDescription().getGeometricRegions()){
				if (geometricRegion instanceof SurfaceGeometricRegion){
					SurfaceGeometricRegion surfaceGeometricRegion = (SurfaceGeometricRegion)geometricRegion;
					for (GeometricRegion adjacentRegion : ((SurfaceGeometricRegion)geometricRegion).getAdjacentGeometricRegions()){
						if (adjacentRegion instanceof VolumeGeometricRegion && ((VolumeGeometricRegion)adjacentRegion).getSubVolume()==subVolume){
							surfaces.add(geometry.getGeometrySurfaceDescription().getSurfaceCollection().getSurface(surfaceGeometricRegion));
						}
					}
				}
			}
			// find unsigned distances in a narrow band for surfaces that bound this SubVolume (expensive)
			// values outside the band are assumed to be initialized to MAX_NUMBER
			long t1 = System.currentTimeMillis();
			double[] distanceMap = localUnsignedDistanceMap(surfaces, samplesX, samplesY, samplesZ, 3);
			long t2 = System.currentTimeMillis();
			System.out.println("          Distance to triangle:   " + (int)((t2-t1)/1000) + " sec.");
			
			// extend signed distance map using fast marching method from narrow band to all points.
			// will do it in 2 steps, positive growth first towards inside, then change the sign of the whole 
			//   distance map, then positive growth towards the exterior
			// this way, the interior distances will end negative and the exterior distances positive
			// 2 step growth saves memory and reduces the number of elements present at any given time in the binary 
			//   heap (binary heap manipulation is the most time consuming factor and it depends on the # of elements)
			Arrays.fill(ignoreMask, true);
			int subvolumeHandle = subVolume.getHandle();
			for (int i = 0; i<ignoreMask.length; i++){
				if (pixels[i] == subvolumeHandle){		// inside
					ignoreMask[i] = false;
				} else {								// outside
					if (distanceMap[i] < MAX_NUMBER){
						distanceMap[i] = -distanceMap[i];		// make negative the part of narrow band which is outside
					}
				}
			}
//			// step 1, we compute distances for the points "inside"
//			// the points outside are cold (we don't compute their distances this step)
			double deltaX = samplesX[1]-samplesX[0];
			double deltaY = samplesY[1]-samplesY[0];
			double deltaZ = samplesZ[1]-samplesZ[0];
			FastMarchingMethodHA fmm = new FastMarchingMethodHA(samplesX.length, samplesY.length, samplesZ.length, 
					deltaX, deltaY, deltaZ, distanceMap, ignoreMask);
			fmm.march();
			
			if(!insideOnly) {
				// we compute everything - inside and outside
				// sign change of the half-completed distance map, the "interior" will become negative as it should be
				for (int i = 0; i<distanceMap.length; i++){
					if (distanceMap[i] < MAX_NUMBER){
						distanceMap[i] = -distanceMap[i];
					}
				}
				// step 2, we compute distances for the points "outside"
				// no cold points (points we don't care about) this time, they are already frozen
				fmm = new FastMarchingMethodHA(samplesX.length, samplesY.length, samplesZ.length, 
						deltaX, deltaY, deltaZ, distanceMap, null);
				fmm.march();
			} else {	// we only compute distances for the "inside"
				// sign change of the half-completed distance map, the "interior" will become negative as it should be
				for (int i = 0; i<distanceMap.length; i++){
					if (distanceMap[i] < MAX_NUMBER){
						if(pixels[i] != subvolumeHandle) {
							// need to filter out the part of the narrow band which is not inside
							distanceMap[i] = MAX_NUMBER;
						} else {
							distanceMap[i] = -distanceMap[i];
						}
					}
				}
			}
			
//			try {		// save some points in a VisIt compatible format
//			int xm = samplesX.length;
//			int ym = samplesY.length;
//			BufferedWriter out = new BufferedWriter(new FileWriter("c:\\TEMP\\2D_circle" + count + ".3D"));
//			out.write("x y z value\n");
//			
//			for(int j=0; j<distanceMap.length; j++) {
//				int x = getX(j,xm,ym);
//				int y = getY(j,xm,ym);
//				int z = getZ(j,xm,ym);
			
//				if(distanceMap[j] < MAX_NUMBER) {
//					if((j%2 == 0 || j%3 == 0)  && (distanceMap[j] <= -2)) {
//						out.write(x + " " + y + " " + z + " " + (int)(distanceMap[j]*10) + "\n");
//					} else if((j%17 == 0 || j%23 == 0) && (distanceMap[j] <= 0.5) && (distanceMap[j] > -2)) {
//						out.write(x + " " + y + " " + z + " " + (int)(distanceMap[j]*10) + "\n");
//					} else if((j%31 == 0 || j%41 == 0) && (distanceMap[j] > 0.5)) {
//						out.write(x + " " + y + " " + z + " " + (int)(distanceMap[j]*10) + "\n");
//					}
//				} 
			
//				if(distanceMap[j] < MAX_NUMBER) {
//					if(j%2 == 0) {
//						out.write(x + " " + y + " " + z + " " + (int)(distanceMap[j]*100) + "\n");
//					}
//				} 
//				if(x==50 && y==50 && z==25) {		// on the surface
//					out.write(x + " " + y + " " + z + " " + (int)(distanceMap[j]*100) + "\n");
//				}
//				if(x==0 && y==0 && z==0) {
//					out.write(x + " " + y + " " + z + " " + 0 + "\n");
//				}
//				if(x==100 && y==100 && z==100) {
//					out.write(x + " " + y + " " + z + " " + 0 + "\n");
//				}
//				
//			}
//			out.close();
//			} catch (IOException e) {
//			}
			
			SubvolumeSignedDistanceMap subvolumeSignedDistanceMap = new SubvolumeSignedDistanceMap(subVolume, samplesX, samplesY, samplesZ, distanceMap);
			distanceMaps.add(subvolumeSignedDistanceMap);
			count++;
		}
		return distanceMaps.toArray(new SubvolumeSignedDistanceMap[distanceMaps.size()]);
	}

	private static double[] localUnsignedDistanceMap(List<Surface> surfaces, double[] samplesX, double[] samplesY, double[] samplesZ, int padFactor){
		// padFactor is the half-width of local distance map; use big number for full brute-force sampling. (2 is a good number???)
		int numX = samplesX.length;
		int numY = samplesY.length;
		int numZ = samplesZ.length;
		double[] distanceMapFast = new double[numX*numY*numZ];
		Arrays.fill(distanceMapFast, MAX_NUMBER);
		
//		double epsilon = 1e-8; // roundoff factor.
//		long t1 = System.currentTimeMillis();
		Vect3d tmp1 = new Vect3d();			// allocate once, use many times
		Vect3d tmp2 = new Vect3d();
		Vect3d tmp3 = new Vect3d();
		Vect3d tmp4 = new Vect3d();
		Vect3d tmp5 = new Vect3d();
		Vect3d tmp6 = new Vect3d();
		Vect3d testPoint = new Vect3d();
		Vect3d tr1 = new Vect3d();
		Vect3d tr2 = new Vect3d();
		Vect3d tr3 = new Vect3d();

		double padDistanceX = (samplesX[1]-samplesX[0])*padFactor;
		double padDistanceY = (samplesY[1]-samplesY[0])*padFactor;
		double padDistanceZ = (samplesZ[1]-samplesZ[0])*padFactor;

		// here we expect either triangles or nonplanar quads which will be broken up into two triangles.
		cbit.vcell.geometry.surface.Triangle triangles[] = new cbit.vcell.geometry.surface.Triangle[2];
		int surfaceIndex = 0;
//		Random rand = new Random();
		for (Surface surface : surfaces){
//			String outstring = "";
//			int numTrianglesTotal = 0;
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
				
				Vect3d unitNormal = new Vect3d();
				polygon.getUnitNormal(unitNormal);
				// convert quads to triangles if necessary
				int numTriangles = 0;
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
					
//					numTrianglesTotal++;
//					if(surfaceIndex == 0 && numTrianglesTotal <4000) {
//						Node A = triangles[triIndex].getNodes(0);
//						Node B = triangles[triIndex].getNodes(1);
//						Node C = triangles[triIndex].getNodes(2);
//						double color = rand.nextDouble();
//						outstring += trianglePointsToString(A, B, C, color);
//					}

					cbit.vcell.geometry.surface.Triangle triangle = triangles[triIndex];
					
//					for (int kkk=0;kkk<numZ;kkk++){
//						for (int jjj=0;jjj<numY;jjj++){
//							for (int iii=0;iii<numX;iii++){
//								int distanceMapIndex = iii+jjj*numX+kkk*numX*numY;
//								if(distanceMapFast[distanceMapIndex] < MAX_NUMBER) {
//									System.out.println(iii + ", " + jjj + ", " + kkk + ", " + Math.sqrt(distanceMapFast[distanceMapIndex]));
//								}
//							}
//						}
//					}
	
					
					// precompute ray indices that are within the bounding box of the quad.
					int tstartI = numX;
					int tendI = -1;
					for (int ii=0;ii<numX;ii++){
						double sampleX = samplesX[ii];
						if (sampleX >= minX-padDistanceX && sampleX <= maxX+padDistanceX){
							tstartI = Math.min(tstartI, ii);
							tendI = Math.max(tendI,  ii);
						}
					}
					int tstartJ = numY;
					int tendJ = -1;
					for (int jj=0;jj<numY;jj++){
						double sampleY = samplesY[jj];
						if (sampleY >= minY-padDistanceY && sampleY <= maxY+padDistanceY){
							tstartJ = Math.min(tstartJ, jj);
							tendJ = Math.max(tendJ,  jj);
						}
					}
					int tstartK = numZ;
					int tendK = -1;
					for (int kk=0;kk<numZ;kk++){
						double sampleZ = samplesZ[kk];
						if (sampleZ >= minZ-padDistanceZ && sampleZ <= maxZ+padDistanceZ){
							tstartK = Math.min(tstartK, kk);
							tendK = Math.max(tendK,  kk);
						}
					}
					
					int ii=0;
					int jj=0;
					int kk=0;
					int distanceMapIndex = 0;
					tr1.set(triangle.getNodes()[0].getX(), triangle.getNodes()[0].getY(), triangle.getNodes()[0].getZ());
					tr2.set(triangle.getNodes()[1].getX(), triangle.getNodes()[1].getY(), triangle.getNodes()[1].getZ());
					tr3.set(triangle.getNodes()[2].getX(), triangle.getNodes()[2].getY(), triangle.getNodes()[2].getZ());
					for (ii=tstartI;ii<=tendI;ii++){
						for (jj=tstartJ;jj<=tendJ;jj++){
							for (kk=tstartK;kk<=tendK;kk++){
								testPoint.set(samplesX[ii],samplesY[jj],samplesZ[kk]);
								distanceMapIndex = ii+jj*numX+kk*numX*numY;
								double distanceToTriangle3dSquared = DistanceMapGenerator.squaredDistanceToTriangle3d(distanceMapFast[distanceMapIndex],testPoint, tr1, tr2, tr3, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6);
								double bestMatchFast = Math.min(distanceMapFast[distanceMapIndex],distanceToTriangle3dSquared);
								distanceMapFast[distanceMapIndex] = bestMatchFast;
							}
						}
//						if(ii==50 && jj==48 && kk==25) {
//							System.out.println(ii + ", " + jj + ", " + kk + ", " + Math.sqrt(distanceMapFast[distanceMapIndex]));
//						}
					}
				}
			}
//			try {
//			if(surfaceIndex==0) {
//				BufferedWriter out = new BufferedWriter(new FileWriter("c:\\TEMP\\triangles" + surfaceIndex + ".3D"));
//				out.write("x y z value\n");
//				out.write(outstring);
//				out.write(0 + " " + 0 + " " + 0 + " " + 0 + "\n");
//				out.write(numX + " " + numY + " " + numZ + " " + 0 + "\n");
//				out.close();
//			}
//			} catch (IOException e) {
//			}

			surfaceIndex++;
//			System.out.println("numTriangles for surface = " + numTrianglesTotal);
		}
		for (int i=0;i<distanceMapFast.length;i++){
			if (distanceMapFast[i]!=MAX_NUMBER){
				distanceMapFast[i] = Math.sqrt(distanceMapFast[i]);
			}
		}
		return distanceMapFast;
	}
	
	public static int getX(int position, int numX, int numY) {
		int z = (int)(position/(numX*numY));
		int tmp1 = position - z*numX*numY;
		int x = (int)(tmp1%numX);
		return x;
	}
	public static int getY(int position, int numX, int numY) {
		int z = (int)(position/(numX*numY));
		int tmp1 = position - z*numX*numY;
		int y = (int)(tmp1/numX);
		return y;
	}
	public static int getZ(int position, int numX, int numY) {
		int z = (int)(position/(numX*numY));
		return z;
	}

	private static double DistanceToPlane(Vect3d point, Vect3d t0, Vect3d t1, Vect3d t2)
	{
		Vect3d tmp1 = new Vect3d(t1);
		tmp1.sub(t0);
		Vect3d tmp2 = new Vect3d(t2);
		tmp2.sub(t0);
		Vect3d n = tmp1.cross(tmp2);
		n.unit();
		Vect3d disp = new Vect3d(point);
		disp.sub(t0);
		return Math.abs(disp.dot(n));
	}

	public static double distanceToTriangle3d(Vect3d p0, Vect3d t1, Vect3d t2, Vect3d t3) {
		Vect3d tmp = new Vect3d();
		Vect3d tmp1 = new Vect3d();
		Vect3d tmp2 = new Vect3d();
		Vect3d v1 = new Vect3d();
		Vect3d v2 = new Vect3d();
		Vect3d v3 = new Vect3d();
		return Math.sqrt(squaredDistanceToTriangle3d(MAX_NUMBER, p0, t1, t2, t3, tmp, tmp1, tmp2, v1, v2, v3));
	}

	
	public static double squaredDistanceToTriangle3d(double bestSquaredDistance, Vect3d p0, Vect3d t1, Vect3d t2, Vect3d t3, Vect3d tmp, Vect3d tmp1, Vect3d tmp2, Vect3d v1, Vect3d v2, Vect3d v3) {
		tmp.set(t2);
		tmp.sub(t1);
		tmp1.set(t3);
		tmp1.sub(t1);
		Vect3d normal = tmp.cross(tmp1);						// 1  - the normal
		double normalLengthSquared = normal.lengthSquared();
	//	System.out.println("the normal = " + normal + ", length = " + normal.length());
	
		tmp.set(p0);
		tmp.sub(t1);
		double tmpLengthSquared = tmp.lengthSquared();
		if(tmpLengthSquared == 0 || normalLengthSquared == 0) {
			return 0;		// point is in a vertex
		}
		
		double tmpDotNormal = tmp.dot(normal);
		double sign = Math.signum(tmpDotNormal);
		double cosalphaSquared = tmpDotNormal*tmpDotNormal / (tmpLengthSquared*normalLengthSquared);	// 2  - cosalpha  
	//	System.out.println("cos alpha = " + cosalpha);
	
		double projectionLengthSquared = tmpLengthSquared * cosalphaSquared;				// 3    - projection length
	//	double otherMethod = DistanceToPlane(p0, t1, t2, t3);
	//	System.out.println("projectionLength = " + projectionLength);
					
		Vect3d projection =  new Vect3d(normal);
		projection.uminusFast();
		projection.scale(sign*Math.sqrt(projectionLengthSquared / normalLengthSquared));	// 4    - projection vector
	//	System.out.println("projection = " + projection);
		if (projection.lengthSquared() >= bestSquaredDistance){
			//System.out.println("skipping ... projection.lengthSquared() = "+projection.lengthSquared()+", bestSquaredDistance = "+bestSquaredDistance);
			return bestSquaredDistance;
		}
			
		Vect3d projected = new Vect3d(p0);
		projected.add(projection);									// 5    - projection of p0 onto the triangle plane
	//	System.out.println("projected = " + projected);
			
		Vect3d v21 = tmp;
		Vect3d v31 = tmp1;
		Vect3d v32 = tmp2;
		
		v21.set(t1);
		v21.sub(t2);
		v21.unit();
		v31.set(t1);								// t3t1
		v31.sub(t3);
		v31.unit();
		v32.set(t2);								// t3t1
		v32.sub(t3);
		v32.unit();
		
		v1.set(v21);
		v1.add(v31);
	//	System.out.println("v1 = " + v1);
			
		
		v2.set(v32);
		v2.sub(v21);
	//	System.out.println("v2 = " + v2);
			
		// v13 + v23   =   - v31 - v32   =    -(v31 + v32)
		v3.set(v31);
		v3.add(v32);
		v3.uminusFast();
	//	System.out.println("v3 = " + v3);
				
		tmp.set(projected);
		tmp.sub(t1);
		tmp1.set(v1);						// f1v
		tmp1.crossFast(tmp);
		double f1 = tmp1.dot(normal);		// f1 > 0 means projected is anticlockwise of v1
	//	System.out.println("f1 = " + f1);
			
		tmp.set(projected);
		tmp.sub(t2);
		tmp1.set(v2);						// f2v
		tmp1.crossFast(tmp);
		double f2 = tmp1.dot(normal);
	//	System.out.println("f2 = " + f2);
	
		tmp.set(projected);
		tmp.sub(t3);
		tmp1.set(v3);
		tmp1.crossFast(tmp);				// f3v
		double f3 = tmp1.dot(normal);
	//	System.out.println("f3 = " + f3);
	
		boolean bInside = false;
		double squaredDistanceToTriangle = 0;
		if(f1 >= 0 && f2 < 0) {
//			System.out.println("Projection of point is inside the t1, t2 quadrant...");
			bInside = isInsideTriangleFast(projected, normal, t1, t2, tmp, tmp1);
			if(bInside == true) {		// easy case, length of projection already known
//				System.out.println("Distance from point to triangle is " + projectionLength);
				squaredDistanceToTriangle = projectionLengthSquared;
			} else {
				squaredDistanceToTriangle = squaredDistanceToTriangleUtil(projected, t1, t2, p0, projectionLengthSquared, tmp, tmp1, tmp2);
			}
		}
		else if(f2 >= 0 && f3 < 0) {
//			System.out.println("Projection of point is inside the t2, t3 quadrant...");
			bInside = isInsideTriangleFast(projected, normal, t2, t3, tmp, tmp1);
			if(bInside == true) {
//				System.out.println("Distance from point to triangle is " + projectionLength);
				squaredDistanceToTriangle = projectionLengthSquared;
			} else {
				squaredDistanceToTriangle = squaredDistanceToTriangleUtil(projected, t2, t3, p0, projectionLengthSquared, tmp, tmp1, tmp2);
			}
		}
		else if(f1 < 0 && f3 >= 0) {
//			System.out.println("Projection of point is inside the t1, t3 quadrant...");
			bInside = isInsideTriangleFast(projected, normal, t3, t1, tmp, tmp1);
			if(bInside == true) {
//				System.out.println("Distance from point to triangle is " + projectionLength);
				squaredDistanceToTriangle = projectionLengthSquared;
			} else {
				squaredDistanceToTriangle = squaredDistanceToTriangleUtil(projected, t3, t1, p0, projectionLengthSquared, tmp, tmp1, tmp2);
			}
		} else if (f1 == 0 && f2 == 0 && f3 == 0) {
			squaredDistanceToTriangle = projectionLengthSquared;
		} else {
			throw new RuntimeException("Unable to localize projection of point on triangle plane, f1=" + f1 + 
					", f2=" + f2 + ", f3=" + f3);
		}
		return squaredDistanceToTriangle;
	}

	private static double squaredDistanceToTriangleUtil(Vect3d projected, Vect3d left, Vect3d right,
				Vect3d p0, double squaredProjectionLength,
				Vect3d tmp1, Vect3d tmp2, Vect3d tmp3) {		// transmitted as working buffers, to avoid expensive allocations
		double squaredDistanceToTriangle = 0;
		
		tmp1.set(right);
		tmp1.sub(projected);
		tmp2.set(left);
		tmp2.sub(projected);
		tmp3.set(right);
		tmp3.sub(left);
		tmp1.crossFast(tmp2);												// 8    - r
		tmp1.crossFast(tmp3);
		
		double length1 = tmp1.length();
		double projectionToSegmentLength = tmp2.dot(tmp1) / length1;		// 10
																		// projectedToSegment (reuse tmp1 for speed)
		tmp1.scale(projectionToSegmentLength / length1);					// 11
		tmp3.set(projected);												// projection of 'projected' onto the segment (reuse tmp3 for speed)
		tmp3.add(tmp1);														// 12
			
		double rx = right.getX();
		double ry = right.getY();
		double rz = right.getZ();
		double lx = left.getX();
		double ly = left.getY();
		double lz = left.getZ();
		double px = p0.getX();
		double py = p0.getY();
		double pz = p0.getZ();
		double tx = tmp3.getX();
		double ty = tmp3.getY();
		double tz = tmp3.getZ();
			
		double tx_minus_lx = tx-lx;
		double ty_minus_ly = ty-ly;
		double tz_minus_lz = tz-lz;
		double d1 = tx_minus_lx*tx_minus_lx + ty_minus_ly*ty_minus_ly + tz_minus_lz*tz_minus_lz;	// distance squared to left
		double tx_minus_rx = tx-rx;
		double ty_minus_ry = ty-ry;
		double tz_minus_rz = tz-rz;
		double d2 = tx_minus_rx*tx_minus_rx + ty_minus_ry*ty_minus_ry + tz_minus_rz*tz_minus_rz;	// distance squared to right
		double rx_minus_lx = rx-lx;
		double ry_minus_ly = ry-ly;
		double rz_minus_lz = rz-lz;
		double d = rx_minus_lx*rx_minus_lx + ry_minus_ly*ry_minus_ly + rz_minus_lz*rz_minus_lz;		// distance squared between left and right
			
		if(d1<=d && d2<=d) {
//			System.out.println(" closest to line ");
			squaredDistanceToTriangle = projectionToSegmentLength*projectionToSegmentLength + squaredProjectionLength;
		} else {
			if(d1<d2) {
//				System.out.println(" closest to vertex left " + left);
				double lx_minus_px = lx-px;
				double ly_minus_py = ly-py;
				double lz_minus_pz = lz-pz;
				squaredDistanceToTriangle = lx_minus_px*lx_minus_px + ly_minus_py*ly_minus_py + lz_minus_pz*lz_minus_pz;
			} else {
//				System.out.println(" closest to vertex right " + right);
				double rx_minus_px = rx-px;
				double ry_minus_py = ry-py;
				double rz_minus_pz = rz-pz;
				squaredDistanceToTriangle = rx_minus_px*rx_minus_px + ry_minus_py*ry_minus_py + rz_minus_pz*rz_minus_pz;
			}
				
		}
		return squaredDistanceToTriangle;
	}

	private static boolean isInsideTriangleFast(Vect3d point, Vect3d normal, Vect3d left, Vect3d right, 
			Vect3d tmp1, Vect3d tmp2) {		// transmitted as working buffers, to avoid expensive allocations
		tmp1.set(left);
		tmp1.sub(point);
		tmp2.set(right);
		tmp2.sub(point);
		tmp1.crossFast(tmp2);
		double loc = tmp1.dot(normal);		// 7
		if(loc < 0) {
	//		System.out.println("        ... and outside the triangle.");
			return false;
		} else {
	//		System.out.println("        ... and inside the triangle.");
			return true;
		}
	}
	
	private static String trianglePointsToString(Node A, Node B, Node C, double color) {
		String line = "";
		Random rand = new Random();
		for(int i=0; i<500; i++) {
			double a = rand.nextDouble();
			double b = rand.nextDouble();
			Node r = pointInTriangle(a, b, A, B, C);
			line += new String(r.getX()*10 + " " + r.getY()*10 + " " + r.getZ()*10 + " " + (int)(color*100) + "\n");
		}
		return line;
	}
	public static double distanceToTriangleExperimental(Node p, Node A, Node B, Node C) {
		double d = MAX_NUMBER;
		
		Random rand = new Random();
		for(int i=0; i<10000; i++) {
			double a = rand.nextDouble();
			double b = rand.nextDouble();
			Node r = pointInTriangle(a, b, A, B, C);
	
			double dd = distanceBetweenPoints(p, r);
			if(dd < d) {
				d = dd;
			}
		}
		
		Node[] NA = new Node[3];		// check the vertexes as well
		NA[0] = A;
		NA[1] = B;
		NA[2] = C;
		for(int i=0; i<3; i++) {
			double dd = distanceBetweenPoints(p, NA[i]);
			if(dd < d) {
				d = dd;
			}
		}
		return d;	
	}
	public static double distanceToTriangleExperimental(Node p, Node A, Node B, Node C, String fileName) {
		double d = MAX_NUMBER;
		Node closestNode = null;
		
		Random rand = new Random();
		try {
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write("x y z value\n");
		out.write(p.getX()+1 + " " + p.getY() + " " + p.getZ() + " 1\n");
		out.write(p.getX() + " " + p.getY()+1 + " " + p.getZ() + " 1\n");
		out.write(p.getX() + " " + p.getY() + " " + p.getZ()+1 + " 1\n");
	
		for(int i=0; i<10000; i++) {
			double a = rand.nextDouble();
			double b = rand.nextDouble();
			Node r = pointInTriangle(a, b, A, B, C);
			String line = new String(r.getX() + " " + r.getY() + " " + r.getZ() + " 2\n");
			out.write(line);
	
			double dd = distanceBetweenPoints(p, r);
			if(dd < d) {
				d = dd;
				closestNode = r;
			}
		}
		
		Node[] NA = new Node[3];		// check the vertexes as well
		NA[0] = A;
		NA[1] = B;
		NA[2] = C;
		for(int i=0; i<3; i++) {
			String line = new String(NA[i].getX() + " " + NA[i].getY() + " " + NA[i].getZ() + " 2\n");
			out.write(line);
			double dd = distanceBetweenPoints(p, NA[i]);
			if(dd < d) {
				d = dd;
				closestNode = NA[i];
			}
		}
		
		String line1 = new String(closestNode.getX() + " " + closestNode.getY() + " " + closestNode.getZ() + " 3\n");
		out.write(line1);
	//	System.out.println("closestNode:  " + closestNode.getX() + ", " + closestNode.getY() + ", " + closestNode.getZ());
		out.close();
		} catch (IOException e) {
		}
		return d;
	}
		private static double distanceBetweenPoints(Node p, Node r) {
		double dd = Math.sqrt(	(p.getX()-r.getX())*(p.getX()-r.getX()) + 
								(p.getY()-r.getY())*(p.getY()-r.getY()) + 
								(p.getZ()-r.getZ())*(p.getZ()-r.getZ()) );
		return dd;
	}

	private static Node pointInTriangle(double a, double b, Node A, Node B, Node C)
	{
		double c = 0;
		double px, py, pz;
		if (a + b > 1)
		{
			a = 1 - a;
			b = 1 - b;
		}
		c = 1 - a - b;
	
		px = (a * A.getX()) + (b * B.getX()) + (c * C.getX());
		py = (a * A.getY()) + (b * B.getY()) + (c * C.getY());
		pz = (a * A.getZ()) + (b * B.getZ()) + (c * C.getZ());
		Node point = new Node(px, py, pz);
		return point;
	}


	public static void main(String args[]){
		try {
			double distanceToTriangle3d = 0;
			
			Node nt1 = new Node(1, 0, 0);
			Node nt2 = new Node(0, 2, 0);
			Node nt3 = new Node(0, 0, 0.05);
			
			Vect3d t1 = new Vect3d(nt1);
			Vect3d t2 = new Vect3d(nt2);
			Vect3d t3 = new Vect3d(nt3);
			
			{
				Vect3d xt1 = new Vect3d(1,0,5);
				Vect3d xt2 = new Vect3d(0,2,5);
				Vect3d xt3 = new Vect3d(0,0,5);
	
				Vect3d p1 = new Vect3d(-1,-1,8);
				Vect3d p2 = new Vect3d(-1,-1,2);
	
				double d1 = DistanceToPlane(p1, xt1, xt2, xt3);
				double d2 = DistanceToPlane(p2, xt1, xt2, xt3);
				System.out.println(d1 + ", " + d2);
				
				d1 = distanceToTriangle3d(p1, xt1, xt2, xt3);
				d2 = distanceToTriangle3d(p2, xt1, xt2, xt3);
				System.out.println(d1 + ", " + d2);
			}
				
			{		// quadrant test
			Vect3d a = new Vect3d(0.5, -1, 3);		// inside t1, t3 quadrant
			Vect3d b = new Vect3d(-1, 1, 3);		// inside t2, t3 quadrant
			Vect3d c = new Vect3d(2, 2, 3);			// inside t1, t2 quadrant
			
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant");
			distanceToTriangle3d = distanceToTriangle3d(b, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant");
			distanceToTriangle3d = distanceToTriangle3d(c, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant");
			}
		
			{
			Vect3d a = new Vect3d();
			a.set(-0.5, -4, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t3 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, -1, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(1.2, -2, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t1 vertex");
			System.out.println(" -------------------------------------------------- ");
	
			a.set(-0.5, -4, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t3 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, -1, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(1.2, -2, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t1 vertex");
			System.out.println(" ==================================================== ");
			
			a.set(3, 0.5, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t1 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(2, 2, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, 4, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t2 vertex");
			System.out.println(" -------------------------------------------------- ");
	
			a.set(3, 0.5, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t1 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(2, 2, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, 4, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t2 vertex");
			System.out.println(" ================================================== ");
	
			a.set(-1.5, 3, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t2 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(-1, 1, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(-3, -0.5, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t3 vertex");
			System.out.println(" -------------------------------------------------- ");
	
			a.set(-1.5, 3, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t2 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(-1, 1, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(-3, -0.5, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t3 vertex");
			System.out.println(" ===================================================== ");
			}
				
			{		// exact distance test
			double distanceToTriangleExperimental = 0;
			Node np1 = new Node(0.3, 0.3, 3);	// inside the triangle
			Node np2 = new Node(-1, -1, 3);		// closest to vertex
			Node np3 = new Node(1, 1, 3);		// closest to line
			Node np4 = new Node(0.5, -0.5, 3);	// closest to line
			
			Vect3d p1 = new Vect3d(np1);
			Vect3d p2 = new Vect3d(np2);
			Vect3d p3 = new Vect3d(np3);
			Vect3d p4 = new Vect3d(np4);
	
			distanceToTriangleExperimental = distanceToTriangleExperimental(np1, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p1, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (inside the triangle) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			
			distanceToTriangleExperimental = distanceToTriangleExperimental(np2, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p2, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to vertex) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			
			distanceToTriangleExperimental = distanceToTriangleExperimental(np3, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p3, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to line) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			
			distanceToTriangleExperimental = distanceToTriangleExperimental(np4, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p4, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to line) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			}
			
			{	// some particular / unusual conditions
				Node nnt1 = new Node(1, 0, 0); 
				Node nnt2 = new Node(0, 1, 0);
				Node nnt3 = new Node(0, 0, 1);
				
				Vect3d tt1 = new Vect3d(nnt1);
				Vect3d tt2 = new Vect3d(nnt2);
				Vect3d tt3 = new Vect3d(nnt3);

				Node np1 = new Node(1, 1, 1);		// in the center of the triangle
				Node np2 = new Node(1, 0, 0);		// right in 1 vertex
				
				Vect3d p1 = new Vect3d(np1);
				Vect3d p2 = new Vect3d(np2);
		
				double distanceToTriangleExperimental = distanceToTriangleExperimental(np1, nnt1, nnt2, nnt3);
				distanceToTriangle3d = distanceToTriangle3d(p1, tt1, tt2, tt3);
				System.out.println("Distance to triangle = " + distanceToTriangle3d + "  - should be " + distanceToTriangleExperimental);
				System.out.println(" -------------------------------------------------- ");
				
				distanceToTriangleExperimental = distanceToTriangleExperimental(np2, nnt1, nnt2, nnt3);
				distanceToTriangle3d = distanceToTriangle3d(p2, tt1, tt2, tt3);
				System.out.println("Distance to triangle = " + distanceToTriangle3d + "  - should be " + distanceToTriangleExperimental);
				System.out.println(" -------------------------------------------------- ");
			}
				
			{		// an error case, to be fixed
		//				47, 37, 39
		//				44, 44, 38	closest
		//				56, 38, 49
		//				17, 60, 40	test point
		//				- from points: 31.692043572731926
		//				- exact comp : 45.74405664170047
		//				- error: 14.052013068968542
					
				Node ntt1 = new Node(47, 37, 39);
				Node ntt2 = new Node(44, 44, 38);		// closest to this one
				Node ntt3 = new Node(56, 38, 49);
				Node naa = new Node(17, 60, 40);
				
				Vect3d tt1 = new Vect3d(ntt1);
				Vect3d tt2 = new Vect3d(ntt2);
				Vect3d tt3 = new Vect3d(ntt3);
				Vect3d aa = new Vect3d(naa);
				System.out.println("Point: " + aa);
				System.out.println("Node A:  " + ntt1.getX() + ", " + ntt1.getY() + ", " + ntt1.getZ());
				System.out.println("Node B:  " + ntt2.getX() + ", " + ntt2.getY() + ", " + ntt2.getZ());
				System.out.println("Node C:  " + ntt3.getX() + ", " + ntt3.getY() + ", " + ntt3.getZ());

				
				double x1 = distanceBetweenPoints(naa, ntt1);
				double x2 = distanceBetweenPoints(naa, ntt2);
				double x3 = distanceBetweenPoints(naa, ntt3);
				System.out.println("Distance to verteces: " + x1 +", " + x2 + ", " + x3);
				double distanceToTriangleExperimental = distanceToTriangleExperimental(naa, ntt1, ntt2, ntt3);
				distanceToTriangle3d = distanceToTriangle3d(aa, tt1, tt2, tt3);
				System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to vertex t2) - should be " + distanceToTriangleExperimental);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
			
		// we work inside a cube of 100x100x100
		// we pick a triangle inside and we fill it with random points
		// we randomly generate points within the cube and we compute the distance to triangle in 2 ways:
		// using distanceToTriangle3d()
		// we compute the distance from the point to each of the points within the triangle and we keep the smallest result
		// different results mean that there are errors in distanceToTriangle3d()
		double error = 0.05;
		Random rand = new Random();
		Node testPoint = new Node();
		Node A = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
		Node B = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
		Node C = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
		System.out.println(" -------------------------------------------------- ");
		System.out.println("Generate random triangles and compare the computed distance with the smallest of the ");
		System.out.println("pithagorean distances from point to multiple randomly generated points within the triangle.");
		System.out.println("Node A:  " + A.getX() + ", " + A.getY() + ", " + A.getZ());
		System.out.println("Node B:  " + B.getX() + ", " + B.getY() + ", " + B.getZ());
		System.out.println("Node C:  " + C.getX() + ", " + C.getY() + ", " + C.getZ());
		
		Vect3d vTestPoint = new Vect3d();
		Vect3d vA = new Vect3d(A);
		Vect3d vB = new Vect3d(B);
		Vect3d vC = new Vect3d(C);
		
		System.out.println("Display the error information if discrepancy is > " + error);
		int counter = 0;
		for(int i=0; i<2000; i++) {			// randomly generate some points and compute the distance from them to the triangle
		
			testPoint.setX(rand.nextDouble()*100);
			testPoint.setY(rand.nextDouble()*100);
			testPoint.setZ(rand.nextDouble()*100);
			vTestPoint.set(testPoint);
		
			double eD = distanceToTriangleExperimental(testPoint, A, B, C);
			double eE = distanceToTriangle3d(vTestPoint, vA, vB, vC);
			if(Math.abs(eD-eE) > error) {
				System.out.println("testPoint:  " + testPoint.getX() + ", " + testPoint.getY() + ", " + testPoint.getZ());
				System.out.println("distanceToTriangle3d distanc: " + eD + ",   experimental distance: " + eE + ",    error is: " + Math.abs(eD-eE));
				counter++;
			}
		}
		System.out.println(counter + " errors");
		
		System.out.println(" --------------------------- temp test - hardcoded ----------------------- ");
		// 2.0096700879212155, 1.9220373530069457, 1.0017712687448852
		A = new Node(2.0096700879212155,1.9220373530069457,1.0017712687448852);
		B = new Node(2.017962611615153,1.9455150168421707,0.9975560101481948);
		C = new Node(1.982037388384847,1.9455150168421707,0.9975560101481948);
		
		System.out.println("Node A:  " + A.getX() + ", " + A.getY() + ", " + A.getZ());
		System.out.println("Node B:  " + B.getX() + ", " + B.getY() + ", " + B.getZ());
		System.out.println("Node C:  " + C.getX() + ", " + C.getY() + ", " + C.getZ());
		System.out.println(" ");
		
		vTestPoint = new Vect3d();
		vA = new Vect3d(A);
		vB = new Vect3d(B);
		vC = new Vect3d(C);
		
		testPoint.setX(1.9603960396039604);
		testPoint.setY(1.9207920792079207);
		testPoint.setZ(0.9702970297029703);
		vTestPoint.set(testPoint);
		double eD = distanceToTriangleExperimental(testPoint, A, B, C);
		double eE = distanceToTriangle3d(vTestPoint, vA, vB, vC);
		System.out.println(" " + eD);
		System.out.println(" " + eE);
		System.out.println(" should be 0.04269219644426988720168280083091");
		
		testPoint.setX(51/25);
		testPoint.setY(48/25);
		testPoint.setZ(25/25);
		vTestPoint.set(testPoint);
		eD = distanceToTriangleExperimental(testPoint, A, B, C);
		eE = distanceToTriangle3d(vTestPoint, vA, vB, vC);
		System.out.println(" ");
		System.out.println(" " + eD);
		System.out.println(" " + eE);
		System.out.println(" should be ");
		
		
	}

	public static SubvolumeSignedDistanceMap[] extractMiddleSlice(SubvolumeSignedDistanceMap[] distanceMaps3D) {
		ArrayList<SubvolumeSignedDistanceMap> distanceMaps = new ArrayList<SubvolumeSignedDistanceMap>();
		for(SubvolumeSignedDistanceMap ssdm : distanceMaps3D) {
			SubVolume subVolume = ssdm.getSubVolume();
			double[] samplesX = ssdm.getSamplesX();
			double[] samplesY = ssdm.getSamplesY();
			double[] samplesZ = new double[] { ssdm.getSamplesZ()[1] };
			double[] sd = ssdm.getSignedDistances();
			
			double[] signedDistances = new double[samplesX.length*samplesY.length];
			System.arraycopy(sd, samplesX.length*samplesY.length, signedDistances, 0, samplesX.length*samplesY.length);
			
			SubvolumeSignedDistanceMap subvolumeSignedDistanceMap = new SubvolumeSignedDistanceMap(subVolume, samplesX, samplesY, samplesZ, signedDistances);
			distanceMaps.add(subvolumeSignedDistanceMap);
		}
		
		return distanceMaps.toArray(new SubvolumeSignedDistanceMap[distanceMaps.size()]);
	}
}
