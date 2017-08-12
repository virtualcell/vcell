package org.vcell.api.common;


public class ApplicationRepresentation {
	
	private final String key;
	private final String branchId;
	private final String name;
	private final String ownerName;
	private final String ownerKey;
	private final String mathKey;
	
	private ApplicationRepresentation(){
		key = null;
		branchId = null;
		name = null;
		ownerName = null;
		ownerKey = null;
		mathKey = null;
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
