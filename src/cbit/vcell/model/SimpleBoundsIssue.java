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
import org.vcell.util.Issue;

import net.sourceforge.interval.ia_math.RealInterval;
import cbit.vcell.model.Parameter;
/**
 * Insert the type's description here.
 * Creation date: (11/1/2005 9:32:59 AM)
 * @author: Jim Schaff
 */
public class SimpleBoundsIssue extends Issue {
	private RealInterval bounds = null;

/**
 * SimpleBoundsIssue constructor comment.
 * @param argSource java.lang.Object
 * @param argCategory java.lang.String
 * @param argMessage java.lang.String
 * @param argSeverity int
 */
public SimpleBoundsIssue(Parameter argParameter, RealInterval argBounds, String argMessage) {
	super(argParameter, IssueCategory.ParameterBoundsDefinition, argMessage, Issue.SEVERITY_BUILTIN_CONSTRAINT);
	if (argBounds==null){
		throw new IllegalArgumentException("parameter bounds cannot be null");
	}
	this.bounds = argBounds;
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:37:13 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 */
public RealInterval getBounds() {
	return bounds;
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:36:23 AM)
 * @return cbit.vcell.model.Parameter
 */
public cbit.vcell.model.Parameter getParameter() {
	return (Parameter)getSource();
}
}
