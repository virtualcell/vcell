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

import cbit.vcell.math.VCML;
/**
 * Insert the type's description here.
 * Creation date: (11/2/2000 3:59:34 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class TimeStep implements java.io.Serializable, org.vcell.util.Matchable {
		
	public enum TimeStepDescription {
		Minimum("KISAO:0000485", "Minimum Time Step"),
		Default("KISAO:0000483", "Default Time Step"),
		Maximum("KISAO:0000467", "Maximum Time Step"),
		;
		
		private final String kisao;
		private final String description;
		
		private TimeStepDescription(String kisao, String description) {
			this.kisao = kisao;
			this.description = description;
		}
		public String getKisao() {
			return kisao;
		}
		public String getDescription() {
			return description;
		}
	}

	
	
	private double fieldMinimumTimeStep = 1.0E-8;
	private double fieldDefaultTimeStep = 0.1;
	private double fieldMaximumTimeStep = 1.0;
/**
 * TimeStep constructor comment.
 */
public TimeStep() {
	super();
}

public static TimeStep getDefaultSundialsTimeStep() {
	return new TimeStep(0, 0.1, 0.1);
}

public static TimeStep getDefaultSmoldynTimeStep() {
	return new TimeStep(1e-4,1e-4,1e-4);
}
/**
 * TimeStep constructor comment.
 */
public TimeStep(double minimumTimeStep, double defaultTimestep, double maximumTimeStep) {
	super();
	fieldMinimumTimeStep = minimumTimeStep;
	fieldDefaultTimeStep = defaultTimestep;
	fieldMaximumTimeStep = maximumTimeStep;
}
/**
 * TimeStep constructor comment.
 */
public TimeStep(TimeStep timeStep) {
	super();
	fieldMinimumTimeStep = timeStep.getMinimumTimeStep();
	fieldDefaultTimeStep = timeStep.getDefaultTimeStep();
	fieldMaximumTimeStep = timeStep.getMaximumTimeStep();
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable object) {
	if (this == object) {
		return true;
	}
	if (object != null && object instanceof TimeStep) {
		TimeStep timeStep = (TimeStep) object;
		if (getMinimumTimeStep() != timeStep.getMinimumTimeStep()) return (false);
		if (getDefaultTimeStep() != timeStep.getDefaultTimeStep()) return (false);
		if (getMaximumTimeStep() != timeStep.getMaximumTimeStep()) return (false);
		return true;
	}
	return false;
}
/**
 * Gets the defaultTimeStep property (double) value.
 * @return The defaultTimeStep property value.
 * @see #setDefaultTimeStep
 */
public double getDefaultTimeStep() {
	return fieldDefaultTimeStep;
}
/**
 * Gets the maximumTimeStep property (double) value.
 * @return The maximumTimeStep property value.
 * @see #setMaximumTimeStep
 */
public double getMaximumTimeStep() {
	return fieldMaximumTimeStep;
}
/**
 * Gets the minimumTimeStep property (double) value.
 * @return The minimumTimeStep property value.
 * @see #setMinimumTimeStep
 */
public double getMinimumTimeStep() {
	return fieldMinimumTimeStep;
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
	//   TimeStep {
	//		DefaultTimeStep		0.0
	//		MinimumTimeStep		1e-7
	//		MaximumTimeStep		0.01
	//   }
	//
	//	
	StringBuffer buffer = new StringBuffer();
	
	buffer.append(VCML.TimeStep+" "+VCML.BeginBlock+"\n");
	
	buffer.append("   "+VCML.DefaultTimeStep+" "+getDefaultTimeStep()+"\n");
	buffer.append("   "+VCML.MinimumTimeStep+" "+getMinimumTimeStep()+"\n");
	buffer.append("   "+VCML.MaximumTimeStep+" "+getMaximumTimeStep()+"\n");

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
	//   TimeStep {
	//		DefaultTimeStep		0.0
	//		MinimumTimeStep		1e-7
	//		MaximumTimeStep		0.01
	//   }
	//
	//	
	try {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.TimeStep)) {
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
			if (token.equalsIgnoreCase(VCML.DefaultTimeStep)) {
				token = tokens.nextToken();
				fieldDefaultTimeStep = Double.parseDouble(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.MinimumTimeStep)) {
				token = tokens.nextToken();
				fieldMinimumTimeStep = Double.parseDouble(token);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.MaximumTimeStep)) {
				token = tokens.nextToken();
				fieldMaximumTimeStep = Double.parseDouble(token);
				continue;
			}
			throw new DataAccessException("unexpected identifier " + token);
		}
	} catch (Throwable e) {
		throw new DataAccessException(
			"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
	}
}
/**
 * create new TimeStep with specified default; adjust min / max time if necessary
 * @param defaultTimeStep default time step
 * @return new TimeStep
 */
public TimeStep withDefault(double defaultTimeStep) {
	double minTs = Math.min(defaultTimeStep,fieldMinimumTimeStep);
	double maxTs = Math.max(defaultTimeStep,fieldMaximumTimeStep);
	return new TimeStep(minTs, defaultTimeStep, maxTs); 
}
}
