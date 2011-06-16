/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

/**
 * This type was created in VisualAge.
 */
public class MathFormatException extends MathException {
	private int lineNumber = -1;
/**
 * MathFormatException constructor comment.
 * @param s java.lang.String
 */
public MathFormatException(String s) {
	this(s,-1);
}
/**
 * MathFormatException constructor comment.
 * @param s java.lang.String
 */
public MathFormatException(String s, int argLineNumber) {
	super(s);
	this.lineNumber = argLineNumber;
}
/**
 * gets line number of format error (of Math language)
 * or (-1) if not defined.
 *
 *
 * Creation date: (4/9/01 4:23:17 PM)
 * @return int
 */
public int getLineNumber() {
	return lineNumber;
}
}
