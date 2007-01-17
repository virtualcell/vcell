package cbit.vcell.math;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
