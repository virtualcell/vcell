package org.vcell.render;
/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 12:16:23 PM)
 * @author: Jim Schaff
 */
public class HitList {
	private java.util.TreeMap hitEventMap = new java.util.TreeMap();
	private double fieldStartX;
	private double fieldStartY;

/**
 * HitList constructor comment.
 */
public HitList(double argStartX, double argStartY) {
	super();
	this.fieldStartX = argStartX;
	this.fieldStartY = argStartY;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:21:02 PM)
 * @param hitEvent cbit.vcell.geometry.gui.HitEvent
 */
public void addHitEvent(HitEvent hitEvent) {
	hitEventMap.put(new Double(hitEvent.getHitRayZ()),hitEvent);
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 6:57:48 PM)
 * @return int
 */
public int getNumHits() {
	return hitEventMap.size();
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 5:05:46 PM)
 * @return int
 * @param z double
 */
public int getRegionIndex(double sampleZ) {
	return 0;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 3:40:29 PM)
 * @return double
 */
public double getStartX() {
	return fieldStartX;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 3:40:29 PM)
 * @return double
 */
public double getStartY() {
	return fieldStartY;
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
	java.util.Collection values = hitEventMap.values();
	int regionIndex = -1;
	if (hitEventMap.isEmpty()){
		return -1;
	}
	//
	// if at least one hit, then can determine which region it is
	//
	java.util.Iterator iter = values.iterator();
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