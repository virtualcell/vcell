/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import cbit.vcell.geometry.surface.VolumeSamples;

/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 12:16:23 PM)
 * @author: Jim Schaff
 */
public class HitList {
	private ArrayList<HitEvent> hitEvents = new ArrayList<HitEvent>();

/**
 * HitList constructor comment.
 */
public HitList() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:21:02 PM)
 * @param hitEvent cbit.vcell.geometry.gui.HitEvent
 */
public void addHitEvent(HitEvent hitEvent) {
	hitEvents.add(hitEvent);
	Collections.sort(hitEvents);
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 6:57:48 PM)
 * @return int
 */
public int getNumHits() {
	return hitEvents.size();
}

/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 6:57:48 PM)
 * @return int
 */
public boolean isEmpty() {
	return hitEvents.isEmpty();
}

public void sampleRegionIDs(double[] samplesZ, int[] regionIds){
	//
	// if no hits, return -1 only
	//
	if (hitEvents.isEmpty()){
		Arrays.fill(regionIds, -1);
		return;
	}
	//
	// if at least one hit, then can determine which region it is
	//
	int sampleIndex = 0;
	int hitIndex = 0;
	while (sampleIndex < samplesZ.length){
		if (samplesZ[sampleIndex] <= hitEvents.get(hitIndex).getHitRayZ()){
			if (hitEvents.get(hitIndex).isEntering()){
				regionIds[sampleIndex] = hitEvents.get(hitIndex).getSurface().getExteriorRegionIndex();
			}else{
				regionIds[sampleIndex] = hitEvents.get(hitIndex).getSurface().getInteriorRegionIndex();
			}
			// next sample
			sampleIndex++;
		}else if (hitIndex < hitEvents.size()-1){
			// next hit record
			hitIndex++;
		}else{
			// sampling past end of hit records, use the last record
			if (hitEvents.get(hitIndex).isEntering()){
				regionIds[sampleIndex] = hitEvents.get(hitIndex).getSurface().getInteriorRegionIndex();
			}else{
				regionIds[sampleIndex] = hitEvents.get(hitIndex).getSurface().getExteriorRegionIndex();
			}
			sampleIndex++;
		}
	}
}

public void sampleRegionIDs(double[] samplesZ, VolumeSamples volumeSamples, int volumeOffset, int volumeStride){
	//
	// if at least one hit, then can determine which region it is
	//
	if (hitEvents.size()==0){
		return;
	}
	int volumeIndex = volumeOffset;
	int sampleIndex = 0;
	int hitIndex = 0;
	while (sampleIndex < samplesZ.length){
		HitEvent currHitEvent = hitEvents.get(hitIndex);
		if (samplesZ[sampleIndex] <= currHitEvent.getHitRayZ()){
			if (currHitEvent.isEntering()){
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getExteriorMask(),(float)(currHitEvent.getHitRayZ()-samplesZ[sampleIndex]));
			}else{
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getInteriorMask(),(float)(currHitEvent.getHitRayZ()-samplesZ[sampleIndex]));
			}
			if (hitIndex>0){
				// if not first hit record, include previous record also
				HitEvent prevHitEvent = hitEvents.get(hitIndex-1);
				if (prevHitEvent.isEntering()){
					volumeSamples.add(volumeIndex, prevHitEvent.getSurface().getInteriorMask(),(float)(samplesZ[sampleIndex] - prevHitEvent.getHitRayZ()));
				}else{
					volumeSamples.add(volumeIndex, prevHitEvent.getSurface().getExteriorMask(),(float)(samplesZ[sampleIndex] - prevHitEvent.getHitRayZ()));
				}
			}
			// next sample
			sampleIndex++;
			volumeIndex += volumeStride;
		}else if (hitIndex < hitEvents.size()-1){
			// next hit record
			hitIndex++;
		}else{
			// sampling past end of hit records, use the last record
			if (currHitEvent.isEntering()){
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getInteriorMask(),(float)(samplesZ[sampleIndex] - currHitEvent.getHitRayZ()));
			}else{
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getExteriorMask(),(float)(samplesZ[sampleIndex] - currHitEvent.getHitRayZ()));
			}
			sampleIndex++;
			volumeIndex += volumeStride;
		}
	}
}


public void fillEmpty(VolumeSamples volumeSamples, int numSamples, int volumeOffset, int volumeStride){
	//
	// if at least one hit, then there are no empty samples
	//
	if (hitEvents.size()>0){
		return;
	}
	
	long[] volumeMasks = volumeSamples.getIncidentSurfaceMask();
	
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

/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:26:45 PM)
 * @param sampleIndexes int[]
 * @param startTime double
 * @param endTime double
 */
public int sample(double sampleZ) {
	//
	// assume already sorted
	//
	if (hitEvents.isEmpty()){
		return -1;
	}
	//
	// if at least one hit, then can determine which region it is
	//
	Iterator<HitEvent> iter = hitEvents.iterator();
	HitEvent currHitEvent = (HitEvent)iter.next();
	if (currHitEvent.getHitRayZ()>sampleZ){
		if (currHitEvent.isEntering()){
			return currHitEvent.getSurface().getExteriorRegionIndex();
		}else{
			return currHitEvent.getSurface().getInteriorRegionIndex();
		}
	}
	while (iter.hasNext()){
		currHitEvent = (HitEvent)iter.next();
		if (currHitEvent.getHitRayZ()>sampleZ){
			if (currHitEvent.isEntering()){
				return currHitEvent.getSurface().getExteriorRegionIndex();
			}else{
				return currHitEvent.getSurface().getInteriorRegionIndex();
			}
		}
	}
	//
	// sample past end of last hit
	//
	if (currHitEvent.isEntering()){ // reverse since on other side of ray
		return currHitEvent.getSurface().getInteriorRegionIndex();
	}else{
		return currHitEvent.getSurface().getExteriorRegionIndex();
	}
}

}
