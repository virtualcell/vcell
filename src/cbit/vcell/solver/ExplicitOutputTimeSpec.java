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
import java.util.Objects;

import cbit.vcell.math.VCML;
import cbit.vcell.solver.SimulationOwner.UnitInfo;

/**
 * Insert the type's description here.
 * Creation date: (9/6/2005 3:10:22 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ExplicitOutputTimeSpec extends OutputTimeSpec {
	private double[] fieldOutputTimes = null;

/**
 * ExplicitOutputTimeSpec constructor comment.
 */
public ExplicitOutputTimeSpec(double[] arg_outputTimes) {
	super();
	if (arg_outputTimes == null || arg_outputTimes.length == 0) {
		throw new RuntimeException("Output times can't be empty");
	}
	fieldOutputTimes = arg_outputTimes;
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
	if (obj != null && obj instanceof ExplicitOutputTimeSpec) {
		ExplicitOutputTimeSpec eot = (ExplicitOutputTimeSpec)obj;
		if (eot.fieldOutputTimes == null && fieldOutputTimes == null) {
			return true;
		}
		
		if (eot.fieldOutputTimes.length != fieldOutputTimes.length) {
			return false;
		}

		for (int i = 0; i < fieldOutputTimes.length; i ++) {
			if (eot.fieldOutputTimes[i] != fieldOutputTimes[i]) {
				return false;
			}
		}
		return true;
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:56:45 AM)
 * @return java.lang.String
 */
//
// reads single list of numbers separated by any combination of spaces, commas and linefeeds
// this method is used to parse the user input (from the text field)
//
public static ExplicitOutputTimeSpec fromString(String line) {
	java.util.StringTokenizer st = new java.util.StringTokenizer(line, ", \n");
	double[] times = new double[st.countTokens()];
	int count = 0;
	while (st.hasMoreTokens()) {
		try {
			String token = st.nextToken();
			double time = Double.parseDouble(token);				
			times[count ++] = time;
		} catch (Exception ex) {
			throw new RuntimeException("Output times must be seperated by comma.");
		}			
	}
	return new ExplicitOutputTimeSpec(times);
}


/**
 * Insert the method's description here.
 * Creation date: (9/8/2005 8:17:43 AM)
 * @return int
 */
public int getNumTimePoints() {
	return fieldOutputTimes.length;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:46:03 AM)
 * @return double[]
 */
public double[] getOutputTimes() {
	return fieldOutputTimes;
}


@Override
public java.lang.String getDescription() {
	return "at " + toCommaSeperatedOneLineOfString() + " sec";
}


@Override
public String describe(UnitInfo unitInfo) {
	Objects.requireNonNull(unitInfo);
	return "at " + toCommaSeperatedOneLineOfString() + ' ' + unitInfo.getTimeUnitString();
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 3:43:11 PM)
 * @return java.lang.String
 */
@Override
public java.lang.String getShortDescription() {
	if (fieldOutputTimes.length <= 3) {
		return "{" + toCommaSeperatedOneLineOfString() + "}";
	} else {
		return "{" + fieldOutputTimes[0] + "," + fieldOutputTimes[1] + ", ..., " + fieldOutputTimes[fieldOutputTimes.length - 1] +"}";
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:56:45 AM)
 * @return java.lang.String
 */
public java.lang.String getVCML() {
	//
	// write format as follows:
	//
	//   OutputOptions {
	//		OutputTimes 0.1,0.3,0.4,... (no spaces or line feeds between numbers)
	//   }
	//
	//	
	StringBuffer buffer = new StringBuffer();
	
	buffer.append(VCML.OutputOptions + " " + VCML.BeginBlock + "\n");
	
	buffer.append("    " + VCML.OutputTimes + " " + toCommaSeperatedOneLineOfString() + "\n");

	buffer.append(VCML.EndBlock + "\n");

	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:52 PM)
 * @return boolean
 */
public boolean isDefault() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:52 PM)
 * @return boolean
 */
public boolean isExplicit() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:52 PM)
 * @return boolean
 */
public boolean isUniform() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:56:45 AM)
 * @return java.lang.String
 */
public java.lang.String toCommaSeperatedOneLineOfString() {
	StringBuilder buffer = new StringBuilder();

	for (int i = 0; i < fieldOutputTimes.length; i ++) {
		buffer.append(fieldOutputTimes[i]);
		if (i != fieldOutputTimes.length - 1) {
			buffer.append(",");
		}
	}

	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:56:45 AM)
 * @return java.lang.String
 */
public java.lang.String toSpaceSeperatedMultiLinesOfString() {
	StringBuffer buffer = new StringBuffer();	
	
	for (int i = 0; i < fieldOutputTimes.length; i ++) {
		buffer.append(fieldOutputTimes[i]);
		if (i != fieldOutputTimes.length - 1) {
			buffer.append(" ");
		}
		if ((i + 1) % 20 == 0) {
			buffer.append("\n");
		}
	}

	return buffer.toString();
}

@Override
public String toString() {
	return toCommaSeperatedOneLineOfString();
} 
}
