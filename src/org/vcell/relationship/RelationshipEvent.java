package org.vcell.relationship;

import java.util.EventObject;

public class RelationshipEvent extends EventObject{
	private int operationType = CHANGED;

	public static final int CHANGED = 0;
	
	private static final String operationNames[] = { "changed" };

	public RelationshipEvent(RelationshipModel relationshipModel, int operationType) {
		super(relationshipModel);
		this.operationType = operationType;
	}
	
	public int getOperationType(){
		return operationType;
	}

	public String getOperationName() {
		return operationNames[operationType];
	}

	public String toString() {
		return "RelationshipEvent:" + getOperationName();
	}
}
