package org.vcell.pathway.group;

import org.vcell.pathway.BioPaxObject;

public class GroupNeighborObject {
	private BioPaxObject bioPaxObject;
	private String type;
	
	public BioPaxObject getBioPaxObject(){
		return bioPaxObject;
	}
	public void setBioPaxObject(BioPaxObject bpObject){
		bioPaxObject = bpObject;
	}
	
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type = type;
	}
}
