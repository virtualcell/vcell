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
 * Creation date: (9/6/2005 3:00:40 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public abstract class OutputTimeSpec implements org.vcell.util.Matchable, java.io.Serializable {

/**
 * OutputTimeSpec constructor comment.
 */
public OutputTimeSpec() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 3:42:17 PM)
 * @return java.lang.String
 */
public abstract String getDescription();
public String getShortDescription() {
	return getDescription();
}

/**
 * describe simulation
 * @param unitInfo not null
 * @return appropriately formatted String
 */
public abstract String describe(SimulationOwner.UnitInfo unitInfo);


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 8:46:47 AM)
 * @return java.lang.String
 */
public abstract String getVCML();


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:07 PM)
 * @return boolean
 */
public abstract boolean isDefault();


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:07 PM)
 * @return boolean
 */
public abstract boolean isExplicit();


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 1:53:07 PM)
 * @return boolean
 */
public abstract boolean isUniform();


/**
 * Insert the method's description here.
 * Creation date: (9/7/2005 9:10:09 AM)
 * @return cbit.vcell.solver.OutputTimeSpec
 */
public static OutputTimeSpec readVCML(CommentStringTokenizer tokens) throws DataAccessException {
	//
	// read format as follows:
	//
	//   OutputOptions {
	//		KeepEvery 1
	//		KeepAtMost	1000
	//   }
	//
	//	OR
	//   OutputOptions {
	//		OutputTimes 0.1,0.3,0.4,... (no spaces or line feeds between numbers)
	//   }
	//
	//	OR	
	//   OutputOptions {
	//		OutputTimeStep 10
	//   }
	//
	OutputTimeSpec ots = null;
	try {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.OutputOptions)) {
			token = tokens.nextToken();
		}
		if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
			throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.KeepEvery)) {
				token = tokens.nextToken();
				int keepEvery = Integer.parseInt(token);
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.KeepAtMost)) {
					throw new DataAccessException("unexpected identifier " + token);
				}
				token = tokens.nextToken();
				int keepAtMost = Integer.parseInt(token);
				ots = new DefaultOutputTimeSpec(keepEvery, keepAtMost);
				continue;
			} else if (token.equalsIgnoreCase(VCML.OutputTimeStep)) {
				token = tokens.nextToken();
				double outputTimeStep = Double.parseDouble(token);
				ots = new UniformOutputTimeSpec(outputTimeStep);
				continue;
			} else if (token.equalsIgnoreCase(VCML.OutputTimes)) {
				token = tokens.nextToken();
				java.util.StringTokenizer st = new java.util.StringTokenizer(token, ",");
				double[] times = new double[st.countTokens()];				
				int count = 0;
				while (st.hasMoreTokens()) {
					token = st.nextToken();
					times[count ++] = Double.parseDouble(token);
				}
				ots = new ExplicitOutputTimeSpec(times);
				continue;
			}
			throw new DataAccessException("unexpected identifier " + token);
		}
	} catch (Throwable e) {
		throw new DataAccessException(
			"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
	}

	return ots;
}
}
