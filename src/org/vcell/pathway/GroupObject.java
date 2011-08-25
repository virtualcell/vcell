package org.vcell.pathway;

import java.util.HashSet;

public class GroupObject extends EntityImpl {
	private HashSet<BioPaxObject> groupedObjects;

	public HashSet<BioPaxObject> getGroupedObjects(){
		return groupedObjects;
	}
	public void setGroupedeObjects(HashSet<BioPaxObject> groupedObjects){
		this.groupedObjects = groupedObjects;
	}
}
