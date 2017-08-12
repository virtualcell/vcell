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

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

public class VolumeSamplesBitSet extends VolumeSamples {
	private HashMap<Long,BitSet> bitSets = new HashMap<Long, BitSet>();
	int size;
	
	public VolumeSamplesBitSet(int size){
		super(size);
	}

	public HashMap<Long,BitSet> getIncidentSurfaceBitSets() {
		return bitSets;
	}

	public boolean hasZeros(){
		for (BitSet bs : bitSets.values()){
			if (bs.cardinality()<size){
				return true;
			}
		}
		return false;
	}
	
	public void add(int index, long mask){
		BitSet bs = bitSets.get(mask);
		if (bs==null){
			bs = new BitSet(size);
			bitSets.put(mask,bs);
		}
		bs.set(index);
	}
		
	public void fillEmpty(int numSamples, int volumeOffset, int volumeStride){
		//
		// if at least one hit, then there are no empty samples
		//
		/*if (hitEvents.size()>0){
			return;
		}*/
		
		BitSet rowPattern = new BitSet(getNumXYZ());
		int volumeIndex = volumeOffset;
		for (int i=0;i<numSamples;i++){
			rowPattern.set(volumeIndex);
			volumeIndex += volumeStride;
		}

		for (BitSet bitSet : getIncidentSurfaceBitSets().values()){
			if (bitSet.intersects(rowPattern)){
				bitSet.or(rowPattern);
			}
		}
	}

	@Override
	public HashSet<Long> getUniqueMasks() {
		throw new RuntimeException("VolumeSamplesBitSet.getUniqueMasks() is not implemented yet.");
	}

	@Override
	public long getMask(int index) {
		throw new RuntimeException("VolumeSamplesBitSet.getMask(int index) is not implemented yet.");
	}
}
