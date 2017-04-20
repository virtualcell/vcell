/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;
/**
 * Insert the type's description here.
 * Creation date: (4/14/2005 11:36:38 AM)
 * @author: Jim Schaff
 */
public class ReactionCanvasDisplaySpec {
	public static final int ARROW_RIGHT = 0;
	public static final int ARROW_BOTH = 1;
	
	private String leftText = null;
	private String rightText = null;
	private String topText = null;
	private String bottomText = null;
	private int arrowType = -1;

/**
 * ReactionDisplayText constructor comment.
 */
public ReactionCanvasDisplaySpec(String argLeftText, String argRightText, String argTopText, String argBottomText, int argArrowType) {
	super();
	this.leftText = argLeftText;
	this.rightText = argRightText;
	this.topText = argTopText;
	this.bottomText = argBottomText;
	this.arrowType = argArrowType;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return int
 */
public int getArrowType() {
	return arrowType;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getBottomText() {
	return bottomText;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getLeftText() {
	return leftText;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getRightText() {
	return rightText;
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2005 11:44:47 AM)
 * @return java.lang.String
 */
public java.lang.String getTopText() {
	return topText;
}
}
