/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
