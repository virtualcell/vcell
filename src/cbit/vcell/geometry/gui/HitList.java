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
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;

import cbit.vcell.geometry.surface.VolumeSamples;
import cbit.vcell.geometry.surface.VolumeSamplesBitSet;
import cbit.vcell.geometry.surface.VolumeSamplesLong;

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
			if (currHitEvent.getUnitNormalInRayDirection()<0){
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getExteriorMask(),(float)(currHitEvent.getHitRayZ()-samplesZ[sampleIndex]));
			}else{
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getInteriorMask(),(float)(currHitEvent.getHitRayZ()-samplesZ[sampleIndex]));
			}
			if (hitIndex>0){
				// if not first hit record, include previous record also
				HitEvent prevHitEvent = hitEvents.get(hitIndex-1);
				if (prevHitEvent.getUnitNormalInRayDirection()<0){
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
			if (currHitEvent.getUnitNormalInRayDirection()<0){
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getInteriorMask(),(float)(samplesZ[sampleIndex] - currHitEvent.getHitRayZ()));
			}else{
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getExteriorMask(),(float)(samplesZ[sampleIndex] - currHitEvent.getHitRayZ()));
			}
			sampleIndex++;
			volumeIndex += volumeStride;
		}
	}
}

}
