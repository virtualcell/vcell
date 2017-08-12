package org.vcell.rest.common;

import cbit.vcell.modeldb.MathModelReferenceRep;
import cbit.vcell.parser.ExpressionException;

public class MathmodelReferenceRepresentation {
	
	public String mmKey;	
	public String name;	
	public String ownerName;	
	public String ownerKey;
	
	public MathmodelReferenceRepresentation(){
		
	}	
	
	public String getMmKey() {
		return mmKey;
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


	public MathmodelReferenceRepresentation(MathModelReferenceRep mathModelReferenceRep) throws ExpressionException{
		this.mmKey = mathModelReferenceRep.getMmKey().toString();
		this.name = mathModelReferenceRep.getName();
		this.ownerName = mathModelReferenceRep.getOwner().getName();
		this.ownerKey = mathModelReferenceRep.getOwner().getID().toString();
	}
}
