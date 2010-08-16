package org.vcell.smoldyn.converter;

import org.vcell.smoldyn.model.Boundaries;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.Origin;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.surface.GeometricRegion;

/**
 * Contains all the methods from {@link SimulationJobToSmoldyn} that don't use the 'this' pointer, in order to simplify the class.
 * Hence, package-level visibility.
 * 
 * @author mfenwick
 *
 */
class ConversionUtilities {
	
	static Boundaries getBoundariesFromVCell(Geometry vcellgeometry) {
		Extent extent = vcellgeometry.getExtent();
		Origin origin = vcellgeometry.getOrigin();
		Boundaries boundaries = new Boundaries(origin.getX(), extent.getX() + origin.getX(), origin.getY(), extent.getY() + origin.getY(),
				origin.getZ(), extent.getZ() + origin.getZ());
		return boundaries;
	}
	
	static Coordinate getAnyCoordinate(Geometry geometry, GeometricRegion region){
		RegionImage regionImage = geometry.getGeometrySurfaceDescription().getRegionImage();
		int numX = regionImage.getNumX();
		int numY = regionImage.getNumY();
		int numZ = regionImage.getNumZ();
		double totalX = 0;
		double totalY = 0;
		double totalZ = 0;
		Origin origin = geometry.getOrigin();
		Extent extent = geometry.getExtent();
		int count = 0;
		GeometricRegion[] allRegions = geometry.getGeometrySurfaceDescription().getGeometricRegions();
		for (int indexX = 0; indexX < numX; indexX++){
			for (int indexY = 0; indexY < numY; indexY++){
				for (int indexZ = 0; indexZ < numZ; indexZ++){
					int offset = indexX + (numX)*indexY + numX*numY*indexZ;
					RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(offset);
					if (allRegions[regionInfo.getRegionIndex()].equals(region)){
						switch (geometry.getDimension()){
						case 2: {
							totalX += origin.getX()+(indexX*extent.getX()/(numX-1));
							totalY += origin.getY()+(indexY*extent.getY()/(numY-1));
							totalZ += origin.getZ()+extent.getZ()/2;
							break;
						}
						case 3: {
							totalX += origin.getX()+(indexX*extent.getX()/(numX-1));
							totalY += origin.getY()+(indexY*extent.getY()/(numY-1));
							totalZ += origin.getZ()+(indexZ*extent.getZ()/(numZ-1));
							break;
						}
						default:
							throw new RuntimeException("unsupported geometry dimension");
						}
						count++;
					}
				}
			}
		}
		if (count==0){
			throw new RuntimeException("cant find region "+region.getName()+" in geometry");
		}
		return new Coordinate(totalX/count, totalY/count, totalZ/count);
	}

	
	static double getBoxsize(int numberofvcellboxes, double extent) {
		return extent / (numberofvcellboxes - 1);//subtract 1 because vcell has two half boxes
	}
	
	
	static void throwRuntimeException(String message) {
		throw new RuntimeException(message);
	}
	
	static void printWarning(String message) {
		System.err.println("converttest warning: " + message);
	}
	
	static void printDebuggingStatement(String message) {
		System.out.println("converttest debugging statement: " + message);
	}
}
