package org.vcell.rest.common;

import cbit.vcell.modeldb.BioModelReferenceRep;
import cbit.vcell.parser.ExpressionException;

public class BiomodelReferenceRepresentation {
	
	public String bmKey;	
	public String name;	
	public String ownerName;	
	public String ownerKey;
	public String versionFlag;
	
	public BiomodelReferenceRepresentation(){
		
	}	
	
	public String getBmKey() {
		return bmKey;
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
	public String getVersionFlag() {
		return versionFlag;
	}


	public BiomodelReferenceRepresentation(BioModelReferenceRep bioModelReferenceRep) throws ExpressionException{
		this.bmKey = bioModelReferenceRep.getBmKey().toString();
		this.name = bioModelReferenceRep.getName();
		this.ownerName = bioModelReferenceRep.getOwner().getName();
		this.ownerKey = bioModelReferenceRep.getOwner().getID().toString();
		this.versionFlag = bioModelReferenceRep.getVersionFlag().toString();
	}
}
