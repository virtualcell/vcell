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

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;

import cbit.vcell.math.*;
/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 3:58:31 PM)
 * @author: 
 */
public class TimeBounds implements java.io.Serializable, org.vcell.util.Matchable {
	private double fieldStartingTime = 0.0;
	private double fieldEndingTime = 1.0;
/**
 * TimeBounds constructor comment.
 */
public TimeBounds() {
	super();
}
/**
 * TimeBounds constructor comment.
 */
public TimeBounds(double startingTime, double endingTime) {
	super();
	fieldStartingTime = startingTime;
	fieldEndingTime = endingTime;
}
/**
 * TimeBounds constructor comment.
 */
public TimeBounds(TimeBounds timeBounds) {
	super();
	fieldStartingTime = timeBounds.getStartingTime();
	fieldEndingTime = timeBounds.getEndingTime();
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable object) {
	if (this == object) {
		return (true);
	}
	if (object != null && object instanceof TimeBounds) {
		TimeBounds timeBounds = (TimeBounds) object;
		if (getStartingTime() != timeBounds.getStartingTime()) return (false);
		if (getEndingTime()   != timeBounds.getEndingTime())   return (false);
		return true;
	}
	return (false);
}
/**
 * Gets the endingTime property (double) value.
 * @return The endingTime property value.
 * @see #setEndingTime
 */
public double getEndingTime() {
	return fieldEndingTime;
}
/**
 * Gets the startingTime property (double) value.
 * @return The startingTime property value.
 * @see #setStartingTime
 */
public double getStartingTime() {
	return fieldStartingTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/00 12:04:47 AM)
 * @return java.lang.String
 */
public String getVCML() {
	//
	// write format as follows:
	//
	//   TimeBounds {
	//		StartingTime	0.0
	//		EndingTime		100.0
	//   }
	//
	//	
	StringBuffer buffer = new StringBuffer();
	
	buffer.append(VCML.TimeBounds+" "+VCML.BeginBlock+"\n");
	
	buffer.append("   "+VCML.StartingTime+" "+getStartingTime()+"\n");
	buffer.append("   "+VCML.EndingTime+" "+getEndingTime()+"\n");

	buffer.append(VCML.EndBlock+"\n");

	return buffer.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/00 3:45:12 PM)
 * @return java.lang.String
 */
public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
	//
	// read format as follows:
	//
	//   TimeBounds {
	//		StartingTime	0.0
	//		EndingTime		100.0
	//   }
	//
	//	
	try {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.TimeBounds)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException(
					"unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.StartingTime)) {
				token = tokens.nextToken();
				fieldStartingTime = Double.parseDouble(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.EndingTime)) {
				token = tokens.nextToken();
				fieldEndingTime = Double.parseDouble(token);
				continue;
			}
			throw new DataAccessException("unexpected identifier " + token);
		}
	} catch (Throwable e) {
		throw new DataAccessException(
			"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
	}
}
}
