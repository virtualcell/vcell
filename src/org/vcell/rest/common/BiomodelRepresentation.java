package org.vcell.rest.common;

import org.vcell.util.document.BioModelInfo;

public class BiomodelRepresentation {
	
	public String modelKey;
	
	public String modelName;
	
	public String userName;

	public String userKey;

	public BiomodelRepresentation(){
		
	}
	
	public String getModelkey() {
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

	public BiomodelRepresentation(BioModelInfo bioModelInfo){
		this.modelKey = bioModelInfo.getVersion().getVersionKey().toString();
		this.modelName = bioModelInfo.getVersion().getName();
		this.userName = bioModelInfo.getVersion().getOwner().getName();
		this.userKey = bioModelInfo.getVersion().getOwner().getID().toString();
	}
}
