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

import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class GroupObject extends EntityImpl {
	public static enum Type { GROUPEDCOMPLEX, GROUPEDINTERACTION, GROUPEDBIOPAXOBJECTS;
	}
	private HashSet<BioPaxObject> groupedObjects = new HashSet<BioPaxObject>();
	private Type type;
	

	public HashSet<BioPaxObject> getGroupedObjects(){
		return groupedObjects;
	}
	public void setGroupedeObjects(HashSet<BioPaxObject> groupedObjects){
		this.groupedObjects = groupedObjects;
	}
	
	public HashSet<BioPaxObject> computeGroupedBioPaxObjects(){
		HashSet<BioPaxObject> gObjects = new HashSet<BioPaxObject>();
		for(BioPaxObject bpo : groupedObjects){
			if(bpo instanceof GroupObject){
				gObjects.addAll(((GroupObject)bpo).computeGroupedBioPaxObjects());
			}else{
				gObjects.add(bpo);
			}
		}
		return gObjects;
	}
	
	public Type getType(){
		return type;
	}
	public void setType(Type newType){
		type = newType;
	}
	public void setType(String typeStr){
		type = Type.valueOf(typeStr);
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);

		HashSet<BioPaxObject> gObjects = new HashSet<BioPaxObject>();
		for(BioPaxObject bpo : groupedObjects){
			if(bpo instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)bpo;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						gObjects.add(concreteObject);
					}
				}
			}else{
				gObjects.add(bpo);
			}
		}
		setGroupedeObjects(gObjects);
	}
}
