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

public class VolumeSamplesShort extends VolumeSamples {
	private short[] incidentSurfaceMaskShort;
	
	public VolumeSamplesShort(int size){
		super(size);
		this.incidentSurfaceMaskShort = new short[size];
	}

	public boolean hasZeros(){
		boolean bHasZero = false;
		for (short mask : incidentSurfaceMaskShort){
			if (mask == 0){
				bHasZero = true;
				break;
			}
		}
		return bHasZero;
	}
	
	public void add(int index, long mask, float distance){
//		System.out.println("index="+index+", mask="+mask+", distance="+distance);
		if( (mask & 0xFFFF) != mask )
		{
			System.err.println("Error: Surface mask doesn't fit into a short.");
		}
		incidentSurfaceMaskShort[index] = (short) (incidentSurfaceMaskShort[index] | mask);
//System.out.println("mask["+index+"]="+mask+", distance="+distance);
		if (RayCaster.connectsAcrossSurface(incidentSurfaceMaskShort[index])){
			System.out.println("connected across surface");
		}
//		getDistanceMapL1()[index] = Math.min(getDistanceMapL1()[index],distance);
	}
	
	public void fillEmpty(int numSamples, int volumeOffset, int volumeStride){
		//
		// if at least one hit, then there are no empty samples
		//
		/*if (hitEvents.size()>0){
			return;
		}*/
		
		short[] volumeMasks = incidentSurfaceMaskShort;
		
		short nonzeroSample = 0;
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
		for (short mask : incidentSurfaceMaskShort){
			if (!uniqueMasks.contains(mask)){
				uniqueMasks.add(((long)mask) & 0xFFFF);
			}
		}
		return uniqueMasks;
	}
	
	@Override
	public long getMask(int index) {
		return ((long)incidentSurfaceMaskShort[index]) & 0xFFFF;
	}
}
