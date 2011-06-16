/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

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
