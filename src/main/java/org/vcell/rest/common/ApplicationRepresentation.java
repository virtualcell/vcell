package org.vcell.rest.common;

import cbit.vcell.modeldb.SimContextRep;

public class ApplicationRepresentation {
	
	private final String key;
	private final String branchId;
	private final String name;
	private final String ownerName;
	private final String ownerKey;
	private final String mathKey;

	public ApplicationRepresentation(SimContextRep simContextRep) {
		key = simContextRep.getScKey().toString();
		branchId = simContextRep.getBranchID().toString();
		name = simContextRep.getName();
		ownerKey = simContextRep.getOwner().getID().toString();
		ownerName = simContextRep.getOwner().getName();
		mathKey = simContextRep.getMathKey().toString();
	}

	public String getKey() {
		return key;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerKey() {
		return ownerKey;
	}

	public String getMathKey() {
		return mathKey;
	}
	

}
