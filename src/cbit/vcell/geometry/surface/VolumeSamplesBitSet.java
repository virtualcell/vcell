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

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

public class VolumeSamplesBitSet implements VolumeSamples {
	private HashMap<Long,BitSet> bitSets = new HashMap<Long, BitSet>();
	int size;
	private float[] distanceMapL1;
	
	public VolumeSamplesBitSet(int size){
		this.size = size;
		this.distanceMapL1 = new float[size];
		Arrays.fill(this.distanceMapL1,Float.MAX_VALUE);
	}

	public HashMap<Long,BitSet> getIncidentSurfaceBitSets() {
		return bitSets;
	}

	public float[] getDistanceMapL1() {
		return distanceMapL1;
	}
	
	public boolean hasZeros(){
		for (BitSet bs : bitSets.values()){
			if (bs.cardinality()<size){
				return true;
			}
		}
		return false;
	}
	
	public void add(int index, long mask, float distance){
		BitSet bs = bitSets.get(mask);
		if (bs==null){
			bs = new BitSet(size);
			bitSets.put(mask,bs);
		}
		bs.set(index);
//		System.out.println("index="+index+", mask="+mask+", distance="+distance);
//		incidentSurfaceMask[index] = incidentSurfaceMask[index] | mask;
		distanceMapL1[index] = Math.min(distanceMapL1[index],distance);
	}

	public int getNumXYZ() {
		return size;
	}
}
