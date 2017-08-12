/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;

import cbit.image.ZoomEvent;
import cbit.image.ZoomListener;

/**
 * Insert the type's description here.
 * Creation date: (10/7/00 1:38:31 PM)
 * @author: 
 */
public class ImagePaneViewMulticaster extends java.awt.AWTEventMulticaster implements ZoomListener,PanListener{
/**
 * ImagePaneViewMulticaster constructor comment.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected ImagePaneViewMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:39:21 PM)
 */
public static PanListener add(PanListener a, PanListener b) {
    return (PanListener) addInternal(a, b);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:39:21 PM)
 */
public static ZoomListener add(ZoomListener a, ZoomListener b) {
	return (ZoomListener) addInternal(a, b);
}
	/** 
	 * Returns the resulting multicast listener from adding listener-a
	 * and listener-b together.  
	 * If listener-a is null, it returns listener-b;  
	 * If listener-b is null, it returns listener-a
	 * If neither are null, then it creates and returns
	 * a new AWTEventMulticaster instance which chains a with b.
	 * @param a event listener-a
	 * @param b event listener-b
	 */
	protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new ImagePaneViewMulticaster(a, b);
	}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:41:07 PM)
 */
public void panning(PanEvent e) {
    ((PanListener) a).panning(e);
    ((PanListener) b).panning(e);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:40:11 PM)
 */
public static PanListener remove(PanListener l, PanListener oldl) {
    return (PanListener) removeInternal(l, oldl);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:40:11 PM)
 */
public static ZoomListener remove(ZoomListener l, ZoomListener oldl) {
	return (ZoomListener) removeInternal(l, oldl);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:41:07 PM)
 */
public void zooming(ZoomEvent e) {
	((ZoomListener) a).zooming(e);
	((ZoomListener) b).zooming(e);
}
}
