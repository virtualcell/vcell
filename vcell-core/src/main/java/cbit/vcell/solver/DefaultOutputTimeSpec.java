/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;
import cbit.vcell.math.VCML;
import cbit.vcell.solver.SimulationOwner.UnitInfo;

/**
 * Insert the type's description here.
 * Creation date: (9/6/2005 3:11:12 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class DefaultOutputTimeSpec extends OutputTimeSpec {
	private int fieldKeepEvery;
	private int fieldKeepAtMost;	

/**
 * DefaultOutputTimeSpec constructor comment.
 */
public DefaultOutputTimeSpec() {
	this(1,1000);
}


/**
 * DefaultOutputTimeSpec constructor comment.
 */
public DefaultOutputTimeSpec(int arg_keepEvery) {
	this(arg_keepEvery,1000);
}


/**
 * DefaultOutputTimeSpec constructor comment.
 */
public DefaultOutputTimeSpec(int arg_keepEvery, int arg_keepAtMost) {
	super();
	if (arg_keepEvery<1){
		throw new IllegalArgumentException("\"keep every\" is "+arg_keepEvery+", must be positive");
	}
	if (arg_keepAtMost<1){
		throw new IllegalArgumentException("\"keep at most\" is "+arg_keepAtMost+", must be positive");
	}
	fieldKeepEvery = arg_keepEvery;
	fieldKeepAtMost = arg_keepAtMost;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (this == obj) {
		return (true);
	}
	if (obj != null && obj instanceof DefaultOutputTimeSpec) {
		DefaultOutputTimeSpec dot = (DefaultOutputTimeSpec)obj;
		if (dot.fieldKeepAtMost == fieldKeepAtMost && dot.fieldKeepEvery == fieldKeepEvery) {
			return true;
		}
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:44:00 AM)
 * @return int
 */
public int getKeepAtMost() {
	return fieldKeepAtMost;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:44:00 AM)
 * @return int
 */
public int getKeepEvery() {
	return fieldKeepEvery;
}


/**
 * describe 
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	String r = "keep every " + fieldKeepEvery + " sample";// + ", max " + fieldKeepAtMost;
	if (fieldKeepEvery != 1) {
		return r + 's' ;
	}
	return r;
}

/**
 * @param unitInfo ignored, na for this time spec
 * @return {@link #getDescription()}
 */
@Override
public String describe(UnitInfo unitInfo) {
	return getDescription(); 
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:46:52 AM)
 * @return java.lang.String
 */
public java.lang.String getVCML() {
	//
	// write format as follows:
	//
	//   OutputOptions {
	//		KeepEvery 1
	//		KeepAtMost	1000
	//   }
	//
	//	
	StringBuffer buffer = new StringBuffer();
	
	buffer.append(VCML.OutputOptions + " " + VCML.BeginBlock + "\n");
	
	buffer.append("    " + VCML.KeepEvery + " " + fieldKeepEvery + "\n");
	buffer.append("    " + VCML.KeepAtMost + " " + fieldKeepAtMost + "\n");

	buffer.append(VCML.EndBlock + "\n");

	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:48 PM)
 * @return boolean
 */
public boolean isDefault() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:48 PM)
 * @return boolean
 */
public boolean isExplicit() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:48 PM)
 * @return boolean
 */
public boolean isUniform() {
	return false;
}

@Override
public String toString() {
	return String.valueOf(fieldKeepEvery);
} 
}
