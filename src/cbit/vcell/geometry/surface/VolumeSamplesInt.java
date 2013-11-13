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

public class VolumeSamplesInt extends VolumeSamples {
	private int[] incidentSurfaceMaskInt;
	
	public VolumeSamplesInt(int size){
		super(size);
		this.incidentSurfaceMaskInt = new int[size];
	}

	public boolean hasZeros(){
		boolean bHasZero = false;
		for (int mask : incidentSurfaceMaskInt){
			if (mask == 0){
				bHasZero = true;
				break;
			}
		}
		return bHasZero;
	}
	
	public void add(int index, long mask){
		if( (mask & 0xFFFFFFFFL) != mask )
		{
			System.err.println("Error: Surface mask doesn't fit into an int.");
		}
		incidentSurfaceMaskInt[index] = (int) (incidentSurfaceMaskInt[index] | (mask & 0xFFFFFFFFL));
		if (RayCaster.connectsAcrossSurface(incidentSurfaceMaskInt[index])){
			System.out.println("connected across surface");
		}
	}
	
	public void fillEmpty(int numSamples, int volumeOffset, int volumeStride){
		//
		// if at least one hit, then there are no empty samples
		//
		/*if (hitEvents.size()>0){
			return;
		}*/
		
		int[] volumeMasks = incidentSurfaceMaskInt;
		
		int nonzeroSample = 0;
		int volumeIndex = volumeOffset;
		for (int i=0;i<numSamples;i++){
			if (volumeMasks[volumeIndex] != 0){
				nonzeroSample = volumeMasks[volumeIndex];
				break;
			}
			volumeIndex += volumeStride;
		}
		
		volumeIndex = volumeOffset;
		for (int i=0;i<numSamples;i++){
			if (volumeMasks[volumeIndex] == 0){
				volumeMasks[volumeIndex] = nonzeroSample;
			}
			volumeIndex += volumeStride;
		}
	}
	
	@Override
	public HashSet<Long> getUniqueMasks() {
		HashSet<Long> uniqueMasks = new HashSet<Long>();
		for (int mask : incidentSurfaceMaskInt){
			long longMask = ((long)mask) & 0xFFFFFFFFL;
			if (!uniqueMasks.contains(longMask)){
				uniqueMasks.add(longMask);
			}
		}
		return uniqueMasks;
	}
	
	@Override
	public long getMask(int index) {
		return ((long)incidentSurfaceMaskInt[index]) & 0xFFFFFFFFL;
	}
}
