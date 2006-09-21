package cbit.vcell.geometry.surface;
import cbit.render.*;
import cbit.render.objects.Surface;
import cbit.render.objects.SurfaceCollection;
import cbit.render.objects.TaubinSmoothing;
import cbit.render.objects.TaubinSmoothingSpecification;
import cbit.render.objects.TaubinSmoothingWrong;
import cbit.util.StdoutSessionLog;
/**
 * Insert the type's description here.
 * Creation date: (6/28/2004 2:52:23 PM)
 * @author: Jim Schaff
 */
public class GeometrySurfaceUtils {
/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 3:09:13 PM)
 * @return cbit.vcell.geometry.surface.GeometricRegion[]
 * @param geoSurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 * @param surfaceCollection cbit.vcell.geometry.surface.SurfaceCollection
 */
static GeometricRegion[] getUpdatedGeometricRegions(GeometrySurfaceDescription geoSurfaceDescription, cbit.vcell.geometry.RegionImage regionImage, SurfaceCollection surfaceCollection) {
	//
	// parse regionImage into ResolvedVolumeLocations
	//
	double sizeOfPixel = 0;
	cbit.vcell.units.VCUnitDefinition volumeUnit = null;
	cbit.vcell.units.VCUnitDefinition surfaceUnit = null;
	cbit.vcell.geometry.GeometrySpec geometrySpec = geoSurfaceDescription.getGeometry().getGeometrySpec();
	switch (geometrySpec.getDimension()){
		case 1: {
			sizeOfPixel = geometrySpec.getExtent().getX()/(regionImage.getNumX()-1);
			//sizeOfPixel /= 9.0;  // to account for the padding from 1D to 3D
			volumeUnit = cbit.vcell.units.VCUnitDefinition.UNIT_um;
			surfaceUnit = cbit.vcell.units.VCUnitDefinition.UNIT_DIMENSIONLESS;
			break;
		}
		case 2: {
			sizeOfPixel= geometrySpec.getExtent().getX()/(regionImage.getNumX()-1) *
						 geometrySpec.getExtent().getY()/(regionImage.getNumY()-1);
			//sizeOfPixel /= 3.0;  // to account for the padding from 2D to 3D
			volumeUnit = cbit.vcell.units.VCUnitDefinition.UNIT_um2;
			surfaceUnit = cbit.vcell.units.VCUnitDefinition.UNIT_um;
			break;
		}
		case 3: {
			sizeOfPixel= geometrySpec.getExtent().getX()/(regionImage.getNumX()-1) *
						 geometrySpec.getExtent().getY()/(regionImage.getNumY()-1) *
						 geometrySpec.getExtent().getZ()/(regionImage.getNumZ()-1);
			volumeUnit = cbit.vcell.units.VCUnitDefinition.UNIT_um3;
			surfaceUnit = cbit.vcell.units.VCUnitDefinition.UNIT_um2;
			break;
		}
	}
	int numX = regionImage.getNumX();
	int numY = regionImage.getNumY();
	int numZ = regionImage.getNumZ();
	int numXY = numX*numY;
	int numXYZ = numX*numY*numZ;
	
	java.util.Vector regionList = new java.util.Vector();
	cbit.vcell.geometry.RegionImage.RegionInfo regionInfos[] = regionImage.getRegionInfos();
	for (int i = 0; i < regionInfos.length; i++){
		cbit.vcell.geometry.RegionImage.RegionInfo regionInfo = regionInfos[i];
		System.out.println(regionInfo);
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
				int yOffsetLastLine = (numY-1)*numX;
				int zOffsetLastSlice = (numZ-1)*numXY;
				for (int zIndex = 0; zIndex < numZ; zIndex++){
					for (int yIndex = 0; yIndex < numY; yIndex++){
						// -x side (including attached edges and corners)
						int totalOctants = 4; // already on face of boundary (removing half)
						//System.out.println("-x side including edges and corners");
						if (regionInfo.isIndexInRegion(yIndex * numX + zIndex * numXY)){ // (regionImage.getRegionIndex(0,yIndex,zIndex) == regionIndex){
							if (yIndex==0 || yIndex==numY-1){
								totalOctants /= 2;
							}
							if (zIndex==0 || zIndex==numZ-1){
								totalOctants /= 2;
							}
							numOctantsToRemove += (8-totalOctants);
						}
						//System.out.println("+x side including edges and corners");
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
						//System.out.println("-y side including edges");
						int totalOctants = 4; // already on face of boundary (removing half)
						if (regionInfo.isIndexInRegion(xIndex + zIndex * numXY)){ // (regionImage.getRegionIndex(xIndex,0,zIndex) == regionIndex){
							if (zIndex==0 || zIndex==numZ-1){
								totalOctants /= 2;
							}
							numOctantsToRemove += (8-totalOctants);
						}
						//System.out.println("+y side including edges");
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
						//System.out.println("-z side including nothing");
						if (regionInfo.isIndexInRegion(xIndex + yIndex * numX)){ // (regionImage.getRegionIndex(xIndex,yIndex,0) == regionIndex){
							numOctantsToRemove += (8-totalOctants);
						}
						// +z side (excluding all attached edges and corners)
						//System.out.println("+z side including nothing");
						if (regionInfo.isIndexInRegion(xIndex + yIndex * numX + (numZ - 1) * numXY)){ // (regionImage.getRegionIndex(xIndex,yIndex,numZ-1) == regionIndex){
							numOctantsToRemove += (8-totalOctants);
						}
					}
				}
				
				size -= sizeOfPixel*0.125*numOctantsToRemove;
				System.out.println("size=" + size);
				break;
			}
		}
		
		VolumeGeometricRegion volumeRegion = new VolumeGeometricRegion(name,size,volumeUnit,subVolume,regionInfo.getRegionIndex());
		regionList.add(volumeRegion);
		System.out.println("added volumeRegion("+volumeRegion.getName()+")");
	}
	//
	// parse surfaceCollection into ResolvedMembraneLocations
	//
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
		Surface surface = surfaceCollection.getSurfaces(i);
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
			GeometricRegion region = (GeometricRegion)regionList.elementAt(j);
			if (region instanceof VolumeGeometricRegion && region.getName().equals(surfaceExteriorSubvolume.getName()+exteriorRegionIndex)){
				exteriorVolumeRegion = (VolumeGeometricRegion)region;
			}
		}
		surfaceRegion.addAdjacentGeometricRegion(exteriorVolumeRegion);
		exteriorVolumeRegion.addAdjacentGeometricRegion(surfaceRegion);

		VolumeGeometricRegion interiorVolumeRegion = null;
		for (int j = 0; j < regionList.size(); j++){
			GeometricRegion region = (GeometricRegion)regionList.elementAt(j);
			if (region instanceof VolumeGeometricRegion && region.getName().equals(surfaceInteriorSubvolume.getName()+interiorRegionIndex)){
				interiorVolumeRegion = (VolumeGeometricRegion)region;
			}
		}
		surfaceRegion.addAdjacentGeometricRegion(interiorVolumeRegion);
		interiorVolumeRegion.addAdjacentGeometricRegion(surfaceRegion);
		System.out.println("added surfaceRegion("+surfaceRegion.getName()+")");
	}

	return (GeometricRegion[])cbit.util.BeanUtils.getArray(regionList,GeometricRegion.class);
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 2:56:03 PM)
 * @return cbit.vcell.geometry.RegionImage
 * @param geoSurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
static cbit.vcell.geometry.RegionImage getUpdatedRegionImage(GeometrySurfaceDescription geoSurfaceDescription) throws cbit.vcell.geometry.GeometryException, cbit.image.ImageException {
	//
	// make new RegionImage if necessary missing or wrong size
	//
	cbit.vcell.geometry.GeometrySpec geometrySpec = geoSurfaceDescription.getGeometry().getGeometrySpec();
	if (geoSurfaceDescription.getRegionImage()==null || 
		geoSurfaceDescription.getRegionImage().getNumX()!=geoSurfaceDescription.getVolumeSampleSize().getX() ||
		geoSurfaceDescription.getRegionImage().getNumY()!=geoSurfaceDescription.getVolumeSampleSize().getY() ||	
		geoSurfaceDescription.getRegionImage().getNumZ()!=geoSurfaceDescription.getVolumeSampleSize().getZ()){
			
		cbit.image.VCImage image = geometrySpec.createSampledImage(geoSurfaceDescription.getVolumeSampleSize());
		cbit.vcell.geometry.RegionImage regionImage = new cbit.vcell.geometry.RegionImage(image);
		return regionImage;
	}
	return geoSurfaceDescription.getRegionImage();
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 3:02:36 PM)
 * @return cbit.vcell.geometry.surface.SurfaceCollection
 */
static SurfaceCollection getUpdatedSurfaceCollection(GeometrySurfaceDescription geoSurfaceDescription, cbit.vcell.geometry.RegionImage regionImage) {
	//
	// make the surfaces (if necessary)
	//
	cbit.vcell.geometry.surface.SurfaceGenerator surfaceGenerator = new cbit.vcell.geometry.surface.SurfaceGenerator(new StdoutSessionLog("suface generator"));
	cbit.util.Extent extent = geoSurfaceDescription.getGeometry().getGeometrySpec().getExtent();
	cbit.util.Origin origin = geoSurfaceDescription.getGeometry().getGeometrySpec().getOrigin();
	SurfaceCollection surfaceCollection = surfaceGenerator.generateSurface(regionImage, 3, extent, origin);
	//
	// smooth surfaces
	//
	if (geoSurfaceDescription.getFilterCutoffFrequency().doubleValue()<0.6){
		TaubinSmoothing taubinSmoothing = new TaubinSmoothingWrong();
		TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.getInstance(geoSurfaceDescription.getFilterCutoffFrequency().doubleValue());
		taubinSmoothing.smooth(surfaceCollection,taubinSpec);
	}
	return surfaceCollection;
}


/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 10:19:59 AM)
 */
public static void updateGeometricRegions(GeometrySurfaceDescription geoSurfaceDescription) throws cbit.vcell.geometry.GeometryException, cbit.image.ImageException, java.beans.PropertyVetoException {
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
	// make the surfaces (if necessary)
	//
	SurfaceCollection updatedSurfaceCollection = geoSurfaceDescription.getSurfaceCollection();
	if (geoSurfaceDescription.getSurfaceCollection()==null || bChanged) {
		updatedSurfaceCollection = getUpdatedSurfaceCollection(geoSurfaceDescription,updatedRegionImage);
		bChanged = true;
	}

	//
	// parse regionImage into VolumeGeometricRegions and SurfaceCollection into SurfaceGeometricRegions
	//
	GeometricRegion updatedGeometricRegions[] = geoSurfaceDescription.getGeometricRegions();
	if (geoSurfaceDescription.getGeometricRegions()==null || bChanged){
		updatedGeometricRegions = getUpdatedGeometricRegions(geoSurfaceDescription,updatedRegionImage,updatedSurfaceCollection);
	}

	geoSurfaceDescription.setGeometricRegions(updatedGeometricRegions);	
}
}