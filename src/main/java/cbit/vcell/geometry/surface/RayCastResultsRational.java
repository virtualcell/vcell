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


public class RayCastResultsRational {
	private int numX;
	private int numY;
	private int numZ;
	private HitListRational[] hitListsXY;  // index = i + numX*j
	private HitListRational[] hitListsXZ;  // index = i + numX*k
	private HitListRational[] hitListsYZ;  // index = j + numY*k
	
	public RayCastResultsRational(HitListRational[] aHitListXY, HitListRational[] aHitListXZ, HitListRational[] aHitListYZ, int aNumX, int aNumY, int aNumZ){
		this.hitListsXY = aHitListXY;
		this.hitListsXZ = aHitListXZ;
		this.hitListsYZ = aHitListYZ;
		this.numX = aNumX;
		this.numY = aNumY;
		this.numZ = aNumZ;
	}

	public int getNumX() {
		return numX;
	}

	public int getNumY() {
		return numY;
	}

	public int getNumZ() {
		return numZ;
	}

	public HitListRational[] getHitListsXY() {
		return hitListsXY;
	}

	public HitListRational[] getHitListsXZ() {
		return hitListsXZ;
	}

	public HitListRational[] getHitListsYZ() {
		return hitListsYZ;
	}
	
	static public double[] computeSignedDistanceMap(boolean[] insideBoundary, double[] unsignedDistanceMap) {
		if(insideBoundary.length != unsignedDistanceMap.length) {
			throw new RuntimeException("Arguments must have the same size");
		}
		
		double[] distanceMap = null;
		if(unsignedDistanceMap != null) {
			distanceMap = unsignedDistanceMap.clone();
		}

		// pass1: all the points within the local distance field INSIDE the mesh become negative
		for(int i=0; i<insideBoundary.length; i++) {
			if(insideBoundary[i] && distanceMap[i] != Double.POSITIVE_INFINITY) {
				distanceMap[i] = -distanceMap[i];
			}
		}
		// pass2: compute the maxNegative and the maxPositive
		double maxNegative = 0;
		double maxPositive = 0;
		for(int i=0; i<insideBoundary.length; i++) {
			if(distanceMap[i] == Double.POSITIVE_INFINITY) {
				continue;
			}
			if(distanceMap[i] < 0) {
				maxNegative = Math.min(maxNegative, distanceMap[i]);
			}else if(distanceMap[i] >0) {
				maxPositive = Math.max(maxPositive, distanceMap[i]);				
			}
		}
		// pass 3: all the Double.POSITIVE_INFINITY points inside are set to maxNegative, all outside are set to maxPositive
		for(int i=0; i<insideBoundary.length; i++) {
			if(distanceMap[i] == Double.POSITIVE_INFINITY) {
				if(insideBoundary[i]) {
					distanceMap[i] = maxNegative;
				} else {
					distanceMap[i] = maxPositive;
				}
			}
		}
		return distanceMap;
	}
}
