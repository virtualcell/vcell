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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import cbit.vcell.matrix.RationalNumber;

/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 12:16:23 PM)
 * @author: Jim Schaff
 */
public class HitListRational {
	private ArrayList<HitEventRational> hitEvents = new ArrayList<HitEventRational>();

/**
 * HitList constructor comment.
 */
public HitListRational() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:21:02 PM)
 * @param hitEvent cbit.vcell.geometry.gui.HitEvent
 */
public void addHitEvent(HitEventRational hitEvent) {
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

public void sampleRegionIDs(RationalNumber[] samplesZ, VolumeSamples volumeSamples, int volumeOffset, int volumeStride){
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
		HitEventRational currHitEvent = hitEvents.get(hitIndex);
		if (samplesZ[sampleIndex].le(currHitEvent.getHitRayZ())){
			if (currHitEvent.getUnitNormalInRayDirection().lt(RationalNumber.ZERO)){
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getExteriorMask());
			}else if (currHitEvent.getUnitNormalInRayDirection().gt(RationalNumber.ZERO)) {
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getInteriorMask());
			}else{
				throw new RuntimeException("don't know whether we are coming or going");
			}
			if (hitIndex>0){
				// if not first hit record, include previous record also
				HitEventRational prevHitEvent = hitEvents.get(hitIndex-1);
				if (prevHitEvent.getUnitNormalInRayDirection().lt(RationalNumber.ZERO)){
					volumeSamples.add(volumeIndex, prevHitEvent.getSurface().getInteriorMask());
				}else{
					volumeSamples.add(volumeIndex, prevHitEvent.getSurface().getExteriorMask());
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
			if (currHitEvent.getUnitNormalInRayDirection().lt(RationalNumber.ZERO)){
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getInteriorMask());
			}else{
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getExteriorMask());
			}
			sampleIndex++;
			volumeIndex += volumeStride;
		}
	}
}


public String getDescription() {
	if (getNumHits()>0){
		StringBuffer buffer = new StringBuffer();
		for (HitEventRational event : hitEvents){
			buffer.append("[t="+event.getHitRayZ().doubleValue()+", centroidT="+event.getCentroidZ().doubleValue()+", polygon="+event.getPolygon().hashCode()+", normal="+event.getUnitNormalInRayDirection().doubleValue()+", extra=\""+event.getDebugMessage()+"\"]\n");
		}
		return buffer.toString();
	}else{
		return "empty hit list";
	}
}

}
