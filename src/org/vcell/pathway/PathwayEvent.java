/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.EventObject;

@SuppressWarnings("serial")
public class PathwayEvent extends EventObject {
	private int operationType = CHANGED;

	public static final int CHANGED = 0;
	
	private static final String operationNames[] = { "changed" };

	public PathwayEvent(PathwayModel pathwayModel, int operationType) {
		super(pathwayModel);
		this.operationType = operationType;
	}
	
	public int getOperationType(){
		return operationType;
	}

	public String getOperationName() {
		return operationNames[operationType];
	}

	@Override
	public String toString() {
		return "PathwayEvent:" + getOperationName();
	}

}
