package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
