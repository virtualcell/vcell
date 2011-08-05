/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;
/**
 * Insert the type's description here.
 * Creation date: (3/3/00 12:24:17 AM)
 * @author: 
 */
public class ConstraintType {
	private static final int EQUALITY_LINEAR		= 1;
	private static final int EQUALITY_NONLINEAR		= 2;
	private static final int INEQUALITY_LINEAR		= 3;	
	private static final int INEQUALITY_NONLINEAR	= 4;

	private int type = 0;
	private String name = null;

	/**
		The equality constraints imply that the constraint expression must be equal to zero. 
	**/
	public static ConstraintType LinearEquality = new ConstraintType(EQUALITY_LINEAR,	"LinearEqualityConstraint");
	public static ConstraintType NonlinearEquality = new ConstraintType(EQUALITY_NONLINEAR,	"NonlinearEqualityConstraint");
	/**
		The inequality constraints imply that the constraint expression must be less than or equal to zero. 
	**/
	public static ConstraintType LinearInequality = new ConstraintType(INEQUALITY_LINEAR,	"LinearInequalityConstraint");
	public static ConstraintType NonlinearInequality = new ConstraintType(INEQUALITY_NONLINEAR,"NonlinearInequalityConstraint");

/**
 * ConstraintType constructor comment.
 */
private ConstraintType(int type, String name) {
	this.type = type;
	this.name = name;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:31:17 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof ConstraintType){
		if (type == ((ConstraintType)obj).type){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:31:17 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
public int hashCode() {
	return type;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:29:37 AM)
 * @return boolean
 */
public boolean isEquality() {
	return (type==EQUALITY_LINEAR || type==EQUALITY_NONLINEAR);
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:29:37 AM)
 * @return boolean
 */
public boolean isLinear() {
	return (type==EQUALITY_LINEAR || type==INEQUALITY_LINEAR);
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:57:51 AM)
 * @return java.lang.String
 */
public String toString() {
	return name;
}
}
