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

public void reconcileHitEvents(){
	//
	// fix hit events caused by numerical imprecision (or ill conditioned meshes ... such as faces that touch or almost touch).
	//
	double epsilon = 1e-8;
	Iterator<HitEvent> iter = hitEvents.iterator();
	HitEvent prev = null;
	while (iter.hasNext()){
		HitEvent curr = iter.next();
		if (prev != null){
			if (Math.abs(prev.getHitRayZ()-curr.getHitRayZ())<epsilon){
				if (prev.getUnitNormalInRayDirection()*curr.getUnitNormalInRayDirection() < 0){
					//
					// normals pointing in opposite directions and hit times are very close ... pretend we missed it (won't sample the inside anyway)
					// this happens when a ray intersects two triangles that form a sharp edge (or hit very close to a dull edge)
					// in either case, 
					// remove them both (treat this as a miss)
					//
					hitEvents.remove(curr);
					hitEvents.remove(prev);
					prev = null;
					curr = null;
					iter = hitEvents.iterator();
				}else{
					//
					// normals pointing in same direction and hit times are very close.  ... we assume that we hit two adjacent triangles that are
					// facing the same way.  In this case, we only need one of these hits.
					// remove curr only, prev stays the same (we treat this as a single hit).
					iter.remove();
					continue;
				}
			}
		}
		prev = curr;
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
			if (currHitEvent.getUnitNormalInRayDirection() < 0){
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getExteriorMask());
			}else if (currHitEvent.getUnitNormalInRayDirection() > 0) {
				volumeSamples.add(volumeIndex, currHitEvent.getSurface().getInteriorMask());
			}else{
				throw new RuntimeException("don't know whether we are coming or going");
			}
			if (hitIndex>0){
				// if not first hit record, include previous record also
				HitEvent prevHitEvent = hitEvents.get(hitIndex-1);
				if (prevHitEvent.getUnitNormalInRayDirection() < 0){
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
			if (currHitEvent.getUnitNormalInRayDirection() < 0){
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
		for (HitEvent event : hitEvents){
			buffer.append("[t="+event.getHitRayZ()+", centroidT="+event.getCentroidZ()+", polygon="+event.getPolygon().hashCode()+", normal="+event.getUnitNormalInRayDirection()+", extra=\""+event.getDebugMessage()+"\"]\n");
		}
		return buffer.toString();
	}else{
		return "empty hit list";
	}
}

}
