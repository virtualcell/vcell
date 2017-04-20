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

/**
 * Insert the type's description here.
 * Creation date: (10/7/00 1:13:13 PM)
 * @author: 
 */
public class PanEvent extends java.awt.AWTEvent {
	private int deltaX;
	private int deltaY;
/**
 * PanEvent constructor comment.
 * @param source java.awt.Component
 * @param id int
 * @param when long
 * @param modifiers int
 * @param x int
 * @param y int
 * @param clickCount int
 * @param popupTrigger boolean
 */
public PanEvent(Object source,int id,int argDeltaX,int argDeltaY) {
	super(source, id);
	deltaX = argDeltaX;
	deltaY = argDeltaY;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:16:01 PM)
 * @return int
 */
public int getDeltaX() {
	return deltaX;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:16:01 PM)
 * @return int
 */
public int getDeltaY() {
	return deltaY;
}
}
