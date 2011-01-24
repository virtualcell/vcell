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

	public String toString() {
		return "PathwayEvent:" + getOperationName();
	}

}
