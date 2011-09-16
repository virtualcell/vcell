package org.vcell.pathway;

import java.util.HashSet;

public class GroupObject extends EntityImpl {
	public static enum Type { GROUPEDCOMPLEX, GROUPEDINTERACTION, GROUPEDBIOPAXOBJECTS;
	}
	private HashSet<BioPaxObject> groupedObjects;
	private Type type;
	

	public HashSet<BioPaxObject> getGroupedObjects(){
		return groupedObjects;
	}
	public void setGroupedeObjects(HashSet<BioPaxObject> groupedObjects){
		this.groupedObjects = groupedObjects;
	}
	
	public Type getType(){
		return type;
	}
	public void setType(Type newType){
		type = newType;
	}
}
