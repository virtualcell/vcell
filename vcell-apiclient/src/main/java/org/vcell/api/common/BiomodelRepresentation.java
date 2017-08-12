package org.vcell.api.common;


public class BiomodelRepresentation {
	
	public String bmKey;
	
	public String name;
	
	public int privacy;
	
	public String[] groupUsers;
	
	public Long savedDate;
	
	public String annot;
	
	public String branchID;
	
	public String modelKey;
	
	public String ownerName;
	
	public String ownerKey;
	
	public SimulationRepresentation[] simulations;
	
	public ApplicationRepresentation[] applications;

	public BiomodelRepresentation(){
		
	}
	
	
	
	public String getBmKey() {
		return bmKey;
	}



	public String getName() {
		return name;
	}



	public int getPrivacy() {
		return privacy;
	}



	public String[] getGroupUsers() {
		return groupUsers;
	}



	public Long getSavedDate() {
		return savedDate;
	}



	public String getAnnot() {
		return annot;
	}



	public String getBranchID() {
		return branchID;
	}



	public String getModelKey() {
		return modelKey;
	}



	public String getOwnerName() {
		return ownerName;
	}



	public String getOwnerKey() {
		return ownerKey;
	}



	public SimulationRepresentation[] getSimulations() {
		return simulations;
	}



	public ApplicationRepresentation[] getApplications() {
		return applications;
	}

}
