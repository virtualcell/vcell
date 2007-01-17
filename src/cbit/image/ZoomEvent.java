package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/7/00 3:30:14 PM)
 * @author: 
 */
public class ZoomEvent extends java.awt.AWTEvent {
	private int zoomDelta = 0;
/**
 * ZoomEvent constructor comment.
 * @param source java.lang.Object
 * @param id int
 */
public ZoomEvent(Object source, int id,int argZoomDelta) {
	super(source, id);
	zoomDelta = argZoomDelta;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 3:31:31 PM)
 * @return int
 */
public int getZoomDelta() {
	return zoomDelta;
}
}
