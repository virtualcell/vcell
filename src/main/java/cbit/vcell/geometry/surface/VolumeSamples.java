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

import java.util.HashSet;

public abstract class VolumeSamples {
	
//	private float[] distanceMapL1;
	private int size;
	
	public VolumeSamples(int size){
		this.size = size;
//		this.distanceMapL1 = new float[size];
//		Arrays.fill(this.distanceMapL1,Float.MAX_VALUE);
	}

//	public float[] getDistanceMapL1() {
//		return distanceMapL1;
//	}
	
	public abstract boolean hasZeros();
	
	public abstract void add(int index, long mask);

	public abstract void fillEmpty(int numSamples, int volumeOffset, int volumeStride);
	
	public abstract HashSet<Long> getUniqueMasks();
	
	public abstract long getMask(int index);
	
	public int getNumXYZ() {
		return size;
	}

}
