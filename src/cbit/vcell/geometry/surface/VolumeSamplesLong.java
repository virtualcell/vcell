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

public class VolumeSamplesLong extends VolumeSamples {
	private long[] incidentSurfaceMaskLong;
	
	public VolumeSamplesLong(int size){
		super(size);
		this.incidentSurfaceMaskLong = new long[size];
	}

	public boolean hasZeros(){
		boolean bHasZero = false;
		for (long mask : incidentSurfaceMaskLong){
			if (mask == 0L){
				bHasZero = true;
				break;
			}
		}
		return bHasZero;
	}
	
	public void add(int index, long mask){
		incidentSurfaceMaskLong[index] = incidentSurfaceMaskLong[index] | mask;
		if (RayCaster.connectsAcrossSurface(incidentSurfaceMaskLong[index])){
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
		
		long[] volumeMasks = incidentSurfaceMaskLong;
		
		long nonzeroSample = 0;
		int volumeIndex = volumeOffset;
		for (int i=0;i<numSamples;i++){
			if (volumeMasks[volumeIndex] != 0L){
				nonzeroSample = volumeMasks[volumeIndex];
				break;
			}
			volumeIndex += volumeStride;
		}
		
		volumeIndex = volumeOffset;
		for (int i=0;i<numSamples;i++){
			if (volumeMasks[volumeIndex] == 0L){
				volumeMasks[volumeIndex] = nonzeroSample;
			}
			volumeIndex += volumeStride;
		}
	}
	
	@Override
	public HashSet<Long> getUniqueMasks() {
		HashSet<Long> uniqueMasks = new HashSet<Long>();
		for (long mask : incidentSurfaceMaskLong){
			if (!uniqueMasks.contains(mask)){
				uniqueMasks.add(mask);
			}
		}
		return uniqueMasks;
	}
	
	@Override
	public long getMask(int index) {
		return incidentSurfaceMaskLong[index];
	}
	
}
