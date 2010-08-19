package org.vcell.smoldyn.converter;

import java.util.ArrayList;

import org.vcell.smoldyn.model.Boundaries;
import org.vcell.smoldyn.model.Participant.Product;
import org.vcell.smoldyn.model.Participant.Reactant;
import org.vcell.smoldyn.model.util.ReactionParticipants;
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
	
	/**
	 * What does this method do????
	 * Maybe it's supposed to just return a point that is inside the geometric region, but then again, but it's supposed to do something
	 * else entirely.
	 */
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
	
	static ReactionParticipants getReactionParticipants(ArrayList<Reactant> reactants, ArrayList<Product> products) {
		ReactionParticipants participants;
		if(reactants.size() == 0) {
			if(products.size() == 0) {
				throw new RuntimeException("reaction problem: reaction has neither reactants nor products");
			} else if(products.size() == 1) {
				participants = new ReactionParticipants(products.get(0));
			} else if(products.size() == 2) {
				participants = new ReactionParticipants(products.get(0), products.get(1));
			} else {
				throw new RuntimeException("reaction problem: too many products");
			}
		} else if (reactants.size() == 1) {
			if(products.size() == 0) {
				participants = new ReactionParticipants(reactants.get(0));
			} else if(products.size() == 1) {
				participants = new ReactionParticipants(reactants.get(0), products.get(0));				
			} else if(products.size() == 2) {
				participants = new ReactionParticipants(reactants.get(0), products.get(0), products.get(1));
			} else {
				throw new RuntimeException("reaction problem: too many products");
			}
		} else if (reactants.size() == 2) {
			if(products.size() == 0) {
				participants = new ReactionParticipants(reactants.get(0), reactants.get(1));
			} else if(products.size() == 1) {
				participants = new ReactionParticipants(reactants.get(0), reactants.get(1), products.get(0));
			} else if(products.size() == 2) {
				participants = new ReactionParticipants(reactants.get(0), reactants.get(1), products.get(0), products.get(1));
			} else {
				throw new RuntimeException("reaction problem: too many products");
			}
		} else {
			throw new RuntimeException("problem with reaction: too many reactants");
		}
		return participants;
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
