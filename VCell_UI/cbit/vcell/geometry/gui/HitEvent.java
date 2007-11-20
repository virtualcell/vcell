package cbit.vcell.geometry.gui;
import org.vcell.spatial.Polygon;
import org.vcell.spatial.Surface;
/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 12:08:18 PM)
 * @author: Jim Schaff
 */
public class HitEvent {
	private Surface surface = null;
	private Polygon polygon = null;
	private boolean entering;
	private double hitRayZ;

/**
 * HitEvent constructor comment.
 */
public HitEvent(Surface argSurface, Polygon argPolygon, boolean argEntering, double argRayZ) {
	super();
	this.surface = argSurface;
	this.polygon = argPolygon;
	this.entering = argEntering;
	this.hitRayZ = argRayZ;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return double
 */
public double getHitRayZ() {
	return hitRayZ;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return cbit.vcell.geometry.surface.Polygon
 */
public Polygon getPolygon() {
	return polygon;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return cbit.vcell.geometry.surface.Surface
 */
public Surface getSurface() {
	return surface;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 12:24:06 PM)
 * @return boolean
 */
public boolean isEntering() {
	return entering;
}
}