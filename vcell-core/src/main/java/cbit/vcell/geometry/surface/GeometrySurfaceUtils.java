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

import org.apache.logging.log4j.LogManager;
//import org.vcell.util.ISize;
//import progress.message.client.EExclusiveQueueOpen;
import org.apache.logging.log4j.Logger;
import org.vcell.util.VCellThreadChecker;

import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryUnitSystem;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (6/28/2004 2:52:23 PM)
 * @author: Jim Schaff
 */ 
public class GeometrySurfaceUtils {
	private static Logger lg = LogManager.getLogger(GeometrySurfaceUtils.class);
/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 3:09:13 PM)
 * @return cbit.vcell.geometry.surface.GeometricRegion[]
 * @param geoSurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 * @param surfaceCollection cbit.vcell.geometry.surface.SurfaceCollection
 */
public static GeometricRegion[] getUpdatedGeometricRegions(GeometrySurfaceDescription geoSurfaceDescription, cbit.vcell.geometry.RegionImage regionImage, SurfaceCollection surfaceCollection) {
	//
	// parse regionImage into ResolvedVolumeLocations
	//
	VCellThreadChecker.checkCpuIntensiveInvocation();
	
	double sizeOfPixel = 0;
	VCUnitDefinition volumeUnit = null;
	VCUnitDefinition surfaceUnit = null;
	GeometrySpec geometrySpec = geoSurfaceDescription.getGeometry().getGeometrySpec();
	GeometryUnitSystem geometryUnitSystem = geoSurfaceDescription.getGeometry().getUnitSystem();
	switch (geometrySpec.getDimension()){
		case 1: {
			sizeOfPixel = geometrySpec.getExtent().getX()/(regionImage.getNumX()-1);
			//sizeOfPixel /= 9.0;  // to account for the padding from 1D to 3D
			volumeUnit = geometryUnitSystem.getLengthUnit();
			surfaceUnit = geometryUnitSystem.getInstance_DIMENSIONLESS();
			break;
		}
		case 2: {
			sizeOfPixel= geometrySpec.getExtent().getX()/(regionImage.getNumX()-1) *
						 geometrySpec.getExtent().getY()/(regionImage.getNumY()-1);
			//sizeOfPixel /= 3.0;  // to account for the padding from 2D to 3D
			volumeUnit = geometryUnitSystem.getAreaUnit();
			surfaceUnit = geometryUnitSystem.getLengthUnit();
			break;
		}
		case 3: {
			sizeOfPixel= geometrySpec.getExtent().getX()/(regionImage.getNumX()-1) *
						 geometrySpec.getExtent().getY()/(regionImage.getNumY()-1) *
						 geometrySpec.getExtent().getZ()/(regionImage.getNumZ()-1);
			volumeUnit = geometryUnitSystem.getVolumeUnit();
			surfaceUnit = geometryUnitSystem.getAreaUnit();
			break;
		}
	}
	int numX = regionImage.getNumX();
	int numY = regionImage.getNumY();
	int numZ = regionImage.getNumZ();
	int numXY = numX*numY;
	
	java.util.Vector<GeometricRegion> regionList = new java.util.Vector<GeometricRegion>();
	cbit.vcell.geometry.RegionImage.RegionInfo regionInfos[] = regionImage.getRegionInfos();
	for (int i = 0; i < regionInfos.length; i++){
		cbit.vcell.geometry.RegionImage.RegionInfo regionInfo = regionInfos[i];
		lg.info(regionInfo);
		cbit.vcell.geometry.SubVolume subVolume = geometrySpec.getSubVolume(regionInfo.getPixelValue());
		String name = subVolume.getName()+regionInfo.getRegionIndex();
		int numPixels = regionInfo.getNumPixels();
		double size = numPixels*sizeOfPixel;
		//int regionIndex = regionInfo.getRegionIndex();
		//
		// adjust of volume region size for side/edge/corner pixels (1/2, 1/4, 1/8 of full size respectively)
		//
		switch (geometrySpec.getDimension()){
			case 1: {
				int numHalvesToRemove = 0;
				// -x pixel
				if (regionInfo.isIndexInRegion(0)){ //(regionImage.getRegionIndex(0,0,0) == regionIndex)
					numHalvesToRemove++;
				}
				// +x pixel
				if (regionInfo.isIndexInRegion(numX-1)){ //(regionImage.getRegionIndex(regionImageNumX-1,0,0) == regionIndex){
					numHalvesToRemove++;
				}
				size -= sizeOfPixel*0.5*numHalvesToRemove;//*9; // 1D is padded by 3 in y and 3 in z (hence factor of 9)
				break;
			}
			case 2: {
				int numQuadrantsToRemove = 0;
				int yOffset = 0;
				for (int yIndex = 0; yIndex < numY; yIndex++){
					// -x side (including attached corners)
					
					if (regionInfo.isIndexInRegion(yOffset)){ // (regionImage.getRegionIndex(0,yIndex,0) == regionIndex){
						if (yIndex==0 || yIndex==numY-1){
							numQuadrantsToRemove += 3;		// corner, remove 3 quadrants
						}else{
							numQuadrantsToRemove += 2;		// side, remove 2 quadrants
						}
					}
					// +x side (including attached corners)
					if (regionInfo.isIndexInRegion(numX-1+yOffset)){ // (regionImage.getRegionIndex(numX-1,yIndex,0) == regionIndex){
						if (yIndex==0 || yIndex==numY-1){
							numQuadrantsToRemove += 3;		// corner, remove 3 quadrants
						}else{
							numQuadrantsToRemove += 2;		// side, remove 2 quadrants
						}
					}
					yOffset += numX;
				}
				int yOffsetLastLine = (numY-1)*numX;
				for (int xIndex = 1; xIndex < numX-1; xIndex++){
					// -y side (excluding corners)
					if (regionInfo.isIndexInRegion(xIndex)){ // (regionImage.getRegionIndex(xIndex,0,0) == regionIndex){
						numQuadrantsToRemove += 2;		// side, remove 2 quadrants
					}
					// +x side (excluding corners)
					if (regionInfo.isIndexInRegion(xIndex+yOffsetLastLine)){ // (regionImage.getRegionIndex(xIndex,numY-1,0) == regionIndex){
						numQuadrantsToRemove += 2;		// side, remove 2 quadrants
					}
				}
				size -= sizeOfPixel*0.25*numQuadrantsToRemove;//*3; // 2D is padded by 3 in z (hence the factor of 3).
				break;
			}
			case 3: {
				int numOctantsToRemove = 0;
				for (int zIndex = 0; zIndex < numZ; zIndex++){
					for (int yIndex = 0; yIndex < numY; yIndex++){
						// -x side (including attached edges and corners)
						int totalOctants = 4; // already on face of boundary (removing half)
						//lg.info("-x side including edges and corners");
						if (regionInfo.isIndexInRegion(yIndex * numX + zIndex * numXY)){ // (regionImage.getRegionIndex(0,yIndex,zIndex) == regionIndex){
							if (yIndex==0 || yIndex==numY-1){
								totalOctants /= 2;
							}
							if (zIndex==0 || zIndex==numZ-1){
								totalOctants /= 2;
							}
							numOctantsToRemove += (8-totalOctants);
						}
						//lg.info("+x side including edges and corners");
						// +x side (including attached edges and corners)
						totalOctants = 4; // already on face of boundary (removing half)
						if (regionInfo.isIndexInRegion(numX - 1 + yIndex * numX + zIndex * numXY)){ // (regionImage.getRegionIndex(numX-1,yIndex,zIndex) == regionIndex){
							if (yIndex==0 || yIndex==numY-1){
								totalOctants /= 2;
							}
							if (zIndex==0 || zIndex==numZ-1){
								totalOctants /= 2;
							}
							numOctantsToRemove += (8-totalOctants);
						}
					}
					for (int xIndex = 1; xIndex < numX-1; xIndex++){
						// -y side (including attached edges along x axis, excluding corners)
						//lg.info("-y side including edges");
						int totalOctants = 4; // already on face of boundary (removing half)
						if (regionInfo.isIndexInRegion(xIndex + zIndex * numXY)){ // (regionImage.getRegionIndex(xIndex,0,zIndex) == regionIndex){
							if (zIndex==0 || zIndex==numZ-1){
								totalOctants /= 2;
							}
							numOctantsToRemove += (8-totalOctants);
						}
						//lg.info("+y side including edges");
						// +y side (including attached edges along x axis, excluding corners)
						totalOctants = 4; // already on face of boundary (removing half)
						if (regionInfo.isIndexInRegion(xIndex + (numY-1) * numX + zIndex * numXY)){ // (regionImage.getRegionIndex(xIndex,numY-1,zIndex) == regionIndex){
							if (zIndex==0 || zIndex==numZ-1){
								totalOctants /= 2;
							}
							numOctantsToRemove += (8-totalOctants);
						}
					}

				}
				
				for (int yIndex = 1; yIndex < numY-1; yIndex++){
					for (int xIndex = 1; xIndex < numX-1; xIndex++){
						int totalOctants = 4; // already on face of boundary (removing half)
						// -z side (excluding all attached edges and corners)
						//lg.info("-z side including nothing");
						if (regionInfo.isIndexInRegion(xIndex + yIndex * numX)){ // (regionImage.getRegionIndex(xIndex,yIndex,0) == regionIndex){
							numOctantsToRemove += (8-totalOctants);
						}
						// +z side (excluding all attached edges and corners)
						//lg.info("+z side including nothing");
						if (regionInfo.isIndexInRegion(xIndex + yIndex * numX + (numZ - 1) * numXY)){ // (regionImage.getRegionIndex(xIndex,yIndex,numZ-1) == regionIndex){
							numOctantsToRemove += (8-totalOctants);
						}
					}
				}
				
				size -= sizeOfPixel*0.125*numOctantsToRemove;
				if (lg.isInfoEnabled()) {
					lg.info("size=" + size);
				}
				break;
			}
		}
		
		VolumeGeometricRegion volumeRegion = new VolumeGeometricRegion(name,size,volumeUnit,subVolume,regionInfo.getRegionIndex());
		regionList.add(volumeRegion);
		if (lg.isInfoEnabled()) {
			lg.info("added volumeRegion("+volumeRegion.getName()+")");
		}
	}
	//
	// parse surfaceCollection into ResolvedMembraneLocations
	//
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
		cbit.vcell.geometry.surface.Surface surface = surfaceCollection.getSurfaces(i);
		int exteriorRegionIndex = surface.getExteriorRegionIndex();
		int interiorRegionIndex = surface.getInteriorRegionIndex();
		cbit.vcell.geometry.SubVolume surfaceExteriorSubvolume = geometrySpec.getSubVolume(regionInfos[exteriorRegionIndex].getPixelValue());
		cbit.vcell.geometry.SubVolume surfaceInteriorSubvolume = geometrySpec.getSubVolume(regionInfos[interiorRegionIndex].getPixelValue());
		String name = "membrane_"+surfaceInteriorSubvolume.getName()+interiorRegionIndex+"_"+surfaceExteriorSubvolume.getName()+exteriorRegionIndex;
		double correctedArea = surface.getArea();
		if (geometrySpec.getDimension()==2){
			correctedArea /= geometrySpec.getExtent().getZ();
		}else if (geometrySpec.getDimension()==1){
			correctedArea /= (geometrySpec.getExtent().getY()*geometrySpec.getExtent().getZ());
		}
			
		SurfaceGeometricRegion surfaceRegion = new SurfaceGeometricRegion(name,correctedArea,surfaceUnit);
		regionList.add(surfaceRegion);
		//
		// connect this surfaceLocation to its exterior and interior volumeLocations
		//
		VolumeGeometricRegion exteriorVolumeRegion = null;
		for (int j = 0; j < regionList.size(); j++){
			GeometricRegion region = regionList.elementAt(j);
			if (region instanceof VolumeGeometricRegion && region.getName().equals(surfaceExteriorSubvolume.getName()+exteriorRegionIndex)){
				exteriorVolumeRegion = (VolumeGeometricRegion)region;
			}
		}
		surfaceRegion.addAdjacentGeometricRegion(exteriorVolumeRegion);
		exteriorVolumeRegion.addAdjacentGeometricRegion(surfaceRegion);

		VolumeGeometricRegion interiorVolumeRegion = null;
		for (int j = 0; j < regionList.size(); j++){
			GeometricRegion region = regionList.elementAt(j);
			if (region instanceof VolumeGeometricRegion && region.getName().equals(surfaceInteriorSubvolume.getName()+interiorRegionIndex)){
				interiorVolumeRegion = (VolumeGeometricRegion)region;
			}
		}
		surfaceRegion.addAdjacentGeometricRegion(interiorVolumeRegion);
		interiorVolumeRegion.addAdjacentGeometricRegion(surfaceRegion);
		if (lg.isInfoEnabled()) {
			lg.info("added surfaceRegion("+surfaceRegion.getName()+")");
		}
	}
	
	return org.vcell.util.BeanUtils.getArray(regionList,GeometricRegion.class);
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 2:56:03 PM)
 * @return cbit.vcell.geometry.RegionImage
 * @param geoSurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
static cbit.vcell.geometry.RegionImage getUpdatedRegionImage(GeometrySurfaceDescription geoSurfaceDescription) throws cbit.vcell.geometry.GeometryException, cbit.image.ImageException, cbit.vcell.parser.ExpressionException {
	//
	// make new RegionImage if necessary missing or wrong size
	//
	cbit.vcell.geometry.GeometrySpec geometrySpec = geoSurfaceDescription.getGeometry().getGeometrySpec();
	if (geoSurfaceDescription.getRegionImage()==null || 
		geoSurfaceDescription.getFilterCutoffFrequency() != geoSurfaceDescription.getRegionImage().getFilterCutoffFrequency() ||
		geoSurfaceDescription.getRegionImage().getNumX()!=geoSurfaceDescription.getVolumeSampleSize().getX() ||
		geoSurfaceDescription.getRegionImage().getNumY()!=geoSurfaceDescription.getVolumeSampleSize().getY() ||	
		geoSurfaceDescription.getRegionImage().getNumZ()!=geoSurfaceDescription.getVolumeSampleSize().getZ()){
			
		cbit.image.VCImage image = geometrySpec.createSampledImage(geoSurfaceDescription.getVolumeSampleSize());
		cbit.vcell.geometry.RegionImage regionImage =
		new RegionImage(
				image,
				geoSurfaceDescription.getGeometry().getDimension(),
				geoSurfaceDescription.getGeometry().getExtent(),
				geoSurfaceDescription.getGeometry().getOrigin(),
				geoSurfaceDescription.getFilterCutoffFrequency().doubleValue()
				);
		return regionImage;
	}
	return geoSurfaceDescription.getRegionImage();
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 10:19:59 AM)
 */
public static void updateGeometricRegions(GeometrySurfaceDescription geoSurfaceDescription) throws cbit.vcell.geometry.GeometryException, cbit.image.ImageException, cbit.vcell.parser.ExpressionException, java.beans.PropertyVetoException {
	//
	// updates if necessary: regionImage, surfaceCollection and resolvedLocations[]
	//
	// assumes that volumeSampleSize and filterCutoffFrequency are already specified
	//
	//

	//
	// make new RegionImage if necessary missing or wrong size
	//
	boolean bChanged = false;
	cbit.vcell.geometry.RegionImage updatedRegionImage = getUpdatedRegionImage(geoSurfaceDescription);
	if (updatedRegionImage != geoSurfaceDescription.getRegionImage()){  // getUpdatedRegionImage returns same image if not changed
		bChanged = true;
	}

	//
	// parse regionImage into VolumeGeometricRegions and SurfaceCollection into SurfaceGeometricRegions
	//
	GeometricRegion updatedGeometricRegions[] = geoSurfaceDescription.getGeometricRegions();
	if (geoSurfaceDescription.getGeometricRegions()==null || bChanged){
		updatedGeometricRegions = getUpdatedGeometricRegions(geoSurfaceDescription,updatedRegionImage,updatedRegionImage.getSurfacecollection());
	}

	geoSurfaceDescription.setGeometricRegions(updatedGeometricRegions);	
}
}
