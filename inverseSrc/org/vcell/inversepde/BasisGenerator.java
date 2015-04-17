/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde;


import org.vcell.util.Extent;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.RegionImage.RegionInfo;

	public class BasisGenerator {
		
    	static final int MAX_RCOMPARTMENTS = 200;	// max # of compartments in a region, assumed when using VCImage and RegionImage ... (see makeContiguous()).
    	static final int MAX_REGIONS = 50;
    	static final int MIN_PIXELS_IN_COMPARTMENT = 9;	// min # of pixels in a compartment; we should merge smaller compartments

		float threshold;			// threshold for particle detection 
		boolean normalized = false;

		private float percentile = 0.03F; 	// (user input/100) energy level per compartment (ex each compartment has 3% of total energy)
		private int levels = 4; 			// number of segments; more separate regions may belong to same segment

		private final FloatImage originalImage;		// the original image
		private float globalMin, globalMax;
		
		// highest compartmentId in use; any newly generated id must be higher
		// use this only after the isoVolumeDetection phase
    	private int maxCompartmentId;		
    	private int numCompartments;		// number of compartments
		
		public BasisGenerator (FloatImage originalImage, float perc, int lev) {
			this.originalImage = originalImage;
			percentile = perc;
			levels = lev;
			dataRestoration();
			computeStatistics();
			maxCompartmentId = 1;		// 0 is reserved for points outside the region of interest
			numCompartments = 0;
		}
		
		// correct against rogue negative pixels - sometimes a common occurence
		// pixels of value 0 are masked out, we assign to negative pixels a minimal non-zero value
		private void dataRestoration() {
			float[] pixels = originalImage.getPixels();
			for(int i=0; i<pixels.length; i++) {
				if(pixels[i] < 0) {
					pixels[i] = Float.MIN_VALUE;
				}
			}
		}
		private void computeStatistics() {
			float[] pixels = originalImage.getPixels();
			globalMin = pixels[0];
			globalMax = pixels[0];
			for(int i=1; i<pixels.length; i++) {
				if(pixels[i] > globalMax) {
					globalMax = pixels[i];
					continue;	// no chance for the other evaluation
				}
				if(pixels[i] < globalMin) {
					globalMin = pixels[i];
				}
			}
		}

		/**
		 * Main phase of the algorithm - time and memory consuming !!
		 * 
		 * levels - number of iso-volumes
		 * percentile - how much of total energy per compartment
		 */
		public int[] isoVolumesDetection () {		
			// threshold original image
			// we assume that all the pixels with value of 0 are masked out and that
			//   the minimal value inside the field is Float.MIN_VALUE
			float interval = globalMax / levels;
			float threshold[] = new float[levels];
			for(int i=0; i<levels; i++) {
				threshold[i] = interval * (i+1);
			}
			// slightly increase highest threshold, otherwise we'll miss globalMax pixels
			threshold[levels-1] += Float.MIN_VALUE;

			// separate image in segments (isosurfaces) based on the thresholding levels
			float[] pixels = originalImage.getPixels();
			byte[] values = new byte[pixels.length];
			for(int i=0; i<pixels.length; i++) {
				for (int j=0; j<levels; j++) {
					if(pixels[i] < threshold[j]) {
						values[i] = (byte) j;
						break;
					}
				}
			}
			VCImage vcImage = null;
			RegionImage regionImage = null;
			try {
				// separate segments with discontinuities into regions
				// we can have more than 1 regions for the same segment
				vcImage = new VCImageUncompressed(null,values,new Extent(1,1,1),originalImage.getNumX(),originalImage.getNumY(),originalImage.getNumZ());
				int numDimensions;
				switch(originalImage.getNumZ()) {
				case 0:
				case 1:
					numDimensions = 2;
					break;
				default:
					numDimensions = 3;
					break;
				}
				regionImage = new RegionImage(vcImage, numDimensions, new Extent(1,1,1),new Origin(0,0,0),0.5);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ImageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0; i<values.length; i++) {
				RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(i);
				int regionIndex = regionInfo.getRegionIndex();
				values[i] = (byte)(50+regionIndex);
			}
		
			// compute statistics and find a seed for each region
			int numRegions = regionImage.getNumRegions();
			RegionStatistics[] regionStatistics = new RegionStatistics[numRegions];
			for(int i=0; i<numRegions; i++) {
				regionStatistics[i] = new RegionStatistics();
			}
			for(int i=0; i<values.length; i++) {
				RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(i);
				int regionIndex = regionInfo.getRegionIndex();
				regionStatistics[regionIndex].intensity += pixels[i];
				regionStatistics[regionIndex].numPixels++;
				if(regionStatistics[regionIndex].seedIndex == 0) {
					regionStatistics[regionIndex].seedIndex = i;		// found a seed for this region
				}
			}
			// more statistics: # of compartments per region
			float totalIntensity = 0;		// normalized intensity of whole image
			for(int i=0; i<numRegions;i++) {
				totalIntensity += regionStatistics[i].intensity;
			}
	    	float averageCompartmentIntensity = totalIntensity*percentile;	// average intensity per compartment
			for(int i=0; i<numRegions;i++) {			// compute number of compartments for each region
				regionStatistics[i].numProjectedCompartments = (int)(regionStatistics[i].intensity / averageCompartmentIntensity);
				if(regionStatistics[i].numProjectedCompartments == 0) {		// round up so that each region has at least 1 compartment
					regionStatistics[i].numProjectedCompartments = 1;
				}
			}
			System.out.println(totalIntensity + " Intensity in " + numRegions + " regions");
			for(int i=0;i<numRegions;i++) {
				System.out.println("Region " + i + ": " + regionStatistics[i].numProjectedCompartments + " compartments of " +
						regionStatistics[i].numPixels + " pixels each.");
			}
			
			// compute a mask for each region
			// we'll use it and the seed to grow compartments
			int[] cValues = new int[pixels.length];	// final compartments here, will build it as we go
			for(int i=0;i<numRegions;i++) {
				System.out.println("Processing region " + i + "   =================== pass 1");
				for(int j=0; j<values.length; j++) {
					RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(j);
					int regionIndex = regionInfo.getRegionIndex();
					if(i == regionIndex) {
						values[j] = 100;
					} else {
						values[j] = 0;
					}
				}
// -------------------				
				int currRegion = i;
				int projectedCompartmentPixels = (int) (regionStatistics[currRegion].numPixels / (double)regionStatistics[currRegion].numProjectedCompartments);
				int seedIndex = regionStatistics[currRegion].seedIndex;
				for(regionStatistics[currRegion].numActualCompartments=0; seedIndex!=0; seedIndex = getNextSeed(values)) {	// as long as seeds still exist
					int seedX = getX(seedIndex);
					int seedY = getY(seedIndex);
					int seedZ = getZ(seedIndex);
					int sumX = 0;		// sum of each pixel location in this compartment - used to compute centroid of compartment
					int sumY = 0;
					int sumZ = 0;
					int k,l,m;
					int x,y,z;	// coord of pixed to be added to current compartment
					int pixelsInCompartment = 0;	// we stop when we reach the # we estimated in RegionStatistics
					regionStatistics[currRegion].numActualCompartments++;		// check against max allowed and raise exception
					regionStatistics[currRegion].compartments[regionStatistics[currRegion].numActualCompartments-1] = new RegionCompartment();

					System.out.println(" --- Compartment " + (regionStatistics[currRegion].numActualCompartments-1));
					System.out.println("     Seed " + seedX + ", " + seedY);
				
					for(int radius =2; radius<100; radius++) {	// we look around the seed on a progressively wider radius
						for(k = -radius; k <= radius && pixelsInCompartment < projectedCompartmentPixels; k++) {
							if((seedX + k) < 0 || (seedX + k) >= originalImage.getNumX())
								continue;		// out of image bounds
							x = seedX + k;

							for(l = -radius; l <= radius && pixelsInCompartment < projectedCompartmentPixels; l++) {
								if((seedY + l) < 0 || (seedY + l) >= originalImage.getNumY())
									continue;	// out of image bounds
								y = seedY + l;
								
								for(m = -radius; m <= radius && pixelsInCompartment < projectedCompartmentPixels; m++) {
									if((seedZ + m) < 0 || (seedZ + m) >= originalImage.getNumZ())
										continue;	// out of image bounds
									z = seedZ + m;
								
									if(k*k + l*l + m*m > radius*radius*radius) {
										continue;	// point outside sphere
									}
									if(values[coord(x,y,z)] != 0) {		// pixel is inside region mask
										values[coord(x,y,z)] = 0;		// we "consume" the mask as we allocate pixels
										sumX += x;
										sumY += y;
										sumZ += z;
										pixelsInCompartment++;
									}
								}
							}
						}
					}
					if(pixelsInCompartment < 1) {
						throw new RuntimeException("No pixels available for compartment " + (regionStatistics[currRegion].numActualCompartments-1));
					}
					else {
						System.out.println("     pixels in compartment: " + pixelsInCompartment);
					}
					regionStatistics[currRegion].compartments[regionStatistics[currRegion].numActualCompartments-1].numPixels = pixelsInCompartment;
					sumX = sumX / pixelsInCompartment;
					sumY = sumY / pixelsInCompartment;
					sumZ = sumZ / pixelsInCompartment;
					regionStatistics[currRegion].compartments[regionStatistics[currRegion].numActualCompartments-1].centroidIndex = coord(sumX, sumY, sumZ);
				}	// end of 1st pass for this region
				
				System.out.println("Processing region " + i + "   =================== pass 2");
				for(int j=0; j<values.length; j++) {
					RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(j);
					int regionIndex = regionInfo.getRegionIndex();
					if(i == regionIndex) {
						values[j] = 100;
					} else {
						values[j] = 0;
					}
				}				
				for(int k=0; k<values.length; k++) {	// find closest centroid for each pixel
					if(values[k] == 0) {
						continue;			// pixel outside this region
					}
					int x = getX(k);	// pixel coordonates, used to compute dist between this pixel and each centroid
					int y = getY(k);
					int z = getZ(k);
					if(coord(x, y, z) == -1) {		// sanity check 1
						throw new RuntimeException("Coordinates out of bounds.");
					}
					else if(k != coord(x, y, z)) {		// sanity check 2
						throw new RuntimeException("Coordinates incompatible with index.");
					}
					int closestCompartmentIndex = 0;				// index of closest centroid
					int distanceToClosest = originalImage.getNumX()*originalImage.getNumX() + originalImage.getNumY()*originalImage.getNumY() + originalImage.getNumZ()*originalImage.getNumZ();	// computing distance to closest centroid
					for(int c=0; c<regionStatistics[currRegion].numActualCompartments; c++ ) {
						int xC = getX(regionStatistics[currRegion].compartments[c].centroidIndex);
						int yC = getY(regionStatistics[currRegion].compartments[c].centroidIndex);
						int zC = getZ(regionStatistics[currRegion].compartments[c].centroidIndex);
						int d = (xC-x)*(xC-x) + (yC-y)*(yC-y) + (zC-z)*(zC-z);
						if(d < distanceToClosest) {
							distanceToClosest = d;
							closestCompartmentIndex = c;
						}
					}
					// all pixels belonging to a compartment should have a unique nonzero value (id)
					// the id of zero is reserved for masked pixels, we'll deal with them during the postprocessing phase below
					int compartmentId = (int)(1+closestCompartmentIndex+MAX_RCOMPARTMENTS*currRegion);
					cValues[k] = compartmentId;
					if(maxCompartmentId < compartmentId) {
						maxCompartmentId = compartmentId;
					}
					
				}
			}
			// masked pixels have intensity of zero, we assign to all of them an id of zero
			for(int i=0; i<pixels.length; i++) {
				if(pixels[i] == 0) {
					cValues[i] = 0;
				}
			}
			postProcessCompartments(cValues);
			return cValues;
		}
		
		// sanity check, compartment corrections
		public int[] postProcessCompartments(int[] cValues) {
			orphanDetection(cValues);
			makeContigous(cValues);
			mergeSmall(cValues);
			computeCompartmentStatistics(cValues);
			
			return cValues;
		}
		
		private void mergeSmall(int[] cValues) {
			CompartmentStatistics[] cs = computeCompartmentStatistics(cValues);
			
			// we parse the statistics list looking for small compartments
			for(int i=0; i<numCompartments; i++) {
				if(cs[i].compartmentNumPixels >= MIN_PIXELS_IN_COMPARTMENT) {
					continue;	// this compartment is large enough, goto next
				}
				// it seems that this compartment is small based on precalculation
				// but we cannot be sure, maybe we merged something in it and it got larger
				int count = 0;
				for(int j=0; j<cValues.length; j++) {
					if(cValues[j] == cs[i].compartmentId) {
						count++;
					}
				}
				if(count >= MIN_PIXELS_IN_COMPARTMENT) {
					continue;	// this apparently small compartment was the beneficiary of a merge and now is large enough
				}
				
				// found a small one; get one of its pixels and look for adjacent pixels belonging to another compartment
				int candidateIndex = -1;  // index of an adjacent pixel which may belong to another compartment
				boolean found = false;
				for(int j=0; j<cValues.length; j++) {
					if(cValues[j] != cs[i].compartmentId) {
						continue;  // this pixel belongs to another compartment
					}
					// found a pixel belonging to the small compartment; let's try to find a neighbouring compartment
					int x = getX(j);
					int y = getY(j);
					int z = getZ(j);
					
					candidateIndex = coord(x+1, y, z);	// -1 if out of range
					if((candidateIndex!=-1) && (cValues[j] != cValues[candidateIndex]) && (cValues[candidateIndex] != 0)) {
						found = true;
						break;		// found a neighbouring compartment
					}
					candidateIndex = coord(x-1, y, z);
					if((candidateIndex!=-1) && (cValues[j] != cValues[candidateIndex]) && (cValues[candidateIndex] != 0)) {
						found = true;
						break;
					}
					candidateIndex = coord(x, y+1, z);
					if((candidateIndex!=-1) && (cValues[j] != cValues[candidateIndex]) && (cValues[candidateIndex] != 0)) {
						found = true;
						break;
					}
					candidateIndex = coord(x, y-1, z);
					if((candidateIndex!=-1) && (cValues[j] != cValues[candidateIndex]) && (cValues[candidateIndex] != 0)) {
						found = true;
						break;
					}
					candidateIndex = coord(x, y, z+1);
					if((candidateIndex!=-1) && (cValues[j] != cValues[candidateIndex]) && (cValues[candidateIndex] != 0)) {
						found = true;
						break;
					}
					candidateIndex = coord(x, y, z-1);
					if((candidateIndex!=-1) && (cValues[j] != cValues[candidateIndex]) && (cValues[candidateIndex] != 0)) {
						found = true;
						break;
					}
				}
				if(found == false) {
					System.out.println("WARNING: Unable to find neighbours for small compartment with Id = " + cs[i].compartmentId);
				}
				else {				// assign all pixels of the small compartment to the neighbour found above
					System.out.println("Merging compartment " + cs[i].compartmentId + " into compartment " + cValues[candidateIndex]);
					for(int j=0; j<cValues.length; j++) {
						if(cValues[j] != cs[i].compartmentId) {
							continue;  // this pixel doesn't need merging
						}
						cValues[j] = cValues[candidateIndex];
					}
				}
			}
		}
		
		private void makeContigous(int[] cValues) {
		
			VCImage vcImage = null;
			RegionImage regionImage = null;
			
			byte[] bPixels = new byte[cValues.length];
			for (int i = 0; i < bPixels.length; i++) {
				if (cValues[i] < 0 || cValues[i] > 255){
					throw new RuntimeException("");
				}
			}
			// separate compartments with discontinuities into separate compartments
			try {
				vcImage = new VCImageUncompressed(null,cValues, new Extent(1,1,1), originalImage.getNumX(),originalImage.getNumY(),originalImage.getNumZ());

				int numDimensions;
				switch(originalImage.getNumZ()) {
				case 0:
				case 1:
					numDimensions = 2;
					break;
				default:
					numDimensions = 3;
					break;
				}
				regionImage = new RegionImage(vcImage, numDimensions, new Extent(1,1,1),new Origin(0,0,0),0.5);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ImageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			float[] pixels = originalImage.getPixels();
			for(int i=0; i<cValues.length; i++) {
				RegionImage.RegionInfo regionInfo = regionImage.getRegionInfoFromOffset(i);
				int regionIndex = 1+regionInfo.getRegionIndex();	// 0 is reserved
				
				if(pixels[i] == 0) {
					cValues[i] = 0;		// keep masked out pixels with ID of 0
				} else {
					cValues[i] = regionIndex;
				}
				if(maxCompartmentId < regionIndex) {
					maxCompartmentId = regionIndex;
				}
			}
			System.out.println("maxCompartmentId = " + maxCompartmentId);
		}
		
		// looking for pixels which are part of the field but have compartmentId of 0
		// ideally there shouldn't be any
		private void orphanDetection(int[] cValues) {
			float[] pixels = originalImage.getPixels();
			int orphansFound = 0;
			for(int i=0; i<cValues.length; i++) {
				if((cValues[i] == 0) && (pixels[i] > 0)) {
				// found orphan - compId is 0 but pixels[] value says it's part of the field
					orphansFound++;
				}
			}
			
			if(orphansFound > 0) {
				System.out.println("WARNING: Found " + orphansFound + " orphaned pixels.");
			} else {
				System.out.println("No orphaned pixels found. This is good!");
			}
		}
		
		private CompartmentStatistics[] computeCompartmentStatistics(int[] cValues) {
			CompartmentStatistics[] cs = new CompartmentStatistics[MAX_REGIONS * MAX_RCOMPARTMENTS];
			for(int i=0; i< (MAX_REGIONS * MAX_RCOMPARTMENTS); i++) {
				cs[i] = new CompartmentStatistics();
			}

			numCompartments = 0;
			int numMaskedPixels = 0;
			
			// make a list with all unique IDs (each compartment has its own ID)
			for(int i=0; i<cValues.length; i++) {
				int id = cValues[i];
				if(id == 0) {
					numMaskedPixels++;
					continue;	// pixels with id of 0 are masked out, we don't deal with them
				}
				boolean error = true;		// sanity check - remains true if we don't stop early the following for loop
				for (int j=0; j<cs.length; j++) {
// TODO: cache the id and table index for previous pixel - the next it's likely to have the same id 
					// -> we'll know the index without searching within all the table
					if(cs[j].compartmentId == 0) {	// id was not found in list, we add it here (first empty spot in list of IDs)
						cs[j].compartmentId = id;		// new ID added to the list
						cs[j].compartmentNumPixels++;
						numCompartments++;	// we just found the ID of a new compartment
						error = false;
						break;
					}
					if(cs[j].compartmentId == id) {	// id already present in list, we just increment pixel counter
						cs[j].compartmentNumPixels++;
						error = false;
						break;
					}
				}
				if (error == true) {	// we should always exit early from the previous for loop
					throw new RuntimeException("Medoid computation failure: Improper ID table.");
				}
			}
			System.out.println("Masked " + numMaskedPixels + " pixels out of " + cValues.length + " pixels.");
			System.out.println("Found " + numCompartments + " compartment(s).");
			
			return cs;
		}
		
		
		
		// finds the medoid of each compartment
		public Medoid[] findMedoids(int[] cValues, int distanceType) {
			// compute statistics anew
			CompartmentStatistics[] cs = computeCompartmentStatistics(cValues);
			// find the medoid for each compartment
			Medoid[] medoid = new Medoid[numCompartments];
			for(int compartmentIndex=0; compartmentIndex<numCompartments; compartmentIndex++) {			// iterate for each compartment
				int id = cs[compartmentIndex].compartmentId;
				System.out.println("finding medoid for compartment "+compartmentIndex+", id="+id+", number of pixels = "+cs[compartmentIndex].compartmentNumPixels);
				float minDistance = Float.MAX_VALUE;
				medoid[compartmentIndex] = new Medoid();
				medoid[compartmentIndex].compartmentId = id;

				int[] compartmentIndices = new int[cs[compartmentIndex].compartmentNumPixels];
				int pixelCount = 0;
				for (int p=0; p<cValues.length; p++) {
					if (cValues[p] == id){
						compartmentIndices[pixelCount++] = p;
					}
				}
				if (pixelCount != cs[compartmentIndex].compartmentNumPixels){
					throw new RuntimeException("number of pixels in compartment "+compartmentIndex+" doesn't match compNumPixels");
				}
				
				//
				// this is a hack for now to not bother finding the medoid of a very very large element
				//
				if (cs[compartmentIndex].compartmentNumPixels > 10000){
					medoid[compartmentIndex].index = compartmentIndices[0];
					continue;
				}

				// compare each pixel with all the others (within the same compartment) and see which is the medoid
				for (int j=0; j<pixelCount; j++) {
					// compute the distance to the other pixels in compartment;
					int pixelIndex = compartmentIndices[j];
					int candidateX = getX(pixelIndex);
					int candidateY = getY(pixelIndex);
					int candidateZ = getZ(pixelIndex);
					float candidateDistance = 0;
					for(int k=0; k<pixelCount; k++) {
						int pixelIndex2 = compartmentIndices[k];
						int x = getX(pixelIndex2);
						int y = getY(pixelIndex2);
						int z = getZ(pixelIndex2);
						switch(distanceType) {
// TODO: compute medoids based on distances AND field intensity
// TODO: compute max width, height, length (min doesn't make sense)
// TODO: compute radius of inscribed circle (for each point, radius is distance to closest margin)(then find out which point has largest radius)
						case Medoid.Euclidian:
							candidateDistance += (candidateX-x)*(candidateX-x) + (candidateY-y)*(candidateY-y) + (candidateZ-z)*(candidateZ-z);
							break;
						case Medoid.Manhattan:
						default:
							candidateDistance += Math.abs(candidateX-x) + Math.abs(candidateY-y) + Math.abs(candidateZ-z);
							break;
						}
					}
					if(minDistance > candidateDistance) {
						// current pixel becomes the new medoid for this compartment
						minDistance = candidateDistance;
						medoid[compartmentIndex].index = pixelIndex;
					}
				}
			}
			return medoid;
		}

		private int getX(int position) {
			return (int)(position % originalImage.getNumX());
		}
		private int getY(int position) {
			return (int)((position % (originalImage.getNumX()*originalImage.getNumY())) / originalImage.getNumX());
		}
		private int getZ(int position) {
			return (int)(position / (originalImage.getNumX() * originalImage.getNumY()));
		}
		private int coord (int x, int y, int z) {
			if(x<0 || y<0 || z<0) {
				return -1;		// out of bounds
			}
			if((x>=originalImage.getNumX()) || (y>=originalImage.getNumY()) || ((z>=originalImage.getNumZ()) && (originalImage.getNumZ()>0))) {
				return -1;		// out of bounds
			}
			return ((y*originalImage.getNumX()) + (x) + (z*originalImage.getNumX()*originalImage.getNumY()));
		}
		private int getNextSeed(byte[] values) {
			for(int i=1; i<values.length; i++) {	// we avoid the pixel at 0.0 as seed
				if(values[i] != 0) {
					return i;
				}
			}
			return 0;
		}
		
		// -------------------------------------------------------------------------------------
		
		// statistics about each compartment
		// used to detect / correct shape abberations
		public class CompartmentStatistics {
			int compartmentId;
			int compartmentNumPixels;
		}
		
		// medoid: pixel whose average dissimilarity to all the pixels in the compartment is minimal
		// (i.e. it is the most centrally located point in the given compartment)
		public class Medoid {
			public static final int Euclidian = 0;
			public static final int Manhattan = 1;
			
			int compartmentId;		// exach pixel belonging to a compartment has a unique value;
			int index;				// pixel position in original array (field)
			public int getIndex() {
				return index;
			}
			public int getCompartmentId() {
				return compartmentId;
			}
		}

	    private class RegionStatistics {
	    	float intensity;			// sum of all pixel intensities in the region
	    	double numPixels;			// number of pixels in the region;
	    	int seedIndex;				// location of seed
	    	// derived values
	    	int numProjectedCompartments;		// projected number of compartments - may be different than 
	    										// actual number due to fragmentation
	    	int numActualCompartments;
	    	RegionCompartment[] compartments;
	    	
	    	RegionStatistics() {
	    		intensity = 0;
	    		numPixels = 0;
	    		seedIndex = 0;
	    		numProjectedCompartments = 0;
	    		numActualCompartments = 0;
	    		compartments = new RegionCompartment[MAX_RCOMPARTMENTS];
	    	}
	    }
	    // compartments belonging to the same region
	    private class RegionCompartment {
	    	float intensity;			// m0 - sum of each pixel intensity (normalized) 
	    	double numPixels;			// number of pixels in compartment
	    	int centroidIndex;			// index of compartment centroid
	    }

		/**
		 * Normalizes a given <code>ImageProcessor</code> to [0,1].
		 * <br>According to the pre determend global min and max pixel value in the series.
		 * <br>All pixel intensity values I are normalized as (I-gMin)/(gMax-gMin)
		 * @param ip ImageProcessor to be normalized
		 */
		private void normalizeFrameFloat(float[] pixels) {
			
			float tmp_pix_value;
			for (int i = 0; i < pixels.length; i++) {
				tmp_pix_value = (pixels[i]-globalMin)/(globalMax - globalMin);
				pixels[i] = (float)(tmp_pix_value);
			}
		}
		
	    /* Interpretation of spatial moments **
           
	     * order 0  = TOTAL MASS [units: concentration, density, etc.]
	     * order 1  = location of CENTRE OF MASS in x and y from 0,0 [units: L]
	     * order 2  = VARIANCE (spreading) around centroid in x and y [units: L^2]
         *
		private void pointLocationsRefinement(ImageProcessor ip) {
			
			int m, k, l, x, y, tx, ty;
			float epsx, epsy;
			float c;	// intensity on which we apply a circular mask
			
			int mask_width = 2 * radius +1;
			
			/* Set every value that ist smaller than 0 to 0		
			for (int i = 0; i < ip.getHeight(); i++) {
				for (int j = 0; j < ip.getWidth(); j++) {
					if(ip.getPixelValue(j, i) < 0.0)
						ip.putPixelValue(j, i, 0.0);
				}
			}
				
			/* Loop over all particles
			for(m = 0; m < this.particles.length; m++) {
				System.out.println("----------------- particle " + m);
				this.particles[m].special = true;
				this.particles[m].score = 0.0F;
				epsx = epsy = 1.0F;

				while (epsx > 0.5 || epsx < -0.5 || epsy > 0.5 || epsy < -0.5) {
					this.particles[m].m0 = 0.0F;
					this.particles[m].m2 = 0.0F;
					epsx = 0.0F;
					epsy = 0.0F;
								
					// compute the moments
					for(k = -radius; k <= radius; k++) {
						if(((int)this.particles[m].x + k) < 0 || ((int)this.particles[m].x + k) >= ip.getHeight())
							continue;		// out of image bounds
						x = (int)this.particles[m].x + k;

						for(l = -radius; l <= radius; l++) {
							if(((int)this.particles[m].y + l) < 0 || ((int)this.particles[m].y + l) >= ip.getWidth())
								continue;	// out of image bounds
							y = (int)this.particles[m].y + l;

							c = ip.getPixelValue(y, x) * (float)mask[coord(k + radius, l + radius, mask_width)];	// circular mask
							if(c > this.particles[m].brightestPixel) {
								this.particles[m].brightestPixel = c;		// locate the brightest pixel
							}
							this.particles[m].m0 += c;
							epsx += (float)k * c;
							epsy += (float)l * c;
							this.particles[m].m2 += (float)(k * k + l * l) * c;
						}
					}

					epsx /= this.particles[m].m0;
					epsy /= this.particles[m].m0;
					this.particles[m].m2 /= this.particles[m].m0;	// the m2 based on the circle
		    
					// This is a little hack to avoid numerical inaccuracy
					tx = (int)(10.0 * epsx);
					ty = (int)(10.0 * epsy);

					if((float)(tx)/10.0 > 0.5) {
						if((int)this.particles[m].x + 1 < ip.getHeight())
							this.particles[m].x++;
					}
					else if((float)(tx)/10.0 < -0.5) {
						if((int)this.particles[m].x - 1 >= 0)
							this.particles[m].x--;						
					}
					if((float)(ty)/10.0 > 0.5) {
						if((int)this.particles[m].y + 1 < ip.getWidth())
							this.particles[m].y++;
					}
					else if((float)(ty)/10.0 < -0.5) {
						if((int)this.particles[m].y - 1 >= 0)
							this.particles[m].y--;
					}

					if((float)(tx)/10.0 <= 0.5 && (float)(tx)/10.0 >= -0.5 && (float)(ty)/10.0 <= 0.5 && (float)(ty)/10.0 >= -0.5)
						break;
				}
				this.particles[m].x += epsx;
				this.particles[m].y += epsy;
			}	
		}
*/
/*		private void findThreshold(ImageProcessor ip, double percent) {
			
			int i, j, thold;			
			
			/* find this ImageProcessors min and max pixel value
			ImageStatistics stats = ImageStatistics.getStatistics(ip, MIN_MAX, null);
			float min = (float)stats.min;
			float max = (float)stats.max;

			double[] hist = new double[256];
			for (i = 0; i< hist.length; i++) {
				hist[i] = 0;
			}

			for(i = 0; i < ip.getHeight(); i++) {
				for(j = 0; j < ip.getWidth(); j++) {
					hist[(int)((ip.getPixelValue(j, i) - min) * 255.0 / (max - min))]++;
				}
			}				

			for(i = 254; i >= 0; i--)
				hist[i] += hist[i + 1];

			thold = 0;
			while(hist[255 - thold] / hist[0] < percent) {
				thold++;	
				if(thold > 255)
					break;				
			}
			thold = 255 - thold + 1;
			this.threshold = ((float)(thold / 255.0) * (max - min) + min);			
		}
*/		
		/*
		 * Dilates a copy of a given ImageProcessor with a pre calculated <code>mask</code>.
		 * Adapted as is from Ingo Oppermann implementation
		 * @param ip ImageProcessor to do the dilation with
		 * @return the dilated copy of the given <code>ImageProcessor</code> 
		 * @see Segmentation3D_#mask
		 *
		private ImageProcessor dilateGeneric(ImageProcessor ip) {
			
			float[] input= (float[])ip.getPixels();
			ImageProcessor dilated_ip = ip.duplicate();
			float[] dilated= (float[])dilated_ip.getPixels();
			
			int i, j, k, l, m, x, y;
			float h;
			int kernel_width = (getRadius()*2) + 1;
			// upper bound and lower bound
			for(j = 0; j < ip.getWidth(); j++) {
				// upper bound
				for(i = 0; i < getRadius(); i++) {
					h = input[coord(i, j, ip.getWidth())];
					for(k = -getRadius(); k <= getRadius(); k++) {
						if((i + k) < 0)
							continue;
						else
							x = i + k;
						for(l = -getRadius(); l <= getRadius(); l++) {
							if(mask[coord(k + getRadius(), l + getRadius(), kernel_width)] == 0)
								continue;
							if((j + l) < 0 || (j + l) >= ip.getWidth())
								continue;
							else
								y = j + l;
							if(input[coord(x, y, ip.getWidth())] > h)
								h = input[coord(x, y, ip.getWidth())];
						}
					}
					dilated[coord(i, j, ip.getWidth())] = h;
				}

				// lower bound
				for(i = (ip.getHeight()- getRadius()); i < ip.getHeight(); i++) {
					h = input[coord(i, j, ip.getWidth())];
					for(k = -getRadius(); k <= getRadius(); k++) {
						if((i + k) >= ip.getHeight())
							continue;
						else
							x = i + k;
						for(l = -getRadius(); l <= getRadius(); l++) {
							if(mask[coord(k + getRadius(), l + getRadius(), kernel_width)] == 0)
								continue;
							if((j + l) < 0 || (j + l) >= ip.getWidth())
								continue;
							else
								y = j + l;
							if(input[coord(x, y, ip.getWidth())] > h)
								h = input[coord(x, y, ip.getWidth())];
						}
					}
					dilated[coord(i, j, ip.getWidth())] = h;
				}
			}
			// left bound and right bound
			for(i = getRadius(); i < (ip.getHeight()- getRadius()); i++) {
				// left bound
				for(j = 0; j < getRadius(); j++) {
					h = input[coord(i, j, ip.getWidth())];
					for(k = -getRadius(); k <= getRadius(); k++) {
						x = i + k;
						for(l = -getRadius(); l <= getRadius(); l++) {
							if(mask[coord(k + getRadius(), l + getRadius(), kernel_width)] == 0)
								continue;
							if((j + l) < 0)
								continue;
							else
								y = j + l;
							if(input[coord(x, y, ip.getWidth())] > h)
								h = input[coord(x, y, ip.getWidth())];
						}
					}
					dilated[coord(i, j, ip.getWidth())] = h;
				}
				// right bound
				for(j = (ip.getWidth() - getRadius()); j < ip.getWidth(); j++) {
					h = input[coord(i, j, ip.getWidth())];
					for(k = -getRadius(); k <= getRadius(); k++) {
						x = i + k;
						for(l = -getRadius(); l <= getRadius(); l++) {
							if(mask[coord(k + getRadius(), l + getRadius(), kernel_width)] == 0)
								continue;
							if((j + l) >= ip.getWidth())
								continue;
							else
								y = j + l;
							if(input[coord(x, y, ip.getWidth())] > h)
								h = input[coord(x, y, ip.getWidth())];
						}
					}
					dilated[coord(i, j, ip.getWidth())] = h;
				}
			}

			// the interior
			for(i = getRadius(); i < (ip.getHeight() - getRadius()); i++) {
				for(j = getRadius(); j < (ip.getWidth() - getRadius()); j++) {

					k = coord(i - getRadius(), j - getRadius(), ip.getWidth());
					h = input[k];

					for(l = 0; l < kernel_width; l++) {
						for(m = 0; m < kernel_width; m++) {
							if(mask[coord(l, m, kernel_width)] == 0)
								continue;

							if(input[k + m] > h)
								h = input[k + m];
						}
						k += ip.getWidth();
					}
					dilated[coord(i, j, ip.getWidth())] = h;
				}
			}
			return dilated_ip;
		}
*/		
	}
