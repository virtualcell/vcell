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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.parser.ExpressionException;

public class RayCaster {
	
	public static boolean bDebug = false;
	public static Geometry createGeometryFromSTL(GeometryThumbnailImageFactory geometryThumbnailImageFactory,SurfaceCollection surfaceCollection, int numSamples) throws ImageException, PropertyVetoException, GeometryException, ExpressionException, IOException{
		
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
	
	public static void show(SurfaceCollection surfaceCollection, RayCastResults rayCastResults_double, RayCastResultsRational rayCastResults_rational, RationalNumber[] samplesX_rational, RationalNumber[] samplesY_rational, RationalNumber[] samplesZ_rational,  double[] samplesX_double, double[] samplesY_double, double[] samplesZ_double, int numX, int numY, int numZ){
		
		long unionOfMasks = 0L;
		for(int i=0; i<surfaceCollection.getSurfaceCount(); i++)
		{
			Surface surf = surfaceCollection.getSurfaces(i);
			unionOfMasks |= surf.getInteriorMask();
			unionOfMasks |= surf.getExteriorMask();
		}

		VolumeSamples volumeSamplesXY_double = null;
		VolumeSamples volumeSamplesXZ_double = null;
		VolumeSamples volumeSamplesYZ_double = null;
		VolumeSamples volumeSamplesXY_rational = null;
		VolumeSamples volumeSamplesXZ_rational= null;
		VolumeSamples volumeSamplesYZ_rational = null;
		if((unionOfMasks & 0x000000FFL) == unionOfMasks )
		{
			volumeSamplesXY_double = new VolumeSamplesByte(numX*numY*numZ);
			volumeSamplesXZ_double = new VolumeSamplesByte(numX*numY*numZ);
			volumeSamplesYZ_double = new VolumeSamplesByte(numX*numY*numZ);
			volumeSamplesXY_rational = new VolumeSamplesByte(numX*numY*numZ);
			volumeSamplesXZ_rational = new VolumeSamplesByte(numX*numY*numZ);
			volumeSamplesYZ_rational = new VolumeSamplesByte(numX*numY*numZ);
		}
		else if((unionOfMasks & 0x0000FFFFL) == unionOfMasks )
		{
			volumeSamplesXY_double = new VolumeSamplesShort(numX*numY*numZ);
			volumeSamplesXZ_double = new VolumeSamplesShort(numX*numY*numZ);
			volumeSamplesYZ_double = new VolumeSamplesShort(numX*numY*numZ);
			volumeSamplesXY_rational = new VolumeSamplesShort(numX*numY*numZ);
			volumeSamplesXZ_rational = new VolumeSamplesShort(numX*numY*numZ);
			volumeSamplesYZ_rational = new VolumeSamplesShort(numX*numY*numZ);
		}
		else if((unionOfMasks & 0xFFFFFFFFL) == unionOfMasks )
		{
			volumeSamplesXY_double = new VolumeSamplesInt(numX*numY*numZ);
			volumeSamplesXZ_double = new VolumeSamplesInt(numX*numY*numZ);
			volumeSamplesYZ_double = new VolumeSamplesInt(numX*numY*numZ);
			volumeSamplesXY_rational = new VolumeSamplesInt(numX*numY*numZ);
			volumeSamplesXZ_rational = new VolumeSamplesInt(numX*numY*numZ);
			volumeSamplesYZ_rational= new VolumeSamplesInt(numX*numY*numZ);
		}
		else
		{
			volumeSamplesXY_double = new VolumeSamplesLong(numX*numY*numZ);
			volumeSamplesXZ_double = new VolumeSamplesLong(numX*numY*numZ);
			volumeSamplesYZ_double = new VolumeSamplesLong(numX*numY*numZ);
			volumeSamplesXY_rational = new VolumeSamplesLong(numX*numY*numZ);
			volumeSamplesXZ_rational = new VolumeSamplesLong(numX*numY*numZ);
			volumeSamplesYZ_rational = new VolumeSamplesLong(numX*numY*numZ);
		}
		
		//
		// volume sample in Z direction (index = i + j*numX)
		//
//		{
		int xyIndex = 0;
		int numXY = numX*numY;
		HitList[] hitListsZ_double = rayCastResults_double.getHitListsXY();
		HitListRational[] hitListsZ_rational = rayCastResults_rational.getHitListsXY();
		for (int j = 0; j < numY; j++){
			for (int i = 0; i < numX; i++){
				hitListsZ_double[xyIndex].sampleRegionIDs(samplesZ_double, volumeSamplesXY_double, xyIndex, numXY);
				hitListsZ_rational[xyIndex].sampleRegionIDs(samplesZ_rational, volumeSamplesXY_rational, xyIndex, numXY);
				xyIndex++;
			}
		}
//		}
		//
		// volume sample in Y direction (index = i + k*numX)
		//
//		{
		int xzIndex = 0;
//		int numXY = numX*numY;
		HitList[] hitListsY_double = rayCastResults_double.getHitListsXZ();
		HitListRational[] hitListsY_rational = rayCastResults_rational.getHitListsXZ();
		for (int k = 0; k < numZ; k++){
			for (int i = 0; i < numX; i++){
				hitListsY_double[xzIndex].sampleRegionIDs(samplesY_double, volumeSamplesXZ_double, i+k*numXY, numX);
				hitListsY_rational[xzIndex].sampleRegionIDs(samplesY_rational, volumeSamplesXZ_rational, i+k*numXY, numX);
				xzIndex++;
			}
		}
//		}
		//
		// volume sample in X direction (index = j + k*numY)
		//
//		{
		int yzIndex = 0;
//		int numXY = numX*numY;
		HitList[] hitListsX_double = rayCastResults_double.getHitListsYZ();
		HitListRational[] hitListsX_rational = rayCastResults_rational.getHitListsYZ();
		for (int k = 0; k < numZ; k++){
			for (int j = 0; j < numY; j++){
				hitListsX_double[yzIndex].sampleRegionIDs(samplesX_double, volumeSamplesYZ_double, j*numX+k*numXY, 1);
				hitListsX_rational[yzIndex].sampleRegionIDs(samplesX_rational, volumeSamplesYZ_rational, j*numX+k*numXY, 1);
				yzIndex++;
			}
		}
//		}
		int index = 0;
		for (int k=0;k<numZ;k++){
			for (int j=0;j<numY;j++){
				for (int i=0;i<numX;i++){
					long volSampleXY_double = volumeSamplesXY_double.getMask(index);
					long volSampleXZ_double = volumeSamplesXZ_double.getMask(index);
					long volSampleYZ_double = volumeSamplesYZ_double.getMask(index);
					long volSampleXY_rational = volumeSamplesXY_rational.getMask(index);
					long volSampleXZ_rational = volumeSamplesXZ_rational.getMask(index);
					long volSampleYZ_rational = volumeSamplesYZ_rational.getMask(index);
					if (RayCaster.connectsAcrossSurface(volSampleXY_double | volSampleXZ_double)){
						System.out.println("discrepency (double) at ("+i+","+j+","+k+") - ("+samplesX_double[i]+","+samplesY_double[j]+","+samplesZ_double[k]+"), between XY and XZ, "+Long.toHexString(volSampleXY_double)+", "+Long.toHexString(volSampleXZ_double));
						System.out.println("double Z ray: "+hitListsZ_double[i+j*numX].getDescription());
						System.out.println("double Y ray: "+hitListsY_double[i+k*numX].getDescription());
						System.out.println("rational Z ray: "+hitListsZ_rational[i+j*numX].getDescription());
						System.out.println("rational Y ray: "+hitListsY_rational[i+k*numX].getDescription());
						System.out.println();
					}
					if (RayCaster.connectsAcrossSurface(volSampleXY_double | volSampleYZ_double)){
						System.out.println("discrepency (double) at ("+i+","+j+","+k+") - ("+samplesX_double[i]+","+samplesY_double[j]+","+samplesZ_double[k]+"), between XY and YZ, "+Long.toHexString(volSampleXY_double)+", "+Long.toHexString(volSampleYZ_double));
						System.out.println("double Z ray: "+hitListsZ_double[i+j*numX].getDescription());
						System.out.println("double X ray: "+hitListsX_double[j+k*numY].getDescription());
						System.out.println("rational Z ray: "+hitListsZ_rational[i+j*numX].getDescription());
						System.out.println("rational X ray: "+hitListsX_rational[j+k*numY].getDescription());
						System.out.println();
					}
					if (RayCaster.connectsAcrossSurface(volSampleXZ_double | volSampleYZ_double)){
						System.out.println("discrepency (double) at ("+i+","+j+","+k+") - ("+samplesX_double[i]+","+samplesY_double[j]+","+samplesZ_double[k]+"), between XZ and YZ, "+Long.toHexString(volSampleXZ_double)+", "+Long.toHexString(volSampleYZ_double));
						System.out.println("double Y ray: "+hitListsY_double[i+k*numX].getDescription());
						System.out.println("double X ray: "+hitListsX_double[j+k*numY].getDescription());
						System.out.println("rational Y ray: "+hitListsY_rational[i+k*numX].getDescription());
						System.out.println("rational X ray: "+hitListsX_rational[j+k*numY].getDescription());
						System.out.println();
					}
					if (RayCaster.connectsAcrossSurface(volSampleXY_rational | volSampleXZ_rational)){
						System.out.println("discrepency (rational) at ("+i+","+j+","+k+") - ("+samplesX_rational[i].doubleValue()+","+samplesY_rational[j].doubleValue()+","+samplesZ_rational[k].doubleValue()+"), between XY and XZ, "+Long.toHexString(volSampleXY_rational)+", "+Long.toHexString(volSampleXZ_rational));
						System.out.println("rational Z ray: "+hitListsZ_rational[i+j*numX].getDescription());
						System.out.println("rational Y ray: "+hitListsY_rational[i+k*numX].getDescription());
					}
					if (RayCaster.connectsAcrossSurface(volSampleXY_rational | volSampleYZ_rational)){
						System.out.println("discrepency (rational) at ("+i+","+j+","+k+") - ("+samplesX_rational[i].doubleValue()+","+samplesY_rational[j].doubleValue()+","+samplesZ_rational[k].doubleValue()+"), between XY and YZ, "+Long.toHexString(volSampleXY_rational)+", "+Long.toHexString(volSampleYZ_rational));
						System.out.println("rational Z ray: "+hitListsZ_rational[i+j*numX].getDescription());
						System.out.println("rational X ray: "+hitListsX_rational[j+k*numY].getDescription());
					}
					if (RayCaster.connectsAcrossSurface(volSampleXZ_rational | volSampleYZ_rational)){
						System.out.println("discrepency (rational) at ("+i+","+j+","+k+") - ("+samplesX_rational[i].doubleValue()+","+samplesY_rational[j].doubleValue()+","+samplesZ_rational[k].doubleValue()+"), between XZ and YZ, "+Long.toHexString(volSampleXZ_rational)+", "+Long.toHexString(volSampleYZ_rational));
						System.out.println("rational Y ray: "+hitListsY_rational[i+k*numX].getDescription());
						System.out.println("rational X ray: "+hitListsX_rational[j+k*numY].getDescription());
					}
					index++;
				}
			}
		}
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
			throw new RuntimeException("Number of masks exceeds max allowed. The image contains too many distinct geometric objects. " +
					"Alternately, sampling resolution is too coarse and results in a single object becoming separated in multiple fragments. " +
					"Try using a finer mesh.");
		}
		setSurfaceMasks(surfaceCollection);
		
		RayCastResults rayCastResults = rayCastXYZ_uv(surfaceCollection, samplesX, samplesY, samplesZ);
		rayCastResults.reconcileHitLists();
		
		if (bDebug){
			RationalNumber ratSamplesX[] = new RationalNumber[numX];
			RationalNumber ratSamplesY[] = new RationalNumber[numY];
			RationalNumber ratSamplesZ[] = new RationalNumber[numZ];
			sampleXYZCoordinates(sampleSize, origin, extent, ratSamplesX, ratSamplesY, ratSamplesZ, bCellCentered);
			RayCastResultsRational rayCastResults_rational = rayCastXYZ_rational(surfaceCollection, ratSamplesX, ratSamplesY, ratSamplesZ);
			show(surfaceCollection,rayCastResults,rayCastResults_rational,ratSamplesX,ratSamplesY,ratSamplesZ,samplesX,samplesY,samplesZ,numX,numY,numZ);
		}
		
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

	public static void sampleXYZCoordinates(ISize sampleSize, Origin origin, Extent extent, RationalNumber[] samplesX, RationalNumber[] samplesY, RationalNumber[] samplesZ, boolean bCellCentered) {
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		RationalNumber rNumX = new RationalNumber(numX);
		RationalNumber rNumY = new RationalNumber(numY);
		RationalNumber rNumZ = new RationalNumber(numZ);
		RationalNumber ox = RationalNumber.getApproximateFraction(origin.getX());
		RationalNumber oy = RationalNumber.getApproximateFraction(origin.getY());
		RationalNumber oz = RationalNumber.getApproximateFraction(origin.getZ());
		RationalNumber half = new RationalNumber(1l,2l);
		RationalNumber extentX = RationalNumber.getApproximateFraction(extent.getX());
		RationalNumber extentY = RationalNumber.getApproximateFraction(extent.getY());
		RationalNumber extentZ = RationalNumber.getApproximateFraction(extent.getZ());
		if (bCellCentered){
			for (int i=0;i<numX;i++){
				RationalNumber rI = new RationalNumber(i);
				samplesX[i] = rI.add(half).mult(extentX).div(rNumX).add(ox);		// ox + ((i+0.5)*extent.getX()/(numX));
			}
			for (int i=0;i<numY;i++){
				RationalNumber rI = new RationalNumber(i);
				samplesY[i] = rI.add(half).mult(extentY).div(rNumY).add(oy); 		// oy + ((i+0.5)*extent.getY()/(numY));
			}
			for (int i=0;i<numZ;i++){
				RationalNumber rI = new RationalNumber(i);
				samplesZ[i] = rI.add(half).mult(extentZ).div(rNumZ).add(oz); 		// oz + ((i+0.5)*extent.getZ()/(numZ));
			}
		}else{
			for (int i=0;i<numX;i++){
				RationalNumber rI = new RationalNumber(i);
				samplesX[i] = rI.mult(extentX).div(rNumX.sub(RationalNumber.ONE)).add(ox);					// ox + (i*extent.getX()/(numX-1));
			}
			for (int i=0;i<numY;i++){
				RationalNumber rI = new RationalNumber(i);
				samplesY[i] = rI.mult(extentY).div(rNumY.sub(RationalNumber.ONE)).add(oy);					// oy + (i*extent.getY()/(numY-1));
			}
			for (int i=0;i<numZ;i++){
				RationalNumber rI = new RationalNumber(i);
				samplesZ[i] = rI.mult(extentZ).div(rNumZ.sub(RationalNumber.ONE)).add(oz);					// oz + (i*extent.getZ()/(numZ-1));
			}
			//
			// instead of sampling right on the "edge" of the bounding box formed by origin/extent ... come in a little to avoid incorrect sampling.
			//
			double epsilonX = 1e-8 * extent.getX()/(numX);
			RationalNumber rEpsilonX = RationalNumber.getApproximateFraction(epsilonX);
			samplesX[0] 				= samplesX[0].add(rEpsilonX);					// += epsilonX;
			samplesX[samplesX.length-1] = samplesX[samplesX.length-1].sub(rEpsilonX);	// -= epsilonX;
			
			double epsilonY = 1e-8 * extent.getY()/(numY);
			RationalNumber rEpsilonY = RationalNumber.getApproximateFraction(epsilonY);
			samplesY[0] 				= samplesY[0].add(rEpsilonY);					// += epsilonY;
			samplesY[samplesY.length-1] = samplesY[samplesY.length-1].sub(rEpsilonY);	// -= epsilonY;
			
			double epsilonZ = 1e-8 * extent.getZ()/(numZ);
			RationalNumber rEpsilonZ = RationalNumber.getApproximateFraction(epsilonZ);
			samplesZ[0] 				= samplesZ[0].add(rEpsilonZ);					// += epsilonZ;
			samplesZ[samplesZ.length-1] = samplesZ[samplesZ.length-1].sub(rEpsilonZ);	// -= epsilonZ;
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
		/*
		for (Long l : consensusMaskArray) {
			System.out.println("++++++++++++++++++++++++++++++++ final mask "+Long.toBinaryString(l));
		}
		*/
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
	public static RayCastResults rayCastXYZ_uv(SurfaceCollection surfaceCollection, double[] samplesX, double[] samplesY, double[] samplesZ){
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
				{
				minX -= epsilon;
				maxX += epsilon;
				minY -= epsilon;
				maxY += epsilon;
				minZ -= epsilon;
				maxZ += epsilon;
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
					Node node0 = triangle.getNodes()[0];
					double ax = node0.getX();
					double ay = node0.getY();
					double az = node0.getZ();
					Node node1 = triangle.getNodes()[1];
					double bx = node1.getX();
					double by = node1.getY();
					double bz = node1.getZ();
					Node node2 = triangle.getNodes()[2];
					double cx = node2.getX();
					double cy = node2.getY();
					double cz = node2.getZ();
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
								double centroidZ = (az + bz + cz)/3;
								String debugMessage = "u,v=("+u+","+v+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
								hitList.addHitEvent(new HitEvent(surface,polygon,nz,v2z,centroidZ,debugMessage));
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
								double centroidY = (ay + by + cy)/3;
								String debugMessage = "u,v=("+u+","+v+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
								hitList.addHitEvent(new HitEvent(surface,polygon,ny,v2y,centroidY,debugMessage));
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
								double centroidX = (ax + bx + cx)/3;
								String debugMessage = "u,v=("+u+","+v+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
								hitList.addHitEvent(new HitEvent(surface,polygon,nx,v2x,centroidX,debugMessage));
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
		System.out.println("***********************(Double UV) ray cast XYZ ("+hitCount+" hits), "+(t2-t1)+"ms *********************");
		return new RayCastResults(hitListsXY, hitListsXZ, hitListsYZ, numX, numY, numZ);
	}


	//
	// compute ray intersection in X-Z plane with ray cast from y = -Infinity to +Infinity
	// hitList is indexed as (i+numX*k)
	//
	public static RayCastResults rayCastXYZ_uvw(SurfaceCollection surfaceCollection, double[] samplesX, double[] samplesY, double[] samplesZ){
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
				{
				double epsilon = 1e-8; // roundoff factor.
				
				minX -= epsilon;
				maxX += epsilon;
				minY -= epsilon;
				maxY += epsilon;
				minZ -= epsilon;
				maxZ += epsilon;
				}
				
				// precompute ray indices that are within the bounding box of the quad.
				int startI = numX;
				int endI = -1;
				for (int ii=0;ii<numX;ii++){
					double sampleX = samplesX[ii];
					if (sampleX >= minX){
						if (sampleX <= maxX){
							startI = Math.min(startI, ii);
							endI = Math.max(endI,  ii);
						}else{
							break;
						}
					}
				}
				int startJ = numY;
				int endJ = -1;
				for (int jj=0;jj<numY;jj++){
					double sampleY = samplesY[jj];
					if (sampleY >= minY){
						if (sampleY <= maxY){
							startJ = Math.min(startJ, jj);
							endJ = Math.max(endJ,  jj);
						}else{
							break;
						}
					}
				}
				int startK = numZ;
				int endK = -1;
				for (int kk=0;kk<numZ;kk++){
					double sampleZ = samplesZ[kk];
					if (sampleZ >= minZ){
						if (sampleZ <= maxZ){
							startK = Math.min(startK, kk);
							endK = Math.max(endK,  kk);
						}else{
							break;
						}
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
					Node node0 = triangle.getNodes()[0];
					double ax = node0.getX();
					double ay = node0.getY();
					double az = node0.getZ();
					Node node1 = triangle.getNodes()[1];
					double bx = node1.getX();
					double by = node1.getY();
					double bz = node1.getZ();
					Node node2 = triangle.getNodes()[2];
					double cx = node2.getX();
					double cy = node2.getY();
					double cz = node2.getZ();
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
					//
					// refer to Journal of Computer Graphics Techniques, Vol. 2, No. 1, 2013.
					//  Watertight Ray/Triangle Intersection
					//
					// uses projected triangle on XY plane ... then uses a 2D edge test for each edge
					// to determine the barycentric coordinates U,V,W  where the 
					//
					for (int rayIndexI = startI; rayIndexI <= endI; rayIndexI++){
						for (int rayIndexJ = startJ; rayIndexJ <= endJ; rayIndexJ++){
							HitList hitList = hitListsXY[rayIndexI+rayIndexJ*numX];
							double vpx = samplesX[rayIndexI];
							double vpy = samplesY[rayIndexJ];

							double Ax = ax-vpx;
							double Ay = ay-vpy;
							double Bx = bx-vpx;
							double By = by-vpy;
							double Cx = cx-vpx;
							double Cy = cy-vpy;
							
							double U = Cx*By-Cy*Bx;
							double V = Ax*Cy-Ay*Cx;
							double W = Bx*Ay-By*Ax;

							if (!((U<0 || V<0 || W<0) && (U>0 || V>0 || W>0))){
								double det = U + V + W;
								if (det != 0){
									U = U/det;
									V = V/det;
									W = W/det;
									double T = U*az+V*bz+W*cz;
									double centroidZ = (az + bz + cz)/3;
									String debugMessage = "u,v,w=("+U+","+V+","+W+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
									hitList.addHitEvent(new HitEvent(surface,polygon,nz,T,centroidZ,debugMessage));
//System.out.println("hit xy @ x="+samplesX[rayIndexI]+", y="+samplesY[rayIndexJ]+" ... ray hit at z="+v2z);
									hitCount++;
								}
							}							
						}
					}
					}
					// SAMPLE IN XZ
					{
					for (int rayIndexI = startI; rayIndexI <= endI; rayIndexI++){
						for (int rayIndexK = startK; rayIndexK <= endK; rayIndexK++){
							HitList hitList = hitListsXZ[rayIndexI+rayIndexK*numX];
							double vpx = samplesX[rayIndexI];
							double vpz = samplesZ[rayIndexK];
							
							double Ax = ax-vpx;
							double Az = az-vpz;
							double Bx = bx-vpx;
							double Bz = bz-vpz;
							double Cx = cx-vpx;
							double Cz = cz-vpz;

							double U = Cx*Bz-Cz*Bx;
							double V = Ax*Cz-Az*Cx;
							double W = Bx*Az-Bz*Ax;

							if (!((U<0 || V<0 || W<0) && (U>0 || V>0 || W>0))){
								double det = U + V + W;
								if (det != 0){
									U = U/det;
									V = V/det;
									W = W/det;
									double T = U*ay+V*by+W*cy;
									double centroidY = (ay + by + cy)/3;
									String debugMessage = "u,v,w=("+U+","+V+","+W+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
									hitList.addHitEvent(new HitEvent(surface,polygon,ny,T,centroidY,debugMessage));
//System.out.println("hit xy @ x="+samplesX[rayIndexI]+", y="+samplesY[rayIndexJ]+" ... ray hit at z="+v2z);
									hitCount++;
								}
							}
						}
					}
					}
					// SAMPLE IN YZ
					{
					for (int rayIndexJ = startJ; rayIndexJ <= endJ; rayIndexJ++){
						for (int rayIndexK = startK; rayIndexK <= endK; rayIndexK++){
							HitList hitList = hitListsYZ[rayIndexJ+rayIndexK*numY];
							double vpy = samplesY[rayIndexJ];
							double vpz = samplesZ[rayIndexK];

							double Ay = ay-vpy;
							double Az = az-vpz;
							double By = bx-vpy;
							double Bz = bz-vpz;
							double Cy = cx-vpy;
							double Cz = cz-vpz;
							
							double U = Cy*Bz-Cz*By;
							double V = Ay*Cz-Az*Cy;
							double W = By*Az-Bz*Ay;

							if (!((U<0 || V<0 || W<0) && (U>0 || V>0 || W>0))){
								double det = U + V + W;
								if (det != 0){
									U = U/det;
									V = V/det;
									W = W/det;
									double T = U*ax+V*bx+W*cx;
									double centroidX = (ax + bx + cx)/3;
									String debugMessage = "u,v,w=("+U+","+V+","+W+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
									hitList.addHitEvent(new HitEvent(surface,polygon,nx,T,centroidX,debugMessage));
//System.out.println("hit yz @ y="+samplesY[rayIndexJ]+", z="+samplesZ[rayIndexK]+" ... ray hit at x="+T/det);
									hitCount++;
								}
							}
						}
					}
					}
				}
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println("***********************(Double UVW) ray cast XYZ ("+hitCount+" hits), "+(t2-t1)+"ms *********************");
		return new RayCastResults(hitListsXY, hitListsXZ, hitListsYZ, numX, numY, numZ);
	}


	
	//
	// compute ray intersection in X-Z plane with ray cast from y = -Infinity to +Infinity
	// hitList is indexed as (i+numX*k)
	//
	public static RayCastResultsRational rayCastXYZ_rational(SurfaceCollection surfaceCollection, RationalNumber[] samplesX, RationalNumber[] samplesY, RationalNumber[] samplesZ){
		int numX = samplesX.length;
		int numY = samplesY.length;
		int numZ = samplesZ.length;
		
		long t1 = System.currentTimeMillis();

		HitListRational hitListsXY[] = new HitListRational[numX*numY];
		for (int j = 0; j < numY; j++){
			for (int i = 0; i < numX; i++){
				hitListsXY[i + numX*j] = new HitListRational(); // samplesX[i], samplesY[j]
			}
		}

		HitListRational hitListsXZ[] = new HitListRational[numX*numZ];
		for (int k = 0; k < numZ; k++){
			for (int i = 0; i < numX; i++){
				hitListsXZ[i + numX*k] = new HitListRational(); // samplesX[i], samplesZ[k]
			}
		}

		HitListRational hitListsYZ[] = new HitListRational[numY*numZ];
		for (int k = 0; k < numZ; k++){
			for (int j = 0; j < numY; j++){
				hitListsYZ[j + numY*k] = new HitListRational(); // samplesY[j], samplesZ[k]
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
			verifySurface(surface);
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
				{
				double epsilon = 1e-8;
				
				minX -= epsilon;
				maxX += epsilon;
				minY -= epsilon;
				maxY += epsilon;
				minZ -= epsilon;
				maxZ += epsilon;
				}
				// precompute ray indices that are within the bounding box of the quad.
				int startI = numX;
				int endI = -1;
				for (int ii=0;ii<numX;ii++){
					double sampleX = samplesX[ii].doubleValue();
					if (sampleX >= minX && sampleX <= maxX){
						startI = Math.min(startI, ii);
						endI = Math.max(endI,  ii);
					}
				}
				int startJ = numY;
				int endJ = -1;
				for (int jj=0;jj<numY;jj++){
					double sampleY = samplesY[jj].doubleValue();
					if (sampleY >= minY && sampleY <= maxY){
						startJ = Math.min(startJ, jj);
						endJ = Math.max(endJ,  jj);
					}
				}
				int startK = numZ;
				int endK = -1;
				for (int kk=0;kk<numZ;kk++){
					double sampleZ = samplesZ[kk].doubleValue();
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
					Node node0 = triangle.getNodes()[0];
					RationalNumber ax = RationalNumber.getApproximateFraction(node0.getX());
					RationalNumber ay = RationalNumber.getApproximateFraction(node0.getY());
					RationalNumber az = RationalNumber.getApproximateFraction(node0.getZ());
					Node node1 = triangle.getNodes()[1];
					RationalNumber bx = RationalNumber.getApproximateFraction(node1.getX());
					RationalNumber by = RationalNumber.getApproximateFraction(node1.getY());
					RationalNumber bz = RationalNumber.getApproximateFraction(node1.getZ());
					Node node2 = triangle.getNodes()[2];
					RationalNumber cx = RationalNumber.getApproximateFraction(node2.getX());
					RationalNumber cy = RationalNumber.getApproximateFraction(node2.getY());
					RationalNumber cz = RationalNumber.getApproximateFraction(node2.getZ());
					RationalNumber v1x = bx.sub(ax);    // bx-ax;
					RationalNumber v1y = by.sub(ay);	// by-ay;
					RationalNumber v1z = bz.sub(az);	// bz-az;
					RationalNumber v0x = cx.sub(ax);	// cx-ax;
					RationalNumber v0y = cy.sub(ay);	// cy-ay;
					RationalNumber v0z = cz.sub(az);	// cz-az;
					
					RationalNumber nx = v1y.mult(v0z).sub(v1z.mult(v0y));			// v1y*v0z - v1z*v0y;
					RationalNumber ny = v1x.mult(v0z).sub(v1z.mult(v0x)).minus();	//-(v1x*v0z - v1z*v0x);
					RationalNumber nz = v1x.mult(v0y).sub(v1y.mult(v0x));			// v1x*v0y - v1y*v0x;
// here we probably don't have to normalize the vector ... so don't have to do this.
					RationalNumber normalLength = RationalNumber.getApproximateFraction(Math.sqrt(nx.mult(nx).add(ny.mult(ny)).add(nz.mult(nz)).doubleValue()));	// Math.sqrt(nx*nx+ny*ny+nz*nz);
					nx = nx.div(normalLength);
					ny = ny.div(normalLength);
					nz = nz.div(normalLength);
					// SAMPLE IN XY
					{
					RationalNumber dot11_xy = v1x.mult(v1x).add(v1y.mult(v1y));		// v1x*v1x + v1y*v1y;
					RationalNumber dot01_xy = v0x.mult(v1x).add(v0y.mult(v1y));		// v0x*v1x + v0y*v1y;
					RationalNumber dot00_xy = v0x.mult(v0x).add(v0y.mult(v0y));		// v0x*v0x + v0y*v0y;
					// get normal in z direction (A,B,C) ... z component of (B-A)X(C-A)
					//boolean entering = (nz < 0);
					RationalNumber invDenom = dot00_xy.mult(dot11_xy).sub(dot01_xy.mult(dot01_xy)).inverse();	// 1.0 / (dot00_xy*dot11_xy - dot01_xy*dot01_xy);
					for (int rayIndexI = startI; rayIndexI <= endI; rayIndexI++){
						for (int rayIndexJ = startJ; rayIndexJ <= endJ; rayIndexJ++){
							HitListRational hitList = hitListsXY[rayIndexI+rayIndexJ*numX];
							RationalNumber vpx = samplesX[rayIndexI].sub(ax);	// samplesX[rayIndexI]-ax;
							RationalNumber vpy = samplesY[rayIndexJ].sub(ay);	// samplesY[rayIndexJ]-ay;
							RationalNumber dot0p_xy = v0x.mult(vpx).add(v0y.mult(vpy));		// v0x*vpx + v0y*vpy;
							RationalNumber dot1p_xy = v1x.mult(vpx).add(v1y.mult(vpy)); 	// v1x*vpx + v1y*vpy;
							RationalNumber u = (dot11_xy.mult(dot0p_xy).sub(dot01_xy.mult(dot1p_xy))).mult(invDenom);	// (dot11_xy * dot0p_xy - dot01_xy * dot1p_xy) * invDenom;
							RationalNumber v = (dot00_xy.mult(dot1p_xy).sub(dot01_xy.mult(dot0p_xy))).mult(invDenom);	// (dot00_xy * dot1p_xy - dot01_xy * dot0p_xy) * invDenom;
//							if ((u > -epsilon) && (v > -epsilon) && (u + v < 1+epsilon)){
							if (u.ge(RationalNumber.ZERO) && v.ge(RationalNumber.ZERO) && u.add(v).le(RationalNumber.ONE)){
								RationalNumber v2z = az.add(u.mult(v0z)).add(v.mult(v1z));                  // az + (u*v0z) + (v*v1z);
								if (nz.isZero()){
									System.err.println("normal is zero for triangle index "+triIndex+" in XY plane: vertices [("+ax+","+ay+","+az+"),("+bx+","+by+","+bz+"),("+cx+","+cy+","+cz+")] in plane YZ");
								}
								RationalNumber centroidZ = az.add(bz).add(cz).div(new RationalNumber(3));
								String debugMessage = "u,v=("+u+","+v+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
								hitList.addHitEvent(new HitEventRational(surface,polygon,nz,v2z,centroidZ,debugMessage));
//System.out.println("hit xy @ x="+samplesX[rayIndexI]+", y="+samplesY[rayIndexJ]+" ... ray hit at z="+v2z);
								hitCount++;
							}
						}
					}
					}
					// SAMPLE IN XZ
					{
					RationalNumber dot11_xz = v1x.mult(v1x).add(v1z.mult(v1z));		// v1x*v1x + v1z*v1z;
					RationalNumber dot01_xz = v0x.mult(v1x).add(v0z.mult(v1z));		// v0x*v1x + v0z*v1z;
					RationalNumber dot00_xz = v0x.mult(v0x).add(v0z.mult(v0z)); 	// v0x*v0x + v0z*v0z;
					// get normal in y direction (A,B,C) ... y component of (B-A)X(C-A)
					//boolean entering = (ny < 0);
					RationalNumber invDenom = dot00_xz.mult(dot11_xz).sub(dot01_xz.mult(dot01_xz)).inverse(); 		// 1.0 / (dot00_xz*dot11_xz - dot01_xz*dot01_xz);
					for (int rayIndexI = startI; rayIndexI <= endI; rayIndexI++){
						for (int rayIndexK = startK; rayIndexK <= endK; rayIndexK++){
							HitListRational hitList = hitListsXZ[rayIndexI+rayIndexK*numX];
							RationalNumber vpx = samplesX[rayIndexI].sub(ax);				// samplesX[rayIndexI]-ax;
							RationalNumber vpz = samplesZ[rayIndexK].sub(az); 				// samplesZ[rayIndexK]-az;
							RationalNumber dot0p_xz = v0x.mult(vpx).add(v0z.mult(vpz)); 	// v0x*vpx + v0z*vpz;
							RationalNumber dot1p_xz = v1x.mult(vpx).add(v1z.mult(vpz));		// v1x*vpx + v1z*vpz;
							RationalNumber u = dot11_xz.mult(dot0p_xz).sub(dot01_xz.mult(dot1p_xz)).mult(invDenom);		// (dot11_xz * dot0p_xz - dot01_xz * dot1p_xz) * invDenom;
							RationalNumber v = dot00_xz.mult(dot1p_xz).sub(dot01_xz.mult(dot0p_xz)).mult(invDenom);		// (dot00_xz * dot1p_xz - dot01_xz * dot0p_xz) * invDenom;
//							if ((u > -epsilon) && (v > -epsilon) && (u + v < 1+epsilon)){
							if (u.ge(RationalNumber.ZERO) && v.ge(RationalNumber.ZERO) && u.add(v).le(RationalNumber.ONE)){
								RationalNumber v2y = ay.add(u.mult(v0y)).add(v.mult(v1y));     // ay + (u*v0y) + (v*v1y);    
								if (ny.isZero()){
									System.err.println("normal is zero for triangle index "+triIndex+" in XZ plane ("+rayIndexI+","+rayIndexK+"): vertices [("+ax+","+ay+","+az+"),("+bx+","+by+","+bz+"),("+cx+","+cy+","+cz+")] in plane YZ");
								}
								RationalNumber centroidY = ay.add(by).add(cy).div(new RationalNumber(3));
								String debugMessage = "u,v=("+u+","+v+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
								hitList.addHitEvent(new HitEventRational(surface,polygon,ny,v2y,centroidY,debugMessage));
//System.out.println("hit xz @ x="+samplesX[rayIndexI]+", z="+samplesZ[rayIndexK]+" ... ray hit at y="+v2y);
								hitCount++;
							}
						}
					}
					}
					// SAMPLE IN YZ
					{
					RationalNumber dot11_yz = v1y.mult(v1y).add(v1z.mult(v1z));			// v1y*v1y + v1z*v1z;
					RationalNumber dot01_yz = v0y.mult(v1y).add(v0z.mult(v1z));			// v0y*v1y + v0z*v1z;
					RationalNumber dot00_yz = v0y.mult(v0y).add(v0z.mult(v0z)); 		// v0y*v0y + v0z*v0z;
					// get normal in y direction (A,B,C) ... y component of (B-A)X(C-A)
					//boolean entering = (nx < 0);
					RationalNumber invDenom = dot00_yz.mult(dot11_yz).sub(dot01_yz.mult(dot01_yz)).inverse(); 	// 1.0 / (dot00_yz*dot11_yz - dot01_yz*dot01_yz);
					for (int rayIndexJ = startJ; rayIndexJ <= endJ; rayIndexJ++){
						for (int rayIndexK = startK; rayIndexK <= endK; rayIndexK++){
							HitListRational hitList = hitListsYZ[rayIndexJ+rayIndexK*numY];
							RationalNumber vpy = samplesY[rayIndexJ].sub(ay); 			// samplesY[rayIndexJ]-ay;
							RationalNumber vpz = samplesZ[rayIndexK].sub(az);			// samplesZ[rayIndexK]-az;
							RationalNumber dot0p_yz = v0y.mult(vpy).add(v0z.mult(vpz));	// v0y*vpy + v0z*vpz;
							RationalNumber dot1p_yz = v1y.mult(vpy).add(v1z.mult(vpz));	// v1y*vpy + v1z*vpz;
							RationalNumber u = dot11_yz.mult(dot0p_yz).sub(dot01_yz.mult(dot1p_yz)).mult(invDenom);	// (dot11_yz * dot0p_yz - dot01_yz * dot1p_yz) * invDenom;
							RationalNumber v = dot00_yz.mult(dot1p_yz).sub(dot01_yz.mult(dot0p_yz)).mult(invDenom);	// (dot00_yz * dot1p_yz - dot01_yz * dot0p_yz) * invDenom;
//							if ((u > -epsilon) && (v > -epsilon) && (u + v < 1+epsilon)){
							if (u.ge(RationalNumber.ZERO) && v.ge(RationalNumber.ZERO) && u.add(v).le(RationalNumber.ONE)){
								RationalNumber v2x = ax.add(u.mult(v0x)).add(v.mult(v1x));  // ax + (u*v0x) + (v*v1x);
								if (ny.isZero()){
									System.err.println("normal is zero for triangle index "+triIndex+" in YZ plane: vertices [("+ax+","+ay+","+az+"),("+bx+","+by+","+bz+"),("+cx+","+cy+","+cz+")] in plane YZ");
								}
								RationalNumber centroidX = ax.add(bx).add(cx).div(new RationalNumber(3));
								String debugMessage = "u,v=("+u+","+v+"),nodes=("+node0.getGlobalIndex()+","+node1.getGlobalIndex()+","+node2.getGlobalIndex()+")";
								hitList.addHitEvent(new HitEventRational(surface,polygon,nx,v2x,centroidX,debugMessage));
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
		System.out.println("***********************(RationalNumber UV) ray cast XYZ ("+hitCount+" hits), "+(t2-t1)+"ms *********************");
		return new RayCastResultsRational(hitListsXY, hitListsXZ, hitListsYZ, numX, numY, numZ);
	}
	
	private static class EdgePolygonRef {
		public int lowToHighPolygonIndex = -1;
		public int highToLowPolygonIndex = -1;
		public void addPolygon(int polygonIndex, int globalVertexIndex1, int globalVertexIndex2) {
			if (globalVertexIndex1<globalVertexIndex2){
				if (lowToHighPolygonIndex == -1){
					lowToHighPolygonIndex = polygonIndex;
				}else{
					throw new RuntimeException("adding polygon index "+polygonIndex+" vertex1="+globalVertexIndex1+" vertex2="+globalVertexIndex2+" --- edge low-to-high handedness already exists for this edge");
				}
			}else{
				if (highToLowPolygonIndex == -1){
					highToLowPolygonIndex = polygonIndex;
				}else{
					throw new RuntimeException("adding polygon index "+polygonIndex+" vertex1="+globalVertexIndex1+" vertex2="+globalVertexIndex2+" --- edge high-to-low handedness already exists for this edge");
				}
			}
		}
	}
	
	private static class Edge {
		public final int lowIndex;
		public final int highIndex;
		public Edge(int index1, int index2){
			if (index1==index2){
				throw new RuntimeException("index1 == index2");
			}
			if (index1<0 || index2<0){
				throw new RuntimeException("index1<0 or index2<0");
			}
			if (index1>index2){
				this.lowIndex = index2;
				this.highIndex = index1;
			}else{
				this.lowIndex = index2;
				this.highIndex = index1;
			}
		}
		@Override
		public int hashCode(){
			return lowIndex;
		}
		@Override
		public boolean equals(Object object){
			if (object instanceof Edge){
				Edge otherEdge = (Edge)object;
				return (lowIndex == otherEdge.lowIndex && highIndex == otherEdge.highIndex);
			}
			return false;
		}
	}
	
	private static void verifySurface(Surface surface) {
		HashMap<Edge, EdgePolygonRef> edgePolygonMap = new HashMap<Edge, EdgePolygonRef>();
		for (int k = 0; k < surface.getPolygonCount(); k++){
			Polygon polygon = surface.getPolygons(k);
			Node[] nodes = polygon.getNodes();
			for (int i=0;i<nodes.length;i++){
				int vertextIndex1 = nodes[i].getGlobalIndex();
				int vertextIndex2 = nodes[(i+1)%nodes.length].getGlobalIndex();
				Edge edge = new Edge(vertextIndex1, vertextIndex2);
				EdgePolygonRef edgePolygonRef = edgePolygonMap.get(edge);
				if (edgePolygonRef == null){
					edgePolygonRef = new EdgePolygonRef();
					edgePolygonMap.put(edge,edgePolygonRef);
				}
				edgePolygonRef.addPolygon(k,vertextIndex1,vertextIndex2);
			}
		}
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
		if (origGeometry.getDimension() < 3) {
			throw new GeometryException("Presently, the Raycaster resampling works only for 3d geometries.");
		}
		GeometrySpec origGeometrySpec = origGeometry.getGeometrySpec();
		VCImage origSubvolumeImage = origGeometrySpec.getSampledImage().getCurrentValue();
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
