package org.vcell.rest.common;

import org.vcell.util.document.BioModelInfo;

public class BiomodelRepresentation {
	
	public String branchID;
	
	public String modelKey;
	
	public String modelName;
	
	public String userName;

	public String userKey;
	
	public long savedDate;

	public BiomodelRepresentation(){
		
	}
	
	public String getBranchID() {
		return branchID;
	}

	public String getModelKey() {
		return modelKey;
	}

	public String getModelName() {
		return modelName;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserKey() {
		return userKey;
	}
	
	public long getSavedDate() {
		return savedDate;
	}

	public BiomodelRepresentation(BioModelInfo bioModelInfo){
		this.branchID = bioModelInfo.getVersion().getBranchID().toString();
		this.modelKey = bioModelInfo.getVersion().getVersionKey().toString();
		this.modelName = bioModelInfo.getVersion().getName();
		this.userName = bioModelInfo.getVersion().getOwner().getName();
		this.userKey = bioModelInfo.getVersion().getOwner().getID().toString();
		this.savedDate = bioModelInfo.getVersion().getDate().getTime();
	}
}
